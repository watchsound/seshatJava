package rnnlibjava;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
 
 
import lang.ArrayListObj;
import lang.Multmap;
import lang.ResizableListI;
import rnnlibjava.DataExporter.DataExportHandler;
import seshat.Util;

public class Mdrnn {

	// data
	 public  PrintStream out;
	 public  Multmap  connections = new Multmap();
	 public  Vector<Layer> hiddenLayers = new Vector<>();
	 public  Vector<Vector<Layer> > hiddenLevels = new Vector<>();
	 public  InputLayer inputLayer;
	 public Vector<NetworkOutput> outputs= new Vector<>();
	  public Vector<Layer> outputLayers = new Vector<>();
	  public ArrayListObj<Boolean> bidirectional;
	  public ArrayListObj<Boolean> symmetry;
	  public  ArrayListObj<Integer> inputBlock;
	  public  Layer  inputBlockLayer;
	  public  BiasLayer bias;
	  public  ArrayListObj<Layer> recurrentLayers = new ArrayListObj<>();
	  public  Map<String, Float> errors = new HashMap<>();
	  public Map<String, Float> normFactors= new HashMap<>();
	  public ArrayListObj<String> criteria  = new ArrayListObj<>();
	  public  WeightContainer  wc;
	  public  DataExportHandler  DEH;
	   
	
	public  Mdrnn(PrintStream o, 
			ConfigFile conf, DataHeader data, 
			WeightContainer weight, DataExportHandler deh) {
		 this.out = o;
		 this.wc = weight;
		 this.DEH = deh;
	    
	    inputLayer = new InputLayer("input", data.numDims, data.inputSize, data.inputLabels, wc, DEH);
	    bidirectional = conf.get_list("bidirectional", true, data.numDims);
	    symmetry = conf.get_list("symmetry", false, data.numDims);
	    inputBlock = conf.get_list("inputBlock", 0, data.numDims);
	    inputBlockLayer = Helpers.in(inputBlock, 0) ? null :
	    	     add_layer(new BlockLayer(inputLayer, inputBlock, wc, deh), false);
	    bias = new BiasLayer(weight, deh);
	  }

 
	  public int  num_seq_dims()   {
	    return bidirectional.size();
	  }

	 public  Layer  get_input_layer()   {
	    return inputBlockLayer != null ?
	         (inputBlockLayer) :   (inputLayer);
	  }

	  public Connection  add_connection(Connection  conn) {
		  connections.put(conn.to, conn);
	      return conn;
	  }

	  public FullConnection  connect_layers(
	      Layer  from, Layer   to,   int[] delay ) {
		  if( delay == null )
			  delay = new int[0];
	    FullConnection  conn = new FullConnection(from, to, wc, delay);
	    add_connection(conn);
	    return conn;
	  }

	  public void make_layer_recurrent(Layer  layer) {
		  int[] delay = new int[ layer.num_seq_dims()];
		  for(int i = 0; i < delay.length; i++) {
			  delay[i] = -layer.directions[i];
			  connect_layers(layer, layer, delay);
			  delay[i] = 0;
		  } 
	  }

	  public Layer gather_level(String name, int levelNum) {
	    return add_layer(new GatherLayer(name, hiddenLevels.get(levelNum), wc, DEH));
	  }

	  public Layer collapse_layer(
	      Layer  src, Layer  dest, boolean[] activeDims) {
		  if( activeDims == null )
			  activeDims = new boolean[0];
	    Layer  layer = add_layer(new CollapseLayer (src, dest, wc, DEH, activeDims));
	    add_connection(new CopyConnection(layer, dest, wc));
	    return layer;
	  }

	 public boolean is_recurrent(  Layer  layer)   {
		for(Connection c : connections.get(layer)) {
			if( c.from == c.to )
				return true;
		} 
	    return false;
	  }

	 public boolean is_mirror(  Layer  layer) {
		 for(int i = 0; i < symmetry.size(); i++) { 
	      if (symmetry.get(i) && (layer.directions[i] < 0)) {
	        return true;
	      }
	    }
	    return false;
	  }
	 public Layer  add_output_layer(Layer  output  ) {
		 return add_output_layer(output, true);
	 }
	  public Layer  add_output_layer(Layer  output, boolean addBias ) {
	    Layer  layer = (output);
	  
	    outputLayers .add(  layer );
	    if (addBias ) {
	      add_bias(layer);
	    }
	    outputs.add(  output.networkOutput);
	    return layer;
	  }
	  
	  public  Layer add_layer(Layer  layer  ) {
		  return add_layer(layer, false, false);
	  }
	  public  Layer add_layer(Layer  layer  , boolean addBias) {
		  return add_layer(layer, addBias, false);
	  }
	 public  Layer add_layer(Layer  layer, boolean addBias  , boolean recurrent ) {
		// System.out.println("add hidden layer = "  + layer);
	    hiddenLayers.add( layer );
	    if (!is_mirror(layer)) {
	      if (addBias) {
	        add_bias(layer);
	      }
	      if (recurrent) {
	        make_layer_recurrent(layer);
	      }
	    }
	    return layer;
	  }

	 public Layer  add_layer(
	        String type,   String name, int size,
	        int[] directions, boolean addBias ,
	        boolean recurrent ) {
	    Layer  layer = null;
	    if (type.equals( "tanh") ) {
	      layer = new NeuronLayer (name, directions, size, wc, DEH, new ActivationFunctions.Tanh());
	    } else if (type.equals( "softsign") ) {
	      layer = new NeuronLayer (name, directions, size, wc, DEH, new ActivationFunctions.Softsign());
	    } else if (type.equals( "logistic") ) {
	      layer = new NeuronLayer (name, directions, size, wc, DEH, new ActivationFunctions.Logistic());
	    } else if (type.equals( "identity") ) {
	      layer = new IdentityLayer(name, directions, size, wc, DEH);
	    } else if (type.equals( "lstm") ) {
	      layer = new LstmLayer (name, directions, size, wc, DEH, 1, null,
	    		  new ActivationFunctions.Tanh(),  new ActivationFunctions.Tanh(),  new ActivationFunctions.Logistic());
	    } else if (type.equals( "linear_lstm") ) {
	      layer = new LstmLayer (
							      name, directions, size, wc, DEH,  1, null,
					    		  new ActivationFunctions.Tanh(),  new ActivationFunctions.Identity(),  new ActivationFunctions.Logistic());
	    } else if (type.equals( "softsign_lstm") ) {
	      layer = new LstmLayer (
								  name, directions, size, wc, DEH, 1, null,
					    		  new ActivationFunctions.Softsign(),  new ActivationFunctions.Softsign(),  new ActivationFunctions.Logistic());
	    } else {
	     // check(false, "layer type " + type + " unknown");
	    }
	    return add_layer(layer, addBias, recurrent);
	  }

	 public Layer  add_hidden_layers_to_level(
		        String type, int size, boolean recurrent,  String name,
		      int dim, int levelNum, int[] directions ) {
		 return add_hidden_layers_to_level(type, size, recurrent, name,
				 dim, levelNum, directions, true);
	 }
	 public Layer  add_hidden_layers_to_level(
	        String type, int size, boolean recurrent,  String name,
	      int dim, int levelNum, int[] directions, boolean addBias ) {
	    if (dim == num_seq_dims()) {
	      String fullName = name + "_" +  (hiddenLevels.get(levelNum).size());
	      Layer  layer = add_layer(
	          type, fullName, size, directions, addBias, recurrent);
	      hiddenLevels.get(levelNum).add(layer);
	      return layer;
	    } else {
	      if (bidirectional.get(dim) ) {
	        directions[dim] = -1;
	        add_hidden_layers_to_level(
	            type, size, recurrent, name, dim + 1, levelNum, directions,
	            addBias);
	      }
	      directions[dim] = 1;
	      return add_hidden_layers_to_level(
	          type, size, recurrent, name, dim + 1, levelNum, directions, addBias);
	    }
	  }

	  public void build() {
	    for(Vector<Layer> v : hiddenLevels) {
	      for(Layer  dest : v) {
	        if (is_mirror(dest)) {
	         int[] sourceDirs = new int[dest.directions.length ];
	         for(int i = 0; i < sourceDirs.length && i < symmetry.size() && i < dest.directions.length; i++ ) {
	        	 sourceDirs[i]  = ( symmetry.get(i)   || dest.directions[i] != 0 ) ? 1 : -1;
	         }
	         for(Layer src : v) { 
	            if (src.directions == sourceDirs) {
	              copy_connections(src, dest, true);
	              break;
	            }
	          }
	        }
	      }
	    }
	    recurrentLayers.clear();
	    for(Layer l : hiddenLayers) {
	      l.build();
	      if (is_recurrent(l)) {
	        recurrentLayers.add(l);
	      }
	    }
	    criteria.clear();
	    for(Layer l :  outputLayers) {
	      l.build();
	    }
	    for(NetworkOutput l :  outputs) { //FIXME ???
	      criteria.addAll(l.criteria);
	    }
	  }

	 public  int copy_connections(Layer  src, Layer  dest, boolean mirror ) {
	    int numCopied = 0;
	    for(Connection c : connections.get(src)) {
	    	FullConnection oldConn = (FullConnection) c; 
	      int[] delay = oldConn.delay;
	      if (mirror) {
	    	 for(int i = 0; i < delay.length; i++) { 
	          if (src.directions[i] != dest.directions[i]) {
	            delay[i] *= -1;
	          }
	        }
	      }
	      add_connection(new FullConnection(oldConn.from == oldConn.to ? dest :
	                                        oldConn.from, dest, wc, delay, oldConn));
	      ++numCopied;
	    }
	    return numCopied;
	  }

	  public FullConnection  add_bias(Layer  layer) {
	    return connect_layers(bias, layer, null);
	  }

	  public void connect_to_hidden_level(Layer  from, int levelNum) {
		  for( int i = 0; i < hiddenLevels.get( levelNum ).size(); i++) { 
	      Layer  to = hiddenLevels.get(levelNum).get(i);
	      if (!is_mirror(to)) {
	        connect_layers(from, to, null);
	      }
	    }
	  }

	  public void connect_from_hidden_level(int levelNum, Layer  to) {
		  for( int i = 0; i < hiddenLevels.get( levelNum ).size(); i++) { 
		      Layer  from = hiddenLevels.get(levelNum).get(i); 
		        connect_layers(from, to, null); 
		    }
	  }
	  public int add_hidden_level(
		        String type, int size  ) {
		  return add_hidden_level(type, size, true, "hidden", true );
	  }
	 public int add_hidden_level(
	        String type, int size, boolean recurrent ,
	        String name  , boolean addBias ) {
		 
	    int levelNum = hiddenLevels.size(); 
	    hiddenLevels.add(new Vector<Layer>());
	    
	    int[] ddd =  new int[ num_seq_dims()];
	    for(int i = 0; i < ddd.length; i++)
	    	ddd[i] = 1;
	    add_hidden_layers_to_level(
	        type, size, recurrent, name, 0, levelNum, ddd, addBias);
	    return levelNum;
	  }

	 @SuppressWarnings("rawtypes")
	public void feed_forward_layer(Layer  layer) {
	    layer.start_sequence();
	  //  pair<CONN_IT, CONN_IT> connRange = connections.equal_range(layer);
	  if( Helpers.verbose ) {
		  System.err.println("feed_forward_layer 1 " + layer )   ;
		    Util.print("directions", layer.input_seq_begin().directions, System.err);
		    Util.print("shape", layer.input_seq_begin().shape , System.err);
		    Util.print("pt", layer.input_seq_begin().pt, System.err);
	  }

		
	    for (CoordIterator it = layer.input_seq_begin(); !it.end; it.op_selfadd()) {
	      for(Connection c : connections.get(layer)) {
	        c. feed_forward( it.op_self() );   //  repeat£ø£ø£ø£ø£ø≤ª∆•≈‰£ø£ø£ø
	      }
	    
	      layer.feed_forward( it.op_self() );
	     
	    }
	    if( Helpers.verbose )  System.err.println("feed_forward_layer 2")   ;
	  }

	 public void feed_back_layer(Layer  layer) {
		 for (CoordIterator it = layer.input_seq_rbegin(); !it.end; it.op_selfadd()) {
			 layer.feed_back( it.op_self());
		      for(Connection c : connections.get(layer)) {
		        c. feed_back( it.op_self() );
		      }  
		    }
		 for (CoordIterator it = layer.input_seq_rbegin(); !it.end; it.op_selfadd()) {
			 layer.update_derivs( it.op_self());
		      for(Connection c : connections.get(layer)) {
		        c. update_derivs( it.op_self() );
		      }  
		    } 
	  }
	  public void feed_forward(  DataSequence  seq) {
	   // check(seq.inputs.size(), "empty inputs in sequence\n" + str(seq));
	    errors.clear();
	    inputLayer.copy_inputs(seq.inputs);
	    for(Layer  layer : hiddenLayers) {  
	      feed_forward_layer(layer);
	    }
	    if( Helpers.verbose )   System.err.println("feed_forward 1")   ;
	    for(Layer  layer : outputLayers) {
	      feed_forward_layer(layer);
	    }
	    if( Helpers.verbose )  System.err.println("feed_forward 2")   ;
	  }
	  public float calculate_output_errors(  DataSequence seq) {
	    float error = 0;
	    errors.clear();
	    if (outputs.size() == 1) {
	      NetworkOutput  l = outputs.get(0);
	      error = l.calculate_errors(seq);
	      errors = l.errorMap;
	      normFactors = l.normFactors;
	    } else {
	      normFactors.clear();
	      for( NetworkOutput  l : outputs) {
	        error += l.calculate_errors(seq);
	        String layerPrefix = ((Layer)(Object)l).name + '_';
	        for(  Entry<String, Float> p : l.errorMap.entrySet()) {
	        	errors.put( p.getKey(), p.getValue() + errors.get(p.getKey() ));
	        	errors.put(layerPrefix + p.getKey(), p.getValue()   ); 
	        }
	        
	        for(  Entry<String, Float> p : l.normFactors.entrySet()) {
	        	normFactors.put( p.getKey(), p.getValue() + errors.get(p.getKey() ));
	        	normFactors.put(layerPrefix + p.getKey(), p.getValue()   ); 
	        } 
	      }
	    }
	    return error;
	  }

	  public float calculate_errors(  DataSequence seq) {
	    feed_forward(seq);
	    return calculate_output_errors(seq);
	  }

	  public void feed_back() {
		  for(int i = outputLayers.size()-1; i >=0; i--) {
			  feed_back_layer(outputLayers.get(i));
		  }
		  for(int i = hiddenLayers.size()-1; i >=0; i--) {
			  feed_back_layer(hiddenLayers.get(i));
		  } 
	  }

	 public float train(  DataSequence  seq) {
		 float error = calculate_errors(seq);
		  if( Helpers.verbose )	 System.err.println("entering..... feed_back");
	    feed_back();
	    return error;
	  }

	  public void print(PrintStream out )   {
	 //FIXME    out.print(  name  + "\n"); 
	    Helpers.prt_line(out);
	    if( Helpers.verbose )   out.println( hiddenLayers.size() + 2 + " layers:" );
	    inputLayer.print(out);
	  //  out.println(  inputLayer   );
	    for(  Layer  layer : hiddenLayers) {
	      if (is_recurrent(layer)) {
	        out.print( "(R) ");
	      }
	      layer.print(out); 
	    }
	    for(  Layer  layer : outputLayers) { 
	      layer.print(out);
	    }
	    Helpers.prt_line(out);
	    out.println( connections.getData().size() + " connections:" );
	    
	    for(Layer l : connections.getData().keySet()) {
	    	for(Connection c : connections.get(l)) {
	    		c.print(out);
	    	}
	    }
	  
	    Helpers.prt_line(out);
	    Helpers. PRINT(bidirectional, out);
	    Helpers. PRINT(symmetry, out);
	    if (inputBlockLayer != null) {
	    	Helpers.PRINT(inputBlock, out);
	    }
	  }

	  public void print_output_shape(PrintStream out  )   {
	    for(  Layer  l : outputLayers) {
	      out .println( l.name + " shape = ("  + l.outputActivations.shape  + ")" ); 
	    }
	  }
}
