package rnnlibjava;

import java.util.Vector;

import rnnlibjava.Container.ViewFloat;
import rnnlibjava.DataExporter.DataExportHandler;

public class BiasLayer extends Layer{
	  // data
	  ViewFloat acts;
	  ViewFloat errors;
	
	
	  public BiasLayer(  WeightContainer  weight, DataExportHandler  deh ) {  
		     super("bias", 0, 0, 1, weight, deh, null) ;
		     acts = super.outputActivations.atCoord(0);
		     errors = super.outputErrors.atCoord(0); 
		     
		     acts.set(0, 1f);
	  } 
	 public ViewFloat out_acts(int[] coords) {
		 return acts;
	 }
	 public ViewFloat out_errs(int[] coords) {
		 return errors;
	 }
}
