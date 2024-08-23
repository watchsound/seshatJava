package rnnlibjava;

import java.util.Vector;

import rnnlibjava.Container.ViewFloat;
import rnnlibjava.DataExporter.DataExportHandler;

public class CollapseLayer extends Layer{
	  // data
	boolean[ ] activeDims;
	int[] outSeqShape;
	
	
	  public CollapseLayer(Layer src, Layer des, 
			  WeightContainer  weight, DataExportHandler  deh ) {  
		  this( src, des, weight, deh, new boolean[0]);
	  }
	  public CollapseLayer(Layer src, Layer des, 
			  WeightContainer  weight, DataExportHandler  deh,
			  boolean[] activDims) {  
		     super(des.name + "_collapse", des.directions, des.input_size(),
		    		 des.input_size() , weight, deh, src) ;
		    this.activeDims = activDims; 
		    this.activeDims = Helpers.resize(activeDims, src.num_seq_dims(),false);
		    outSeqShape = new int[0]; //FIXME ??
	  } 
	  
	  
	  public void start_sequence()
		{	
		  
			Vector<Integer> tmp = new Vector<>();
			for (int i = 0; i < activeDims.length; ++i)
			{
				if (activeDims[i])
				{
					tmp.add( source.output_seq_shape().at(i));
				}
			}
			assert(tmp.size() == num_seq_dims());
			outSeqShape = new int[ tmp.size() ];
			for(int i =0; i < tmp.size(); i++) {
				outSeqShape[i] = tmp.get(i);
			}
			inputActivations.reshape(source.output_seq_shape(), 0f);
			outputActivations.reshape(outSeqShape, 0f);
			reshape_errors();
		}
		public int[] get_out_coords(int[] inCoords)
		{
			Vector<Integer> tmp = new Vector<>();
			assert(inCoords.length == activeDims.length);
			for (int i = 0; i < inCoords.length; ++i)
			{
				if (activeDims[i])
				{
					tmp.add( inCoords[i] );
				}
			}
			int[] outCoords = new int[tmp.size()];
			for(int i =0; i < tmp.size(); i++) {
				outCoords[i] = tmp.get(i);
			}
			return outCoords;
		}
		public void feed_forward(int[] coords)
		{
			Helpers.range_plus_equals( outputActivations.atCoord(get_out_coords(coords)), inputActivations.atCoord(coords));
		}
		public void feed_back(int[] coords)
		{
			Helpers.copy(outputErrors.atCoord(get_out_coords(coords)), inputErrors.atCoord(coords));
		}
}
