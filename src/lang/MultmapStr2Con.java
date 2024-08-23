package lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rnnlibjava.Connection;
import rnnlibjava.Layer;
import rnnlibjava.WeightContainer.Con;

public class MultmapStr2Con {
	 Map<String, List<Con>> data = new HashMap<>();
	 
	 public void put(String layer, Con con) {
		 List<Con> list = data.get(layer);
		 if( list == null ) {
			 list = new ArrayList<>();
			 data.put(layer, list);
		 }
		 list.add(con);
	 }
	 public void remove(String layer, Con con) {
		 List<Con> list = data.get(layer);
		 if( list != null ) {
			 list.remove(con);
		 }
	 }
	 public Set<String> keys()	{
		 return data.keySet();
	 }
	 public void remove(String layer) {
		 data.remove(layer);
	 }
	 public  Map<String, List<Con>> getData(){
		 return data;
	 }
	 public List<Con> get(String layer){
		 return data.get(layer);
	 }
	 
}
