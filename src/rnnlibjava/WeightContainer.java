package rnnlibjava;

import java.util.List;
import java.util.Vector;

//import com.google.common.collect.ArrayListMultimap;

import lang.ArrayListFloat;
import lang.MultmapStr2Con;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Pair.PairInt;

public class WeightContainer extends DataExporter{

	public static class Con {
		public String key1;
		public String key2;
		public int val1;
		public int val2;
		public Con() {};
		public Con(String k1, String k2, int v1, int v2) {
			this.key1 = k1;
			this.key2 = k2;
			this.val1 = v1;
			this.val2 = v2;
		}
	}
	
	 ResizableListI<Float> weights = new ArrayListFloat();
	 ResizableListI<Float> derivatives =  new ArrayListFloat();
	//ArrayListMultimap<String, Con> connections =  ArrayListMultimap.create();
	 MultmapStr2Con  connections = new MultmapStr2Con();
	
	
	public WeightContainer(DataExportHandler handle) {
		super("weightContainer", handle	);
	}
	
	 
	 public void link_layers(String fromName, String toName ) {
	     link_layers(fromName, toName, "", 0, 0);
	  }
	 public void link_layers(String fromName, String toName, String connName ) {
	     link_layers(fromName, toName, connName, 0, 0);
	  }
	 public void link_layers(String fromName, String toName,
			 String connName  , int paramBegin , int paramEnd ) {
		 connections.put(toName, new Con(fromName, connName, paramBegin, paramEnd)) ;
	  }
	  
	  public PairInt new_parameters(int numParams, String fromName, String toName, String connName) {
	    int begin = weights.size();
	     Helpers.resize_self(weights, weights.size() + numParams, 0f);
	    
	    int end = weights.size();
	    link_layers(fromName, toName, connName, begin, end);
	    return new PairInt(begin, end);
	  }

	  public  ViewList<Float> get_weights(PairInt range)
	  {
		  
		   return Helpers.splice(weights, range.first, range.second);
	  }

	 public  ViewList<Float> get_derivs(PairInt range)
	  {
		 return Helpers.splice(derivatives, range.first, range.second); 
	  }

	 public int randomise(float range)
	  {
	    int numRandWts = 0;
	    for(int i = 0; i < weights.size(); i++) {
	    	if( Float.isInfinite( weights.at(i) )) {
	    		weights.set(i, Random.uniform(range) );//(float) (Math.random() * range));
	    		numRandWts++;
	    	}
	    } 
	    return numRandWts;
	  }

	  public void reset_derivs()
	  {
	    Helpers.fill(derivatives, 0);
	  }

	  public void save_by_conns(float[] container, String nam)
	  {
		  for(String k : connections.keys()) {
			 List<Con> r = connections.get(k); 
			 for(Con c : r) {
			     save_range( new ViewFloat(container, c.val1, c.val2), c.key2 + "_" + nam);
			 }
		  } 
	  }
	  public void save_by_conns(ResizableListI<Float> container, String nam)
	  {
		  for(String k : connections.keys()) {
			 List<Con> r = connections.get(k); 
			 for(Con c : r) {  
			     save_range( new Container.ViewList<Float>(container, c.val1, c.val2), c.key2 + "_" + nam);
			 }
		  } 
	  }
//
//	  //MUST BE CALLED BEFORE WEIGHT CONTAINER IS USED
	 public void build()
	  {
	    Helpers. fill(weights, Float.POSITIVE_INFINITY);
	     Helpers.resize_self(derivatives, weights.size(),0f); 
	    save_by_conns(weights, "weights");
	    reset_derivs();
	  }
	 
	public static float perturb_weight(float weight, float stdDev, boolean additive)
	 {
	 	 weight += Random.normal(Math.abs(additive ? stdDev : stdDev * weight),0);
	 	 return weight;
	 }
	public static void perturb_weights(SimpleListI<Float> weights, float stdDev ) 
	{
		perturb_weights( weights, stdDev, true);
	}

	public static void perturb_weights(SimpleListI<Float> weights, float stdDev, boolean additive) 
	 {
	 	for(int i = 0 ; i < weights.size(); i++)
	 	{ 
	 		float w0 = perturb_weight(weights.at(i), stdDev, additive);
	 		weights.set(i, w0);
	 	}
	 }
	 public static void perturb_weights(SimpleListI<Float> weights, SimpleListI<Float> stdDev ) 
	 {
			perturb_weights( weights, stdDev, true);
	 }
	 public static void perturb_weights(SimpleListI<Float> weights, SimpleListI<Float> stdDev, boolean additive)  
	 {
	 	//assert(boost::size(weights) == boost::size(stdDevs));
	    for(int i = 0 ; i < weights.size(); i++)
	 	{
	    	float w0 = perturb_weight(weights.at(i), stdDev.at(i), additive);
	    	weights.set(i, w0);
	 	}
	 }
}
