package rnnlibjava;

import java.util.Vector;

import rnnlibjava.Container.ViewFloat;
import rnnlibjava.DataExporter.DataExportHandler;
import rnnlibjava.Layer.FlatLayer;

public class NeuronLayer extends FlatLayer{
	//functions
	  public ActivationFunctions.FN F;
	
	  public NeuronLayer(String name, int numDims, int size,  WeightContainer  weight, DataExportHandler  deh,
			  ActivationFunctions.FN F) {  
		     super(name, numDims,   size,   weight, deh, null) ;
		     this.F = F;
		    init();     
	  } 
	  public NeuronLayer(String name, int[] directions, int size,  WeightContainer  weight, DataExportHandler  deh,
			  ActivationFunctions.FN F) {  
		     super(name, directions,   size,   weight, deh, null) ;
		     this.F = F;
		    init();     
	  } 
	  public void init()
		{
			display( inputActivations, "inputActivations");
			display( outputActivations, "outputActivations");
			display( inputErrors, "inputErrors");
			display( outputErrors, "outputErrors");	
		}
	  
	public  void feed_forward(int[] coords)
		{
			Helpers.transform( inputActivations.atCoord( coords ),  outputActivations.atCoord( coords ),
					new MathOpI.MathOp1<Float>() {
				public Float op(Float v) {
					return F.fn(v);
				}
			} );
		}
	public	void feed_back(int[] coords)
		{
		    ViewFloat ie = inputErrors.atCoord( coords );
		    ViewFloat oa = outputActivations.atCoord( coords );
		    ViewFloat oe = outputErrors.atCoord( coords );
		    for(int t = 0; t < ie.size() && t < oa.size() && t < oe.size(); t++) {
		    	ie.set(t,  F.deriv( oa.at(t)  )  * oe.at(t));
		    } 
		}
}
