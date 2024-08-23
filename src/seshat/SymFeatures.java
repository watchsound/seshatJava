package seshat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

import lang.ArrayListFloat;
import lang.ArrayListInt;
import rnnlibjava.DataSequence;
import rnnlibjava.Helpers;
import rnnlibjava.SeqBuffer;
import rnnlibjava.SeqBuffer_float;
import seshat.Online.Point;
import seshat.Online.sentence;
import seshat.Sample.SegmentHyp;
import seshat.Stroke.Punto;

public class SymFeatures {
	 static   int  ON_FEAT = 7;
	  static   int OFF_FEAT = 9;
	  double[] means_on = new double[ON_FEAT];
	  double[] means_off = new double[OFF_FEAT];
	  double[]  stds_on = new double[ON_FEAT];
	  double[] stds_off = new double[OFF_FEAT];
	  
	public  SymFeatures(String mav_on, String mav_off, boolean flag) {
		  mav_on = mav_on.trim();
		  mav_off = mav_off.trim();
		  //Load means and stds normalization
		  Scanner scanner = null;
		  if( flag ) {
			  if( !mav_on.startsWith("/"))
				  mav_on = "/" + mav_on;
			   InputStream is = meParser.class.getResourceAsStream(mav_on); 
				try {
					scanner = new Scanner(is);
				} catch ( Exception e) { 
					  throw new RuntimeException( e);
				}
		  } else {
			  
			  File f = new File( mav_on );
			  
			  if( !f.exists() ) {
			     System.err.println( "Error loading online mav file: " +   mav_on );
			     throw new RuntimeException(  "Error loading online mav file: " +  mav_on );
			  }

				try {
					scanner = new Scanner(f);
				} catch (FileNotFoundException e) {
					 e.printStackTrace();
				}
		  }
		 
		  
		 
		  
		  //Read values online
		  for(int i=0; i<ON_FEAT; i++) {
			  String token = scanner.next();
			  means_on[i] = Float.parseFloat(token);
		  
		  }
		  for(int i=0; i<ON_FEAT; i++) {
			  String token = scanner.next();
			  stds_on[i] = Float.parseFloat(token); 
		  }
		  scanner.close();

		  
		  if( flag ) {
			  if( !mav_off.startsWith("/"))
				  mav_off = "/" + mav_off;
			   InputStream is = meParser.class.getResourceAsStream(mav_off); 
				try {
					scanner = new Scanner(is);
				} catch ( Exception e) { 
					  throw new RuntimeException( e);
				}
		  } else {
			  
			  File f = new File( mav_off );
			  if( !f.exists() ) {
				     System.err.println( "Error loading offline mav file: " + mav_off );
				     throw new RuntimeException(  "Error loading offline mav file: " + mav_off);
				  }
				  

				try {
					scanner = new Scanner(f);
				} catch (FileNotFoundException e) {
					 e.printStackTrace();
				}
		  }
		  
       
		 
		  
		  for(int i=0; i<OFF_FEAT; i++) {
			  String token = scanner.next();
			  means_off[i] = Float.parseFloat(token);
		  
		  }
		  for(int i=0; i<OFF_FEAT; i++) {
			  String token = scanner.next();
			  stds_off[i] = Float.parseFloat(token); 
		  }
		  scanner.close();
	}
	 

	 public DataSequence getOnline(Sample M, SegmentHyp SegHyp) {
		//Create and fill sequence of points
		  sentence sent=new sentence( SegHyp.stks.size() );

		  for(Integer it : SegHyp.stks ) {
		    
			  Online. stroke st = new Online.stroke(M.getStroke( it).getNpuntos(), true); //means is pendown stroke

		    for(int j=0; j<M.getStroke( it).getNpuntos(); j++) {
		      Punto  p = M.getStroke( it).get(j);
		      Point q = new Point((int)p.x, (int)p.y);

		      st.points.add( q );
		    }

		    sent.strokes.add(st);
		  }

		  // Remove repeated points
		  sentence  no_rep = sent.anula_rep_points();
		  // Median filter
		  sentence   traz_suav=no_rep.suaviza_traza();

		  //Compute online features
		  sentenceF feat = new sentenceF();
		  feat.calculate_features( traz_suav);

		  //Create DataSequence
		  
		  //Set sequence shape
		  int nvec = feat.n_frames;

		  //Check number of online features
		  if( feat.frames[0].get_fr_dim() != ON_FEAT ) {
		    System.err.print( "Error: unexpected number of online features\n");
		    throw new RuntimeException( "Error: unexpected number of online features\n" );
		  }

		  //Create sequence
		  DataSequence  seq = new DataSequence(ON_FEAT);

		   ArrayListInt shape = new ArrayListInt(1);
		   shape.add(0, nvec); ;
//		  int[] shape = new int[1];
//		  shape[0] = nvec;

		  //Create aux SeqBuffer to fill data
		  SeqBuffer_float  auxBuf = new SeqBuffer_float(shape, ON_FEAT);

		  //Save the input vectors following the SeqBuffer data representation
		  for(int i=0; i<nvec; i++) {
		    for(int j=0; j<ON_FEAT; j++) {
		      double val = feat.frames[i].getFea(j);

		      //Normalize to normal(0,1)
		      val = (val - means_on[j])/stds_on[j];

		      auxBuf.set(i*ON_FEAT + j, (float)val); 
		    }
		  }

		  //Assign the loaded data
		  seq.inputs =  auxBuf;
		 

		  //Create target vector (content doesn't matter, just because it's required)
		  int[] target = new int[nvec];
		  shape.set(0,  nvec);
		  seq.targetClasses.setData( target );
		  seq.targetClasses.shape = shape;
		  seq.tag = "none";
		   
		  if(Helpers.verbose)
			  System.out.println( " in symfeatures ..  seq-inputs- " +  seq.inputs.seq_shape().size() + "  " + seq.inputs.seq_shape());

		  //Return extracted features for the sequence of strokes
		  return seq;
	 }
	 
	 public DataSequence getOfflineFKI(ObjectWrapper<int[][]> img, int H, int W) {

		  //Create sequence
		  DataSequence  seq = new DataSequence(OFF_FEAT);

		  //Set sequence shape
		  int nvec = W;
 
		  ArrayListInt shape = new ArrayListInt(1);
		   shape.add( nvec); ;
		   

		  //Create aux SeqBuffer to fill data
		   SeqBuffer_float auxBuf = new SeqBuffer_float(shape, OFF_FEAT);


		  //Compute FKI offline features
		  double[] c = new double[OFF_FEAT+1];
		  double c4ant=H+1, c5ant=0;

		  //For every column
		  for(int x=0; x<W; x++) {

		    //Compute the FKI 9 features
		    for(int i=0; i<OFF_FEAT; i++)
		      c[i] = 0;
		    c[4]=H+1;

		    for(int y=1; y<=H; y++) {
		      if( img.obj[y-1][x] != 0 ) { //Black pixel
			c[1] += 1;
			c[2] += y;
			c[3] += y*y;
			if( y<c[4] ) c[4]=y;
			if( y>c[5] ) c[5]=y;
		      }
		      if( y>1 && img.obj[y-1][x] != img.obj[y-2][x] ) c[8]++; 
		    }
		    
		    c[2] /= H;
		    c[3] /= H*H;

		    for(int y=(int) (c[4]+1); y<c[5]; y++)
		      if( img.obj[y-1][x] != 0) //Black pixel
			c[9]++;
		    
		    c[6]=H+1; c[7]=0;
		    if( x+1 < W ) {
		      for(int y=1; y<=H; y++) {
			if( img.obj[y-1][x+1] != 0 ) { //Black pixel
			  if( y<c[6] ) c[6]=y;
			  if( y>c[7] ) c[7]=y;
			}
		      }
		    }
		    c[6] = (c[6] - c4ant)/2;
		    c[7] = (c[7] - c5ant)/2;

		    c4ant = c[4];
		    c5ant = c[5];

		    //Save the input vectors following the SeqBuffer data representation
		    for(int j=0; j<OFF_FEAT; j++) {
		      //Normalize to normal(0,1)
		      c[j+1] = (c[j+1] - means_off[j])/stds_off[j];

		      auxBuf.set(x*OFF_FEAT + j, (float) c[j+1]);
		    }
		  }

		  //Assign the loaded data
		  seq.inputs =  auxBuf;
		 

		  //Create target vector (content doesn't matter, just because it's required)
		  int[] target = new int[nvec];
		  shape.set( 0, nvec );
		  seq.targetClasses.setData(  target );
		  seq.targetClasses.shape = shape;
		  seq.tag = "none";
		  
		  //Return extracted features for the sequence of strokes
		  return seq;
	 }


	public DataSequence getOfflineFKI(int img, int rows, int cols) {
		// TODO Auto-generated method stub
		return null;
	}
}
