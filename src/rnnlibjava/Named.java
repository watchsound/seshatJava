package rnnlibjava;

import java.io.PrintStream;

public interface Named {

	String getName();
	void setName(String name);
	void print();
	void print(PrintStream out);
}
