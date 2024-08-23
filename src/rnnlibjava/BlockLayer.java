package rnnlibjava;

import java.io.PrintStream;

import lang.SimpleListI;
import rnnlibjava.Container.View;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.DataExporter.DataExportHandler;

public class BlockLayer  extends Layer{
	// data
	  SimpleListI<Integer> blockShape;
	  int[] blockOffset;
	  int[] inCoords;
	  int sourceSize;
	  CoordIterator blockIterator;
	  int[] outSeqShape;

	
	  public BlockLayer(Layer src, SimpleListI<Integer> blockshape,  WeightContainer  weight, DataExportHandler  deh ) {  
		     super(src.name + "_block", src.num_seq_dims(), 0,
		    		 Helpers.productInt(blockshape) * src.output_size(), weight, deh, src) ;
		     blockShape = blockshape;
		      blockOffset = new int[num_seq_dims()];
		      inCoords= new int[num_seq_dims()];
		      sourceSize =  src.outputActivations.depth;
		      blockIterator = new CoordIterator(blockShape);
		      outSeqShape = new int[num_seq_dims()];
		    
		    wc.link_layers(
		        this.source.name, this.name,
		        this.source.name + "_to_" + this.name);
		    display(this.outputActivations, "activations");
		    display(this.outputErrors, "errors");
	  } 

	  
	  public void print(PrintStream out )   {
		   super.print(out);
		    out.print(  " block "+  blockShape );
	  }
	 public  void start_sequence() {
		    for (int i = 0; i < outSeqShape.length; ++i) {
		      outSeqShape[i] = (int) Math.ceil(
		          (float)this.source.output_seq_shape().at(i) /
		          (float)blockShape.at(i));
		    }
		    outputActivations.reshape(outSeqShape, 0f);
		    outputErrors.reshape(outputActivations, 0f);
	 }
	public void feed_forward(int[] outCoords) {
	    View<Float>.Iter  outIt = this.outputActivations.atCoord( outCoords ).begin();
	   Helpers. range_multiply_val(blockOffset, outCoords, blockShape);
	    for (blockIterator.begin(); !blockIterator.end;  blockIterator.op_selfadd()) {
	    	 Helpers.range_plus(inCoords,  blockIterator.op_self(), blockOffset);
	      ViewFloat inActs = this.source.outputActivations.at2(inCoords);
	      if (inActs.begin().size() > 0) { //FIXME ME
	        Helpers.copy(inActs.begin(),  outIt);
	      } else { 
	        Helpers.fill(outIt.newView(sourceSize), 0);
	      }
	      outIt.moved(sourceSize ) ; 
	    }
	  }
	public void feed_back(int[] outCoords) {
		   View<Float>.Iter  outIt = this.outputErrors.atCoord( outCoords ).begin();
		   Helpers. range_multiply_val(blockOffset, outCoords, blockShape);
		   for (blockIterator.begin(); !blockIterator.end;  blockIterator.op_selfadd()) {   
			  Helpers.range_plus(inCoords,  blockIterator.op_self(), blockOffset);
			  View<Float>.Iter inErr = this.source.outputErrors.at2(inCoords).begin(); 
		      if (inErr.size() >0) { 
		    	  for(int i = 0; i < sourceSize; i++) {
		    		  inErr.addChange(i, outIt.at(i));
		    	  }
		        //  Helpers.transform(outIt, outIt + sourceSize, inErr, inErr, plus<float>());
		      }
		      outIt.moved(sourceSize ) ;
		    }
		  }
}
