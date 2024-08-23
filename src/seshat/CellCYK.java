package seshat;

import seshat.TableCYK.coo;

public class CellCYK implements Comparable<CellCYK>{
	  //Bounding box spatial region coordinates
	 public int x,y; //top-left
	 public int s,t; //bottom-right

	  //Hypotheses for every non-terminals
	 public  int nnt;
	 public  Hypothesis[]  noterm;

	  //Strokes covered in this cell
	public  int nc;
	public boolean[] ccc;
	public  int talla; //total number of strokes

	  //Next cell in linked list (CYK table of same size)
	public  CellCYK sig;


	  //Methods
	public  CellCYK(int n, int ncc) {
		  sig = null;
		  nnt = n;
		  nc = ncc;
		  talla = 0;

		  //Create (empty) hypotheses
		  noterm = new Hypothesis [nnt];
		  for(int i=0; i<nnt; i++)
		    noterm[i] = null;

		  //Create (empty) strokes covered
		  ccc = new boolean[nc];
		  for(int i=0; i<nc; i++)
		    ccc[i] = false;
	}
	public void destructor() {
		
	}

	@Override
	public int compareTo(CellCYK C) {
		      if( x < C.x )
			    return -1;
			  if( x == C.x ) {
			    if( y < C.y )
			      return -1;
			    if( y == C.y ) {
			      if( s < C.s )
				      return -1;
			      if( s == C.s ) {
				     if( t < C.t )
				         return -1;
				     if( t == C.t )
				         return 0;
				     return 1;
			      }
			      return 1;
			    }
			    return 1;
			  }
			  return 1; 
	}
	public  void ccUnion(CellCYK  A, CellCYK B) {
		  for(int i=0; i<nc; i++)
			    ccc[i] = ( A.ccc[i] || B.ccc[i] ) ? true : false;
	}
	public  boolean ccEqual(CellCYK H) {
		  if( talla != H.talla )
			    return false;
			  
			  for(int i=0; i<nc; i++)
			    if( ccc[i] != H.ccc[i] )
			      return false;
			  
			  return true;
	}
	public boolean compatible(CellCYK H) {
		  for(int i=0; i<nc; i++)
			    if( ccc[i] && H.ccc[i] )
			      return false;
			  
		  return true;
	}
}
