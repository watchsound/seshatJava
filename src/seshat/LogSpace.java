package seshat;

import java.util.List;

public class LogSpace {
	  int N;
	  int RX, RY;
	  CellCYK[]  data;

	  void quicksort(CellCYK[] vec, int ini, int fin) {
		  if( ini < fin ) {
			    int piv = partition(vec, ini, fin);
			    quicksort(vec, ini,   piv);
			    quicksort(vec, piv+1, fin);
			  }
	  }
	  int  partition(CellCYK [] vec, int ini, int fin) {
		  int piv = vec[ini].x;
		  int i=ini-1, j=fin+1;

		  do{
		    do{
		      j--;
		    }while(vec[j].x > piv);
		    do{
		      i++;
		    }while(vec[i].x < piv);

		    if( i<j ) {
		      CellCYK  aux = vec[i];
		      vec[i] = vec[j];
		      vec[j] = aux;
		    }
		  }while( i<j );

		  return j;
	  }
	  void bsearch(int sx, int sy, int ss, int st, List<CellCYK>  set) {
		  //Binary search of "sx"
		  int i,j;
		  for(i=0, j=N; i<j; ) {
		    int m=(i+j)/2;

		    if( sx <= data[m].x )
		      j=m;
		    else
		      i=m+1;
		  }

		  //Retrieve the compatible regions
		  while( i<N && data[i].x <= ss ) {
		    if( data[i].y <= st && data[i].t >= sy ) {
		      set.add(data[i]);
		    }
		    i++;
		  }
	  }
	  void bsearchStv(int sx, int sy, int ss, int st, List<CellCYK> set, boolean U_V, CellCYK  cd) {

		  //Binary search of "sx"
		  int i,j;
		  for(i=0, j=N; i<j; ) {
		    int m=(i+j)/2;

		    if( sx <= data[m].x )
		      j=m;
		    else
		      i=m+1;
		  }

		  //Retrieve the compatible regions
		  if( U_V ) { //Direction 'Up' (U)
		    while( i<N && data[i].x <= ss ) {
		      if( data[i].t <= st && data[i].t >= sy && data[i].s <= ss ) {
			if( data[i].t < cd.y )
			  sy = Math.max(Math.max(data[i].y, data[i].t-RY),sy);
			set.add(data[i]);
		      }
		      i++;
		    }
		  }
		  else { //Direction 'Down' (V)
		    while( i<N && data[i].x <= ss ) {
		      if( data[i].y <= st && data[i].y >= sy && data[i].s <= ss ) {
			if( data[i].y > cd.t )
			  st = Math.min(Math.min(data[i].t, data[i].y+RY),st);
			set.add(data[i]);
		      }
		      i++;
		    }
		  }
	  }
	  void bsearchHBP(int sx, int sy, int ss, int st, List<CellCYK> set, CellCYK cd) {
		  //Binary search of "sx"
		  int i,j;
		  for(i=0, j=N; i<j; ) {
		    int m=(i+j)/2;

		    if( sx <= data[m].x )
		      j=m;
		    else
		      i=m+1;
		  }

		  //Retrieve the compatible regions
		  while( i<N && data[i].x <= ss ) {
		    if( data[i].y <= st && data[i].t >= sy ) {
		      if( data[i].x > cd.s )
			ss = Math.min(Math.min(data[i].s, data[i].x+RX),ss);
		      set.add(data[i]);
		    }
		    i++;
		  }
	  }

	 public LogSpace(CellCYK  c, int nr, int dx, int dy) {
		  //List length
		  N=nr;
		  //Size of the "reference symbol"
		  RX = dx;
		  RY = dy;

		  //Create a vector to store the regions
		  data = new CellCYK[N];
		  int i=0;
		  for(CellCYK r=c; r != null; r=r.sig)
		    data[i++] = r;

		  //Sort the regions
		  quicksort(data, 0, N-1);
	 }
	 public void destructor() {}

	 public void getH(CellCYK  c, List<CellCYK> set) {
		 int sx, sy, ss, st;

		  //Set the region to search
		  sx = Math.max(c.x+1, c.s-(int)(RX*2));  // (sx,sy)------
		  ss = c.s + RX*8;                    //  ------------
		  sy = c.y - RY;                      //  ------------
		  st = c.t + RY;                      //  ------(ss,st)

		  //Retrieve the regions
		  bsearchHBP(sx, sy, ss, st, set, c);
	 }
	 public  void getV(CellCYK c, List<CellCYK> set){
		 int sx, sy, ss, st;

		  //Set the region to search
		  sx = c.x - 2*RX;
		  ss = c.s + 2*RX;
		  sy = Math.max(c.t - RY, c.y+1);
		  st = c.t + RY*3;

		  //Retrieve the regions
		  bsearchStv(sx, sy, ss, st, set, false, c);
	 }
	 public void getU(CellCYK c, List<CellCYK> set){
		 int sx, sy, ss, st;

		  //Set the region to search
		  sx = c.x - 2*RX;
		  ss = c.s + 2*RX;
		  sy = c.y - RY*3;
		  st = Math.min(c.y + RY, c.t-1);

		  //Retrieve the regions
		  bsearchStv(sx, sy, ss, st, set, true, c);
	 }
	 public  void getI(CellCYK c, List<CellCYK> set){
		 int sx, sy, ss, st;

		  //Set the region to search
		  sx = c.x + 1;  // (sx,sy)------
		  ss = c.s + RX; //  ------------
		  sy = c.y + 1;  //  ------------
		  st = c.t + RY; //  ------(ss,st)

		  //Retrieve the regions
		  bsearch(sx, sy, ss, st, set);
	 }
	 public void getM(CellCYK c, List<CellCYK> set){
		 int sx, sy, ss, st;

		  //Set the region to search
		  sx = c.x - 2*RX;            // (sx,sy)------
		  ss = Math.min(c.x + 2*RX, c.s); //  ------------
		  sy = c.y - RY;              //  ------------
		  st = Math.min(c.y + 2*RY, c.t); //  ------(ss,st)

		  //Retrieve the regions
		  bsearch(sx, sy, ss, st, set);
	 }
	 public void getS(CellCYK c, List<CellCYK> set){
		  int sx, sy, ss, st;

		  //Set the region to search
		  sx = c.x-1;      // (sx,sy)------
		  ss = c.x+1;      //  ------------
		  sy = c.y - RY;   //  ------------
		  st = c.t + RY;   //  ------(ss,st)

		  bsearch(sx, sy, ss, st, set);
	 }
}
