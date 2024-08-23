package lang;

import java.util.ArrayList;

import rnnlibjava.Helpers;

public class ArrayListObj<T> extends ArrayList<T> implements ResizableListI<T>{
 
	private static final long serialVersionUID = 1L;

	public static<T> ArrayListObj<T> create(int size, T fill) {
		ArrayListObj<T> obj = new ArrayListObj<T>(size);
		for(int i = 0; i < size; i++) {
			obj.add(fill);
		}
		return obj;
	}

	public ArrayListObj(){
		
	}
    public ArrayListObj(int initialCapacity){
		super(initialCapacity);
	}
    public ArrayListObj(T[] data){
		super(data.length);
		for(T t : data) {
			add(t);
		}
	}
    public ArrayListObj<T> copy(){
    	if( this.isEmpty() )
    		return new ArrayListObj<T>();
    	T t = get(0);
    	if( t == null )
    		return  new ArrayListObj<T>();
    	if( t instanceof String) {
    		 ArrayListObj<String> c1 = new ArrayListObj<String>( super.toArray(Helpers.getArray(String.class,0)) ); 
    		 return ( ArrayListObj<T>)c1;
    	}
    	 ArrayListObj<T> c1 = new ArrayListObj<T>( super.toArray(Helpers.getArray(t,0)) ); 
		 return c1;
	 }
	public int length() {
		return size();
	}
    
	
	@Override
	public T at(int i) {
		 return super.get(i);
	}

	@Override
	public T removeLast() {
		if( this.isEmpty() ) return null;
	 	return this.remove(size()-1);
	} 
}
