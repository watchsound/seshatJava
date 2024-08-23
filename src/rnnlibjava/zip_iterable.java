package rnnlibjava;

import java.util.Iterator;

public class zip_iterable implements Iterable{

	private Iterable[] its;  
	public zip_iterable(Iterable...  its ) {
		this.its  = its;  
	}
	
	 
 

	@Override
	public Iterator iterator() {
		return new myIterator() ;
	}
	private class myIterator implements Iterator{
		 Iterator[] i1 = new Iterator[its.length];
		 myIterator(){
			  for(int i = 0; i < i1.length; i++) {
	             i1[i] = its[i].iterator();	
	          }
		}

		@Override
		public boolean hasNext() {
			for(int i = 0; i < i1.length; i++) {
				if(! i1[i].hasNext() )
					return false;
			}
			return true;
		}

		@Override
		public Object next() {
			for(int i = 0; i < i1.length; i++) {
				if( i1[i].hasNext() )
					return i1[i].next();
			}
			return null;
		}
		
	}

}
