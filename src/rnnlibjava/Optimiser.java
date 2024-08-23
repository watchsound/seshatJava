package rnnlibjava;

import java.io.PrintStream;

public interface Optimiser {
	    void update_weights() ;
	    void print(PrintStream out) ;
	    void build();
	    
	     
}