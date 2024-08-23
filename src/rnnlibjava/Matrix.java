package rnnlibjava;

import java.util.Vector;

import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;

public class Matrix {

	public static final boolean OP_TRACKING = false;
	
	public static int matrixOps = 0;
	
	// M += a * b
	public static void outer(
	    float[] a , float[] M,  float[] b ) {
	   int m = 0;
	   for(int i = 0; i < b.length; i++) {
		   float input = b[i];
		   for(int j = 0 ; j < a.length; j++, m++) {
			   M[m] += a[j] * input;
		   }
	   } 
	}
	public static void outer(
		    SimpleListI<Float> a,   SimpleListI<Float> M, SimpleListI<Float> b ) { 
		 int m = 0;
		 for(int i = 0; i < b.length(); i++) {
			   float input = b.at(i);
			   for(int j = 0 ; j < a.length(); j++, m++) {
				   M.set(m, M.at(m ) + a.at(j) * input);
			   }
		   } 
	}
	// out += M in
	public static void dot(
	    float[] in,   float[] M, float[] out ) {
        int m = 0;
		for(int o = 0; o < out.length; o++) {
			float sum = 0;
			for(int i = 0 ; i < in.length; i++,m++) {
				sum += M[m] * in[i];
			}
			out[o] += sum;
		} 
	 
	}
	public static void dot(
		    SimpleListI<Float> in,   SimpleListI<Float> M, SimpleListI<Float> out ) { 
		    int m = 0;
			for(int o = 0; o < out.size(); o++) {
				float sum = 0;
				for(int i = 0 ; i < in.size(); i++,m++) {
					sum += M.at(m) * in.at(i);
				}
				try {
				out.set(o, out.at(o) + sum); 
				}catch(Exception e) {
					System.out.println(e);
				}
			}  
		}

	// out += transpose(M) in
	public static void dot_transpose(
	      float[] in,  float[] M, float[] out) {
      int m = 0;
	  for(int i = 0; i < in.length; i++) {
		  float input = in[i];
		  for(int o = 0 ; o < out.length; o++,m++) {
			  out[o] += M[m] * input;
		  }
	  } 
 
	}
	public static void dot_transpose(
		      SimpleListI<Float> in,  SimpleListI<Float> M, SimpleListI<Float> out) {
		  int m = 0;
		  for(int i = 0; i < in.length(); i++) {
			  float input = in.at(i);
			  for(int o = 0 ; o < out.length(); o++,m++) {
				  out.set(o,  out.at(o) + M.at(m) * input);
			  }
		  } 
	 
		}
	// out += transpose(M^2) in
	public static void dot_transpose_m_squared(
	    float[] in,  float[] M, float[] out ) {
		int m = 0;
		  for(int i = 0; i < in.length; i++) {
			  float input = in[i];
			  for(int o = 0 ; o < out.length; o++,m++) {
				  out[o] += M[m] * M[m] * input;
			  }
		  }  
	}

	// M += a^2 * b
	public static void outer_a_squared(
	     float[] a , float[] M,   float[] b  ) {
		int m = 0;
		  for(int bi = 0; bi < b.length; bi++) {
			  float input = b[bi];
			  for(int ai = 0 ; ai < a.length; ai++,m++) {
				  M[m] += a[ai]*a[ai] * input;
			  }
		  }   
	}
 
	///
	// M += a * b
	public static void outer(
	    int[] a , int[] M,  int[] b ) {
		int m = 0;
	   for(int i = 0; i < b.length; i++) {
		   int input = b[i];
		   for(int j = 0 ; j < a.length; j++, m++) {
			   M[m] += a[j] * input;
		   }
	   } 
	}
	
	// out += M in
	public static void dot(
			int[] in,   int[] M, int[] out ) {
		int m = 0;
		for(int o = 0; o < out.length; o++) {
			int sum = 0;
			for(int i = 0 ; i < in.length; i++,m++) {
				sum += M[m] * in[i];
			}
			out[o] += sum;
		} 
	 
	}

	// out += transpose(M) in
	public static void dot_transpose(
			int[] in,  int[] M, int[] out) {
		int m = 0;
	  for(int i = 0; i < in.length; i++) {
		  int input = in[i];
		  for(int o = 0 ; o < out.length; o++,m++) {
			  out[o] += M[m] * input;
		  }
	  } 
 
	}

	// out += transpose(M^2) in
	public static void dot_transpose_m_squared(
			int[] in,  int[] M, int[] out ) {
		int m = 0;
		  for(int i = 0; i < in.length; i++) {
			  int input = in[i];
			  for(int o = 0 ; o < out.length; o++,m++) {
				  out[o] += M[m] * M[m] * input;
			  }
		  }  
	}

	// M += a^2 * b
	public static void outer_a_squared(
			int[] a , int[] M,   int[] b  ) {
		int m = 0;
		  for(int bi = 0; bi < b.length; bi++) {
			  int input = b[bi];
			  for(int ai = 0 ; ai < a.length; ai++,m++) {
				  M[m] += a[ai]*a[ai] * input;
			  }
		  }   
	}
	
	///////////
	// M += a * b
		public static void outer(
		    Vector<Float> a , float[] M,  Vector<Float> b ) {
			int m = 0;
		   for(int i = 0; i < b.size(); i++) {
			   float input = b.get(i);
			   for(int j = 0 ; j < a.size(); j++, m++) {
				   M[m] += a.get(j) * input;
			   }
		   } 
		}
		
		// out += M in
		public static void dot(
				 Vector<Float> in,   float[] M,  Vector<Float> out ) {
			int m = 0;
			for(int o = 0; o < out.size(); o++) {
				float sum = 0;
				for(int i = 0 ; i < in.size(); i++,m++) {
					sum += M[m] * in.get(i);
				}
				Float f = out.get(o);
				if ( f == null ) f = 0f;
				out.set(o, f + sum);
				 
			} 
		 
		}

		// out += transpose(M) in
		public static void dot_transpose(
				 Vector<Float> in,  float[] M,  Vector<Float> out) {
			int m = 0;
		  for(int i = 0; i < in.size(); i++) {
			  float input = in.get(i);
			  for(int o = 0 ; o < out.size(); o++,m++) { 
				    Float f = out.get(o);
					if ( f == null ) f = 0f;
					out.set(o, f + M[m] * input);
					
			  }
		  } 
	 
		}

		// out += transpose(M^2) in
		public static void dot_transpose_m_squared(
				 Vector<Float> in,  float[] M,  Vector<Float> out ) {
			int m = 0;
			  for(int i = 0; i < in.size(); i++) {
				  float input = in.get(i);
				  for(int o = 0 ; o < out.size(); o++,m++) {
					    Float f = out.get(o);
						if ( f == null ) f = 0f;
						out.set(o, f+ M[m] * M[m] * input);
						
				  }
			  }  
		}

		// M += a^2 * b
		public static void outer_a_squared(
				 Vector<Float> a , float[] M,   Vector<Float> b  ) {
			int m = 0;
			  for(int bi = 0; bi < b.size(); bi++) {
				  float input = b.get( bi );
				  for(int ai = 0 ; ai < a.size(); ai++,m++) {
					  M[m] += a.get(ai)*a.get(ai) * input;
				  }
			  }   
		}
	
//	public static void dot(const R& a, const float[] M, const R& b) {
//	  dot(boost::begin(a), boost::end(a), M, boost::begin(b), boost::end(b));
//	}
//
//	template<class R> static void dot_transpose(
//	    const R& a, const float[] M, const R& b) {
//	  dot_transpose(
//	      boost::begin(a), boost::end(a), M, boost::begin(b), boost::end(b));
//	}

//	template<class R> static void outer_a_squared(
//	    const R& a, float[] M, const R&b) {
//	  outer_a_squared(
//	      boost::begin(a), boost::end(a), M, boost::begin(b), boost::end(b));
//	}
//
//	template<class R> static void dot_transpose_m_squared(
//	    const R& a, const float[] M, const R& b) {
//	  dot_transpose_m_squared(
//	      boost::begin(a), boost::end(a), M, boost::begin(b), boost::end(b));
//	}
//
	public static float  elt(ViewFloat M, int x, int y, int width) {
	  return M.at( (y*width) + x );
	} 
	public static  void  elt(ViewFloat M, int x, int y, int width,  float newValue) {
		   M.set( (y*width) + x , newValue);
	 } 
}
