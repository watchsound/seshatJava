package rnnlibjava;

import java.io.PrintStream; 

import lang.ArrayListInt;
import lang.ArrayListObj;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.View;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Container.ViewV;
import seshat.Util;

public class SeqBuffer<T> extends MultiArray<T>{

    int depth;
		 

//	public SeqBuffer(T dft) {
//		super(dft);
//		depth = 0;
//	}
	public SeqBuffer(int dep, T dft) {
		//super(new int[0], dft);
		super.d = dft;
		this.depth = dep;
		reshape(new int[0]);		
	}
	public SeqBuffer(int[] shape, int dep, T dft) {
		//super(shape, dft);
		super.d = dft;
		this.depth = dep;
		reshape(shape);	
	}
//	public void setDepth(int dep) {
//		this.depth = dep;
//		reshape(this.shape);
//	}
	 
	 

	  public int seq_offset(int[] coords) {
	    return this.offset(coords) / this.shape_back();
	  }
//
//	  using MultiArray<T>::operator[];
//	  using MultiArray<T>::at;
//
 

	  public ViewList<T> atCoord(int coord) {
	    if ((coord >= 0) && ((coord * this.shape_back()) < Helpers.productInt(this.shape))) {
	       int start = 0 + coord * this.shape_back();
	  	    int end = start + this.shape_back();
	  	   // const T* start = &this->data.front() + (coord * this->shape.back());
	  	   // const T* end = start  + this->shape.back();
	  	    return new ViewList<T>(this.data, start, end);
	    }
	    return new ViewList<T>(this.data, 0,0);
	  }
 
//
//	  const View<T> front(const vector<int>& dirs = empty_list_of<int>()) {
//	    return (*this)[*begin(dirs)];
//	  }
//
//	  const View<T> back(const vector<int>& dirs = empty_list_of<int>()) {
//	    return (*this)[*rbegin(dirs)];
//	  }
//
	  public CoordIterator begin( )  {
		  return begin(new int[0]);
	  }
	  public CoordIterator begin(int[] dirs)   {
	    return new CoordIterator(seq_shape(), dirs);
	  }

	  public CoordIterator rbegin(int[] dirs)   {
		    return new CoordIterator(seq_shape(), dirs, true);
	 }
	 
	  public  View<Integer> seq_shape()   {
	    if (this.shape.length() == 0) {
	      return new ViewInt(new int[0]);
	    }
	    if(Helpers.verbose)   System.out.print ("seq_shape() = " );
	    if(Helpers.verbose)   Util.print(this.shape, System.out);
	    return new Container.ViewList<Integer>( this.shape, 0, this.shape.length()-1);
	//    return new Container.ViewList<Integer>(this.shape);
	  //  return View<const size_t>(&this->shape.front(), &this->shape.back());
	  }
	 static ArrayListObj<Float> seqMean = new ArrayListObj<Float>();
	 public  ArrayListObj<Float> seq_means()   {
	     Helpers.resize_self(seqMean, depth, (Float)0f);
	    Helpers.fill(seqMean, 0f);
	    
	    int seqSize = seq_size();
	    for(int i = 0; i < seqSize; i++) {
	      Helpers.range_plus_equals(seqMean, (ArrayListObj<Float>)this.atCoord(i).tov());
	    }
	    Helpers.range_divide_val(seqMean, (float)seqSize);
	    return seqMean;
	  }

	  public int seq_size()   {
	    return Helpers.productInt(seq_shape());
	  }
//
	  public int num_seq_dims()   {
	    return shape.length() - 1;
	  }

	  public void reshape( SeqBuffer   buff) {
	       reshape(buff.seq_shape());
	  }

	  public void reshape(  SeqBuffer  buff,   T  fillVal) {
	      reshape(buff.seq_shape(), fillVal); 
	  }
	  public void reshape(  ResizableListI<Integer> newSeqShape) {
		  if (depth >0) {
			  if(Helpers.verbose)  System.out.print ("newSeqShape: " ); if(Helpers.verbose)  Util.print( newSeqShape, System.out);  
			  
		      // check(!in(newSeqShape, 0), "reshape called with shape " +
		      //       str(newSeqShape) + ", all dimensions must be >0");
		      this.shape =  new ArrayListInt();
		    
		      for(int i = 0; i < newSeqShape.size(); i++) {
		    	  this.shape.add(newSeqShape.at(i));
		    	 
		      }
		        
		      this.shape.add(depth);
		     
		       
		      this.resize_data( );
		      if(Helpers.verbose)  System.out.print ("shape: " );   if(Helpers.verbose) Util.print( shape, System.out);   
		    }
		  else {
			//  if (newSeqShape.length() > 0);
			//   this. shape = newSeqShape;
		  }
	  }

	  public void reshape(  ViewInt  buff,   T  fillVal) {
	    reshape( buff , fillVal);
	  }
	  public void reshape(int[] newSeqShape) {
	    if (depth >0) {
	    	 if(Helpers.verbose) 	System.out.print ("newSeqShape: " );  if(Helpers.verbose) Util.print( newSeqShape, System.out);  
	      this.shape = (ResizableListI<Integer>) new ArrayListInt(newSeqShape); 
	      
	   //   if( !this.reshaped ) {
	         this.shape.add(depth);
	    //     this.reshaped = true;
	   //   }
	      
	     
	       
	      this.resize_data( );
	      if(Helpers.verbose)  System.out.print ("shape: " );  if(Helpers.verbose) Util.print( shape, System.out);   
	    }
	  }

	 public void reshape(int[] newSeqShap, T fillval) {
	     reshape(newSeqShap);
	     fill_data(fillval);
	  }

	  public void reshape_with_depth(int[] newSeqShap, int dep) {
	    depth = dep;
	    reshape(newSeqShap);
	  }

	  public void reshape_with_depth(int[] newSeqShap, int dep, T fillval) { 
	    reshape_with_depth(newSeqShap, dep);
	    fill_data(fillval);
	  }

	  public void print(PrintStream out)   {
	    //out << "DIMENSIONS: " << seq_shape() << endl;
		  for(int j = 0; j < shape_back(); j++) {
			  for(int i = 0; i < seq_size(); i++) {
				  out.print( this.atCoord(i).at(j) + " ");
			  }
		  }
	      out.println();
	   
	  }
	  public SeqBuffer<T> assign(SeqBuffer<T> a){
		  depth = a.depth;
		  d  = a.d;
		  super.reshape(a.shape);
		  Helpers.copy(a.data, data); 
		//  shape = a.shape;
		//  data = a.data;
		//  d = a.d;
		//  resize_data();
		  return this;
	  }
	  public SeqBuffer<T> copy(){
		     SeqBuffer<T> c = new SeqBuffer<T>(depth,d);
			 c.assign(this);
			 return c;
		 }

//	  template<class T2> SeqBuffer<T>& operator =(const SeqBuffer<T2>& a) {
//	    depth = a.depth;
//	    MultiArray<T>::assign(a);
//	    return *this;
//	  }


}
