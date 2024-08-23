package rnnlibjava;

import rnnlibjava.DataExporter.DataExportHandler;

public class IdentityLayer extends Layer.FlatLayer{

	  public IdentityLayer(String name, int numSeqDims, int size, WeightContainer  weight, DataExportHandler  deh ) {  
	     super(name, numSeqDims, size,   weight, deh, null) ;
	         display( outputErrors, "errors");
			 display( outputActivations, "activations");
	  }
	  
	  public IdentityLayer(String name, int[]  directions, int size, WeightContainer  weight, DataExportHandler  deh ) {  
		     super(name, directions, size,   weight, deh, null) ;
		         display( outputErrors, "errors");
				 display( outputActivations, "activations");
		  }
	 
 
		public void feed_forward(int[] coords)
		{
			 
			Helpers.copy( inputActivations.atCoord(coords), outputActivations.atCoord(coords));
		}
		
		public void feed_back(int[] coords)
		{
			 
			Helpers.copy( outputErrors.atCoord(coords), inputErrors.atCoord(coords));
		}
 
}
