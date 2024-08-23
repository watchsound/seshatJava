package rnnlibjava;

import java.io.PrintStream;
import java.util.Scanner;

import lang.ArrayListObj;
import lang.SimpleListI;

public class DataSequence {
	public static String label_seq_to_str(SimpleListI labelSeq, ArrayListObj<String> alphabet ) {
		return label_seq_to_str(labelSeq, alphabet, " ");
	}
	
	public static String label_seq_to_str(SimpleListI labelSeq, ArrayListObj<String> alphabet, String delim)
	{
		StringBuilder ss = new StringBuilder();
		for( int i = 0; i < labelSeq.size(); i++)   
		{
			if ( alphabet.contains( labelSeq.at(i) ))
			{
				ss.append(  alphabet.get(i) );
			}
			else
			{
				ss.append( "<NULL>" );
			}
			if (i != labelSeq.size()-1)
			{
				ss.append(delim);
			}
		}
		return ss.toString();
	}
	public static int[] str_to_label_seq(String labelSeqString, ArrayListObj<String> alphabet)
	{
		ArrayListObj<Integer> v = new ArrayListObj<>();
		Scanner ss = new Scanner(labelSeqString);  
		while(ss.hasNext())
		{
	/*		check(in_right(alphabet, lab), lab + " not found in alphabet");*/
//			if (warn_unless(in_right(alphabet, lab), lab + " not found in alphabet"))
			int i = alphabet.indexOf( ss.next() );
			if (i != alphabet.size())
			{
				v.add(i);
			}
		}
		return Helpers.v2i(v);
	}
	
	
	
	
	
	
	
	
	
	//data
	public SeqBuffer_float inputs;
	public SeqBuffer_int inputClasses;
	public SeqBuffer_float targetPatterns;
	public 	SeqBuffer_int targetClasses;
	public 	SeqBuffer_float importance;
	public 	ArrayListObj<Integer> targetLabelSeq;
		public 	ArrayListObj<String> targetWordSeq;
		public 	String tag;
		
		//functions
		public DataSequence( DataSequence ds) {
			inputs = ds.inputs;
			inputClasses = ds.inputClasses;
			targetPatterns = ds.targetPatterns;
			targetClasses = ds.targetClasses;
			importance = ds.importance;
			targetLabelSeq = ds.targetLabelSeq;
			tag =ds.tag;
		}
		
		public DataSequence(int inputDepth  ) {
			this(inputDepth, 0);
		}
		public DataSequence ()   {
			this(0, 0);
		}
		public DataSequence(int inputDepth  , int targetPattDepth  ) {
			inputs = new SeqBuffer_float(inputDepth);
			inputClasses = new SeqBuffer_int();
			targetPatterns = new SeqBuffer_float(targetPattDepth);
			targetClasses =  new SeqBuffer_int();;
			importance  =  new SeqBuffer_float();  
			targetLabelSeq = new ArrayListObj<>();
			targetWordSeq = new ArrayListObj<>();
			tag="";
		}
		public int num_timesteps()  
		{
			return inputs.seq_size();
		}	
		
		public void print(PrintStream out, ArrayListObj<String> targetLabels , ArrayListObj<String> inputLabels)
		{
			if( targetLabels == null ) targetLabels = new ArrayListObj<>();
			if( inputLabels == null ) inputLabels = new ArrayListObj<>();
			
			Helpers.PRINT(tag, out);
			out.println( "input shape = (" + inputs.shape + ")"  );
			out.println( "timesteps = " + inputs.seq_size() );
			if (targetLabelSeq.size() > 0 && targetLabels.size() > 0)
			{
				out.println( "target label sequence:" );
				out.println( label_seq_to_str(new Container.ViewList<Integer>(this.targetLabelSeq),  targetLabels) );
			}
			if (targetPatterns.size() >0 )
			{
				out.println( "target shape = (" + targetPatterns.shape +  ")" );
			}
			if (Helpers.verbose)
			{
				if(targetClasses.size() >0 && targetLabels.size() > 0 )
				{
					out.println( label_seq_to_str( new Container.ViewInt(targetClasses.data),  targetLabels) );
				}
				if(inputClasses.size() >0 && inputLabels.size() > 0)
				{
					out.println( label_seq_to_str(new Container.ViewInt(inputClasses.data), inputLabels) );
				}
			}
		}
	
	
	
}
