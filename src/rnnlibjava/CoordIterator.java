package rnnlibjava;

import java.util.Vector;

import lang.ArrayListInt;
import lang.SimpleListI;
import rnnlibjava.Container.View;
import seshat.Util;
 
/**
 *  T  must be  integer?????
 *
 * @param <T>
 */
public class CoordIterator<T> {
	  // data
	 SimpleListI<T> shape;
	 int[] directions;
	 int[] pt;
	  boolean end;

//	  public  CoordIterator(int[] sh){
//		  this(sh, new int[0], false);
//	  }
//      public  CoordIterator( int[] sh, int[] dirs){
//    	 this(sh, dirs, false);
//	  }
//	  // functions
//	 public  CoordIterator( int[] sh, int[] dirs,    boolean reverse){
//		this( new ArrayListInt(sh), dirs, reverse);
//	 }
   public  CoordIterator(SimpleListI<T> sh){
		  this(sh, new int[0], false);
	  }
   public  CoordIterator( SimpleListI<T> sh, int[] dirs){
    	 this(sh, dirs, false);
	  }
	  // functions
	public  CoordIterator( SimpleListI<T> sh, int[] dirs,
	      boolean reverse){
		  this.shape = sh;
		  
		  this.directions = Helpers.clone( dirs );
		  this.end = reverse;
		  
		  this.pt = new int[shape.length()];
		  for(int i = 0 ; i < this.shape.length(); i++)
			  this.pt[i] = 0; 
		  
		  this.directions = Helpers.resize(this.directions, shape.length(), 1);
		  
		  if (reverse) {
		      Helpers. range_multiply_val(directions, -1);
		    }
		    begin();
	  }
//	public View<T> toCurView(){
//		return new ViewList<T>(this.)
//	}
	     
	 public void step(int d) {
	    if (directions[d] > 0) {
	    	int v = (Integer)shape.at(d);
	      if (pt[d] == v - 1) {
	        pt[d] = 0; //.set(d, 0);
	        if (d != 0) {
	          step(d-1);
	        } else {
	          end = true;
	        }
	      } else {
	         ++pt[d]; 
	      }
	    } else {
	      if (pt[d] == 0) {
	    	int v = (Integer)shape.at(d);
	        pt[d] = v -1;//.set(d, shape[d]-1);
	        if (d != 0) {
	          step(d-1);
	        } else {
	          end = true;
	        }
	      } else {
	    	  --pt[d]; 
	      }
	    }
	  }

	  public CoordIterator op_selfadd() {
	    if (shape.length() > 0) {
	      step(shape.length()-1);
	    } else {
	      end = true;
	    }
	    return this;
	  }

	  public int[] op_self() {
	    return pt;
	  }

	  void begin() {
		for(int i = 0; i < shape.length(); i++) { 
		  int v = (Integer)shape.at(i);
	      pt[i] =   ( directions[i] > 0  ? 0 : v  - 1) ;
	    }
	    end = false;
	  }
}
