package rnnlibjava;

import java.util.Vector;

import lang.ArrayListInt;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewInt;

public class MultiArray_int  extends MultiArrayBase<Integer>{
	 //data
	  int[] data = new int[0]; 
	  //functions
	  public MultiArray_int() {
	//	  this(new int[0]);
	  }
	  public MultiArray_int(int[] s) {
	     reshape(s);
	  }
	  public MultiArray_int(int[] s,   int  fillval) {
	    reshape(s, fillval);
	  }
	 public MultiArray_int(ResizableListI<Integer> s  ) {
			  reshape(s);
	 }
	 public MultiArray_int(ResizableListI<Integer> s , int fillval ) {
		  reshape(s, fillval);
}
	  public void destructor() {
	  }
	  
 
	 public int[] getData() {
		return data;
	}
	public void setData(int[] data) {
		this.data = data;
	}
	public  int size() {
	    return data.length ;
	  }
	 
	  public boolean empty()   {
	    return data.length == 0;
	  }
		public void fill_data(Integer fill) {
			Helpers.fill(data, fill);
		}
	 public void resize_data( ) {
	   data = Helpers.resize(data,   (int) Helpers.productInt(shape) ,this.getDefaultFill() );
	   super.resize_data( );
	 }
	 
//	 public void reshape(int[]  newShape, int fill) {
//	    assert(newShape.length>0);
//	    shape =  (ResizableListI<Integer>) new ArrayListInt(newShape); ;
//	    resize_data(fill);
//	  }
 
//	  public void fill_data(int fillVal) {
//	    fill(data, fillVal);
//	  }
//	  public void fill(int[] data, int fillVal) {
//		for(int i = 0; i < data.length; i++) 
//			data[i] = fillVal;
//	  }
//	  
	 
	//this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewInt atCoord (int[] coords) {
		 if( coords.length == 0)
			 return new ViewInt(data, 0, data.length) ;
		 int start = offset(coords);
		 int end = start + strides[coords.length-1];
		 return new ViewInt(data, start, end) ;
	  }
	//this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewInt at2(int[] coords) {
		  if( in_range(coords))
			  return atCoord(coords);
		  return new ViewInt(data, 0,0);
	  }
	 
	  public void assign(MultiArray_int a) {
		  reshape(a.shape);
		  Helpers.copy(a.data, data); 
	  }
	  public MultiArray_int copy(){
		  MultiArray_int c = new MultiArray_int();
			 c.assign(this);
			 return c;
		 }
	  
	  public MultiArray_int op_assign(MultiArray_int a) {
		  assign(a);
		  return this;
	  } 
	  public boolean equals(MultiArray_int a) {
		  return Helpers.equal(data, a.data) && Helpers.equal( shape, a.shape);
	 } 
	 public int hashCode() {
		 return Helpers.hashCode(data) + 17 * Helpers.hashCode(shape);
	 }
	 public   Integer getDefaultFill() {
		 return 0 ;
	 }
	 
	 
		@Override
		public Integer at(int i) {
		 	return this.data[i];
		}
		@Override
		public Integer set(int i, Integer v) {
		 	return this.data[i] = v;
		}
		@Override
		public int length() { 
			return this.data.length;
		}
}
