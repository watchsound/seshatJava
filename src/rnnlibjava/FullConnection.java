package rnnlibjava;

import java.io.PrintStream;

import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Pair.PairInt;

public class FullConnection extends Connection{

	
	//data
		int[]   delay;
		int[] delayedCoords;
		FullConnection  source;
		PairInt paramRange;
	    WeightContainer  wc;

		//functions
	    public FullConnection(Layer  f, Layer  t, WeightContainer  weight ) {
	    	this(f, t, weight, new int[0], null);
	    }
	    public FullConnection(Layer  f, Layer  t, WeightContainer  weight, int[] d ) {
	    	this(f, t, weight,d , null);
	    }
	  public FullConnection(Layer  f, Layer  t, WeightContainer  weight,  int[] d, FullConnection  s  ) {
			super("", f, t);
			this.name =  make_name(f, t, d);
			this.source = s;
			//paramRange(source ? source.paramRange : wc.new_parameters(this.from.output_size() * this.to.input_size(), this.from.name, this.to.name, name))     
	 
	     wc = weight;
	    if (source != null)
	      {
		      paramRange = source.paramRange;
		     wc.link_layers(this.from.name, this.to.name, this.name, paramRange.first, paramRange.second);
	      }
	    else
	      paramRange = wc.new_parameters(this.from.output_size() * this.to.input_size(), this.from.name, this.to.name, name);
	      set_delay(d);
	   // assert(num_weights() == (this.from.output_size() * this.to.input_size()));
	    if (!this.from.name.equals("bias") && this.from != this.to &&  this.to.source == null)
	      {
		    this.to.source = this.from;
	      }
	  }
		 
		public void set_delay(int[] d)
		{
			delay = Helpers.clone(d); //FIXME =?
			//assert(delay.size() == 0 || delay.size() == this.from.num_seq_dims());
			delayedCoords = Helpers.resize(delayedCoords, delay.length);
		 
		}
		public String  make_name(Layer f, Layer  t,   int[] d)
		{
			String name;
			name = f.name + "_to_" + t.name;
			for(int i = 0; i < d.length; i++) {
				
			}
			if (Helpers.isNotEmpty(d))
			{
				StringBuilder temp = new StringBuilder();
				temp.append( "_delay_" );
				temp.append( d[0] );
				for(int i = 1; i < d.length; i++) {
					temp.append("_" + d[i]);
				}
				 
				name += temp.toString();
			}
			return name;
		}
		public  ViewList<Float> weights()
		{
			return wc.get_weights(paramRange);
		}
		public  ViewList<Float>  derivs()
		{
			return wc.get_derivs(paramRange);
		}
		public int num_weights()  
		{
			return Helpers.difference(paramRange);
		}
		public int[] add_delay( int[] toCoords)
		{
			if (delay.length == 0)
			{
				return  toCoords;
			}
			Helpers.range_plus(delayedCoords, toCoords, delay);
			if (this.from.outputActivations.in_range(delayedCoords))
			{
				return  delayedCoords;
			}
			return new int[0];
		}
		public void feed_forward( int[] toCoords)
		{
			int[] fromCoords = add_delay(toCoords);
			if (fromCoords != null && fromCoords.length > 0 )
			{
				Matrix.dot(this.from.out_acts( fromCoords), weights().begin(), this.to.inputActivations.atCoord(toCoords));			
			}
		}
		public void feed_back( int[]  toCoords)
		{
			 int[] fromCoords = add_delay(toCoords);
			 if (fromCoords != null && fromCoords.length > 0 ) 
			{
				 Matrix.dot_transpose(this.to.inputErrors.atCoord(toCoords), weights().begin(), this.from.out_errs( fromCoords));
			}
		}
		public void update_derivs( int[] toCoords)
		{
			  int[] fromCoords = add_delay(toCoords);
			if (fromCoords != null && fromCoords.length > 0 ) 
			{
				Matrix.outer(this.from.out_acts( fromCoords), derivs().begin(), this.to.inputErrors.atCoord(toCoords));
			}
		}
		public void print(PrintStream  out)  
		{
			out.print(name);
			out.print(  " (" + num_weights() + " wts");
			if (source != null )
			{
				out.print(  " shared with " + source.name );
			}
			out.print( ")" );
		}
		
		
}
