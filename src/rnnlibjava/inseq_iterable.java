package rnnlibjava;

import java.util.Iterator;

public class inseq_iterable implements Iterable{

	private Iterable one;
	private Iterable two;

	public inseq_iterable(Iterable  one, Iterable  two) {
		this.one  = one;
		this.two = two;
	}
	
	
	
	public Iterable getOne() {
		return one;
	}



	public void setOne(Iterable one) {
		this.one = one;
	}



	public Iterable getTwo() {
		return two;
	}



	public void setTwo(Iterable two) {
		this.two = two;
	}



	@Override
	public Iterator iterator() {
		return new Iterator() { 
            Iterator i1 = one.iterator();
            Iterator i2 = two.iterator();
			@Override
			public boolean hasNext() {
				return i1.hasNext() && i2.hasNext() ;
			}

			@Override
			public Object next() { 
				 if( i1.hasNext() )
					 return i1.next();
				 if( i2.hasNext() )
					 return i2.next();
				 return null;
			}};
	}

}
