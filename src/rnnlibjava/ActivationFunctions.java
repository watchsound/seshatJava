package rnnlibjava;

public class ActivationFunctions {
	public static interface FN{
		float fn(float v);
		float deriv(float v);
		float second_deriv(float v);
	}
	public static int sign(float v) {
		if( v == 0 ) return 0;
		return v < 0 ? -1 : 1;
	}
	// Implement logistic function
	// f(x) = 1 / (1 + exp(-x))
	public static class Logistic implements FN{
	   public   float fn(float x) {
	    if (x < Log.expLimit) {
	      if (x > -Log.expLimit) {
	        return (float) (1.0 / (1.0 + Math.exp(-x)));
	      }
	      return 0;
	    }
	    return 1;
	  }
	  public   float deriv(float y) {
	    return (float) (y*(1.0-y));
	  }
	  public   float second_deriv(float y) {
	    return deriv(y) * (1 - (2 * y));
	  }
	};
	// Implements a soft version of the sign function with
	// first and second derivatives.
	// f(x) = x / (1 + |x|)
	public static class   Softsign  implements FN{
	  public float   fn(float x) {
	    if (x < Float.MAX_VALUE) {
	      if (x > -Float.MAX_VALUE) {
	        return (x/(1 + Math.abs(x)));
	      }
	      return -1;
	    }
	    return 1;
	  }
	  public   float deriv(float y) {
	    return  (1 - Math.abs(y)) * (1 - Math.abs(y));
	  }
	  public   float second_deriv(float y) {
	    return (float) (-2 *  sign(y) * Math.pow((1 - Math.abs(y)), 3));
	  }
	};
	// Identity activation function
	// f(n)   = x
	// f'(n)  = 1
	// f''(n) = 0
	public static class Identity  implements FN{
	   public float fn(float x) {
	    return x;
	  }
	   public float deriv(float y) {
	    return 1;
	  }
	   public float second_deriv(float y) {
	    return 0;
	  }
	};
	// Logistic unit in the range [-2, 2]
	public static class Maxmin2  implements FN{
	  public   float fn(float x) {
	    return (4 * new Logistic().fn(x)) - 2;
	  }
	  public   float deriv(float y) {
	    if (y == -2 || y == 2) {
	      return 0;
	    }
	    return (float) ((4 - (y * y)) / 4.0);
	  }
	  public   float second_deriv(float y) {
	    return (float) (-deriv(y) * 0.5 * y);
	  }
	};
	// Logistic unit in the range [-1, 1]
	public static class   Maxmin1  implements FN{
	  public   float fn(float x) {
	    return (2 * new Logistic().fn(x)) - 1;
	  }
	 public    float deriv(float y) {
	    if (y == -1 || y == 1) {
	      return 0;
	    }
	    return (float) ((1.0 - (y * y)) / 2.0);
	  }
	 public   float second_deriv(float y) {
	    return -deriv(y) * y;
	  }
	};
	// Logistic unit in the range [0, 2]
	public static class Max2min0  implements FN{
		public    float fn(float x) {
	    return (2 * new Logistic().fn(x));
	  }
		public    float deriv(float y) {
	    if (y == -1 || y == 1) {
	      return 0;
	    }
	    return (float) (y * (1 - (y / 2.0)));
	  }
		public    float second_deriv(float y) {
	    return deriv(y) * (1 - y);
	  }
	};
	public static class   Tanh  implements FN{
	  public float fn(float x) {
	    return new Maxmin1().fn(2*x);
	  }
	  public float deriv(float y) {
	    return (float) (1.0 - (y *  y));
	  }
	  public float second_deriv(float y) {
	    return -2 * deriv(y) * y;
	  }
	};

}
