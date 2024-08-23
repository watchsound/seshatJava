package rnnlibjava;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class NetworkOutput {
	//data
	Map<String, Float> errorMap;
	Map<String, Float>  normFactors;
	Vector<String> criteria;
	
	//functions
	public NetworkOutput(){
		errorMap = new HashMap<>();
		normFactors = new HashMap<>();
		criteria = new Vector<>();
	}
	public float calculate_errors( DataSequence  seq){
		return Float.MAX_VALUE;
	}
	
	public void ERR(String name, float v) {
		errorMap.put(name,v);
	}
}
