package rnnlibjava;

import java.io.PrintStream;
import java.util.Vector;

import lang.ArrayListInt;
import lang.ResizableListI;
import lang.SimpleListI;
import rnnlibjava.Container.ViewInt;
import rnnlibjava.Container.ViewV;

public abstract class MultiArrayBase<T> implements SimpleListI<T>{

	public ResizableListI<Integer> shape = new ArrayListInt();
	public int[] strides = new int[0];
 
	//FIXME --- should not add this. for debug?????
	//protected boolean reshaped = false;
 
	public void print(PrintStream out) {
	}

	public abstract int size();

	public int num_dims() {
		return shape.length();
	}

	public abstract boolean empty(); 

	public void resize_data( ) {
       
		strides = Helpers.resize(strides, shape.length());
        if( strides.length == 0) {
        	//FIXME
        	return;
        }
		strides[strides.length - 1] = 1;
		for (int i = shape.length() - 2; i >= 0; --i) {
			strides[i] = strides[i + 1] * shapeAt(i + 1);
		}
	}

	public void reshape(int[] newShape) {
		assert (newShape.length > 0);
		shape = (ResizableListI<Integer>) new ArrayListInt(newShape);
		;
		resize_data( );
	}

	public void reshape(int[] newShape, T fill) {
		reshape(newShape);
		this.fill_data(fill);
		 
	}
	public abstract void  fill_data(T fill);


	public void reshape(ResizableListI<Integer> newShape) {
		assert (newShape.length() > 0);
		shape = newShape;
		resize_data( );
	}
	public void reshape(ResizableListI<Integer> newShape, T fillVal) {
		reshape(newShape);
		this.fill_data(fillVal);
	}
	public boolean in_range(int[] coords) {
		if (coords.length > shape.length()) {
			return false;
		}
		for (int shapeIt = 0, coordIt = 0; coordIt < coords.length; coordIt++, shapeIt++) {
			int c = coords[coordIt];
			if (c < 0 || c >= shapeAt(shapeIt))
				return false;
		}
		return true;
	}

	public int offset(int[] coords) {
		return (int) Helpers.inner_product(coords, this.strides);
	}

	public int shapeAt(int pos) {
		return (Integer) shape.at(pos);
	}

	public int shape_back() {
		if( this.shape.length() == 0)
			return 0;//FIXME
		return this.shapeAt(this.shape.length() - 1);
	}

	public abstract T getDefaultFill();
}
