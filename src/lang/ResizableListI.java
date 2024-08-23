package lang;

public interface ResizableListI<T> extends SimpleListI<T>{ 
		boolean add(T v);
		T removeLast();
}
