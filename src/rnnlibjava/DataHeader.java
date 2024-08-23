package rnnlibjava;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lang.ArrayListObj;

public class DataHeader {
	//data
	public int numDims;
	public  ArrayListObj<String> inputLabels = new ArrayListObj<String>();
	public Map<String, Integer> inputLabelCounts = new HashMap<String,Integer>();
	public  ArrayListObj<String> targetLabels = new ArrayListObj<String>();
	public Map<String, Integer>  targetLabelCounts = new HashMap<String,Integer>();
	public int inputSize;
	public int outputSize = 0;
	public int numSequences;
	public int numTimesteps = 0;
	public int totalTargetStringLength =0;

	//functions
	public   DataHeader()   {}
}
