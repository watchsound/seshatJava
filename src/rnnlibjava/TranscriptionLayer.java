package rnnlibjava;

import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import lang.ArrayListObj;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewList;
import rnnlibjava.Container.ViewLog;
import rnnlibjava.Pair.PairInt;

public class TranscriptionLayer extends SoftmaxLayer//, public NetworkOutput
{	
	
	public static ArrayListObj<String> make_target_labels(SimpleListI<String> labs)
	{
		 ArrayListObj<String> temp = new  ArrayListObj<String>();
		 for(int i = 0; i < labs.size(); i++)
			 temp.add(labs.at(i));
		 temp.add("blank");
		return temp;
	}
	
	//data
	PrintStream out;
	SeqBuffer_log forwardVariables;
	SeqBuffer_log backwardVariables;
	int blank;
	int totalSegments;
	int totalTime;
	ResizableListI<Log> dEdYTerms;
	ArrayListObj<Integer>  outputLabelSeq;
	boolean confusionMatrix;
	
	//functions
	  public TranscriptionLayer(PrintStream o, String name, SimpleListI<String> labs, 
	    		WeightContainer  wc, DataExportHandler  deh ) {
		  this(o, name, labs, wc, deh, false);
	  }
	  
    public TranscriptionLayer(PrintStream o, String name, SimpleListI<String> labs, 
    		WeightContainer  wc, DataExportHandler  deh, boolean cm  ) {
	    super(name, 1, make_target_labels(labs), wc, deh);
		this.out = o;
		this.blank = targetLabels.indexOf("blank");
		this.dEdYTerms = new ArrayListObj<>();
		for(int i = 0; i < output_size(); i++) {
			this.dEdYTerms.add(null);
		}
		this.confusionMatrix = cm;
 
		if( this.networkOutput == null ) {
			this.networkOutput = new  ClassificationLayer(this, out, this.targetLabels);  
		}
		this.networkOutput.criteria = new Vector<String>();
		this.networkOutput.criteria.add( "ctcError");
		this.networkOutput.criteria.add( "labelError");
		this.networkOutput.criteria.add( "sequenceError"); 
		display(forwardVariables, "forwardVariables",  targetLabels);
		display(backwardVariables, "backwardVariables", targetLabels);
	}	
 
	public PairInt segment_range(int time)   
	{
		 return segment_range(time, -1);
	}
	public PairInt segment_range(int time, int totalSegs )   
	{
		if (totalSegs < 0)
		{
			totalSegs = totalSegments;
		}
		int start = Math.max(0, totalSegs - (2 *(totalTime-time)));
		int end = Math.min(totalSegs, 2 * (time + 1));
		return new PairInt(start, end);
	}
	public ArrayListObj<Integer> path_to_string(int[] path)  
	{
		ArrayListObj<Integer> str = new ArrayListObj<Integer>();
		 
		int prevLabel = -1;
		for (int   i = 0; i < path.length; i++)
		{
			int label = path[i];
			if (label != blank && (str.isEmpty()  || label != str.get(str.size()-1) || prevLabel == blank))
			{
				str .add(label );  
			}
			prevLabel = label;
		}
		return str;
	}
	public ArrayListObj<Integer> best_label_seq()  
	{
		int[] path = new int[ outputActivations.seq_size() ]; 
		for(int i =0; i < outputActivations.seq_size(); i++)
		{
			path[i] = Helpers.arg_max(outputActivations.atCoord(i)) ;
		}
//		ArrayListObj<Integer> path = new ArrayListObj<Integer>();
//		for(int i =0; i < outputActivations.seq_size(); i++)
//		{
//			path .add( Helpers.arg_max(outputActivations.at(i)) );
//		}
		return path_to_string(path);
	}
	public Log prior_label_prob(int label)
	{
		return Log.logDefault;
	}
	public float calculate_errors(  DataSequence  seq)
	{
		totalTime = outputActivations.seq_size();
		int requiredTime = seq.targetLabelSeq.size();
		int oldLabel = -1;
		for(int label : seq.targetLabelSeq)
		{
			if (label == oldLabel)
			{
				++requiredTime;
			}
			oldLabel = label;
		}
		if (totalTime < requiredTime)
		{
			out.println(  "warning, seq " + seq.tag + " has requiredTime " + requiredTime + " > totalTime " + totalTime  );
			return 0;
		}		
		totalSegments = (seq.targetLabelSeq.size() * 2) + 1;
		
		//calculate the forward variables
		forwardVariables.reshape_with_depth( Helpers.list_of(totalTime, 0), totalSegments, Log.logDefault);
		forwardVariables.set(0,   logActivations.at(blank));
		if (totalSegments > 1)
		{
			forwardVariables.set(1,   logActivations.at( seq.targetLabelSeq.get(0) ) );
		}
		for(int t = 1; t < totalTime; t++ )
		{
			ViewLog logActs = logActivations.atCoord(t);
			ViewLog oldFvars = forwardVariables.atCoord(t-1);
			ViewLog fvars = forwardVariables.atCoord(t);
			PairInt ss = segment_range(t);
			for(int s = ss.first; s < ss.second; s++) 
			{
				Log  fv;
				//s odd (label output)
				if ( (s & 1) != 0)
				{
					int labelIndex = s/2;
					int labelNum = seq.targetLabelSeq.at( labelIndex );
					fv = oldFvars.at(s).add(  oldFvars.at(s-1) );
					if (s > 1 && (labelNum != seq.targetLabelSeq.at(labelIndex-1)))
					{
						fv  = fv.add(  oldFvars.at(s-2) );
					}
					fv  = fv.times(  logActs.at(labelNum).times( prior_label_prob(labelIndex) ) );
				}
				//s even (blank output)
				else
				{
					fv = oldFvars.at(s);
					if (s != 0)
					{
						fv = fv.add(  oldFvars.at(s-1) );
					}
					fv  =  fv.times( logActs.at(blank) );
				}
				fvars.set(s, fv)  ;
			}
		}
		ViewLog lastFvs = forwardVariables.atCoord(totalTime - 1);
		Log logProb = lastFvs.at(lastFvs.size()-1);
		if (totalSegments > 1)
		{
			logProb = logProb.add( Helpers.nth_last(lastFvs, 2) );
		}
		//check(logProb.log() <= 0, "sequence\n" + str(seq) + "has log probability " + str(logProb.log()));

		//calculate the backward variables
		backwardVariables.reshape_with_depth(Helpers.list_of(totalTime, 0), totalSegments, new Log());
		ViewLog lastBvs = backwardVariables.atCoord(totalTime - 1);
		lastBvs.set( lastBvs.size()-1, Log.logDefault) ;
		if (totalSegments > 1)
		{
			lastBvs.set( lastBvs.size()-2, Log.logDefault) ;
		}
		//LOOP over time, calculating backward variables recursively
		for(int t = totalTime - 1 -1 ; t >= 0; t--)
		{
			ViewLog oldLogActs = logActivations.atCoord(t+1);
			ViewLog oldBvars = backwardVariables.atCoord(t+1);
			ViewLog bvars = backwardVariables.atCoord(t); 
			PairInt  ss = segment_range(t);
			for(int s = ss.first; s <  ss.second; s ++ )
			{
				Log  bv;
				
				//s odd (label output)
				if ( (s&1) != 0 )
				{
					int labelIndex = s/2;
					int labelNum = seq.targetLabelSeq.at(labelIndex);
					bv = (oldBvars.at(s) .times( ( oldLogActs.at(labelNum) ) .times(  prior_label_prob(labelIndex)) )
							.add(    (oldBvars.at(s+1).times(  oldLogActs.at(blank)) ) ) );
					if (s < (totalSegments-2))
					{
						int nextLabelNum = seq.targetLabelSeq.at(labelIndex + 1);
						if (labelNum != nextLabelNum)
						{
							bv = bv.add(  (oldBvars.at(s+2) .times( 
									oldLogActs.at(nextLabelNum) ).times( 
											prior_label_prob(labelIndex + 1)) ));
						}
					}
				}
				
				//s even (blank output)
				else
				{
					bv = oldBvars.at(s).times(  oldLogActs.at(blank));
					if (s < (totalSegments-1))
					{
						bv = bv .add(   (oldBvars.at(s+1).times(  oldLogActs.at( seq.targetLabelSeq.at(s/2) ) )
								.times(prior_label_prob(s/2))) );
					}
				}
				bvars.set(s, bv) ;
			}
		}
		//inject the training errors
		for(int time = 0 ; time < totalTime; time++)
		{
			Helpers.fill(dEdYTerms, new Log(0, false));
			ViewLog fvars = forwardVariables.atCoord(time);
			ViewLog bvars = backwardVariables.atCoord(time);
			for (int s = 0; s <= totalSegments; s++)
			{
				//k = blank for even s, target label for odd s
				int k = (s&1) != 0 ? seq.targetLabelSeq.at(s/2) : blank;
				dEdYTerms.set(k, dEdYTerms.at(k).add  (fvars.at(s).times(   bvars.at(s) )) );
			}
			//LOOP(TDLL t, zip(outputErrors[time], dEdYTerms, logActivations[time]))
			ViewFloat  oo1 = outputErrors.atCoord(time);
			ViewLog  oo3 = logActivations.atCoord(time);
			for(int t = 0; t< oo1.size() && t < dEdYTerms.size() && t < oo3.size(); t++ )
			{
				oo1.set(t,   -((dEdYTerms.at(t).divide( (logProb.times( oo3.at(t) ) ))).exp())   );
			}
				 
		}
		//calculate the aligment errors
		outputLabelSeq = best_label_seq();
		StringAlignment  alignment = new StringAlignment(seq.targetLabelSeq, outputLabelSeq, Helpers.verbose);
		float labelError = alignment.distance;
		float substitutions = alignment.substitutions;
		float deletions = alignment.deletions;
		float insertions = alignment.insertions;
		float seqError = labelError != 0 ? 1 : 0;
		float ctcError = -logProb.log();
		
		//store errors in map
		float normFactor = seq.targetLabelSeq.size(); 
		this.networkOutput.normFactors.put( "labelError" ,normFactor);
		this.networkOutput.normFactors.put( "substitutions", normFactor);
		this.networkOutput.normFactors.put("deletions",normFactor);
		this.networkOutput.normFactors.put("insertions",normFactor);
		this.networkOutput.errorMap.clear();
		this.networkOutput. ERR("networkOutput",labelError);
		this.networkOutput. ERR("seqError",seqError);
		this.networkOutput. ERR("substitutions",substitutions);
		this.networkOutput. ERR("deletions",deletions);
		this.networkOutput. ERR("insertions",insertions);
		this.networkOutput. ERR("ctcError",ctcError); 
		 
		if (confusionMatrix)
		{
			for (Entry<Integer, Integer>  en : alignment.delsMap.entrySet())
			{
				Float v = this.networkOutput. errorMap.get( "_" + targetLabels.at(en.getKey()) + "_deletions" );
				if( v == null )  v = 0f;
				 this.networkOutput. errorMap.put( "_" + targetLabels.at(en.getKey()) + "_deletions" , v + en.getValue() /normFactor   );
			}
			for (Entry<Integer, Integer>  en : alignment.insMap.entrySet())
			{
				Float v = this.networkOutput. errorMap.get( "_" + targetLabels.at(en.getKey()) + "_insertions" );
				if( v == null )  v = 0f;
				 this.networkOutput. errorMap.put( "_" + targetLabels.at(en.getKey()) + "_insertions" , v + en.getValue() /normFactor   );
			}	 
			
			for (Entry<Integer, Map<Integer, Integer>>  p : alignment.subsMap.entrySet()) 
			{
				int refIndex = p.getKey();
				Float v = this.networkOutput. errorMap.get( "_" + targetLabels.at(refIndex) + "_substitutions" );
				if( v == null )  v = 0f;
				 this.networkOutput. errorMap.put( "_" + targetLabels.at(refIndex) + "_substitutions" , v + Helpers.sum_right( p.getValue()) /normFactor   );
				 
				for(Entry<Integer, Integer> p2 : p.getValue().entrySet()) 
				{
					Float v2 = this.networkOutput. errorMap.get( "_" + targetLabels.at(refIndex) + "->" + targetLabels.at(p2.getKey()) );
					if( v2 == null )  v2 = 0f;
					 this.networkOutput. errorMap.put( "_" + targetLabels.at(refIndex) + "->" + targetLabels.at(p2.getKey()) , 
							                                       v + p2.getValue() /normFactor   ); 
				} 
			}	
		}
		if (Helpers.verbose)
		{
			out.println( "target label sequence (length " + seq.targetLabelSeq.size() + "):" );
			out.println(  DataSequence.label_seq_to_str(seq.targetLabelSeq, targetLabels)  );
			out.println( "output label sequence (length " + outputLabelSeq.size() + "):" );
			out.println(  DataSequence.label_seq_to_str(outputLabelSeq, targetLabels) );
		}
		return ctcError;
	}

}
