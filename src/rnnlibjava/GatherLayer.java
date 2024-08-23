package rnnlibjava;

import java.util.Vector;

import rnnlibjava.Container.View.Iter;
import rnnlibjava.Container.ViewFloat;

public class GatherLayer extends Layer
{
	//data
	Vector<Layer> sources;
		
	//functions
   public GatherLayer(String name, Vector<Layer> srcs, WeightContainer weight, DataExportHandler deh) {
       super(name, srcs.get(0).num_seq_dims(), 0, get_size(srcs), weight, deh, srcs.get(0));
		this.sources = srcs; 
		source = sources.get(0);
		wc.new_parameters(0, source.name, name, source.name + "_to_" + name);
		display(outputActivations, "activations");
		display(outputErrors, "errors");
	}
   
	public static int get_size(Vector<Layer> srcs)
	{
		int size = 0;
		for (int i = 0; i < srcs.size(); ++i)
		{
			size += srcs.get(i).output_size();
		}
		return size;
	}
	public void feed_forward(int[] outCoords)
	{
		Iter  actBegin = outputActivations.atCoord( outCoords ).begin();
		for(Layer l : sources)
		{
			ViewFloat inActs = l.outputActivations.atCoord( outCoords );
			Helpers.copy(inActs, actBegin);
			actBegin .moved(  inActs.size());
		}
	}
	public void feed_back(int[] outCoords)
	{
		Iter  errBegin = outputErrors.atCoord( outCoords ).begin();
		for(Layer  l : sources)
		{
			ViewFloat inErrs = l.outputErrors.atCoord( outCoords );
			int dist = inErrs.size();
			Helpers.copy(errBegin.newView(dist), inErrs.begin());
			errBegin .moved( dist );
		}
	}

}
