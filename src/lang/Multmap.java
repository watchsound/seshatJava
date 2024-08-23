package lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

import rnnlibjava.Connection;
import rnnlibjava.Layer;

public class Multmap {
	 Map<Layer, List<Connection>> data = new HashMap<>();
	 
	 public void put(Layer layer, Connection con) {
		 List<Connection> list = data.get(layer);
		 if( list == null ) {
			 list = new ArrayList<>();
			 data.put(layer, list);
		 }
		 list.add(con);
	 }
	 public void remove(Layer layer, Connection con) {
		 List<Connection> list = data.get(layer);
		 if( list != null ) {
			 list.remove(con);
		 }
	 }
	 public void remove(Layer layer) {
		 data.remove(layer);
	 }
	 public  Map<Layer, List<Connection>> getData(){
		 return data;
	 }
	 public List<Connection> get(Layer layer){
		 return data.get(layer);
	 }
	 
}
