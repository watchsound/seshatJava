package seshat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

public class Stroke {
	
	public static class Punto{
		  float x,y;

		  Punto(float vx, float vy) {
		    x = vx;
		    y = vy;
		  }
		  Punto(Punto copy) {
			    x = copy.x;
			    y = copy.y;
			  }

		  Punto() {}
		};
	public static boolean esNum(char c){
			  return (c >= '0' && c <= '9') || c=='-' || c=='.';
	 }

		
		Punto[]  pseq;
		  int NP;
		  int id; //InkML information

		 public   int rx, ry, rs, rt;
		 public int cx, cy; //Centroid
		  
		 public Stroke(int np) {
			  NP = np;
			  pseq = new Punto[NP];

			  cx = cy = 0;
			  rx = ry =  Integer.MAX_VALUE;
			  rs = rt = -Integer.MAX_VALUE;
			  for(int i=0; i<NP; i++)
			    pseq[i].x = pseq[i].y = -1;
		 }
		 public Stroke(int np, Scanner br)  {
			 NP = np;
			  pseq = new Punto[NP];

			  rx = ry =  Integer.MAX_VALUE;
			  rs = rt = -Integer.MAX_VALUE;
			  
			  for(int i=0; i<NP; i++) { 
				  pseq[i] = new Punto();
			      pseq[i].x = Float.parseFloat( br.next() );
			      pseq[i].y = Float.parseFloat( br.next() ); 
			     if( pseq[i].x < rx ) rx = (int) pseq[i].x;
			     if( pseq[i].y < ry ) ry = (int) pseq[i].y;
			     if( pseq[i].x > rs ) rs = (int) pseq[i].x;
			     if( pseq[i].y > rt ) rt = (int) pseq[i].y;
			    
			  }
			  
		 }
		 
		 public Stroke(int np, File fd) throws  Exception {
			 NP = np;
			  pseq = new Punto[NP];

			  rx = ry =  Integer.MAX_VALUE;
			  rs = rt = -Integer.MAX_VALUE;
			  
			  BufferedReader  br=new BufferedReader(new FileReader( fd ));
			   String str = null; 
			   int i = 0;
			   while((str = br.readLine()) != null){ 
			      String[] f = str.split(" ");
			      pseq[i].x = Float.parseFloat( f[0] );
			      pseq[i].y = Float.parseFloat( f[1] ); 
			      pseq[i] = new Punto();
			     if( pseq[i].x < rx ) rx = (int) pseq[i].x;
			     if( pseq[i].y < ry ) ry = (int) pseq[i].y;
			     if( pseq[i].x > rs ) rs = (int) pseq[i].x;
			     if( pseq[i].y > rt ) rt = (int) pseq[i].y;
			     i++;
			  }
			   br.close();
		 }
		 public  Stroke(File fd) {
			 
		 }
		 public  Stroke(char[] str, int inkml_id) {
			 char[] aux = new char[512];
			  int iaux;

			  id = inkml_id;
			  
			  Vector<Punto> data = new Vector<Punto>();

			  //Remove broken lines
			  int length = str.length;
			  for(int i=0; i < length; i++) {
			    if( str[i] == '\n' ) {
			      for(int j=i; j < length ; j++)
				      str[j] = str[j+1];
			      length --;
			    }
			  }

			  for(int i=0; i < length; i++) {

			    while( i < length && !esNum(str[i]) ) i++;
			    
			   // if( !str[i] ) break;

			    float px=0, py=0;

			    for(iaux=0; i<length && esNum(str[i]); iaux++, i++)
			          aux[iaux] = str[i];
			    aux[iaux] = 0;

			 //   if( !str[i] ) break;

			  //  px=atof(aux);
			    px =  Float.parseFloat( Util.toString(aux).trim());

			    while( i < length && !esNum(str[i]) ) i++;
			    
			  //  if( !str[i] ) break;

			    for(iaux=0;  i < length && esNum(str[i]); iaux++, i++)
			          aux[iaux] = str[i];
			    aux[iaux] = 0;

			  //  py=atof(aux);
			    py =  Float.parseFloat( Util.toString(aux).trim());
			    
			    while(i < length && str[i] != ',' ) i++;
			    i--;

			    data.add(new Punto(px,py));
			  }
			  
			  NP = (int)data.size();
			  pseq = new Punto[NP];
			  for(int i=0; i<NP; i++) {
			    set(i, data.get(i)); 
			  }
		 }
		 public void destructor() {}
	 

		 public void set(int idx, Punto p) {
			  pseq[idx].x = p.x;
			  pseq[idx].y = p.y;

			  if( pseq[idx].x < rx ) rx = (int) pseq[idx].x;
			  if( pseq[idx].y < ry ) ry = (int) pseq[idx].y;
			  if( pseq[idx].x > rs ) rs = (int) pseq[idx].x;
			  if( pseq[idx].y > rt ) rt = (int) pseq[idx].y;
		 }
		 public  Punto get(int idx) {
			  return pseq[idx];
		 }
		 public int getNpuntos() {
			  return NP;
		 }
		 public int getId() {
			  return id;
		 }
		 public  void print() {
			  System.out.print("STROKE - "+  NP  +" points\n" );
			  for(int i=0; i<NP; i++)
				  System.out.print(" (" +pseq[i].x+ "," +  pseq[i].y + ")" );
			  System.out.print("\n");
		 }

		 public  float min_dist(Stroke st) {
			  float mind = Float.MAX_VALUE;
			  for(int i=0; i<NP; i++) {
			    for(int j=0; j<st.getNpuntos(); j++) {
			      Punto  p = st.get(j);

			      float d = (pseq[i].x - p.x)*(pseq[i].x - p.x)
				+ (pseq[i].y - p.y)*(pseq[i].y - p.y);

			      if( d < mind ) mind=d;
			    }
			  }

			  return (float) Math.sqrt( mind );
		 }
}
