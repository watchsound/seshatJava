package rnnlibjava;

import java.lang.reflect.Array;
import java.util.Vector;

import lang.ArrayListObj;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Pair.PairFloat;
import rnnlibjava.Pair.PairInt;

public class Container {
	public static<E> E[] getArray(Class<E> clazz, int size) {
	    @SuppressWarnings("unchecked")
	    E[] arr = (E[]) Array.newInstance(clazz, size); 
	    return arr;
	}
	public static<E> E[] getArray(E t, int size) {
		  @SuppressWarnings("unchecked")
		    E[] arr = (E[]) Array.newInstance(t.getClass().getComponentType(), size); 
		    return arr; 
	}
	 public static abstract class View<V> implements ResizableListI<V>{ 
			int spos;
			//in c++,  epos is a pointer to represent end of list, 
			//so the range is [spos, epos)     spos <= xxx < epos
			int epos;
		  
			public Iter begin() {
				 return new Iter(spos, false);
			}
			public Iter end() {
				 return new Iter(epos, true);
			}
		     
			public int size() {
				return epos - spos  ;
			}
			public boolean valid() {
				if( size() == 0 )
					return false;
				V a = at(0);
				if( a == null )
					return false;
				if( a instanceof Number) {
				 	//return ((Number)a).intValue() != 0;
					if( ((Number)a).intValue() == 0 ) {
						System.out.print("");
					}
				}
				return true;
			}
			public int length() {
				return size();
			}
		   public abstract View<V> slice(int first, int last);
		   public View<V> slice(PairInt r) {
			   return slice(r.first, r.second);
		   }
		   public View<V> slice( ) {
			   return slice(0, Integer.MAX_VALUE);
		   }
		   public View<V> slice( int first) {
			   return slice(first, Integer.MAX_VALUE);
		   }
		   public abstract V at(int i);
		   public abstract V set(int i, V v);
		   public abstract void addChange(int i, V v);
		   public abstract boolean add(V v); 
		   public abstract V removeLast( );
		   public abstract View<V> copy();
		//   public abstract View op_assign(int[] r) ;
		   public ArrayListObj<V> tov(){
			   return tov(0);
		   }
		   public abstract ArrayListObj<V> tov(int start);
		 //  public abstract SimpleListI<V> tol(int start);
		   
		   public   Object to() {
			   return to(0);
		   }
		   
		   public abstract Object to(int start);
		   
		   //start, end are relative position
		   public abstract View<V> newView(int start, int end) ;
		   
		   public class Iter  implements SimpleListI<V>{
			   //absolute position
			   int pos;
			   boolean end;
//			   public Iter(int pos) {
//				  this(pos, false);
//			   }
			   public Iter(int pos, boolean end) {
				   this.pos = pos;
				   this.end = end;
			   }
			   public Iter copy() {
				   return new Iter(pos, end);
			   }
			   public boolean invalid(int offset) {
				   return pos +offset >= epos || pos + offset < spos;
			   }
			   public Iter newIter(int rMovedSteps) {
				   return new Iter(pos + rMovedSteps, end);
			   }
			   public View<V> newView(Iter  end){
				   return View.this.newView( pos - spos, end.pos - spos);
			   }
			   public View<V> newView(int length){
				   return View.this.newView( pos - spos, pos - spos + length);
			   } 
			   public View<V> newViewWithShift(int startShift, int endShift){
				   return View.this.newView( pos  + startShift - spos, pos + endShift - spos  );
			   } 
			   public View<V> view(){
				   return View.this;
			   }
			   
			   public Iter moved(int steps) {
				   pos += steps;
				   return this;
			   }
			    
			   public boolean hasNext() {
				   return pos < epos;
			   }
			   public V next() {
				   if( pos >= epos )
					   return null;
				   pos++;
				   return value();
			   }
			   public void value(V v) {
				   View.this.set(pos -spos, v);
			   }
			   public V value() {
				   return View.this.at(pos - spos);
			   }
			   public V at(int i) {
				   return View.this.at(pos + i - spos);
			   }
			   public boolean add(V v) {
				  return View.this.add(v);
			   }
			   public int size() {
				   return epos - pos + 1;
			   }
			   public int length() {
				   return size();
			   }
			   public int curPos() {
				   return pos - spos;
			   }
			   public int curPosInOriginView() {
				   return pos;
			   }
			 
			 
			   public V set(int i, V v) {
				   V ov = at(i);
				   View.this.set(pos + i - spos, v );
				   return ov;
			   }
			   public  void addChange(int i, V v) {
				   View.this.addChange(pos + i - spos, v );
			   }
			   public ArrayListObj<V> tov(){
				   return  View.this.tov(pos);
			   }
		   }
		} 
	 public static class ViewV<T> extends View<T>{
		Vector<T> p; 
		public ViewV(Vector<T> p) {
			this(p, 0,p.size() );
		}
//		public ViewV(Vector<T> p, int spos) {
//			this(p, spos,p.size() );
//		}
		public ViewV(Vector<T> p, int spos, int epos) {
			 this.p = p;
			 this.spos = spos;
			 this.epos = epos; 
		 }
		public ViewV<T> copy(){
			return new ViewV<T>(p, spos, epos);
		}
		public   ViewV<T> newView(int start, int end) {
			ViewV<T> v = new ViewV<T>(p, start + spos, end+ spos); 
			return v;
		}
	   public ViewV<T> slice(int first, int last) {
		   first = Helpers.bound(first, 0, this.size());
		   if( last < 0) {
			   last += this.size();
		   }
		   last = Helpers.bound(last, first, this.size());
		   return new ViewV<T>(p, first, last);
	   }
	  
	  
	   public T at(int i) {
		   return p.get( i + spos );
	   }
	   public boolean add(T t) {
		     p.add(t);
		     if( epos == p.size()-1)
		         epos++;
		     return true;
	   }
	   public T removeLast( ) {
		   if( p.isEmpty() ) return null;
		    T v = p.remove(epos); 
		    this.epos--;
		    return v;
	   }
	   public ViewV<T> op_assign(Vector<T> r) {
		   for( int i = 0; i < r.size(); i++) {
			   p.set(i+spos, r.get(i));
		   }
		   return this;
	   }
	   public ArrayListObj<T> tov(int start){
		   ArrayListObj<T> r = new ArrayListObj<>();
		   for( int i = start; i < size(); i++) {
			  r .add(  p.get( i+spos) ); 
		   }
		   return r;
	   }
	   public T[] to(int start){
		   T[] t = getArray(p.get(spos), size() - start);
		   for(int i = 0; i < t.length; i++) {
			   t[i] = at(i + spos + start);
		   }
		   return t; 
	   }
		@Override
		public T set(int i, T v) {
			T ov = at(i);
			p.set(i+spos, v);
			return ov;
		} 
		@Override
		public void addChange(int i, T v) {
			 throw new RuntimeException("not implemented");
		}
	} 
	 
	 public static class ViewList<T> extends View<T>{
		 ResizableListI<T> p; 
		public ViewList(ResizableListI<T> p) {
			this(p, 0,p.size() );
		}
//		public ViewList(ResizableListI<T> p, int spos) {
//			this(p, spos,p.size() );
//		}
		public ViewList(ResizableListI<T> p, int spos, int epos) {
			 this.p = p;
			 this.spos = spos;
			 this.epos = epos; 
		 }
		public ViewList copy(){
			return new ViewList (p, spos, epos);
		}
		public   ViewList<T> newView(int start, int end) {
			ViewList<T> v = new ViewList<T>(p, start + spos, end+ spos); 
			return v;
		}
	   public ViewList<T> slice(int first, int last) {
		   first = Helpers.bound(first, 0, this.size());
		   if( last < 0) {
			   last += this.size();
		   }
		   last = Helpers.bound(last, first, this.size());
		   return new ViewList<T>(p, first, last);
	   }
	  
	  
	   public T at(int i) {
		   return p.at( i + spos );
	   }
	   public boolean add(T t) {
		   p.add(t);
		   epos++;
		   return true;
	   }
	   public T removeLast( ) {
		    if( p.size() == 0 || size() <= 0 ) return null;
		    
		    T v = p.removeLast( ); 
		    if( p.size() <= epos)
		        this.epos--;
		    return v;
	   }
	   public ViewList<T> op_assign(Vector<T> r) {
		   for( int i = 0; i < r.size(); i++) {
			   p.set(i+spos, r.get(i));
		   }
		   return this;
	   }
	   public ArrayListObj<T> tov(int start){
		   ArrayListObj<T> r = new ArrayListObj<>();
		   for( int i = start; i < size(); i++) {
			  r .add(  p.at( i+spos) ); 
		   }
		   return r;
	   }
	   public T[] to(int start){
		   T[] t = getArray(p.at(spos), size() - start);
		   for(int i = 0; i < t.length; i++) {
			   t[i] = at(i + spos + start);
		   }
		   return t; 
	   }
		@Override
		public T set(int i, T v) {
			T ov = at(i);
			p.set(i+spos, v);
			return ov;
		} 
		@Override
		public void addChange(int i, T v) {
			T ov = at(i);
			if( ov == null)
				p.set(i+spos, v);
			else {
				if( ov instanceof Integer) {
					Integer ov1 = (Integer)ov;
					Integer v1 = (Integer)v;
					 p.set(i+spos, (T) (Integer)(ov1.intValue() + v1.intValue()));
				}
				if( ov instanceof Float) {
					Float ov1 = (Float)ov;
					Float v1 = (Float)v;
					 p.set(i+spos, (T) (Float)(ov1.floatValue() + v1.floatValue()));
				}
			}
			// throw new RuntimeException("not implemented");
		}
	}
	 
	 public static class ViewInt extends View<Integer>{
		int[] p; 
		public ViewInt(int[] p) {
			this(p, 0, p.length );
		}
//		public ViewInt(int[] p, int spos) {
//			this(p, spos, p.length );
//		}
		public ViewInt(int[] p, int spos, int epos) {
			 this.p = p;
			 this.spos = spos;
			 this.epos = epos;
		 }
		public ViewInt copy(){
			return new ViewInt (p, spos, epos);
		}
		public   ViewInt newView(int start, int end) {
			ViewInt v = new ViewInt(p, start+ spos, end+ spos); 
			return v;
		}
		
	   public ViewInt slice(int first, int last) {
		   first = Helpers.bound(first, 0, this.size());
		   if( last < 0) {
			   last += this.size();
		   }
		   last = Helpers.bound(last, first, this.size());
		   return new ViewInt(p, first, last);
	   }
	    
	   public Integer at(int i) {
		   //FIXME
		    if( i + spos >= p.length )
		    	return p[p.length-1];
			   
		  
		   return p[i + spos];
	   }
	   public boolean add(Integer t) {
		     p = Helpers.resize(p, size()+1,t);
		     epos++;
		     return true;
	   }
	   public Integer removeLast( ) {
		    if( p.length == 0 || size() <= 0 ) return null;
		    
		    int v = p[p.length-1];
		    p = Helpers.resize(p, p.length-1, 0);
		    if( p.length <= epos)
		        this.epos--;
		    return v;
	   }
	   
	   public ViewInt op_assign(int[] r) {
		   for( int i = 0; i < r.length; i++) {
			   p[i+spos] = r[i];
		   }
		   return this;
	   }
	   public ArrayListObj<Integer> tov(int start){
		   ArrayListObj<Integer> r = new ArrayListObj<>();
		   for( int i = start; i < size(); i++) {
			  r .add(  p[i+spos] ); 
		   }
		   return r;
	   }
	   public int[] to(int start){
		  int[] r = new int[size() - start];
		   for( int i = 0; i < r.length; i++) {
			  r[i] =  p[i+spos + start] ; 
		   }
		   return r;
	   }
		@Override
		public Integer set(int i, Integer v) {
			Integer ov = at(i);
			 p[i+spos] = v;
			 return ov;
		} 
		@Override
		public void addChange(int i, Integer v) {
			p[i+spos] += v;
		}
	} 
	 public static class ViewFloat extends View<Float>{
		float[] p; 
		public ViewFloat(float[] p) {
			this(p, 0,p.length );
		}
//		public ViewFloat(float[] p, int spos) {
//			this(p, spos,p.length );
//		}
		public ViewFloat(float[] p, int spos, int epos) {
			 this.p = p;
			 this.spos = spos;
			 this.epos = epos;
		 }
		public ViewFloat copy(){
			return new ViewFloat (p, spos, epos);
		}
		public   ViewFloat newView(int start, int end) {
			ViewFloat v = new ViewFloat(p, start+ spos, end+ spos); 
			return v;
		} 
	   public ViewFloat slice(int first, int last) {
		   first = Helpers.bound(first, 0, this.size());
		   if( last < 0) {
			   last += this.size();
		   }
		   last = Helpers.bound(last, first, this.size());
		   return new ViewFloat(p, first, last);
	   }
	  
	   public Float at(int i) {
		   //FIXME 
		   if( i + spos  > Integer.MAX_VALUE -6 || i + spos >= p.length ||  i + spos < 0 ) {
			   return p[p.length-1];
		   }
		   return p[i + spos];
	   }
	   public boolean add(Float t) {
		     p = Helpers.resize(p, size()+1,t);
		     epos++;
		     return true;
	   }
	   public Float removeLast( ) {
		    if( p.length == 0 || size() <= 0 ) return null;
		    
		    float v = p[p.length-1];
		    p = Helpers.resize(p, p.length-1, 0);
		    if( p.length <= epos)
		        this.epos--;
		    return v;
	   }
	   public ViewFloat op_assign(float[] r) {
		   for( int i = 0; i < r.length; i++) {
			   p[i+spos] = r[i];
		   }
		   return this;
	   }
	   public ArrayListObj<Float> tov(int start){
		   ArrayListObj<Float> r = new ArrayListObj<>();
		   for( int i = start; i < size(); i++) {
			  r .add(  p[i+spos] ); 
		   }
		   return r;
	   }
	   public float[] to(int start){
		   float[] r = new float[size()-start];
		   for( int i = 0; i < r.length; i++) {
			  r[i] =  p[i+spos + start] ; 
		   }
		   return r;
	   }
		@Override 
		public Float set(int i, Float v) {
			Float ov = at(i);
			try {
				p[i+spos] = v;
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return ov;
		} 
		@Override
		public void addChange(int i, Float v) {
			p[i+spos] += v;
		}
	} 
	 public static class ViewLog extends View<Log>{
		 float[] expVal; 
		 float[] logVal; 
			public ViewLog(float[] exp, float[] log) {
				this(exp, log, 0,0);
			}
//			public ViewLog(Log[] p, int spos) {
//				this(p, spos,0);
//			}
			public ViewLog(float[] exp, float[] log, int spos, int epos) {
				 this.expVal = exp;
				 this.logVal = log;
				 this.spos = spos;
				 this.epos = epos;
			 }
			public ViewLog copy(){
				return new ViewLog (expVal, logVal, spos, epos);
			}
			public   ViewLog newView(int start, int end) {
				ViewLog v = new ViewLog(expVal, logVal, start+ spos, end+  spos); 
				return v;
			} 
		   public ViewLog slice(int first, int last) {
			   first = Helpers.bound(first, 0, this.size());
			   if( last < 0) {
				   last += this.size();
			   }
			   last = Helpers.bound(last, first, this.size());
			   return new ViewLog(expVal, logVal, first, last);
		   }
	 
		   public Log at(int i) {
			   //FIXME
			   if( i + spos  > Integer.MAX_VALUE -6 || i+spos >= expVal.length || i + spos < 0)
				   i = expVal.length - spos -1;
			   return new Log( expVal[i + spos], logVal[i + spos]);
		   }
		   public boolean add(Log t) {
			     expVal = Helpers.resize(expVal, size()+1, t.expVal);
			     logVal = Helpers.resize(logVal, size()+1, t.logVal);
			     epos++;
			     return true;
		   }
		   public Log removeLast( ) {
			    if( expVal.length == 0 || size() <= 0 ) return null;
			    
			    Log v = new Log( expVal[expVal.length-1], logVal[expVal.length-1]);
			    expVal = Helpers.resize(expVal, expVal.length-1, 0);
			    logVal = Helpers.resize(logVal, logVal.length-1, 0);
			    if( expVal.length <= epos)
			        this.epos--;
			    return v;
		   }
		   
		   public ViewLog op_assign(Log[] r) {
			   for( int i = 0; i < r.length; i++) {
				   expVal[i+spos] = r[i].expVal;
				   logVal[i+spos] = r[i].logVal;
			   }
			   return this;
		   }
		   public ArrayListObj<Log> tov(int start){
			   ArrayListObj<Log> r = new ArrayListObj<>();
			   for( int i = start; i < size(); i++) {
				  r .add( new Log( expVal[i + spos], logVal[i + spos]) ); 
			   }
			   return r;
		   }
		   public Log[] to(int start){
			   Log[] r = new Log[size() - start];
			   for( int i = 0; i < r.length; i++) {
				  r[i] = new Log( expVal[i + spos + start], logVal[i + spos + start]);     
			   }
			   return r;
		   }
			@Override
			public Log set(int i, Log v) {
				//FIXME
				 if( i + spos  > Integer.MAX_VALUE -6  || i + spos < 0)
					   i = expVal.length -1 - spos;
				Log ov = at(i);
				 expVal[i+spos] = v.expVal;
				 logVal[i+spos] = v.logVal;
				 return ov;
			} 
			@Override
			public void addChange(int i, Log v) { 
				expVal[i+spos] += v.expVal;
				logVal[i+spos] += v.logVal;
			}
		} 
	 
	 
	 

      
//    	   VectorR(const View<const T>& v) {
//    *this = v;
//  }
//  Vector(size_t n): vector<T>(n) {}
//  Vector(size_t n, const T& t): vector<T>(n, t) {}
//  Vector<T>& grow(size_t length) {
//    this->resize(this->size() + length);
//    return *this;
//  }
//  Vector<T>& shrink(size_t length) {
//    this->resize(max((size_t)0, this->size() - length));
//    return *this;
//  }
//  void push_front(const T& t) {
//    this->insert(this->begin(), t);
//  }
//  T& pop_front() {
//    T& front = front();
//    erase(this->begin());
//    return front;
//  }
//  View<T> slice(int first = 0, int last = numeric_limits<int>::max()) {
//    first = bound(first, 0, (int)this->size());
//    if (last < 0) {
//      last += (int)this->size();
//    }
//    last = bound(last, first, (int)this->size());
//    return View<T>(&((*this)[first]), &((*this)[last]));
//  }
//  View<T> slice(pair<int, int>& r) {
//    return slice(r.first, r.second);
//  }
//  const View<T> slice(
//      int first = 0, int last = numeric_limits<int>::max()) const {
//    return slice(first, last);
//  }
//  const View<T> slice(pair<int, int>& r) const {
//    return slice(r.first, r.second);
//  }
//  template<class R> Vector<T>& extend(const R& r) {
//    size_t oldSize = this->size();
//    grow(boost::size(r));
//    copy(boost::begin(r), boost::end(r), this->begin() + oldSize);
//    return *this;
//  }
//  Vector<T> replicate(size_t times) const {
//    Vector<T> v;
//    REPEAT(times) {
//      v.extend(*this);
//    }
//    return v;
//  }
//  template<class R> Vector<T>& operator =(const R& r) {
//    vector<T>::resize(boost::size(r));
//    copy(r, *this);
//    return *this;
//  }
//  template<class T2> Vector<T2> to() const {
//    Vector<T2> v;
//    LOOP(const T& t, *this) {
//      v += lexical_cast<T2>(t);
//    }
//    return v;
//  }
     
}
