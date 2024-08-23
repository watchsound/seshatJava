package rnnlibjava;

import java.io.PrintStream;
import java.util.Vector;

import lang.ArrayListObj;
import rnnlibjava.Container.ViewFloat;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.DataExporter.DataExportHandler;

public class ClassificationLayer extends NetworkOutput{
	
	public static Layer  make_classification_layer(PrintStream out, String name,
			int numSeqDims, ArrayListObj<String> labels, WeightContainer  weight, DataExportHandler deh)
	{
		assert(labels.size() >= 2);
		if (labels.size() == 2)
		{
		  return new  BinaryClassificationLayer(out, name, numSeqDims, labels, weight, deh);
		}
		return new MulticlassClassificationLayer(out, name, numSeqDims, labels, weight, deh);
	}
	
	//data
	PrintStream out;
	ArrayListObj<String> labels;
	SeqBuffer_int targets;
	int[][] confusionMatrix;
	int[] numErrorsByClass;
	int[] numTargetsByClass;
	int[] outputs = new int[0];
	Layer layer;
//	//functions
	public ClassificationLayer(Layer layer, PrintStream o, ArrayListObj<String> labs) { 
		this.layer = layer;
		this.out = o;
		this.labels = labs.copy();
		this.targets = new SeqBuffer_int(labels.size());
		this.confusionMatrix = new int[labels.size()][]; 
		numErrorsByClass = new int[labels.size()];
		numTargetsByClass = new int[labels.size()];
		  
	    for(int i = 0; i < confusionMatrix.length; i++) {
	    	confusionMatrix[i] = new int[labels.size()];
	    }
		 
		criteria.add("crossEntropyError") ; 
		criteria.add("classificationError") ; 
		 
	}
	public  int output_class(int pt) {
		return layer.output_class(pt);
	}
	public float class_prob(int pt, int index) {
		return layer.class_prob(pt, index);
	}
	public float set_error(int pt, int targetClass) {
		return layer.set_error(pt, targetClass);
	}
	public float calculate_errors( DataSequence  seq)
	{
		for(int i = 0; i < confusionMatrix.length; i++) {
			Helpers.fill(confusionMatrix[i], 0);
		}
	 
		outputs = new int[0];
		targets.reshape(seq.targetClasses.seq_shape(), 0 );
		float crossEntropyError = 0;
		for(int pt = 0; pt < seq.targetClasses.seq_size(); pt++) {
		 
			int outputClass = output_class(pt);
		  
			outputs = Helpers.append(outputs, outputClass);
			// outputs = Helpers.resize(outputs, outputs.length+ outputClass);
			 
			int targetClass = seq.targetClasses.atCoord(pt).at(0);
			if (targetClass >= 0)
			{
				ViewInt targs = targets.atCoord(pt);	
				targs.set( targetClass,  1);
				crossEntropyError -= set_error(pt, targetClass);
				++confusionMatrix[targetClass][outputClass];
			}
		}
		errorMap.clear();
		
		for(int i = 0; i < confusionMatrix.length; i++)  
		{
			int[] v = confusionMatrix[i];
			numTargetsByClass[i] = (int)Helpers.sum(v);
			numErrorsByClass[i] = numTargetsByClass[i] - v[i];
		}
		float numTargets = Helpers.sum(numTargetsByClass);
		if (numTargets != 0)
		{
			errorMap.put( "crossEntropyError", crossEntropyError);
			errorMap.put("classificationError",  Helpers.sum(numErrorsByClass) / numTargets);
			for(int i = 0; i < confusionMatrix.length; i++) 
			{
				if (numTargetsByClass[i] != 0)
				{
					errorMap.put("_" + labels.get(i) , numErrorsByClass[i] / numTargets);
					if(Helpers.verbose && (confusionMatrix.length > 2))
					{
						int[] v = confusionMatrix[i];
						for(int j = 0; j < v.length; j++)
						{
							if (j != i && v[j] != 0)
							{
								errorMap.put("_" + labels.get(i) + "->" + labels.get(j) , v[j] / numTargets);
							}
						}
					}
				}
			}
		}
		return crossEntropyError;
	} 
	
	public static class MulticlassClassificationLayer extends SoftmaxLayer 
	{
		//functions
	 
	  public MulticlassClassificationLayer(PrintStream out, String name, int numSeqDims, 
			  ArrayListObj<String> labels, WeightContainer  wc, DataExportHandler  deh) {
		    super(name, numSeqDims, labels, wc, deh);
		    networkOutput = new ClassificationLayer(this, out, labels);  
	  }
	  public	int output_class(int pt)  
		{
			return Helpers.arg_max( outputActivations.atCoord(pt));
		}
		public float class_prob(int pt, int index)  
		{
			return Math.max(Float.MIN_VALUE,  outputActivations.atCoord(pt).at(index) );
		}
		public float set_error(int pt, int targetClass)
		{
			float targetProb = class_prob(pt, targetClass);
			ViewFloat errs  =  outputErrors.atCoord(pt);
			errs.set( targetClass, - (1/targetProb));
			return (float) Math.log(targetProb);
		}
//		public float calculate_errors( DataSequence  seq) {
//			return networkOutput.calculate_errors(seq);
//		}
	};
	
	public static class BinaryClassificationLayer extends   NeuronLayer 
	{
	    
		public BinaryClassificationLayer(PrintStream out, String name, int numSeqDims,  
				ArrayListObj<String> labels, WeightContainer  weight, DataExportHandler  deh) { 
		 	super(name, numSeqDims, 1, weight, deh, new ActivationFunctions.Logistic());
		 	networkOutput = new ClassificationLayer(this, out, labels);  
			//display(targets, "targets", labels);
		}
		public int output_class(int pt)  
		{
			return (outputActivations.atCoord(pt).at(0) > 0.5 ? 1 : 0);
		}
		public float class_prob(int pt, int index)  
		{
			float act = Math.max(Float.MIN_VALUE, outputActivations.atCoord(pt).at(0));
			return (index == 1 ? act : 1-act);
		}
		public float set_error(int pt, int targetClass)
		{
			float targetProb = class_prob(pt, targetClass);
			outputErrors.atCoord(pt).set(0, (targetClass != 0? -(1/targetProb) : (1/targetProb))); 
			return (float) Math.log(targetProb);
		}
//		public float calculate_errors( DataSequence  seq) {
//			return networkOutput.calculate_errors(seq);
//		}
	};
}
