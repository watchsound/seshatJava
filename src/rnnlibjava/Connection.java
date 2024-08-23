package rnnlibjava;

import java.io.PrintStream;

import lang.ArrayListFloat;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;

public class Connection {
	//data
		Layer  from;
		Layer  to;
		String name;
		//name
		public Connection(String name, Layer   f, Layer  t) { 
			this.name = name;
			from = f;
			to  = t; 
		}
	 
		public int num_weights()   {return 0;}
		public void feed_forward(int[]coords){}
		public void feed_back(int[] coords){}
		public void update_derivs(int[] coords){}
		public void print(PrintStream out)  {}
		public   ViewList<Float> weights(){
			return new ViewList<Float>(new ArrayListFloat());
		}
}
