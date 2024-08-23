package rnnlibjava;

public class MathOpI{
	
	public static interface MathOp1<T> {
	    T op(T a);
	}
	public static interface MathOp1i  {
	    int op(int a);
	}
	public static interface MathOp1f  {
	    float op(float a);
	}
	public static interface MathOp1c  {
		char op(char a);
	}
	public static interface MathOp1b<T> {
	    boolean op(T a);
	}
	public static interface MathOp2<T> {
	    T op(T a, T b);
	}
	public static interface MathOp2i  {
	    int op(int a, int b);
	}
	public static interface MathOp2f  {
		float op(float a, float b);
	}
	public static interface MathOp2b<T> {
	    boolean op(T a, T b);
	}
	public static interface MathOp3<T> {
	    T op(T a, T b, T c);
	}
	public static interface MathOp3b<T> {
	    boolean op(T a, T b, T c);
	}
	
}