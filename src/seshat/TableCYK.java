package seshat;

import java.util.HashMap;
import java.util.Map;

public class TableCYK{
	 public static  class coo implements Comparable<coo>{
		  int x,y,s,t;

		  public coo() {
		    x = y = s = t = -1;
		  }

		  public coo(int a, int b, int c, int d) {
		    x=a; y=b; s=c; t=d;
		  }

		  public boolean equals(Object obj) {
			  if( obj instanceof coo) {
				  coo other = (coo)obj;
				  return x == other.x && y == other.y && s == other.s && t == other.t;
			  }
			  return false;
		  }
		  public int hashCode() {
			  return x + 7 * y + 13 * s + 23 * t;
		  }

		@Override
		public int compareTo(coo o) { 
			  coo A = this;
			  coo B = o;
			  if( A.x < B.x ) return -1;
			  if( A.x == B.x ) {
			    if( A.y < B.y ) return -1;
			    if( A.y == B.y ) {
			      if( A.s < B.s ) return -1;
			      if( A.s == B.s ) {
				     if( A.t < B.t ) return -1;
				     if( A.t == B.t ) return 0;
				     return 1;
			      }
			      return 1;
			    }
			    return 1;
			  }
			  return 1; 
		} 
    };
    public static boolean less(coo a, coo b) {
    	return a.compareTo(b) < 0;
    }
	 
    public CellCYK[]  T;
    public   Map<coo,CellCYK >[]  TS;
    public  int N, K;

	  //Hypothesis that accounts for the target (input) math expression
    public   Hypothesis  Target;

	  //Percentage of strokes covered by the most likely hypothesis (target)
    public   int pm_comps;
	 
	 public  TableCYK(int n, int k) {
		  N = n;
		  K = k;

		  //Target = NULL;
		  Target = new Hypothesis(-1, -Float.MAX_VALUE, null, -1);
		  pm_comps = 0 ;

		  T = new CellCYK[N];
		  for(int i=0; i<N; i++)
		    T[i] = null;

		  TS =(Map<TableCYK.coo,CellCYK>[]) new HashMap[N];
		  for(int i = 0; i < N; i++) {
			  TS[i] = new HashMap<>();
		  }
	 }
	 public void destructor() {
		 
	 }

	 public Hypothesis getMLH() {
		 return Target;
	 }
	 public   CellCYK  get(int n) {
		 return T[n-1];
	 }
	 public  int size(int n) {
		 return TS[n-1].size(); 
	 }
	 public   void updateTarget(coo  K, Hypothesis  H) {
		  int pcomps = 0;
		  
		  for(int i=0; i < H.parent.nc; i++)
		    if( H.parent.ccc[i] )
		      pcomps++;
		  
		  if( pcomps > pm_comps || (pcomps==pm_comps && H.pr > Target.pr) ) {
		    Target.copy( H );
		    pm_comps = pcomps;
		  }
	 }
	 public  void add(int n, CellCYK  celda, int noterm_id, boolean[] esinit) {
		 coo key = new coo(celda.x, celda.y, celda.s, celda.t);
		 CellCYK it=TS[n-1].get( key );

		  celda.talla = n;

		  if( it == null ) {
		    //Link as head of size  n
		    celda.sig = T[n-1];
		    T[n-1] = celda;
		    TS[n-1].put(key,  celda);

		    if( noterm_id >= 0 ) {
		      if( esinit[noterm_id] )
			updateTarget(key, celda.noterm[noterm_id] );
		    }
		    else {

		      for(int nt=0; nt<celda.nnt; nt++)
			if( celda.noterm[nt] != null && esinit[nt] )
			    updateTarget( key, celda.noterm[nt] ); 
		    }
		  }
		  else { //Maximize probability avoiding duplicates

		    int VA, VB;
		    if( noterm_id < 0 ) {
		      VA = 0;
		      VB = celda.nnt;
		    }
		    else {
		      VA = noterm_id;
		      VB = VA+1;
		    }
		    
		    CellCYK  r = it ;

		    if( !celda.ccEqual( r ) ) { 
		      //The cells cover the same region with a different set of strokes

		      float maxpr_c=-Float.MAX_VALUE;
		      for(int i=VA; i<VB; i++)
			  if( celda.noterm[i] != null && celda.noterm[i].pr > maxpr_c )
			      maxpr_c = (float) celda.noterm[i].pr;

		      float maxpr_r=-Float.MAX_VALUE;
		      for(int i=0; i<r.nnt; i++)
			    if( r.noterm[i] != null && r.noterm[i].pr > maxpr_r )
			       maxpr_r = (float) r.noterm[i].pr;

		      //If the new cell contains the most likely hypothesis, replace the hypotheses
		      if( maxpr_c > maxpr_r ) {

			//Copy the new set of strokes
			for(int i=0; i<celda.nc; i++)
			  r.ccc[i] = celda.ccc[i];

			//Replace the hypotheses for each non-terminal
			for(int i=0; i<celda.nnt; i++)
			  if( celda.noterm[i] != null) {

			    if( r.noterm[i] != null ) {
			      r.noterm[i].copy( celda.noterm[i] );
			      r.noterm[i].parent = r;
			      
			      if( esinit[i] )
				updateTarget( key, r.noterm[i] );
			    }
			    else{
			      r.noterm[i] = celda.noterm[i];
			      r.noterm[i].parent = r;
			      
			      //Set to NULL such that the "delete celda" doesn't delete the hypothesis
			      celda.noterm[i] = null;
			      
			      if( esinit[i] )
				    updateTarget( key, r.noterm[i] );
			      }

			  }
			  else if( r.noterm[i] != null ) {
			   // delete r.noterm[i];
			    r.noterm[i] = null;
			  }

		      }

		    //  delete celda;

		      //Finished
		      return;
		    }


		    for(int i=VA; i<VB; i++) {

		      if( celda.noterm[i] != null) {
			if( r.noterm[i] != null ) {

			  if( celda.noterm[i].pr > r.noterm[i].pr ) {
			    //Maximize probability (replace)
			    r.noterm[i].copy( celda.noterm[i] );
			    r.noterm[i].parent = r;

			    if( esinit[i] )
			      updateTarget( key, r.noterm[i] );
			  }

			}
			else {
			  r.noterm[i] = celda.noterm[i];
			  r.noterm[i].parent = r;

			  //Set to NULL such that the "delete celda" doesn't delete the hypothesis
			  celda.noterm[i] = null;
		      
			    if( esinit[i] )
			       updateTarget( key, r.noterm[i] );
			    }
		      }

		    }

		 //   delete celda;
	 }
	}
}

