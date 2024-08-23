package rnnlibjava;

import java.util.Vector;

import lang.ArrayListInt;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.Container.ViewLog;

public class MultiArray_log  extends MultiArrayBase<Log>{
	 //data
	  protected float[] logVal = new float[0]; 
	  protected float[] expVal = new float[0]; 
	  //functions
	  public MultiArray_log() {
		 // this(new int[0]);
	  }
	  public MultiArray_log(int[] s) {
	     reshape(s);
	  }
	  public MultiArray_log(int[] s,   Log  fillval) {
	    reshape(s, fillval);
	  }
	  public void destructor() {
	  }
	  
	  public MultiArray_log(ResizableListI<Integer> s) {
		     reshape(s);
	 }
	  public MultiArray_log(ResizableListI<Integer> s, Log fillval) {
		     reshape(s, fillval);
	 }
	 public  int size() {
	    return logVal.length ;
	  }
	 
	  public boolean empty()   {
	    return logVal.length == 0;
	  }
		public void fill_data(Log fill) {
			float log = fill.logVal;
			float exp = fill.expVal;
			Helpers.fill(logVal, log);
			Helpers.fill(expVal, exp);
		}
		
	 public void resize_data( ) {
			float log = getDefaultFill().logVal;
			float exp = getDefaultFill().expVal;
	      logVal = Helpers.resize(logVal,   (int) Helpers.productInt(shape) , log);
	      expVal = Helpers.resize(expVal,   (int) Helpers.productInt(shape) , exp );
	      super.resize_data( );
	  }
	 
// 	 public void reshape(int[]  newShape, float fill) {
//	    assert(newShape.length>0);
//	    shape =  (ResizableListI<Integer>) new ArrayListInt(newShape); 
//	    resize_data(fill);
//	  }
//	 
//	 
//	  public void fill_data(float fillVal) {
//	    fill(data, fillVal);
//	  }
//	  public void fill(float[] data, float fillVal) {
//		for(int i = 0; i < data.length; i++) 
//			data[i] = fillVal;
//	  }
	  
 
	//this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewLog atCoord (int[] coords) {
		 if( coords.length == 0)
			 return new ViewLog(expVal, logVal, 0, logVal.length) ;
		 int start = offset(coords);
		 int end = start+ strides[coords.length-1];
		 return new ViewLog(expVal, logVal, start, end) ;
	  }
	   
	  public ViewLog at2(int[] coords) {
		  if( in_range(coords))
			  return atCoord(coords);
		  return new ViewLog(expVal, logVal, 0,0);
	  }
	 
	  public void assign(MultiArray_log a) {
		  reshape(a.shape);
		  Helpers.copy(a.logVal, logVal); 
		  Helpers.copy(a.expVal, expVal); 
	  }
	  
	  public MultiArray_log copy(){
		  MultiArray_log c = new MultiArray_log();
			 c.assign(this);
			 return c;
		 }
	  
	  public MultiArray_log op_assign(MultiArray_log a) {
		  assign(a);
		  return this;
	  } 
	  public boolean equals(MultiArray_log a) {
		  return Helpers.equal(logVal, a.logVal) && Helpers.equal( shape, a.shape);
	 } 
	 public int hashCode() {
		 return Helpers.hashCode(logVal) + 17 * Helpers.hashCode(shape);
	 }
	 public   Log getDefaultFill() {
		 return Log.logDefault;
	 }
	 
		@Override
		public Log at(int i) {
		 	return new Log(this.expVal[i], this.logVal[i]);
		}
		@Override
		public Log set(int i, Log v) {
		 	  this.logVal[i] = v.logVal;
			  this.expVal[i] = v.expVal; 
		 	  return v;
		}
		@Override
		public int length() { 
			return this.logVal.length;
		}
}
