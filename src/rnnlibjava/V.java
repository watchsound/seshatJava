package rnnlibjava;

import java.util.Vector;

import rnnlibjava.Container.View;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.Container.ViewV;
import rnnlibjava.Pair.PairInt;

public class V {

	public static abstract class Vector<V>{
		 
		  public Vector( )  { 
	      }
	      public Vector(int n, V fill)  {
	    	  resize(n, fill);
	      }
	      public abstract int size();
	      public abstract int indexOf(V v);
	      public abstract boolean add(V v);
	      public abstract boolean add(int pos, V v);
	      public abstract boolean remove(V v);
	      public abstract V remove(int index);
	      public abstract V get(int index);
	      public void resize(int n, V fill) {
	    	  int count = this.size();
	    	  if( count == n)
	    		  return;
	    	  if( count < n ) {
	    		  for(int i = 0; i < n - count; i++) {
	    			  add(fill);
	    		  }
	    	  } else {
	    		  for(int i = 0; i < count-n; i++) {
	    			  this.remove(n);
	    		  }
	    	  }
	      }
	      
	      public Vector<V> grow(int length, V fill){
	    	  resize(this.size() + length, fill);
	    	  return this;
	      }
	      public Vector<V> shrink(int length ){
	    	  resize(Math.max(0, this.size() -length), null);
	    	  return this;
	      }
	      public void push_front(V t) {
	    	  this.add(0,t);
	      }
	 
	      public V pop_front() {
	    	  return this.size() == 0 ? null :  this.remove(0);
	      }
	      
	      public View<V> slice(int first, int last) {
	    	first = Helpers.bound(first, 0, (int)this.size());
	    	if (last < 0) {
	    	   last += (int)this.size();
	    	}
	    	last = Helpers.bound(last, first, (int)this.size());
	    	return createView( first, last);
	      }
	      
	      public abstract View<V> createView(int first, int last);
	      
		public View<V> slice(PairInt r) {
		    return slice(r.first, r.second);
		} 
		public  abstract Vector<V> extend(Vector<V> r ) ;
		
		public abstract Vector<V> replicate(int times);
	    public abstract Vector<V> op_assign(Vector<V> r) ;
	    public abstract Vector<V> to() ;
	}
	
	
	public static class VectorR<V> extends Vector<V> {
		java.util.Vector<V> data = new java.util.Vector<V>();

		public VectorR() {
		}

		public VectorR(java.util.Vector<V> v) {
			this.data = v;
		}

		public VectorR(ViewV<V> v) {
			this.data.clear();
			for(V vv : v.tov())
				this.data.add(vv); 
		}

		public VectorR(int n, V fill) {
			resize(n, fill);
		}
	    public int indexOf(V v) {
		   return data.indexOf(v);
	    }
		public V pop_front() {
			return this.data.isEmpty() ? null : this.data.remove(0);
		}

		public VectorR<V> extend(Vector<V> r) {
			for (int i = 0; i < r.size(); i++)
				this.data.add(r.get(i));
			return this;
		}

		public VectorR<V> replicate(int times) {
			VectorR<V> v = new VectorR<V>();
			for (int i = 0; i < times; i++) {
				v.extend(this);
			}
			return v;
		}

		public VectorR<V> op_assign(Vector<V> r) {
			this.data.clear();
			this.extend(r);
			return this;
		}

		public VectorR<V> to() {
			VectorR<V> v = new VectorR<V>(this.data);
			return v;
		}

		@Override
		public int size() {
			 return data.size();
		}

		@Override
		public boolean add(V v) {
		 	return data.add(v);
		}

		@Override
		public boolean add(int pos, V v) {
		 	  data.add(pos, v);
		 	  return true;
		}

		@Override
		public boolean remove(V v) {
		 	return data.remove(v);
		}

		@Override
		public V remove(int index) { 
			return data.remove(index);
		}

		@Override
		public V get(int index) {
			 return data.get(index);
		}

		@Override
		public View<V> createView(int first, int last) {
			View<V> v = new ViewV<V>(data, first, last);
			return v;
		} 
	}
	
	public static class VectorInt extends Vector<Integer> {
		int[] data = new int[0];

		public VectorInt () {
		}

		public VectorInt (int[] data) {
			this.data = data;
		}

		public VectorInt(ViewInt v) {
			this.data = v.p;
		}

		public VectorInt(int n, int fill) {
			resize(n, fill);
		}

		public Integer pop_front() {
			if( this.data.length == 0 )
				return null ;
			int v = this.data[0];
			int[] tp = new int[data.length-1];
			System.arraycopy(data, 1, tp, 0,  tp.length);
			this.data = tp;
			return v;
		}

		public VectorInt  extend(VectorInt r) {
			int[] f = new int[this.data.length + r.data.length];
			System.arraycopy(data, 0, f, 0,  data.length);
			System.arraycopy(r.data, 0, f, data.length,  r.data.length);
			this.data = f;
			return this;
		}
		@Override
		public VectorInt extend(Vector<Integer> r) {
		    VectorInt rr = (VectorInt)r;
			return extend(rr);
		}

		@Override
		public Vector<Integer> op_assign(Vector<Integer> r) {
			 VectorInt rr = (VectorInt)r;
			return op_assign(rr);
		}
		public VectorInt replicate(int times) {
			VectorInt v = new VectorInt();
			for (int i = 0; i < times; i++) {
				v.extend(this);
			}
			return v;
		}

		public VectorInt op_assign(VectorInt r) {
			this.data = new int[0]; 
			this.extend(r);
			return this;
		}

		public VectorInt to() {
			VectorInt v = new VectorInt(this.data);
			return v;
		}

		@Override
		public int size() {
			 return data.length;
		}

		@Override
		public boolean add(Integer v) {
			int[] tp = new int[data.length+1];
			System.arraycopy(data, 01, tp, 0,  data.length);
			tp[data.length] = v;
		    this.data = tp;
		    return true;
		}

		@Override
		public boolean add(int pos, Integer v) {
			int[] tp = new int[data.length+1];
			if( pos == 0 ) {
				System.arraycopy(data, 0, tp, 1,  data.length); 
				
			}else {
				System.arraycopy(data, 0, tp, 0 ,  pos ); 
				if( data.length > pos)
				   System.arraycopy(data, pos, tp, pos+1 ,  data.length - pos); 
			}
			tp[0] = v;
		 
		    this.data = tp;
		    return true;
		}
	   public int indexOf(Integer v) {
			 for(int i = 0; i < data.length; i++) {
				 if( data[i] == v)
					 return i;
			 }
			 return -1;
	   }
		@Override
		public boolean remove(Integer v) {
		 	int index = indexOf(v);
		 	if( index < 0)
		 		return false;
		 	  remove(index);
		 	  return true;
		}

		@Override
		public Integer remove(int index) { 
			Integer v = data[index];
			int[] tp = new int[data.length-1];
			if( index == 0 ) {
				System.arraycopy(data, 1, tp, 0,  data.length-1); 
			}
			else {
				System.arraycopy(data, 0, tp, 0 ,  index ); 
				if( data.length > index)
					System.arraycopy(data, index+1, tp, index ,  data.length - index -1); 
			}

		    this.data = tp;
		    return v;
		}

		@Override
		public Integer get(int index) {
			 return data[index];
		}

		@Override
		public ViewInt createView(int first, int last) {
			ViewInt v = new ViewInt(data, first, last);
			return v;
		} 
	}
	
	public static class VectorFloat extends Vector<Float> {
		float[] data = new float[0];

		public VectorFloat () {
		}

		public VectorFloat (float[] data) {
			this.data = data;
		}

		public VectorFloat(VectorFloat v) {
			this.data = v.data;
		}

		public VectorFloat(int n, float fill) {
			resize(n, fill);
		}

		public Float pop_front() {
			if( this.data.length == 0 )
				return null ;
			float v = this.data[0];
			float[] tp = new float[data.length-1];
			System.arraycopy(data, 1, tp, 0,  tp.length);
			this.data = tp;
			return v;
		}

		public VectorFloat  extend(VectorFloat r) {
			float[] f = new float[this.data.length + r.data.length];
			System.arraycopy(data, 0, f, 0,  data.length);
			System.arraycopy(r.data, 0, f, data.length,  r.data.length);
			this.data = f;
			return this;
		}
		@Override
		public VectorFloat extend(Vector<Float> r) {
		    VectorFloat rr = (VectorFloat)r;
			return extend(rr);
		}

		@Override
		public Vector<Float> op_assign(Vector<Float> r) {
			 VectorFloat rr = (VectorFloat)r;
			return op_assign(rr);
		}
		public VectorFloat replicate(int times) {
			VectorFloat v = new VectorFloat();
			for (int i = 0; i < times; i++) {
				v.extend(this);
			}
			return v;
		}

		public VectorFloat op_assign(VectorFloat r) {
			this.data = new float[0]; 
			this.extend(r);
			return this;
		}

		public VectorFloat to() {
			VectorFloat v = new VectorFloat(this.data);
			return v;
		}

		@Override
		public int size() {
			 return data.length;
		}

		@Override
		public boolean add(Float v) {
			float[] tp = new float[data.length+1];
			System.arraycopy(data, 01, tp, 0,  data.length);
			tp[data.length] = v;
		    this.data = tp;
		    return true;
		}

		@Override
		public boolean add(int pos, Float v) {
			float[] tp = new float[data.length+1];
			if( pos == 0 ) {
				System.arraycopy(data, 0, tp, 1,  data.length); 
				
			}else {
				System.arraycopy(data, 0, tp, 0 ,  pos ); 
				if( data.length > pos)
				   System.arraycopy(data, pos, tp, pos+1 ,  data.length - pos); 
			}
			tp[0] = v;
		 
		    this.data = tp;
		    return true;
		}
	   public int indexOf(Float v) {
			 for(int i = 0; i < data.length; i++) {
				 if( data[i] == v)
					 return i;
			 }
			 return -1;
	   }
		@Override
		public boolean remove(Float v) {
		 	int index = indexOf(v);
		 	if( index < 0)
		 		return false;
		 	  remove(index);
		 	  return true;
		}

		@Override
		public Float remove(int index) { 
			Float v = data[index];
			float[] tp = new float[data.length-1];
			if( index == 0 ) {
				System.arraycopy(data, 1, tp, 0,  data.length-1); 
			}
			else {
				System.arraycopy(data, 0, tp, 0 ,  index ); 
				if( data.length > index)
					System.arraycopy(data, index+1, tp, index ,  data.length - index -1); 
			}

		    this.data = tp;
		    return v;
		}

		@Override
		public Float get(int index) {
			 return data[index];
		}

		@Override
		public ViewFloat createView(int first, int last) {
			ViewFloat v = new ViewFloat(data, first, last);
			return v;
		} 
	}
}

