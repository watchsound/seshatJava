package rnnlibjava;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class Log {
	  
	
	public  static  float expMax = Float.MAX_VALUE;
	public  static   float expMin = Float.MIN_VALUE;
	public  static   float expLimit = (float) Math.log( expMax );
	public  static   float logInfinity = (float) 1e100;
	public  static   float logZero = - logInfinity ;
	
	public static Log logDefault = new Log(0, false); 
	
	 //data
	  float expVal;
	  float logVal;
	  

	  //static functions
	  static float safe_exp(float x)
	  {
	    if (x == logZero)
	      {
		return 0;
	      }
	    if (x >= expLimit)
	      {
		return expMax;
	      }
	    return (float) Math.exp(x);
	  }
	  static float safe_log(float x)
	  {
	    if (x <= expMin)
	      {
		return logZero;
	      }
	    return (float) Math.log(x);
	  }
	  static float log_add(float x, float y)
	  {
	    if (x == logZero)
	      {
		return y;
	      }
	    if (y == logZero)
	      {
		return x;
	      }
	    if (x < y)
	      {
	    	return (float) (y + Math.log(1.0 + safe_exp(x - y)));
	      }
	    return (float) (x + Math.log(1.0 + safe_exp(y - x)));
	  }
	  static float log_subtract(float x, float y)
	  {
	    if (y == logZero)
	      {
		return x;
	      }
	    if (y >= x)
	      {
		return logZero;
	      }
	    return (float) (x + Math.log(1.0 - safe_exp(y - x)));
	  }
	  static float log_multiply(float x, float y)
	  {
	    if (x == logZero || y == logZero)
	      {
				return logZero;
	      }
	    return x + y;
	  }
	  static float log_divide(float x, float y)
	  {
	    if (x == logZero)
	      {
		return logZero;
	      }
	    if (y == logZero)
	      {
		return logInfinity;
	      }
	    return x - y;
	  }

	  //functions
	 public Log() {
		 this(0, false);
	 }
	 public  Log(float v, boolean logScale) {
		 this.expVal = logScale ? -1 : v;
		 this.logVal = logScale ? v : safe_log(v);
	 }
	 public  Log(float expVal, float logVal) {
		 this.expVal = expVal;
		 this.logVal = logVal;
	 }
	 
	  public Log  assign( Log l)
	  {
	    logVal = l.logVal;
	    expVal = l.expVal;
	    return this;
	  }
	 public Log add(Log l)
	  {
	    logVal = log_add(logVal, l.logVal);
	    expVal = -1;
	    return this;
	  }
	 public Log minus( Log l)
	  {
	    logVal = log_subtract(logVal, l.logVal);
	    expVal = -1;
	    return this;
	  }
	 public Log times( Log l)
	  {
	    logVal = log_multiply(logVal, l.logVal);
	    expVal = -1;
	    return this;
	  }
	  public Log divide(Log l)
	  {
	    logVal = log_divide(logVal, l.logVal);
	    expVal = -1;
	    return this;
	  }
	  float exp()
	  {
	    if (expVal < 0)
	      {
		expVal = safe_exp(logVal);
	      }
	    return expVal;
	  }
	  float log()  
	  {
	    return logVal;
	  }
	 
	//helper functions
	 public Log   plus(Log log1, Log log2)
	{
	  return new Log(log_add(log1.log(), log2.log()), true);
	}
	public Log minus(Log log1, Log log2)
	{
	  return new Log(log_subtract(log1.log(), log2.log()), true);
	}
	public Log times(Log log1, Log log2)
	{
	  return new Log(log_multiply(log1.log(), log2.log()), true);
	}
	public Log divide(Log log1, Log log2)
	{
	  return new Log(log_divide(log1.log(), log2.log()), true);
	}
	public boolean greater(Log log1, Log log2)
	{
	  return (log1.log() > log2.log());
	}
	public boolean lesser(Log log1, Log log2)
	{
	  return (log1.log() < log2.log());
	}
	public boolean equals(Log log1, Log log2)
	{
	  return (log1.log() == log2.log());
	}
	public boolean lesserOrEquals(Log log1, Log log2)
	{
	  return (log1.log() <= log2.log());
	}
	public boolean greaterOrEquals(Log log1, Log log2)
	{
	  return (log1.log() >= log2.log());
	
	}
	public OutputStream print(OutputStream out, Log l)   {
		try {
			out.write(new Float(l.log()).toString().getBytes());
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return out;
	}
	public Scanner read(Scanner in, Log l) {
		String t = in.next();
		float d = Float.parseFloat(t);
		Log log = new Log(d, true);
		l.assign(log);
		return in;
	}
 
  }
