package rnnlibjava;

import java.io.PrintStream;

import lang.ArrayListFloat;
import rnnlibjava.Container.View;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;

public abstract class Layer extends DataExporter{
	
	public static int ids = 0;
	public int id;
	
	public int[] directions = new int[0];
	
	public  SeqBuffer_float inputActivations = new SeqBuffer_float();
	public  SeqBuffer_float outputActivations = new SeqBuffer_float();
	public  SeqBuffer_float inputErrors = new SeqBuffer_float();
	public  SeqBuffer_float outputErrors = new SeqBuffer_float();
	
	public Layer source;
	public WeightContainer wc;
	public NetworkOutput networkOutput;
	
	 // functions
	 public Layer(String name, int numSeqDims, int inputSize,
	      int outputSize, WeightContainer  weight, DataExportHandler  deh, Layer src) { 
	    super(name, deh);
	    id = ids++;
	    if( id == 5) {
	    //	System.out.println("");
	    }
	    inputActivations  = new SeqBuffer_float(inputSize);  ;
	    outputActivations= new SeqBuffer_float(outputSize);  ;
	    inputErrors= new SeqBuffer_float(inputSize);  ;
	    outputErrors= new SeqBuffer_float(outputSize);  ;
	    source =  src;
	    wc = weight;
	   
	    directions = Helpers.resize(directions, numSeqDims, 1);
	  }
	 
	 public Layer(String name, int[] dirs,  int inputSize,
		      int outputSize, WeightContainer  weight, DataExportHandler  deh, Layer src) { 
		    super(name, deh);
		    inputActivations  = new SeqBuffer_float(inputSize);  ;
		    outputActivations= new SeqBuffer_float(outputSize);  ;
		    inputErrors= new SeqBuffer_float(inputSize);  ;
		    outputErrors= new SeqBuffer_float(outputSize);  ;
		    directions = Helpers.clone( dirs );
		    source =  src;
		    wc = weight; 
		    
      }
	//
 
 
	  public   int input_size()   {
	      return inputActivations.depth;
	  }

	  public   int output_size()   {
	    return outputActivations.depth;
	  }

	  public   int num_seq_dims()   {
	    return directions.length;
	  }

	  public   View<Integer> output_seq_shape()   {
	    return outputActivations.seq_shape();
	  }

	  public   View<Integer> input_seq_shape()   {
	    return input_size() != 0 ? inputActivations.seq_shape() : output_seq_shape();
	  }

	  public   CoordIterator output_seq_begin()   {
	    return outputActivations.begin(directions);
	  }

	  public CoordIterator input_seq_begin()   {
	    return input_size() != 0 ? inputActivations.begin(directions) :
	        outputActivations.begin(directions);
	  }

	  public   CoordIterator input_seq_rbegin()   {
	    return input_size() != 0 ? inputActivations.rbegin(directions) :
	        outputActivations.rbegin(directions);
	  }

	  public   void print(PrintStream out )   {
		  out.print( name  + " ");
	      out.print(" " + num_seq_dims() + "D");
	     
	    if (directions.length > 0) {
	      out.print(  " (" );
	      for(int d : directions) { 
	        out.print ((d > 0) ? "+" : "-");
	      }
	      out.print(  ")" );
	    }
	    if (input_size() == 0) {
	     out.print(  " size " + output_size() );
	    } else if (output_size() == 0 || input_size() == output_size()) {
	    	out.print(  " size "  + input_size());
	    } else {
	    	out.print(  " inputSize " + input_size()  + " outputSize " + output_size());
	    }
	    if (source != null) {
	    	out.print(  " source \"" + source.name + "\"" );
	    }
	  }

	  public   void build() {
	   // assert(source);
	  }

	  public   void reshape_errors() {
	    inputErrors.reshape(inputActivations, 0f);
	    outputErrors.reshape(outputActivations, 0f);
	  }

	  public   void start_sequence() {
	//    assert(!in(source->output_seq_shape(), 0));
	    inputActivations.reshape(source.output_seq_shape(), 0f);
	    outputActivations.reshape(source.output_seq_shape(), 0f);
	    reshape_errors();
	  }

	  public     ViewFloat out_acts(int[] coords) {
	     return outputActivations.atCoord(coords);
	  }
	  public     ViewFloat out_errs(int[] coords) {
		     return outputErrors.atCoord(coords);
	  }
	 

	  public   void feed_forward(int[] coords) {}

	  public   void feed_back(int[] coords) {}

	  public   void update_derivs(int[] coords) {}

	  public   ViewList<Float>   weights() {
	    return new ViewList<Float>(new ArrayListFloat());
	  }
	  
	  public static class FlatLayer extends Layer {
		  public FlatLayer(String name, int numSeqDims, int size, WeightContainer  weight, DataExportHandler  deh, 
				  Layer  src  ) {  
		    super(name, numSeqDims, size, size, weight, deh, src) ;
		  }
		  public FlatLayer(String name, int[] dirs, int size, WeightContainer  weight, DataExportHandler  deh, 
				  Layer  src  ) {  
		    super(name, dirs, size, size, weight, deh, src) ;
		  }
 
      } 
	  
	  ////////////////////////// for cassification class  ... hack for c++ mutliple inheritance.
		public  int output_class(int pt) {
			return 0;
		}
		public float class_prob(int pt, int index) {
			return 0;
		}
		public float set_error(int pt, int targetClass) {
			return 0;
		}
}
