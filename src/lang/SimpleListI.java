package lang;

public interface SimpleListI<V>{
	int size();
	V at(int i );
	V set(int i, V v); 
	
	int length();
	
	
	
	 SimpleListI<V> copy();
}