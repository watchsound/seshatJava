package rnnlibjava;

import java.io.PrintStream;

import lang.ArrayListFloat;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.DataExporter.DataExportHandler;

public class SteepestDescent extends DataExporter implements Optimiser {
	  // data
	  PrintStream out;
	  ResizableListI<Float> deltas;
	  float learnRate;
	  float momentum;
	  WeightContainer  wc;

	  // data
	  SimpleListI<Float> wts;
	  SimpleListI<Float> derivs;
	  
	  SteepestDescent(
			     String name, PrintStream o, SimpleListI<Float> weights,
			     SimpleListI<Float> derivatives, WeightContainer  weight, DataExportHandler  deh ) {
		  this(name, o, weights, derivatives, weight, deh, 1e-4f, 0.9f);
	  }
	  SteepestDescent(
			     String name, PrintStream o, SimpleListI<Float> weights,
			     SimpleListI<Float> derivatives, WeightContainer  weight, DataExportHandler  deh,
			     float lr, float mom ) {
			     super(name, deh);
			     this.deltas = new ArrayListFloat();
			    		 
			    		 
			     this.wts = weights;
			     this.derivs = derivatives;
			     
			     this.out = o;
			     this.learnRate =  lr;
			     this.momentum = mom; 
			     this.wc = weight;
			     build();
	  }
	 

	 public void update_weights() {
	  //  assert(wts.size() == derivs.size());
	  //  assert(wts.size() == deltas.size());
	    for(int i = 0; i < wts.size(); i++ ) {
	      float delta = (momentum * deltas.at(i)) - (learnRate * derivs.at(i));
	      deltas.set(i,  delta);
	      wts.set(i, wts.at(i) + delta);
	    }
	    if (Helpers.verbose) {
	      out.println(this.name + " weight updates:" );
	      Helpers.PRINT(Helpers.minmax(wts), out);
	      Helpers.PRINT(Helpers.minmax(derivs), out);
	      Helpers.PRINT(Helpers.minmax(deltas), out);
	    }
	  }

	  // NOTE must be called after any change to weightContainer
	 public  void build() {
	    if (deltas.size() != wts.size()) {
	    	Helpers.resize_self(deltas, wts.size(), 0f);
	      
	      Helpers.fill(deltas, 0);
	      wc.save_by_conns(deltas, name + "_deltas");
	    }
	  }

	 public  void print(PrintStream out )   {
	    out.println( "steepest descent" );
	    Helpers.PRINT(learnRate, out);
	    Helpers.PRINT(momentum, out);
	  }
}
