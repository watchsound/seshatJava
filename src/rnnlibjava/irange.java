package rnnlibjava;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
 

public class irange implements Iterable<Integer>{
	
	public static void main(String[] argv) {
		irange i = new irange(4,7);
		Iterator<Integer> it = i.iterator();
		it.next(); it.next();
		System.out.println(it.next());
		 if( it.next() != 7)
		    	throw new RuntimeException();
		 
		while( it.hasNext() ) {
			System.out.println(it.next());
		}
		int[] ia = new int[] {1,2,3,4,8};
	    i = indices(ia);
	    System.out.println( i.end );
	    if( i.end != 5)
	    	throw new RuntimeException();
	}
	
	 public static irange indices(Object r) {
    	 int size = Helpers.size(r);
    	 return new irange(0, size);
     }
	
	int start;
	int end;
	public irange(int start, int end) {
		this.start = start;
		this.end = end;
	}
	public int length() {
		return this.end - this.start ;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
            int curPos = start ;
			@Override
			public boolean hasNext() {
			 	return curPos <  end;
			}

			@Override
			public Integer next() { 
				return curPos++;
			}};
	}
	
	
}
 
