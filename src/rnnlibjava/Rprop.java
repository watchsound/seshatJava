package rnnlibjava;

import java.io.PrintStream;

import lang.ResizableListI;
import lang.SimpleListI;

public class Rprop extends DataExporter implements Optimiser {
	  // data
	  PrintStream out;
	  ResizableListI<Float> deltas;
	  ResizableListI<Float> prevDerivs;
	  float etaChange;
	  float etaMin;
	  float etaPlus;
	  float minDelta;
	  float maxDelta;
	  float initDelta;
	  float prevAvgDelta;
	  boolean online;
	  WeightContainer  wc;
	  
	  // data
	  SimpleListI<Float> wts;
	  SimpleListI<Float> derivs;

	  // functions
	  Rprop(
	     String name, PrintStream o, ResizableListI<Float> weights,
	     ResizableListI<Float> derivatives, WeightContainer  weight, DataExportHandler  deh, boolean on ) {
	     super(name, deh);
	     this.wts = weights;
	     this.derivs = derivatives;
	     this.out = o;
	     this.etaChange = 0.01f;
	     this.etaMin = 0.5f;
	     this.etaPlus = 1.2f;
	     this.minDelta = 1e-9f;
	     this.maxDelta = 0.2f;
	     this.initDelta = 0.01f;
	     this.prevAvgDelta = 0f;
	     this.online = on;
	     this.wc = weight;
	    
	    if (online) {
	      save(prevAvgDelta, "prevAvgDelta");
	      save(etaPlus, "etaPlus");
	    }
	    build();
	  }
	 

	  public void update_weights() {
	   // assert(wts.size() == derivs.size());
	   // assert(wts.size() == deltas.size());
	  // assert(wts.size() == prevDerivs.size());
	    for(int i = 0; i < wts.size() ; i++ ) {
	      float deriv = derivs.at(i);
	      float delta = deltas.at(i);
	      float derivTimesPrev =  deriv * prevDerivs.at(i);
	      if (derivTimesPrev > 0) {
	        deltas.set(i, Helpers.bound(delta * etaPlus, minDelta, maxDelta));
	        wts.set(i,  wts.at(i) -  Helpers.sign(deriv) * delta );
	        prevDerivs.set( i, deriv );
	      } else if (derivTimesPrev < 0) {
	        deltas.set(i,  Helpers. bound(delta * etaMin, minDelta, maxDelta));
	        prevDerivs.set(i, 0f);
	      } else {
	        wts.set(i, wts.at(i) - Helpers.sign(deriv) * delta);
	        prevDerivs.set(i,  deriv);
	      }
	    }
	    // use eta adaptations for online training (from Mike Schuster's thesis)
	    if (online) {
	      float avgDelta = Helpers.mean(deltas);
	      if (avgDelta > prevAvgDelta) {
	        etaPlus = Math.max((float)1.0, etaPlus - etaChange);
	      } else {
	        etaPlus += etaChange;
	      }
	      prevAvgDelta = avgDelta;
	    }
	    if (Helpers.verbose) {
	      Helpers.PRINT(Helpers.minmax(wts), out);
	      Helpers.PRINT(Helpers.minmax(derivs), out);
	      Helpers.PRINT(Helpers.minmax(deltas), out);
	      Helpers.PRINT(Helpers.minmax(prevDerivs), out);
	    }
	  }

	  // NOTE must be called after any change to weightContainer
	  public void build() {
	    if (deltas.size() != wts.size()) {
	      Helpers.resize_self( deltas , wts.size(), 0f);
	      Helpers.resize_self( prevDerivs , wts.size(), 0f);
	    
	      Helpers.fill(deltas, initDelta);
	      Helpers. fill(prevDerivs, 0);
	      wc.save_by_conns(
	          deltas, ((name.equals( "optimiser") ) ? "" : name + "_") + "deltas");
	      wc.save_by_conns(
	          prevDerivs, ((name.equals(  "optimiser") ) ? "" : name + "_") + "prevDerivs");
	    }
	  }

	  public void print(PrintStream out  )   {
	    out .println("RPROP");  
	    Helpers.PRINT(online, out);
	    if (online) {
	    	Helpers.PRINT(prevAvgDelta, out);
	    	Helpers. PRINT(etaChange, out);
	    }
	    Helpers. PRINT(etaMin, out);
	    Helpers. PRINT(etaPlus, out);
	    Helpers.PRINT(minDelta, out);
	    Helpers.PRINT(maxDelta, out);
	    Helpers. PRINT(initDelta, out);
	  }

}
