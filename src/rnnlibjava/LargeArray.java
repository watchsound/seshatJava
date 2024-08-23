package rnnlibjava;

import java.util.ArrayList;
import java.util.List;

public class LargeArray {
    public static final int MAX_SIZE = Integer.MAX_VALUE - 6;
    
    
    List<float[]> dataList = new ArrayList<>();
    
    public LargeArray() {
    	this(new float[0]);
    }
    public LargeArray(float[] data) {
    	dataList.add(data);
    }
    
    public LargeArray(long size) {
    	int count = (int) (size / MAX_SIZE) ;
    	if(count == 0 ) {
    		float[] data = new float[(int)size];
    		dataList.add(data);
    		return;
    	} 
    	
    	for(int i = 0; i <= count; i++) {
    		float[] data = new float[MAX_SIZE];
    		dataList.add(data);
    	}
    	int remain = (int) (size % MAX_SIZE);
    	if( remain > 0) {
    		float[] data = new float[remain];
    		dataList.add(data);
    	}
    }
    
    public float at(long index) {
    	 int count = (int) (index / MAX_SIZE) ;
    	 int remain = (int) (index % MAX_SIZE);
    	 return dataList.get(count)[(int)remain];  
    }
    public void set(long index, float value) {
    	 int count = (int) (index / MAX_SIZE) ;
    	 int remain = (int) (index % MAX_SIZE);
    	 dataList.get(count)[remain] = value;
    }
    public void add(float value) {
    	float[] row = dataList.remove(dataList.size()-1);
    	if( row.length < MAX_SIZE -1) {
    		row = Helpers.resize(row, row.length+1, value);
    		dataList.add(row);
    	} else {
    		float[] arow = new float[0];
    		arow[0] = value;
    		dataList.add(arow);
    	}
    }
    
    
}
