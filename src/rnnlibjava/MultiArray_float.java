package rnnlibjava;

import java.util.Vector;

import lang.ArrayListInt;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewInt;

public class MultiArray_float  extends MultiArrayBase<Float>{
	 //data
	  protected float[] data = new float[0]; 
	  //functions
	  public MultiArray_float() {
		 // this(new int[0]);
	  }
	  public MultiArray_float(int[] s) {
	     reshape(s);
	  }
	  public MultiArray_float(int[] s,   float  fillval) {
	    reshape(s, fillval);
	  }
	  public void destructor() {
	  }
	  
	  public MultiArray_float(ResizableListI<Integer> s) {
		     reshape(s);
	 }
	  public MultiArray_float(ResizableListI<Integer> s, float fillval) {
		     reshape(s, fillval);
	 }
	 public  int size() {
	    return data.length ;
	  }
	 
	  public boolean empty()   {
	    return data.length == 0;
	  }
		public void fill_data(Float fill) {
			Helpers.fill(data, fill);
		}
		
	 public void resize_data( ) {
	   data = Helpers.resize(data,   (int) Helpers.productInt(shape) ,this.getDefaultFill() );
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
	  public ViewFloat atCoord (int[] coords) {
		 if( coords.length == 0)
			 return new ViewFloat(data, 0, data.length) ;
		 int start = offset(coords);
		 int end = start+ strides[coords.length-1];
		 return new ViewFloat(data, start, end) ;
	  } 
	//this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewFloat at2(int[] coords) {
		  if( in_range(coords))
			  return atCoord(coords);
		  return new ViewFloat(data, 0,0);
	  }
	 
	  public void assign(MultiArray_float a) {
		  reshape(a.shape);
		  Helpers.copy(a.data, data); 
		   
	  }
	  
	  public MultiArray_float copy(){
		  MultiArray_float c = new MultiArray_float();
			 c.assign(this);
			 return c;
		 }
	  
	  public MultiArray_float op_assign(MultiArray_float a) {
		  assign(a);
		  return this;
	  } 
	  public boolean equals(MultiArray_float a) {
		  return Helpers.equal(data, a.data) && Helpers.equal( shape, a.shape);
	 } 
	 public int hashCode() {
		 return Helpers.hashCode(data) + 17 * Helpers.hashCode(shape);
	 }
	 public   Float getDefaultFill() {
		 return 0f;
	 }
	 
		@Override
		public Float at(int i) {
		 	return this.data[i];
		}
		@Override
		public Float set(int i, Float v) {
		 	return this.data[i] = v;
		}
		@Override
		public int length() { 
			return this.data.length;
		}
}
