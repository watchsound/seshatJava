package rnnlibjava; 

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import lang.ArrayListObj;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import seshat.Util;

public class Helpers {
	public static void main(String[] argv) {
		char c = '\0';
		System.out.println(c);
		System.out.println((int)c);
		char[] v = new char[3];
		v[0] = 0;
		v[1] = '\0';
		System.out.println(v[0] == v[1]);
	}
	//global variables
	public static  float realMax = Float.MAX_VALUE;
	public static   float realMin = Float.MIN_VALUE;
	public static   float infinity = Float.POSITIVE_INFINITY;
	public static boolean runningGradTest = false;
	public static boolean verbose = false;
	public static PrintStream COUT = System.out;
	public static PrintStream cout = System.out;
	public static PrintStream cerr = System.err;
	
	public static int[] clone(int[] data) {
		if( data == null ) return null;
		if( data.length == 0 ) return new int[0];
		int[] r = new int[data.length];
		for(int i = 0; i < data.length; i++) {
			r[i] = data[i];
		}
		return r;
	}
	
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
	public static ViewList<Float> splice(ResizableListI<Float> data, int first, int last){
		   first = bound(first, 0, (int)data.size());
		    if (last < 0) {
		      last += (int)data.size();
		    }
		    last = bound(last, first, data.size());
		    return new  ViewList<Float>(data, first, last);
	}
	
	
	 public static  ArrayListObj<Integer>  str2int_1d( ArrayListObj<String>  data){
		   ArrayListObj<Integer>  r = new  ArrayListObj<Integer> (); 
			  for(String s : data) {
				  r.add(Integer.parseInt(s));
			  } 
		  return r;
	  }
	  
	  public static ArrayListObj<ArrayListObj<Integer>> str2int_2d( ArrayListObj<ArrayListObj<String>> data){
		  ArrayListObj<ArrayListObj<Integer>> r = new ArrayListObj<ArrayListObj<Integer>>();
		  for(ArrayListObj<String> row : data) {
			  ArrayListObj<Integer> nrow = new ArrayListObj<>();
			  r.add(nrow);
			  for(String s : row) {
				  nrow.add(Integer.parseInt(s));
			  }
		  }
		  return r;
	  }
	  
	  
	public static boolean isNotEmpty(int[] data) {
		if( data == null || data.length == 0 )
			return false;
		//if( data[0] == '\0' )
		//	return false;
		for(int d : data) {
			if( d != 0 )  return true;
		}
	    return false;
	}
	public static int[] v2i(ArrayListObj<Integer> data) {
		int[] t = new int[data.size()];
		for(int i = 0; i < data.size(); i++)
			t[i] = data.get(i);
		return t;
	}
	public static float[] v2f(ArrayListObj<Float> data) {
		float[] t = new float[data.size()];
		for(int i = 0; i < data.size(); i++)
			t[i] = data.get(i);
		return t;
	}
	
	public static int[] append(int[] data, int value) {
		return resize(data, data.length+1, value);
	}
	public static boolean[] append(boolean[] data, boolean value) {
		return resize(data, data.length+1, value);
	}
	public static float[] append(float[] data, float value) {
		return resize(data, data.length+1, value);
	}
	public static int[] resize(int[] data, int newSize) {
		return resize(data, newSize, 0);
	}
	public static int[] resize(int[] data, int newSize, int defaultValue) {
		int[] newone = new int[newSize]; 
		if( data == null || data.length == 0 ) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = defaultValue;
			}
			return newone;
		}
		if( data.length == newSize )
			return data;
		if( data.length > newSize) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = data[i];
			}
			return newone;
		}
		
		
		System.arraycopy(data, 0, newone, 0, data.length);
		for(int i = data.length; i < newSize; i++) {
			newone[i] = defaultValue;
		}
		return newone;
	}
	public static boolean[] resize(boolean[] data, int newSize, boolean defaultValue) {
		boolean[] newone = new boolean[newSize];
		if( data == null || data.length == 0) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = defaultValue;
			}
			return newone;
		}
		if( data.length == newSize )
			return data;
		if( data.length > newSize) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = data[i];
			}
			return newone;
		}
		
		System.arraycopy(data, 0, newone, 0, data.length);
		for(int i = data.length; i < newSize; i++) {
			newone[i] = defaultValue;
		}
		return newone;
	}
	 
	public static float[] resize(float[] data, int newSize) {
		return resize(data, newSize, 0);
	}
	public static float[] resize(float[] data, int newSize, float defaultValue) {
		float[] newone = new float[newSize];
		if( data == null || data.length == 0  ) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = defaultValue;
			}
			return newone;
		}
		if( data.length == newSize )
			return data;
		if( data.length > newSize) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = data[i];
			}
			return newone;
		}
		
		System.arraycopy(data, 0, newone, 0, data.length);
		for(int i = data.length; i < newSize; i++) {
			newone[i] = defaultValue;
		}
		return newone;
	}
	public static<T> T[] resize(T[] data, int newSize) {
		return resize(data, newSize, null);
	}
	public static<T> T[] resize(T[] data, int newSize, T defaultValue) {
		T[] newone = Container.getArray(data[0], newSize);
		if( data == null || data.length == 0) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = defaultValue;
			}
			return newone;
		}
		if( data.length == newSize )
			return data;
		if( data.length > newSize) {
			for(int i = 0; i < newSize; i++) {
				newone[i] = data[i];
			}
			return newone;
		}
		System.arraycopy(data, 0, newone, 0, data.length);
		for(int i = data.length; i < newSize; i++) {
			newone[i] = defaultValue;
		}
		return newone;
	}
	 
	public static<T> void resize_self(ResizableListI<T> data, int newSize, T defaultValue){ 
		
		if( data.size() == newSize)
			return  ;
		if ( data.size() < newSize) {
			for(int i = data.size(); i < newSize; i++) {
				data.add(defaultValue);
			}
		} else {
			for(int i = data.size(); i > newSize; i--) {
				data.removeLast();
			}
		} 
	}
	 public static int size(Object r) {
    	 int size = 0;
    	 if( r instanceof Collection)
    		 size = ((Collection)r).size();
    	 else  if( r instanceof SimpleListI)
    		 size = ((SimpleListI)r).size();
    	 else  if( r instanceof int[])
    		 size = ((int[])r).length ;
    	 else  if( r instanceof float[])
    		 size = ((float[])r).length ;
     	 else  if( r instanceof char[])
    		 size = Util.toString((char[])r).length() ;
    	 else  if( r instanceof Object[])
    		 size = ((Object[])r).length ;
    	 else {
    		try {
				Method m = r.getClass().getDeclaredMethod("size");
				size = (int) m.invoke(r);
			} catch ( Exception e) {  
				e.printStackTrace();
			} 
    		if( size == 0 ) {
    			try {
    				Method m = r.getClass().getDeclaredMethod("length");
    				size = (int) m.invoke(r);
    			} catch ( Exception e) {  
    				e.printStackTrace();
    			} 
    		}
    	 }
    	 return size;
     }
	public static  void PRINT(Object x, PrintStream o) {
		String v = x.toString();
		
	    o.print( v );
		o.print(" = " );
		if( v.length() == 0 || "false".equalsIgnoreCase(v) || "0".equals(v))
		   o.print("false" );
		else
		   o.print("true" );
		o.print("\n" ); 
	}
	public static  void PRINTN(Object x, PrintStream o) {
		String v = x.toString();
		
	    o.print( v );
		o.print(":\n" );
		print_range(o, x, "\n"); 
		o.print("\n" ); 
	}
	 
	public static  void PRT(Object x) {
		PRINT(x, cout);
	}
	public static  void PRTN(Object x) {
		PRINTN(x, cout);
	}
	public static  void PRINTR(Object x, PrintStream o, Object d) {
		String v = x.toString();
		
	    o.print( v );
		o.print(" = " );
		print_range(o, x, d.toString()); 
		o.print("\n" ); 
	}
	public static void print_range(PrintStream o, Object x, String d) {
		if( x instanceof Collection) {
			Collection c = (Collection)x;
			java.util.Iterator it = c.iterator();
			while(it.hasNext()) {
				o.print(it.next() + d);
			}
			return;
		}
		if( x instanceof int[]) {
			int[] c = (int[])x;
			for(int cc : c) {
				o.print(cc + d);
			}
			return;
		}
		if( x instanceof float[]) {
			float[] c = (float[])x;
			for(float cc : c) {
				o.print(cc + d);
			}
			return;
		}
		if( x instanceof String[]) {
			String[] c = (String[])x;
			for(String cc : c) {
				o.print(cc + d);
			}
			return;
		}
		if( x instanceof char[]) {
			char[] c = (char[])x;
			for(char cc : c) {
				o.print(cc + d);
			}
			return;
		}
	}
	   
	public static  void PRTR(Object x,  Object d) {
		PRINTR((x), cout, (d));
	}
	
	
	public static void  check(boolean condition, String str) {
		if(!condition) {
			try {
				cout.print(("ERROR: " + str + "\n\n") );
				cerr.print(("ERROR: " + str + "\n\n") );
			} catch ( Exception e) { 
				e.printStackTrace();
			}
			
		}
	}
	public static void  CHECK_STRICT(boolean condition, String str) {
		if(!condition) {
			try {
				cout.print(("ERROR: " + str + "\n\n") );
				cerr.print(("ERROR: " + str + "\n\n")  );
			} catch (Exception e) { 
				e.printStackTrace();
			} 
			throw new RuntimeException( ("ERROR: " + str + "\n\n")  );
		}
	}

	//MISC FUNCTIONS
	public static boolean  warn_unless(boolean condition, String str) {
		if(!condition) {
		 	cout.print(("WARNING: " + str + "\n\n") );  
		}
		 return condition;
	}
 
	public static void print_time(double totalSeconds) {
		print_time(totalSeconds, cout, false);
	}
	public static void print_time(double totalSeconds, PrintStream cout) {
		print_time(totalSeconds, cout, false);
	}
    public static void print_time(double totalSeconds, PrintStream cout, boolean abbrv) {
      int wholeSeconds = (int) Math.floor(totalSeconds);
	  int seconds = wholeSeconds % 60;
	  int totalMinutes = wholeSeconds / 60;
	  int minutes = totalMinutes % 60;
	  int totalHours = totalMinutes / 60;
	  int hours = totalHours % 24;
	  int totalDays = totalHours / 24;
	  int days = totalDays % 365;
	  if (days!=0) {
	    cout.print( ( days + " day" )  );
	    if (days > 1) {
	     cout.print("s");
	    }
	    cout.print(" ");
	  }
	  if (hours!=0) {
		  cout.print( ( hours + (abbrv ? " hr" : " hour") ) ); 
	    if (hours > 1) {
	    	cout.print("s");
	    }
	    cout.print(" ");
	  }
	  if (minutes!=0) {
		  cout.print( ( minutes + (abbrv ? " min" : " minute") ) ); 
	     if (minutes > 1) {
    	 	cout.print("s");
	    }
	    cout.print(" ");
	  }
	  cout.print(  ((totalSeconds - wholeSeconds + seconds)+ (abbrv ? " secs" : " seconds")  ) );
 
	}
    
    private static int num = 0;
	public static void mark() { 
	    cout.println( "MARK " + num) ;
	    ++num;
	}
	
	
	public static int squared(int t) {
	  return t *t;
	}
	public static float squared(float t) {
	   return t *t;
    }
	
	public static int sign(int t) {
		 if (t < 0) {
			    return -1;
			  } else if (t > 0) {
			    return 1;
			  } else {
			    return 0;
			  }
	 }
	
	public static float sign(float t) {
		 if (t < 0) {
			    return -1;
			  } else if (t > 0) {
			    return 1;
			  } else {
			    return 0;
			  }
	 }
	public static int bound(int v, int minVal, int maxVal) {
		 return Math.min(Math.max(minVal, v), maxVal);
	}
	public static float bound(float v, float minVal, float maxVal) {
		 return Math.min(Math.max(minVal, v), maxVal);
	}
	
	public static String str(Object t) {
		return t.toString();
	}
	public static String str(int t) {
		return t +"";
	}
	public static String str(float t) {
		return t +"";
	}
    
 
	public static<T> void flood(ArrayListObj<T> v, int size, T element) {
		resize_self(v, size, element);
	}
	 
	public static<T> int find(ArrayListObj<T> r, T t) { 
		return r.indexOf(t);
	}
   
 
	//template <class T1, class T2>
	//static pair<counting_iterator<T2>, counting_iterator<T2> >
	//span(const T1& t1, const T2& t2)
	public static irange span(int t1, int t2) {
	  return new irange ((t1 < t2 ? t1  : t2), t2);
	  //return make_pair(counting_iterator<T2>(
	  //    t1 < t2 ? static_cast<T2>(t1) : t2), counting_iterator<T2>(t2));
	}
	//template <class T> static pair<counting_iterator<T>, counting_iterator<T> >
	//span(const T& t)
     public static irange  span(int t) {
	  return span(0, t);
	  //return make_pair(counting_iterator<T>(0), counting_iterator<T>(t));
	}
	//template <class R>
	//static pair<
	//  counting_iterator<typename range_difference<R>::type>,
	//  counting_iterator<typename range_difference<R>::type> > indices(const R& r)
     public static irange indices(Object r) {
    	  return irange.indices(r);
     }
  
     public static<T> void transform(ArrayListObj<T>  r1, ArrayListObj<T>  r2, MathOpI.MathOp1<T>  op) {
    	 for(int i  = 0; i < r1.size(); i++) {
    		 T t = r1.get(i);
    		 r2.set(i, op.op(t));
    	 }
     }
     public static<T> void transform(SimpleListI<T>  r1, SimpleListI<T>  r2, MathOpI.MathOp1<T>  op) {
    	 for(int i  = 0; i < r1.size(); i++) {
    		 T t = r1.at(i);
    		 r2.set(i, op.op(t));
    	 }
     }
     public static  void transform(int[] r1, int[] r2, MathOpI.MathOp1i op) {
    	 for(int i = 0; i < r1.length; i++)
    		 r2[i] = op.op(r1[i]); 
     }
     public static  void transform(float[] r1, float[] r2, MathOpI.MathOp1f op) {
    	 for(int i = 0; i < r1.length; i++)
    		 r2[i] = op.op(r1[i]); 
     }
     public static  void transform(char[] r1, char[] r2, MathOpI.MathOp1c op) {
    	 int length = Util.toString(r1).length();
    	 for(int i = 0; i <  length; i++)
    		 r2[i] = op.op(r1[i]); 
    	 if( length < r1.length && length < r2.length ) {
    		 r2[length] = r1[length];
    	 }
     }
     public static<String> void transform(String[] r1, String[] r2, MathOpI.MathOp1<String> op) {
    	 for(int i = 0; i < r1.length; i++)
    		 r2[i] = op.op(r1[i]); 
     }
      
     public static<T> void transform(ArrayListObj  to, ArrayListObj  r1,  ArrayListObj r2,  MathOpI.MathOp2  op) {
    	 for(int i = 0; i < r1.size() && i< r2.size(); i++) {
    		 to.set(i, op.op(r1.get(i), r2.get(i)));
    	 }
     }
     public static<T> void transform(SimpleListI   to, SimpleListI   r1,  SimpleListI  r2,  MathOpI.MathOp2  op) {
    	 for(int i = 0; i < r1.size() && i< r2.size(); i++) {
    		 to.set(i, op.op(r1.at(i), r2.at(i)));
    	 }
     }
     public static  void transform(int[]  to, int[]  r1,  int[] r2,  MathOpI.MathOp2i  op) {
    	 for(int i = 0; i < r1.length && i< r2.length; i++) {
    		 to[i] = ( op.op(r1[i], r2[i]));
    	 }
     }
     public static  void transform(float[]  to, float[]  r1,  float[] r2,  MathOpI.MathOp2f  op) {
    	 for(int i = 0; i < r1.length && i< r2.length; i++) {
    		 to[i] = ( op.op(r1[i], r2[i]));
    	 }
     }
     public static  void transform(ViewFloat  to, ViewFloat  r1,  ViewFloat r2,  MathOpI.MathOp2f  op) {
    	 for(int i = 0; i < r1.size() && i< r2.size(); i++) {
    		 to.set(i, op.op(r1.at(i), r2.at(i))); 
    	 }
     }
     
     public static<E> boolean in_range(ArrayListObj<E> r, int n	) {
    	 return n >=0 && n < r.size();
     }
     public static boolean in_range(int[] r, int n	) {
    	 return n >=0 && n < r.length;
     }
     public static boolean in_range(char[] r, int n	) {
    	 int length = Util.toString(r).length();
    	 return n >=0 && n < length;
     }
     public static boolean in_range(float[] r, int n	) {
    	 return n >=0 && n < r.length;
     }
     public static<E> E nth_last(ArrayListObj<E> r, int n	) {
    	 return r.get( r.size() - n);
     }
     public static<E> E nth_last(SimpleListI<E> r, int n	) {
    	 return r.at( r.size() - n);
     }
     public static int nth_last(int[] r, int n	) {
    	 return r[ r.length - n];
     }
     public static float nth_last(float[] r, int n	) {
    	 return r[ r.length - n];
     }
     public static char nth_last(char[] r, int n	) {
    	 int length = Util.toString(r).length();
    	 return r[  length - n];
     }
     public static<E> E nth_last(ArrayListObj<E> r ) {
    	 return r.get( r.size() - 1);
     }
     public static int nth_last(int[] r ) {
    	 return r[ r.length - 1];
     }
     public static float nth_last(float[] r ) {
    	 return r[ r.length - 1];
     }
     public static char nth_last(char[] r ) {
    	 int length = Util.toString(r).length();
    	 return r[  length - 1];
     }
     public static int last_index(ArrayListObj  r ) {
    	 return r.size() -1;
     }
     public static int last_index(int[] r ) {
    	 return  r.length - 1 ;
     }
     public static int last_index(float[] r ) {
    	 return  r.length - 1 ;
     }
     public static int last_index(char[] r ) {
    	 int length = Util.toString(r).length();
    	 return  length - 1 ;
     }
     public static<T> boolean in(ArrayListObj<T> r , T t) {
    	 return r.contains(t);
     }
     public static boolean in(int[] r, int t ) {
    	 for(int i : r) {
    		 if ( i == t ) return true;
    	 }
    	 return false;
     }
     public static boolean in(float[] r, float t ) {
    	 for(float i : r) {
    		 if ( i == t ) return true;
    	 }
    	 return false;
     }
     public static boolean in(char[] r, char t ) {
    	 int length = Util.toString(r).length();
    	 for(int i = 0; i < length; i++) {
    		 if ( r[i] == t ) return true;
    	 }
    	 return false;
     }
     public static<T> int index(ArrayListObj<T> r , T t) {
    	 return r.indexOf(t);
     }
     public static int index(int[] r, int t ) {
    	for( int i = 0; i < r.length; i++) {
    		if( r[i] == t)
    			return i;
    	}
    	return -1;
     }
     public static int index(float[] r, float t ) {
    	 for( int i = 0; i < r.length; i++) {
     		if( r[i] == t)
     			return i;
     	}
     	return -1;
     }
     public static int index(char[] r, char t ) {
    	 int length = Util.toString(r).length();
    	 for( int i = 0; i < length; i++) {
     		if( r[i] == t)
     			return i;
     	}
     	return -1;
     }
     public static void reverse(ArrayListObj  r  ) {
    	 Collections.reverse(r);
     }
     public static void reverse(int[] r  ) {
    	for(int i = 0; i < r.length/2; i++) {
    		int t = r[i];
    		r[i] = r[r.length-1 - i];
    		r[r.length-1 -i] = t;
    	} 
     }
     public static void reverse(float[] r  ) {
     	for(int i = 0; i < r.length/2; i++) {
     		float t = r[i];
     		r[i] = r[r.length-1 - i];
     		r[r.length-1 -i] = t;
     	} 
      }
     public static void reverse(char[] r  ) {
    	 int length = Util.toString(r).length();
      	for(int i = 0; i <  length/2; i++) {
      		char t = r[i];
      		r[i] = r[ length-1 - i];
      		r[ length-1 -i] = t;
      	} 
       }
 
     public static   void sort(ArrayListObj  r  ) {
    	 Collections.sort(r);
     }
     public static void sort(int[] r  ) { 
    	 Arrays.sort(r);
      }
     public static void sort(float[] r  ) { 
    	 Arrays.sort(r);
      }
     public static void sort(char[] r  ) { 
    	 Arrays.sort(r);
      }
   
     public  static Pair<Comparable,Comparable> minmax(ArrayListObj<Comparable> r){
    	 if( r.isEmpty())
    		 return new Pair();
    	 Comparable min  = r.get(0);
    	 Comparable max = r.get(0);
    	 for(Comparable c : r) {
    		 if( c.compareTo(min) < 0)
    			 min = c;
    		 if( c.compareTo(min) > 0)
    			 max = c; 
    	 }
    	 return new Pair(min, max);
     }
     public  static Pair.PairFloat  minmax(SimpleListI<Float> r){
    	 if( r.size() == 0)
    		 return new Pair.PairFloat();
    	 Float min  = r.at(0);
    	 Float max = r.at(0);
    	 for(int i = 0; i < r.size(); i++) {
    		 Float c = r.at(i);
    		 if( c < min )
    			 min = c;
    		 if( c > max )
    			 max = c; 
    	 }
    	 return new Pair.PairFloat(min, max);
     }
     public  static Pair<Integer,Integer> minmax(int[] r){
    	 if( r.length == 0)
    		 return new Pair();
    	 int min  = r[0];
    	 int max = r[0];
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] < min ) min = r[i];
    		 if( r[i] > max ) max = r[i];
    	 }
    	 return new Pair(min, max);
     }
     public  static Pair<Float,Float> minmax(float[] r){
    	 if( r.length == 0)
    		 return new Pair();
    	 float min  = r[0];
    	 float max = r[0];
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] < min ) min = r[i];
    		 if( r[i] > max ) max = r[i];
    	 }
    	 return new Pair(min, max);
     }
     
     public static  void bound_range(ArrayListObj<Comparable> r, Comparable min, Comparable max){
    	 if( r.isEmpty())
    		 return ;
    	 for( int i = 0; i < r.size(); i++) {
    		 Comparable c = r.get(i);
    		 if( c.compareTo(min) < 0)
    			 r.set(i, min);
    		 if( c.compareTo(max) > 0)
    			 r.set(i, max);
    		 
    	 } 
     }
     public static void bound_range(SimpleListI<Float> r, Float min, Float max){
    	 if( r.size() == 0)
    		 return ;
    	 for( int i = 0; i < r.size(); i++) {
    		 Float c = r.at(i);
    		 if( c.compareTo(min) < 0)
    			 r.set(i, min);
    		 if( c.compareTo(max) > 0)
    			 r.set(i, max);
    		 
    	 } 
     }
     public static  void bound_range(int[] r, int min, int max){ 
    	 for( int i = 0; i < r.length; i++) { 
    		 if( r[i] < min )
    			 r[i] = min;
    		 if( r[i] > max )
    			 r[i] = max; 
    	 } 
     }
     public static  void bound_range(float[] r, float min, float max){ 
    	 for( int i = 0; i < r.length; i++) { 
    		 if( r[i] < min )
    			 r[i] = min;
    		 if( r[i] > max )
    			 r[i] = max; 
    	 } 
     }
     public static<T> void fill(ArrayListObj<T> r, T v) {
    	 for(int i = 0; i < r.size(); i++) {
    		 r.set(i, v);
    	 }
     }
     public static<T> void fill(SimpleListI<T> r, T v) {
    	 for(int i = 0; i < r.length(); i++) {
    		 r.set(i, v);
    	 }
     }
     public static void fill(SimpleListI<Float> r, float v) {
    	 for(int i = 0; i < r.length(); i++) {
    		 r.set(i, v);
    	 }
     }
     public static void fill(SimpleListI<Integer> r, int v) {
    	 for(int i = 0; i < r.length(); i++) {
    		 r.set(i, v);
    	 }
     }
     public static void fill(int[] r, int v) {
    	 for(int i = 0; i < r.length; i++) {
    		 r[i] = v;
    	 }
     }
     public static void fill(float[] r, float v) {
    	 for(int i = 0; i < r.length; i++) {
    		 r[i] = v;
    	 }
     }
     public static void fill(boolean[] r, boolean v) {
    	 for(int i = 0; i < r.length; i++) {
    		 r[i] = v;
    	 }
     }
     public static void fill(char[] r, char v) {
    	 int length = Util.toString(r).length();
    	 for(int i = 0; i <  length; i++) {
    		 r[i] = v;
    	 }
     }
     public static<T> int count(ArrayListObj<T> r, T v) {
    	 int c = 0;
    	 for(int i = 0; i < r.size(); i++) {
    		 if( r.get(i).equals(v))
    			 c++;
    	 }
    	 return c;
     }
     public static int count(int[] r, int v) {
    	 int c=0;
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] == v )
    			 c++;
    	 }
    	 return c;
     }
     public static int count(float[] r, float v) {
    	 int c=0;
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] == v )
    			 c++;
    	 }
    	 return c;
     }
     public static int count(char[] r, char v) {
    	 int length = Util.toString(r).length();
    	 int c=0;
    	 for(int i = 0; i < length; i++) {
    		 if( r[i] == v )
    			 c++;
    	 }
    	 return c;
     }
     public static void copy(SimpleListI source, SimpleListI dest) {
    	 for(int i = 0; i < source.size(); i++)
    		 dest.set(i, source.at(i));
     }
     public static void copy(ArrayListObj source, ArrayListObj dest) {
    	 for(int i = 0; i < source.size(); i++)
    		 dest.set(i, source.get(i));
     }
     public static void copy(ViewFloat source, ViewFloat dest) {
    	 for(int i = 0; i < source.size(); i++)
    		 dest.set(i, source.at(i));
     }
     public static void copy(int[] source, int[] dest) {
    	 System.arraycopy(source, 0, dest, 0, source.length);
     }
     public static void copy(float[] source, float[] dest) {
    	 System.arraycopy(source, 0, dest, 0, source.length);
     }
     public static void copy(char[] source, char[] dest) {
    	 int length = Util.toString(source).length();
    	 System.arraycopy(source, 0, dest, 0, length);
     }
     public static void reverse_copy(ArrayListObj source, ArrayListObj dest) {
    	 int size = source.size();
    	 for(int i = 0; i < size; i++)
    		 dest.set(i, source.get(size-i-1));
     }
     public static void reverse_copy(int[] source, int[] dest) {
    	 int size = source.length;
    	 for(int i = 0; i < size; i++)
    		 dest[i] =  source[size-i-1];
     }
     public static void reverse_copy(float[] source, float[] dest) {
    	 int size = source.length;
    	 for(int i = 0; i < size; i++)
    		 dest[i] =  source[size-i-1];
     }
     public static void reverse_copy(char[] source, char[] dest) {
    	 int length = Util.toString(source).length();
    	 for(int i = 0; i < length; i++)
    		 dest[i] =  source[length-i-1];
     }
     
     public static void vector_assign(ArrayListObj source, ArrayListObj dest) {
         copy(source, dest);
     }
     public static void range_negate_equals(ArrayListObj  r) {
    	 if( r.isEmpty())
    		 return;
    	 Object e = r.get(0);
    	 if( e instanceof Integer) {
    		 transform(r, r, new MathOpI.MathOp1() {
       		  public Object op(Object a) {
       			  return -((Integer)a);
       		  }
         	 });
    	 } else if( e instanceof Float ) {
    		 transform(r, r, new MathOpI.MathOp1() {
    			  public Object op(Object a) {
           			  return -((Float)a);
           		  }
       	    });
    	 } 
     }
    
     public static void range_negate_equals(int[]  r) {
    	 for(int i = 0; i < r.length; i++) {
    		 r[i] = - r[i];
    	 }
     }
     public static void range_negate_equals(float[]  r) {
    	 for(int i = 0; i < r.length; i++) {
    		 r[i] = - r[i];
    	 }
     }
    
     public static ArrayListObj range_negate(ArrayListObj  r) {
    	 if( r.isEmpty())
    		 return new ArrayListObj();
    	 Object e = r.get(0);
    	 ArrayListObj v = new ArrayListObj();
    	 vector_assign(r, v);
    	 range_negate_equals(v);
    	 return v; 
     }
     public static int[] range_negate(int[]  r) {
    	 if( r.length == 0)
    		 return new int[0];
    	 int[] v = new int[r.length];
    	 copy(r, v);
    	 range_negate_equals(v); 
    	 return v; 
     }
     public static float[] range_negate(float[]  r) {
    	 if( r.length == 0)
    		 return new float[0];
    	 float[] v = new float[r.length];
    	 copy(r, v);
    	 range_negate_equals(v); 
    	 return v; 
     }
 
     public static ArrayListObj flip(ArrayListObj  r) {
    	 if( r.isEmpty())
    		 return new ArrayListObj(); 
    	 ArrayListObj v = new ArrayListObj(); 
    	 reverse_copy(r, v);
    	 return v; 
     }
     public static int[] flip(int[]  r) {
    	 if( r.length == 0)
    		 return new int[0];
    	 int[] v = new int[r.length]; 
    	 reverse_copy(r, v); 
    	 return v; 
     }
     public static float[] flip(float[]  r) {
    	 if( r.length == 0)
    		 return new float[0];
    	 float[] v = new float[r.length]; 
    	 reverse_copy(r, v); 
    	 return v; 
     }
     
     public static boolean equal(ArrayListObj source, ArrayListObj dest) {
    	 if( source.size() != dest.size() )
    		 return false;
    	 for(int i = 0; i < source.size(); i++) {
    		 if( !source.get(i).equals(dest.get(i)))
    			 return false;
    	 }
    	 return true;
     }
     public static boolean equal(SimpleListI source, SimpleListI dest) {
    	 if( source.size() != dest.size() )
    		 return false;
    	 for(int i = 0; i < source.size(); i++) {
    		 if( !source.at(i).equals(dest.at(i)))
    			 return false;
    	 }
    	 return true;
     }
     public static boolean equal(int[] source, int[] dest) {
    	 if( source.length != dest.length )
    		 return false;
    	 for(int i = 0; i < source.length; i++) {
    		 if( source[i] != dest[i] )
    			 return false;
    	 }
    	 return true;
     }
     public static boolean equal(float[] source, float[] dest) {
    	 if( source.length != dest.length )
    		 return false;
    	 for(int i = 0; i < source.length; i++) {
    		 if( source[i] != dest[i] )
    			 return false;
    	 }
    	 return true;
     }
 
     public static Comparable max(ArrayListObj<Comparable> r) {
    	 if( r == null || r.isEmpty() )
    		 return null;
    	 Comparable max = r.get(0);
    	 for(Comparable c : r ) {
    		 if( max.compareTo(c) < 0)
    			 max = c;
    	 }
    	 return max;
     }
     public static int max(int[] r) {
    	 if( r.length == 0 )
    		 return 0;
    	 int max = r[0];
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] > max)
    			 max = r[i];
    	 }
    	 return max;
     }
     public static float max(float[] r) {
    	 if( r.length == 0 )
    		 return 0;
    	 float max = r[0];
    	 for(int i = 0; i < r.length; i++) {
    		 if( r[i] > max)
    			 max = r[i];
    	 }
    	 return max;
     }
      
     public static void print_range(ArrayListObj r, PrintStream out) {
    	 if( r.isEmpty())
    		 return;
    	 out.print(r.get(0));
    	 for(int i =1; i < r.size(); i++) 
    		 out.print(" " + r.get(i).toString() );
     }
     public static void print_range(int[] r, PrintStream out) {
    	 if( r.length == 0)
    		 return;
    	 out.print(r[0]);
    	 for(int i =1; i < r.length; i++) 
    		 out.print(" " + r[i] );
     }
     public static void print_range(float[] r, PrintStream out) {
    	 if( r.length == 0)
    		 return;
    	 out.print(r[0]);
    	 for(int i =1; i < r.length; i++) 
    		 out.print(" " + r[i] );
     }
     
 
 
//	template <class C, class Tr, class R> static basic_ostream<C, Tr>& operator <<(
//	    basic_ostream<C, Tr>& out, const R& r) {
//	  print_range(out, r);
//	  return out;
//	}
//	template <class C, class Tr, class R> static basic_istream<C, Tr>& operator >>(
//	    basic_istream<C, Tr>& in, R& r) {
//	  typename range_iterator<R>::type b = boost::begin(r);
//	  typename range_iterator<R>::type e = boost::end(r);
//	  for (; b != e; ++b) {
//	    in >> *b;
//	  }
//	  return in;
//	}
     public static void readString(Scanner scanner, ArrayListObj<String> v, int size) {
    	 for(int i = 0; i < size; i++) {
    		 v.add(scanner.next());
    	 }
     }
     public static void readInt(Scanner scanner, ArrayListObj<Integer> v, int size) {
    	 for(int i = 0; i < size; i++) {
    		 v.add(scanner.nextInt());
    	 }
     }
     public static void readFloat(Scanner scanner, ArrayListObj<Float> v, int size) {
    	 for(int i = 0; i < size; i++) {
    		 v.add(scanner.nextFloat());
    	 }
     }
     public static void readFloat(Scanner scanner, float[] v, int size) {
    	 for(int i = 0; i < size; i++) {
    		 v[i] = scanner.nextFloat() ;
    	 }
     }
     public static void readInt(Scanner scanner, int[] v, int size) {
    	 for(int i = 0; i < size; i++) {
    		 v[i] = scanner.nextInt() ;
    	 }
     }
     
     
     public static void delete_range(ArrayListObj r) {
    	 r.clear();
     }
     
     public static int range_min_size(ArrayListObj... vs) {
    	 int min = 0;
    	 for(ArrayListObj v : vs) {
    		 if( v.size() < min)
    			 min = v.size();
    	 }
    	 return min;
     }
     public static int range_min_size(int[]... vs) {
    	 int min = 0;
    	 for(int[] v : vs) {
    		 if( v.length < min)
    			 min = v.length;
    	 }
    	 return min;
     }
     public static int range_min_size(float[]... vs) {
    	 int min = 0;
    	 for(float[] v : vs) {
    		 if( v.length < min)
    			 min = v.length;
    	 }
    	 return min;
     }
  
     public static float max_element_f(ArrayListObj<Float> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  float max = v.get(0);
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.get(i) > max)
    			  max = v.get(i);
    	  }
    	  return max;
     }
     public static int max_element_i(ArrayListObj<Integer> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  int max = v.get(0);
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.get(i) > max)
    			  max = v.get(i);
    	  }
    	  return max;
     }
     public static float max_element_f(SimpleListI<Float> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  float max = v.at(0);
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.at(i) > max)
    			  max = v.at(i);
    	  }
    	  return max;
     }
     public static int max_element(int[] v) {
    	 if( v.length == 0 )
    		 return 0;
    	 int max = v[0];
    	  for(int i = 1; i < v.length; i++) {
    		  if( v[i] > max)
    			  max = v[i];
    	  }
    	  return max;
     }
     public static float max_element(float[] v) {
    	 if( v.length == 0 )
    		 return 0;
    	 float max = v[0];
    	  for(int i = 1; i < v.length; i++) {
    		  if( v[i] > max)
    			  max = v[i];
    	  }
    	  return max;
     }
     /////
     public static int max_element_index_f(ArrayListObj<Float> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  float max = v.get(0);
    	  int pos = 0;
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.get(i) > max) {
    			  max = v.get(i);
    			  pos = i;
    		  }
    	  }
    	  return pos;
     }
     public static int max_element_index_i(ArrayListObj<Integer> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  int max = v.get(0);
    	  int pos = 0;
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.get(i) > max) {
    			  max = v.get(i);
    			  pos = i;
    		  }
    	  }
    	  return pos;
     }
     public static int max_element_index_f(SimpleListI<Float> v) {
    	 if( v.size() == 0)
    		 return 0;
    	  float max = v.at(0);
    	  int pos = 0;
    	  for(int i = 1; i < v.size(); i++) {
    		  if( v.at(i) > max) {
    			  max = v.at(i);
    			  pos = i;
    		  }
    	  }
    	  return pos;
     }
     public static int max_element_index(int[] v) {
    	 if( v.length == 0 )
    		 return 0;
    	 int max = v[0];
    	 int pos = 0;
    	  for(int i = 1; i < v.length; i++) {
    		  if( v[i] > max) {
    			  max = v[i];
    			  pos = i;
    		  }
    	  }
    	  return pos;
     }
     public static int max_element_index(float[] v) {
    	 if( v.length == 0 )
    		 return 0;
    	 float max = v[0];
    	 int pos  =0;
    	  for(int i = 1; i < v.length; i++) {
    		  if( v[i] > max) {
    			  max = v[i];
    			  pos = i;
    		  }
    	  }
    	  return pos;
     }
     
     ////
    public static int arg_max_f(ArrayListObj<Float> r) {
    	return max_element_index_f(r);
    }
    public static int arg_max_i(ArrayListObj<Integer> r) {
    	 return max_element_index_i(r);
    }
    public static int arg_max(SimpleListI<Float> r) {
    	return max_element_index_f(r); 
    }
    public static int arg_max(int[] r) {
    	return max_element_index(r); 
    }
    public static int arg_max(float[] r) {
    	return max_element_index(r); 
    }
    /**
     * 
     * @param data
     * @param one
     * @param two
     * @return -1 if not found
     */
    public static int distance(ArrayListObj data, Object one, Object two) {
    	int pos1 = data.indexOf(one);
    	int pos2 = data.indexOf(two);
    	if( pos1<0 || pos2<0)
    		return  -1;
    	return Math.abs(pos1-pos2);
    }
    
    public static zip_iterable zip(Iterable... r1 ) {
    	return new zip_iterable(r1);
    }
    
    public static float inner_product(SimpleListI<Float> a, SimpleListI<Float> b, Float c) {
    	float v = c;
    	for(int i = 0; i < a.size() && i < b.size(); i++) {
    		v +=  a.at(i)*b.at(i);
    	}
    	return v;
    }
    
   
    public static float inner_product(ArrayListObj<Float> a, ArrayListObj<Float> b) {
    	float v = 0;
    	for(int i = 0; i < a.size() && i < b.size(); i++) {
    		v +=  a.get(i)*b.get(i);
    	}
    	return v;
    }
    public static float inner_product(SimpleListI<Float> a, SimpleListI<Float> b) {
    	float v = 0;
    	for(int i = 0; i < a.size() && i < b.size(); i++) {
    		v +=  a.at(i)*b.at(i);
    	}
    	return v;
    }
    public static float inner_product(float[] a, float[] b) {
    	float v = 0;
    	for(int i = 0; i < a.length && i < b.length; i++) {
    		v +=  a[i] * b[i];
    	}
    	return v;
    }
    public static int inner_product(int[] a, int[] b) {
    	int v = 0;
    	for(int i = 0; i < a.length && i < b.length; i++) {
    		v +=  a[i] * b[i];
    	}
    	return v;
    }
   
    public static float norm(ArrayListObj<Float> a, ArrayListObj<Float> b) {
        return (float) Math.sqrt( inner_product(a,b));
    }
    
    public static float norm(float[] a, float[] b) {
    	   return (float) Math.sqrt( inner_product(a,b));
    }
    public static float norm(int[] a, int[] b) {
    	   return (float) Math.sqrt( inner_product(a,b));
    }
     
    public static float sum_of_squares(ArrayListObj<Float> a, ArrayListObj<Float> b) {
    	float v = 0;
    	for(int i = 0; i < a.size() && i < b.size(); i++) {
    		v += ( a.get(i) - b.get(i)) *  ( a.get(i) - b.get(i)) ;
    	}
    	return v/2;
    }
    public static float sum_of_squares(int[] a, int[] b) {
    	float v = 0;
    	for(int i = 0; i < a.length && i < b.length; i++) {
    		v +=  (a[i] - b[i])*(a[i] - b[i]);
    	}
    	return v/2;
    }
    public static float sum_of_squares(float[] a, float[] b) {
    	float v = 0;
    	for(int i = 0; i < a.length && i < b.length; i++) {
    		v +=  (a[i] - b[i])*(a[i] - b[i]);
    	}
    	return v /2;
    }
    
    public static float product(ArrayListObj<Float> a ) {
    	float v = 1;
    	for(int i = 0; i < a.size() ; i++) {
    		v  *= a.get(i) ;
    	}
    	if( v < 0 )
    		v = Integer.MAX_VALUE -5;
    	return v ;
    }
    public static int productInt(SimpleListI  a ) {
    	int v = 1;
    	for(int i = 0; i < a.size() ; i++) {
    		v  *=  (Integer)a.at(i) ;
    	}
    	if( v < 0 )
    		v = Integer.MAX_VALUE -5;
    	return v ;
    }
    public static float productFloat(SimpleListI  a ) {
    	float v = 1;
    	for(int i = 0; i < a.size() ; i++) {
    		v  *=  (Float)a.at(i) ;
    	}
    	if( v < 0 )
    		v = Float.MAX_VALUE -5;
    	return v ;
    }
    public static float product(float[] a ) {
    	float v = 1;
    	for(int i = 0; i < a.length; i++) {
    		v *= a[i];
    	}
    	if( v < 0 )
    		v = Float.MAX_VALUE -5;
    	return v;
    }
    public static int product(int[] a ) {
    	int v = 1;
    	for(int i = 0; i < a.length; i++) {
    		v *= a[i];
    	}
    	if( v < 0 )
    		v = Integer.MAX_VALUE -5;
    	return v;
    }
    public static float sum(SimpleListI<Float> a ) {
    	float v = 0;
    	for(int i = 0; i < a.size() ; i++) {
    		v  += a.at(i) ;
    	}
    	return v ;
    }
    public static float sum(ArrayListObj<Float> a ) {
    	float v = 0;
    	for(int i = 0; i < a.size() ; i++) {
    		v  += a.get(i) ;
    	}
    	return v ;
    }
    public static float sum(float[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += a[i];
    	}
    	return v;
    }
    public static float sum(int[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += a[i];
    	}
    	return v;
    }
    public static float abs_sum(ArrayListObj<Float> a ) {
    	float v = 0;
    	for(int i = 0; i < a.size() ; i++) {
    		v  += Math.abs( a.get(i) ) ;
    	}
    	return v ;
    }
    public static float abs_sum(float[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += Math.abs( a[i]) ;
    	}
    	return v;
    }
    public static float abs_sum(int[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += Math.abs( a[i]) ;
    	}
    	return v;
    }
    public static float log_sum(ArrayListObj<Float> a ) {
    	float v = 0;
    	for(int i = 0; i < a.size() ; i++) {
    		v  += Log.safe_log( a.get(i) ) ;
    	}
    	return v ;
    }
    public static float log_sum(float[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += Log.safe_log( a[i]) ;
    	}
    	return v;
    }
    public static float log_sum(int[] a ) {
    	float v = 0;
    	for(int i = 0; i < a.length; i++) {
    		v += Log.safe_log( a[i]) ;
    	}
    	return v;
    }
    public static float mean(SimpleListI<Float> a ) {
    	 return sum(a)/a.size();
    }
    public static float mean(float[] a ) {
    	 return sum(a)/a.length;
    }
    public static float mean(int[] a ) {
    	return sum(a)/a.length;
    }
 
    public static float variance(ArrayListObj<Float> a ) {
    	float v = 0;
    	float M = mean(a);
    	for(int i = 0; i < a.size() ; i++) {
    		v  += ( a.get(i)-M) * ( a.get(i)-M);
    	}
    	return v / a.size() ;
    }
    public static float variance(float[] a ) {
    	float v = 0; float M = mean(a);
    	for(int i = 0; i < a.length; i++) {
    		v += (a[i]-M)*(a[i]-M) ;
    	}
    	return v /a.length;
    }
    public static float variance(int[] a ) {
    	float v = 0; float M = mean(a);
    	for(int i = 0; i < a.length; i++) {
    		v +=(a[i]-M)*(a[i]-M);
    	}
    	return v /a.length;
    }
 
    public static float std_dev(ArrayListObj<Float> a ) {
   	 return  (float) Math.sqrt(variance(a));
   }
   public static float std_dev(float[] a ) {
   	 return  (float) Math.sqrt(variance(a));
   }
   public static float std_dev(int[] a ) {
   	return  (float) Math.sqrt(variance(a));
   }
   
   public static ArrayListObj<Float> int2float(ArrayListObj<Integer> data){
	   ArrayListObj<Float> fs = new ArrayListObj<>();
	   for(Integer i : data) {
		   fs.add(i.floatValue());
	   }
	   return fs;
   }
 
   public static ArrayListObj<Float> range_plus(ArrayListObj<Float> a, ArrayListObj<Float> b, ArrayListObj<Float> c) {
	   transform(a, b, c,  new MathOpI.MathOp2<Float>() { 
		@Override
		public Float op(Float c, Float b) { 
			return c + b;
		}
	  }); 
	   return a;
   }
   public static SimpleListI<Float> range_plus(SimpleListI<Float> a, SimpleListI<Float> b, SimpleListI<Float> c) {
	   transform(a, b, c,  new MathOpI.MathOp2<Float>() { 
		@Override
		public Float op(Float b, Float c) { 
			return c + b;
		}
	  }); 
	   return a;
   }
   public static int[] range_plus(int[] a, int[] b,int[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2i() { 
		@Override
		public int op(int c, int b) { 
			return c + b;
		}
	  }); 
	   return a;
   }
   public static int[] range_plus(int[] a, SimpleListI<Integer> b,int[] c) {
	   for(int i  = 0; i < b.length(); i++) {
		   a[i] = b.at(i) + c[i];
	   }
	   return a;
   }
   /**
    * add b + c ->  a
    * @param a
    * @param b
    * @param c
    * @return
    */
   public static float[] range_plus(float[] a, float[] b,float[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2f() { 
		@Override
		public float op(float c, float b) { 
			return c + b;
		}
	  }); 
	   return a;
   }
   public static float[] range_minus(float[] a, float[] b,float[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2f() { 
		@Override
		public float op(float b, float c) { 
			return b - c;
		}
	  }); 
	   return a;
   }
   public static int[] range_minus(int[] a, int[] b,int[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2i() { 
		@Override
		public int op(int b, int c) { 
			return b - c;
		}
	  }); 
	   return a;
   }
   public static ArrayListObj<Float> range_plus_equals(ArrayListObj<Float> a, ArrayListObj<Float> b ) {
	   return range_plus(a,a,b);
   }
   public static SimpleListI<Float> range_plus_equals(SimpleListI<Float> a, SimpleListI<Float> b ) {
	   return range_plus(a,a,b);
   }
   public static int[] range_plus_equals(int[] a, int[] b ) {
	   return range_plus(a,a,b);
   }
   public static float[] range_plus_equals(float[] a, float[] b ) {
	   return range_plus(a,a,b);
   }
   /**
    *  b + c => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_multiply_val(SimpleListI<Integer> a, SimpleListI<Integer> b, SimpleListI<Integer>  c) {
	   for(int i = 0; i < b.size() && i < c.size(); i++) {
  		  a.set(i,  b.at(i) * c.at(i));
  	   } 
   }
   /**
    *  b + c => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_multiply_val(ArrayListObj<Float> a, ArrayListObj<Float> b, Float  c) {
	   for(int i = 0; i < b.size(); i++) {
  		  a.set(i,  b.get(i) * c);
  	   } 
   }
   /**
    *  b + c => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_multiply_val(int[] a, int[] b, SimpleListI<Integer> c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] * c.at(i);
	   }
   }
   /**
    *  b + c => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_multiply_val(int[] a, int[] b, int c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] * c;
	   }
   }
   public static void range_multiply_val(int[] a, int[] b, int[] c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] * c[i];
	   }
   }
   /**
    *  b + c => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_multiply_val(float[] a, float[] b, float c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] * c;
	   }
   }
   /**
    * 
    * @param a
    * @param c
    */
   public static void range_multiply_val(ArrayListObj<Float> a,  float  c) {
	   for(int i = 0; i < a.size(); i++) {
		   a.set(i, a.get(i)*c);
	   }
   }
   public static void range_multiply_val(ArrayListObj<Integer> a,  int  c) {
	   for(int i = 0; i < a.size(); i++) {
		   a.set(i, a.get(i)*c);
	   }
   }
   public static void range_multiply_val(float[] a , float c ) {
	   for(int i  = 0; i < a.length; i++) {
		   a[i] = a[i] * c;
	   }
   }
   public static void range_multiply_val(int[] a , int c ) {
	   for(int i  = 0; i < a.length; i++) {
		   a[i] = a[i] * c;
	   }
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_divide_val(ArrayListObj<Float> a, ArrayListObj<Float> b, Float  c) {
	   for(int i = 0; i < b.size(); i++) {
	       a.set(i,  b.get(i) / c);
	   } 
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_divide_val(int[] a, int[] b, int c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] / c;
	   }
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_divide_val(float[] a, float[] b, float c ) {
	   for(int i  = 0; i < b.length; i++) {
		   a[i] = b[i] / c;
	   }
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_divide_val(SimpleListI<Float> a, SimpleListI<Float> b, float c ) {
	   for(int i  = 0; i < b.size() && i< a.size(); i++) {
		   a.set(i ,  b.at(i) / c );
	   }
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static void range_divide_val_log(SimpleListI<Log> a, SimpleListI<Log> b, Log c ) {
	   for(int i  = 0; i < b.size(); i++) {
		   a.set(i ,  b.at(i).divide(c) );
	   }
   }
 
   public static void range_divide_val(ArrayListObj<Float> a,  Float  c) {
	   for(int i = 0; i < a.size(); i++) {
		   a.set(i, a.get(i)/c);
	   }
   }
   
   public static void range_divide_val(float[] a , float c ) {
	   for(int i  = 0; i < a.length; i++) {
		   a[i] = a[i] / c;
	   }
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static ArrayListObj<Float> range_divide(ArrayListObj<Float> a, ArrayListObj<Float> b, ArrayListObj<Float> c) {
	   transform(a, b, c,  new MathOpI.MathOp2<Float>() { 
		@Override
		public Float op(Float x, Float y) { 
			return x / y;
		}
	  }); 
	   return a;
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static int[] range_divide(int[] a, int[] b,int[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2i() { 
		@Override
		public int op(int x, int y) { 
			return x / y;
		}
	  }); 
	   return a;
   }
   /**
    * b/c  => a
    * @param a
    * @param b
    * @param c
    */
   public static float[] range_divide(float[] a, float[] b,float[] c) {
	   transform(a, b, c,  new MathOpI.MathOp2f() { 
		@Override
		public float op(float x, float y) { 
			return x /  y;
		}
	  }); 
	   return a;
   }
 
   public static void print(PrintStream out, ArrayListObj t1, ArrayListObj t2) {
	   
   }
//    //TUPLE OPERATIONS
//    template<class T1, class T2> static ostream& operator << (
//        ostream& out, const tuple<T1, T2>& t) {
//      out << t.get<0>() << " " << t.get<1>();
//      return out;
//    }
//    template<class T1, class T2, class T3> static ostream& operator << (
//        ostream& out, const tuple<T1, T2, T3>& t) {
//      out << t.get<0>() << " " << t.get<1>() << " " << t.get<2>();
//      return out;
//    }
//    template<class T1, class T2, class T3, class T4> static ostream& operator << (
//        ostream& out, const tuple<T1, T2, T3, T4>& t) {
//      out << t.get<0>() << " " << t.get<1>() << " " << t.get<2>() << " "
//          << t.get<3>();
//      return out;
//    }
//    template<class T1, class T2, class T3, class T4, class T5>
//    static ostream& operator << (ostream& out, const tuple<T1, T2, T3, T4, T5>& t) {
//      out << t.get<0>() << " " << t.get<1>() << " " << t.get<2>() << " "
//          << t.get<3>() << " " << t.get<4>();
//      return out;
//    }
   
   
   public static boolean in_open_interval(Pair<Number, Number> interval, Number val) {
	   return interval.first.floatValue() <  val.floatValue() 
			   && interval.second.floatValue() >  val.floatValue();
   }
   public static boolean in_open_interval(Pair.PairFloat interval, float val) {
	   return interval.first  > val    && interval.second  >  val ;
   }
   public static boolean in_open_interval(Pair.PairInt interval, int val) {
	   return interval.first  <  val    && interval.second  >  val ;
   }
   public static boolean in_closed_interval(Pair<Number, Number> interval, Number val) {
	   return interval.first.floatValue() <= val.floatValue() 
			   && interval.second.floatValue() >= val.floatValue();
   }
   public static boolean in_closed_interval(Pair.PairFloat interval, float val) {
	   return interval.first  >= val    && interval.second  >= val ;
   }
   public static boolean in_closed_interval(Pair.PairInt interval, int val) {
	   return interval.first  <=  val    && interval.second  >=  val ;
   }
   
   public static void self_plus(Pair.PairInt a, Pair.PairInt b) {
	    a.first += b.first;
	    a.second += b.second;
   }
   public static void self_plus(Pair.PairFloat a, Pair.PairFloat b) {
	    a.first += b.first;
	    a.second += b.second;
  }
   public static Pair.PairInt plus(Pair.PairInt a, Pair.PairInt b) {
	   Pair.PairInt r = new Pair.PairInt();
	   r.first =  a.first + b.first;
	   r.second = a.second + b.second;
	   return r;
  }
   public static Pair.PairFloat plus(Pair.PairFloat a, Pair.PairFloat b) {
	   Pair.PairFloat r = new Pair.PairFloat();
	   r.first =  a.first + b.first;
	   r.second = a.second + b.second;
	   return r;
  }
 
   public static float pair_product(Pair.PairFloat a ) {
	   return a.first * a.second;
   }
   public static int pair_product(Pair.PairInt a ) {
	   return a.first * a.second;
   }
   public static float pair_sum(Pair.PairFloat a ) {
	   return a.first + a.second;
   }
   public static int pair_sum(Pair.PairInt a ) {
	   return a.first + a.second;
   }
   public static float pair_mean(Pair.PairFloat p ) {
	   return (float) (pair_sum(p)/2.0);
   }
   public static int pair_mean(Pair.PairInt p ) {
	   return (int) (pair_sum(p)/2.0);
   }
   public static float difference(Pair.PairFloat a ) {
	   return a.second - a.first;
   }
   public static int difference(Pair.PairInt a ) {
	   return a.second - a.first;
   }
   
   public static<T1,T2> boolean in(Map<T1,T2> a, T1 b) {
	   return a.containsKey(b);
   }
    
   public static<T1,T2> T2 at(Map<T1,T2> a, T1 b) {
	   return a.get(b);
   }
 
   public static void print_left(Map m) {
	   print_left(m, cout, ' ');
   }
   public static void print_left(Map m, PrintStream out, char delim) {
	   for(Object k : m.keySet()) {
		   out.print( k );
		   out.print(delim);
	   }
   }
   
   public static void print_right(Map m) {
	   print_right(m, cout, ' ');
   }
   public static void print_right(Map m, PrintStream out, char delim) {
	   for(Object k : m.values()) {
		   out.print( k );
		   out.print(delim);
	   }
   }
   public static void print(Map m, PrintStream out ) {
	   for(Object key :  m.keySet()) {
		   out.print( key + " " + m.get(key) );
		   out.println();
	   }
   }
 
   public static float sum_right(Map m) {
	   float ret = 0;
	   for(Object v : m.values()) {
		   ret +=  ((Number)v).floatValue();
	   }
	   return ret;
   }
   public static void add(Map to, Map from) { 
	   for(Object v : from.keySet()) {
		  Number fv = (Number) from.get(v);
		  Number ft = (Number) to.get(v);
		  to.put(v, ft == null? fv :  fv.floatValue() + ft.floatValue());
	   } 
   }
   public static void minus(Map to, Map from) { 
	   for(Object v : from.keySet()) {
		  Number fv = (Number) from.get(v);
		  Number ft = (Number) to.get(v);
		  to.put(v, ft == null? -fv.floatValue() :   ft.floatValue() - fv.floatValue());
	   } 
   }
   public static void divide(Map to, Map from) { 
	   for(Object v : from.keySet()) {
		  Number fv = (Number) from.get(v);
		  Number ft = (Number) to.get(v);
		  to.put(v, ft == null? 0 :   ft.floatValue() / fv.floatValue());
	   } 
   }
   public static void multiple(Map to, Map from) { 
	   for(Object v : from.keySet()) {
		  Number fv = (Number) from.get(v);
		  Number ft = (Number) to.get(v);
		  to.put(v, ft == null? 0 :   ft.floatValue() * fv.floatValue());
	   } 
   }
   public static void multiple(Map to, float b) { 
	   for(Object v : to.keySet()) {
		  Number fv = (Number) to.get(v); 
		  to.put(v, fv.floatValue()* b);
	   } 
   }
   public static void divide(Map to, float b) { 
	   for(Object v : to.keySet()) {
		  Number fv = (Number) to.get(v); 
		  to.put(v, fv.floatValue() / b);
	   } 
   }
  
   public static void print(Object t) {
	   print(  cout, t);
   }
    public static void print( PrintStream out, Object... t ) {
	   for(Object tt : t) {
		   out.print(tt + " "); 
	   }
	   out.println();
   }
 
    public static void print(float t) {
    	float[] tt = new float[1];
    	tt[0] = t;
 	   print(  cout, tt);
    }
     public static void print( PrintStream out, float... t ) {
 	   for(float tt : t) {
 		   out.print(tt + " "); 
 	   }
 	   out.println();
    }
     
     public static void prt_line( ) {
    	 prt_line(cout);
     }
    public static void prt_line(PrintStream out ) {
    	out.println("------------------------------");
    }
 
    
////    template<class T> static T read(const string& data) {
////      T val;
////      stringstream ss;
////      ss << boolalpha << data;
////      check(ss >> val, "cannot read string '" + data +
////            "' into variable with type '" + typeid(T).name() + "'");
////      return val;
////    }
//    
    public static boolean disjoint(Set s1, Set s2) {
    	for(Object o : s1) {
    		if( s2.contains(o))
    			return false;
    	}
    	for(Object o : s2) {
    		if( s1.contains(o))
    			return false;
    	}
    	return true;
    }
    public static boolean intersecting(Set s1, Set s2) {
    	 return !disjoint(s1,s2);
    }
    public static float  KL_normal(float pMean, float pVar, float qMean, float qVar) {
    	 return (float) (0.5 * (Math.log(qVar/pVar) - 1 + (( (pMean - qMean)* (pMean - qMean) + pVar) / qVar)));
    }
    
    public static int hashCode(SimpleListI  data) {
    	int code = 0;
    	final int[] p = {3,5,7,11,13,17,23,29,31,41};
    	for(int i = 0; i < data.size(); i++) {
    		code +=  data.at(i).hashCode() * p[ i % p.length ];
    	}
    	return code;
    }
    
    public static int hashCode(int[] data) {
    	int code = 0;
    	final int[] p = {3,5,7,11,13,17,23,29,31,41};
    	for(int i = 0; i < data.length; i++) {
    		code +=  data[i] * p[ i % p.length ];
    	}
    	return code;
    }
    public static int hashCode(float[] data) {
    	float code = 0;
    	final int[] p = {3,5,7,11,13,17,23,29,31,41};
    	for(int i = 0; i < data.length; i++) {
    		code +=  data[i] * p[ i % p.length ];
    	}
    	return (int) code;
    }

    public static<T> int hashCode(ArrayListObj<T> data) {
    	int code = 0;
    	final int[] p = {3,5,7,11,13,17,23,29,31,41};
    	for(int i = 0; i < data.size(); i++) {
    		T d = data.get(i); 
    		code +=  d.hashCode() * p[ i % p.length ];
    	}
    	return code;
    }
////    //PROBABILITY FUNCTIONS
////    static real_t KL_normal(real_t pMean, real_t pVar, real_t qMean, real_t qVar) {
////      return 0.5 * (log(qVar/pVar) - 1 + ((squared(pMean - qMean) + pVar) / qVar));
////    }
//    
     public static<T> ArrayListObj<T> empty_list_of(){
     	return new ArrayListObj<T>();
     }
     public static int[] list_of(int size, int d) {
    	 int[] r = new int[size];
    	 for(int i = 0; i < r.length; i++)
    		 r[i] = d;
    	 return r;
     }
}
