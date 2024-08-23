package rnnlibjava;

import java.util.Vector;

import lang.ArrayListObj;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Container.ViewLog;
import rnnlibjava.Container.ViewV;
import rnnlibjava.DataExporter.DataExportHandler;
import rnnlibjava.Layer.FlatLayer;

public class SoftmaxLayer extends FlatLayer{
	//data
	ArrayListObj<String> targetLabels;
	SeqBuffer_log logActivations;
	SeqBuffer_log unnormedlogActivations;
	SeqBuffer_float unnormedActivations;
	
	
	  public SoftmaxLayer(String name, int numSeqDims,  ArrayListObj<String> labels, 
			  WeightContainer  wc, DataExportHandler  deh ) {  
		     super(name, numSeqDims, labels.size(), wc, deh, null) ;
		 	targetLabels = labels;
			logActivations = new SeqBuffer_log( output_size() ) ;
			unnormedlogActivations= new SeqBuffer_log( output_size() ) ; 
			unnormedActivations = new SeqBuffer_float( output_size() );
		     
		    display( outputActivations, "outputActivations" ); 
	  } 
	 public void start_sequence()
		{
			super.start_sequence();
			logActivations.reshape( inputActivations.seq_shape());
			unnormedlogActivations.reshape(logActivations.seq_shape());
			unnormedActivations.reshape(logActivations.seq_shape());
		}
		public void feed_forward(int[] coords)
		{	
			//transform to log scale and centre inputs on 0 for safer exponentiation
			ViewLog unnormedLogActs = unnormedlogActivations.atCoord( coords );
			ViewFloat iatc = inputActivations.atCoord(coords);
			float offset = Helpers.pair_mean(Helpers.minmax( iatc ));
			
			for(int t = 0; t < iatc.size() && t < unnormedLogActs.size(); t++) {
				Log log = new Log(iatc.at(t) - offset, true);
				unnormedLogActs.set(t, log);
			}
		 
			//apply exponential
			ViewFloat unnormedActs = unnormedActivations.atCoord( coords );
			for(int t = 0; t < unnormedLogActs.size() && t < unnormedActs.size(); t ++) {
				//FIXME
				unnormedActs.set(t,  unnormedLogActs.at(t).exp()  );
				
			//	unnormedLogActs.set(t, new Log (unnormedActs.at(t), false)  );
			}
			   
			//normalise
			float Z = Helpers.sum(unnormedActs);
			Helpers.range_divide_val( outputActivations.atCoord(coords), unnormedActs, Z);
			Helpers.range_divide_val_log( logActivations.atCoord(coords), unnormedLogActs, new Log(Z,false));
			//System.out.println();
		}
		public void feed_back(int[] coords)
		{
			ViewFloat outActs =  outputActivations.atCoord(coords);
			ViewFloat outErrs =  outputErrors.atCoord(coords);
			float Z = Helpers.inner_product(outActs, outErrs);
			
			ViewFloat inErrs =  inputErrors.atCoord(coords);
			for(int t = 0; t < inErrs.size(); t ++) {
				inErrs.set(t,  outActs.at(t) * ( outErrs.at(t) -Z)  );
			} 
		}

}
