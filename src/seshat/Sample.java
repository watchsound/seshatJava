package seshat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import seshat.Stroke.Punto;

public class Sample {

	public static final double PI =   3.14159265;
	public static class SegmentHyp{
	  List<Integer> stks = new ArrayList<Integer>();  //List of strokes

	  //Bounding box (online coordinates)
	  int rx, ry; //Top-left
	  int rs, rt; //Bottom-right

	  int cen;
	};
	public static boolean isRelation(char[] str) {
		  if( Util.strcmp(str, "Hor") == 0 ) return true;
		  if( Util.strcmp(str, "Sub") == 0  ) return true;
		  if( Util.strcmp(str, "Sup") == 0  ) return true;
		  if( Util.strcmp(str, "Ver") == 0  ) return true;
		  if( Util.strcmp(str, "Ins") == 0  ) return true;
		  if( Util.strcmp(str, "Mrt") == 0  ) return true;
		  return false;
		}
	
	 Vector<Stroke> dataon = new Vector<Stroke>();
	  float[][] stk_dis;

	  int[][] dataoff;
	  int[] X = new int[1];
	  int[] Y = new int[1];
	  int IMGxMIN, IMGyMIN, IMGxMAX, IMGyMAX;
	  int [][]pix_stk;

	  SymRec SR;

	  //Information to create the output InkML file
	  char[] outinkml, outdot;
	  String UItag;
	  int[] next_id = new int[1];

	  public void loadInkML(String str) {
		  
	  }
	  public void loadSCGInk(String str) {
		  File f = new File(str);
		  if( !f.exists()) {
			  System.err.println( "Error loading SCGInk file '" +   str + "'\n");
			  throw new RuntimeException( "Error loading SCGInk file '" +   str+ "'\n" );
		  }
		 
		  Scanner fd = null;
			try {
				fd = new Scanner(new FileReader(f));
			} catch (FileNotFoundException e) {
			   throw new RuntimeException(e);
			}

		  String token = fd.next();
		  if( !token.startsWith("SCG_INK")) {
			  System.err.println( "Error: input file format is not SCG_INK\n");
			  throw new RuntimeException(  "Error: input file format is not SCG_INK\n");
		  }
		 
		  token = fd.next();
		  int nstrokes = Integer.parseInt(token);
		
		   
		  
		  for(int i=0; i<nstrokes; i++) {
			  token = fd.next();
			  int npuntos = Integer.parseInt(token); 
		     dataon.add(new Stroke(npuntos, fd));
		  }

		  fd.close();
	  }
	  public void loadSCGInkByContent(String content) {
		  
		 
		  Scanner fd = null;
			try {
				fd = new Scanner( content );
			} catch ( Exception e) {
			   throw new RuntimeException(e);
			}

		  String token = fd.next();
		  if( !token.startsWith("SCG_INK")) {
			  System.err.println( "Error: input file format is not SCG_INK\n");
			  throw new RuntimeException(  "Error: input file format is not SCG_INK\n");
		  }
		 
		  token = fd.next();
		  int nstrokes = Integer.parseInt(token);
		
		   
		  
		  for(int i=0; i<nstrokes; i++) {
			  token = fd.next();
			  int npuntos = Integer.parseInt(token); 
		     dataon.add(new Stroke(npuntos, fd));
		  }

		  fd.close();
	  }
	 public void linea(int[][] img, Punto  pa, Punto  pb, int stkid) {
		  final float dl = 3.125e-3f;
		  int dx = (int)pb.x - (int)pa.x;
		  int dy = (int)pb.y - (int)pa.y;

		  for(float l=0.0f; l < 1.0; l += dl) {
		    int x = (int)pa.x + (int)(dx*l+0.5);
		    int y = (int)pa.y + (int)(dy*l+0.5);

		    for(int i=y-1; i<=y+1; i++)
		      for(int j=x-1; j<=x+1; j++) {
			img[i][j] = 0;
			if( stkid>=0 ) pix_stk[i][j] = stkid;
		      }
		  }
	 }
	 public void linea_pbm(ObjectWrapper<int[][]> img, Punto  pa, Punto  pb, int stkid) {
		  final float dl = 3.125e-3f;
		  int dx = (int)pb.x - (int)pa.x;
		  int dy = (int)pb.y - (int)pa.y;

		  for(float l=0.0f; l < 1.0; l += dl) {
		    int x = (int)pa.x + (int)(dx*l+0.5);
		    int y = (int)pa.y + (int)(dy*l+0.5);

		    img.obj[y][x] = 0;
		  }
	 }
	 public boolean not_visible(int si, int sj, Punto  pi, Punto  pj) {
		  Punto pa = new Punto();
		  Punto pb = new Punto();
		  //Coordinates in pixels of the rendered image
		  pa.x = 5 + (X[0]-10)*(float)(pi.x - IMGxMIN)/(IMGxMAX - IMGxMIN + 1);
		  pa.y = 5 + (Y[0]-10)*(float)(pi.y - IMGyMIN)/(IMGyMAX - IMGyMIN + 1);
		  pb.x = 5 + (X[0]-10)*(float)(pj.x - IMGxMIN)/(IMGxMAX - IMGxMIN + 1);
		  pb.y = 5 + (Y[0]-10)*(float)(pj.y - IMGyMIN)/(IMGyMAX - IMGyMIN + 1);

		  
		  final float dl = 3.125e-4f;
		  int dx = (int)pb.x - (int)pa.x;
		  int dy = (int)pb.y - (int)pa.y;

		  for(float l=0.0f; l < 1.0; l += dl) {
		    int x = (int)pa.x + (int)(dx*l+0.5);
		    int y = (int)pa.y + (int)(dy*l+0.5);

		    if( dataoff[y][x] == 0 && pix_stk[y][x] != si && pix_stk[y][x] != sj )
		      return true;
		  }
		  
		  return false; 
	 } 

	public  int RX, RY;
	public float INF_DIST;  //Infinite distance value (visibility)
	public  float NORMF;     //Normalization factor for distances

	public  int ox, oy, os, ot; //Online bounding box
	public  int bx, by, bs, bt; //Offline bounding box

	public  Sample(String in) {

		  RX = RY = 0;
		  outinkml = outdot = null;

		  //Read file extension
		  boolean isInkML = true;
		   char[] auxext = new char[7];
		   Util.strcpy(auxext, ".inkml", 0); 
		  for(int i= in.length()-1, j=0; i>=0 && j<6; i--, j++)
		    if( in.charAt(i) != auxext[5-j] ) {
		      isInkML = false;
		      break;
		    }

		  if( !isInkML )
		    loadSCGInk( in );
		  else
		    loadInkML( in );
		  
		  ox = oy =  Integer.MAX_VALUE;
		  os = ot = -Integer.MAX_VALUE;
		  for(int i=0; i<nStrokes(); i++) {
		    //Compute bouding box
		    if( dataon.get(i).rx < ox ) ox = dataon.get(i).rx;
		    if( dataon.get(i).ry < oy ) oy = dataon.get(i).ry;
		    if( dataon.get(i).rs > os ) os = dataon.get(i).rs;
		    if( dataon.get(i).rt > ot ) ot = dataon.get(i).rt;
		    
		    //Compute centroid
		    dataon.get(i).cx = dataon.get(i).cy = 0;
		    int np;
		    for(np=0; np < dataon.get(i).getNpuntos() ; np++) {
		      Punto  pto = dataon.get(i).get(np);
		      dataon.get(i).cx += pto.x;
		      dataon.get(i).cy += pto.y;
		    }
		    dataon.get(i).cx /= np;
		    dataon.get(i).cy /= np;
		  }

		  //Render image representation
		  dataoff = render( X,  Y);
	}
	
	public  Sample(String content, boolean flag) {

		  RX = RY = 0;
		  outinkml = outdot = null;

		  
		  loadSCGInkByContent( content ); 
		  
		  ox = oy =  Integer.MAX_VALUE;
		  os = ot = -Integer.MAX_VALUE;
		  for(int i=0; i<nStrokes(); i++) {
		    //Compute bouding box
		    if( dataon.get(i).rx < ox ) ox = dataon.get(i).rx;
		    if( dataon.get(i).ry < oy ) oy = dataon.get(i).ry;
		    if( dataon.get(i).rs > os ) os = dataon.get(i).rs;
		    if( dataon.get(i).rt > ot ) ot = dataon.get(i).rt;
		    
		    //Compute centroid
		    dataon.get(i).cx = dataon.get(i).cy = 0;
		    int np;
		    for(np=0; np < dataon.get(i).getNpuntos() ; np++) {
		      Punto  pto = dataon.get(i).get(np);
		      dataon.get(i).cx += pto.x;
		      dataon.get(i).cy += pto.y;
		    }
		    dataon.get(i).cx /= np;
		    dataon.get(i).cy /= np;
		  }

		  //Render image representation
		  dataoff = render( X,  Y);
	}
	
	public void destructor() {}

	public  int dimX() {
		return X[0];
	};
	public  int dimY() {
		return Y[0];
	};
	public  int nStrokes() {
		  return (int)dataon.size();
	};
	public  int get(int x, int y) {
		return dataoff[y][x];
	};
	public  Stroke  getStroke(int i) {
		return dataon.get(i);
	}

	public  void getCentroids(CellCYK cd, int[]  ce, int[] as, int[] ds) {
		  int regy = Integer.MAX_VALUE, regt=-Integer.MAX_VALUE, N=0;
		   ce[0] = 0;

		  for(int i=0; i<cd.nc; i++) 
		    if( cd.ccc[i] ) {

		      for(int j=0; j<dataon.get(i).getNpuntos(); j++) {
			Punto  p = dataon.get(i).get(j);
			
			if( dataon.get(i).ry < regy )
			  regy = dataon.get(i).ry;
			if( dataon.get(i).rt > regt )
			  regt = dataon.get(i).rt;
			
			 ce[0] += p.y;
			
			N++;
		      }
		      
		    }
		  
		  ce[0] /= N;
		   as[0] = ( ce[0]+regt)/2;
		   ds[0] = (regy+ ce[0])/2;
	};
	public  void getAVGstroke_size(float[] avgw, float[] avgh) {
		   avgw[0] =  avgh[0] = 0.0f;
		  for(int i=0; i<(int)dataon.size(); i++) {
		     avgw[0] += dataon.get(i).rs - dataon.get(i).rx;
		     avgh[0] += dataon.get(i).rt - dataon.get(i).ry;
		  }
		   avgw[0] /= (int)dataon.size();
		   avgh[0] /= (int)dataon.size();
	};

	public  void  detRefSymbol() {
		  Vector<Integer> vmedx = new Vector<Integer>();
		  Vector<Integer> vmedy = new Vector<Integer>();
		  int nregs=0, lAr;
		  float mAr=0;
		  RX=0; RY=0;

		  //Compute reference symbol for normalization
		  for(int i=0; i<nStrokes(); i++) {
		    int ancho = dataon.get(i).rs - dataon.get(i).rx + 1;
		    int alto  = dataon.get(i).rt - dataon.get(i).ry + 1;
		    float aspectratio = (float)ancho/alto;
		    int area = ancho*alto;

		    vmedx.add(ancho);
		    vmedy.add(alto);

		    mAr += area;
		    if( aspectratio >= 0.25 && aspectratio <= 4.0 ) {
		      RX += ancho;
		      RY += alto;
		      nregs++;
		    }
		  }

		  //Average area
		  mAr /= vmedx.size();
		  lAr = (int)(Math.sqrt(mAr)+0.5);
		  lAr *= 0.9;

		  if( nregs > 0 ) {
		    RX /= nregs;
		    RY /= nregs;
		  }
		  else {
		    for(int i=0; i<nStrokes(); i++) {
		      int ancho = dataon.get(i).rs - dataon.get(i).rx + 1;
		      int alto  = dataon.get(i).rt - dataon.get(i).ry + 1;
		      
		      RX += ancho;
		      RY += alto;
		      nregs++;
		    }
		    RX /= nregs;
		    RY /= nregs;
		  }

		  //Compute median
		  Collections.sort(vmedx);
		  Collections.sort(vmedy);
		//  sort(vmedx.begin(),vmedx.end());
		//  sort(vmedy.begin(),vmedy.end());

		  //Reference is the average of (mean,median,avg_area)
		  RX = (int) ((RX + vmedx.get(vmedx.size()/2) + lAr)/3.0);
		  RY = (int) ((RY + vmedy.get( vmedy.size()/2) + lAr)/3.0);

	};
	public  void  compute_strokes_distances(int rx, int ry) {
		//Create distances matrix NxN (strokes)
		  stk_dis = new float[nStrokes()][];
		  for(int i=0; i<nStrokes(); i++)
		    stk_dis[i] = new float[nStrokes()];

		  float aux_x = rx;
		  float aux_y = ry;
		  NORMF    = (float) Math.sqrt(aux_x*aux_x + aux_y*aux_y);
		  INF_DIST = Float.MAX_VALUE/NORMF;

		  //Compute distance among every stroke.
		  for(int i=0; i<nStrokes(); i++) {
		    stk_dis[i][i] = 0.0f;

		    for(int j=i+1; j<nStrokes(); j++) {
		      stk_dis[i][j] = stroke_distance( i, j )/NORMF;
		      stk_dis[j][i] = stk_dis[i][j];
		    }
		  }

		if( !Util.debug )
			return;
		
		  System.err.print( "===INI Strokes Dist LIST===\n");
		  for(int i=0; i<nStrokes(); i++) {
		    for(int j=0; j<nStrokes(); j++) {
		      if( i!=j && stk_dis[i][j] < INF_DIST )
			     System.err.print( i +  " . " + j + ": d=" + Util.format(stk_dis[i][j],2) + "\n");
		    }
		    System.err.print( "\n");
		  }
		  System.err.print( "===END Strokes Dist LIST===\n\n");
		  System.err.print( "===INI DISTANCE MATRIX===\n");
		  for(int i=0; i<nStrokes(); i++) {
		    for(int j=0; j<nStrokes(); j++) {
		      if( stk_dis[i][j] >= INF_DIST ) System.err.print( "   *  ");
		          else System.err.print( "  " + Util.format(stk_dis[i][j], 2) );
		    }
		    System.err.print( "\n");
		  }
		  System.err.print( "===END DISTANCE MATRIX===\n");
	};
	
	public  float stroke_distance(int si, int sj) {
		  Punto  pi,  pj,  min_i = null,  min_j = null;
		  float dmin = Float.MAX_VALUE;  

		  for(int npi=0; npi < dataon.get(si).getNpuntos() ; npi++) {
		    pi = dataon.get(si).get(npi);

		    for(int npj=0; npj < dataon.get(sj).getNpuntos() ; npj++) {
		      pj = dataon.get(sj).get(npj);

		      float dis = (pi.x - pj.x)*(pi.x - pj.x) + (pi.y - pj.y)*(pi.y - pj.y);
		      
		      if( dis < dmin ) {
			dmin = dis;
			min_i = pi;
			min_j = pj;
		      }
		    }
		  }

		  if( not_visible( si, sj, min_i, min_j ) )
		    dmin = Float.MAX_VALUE;

		  return (float) (dmin < Float.MAX_VALUE ? Math.sqrt(dmin) : Float.MAX_VALUE);
	};
	
	public float getDist(int si, int sj) {
		      if( si<0 || sj<0 || si>=nStrokes() || sj>=nStrokes() ) {
			    System.err.print( "ERROR: stroke id out of range in getDist(" + si + "," + sj + ")\n" );
			    throw new RuntimeException( "ERROR: stroke id out of range in getDist(" + si + "," + sj + ")\n" );
			  }
			  return stk_dis[si][sj];
	}
	public  void  get_close_strokes(int id, List<Integer> L, float dist_th) {

		  boolean[]  added = new boolean[id];
		  for(int i=0; i<id; i++)
		    added[i] = false;

		  //Add linked strokes with distance < dist_th
		  for(int i=0; i<id; i++) {
		    if( getDist(id, i) < dist_th ) {
		      L.add(i);
		      added[i] = true;
		    }
		  }

		  boolean updated=true;

		  //Add second degree distance < dist_th  
		  List<Integer> auxlist = new ArrayList<Integer>();
		  for(Integer it : L) {
		    for(int i=0; i<id; i++)
		      if( !added[i] && getDist( it, i) < dist_th ) {
			    auxlist.add(i);
			    added[i] = true;
		      }
		  }
		  for(Integer it : auxlist)
		    L.add( it );
	}
	

	public  float group_penalty(CellCYK  A, CellCYK  B) {
		  //Minimum or single-linkage clustering
		  float dmin = Float.MAX_VALUE;
		  for(int i=0; i<A.nc; i++)
		    if( A.ccc[ i ] ) {
		      for(int j=0; j<B.nc; j++)
			if( B.ccc[ j ] && j!=i && getDist(i,j) < dmin )
			  dmin = getDist(i,j);
		    }

		  return dmin;
	}
	public  boolean  visibility(List<Integer> strokes_list) {
		  Map<Integer,Boolean> visited = new HashMap<Integer, Boolean>();
		  for(Integer i : strokes_list) {
			  visited.put(i, false);
		  }
	 
		  Queue<Integer> Q = new LinkedBlockingQueue<Integer>();
		  Q.add(  strokes_list.get(0) );
		  visited.put(strokes_list.get(0), true);

		  while( !Q.isEmpty() ) {
		    int id = Q.peek(); Q.remove();

		    for(Integer it : strokes_list) {
		      if( id !=  it && !visited.get(it) && getDist(id,  it) < INF_DIST ) {
		    	  visited.put(it, true);
			     Q.add( it);
		      }
		    }
		  }
		  for(Integer it : strokes_list) {
			  if( !visited.get(it))
				  return false;
		  }
		 
		  return true;
	}

	public  void setSymRec( SymRec sr ) {
		 SR = sr;
	}

	public  void setRegion(CellCYK  c, int nStk) {
		  c.ccc[nStk] = true;

		  c.x = dataon.get(nStk).rx;
		  c.y = dataon.get(nStk).ry;
		  c.s = dataon.get(nStk).rs;
		  c.t = dataon.get(nStk).rt;
	}
	public  void setRegion(CellCYK  c, List<Integer>  LT) { 
		  c.x = c.y =  Integer.MAX_VALUE;
		  c.s = c.t = -Integer.MAX_VALUE;

		  
          for(Integer it : LT) {
		    c.ccc[ it] = true; 
		    if( dataon.get(it).rx < c.x )
		      c.x = dataon.get(it).rx;
		    if( dataon.get(it).ry < c.y )
		      c.y = dataon.get(it).ry;
		    if( dataon.get(it).rs > c.s )
		      c.s = dataon.get(it).rs;
		    if( dataon.get(it).rt > c.t )
		      c.t = dataon.get(it).rt;

		  }
	}
	
	public void setRegion(CellCYK  c, int[] v, int size) {
		  c.x = c.y =  Integer.MAX_VALUE;
		  c.s = c.t = -Integer.MAX_VALUE;

		  for(int i=0; i<size; i++) {

		    c.ccc[v[i]] = true;
		    
		    if( dataon.get(v[i]).rx < c.x )
		      c.x = dataon.get(v[i]).rx;
		    if( dataon.get(v[i]).ry < c.y )
		      c.y = dataon.get(v[i]).ry;
		    if( dataon.get(v[i]).rs > c.s )
		      c.s = dataon.get(v[i]).rs;
		    if( dataon.get(v[i]).rt > c.t )
		      c.t = dataon.get(v[i]).rt;

		  }

	}

	public  int[][] render(int[] pW, int[] pH){

		  int xMAX=-Integer.MAX_VALUE, yMAX=-Integer.MAX_VALUE, xMIN=Integer.MAX_VALUE, yMIN=Integer.MAX_VALUE;

		  for(int i=0; i < nStrokes(); i++) {
		    
		    for(int np=0; np < dataon.get(i).getNpuntos() ; np++) {
		      Punto  pto = dataon.get(i).get(np);

		      if( pto.x > xMAX ) xMAX = (int) pto.x;
		      if( pto.x < xMIN ) xMIN = (int) pto.x;
		      if( pto.y > yMAX ) yMAX = (int) pto.y;
		      if( pto.y < yMIN ) yMIN = (int) pto.y;
		    }
		    
		  }

		  //Image dimensions
		  int W = xMAX - xMIN + 1;
		  int H = yMAX - yMIN + 1;
		  float R = (float)W/H;

		  //Keeping the aspect ratio (R), scale to 256 pixels height

		  H=256;
		  W=(int)(H*R);
		  if( W<=0 ) W=1;

		  //Give some margin to the image
		  W += 10;
		  H += 10;

		  //Create image
		  int[][] img = new int[H][];
		  for(int i=0; i<H; i++) {
		    img[i] = new int[W];
		    for(int j=0; j<W; j++)
		      img[i][j] = 255;
		  }

		  //Create the structure that stores to which stroke belongs each pixel
		  pix_stk = new int[H][];
		  for(int i=0; i<H; i++) {
		    pix_stk[i] = new int[W];
		    for(int j=0; j<W; j++)
		      pix_stk[i][j] = -1;
		  }

		  //Render image
		  Punto pant = new Punto();
		  Punto aux = new Punto();
		  Punto pto = null;
		  for(int i=0; i<nStrokes(); i++) {
		    
		    for(int np=0; np < dataon.get(i).getNpuntos() ; np++) {
		      pto = dataon.get(i).get(np);

		      aux.x = 5 + (W-10)*(float)(pto.x - xMIN)/(xMAX - xMIN + 1);
		      aux.y = 5 + (H-10)*(float)(pto.y - yMIN)/(yMAX - yMIN + 1);

		      img[(int)aux.y][(int)aux.x] = 0;
		      pix_stk[(int)aux.y][(int)aux.x] = i;

		      //Draw a line between last point and current point
		      if( np>=1 )
			linea(img,  pant,  aux, i);
		      
		      //Update last point
		      pant.x = aux.x;
		      pant.y = aux.y;
		    }
		  }

		   pW[0] = W;
		   pH[0] = H;

		  IMGxMIN = xMIN;
		  IMGyMIN = yMIN;
		  IMGxMAX = xMAX;
		  IMGyMAX = yMAX;

		  return img;
	}
	public void renderStrokesPBM( List<Integer>  SL, ObjectWrapper<int[][]> img, int[] rows, int[] cols) {
		 //Parameters used to render images while training the RNN classifier
		  final int REND_H =  40;
		  final int REND_W = 200;
		  final int OFFSET =   1;

		  int xMin, yMin, xMax, yMax, H, W;
		  xMin = yMin =  Integer.MAX_VALUE;
		  xMax = yMax = -Integer.MAX_VALUE;

		  //Calculate bounding box of the region defined by the points
		  for(Integer it : SL) {
		    for(int i=0; i<dataon.get( it ).getNpuntos(); i++) {
		      Punto  p = dataon.get( it ).get(i);

		      if( p.x < xMin ) xMin = (int) p.x;
		      if( p.y < yMin ) yMin = (int) p.y;
		      if( p.x > xMax ) xMax = (int) p.x;
		      if( p.y > yMax ) yMax = (int) p.y;
		    }
		  }
		  //Image dimensions
		  W = xMax - xMin + 1;
		  H = yMax - yMin + 1;

		  //Scale image to height REND_H pixels, keeping the aspect ratio
		  W = (int) (REND_H * (float)W/H);
		  H = REND_H;
		  
		  //If image is too wide (for example, a fraction bar) truncate width to REND_W
		  if( W > REND_W )
		    W = REND_W;

		  //Enforce a minimum size of 3 in both dimensions: height and width
		  if( H < 3 ) H = 3;
		  if( W < 3 ) W = 3;

		  //Create image
		  rows[0] = H+OFFSET*2;
		  cols[0] = W+OFFSET*2;

		  img.obj = new int[ rows[0] ][];
		  for(int i=0; i< rows[0]; i++) {
			 img.obj[i] = new int[  cols[0] ];
		    for(int j=0; j< cols[0]; j++)
		    	img.obj[i][j] = 255;
		  }

		  Punto pant = new Punto();
		  Punto aux = new Punto();

		  if( SL.size() == 1 && dataon.get(SL.get(0)).getNpuntos() == 1 ) {
		    //A single point is represented with a full black image
		    for(int i=OFFSET; i<H-OFFSET; i++)
		      for(int j=OFFSET; j<W-OFFSET; j++)
		    	  img.obj[i][j] = 0;
		  }
		  else {

		    for(Integer it : SL) {
		      for(int i=0; i<dataon.get(it).getNpuntos(); i++) {
					Punto  p = dataon.get(it).get(i);
		
					aux.x = OFFSET + (W-1)*(p.x - xMin)/(float)(xMax-xMin+1);
					aux.y = OFFSET + (H-1)*(p.y - yMin)/(float)(yMax-yMin+1);
		
					img.obj[(int)aux.y][(int)aux.x] = 0;
		
					//Draw a line between last point and current point
					if( i>=1 )
					  linea_pbm( img,  pant,  aux, -1);
					else if( i==0 && dataon.get(it).getNpuntos()==1 )
					  linea_pbm( img,  aux,   aux, -1);
				    
					//Update last point
					pant = new Punto( aux );
		      }
		    }
		  }

		  //Create smoothed image
		  int[][] img_smo = new int[  rows[0] ][];
		  for(int i=0; i< rows[0]; i++) {
		    img_smo[i] = new int[ cols[0] ];
		    for(int j=0; j< cols[0]; j++)
		      img_smo[i][j] = 0;
		  }

		  //Smooth AVG(3x3)
		  for(int y=0; y< rows[0]; y++)
		    for(int x=0; x< cols[0]; x++) {
		      
		      for(int i=y-1; i<=y+1; i++)
		  	for(int j=x-1; j<=x+1; j++)
		  	  if( i>=0 && j>=0 && i< rows[0] && j< cols[0] )
		  	    img_smo[y][x] += img.obj[i][j];
		  	  else
		  	    img_smo[y][x] += 255; //Background
		      
		      img_smo[y][x] /= 9; //3x3
		    }

		  //Replace IMG with the smoothed image and free memory
		  for(int y=0; y< rows[0]; y++)
		    for(int x=0; x< cols[0]; x++)
		     img.obj[y][x] = img_smo[y][x] < 255 ? 1 : 0;

	}

	public void render_img(String out) {
		 File fh = new File(Util.toString(out));
		 OutputStream frender = null;;
		try {
			frender = new FileOutputStream(fh);
		} catch (FileNotFoundException e) { 
		}
		  if( frender != null ) {
		    Util.printEntry(frender, "P2\n" + X[0] + " " + Y[0] + "\n255\n" );
		    for(int i=0; i<Y[0]; i++) {
		      for(int j=0; j<X[0]; j++)
		    	  Util.printEntry(frender, " " + Util.formatInt(dataoff[i][j], "%3d" ) );
		      Util.printEntry(frender, "\n");
		    }
		    try {
				frender.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		  }
		  else
		     System.err.print( "WARNING: Error creating file '" + Util.toString(out) + "'\n" );

	}
	
	public  void set_out_inkml(String  out) {
		  outinkml = new char[ out.length()+1];
		   Util. strcpy(outinkml, out,0);
	}
	public  void set_out_dot(String out) {
		  outdot = new char[ out.length()+1];
		  Util.strcpy(outdot, out, 0);
	}
	public  char[] getOutDot() {
		return outdot;
	}

	public  void print() {
		System.out.print( "Number of strokes: " +  nStrokes() + "\n" );
	};
	
	public void printInkML(Grammar G, Hypothesis  H) {

		  //If no output file specified, skip
		  if(  outinkml == null || outinkml.length == 0) return;

		  File fd = new File(Util.toString(outinkml));
		 
		  OutputStream fout = null;
		try {
			fout = new FileOutputStream(fd);
		} catch (FileNotFoundException e1) {
		    System.err.print( e1);
		    throw new RuntimeException(   e1 );
    	}
		  

		  Util.printEntry(fout, "<ink xmlns=\"http://www.w3.org/2003/InkML\">\n");
		  Util. printEntry(fout, "<annotation type=\"UI\">" +UItag+ "</annotation>\n" );
		  Util. printEntry(fout, "<annotationXML type=\"truth\" encoding=\"Content-MathML\">\n");
		  //fprintf(fout, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		  Util. printEntry(fout, "<math xmlns='http://www.w3.org/1998/Math/MathML'>\n");

		  //Save the value of next_id in order to restore it after printing the MathML expression
		  //and generating the IDs for symbols
		  int nid_bak = next_id[0];
		  next_id[0]++; //Skip one ID that will be the traceGroup that starts the symbol part

		  //Print MathML
		  if(  H.pt == null)
		    H.prod.print_mathml(G, H, fout,  next_id);
		  else {
		    char tipo  = H.pt.getMLtype( H.clase );
		    char[] clase = H.pt.getTeX( H.clase );

		      
		    String inkid = Util.toString(clase) + "_" + (next_id[0]+1);
		    H.inkml_id = inkid;
		    Util.printXmlEntry(fout, tipo,  H.inkml_id, Util.toString(clase)); 
		  }

		  //Restore next_id
		  next_id[0] = nid_bak;

		  Util.printEntry(fout, "</math>\n");
		  Util.printEntry(fout, "</annotationXML>\n");

		  //Print the strokes
		  for(int i=0; i<nStrokes(); i++) {
			  Util.printEntry(fout, "<trace id=\"" + i + "\">\n");
		    
		    Punto  pto = dataon.get(i).get(0);
		    Util.printEntry(fout, (int)pto.x + " "+ (int)pto.y);
		    for(int np=1; np < dataon.get(i).getNpuntos() ; np++) {
		      pto = dataon.get(i).get(np);
		      Util.printEntry(fout, ", " + (int)pto.x + " " + (int)pto.y);
		    }

		    Util.printEntry(fout, "\n</trace>\n");
		  }
		  ++next_id[0];
		  Util.printEntry(fout, "<traceGroup xml:id=\"" + next_id[0]+ "\">\n" );
		  Util.printEntry(fout, "<annotation type=\"truth\">Segmentation</annotation>\n");

		  //Print the information about symbol segmentation and recognition
		  printSymRecInkML(H, fout);

		  Util.printEntry(fout, "</traceGroup>\n");
		  Util.printEntry(fout, "</ink>\n");

		 try {
			fout.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}

	}
	public  void printSymRecInkML(Hypothesis  H, OutputStream fout) {

		  //Example (symbol + composed of strokes 3 and 4)

		  // <traceGroup xml:id="17">
		  //    <annotation type="truth">+</annotation>
		  //    <traceView traceDataRef="3"/>
		  //    <traceView traceDataRef="4"/>
		  //    <annotationXML href="+_1"/>
		  // </traceGroup>

		  if( H.prod != null &&  H.pt == null ) {
		    printSymRecInkML( H.hi, fout );
		    printSymRecInkML( H.hd, fout );
		  }
		  else {
		    next_id[0]++;
		    Util.printEntry(fout, "<traceGroup xml:id=\"" + next_id[0] +  "\">\n" );
		    Util.printEntry(fout, "  <annotation type=\"truth\">" + H.pt.getTeX(H.clase)  + "</annotation>\n");
		    for(int i=0; i<H.parent.nc; i++)
		      if( H.parent.ccc[i] ) {
		    	  Util.printEntry(fout, "  <traceView traceDataRef=\""+i+"\"/>\n" );
		      }
		    Util.printEntry(fout, "  <annotationXML href=\"" +H.inkml_id+ "\"/>" );
		    Util.printEntry(fout, "</traceGroup>\n");
		  }
	}
}
