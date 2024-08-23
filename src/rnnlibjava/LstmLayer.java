package rnnlibjava;

import java.io.PrintStream;

import lang.SimpleListI;
import rnnlibjava.Container.View;
import rnnlibjava.Container.View.Iter;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Pair.PairInt;

public class LstmLayer extends Layer{
	int numBlocks;
	  int cellsPerBlock;
	  int numCells;
	  int gatesPerBlock;
	  int unitsPerBlock;
	  int peepsPerBlock;
	  SeqBuffer_float inGateActs;
	  SeqBuffer_float forgetGateActs;
	  SeqBuffer_float outGateActs;
	  SeqBuffer_float preOutGateActs;
	  SeqBuffer_float states;
	  SeqBuffer_float preGateStates;
	  SeqBuffer_float cellErrors;
	 int[][] stateDelays;
	 int[] delayedCoords;
	 ViewFloat[] oldStates;
	 ViewFloat[] nextErrors;
	 ViewFloat[] nextFgActs;
	 ViewFloat[] nextCellErrors;
	 ActivationFunctions.FN CI;
	 ActivationFunctions.FN CO;
	 ActivationFunctions.FN G;
	 
	  LstmLayer  peepSource;
	  PairInt peepRange;
	 
	  //functions
	  public LstmLayer(
			    String name, int[] directions, int nb, WeightContainer  wc, DataExportHandler  deh ,
			    ActivationFunctions.FN CI,
				 ActivationFunctions.FN CO,
				 ActivationFunctions.FN G 
		      ) {
		  this(name, directions, nb, wc, deh, 1, null, CI, CO, G );
	  }
	  
	  public LstmLayer(
		    String name, int[] directions, int nb, WeightContainer  wc, DataExportHandler  deh,
		    int cpb  , LstmLayer ps,
		    ActivationFunctions.FN CI,
			 ActivationFunctions.FN CO,
			 ActivationFunctions.FN G  
	       ) {
	    super(name, directions, (cpb + directions.length + 2) * nb, nb, wc, deh, null);
	      this.CI = CI;
	      this.CO = CO;
	      this.G = G;
	      numBlocks = nb;
	      cellsPerBlock = cpb;
	      numCells = numBlocks * cellsPerBlock;
	      gatesPerBlock =  num_seq_dims() + 2 ;
	      unitsPerBlock = gatesPerBlock + cellsPerBlock;
	      peepsPerBlock = gatesPerBlock * cellsPerBlock;
	      inGateActs = new SeqBuffer_float(numBlocks);
	      forgetGateActs   = new SeqBuffer_float(numBlocks *  num_seq_dims());
	      outGateActs = new SeqBuffer_float(numBlocks);
	      preOutGateActs = new SeqBuffer_float(numCells);
	      states = new SeqBuffer_float(numCells);
	      preGateStates = new SeqBuffer_float(numCells);
	      cellErrors = new SeqBuffer_float(numCells);
	      stateDelays = new int[ num_seq_dims()][];
	      delayedCoords = new int[ num_seq_dims()];
	      oldStates  = new ViewFloat[ num_seq_dims()];
	      nextErrors  = new ViewFloat[ num_seq_dims()];
	      nextFgActs  = new ViewFloat[ num_seq_dims()];
	      nextCellErrors  = new ViewFloat[ num_seq_dims()];
	 
	       peepSource = ps;
	       peepRange =  peepSource != null? peepSource.peepRange
	                : wc.new_parameters(
	                      peepsPerBlock*numBlocks, name, name, name + "_peepholes")   ;
	    if (peepSource != null) {
	       wc.link_layers(
	          name, name, name + "_peepholes", peepRange.first, peepRange.second);
	    }

	    //initialise the state delays
	    for(int i = 0; i < num_seq_dims(); i++) {
	    	 stateDelays[i] =	Helpers.resize(stateDelays[i], num_seq_dims(), 0);
	         stateDelays[i][i] = -directions[i];
	    }
	    //export the data
	    //display(this.inputActivations, "inputActivations");
	    //display(this.outputActivations, "outputActivations");
	    //display(this.inputErrors, "inputErrors");
	    //display(this.outputErrors, "outputErrors");
	    //DISPLAY(cellErrors);
	    //DISPLAY(states);
	    //DISPLAY(inGateActs);
	    //DISPLAY(forgetGateActs);
	    //DISPLAY(outGateActs);
	  }
//	  ~LstmLayer() { }
	 public void start_sequence() {
	    super.start_sequence();
	    inGateActs.reshape(this.output_seq_shape());
	    forgetGateActs.reshape(this.output_seq_shape());
	    outGateActs.reshape(this.output_seq_shape());
	    preOutGateActs.reshape(this.output_seq_shape());
	    states.reshape(this.output_seq_shape());
	    preGateStates.reshape(this.output_seq_shape());
	    cellErrors.reshape(states);
	  }
	  public void feed_forward( int[]  coords) {
		View<Float>.Iter   actBegin = this.outputActivations.atCoord(coords).begin() ;
		View<Float>.Iter  inActIt =    this.inputActivations.atCoord(coords).begin() ;
		View<Float>.Iter  inGateActBegin =   inGateActs.atCoord(coords).begin() ;
		View<Float>.Iter  fgActBegin =  forgetGateActs.atCoord(coords).begin() ;
		View<Float>.Iter  outGateActBegin =   outGateActs.atCoord(coords).begin() ;
		View<Float>.Iter  stateBegin =  states.atCoord(coords).begin() ;
		View<Float>.Iter  preGateStateBegin = preGateStates.atCoord(coords).begin() ;
		View<Float>.Iter  preOutGateActBegin =   preOutGateActs.atCoord(coords).begin() ;
	      for(int d = 0; d < this.num_seq_dims(); d++) { 
	          oldStates[d] = states.at2(Helpers.range_plus(
	          delayedCoords, coords, stateDelays[d]));
	    }
	   
	    View<Float>.Iter peepWtIt =  wc.get_weights(
	        peepRange).begin();
 
	    int cellStart = 0;
	    int cellEnd = cellsPerBlock;
	    View<Float>.Iter fgActEnd =   fgActBegin .newIter( this.num_seq_dims() )   ;
	    for(int b = 0; b < numBlocks; b++) {   
	    	View<Float> fgActs = fgActBegin .newView( fgActEnd );
	      //input gate
	      //extra inputs from peepholes (from old states)
	      for(ViewFloat os : oldStates) { 
	        if (os.valid() ) { 
	           Matrix.dot( os.newView(cellStart, cellEnd), peepWtIt, inActIt.newView(1));
	        } 
	      }
          peepWtIt.moved(  cellsPerBlock );
	 
	      float inGateAct = G.fn( inActIt.value());
	      inGateActBegin.set(b,  inGateAct);
	      inActIt.moved(1);
	      if( inActIt.invalid(0) ) { //FIXME
	    	  break;
	      }
	      //forget gates
	      //extra inputs from peepholes (from old states)
	      for(int d = 0; d < this.num_seq_dims(); d++) {
	        ViewFloat os = oldStates[d];
	        if (os.valid()) { 
		           Matrix.dot( os.newView(cellStart, cellEnd), peepWtIt, inActIt.newView(1));
		    } 
	       
	        peepWtIt.moved(  cellsPerBlock );
	 
	        float vv = G.fn( inActIt.value( ));
	        fgActs.set(d,  vv);
		    inActIt.moved(1); 
	      }

	      if( inActIt.invalid(cellsPerBlock) ) { //FIXME
	    	  break;
	      }
	      
	      //pre-gate cell states 
	      Helpers. transform(
	          inActIt.newView(cellsPerBlock), preGateStateBegin.newIter( cellStart ),
	          new MathOpI.MathOp1<Float>() {
	        	  public Float op(Float one) {
	        		  return CI.fn(one);
	        	  }
	          }); 
	      inActIt.moved( cellsPerBlock );
	    
	      //cell states
	      for(int c = cellStart; c <  cellEnd; c++) {
	       
	        float state = inGateAct * preGateStateBegin.at(c);
	        for(int d = 0; d < num_seq_dims(); d++) { 
	          ViewFloat os = oldStates[d];
	          if (os.valid() ) {
	            state += fgActs.at(d)  * os.at(c);
	          }
	        }
	        stateBegin.set(c, state);  
	        preOutGateActBegin.set(c , CO.fn(state));  
	      }

	      if( inActIt.invalid(0) ) { //FIXME
	    	  break;
	      }
	      //output gate
	      //extra input from peephole (from current state) 
          Matrix.dot( stateBegin.newViewWithShift(cellStart, cellEnd), peepWtIt, inActIt.newView(1));
          peepWtIt.moved(  cellsPerBlock );
	    
          float outGateAct = G.fn( inActIt.value( )); 
	      outGateActBegin.set(b,  outGateAct);
	      inActIt.moved(1); 

	      //output activations
	      Helpers.transform(preOutGateActBegin.newViewWithShift(cellStart, cellEnd) ,
	    		  actBegin.newIter(cellStart),
	    		  new MathOpI.MathOp1<Float>() {
		        	  public Float op(Float one) {
		        		  return one * outGateAct;
		        	  }
	               }) ;
	 
	      cellStart = cellEnd;
	      cellEnd += cellsPerBlock;
	      fgActBegin = fgActEnd.copy();
	      fgActEnd.moved( this.num_seq_dims() );
	    }
	  }
	 public void feed_back( int[] coords) {
	    //activations
		 View<Float>.Iter   inGateActBegin = this.inGateActs.atCoord(coords).begin() ;
		 View<Float>.Iter   forgetGateActBegin = this.forgetGateActs.atCoord(coords).begin() ;
		 View<Float>.Iter   outGateActBegin = this.outGateActs.atCoord(coords).begin() ;
		 View<Float>.Iter   preGateStateBegin = this.preGateStates.atCoord(coords).begin() ;
		 View<Float>.Iter   preOutGateActBegin = this.preOutGateActs.atCoord(coords).begin() ;
		 
		 
	    //errors
	    ViewFloat inErrs = this.inputErrors.atCoord(coords);
	    View<Float>.Iter cellErrorBegin = cellErrors.atCoord(coords).begin();
	    View<Float>.Iter outputErrorBegin = this.outputErrors.atCoord(coords).begin();
	    View<Float>.Iter errorIt = inErrs.begin();
	 
	    View<Float>.Iter peepWtIt =
	        wc.get_weights(peepRange).begin();
 
	    for(int d = 0; d < num_seq_dims(); d++) { 
	      oldStates[d] = states.at2( Helpers.range_plus(
	          delayedCoords, coords, stateDelays[d]));
	      Helpers.range_minus(delayedCoords, coords, stateDelays[d]);
	      nextErrors[d] = this.inputErrors.at2(delayedCoords);
	      nextFgActs[d] = forgetGateActs.at2(delayedCoords);
	      nextCellErrors[d] = cellErrors.at2(delayedCoords);
	    }
	    int cellStart = 0;
	    int cellEnd = cellsPerBlock;
	    int fgStart = 0;
	    int gateStart = 0;
	    for(int b = 0; b < numBlocks; b++) { 
	      float inGateAct = inGateActBegin.at(b);
	      float outGateAct = outGateActBegin.at(b);

	      //output gate error
	      float outGateError = G.deriv(outGateAct) *
	         Helpers. inner_product(
	            (SimpleListI<Float> )  preOutGateActBegin.newViewWithShift(cellStart, cellEnd) , (SimpleListI<Float> ) outputErrorBegin.newIter(cellStart) , 0.0f);

	      //cell pds (dE/dState)
	      for(int c = cellStart; c < cellEnd; c++) {
	    
	        float deriv =
	            CO.deriv(preOutGateActBegin.at(c)) * outGateAct * outputErrorBegin.at(c);
	 
	        int cOffset = c - cellStart;
	        float igPeepWt = peepWtIt.at(cOffset);
	        float ogPeepWt = peepWtIt.at(peepsPerBlock - cellsPerBlock + cOffset);
	        deriv += outGateError * ogPeepWt;
	 
	        for(int d = 0; d < this.num_seq_dims(); d++) {
	           float  fgPeepWt = peepWtIt.at( cOffset + (cellsPerBlock * (d + 1)));
 
	          ViewFloat nextErrs = nextErrors[d];
	          if (nextErrs.valid()) {
 
	            deriv += (nextErrs.at( gateStart + 1 + d ) * fgPeepWt) +
	                (nextErrs.at( gateStart ) * igPeepWt);
	 
	            deriv += (nextFgActs[d].at( fgStart + d ) * nextCellErrors[d].at(c));
	          }
	        }
	        cellErrorBegin.set(c, deriv); 
	      }

	      //input gate error
	      errorIt.value( G.deriv(inGateAct) *
	          Helpers.inner_product(  (SimpleListI<Float> )  cellErrorBegin.newViewWithShift(cellStart, cellEnd) ,
	        		  (SimpleListI<Float>) preGateStateBegin.newIter(cellStart), 0.0f)) ;
	      
	      errorIt.moved(1);
	    

	      //forget gate error
	      for(int d = 0; d < this.num_seq_dims(); d++) {
	     
	        ViewFloat os = oldStates[d];
	        if (os.valid()) {
	        	  errorIt.value( G.deriv(  forgetGateActBegin.at(  fgStart + d )    ) *
	        	          Helpers.inner_product(  (SimpleListI<Float> )  cellErrorBegin.newViewWithShift(cellStart, cellEnd) ,
	        	        		  (SimpleListI<Float>) os.begin().moved(cellStart), 0.0f)) ;
	       
	        } else {
	           errorIt.value(0f);
	        }
	        errorIt.moved(1);
	      }

	      //cell errors
	      for(int c = cellStart; c <  cellEnd; c++) {
	    	  errorIt.value(   inGateAct * CI.deriv(preGateStateBegin.at(c)) * cellErrorBegin.at(c) ) ; 
	    	  errorIt.moved(1); 
	      }
	       errorIt.value( outGateError );
	       errorIt.moved(1); 
 
	      peepWtIt.moved( peepsPerBlock );
	 
	      cellStart += cellsPerBlock;
	      cellEnd += cellsPerBlock;
	      fgStart += this.num_seq_dims();
	      gateStart += unitsPerBlock;
	    }

	    //constrain errors to be in [-1,1] for stability
	     if (!Helpers.runningGradTest) {
	         Helpers. bound_range(inErrs, -1.0f, 1.0f);
	     }
	  }
	 
	  public void update_derivs(int[] coords)
	  {
			 View<Float>.Iter   stateBegin = this.states.atCoord(coords).begin() ;
			 View<Float>.Iter   errorBegin = this.inputErrors.atCoord(coords).begin() ;
			 View<Float>.Iter   pdIt =  wc.get_derivs(peepRange).begin();;
			 
			  for(int d = 0; d < this.num_seq_dims(); d++) {
				  oldStates[d] = states.at2( Helpers.range_plus(
				          delayedCoords, coords, stateDelays[d]));
			  }
	   
			  for(int b = 0; b < numBlocks; b++) {
	 
	      int cellStart = b * cellsPerBlock;
	      int cellEnd = cellStart + cellsPerBlock;
	      int errorOffset = b * unitsPerBlock;
	      float inGateError = errorBegin.at( errorOffset );
	      
	      for(int d = 0; d < this.num_seq_dims(); d++) {
	           ViewFloat os = oldStates[d];
	        if (os.valid())  {
	        	for(int c = cellStart; c <  cellEnd; c++) {
	                
	              pdIt.addChange( c - cellStart,   inGateError * os.at(c));
	          }
	          float forgGateError = errorBegin.at( errorOffset + d + 1 );
	          for(int c = cellStart; c < cellEnd; c++) {
	        	  pdIt.addChange((c - cellStart) + ((d + 1) * cellsPerBlock),   forgGateError * os.at(c)); 
	          }
	        }
	      }
	      float outGateError = errorBegin.at( errorOffset + unitsPerBlock - 1 );
	      for(int c = cellStart; c <  cellEnd; c++) {
	    	  pdIt.addChange((c - cellStart) + + peepsPerBlock - cellsPerBlock ,   outGateError * stateBegin.at(c));  
	      }
	      pdIt.moved( peepsPerBlock );
	    }
	  }
	  public void print(PrintStream out )   {
	    super.print(out);
	    out.print(  " " + Helpers. difference(peepRange) + " peeps");
	    if (peepSource !=  null) {
	     out.print( " (shared with " + peepSource.name + ")" );
	    }
	  }
	  public  ViewList<Float> weights() {
	    return wc.get_weights(peepRange);
	  }
//	#endif
}
