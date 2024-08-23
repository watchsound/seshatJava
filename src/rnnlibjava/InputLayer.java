package rnnlibjava;

import java.util.Vector;

import lang.ArrayListObj;
import rnnlibjava.DataExporter.DataExportHandler;

public class InputLayer extends Layer{
	//functions
	
	
	  public InputLayer(String name, int numSeqDims, int size, ArrayListObj<String> inputLabels, WeightContainer  weight, DataExportHandler  deh ) {  
		     super(name, numSeqDims, 0, size,   weight, deh, null) ;
		      //   display( outputActivations, "activations", inputLabels);
				// display( outputErrors, "errors", inputLabels);
	  } 
		public void copy_inputs( SeqBuffer_float inputs)
		{
			 
			 outputActivations.assign(inputs);
	 		 outputErrors.reshape( outputActivations, 0f);
		}
}
