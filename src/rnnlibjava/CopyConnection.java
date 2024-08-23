package rnnlibjava;

import java.io.PrintStream;

public class CopyConnection extends Connection{

	//functions
	 public  CopyConnection(Layer  f, Layer  t, WeightContainer weight) {
		 super(	f.name  + "_to_" + t.name, f, t);
	     this.to.source = this.from;
			weight.link_layers(this.from.name, this.to.name);
		}
		 
		public void feed_forward(int[] coords)
		{
			Helpers.range_plus_equals(this.to.inputActivations.atCoord(coords) , this.from.outputActivations.atCoord(coords));
		}
		public void feed_back(int[] coords)
		{
			Helpers.range_plus_equals(this.from.outputErrors.atCoord(coords), this.to.inputErrors.atCoord(coords));
		}
		public void print(PrintStream out)  
		{
			super.print(out);
			out.print(  " (copy)" );
		}
}
