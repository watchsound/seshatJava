package rnnlibjava;

import java.io.PrintStream;
import java.util.Vector;

import lang.ArrayListInt;
import lang.ArrayListObj;
import lang.SimpleListI;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Container.ViewV;

public class MultiArray<T>  extends MultiArrayBase<T>{
	 //data
   	ArrayListObj<T> data = new ArrayListObj<>();
	  T d;  //default value
	 
	  public MultiArray( ) {  
	  }  
	  //functions
	  public MultiArray(T d) {  
		  this.d = d;
	  }
	  public MultiArray(int[] s, T d) { 
		 this.d = d;
	     reshape(s, d);
	  }
	  public MultiArray(int[] s ) {  
		  reshape(s );
	  }
	  
		public void print(PrintStream out) {
			 
		}
	  
	 public  int size() {
	    return data.size() ;
	  }
 
	  public boolean empty()   {
	    return data.size() == 0;
	  }
	  
	  
	 public void resize_data( ) {
		 int newsize =  (int) Helpers.productInt(shape);
		 data.ensureCapacity(newsize);
	     Helpers.resize_self(data,  newsize ,this.getDefaultFill() );
	     super.resize_data( );
	  }
	 
	 
	  public void fill_data(T fillVal) {
	    fill(data, fillVal);
	  }
	  public void fill(ArrayListObj<T> data, T fillVal) {
		 this.data.clear();
		for(int i = 0; i < data.size(); i++) 
			this.data.add(fillVal);
	  }
	  
	  
	//this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewList<T> atCoord(int[] coords) {
		 if( coords.length == 0)
			 return new ViewList<T>(data, 0, data.size()) ;
		 int start = offset(coords);
		 int end = start + strides[coords.length-1];
		 return new ViewList<T>(data, start, end) ;
	  }

	  //this corresponding to at[] method in c++;
	  //because we use at() to represent [] across codes.
	  public ViewList<T> at2(int[] coords) {
		  if( in_range(coords))
			  return atCoord(coords);
		  return new ViewList<T>(data, 0,0);
	  }
	  
	 public MultiArray<T> copy(){
		 MultiArray<T> c = new MultiArray<T>();
		 c.assign(this);
		 return c;
	 }
	 
	  public void assign(MultiArray<T> a) {
		  d = a.d;
		  reshape(a.shape);
		  Helpers.copy(a.data, data); 
	  }
	  
	  public MultiArray<T> op_assign(MultiArray<T> a) {
		  assign(a);
		  return this;
	  } 
	  public boolean equals(MultiArray<T> a) {
		  return Helpers.equal(data, a.data) && Helpers.equal( shape, a.shape);
	 } 
	 public int hashCode() {
		 return Helpers.hashCode(data) + 17 * Helpers.hashCode(shape);
	 }
	 public   T getDefaultFill() {
		 return this.d;
	 }
	@Override
	public T at(int i) {
	 	return this.data.get(i);
	}
	@Override
	public T set(int i, T v) {
	 	return this.data.set(i, v);
	}
	@Override
	public int length() { 
		return this.data.length();
	}
}
