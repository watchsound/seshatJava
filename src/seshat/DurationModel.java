package seshat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class DurationModel {
	 public int max_strokes;
	  public int Nsyms;
	  public float[][] duration_prob;

	  public void loadModel(String file, SymRec sr, boolean flag) throws  Exception {
		  String str= null;
		  int count, nums;
          Scanner scanner =  null;
          file = file.trim();
          if( flag ) {
        	 
			  if( !file.startsWith("/"))
				  file = "/" + file;
			   InputStream is = meParser.class.getResourceAsStream(file); 
			   scanner = new Scanner(is);
          } else {
        	  File f = new File(file);
     		 if( !f.exists()) { 
     			 
     			 System.err.println("Error loading duration model '" + str  + "'\n");
     			 throw new RuntimeException("Error loading duration model '" +  str  + "'\n");
     		 } 
     		   scanner = new Scanner(f);
          }
          
		  //Load data
		  while( true ) {
			  if (scanner.hasNext() ) {
				  count = Integer.parseInt(scanner.next());
			  } else {
				  break;
			  }
			  if (scanner.hasNext() ) {
				  str = scanner.next() ;
			  } else {
				  break;
			  }
			  if (scanner.hasNext() ) {
				  nums = Integer.parseInt(scanner.next());
			  } else {
				  break;
			  }
		      if( nums <= max_strokes ) {
		        duration_prob[ sr.keyClase(str) ][nums-1] = count;
		      }
		  }

		  //Compute probabilities
		  for(int i=0; i<Nsyms; i++) {
		    int total=0;

		    for(int j=0; j<max_strokes; j++) {
		      if( duration_prob[i][j] == 0 ) //Add-one smoothing
			duration_prob[i][j] = 1;
		      total += duration_prob[i][j];
		    }
		    
		    for(int j=0; j<max_strokes; j++)
		      duration_prob[i][j] /= total;
		  }
          if( scanner != null ) {
        	  scanner.close();
          }
	  }

	 public  DurationModel(char[] str, int mxs, SymRec  sr, boolean flag) {
		
		  max_strokes = mxs;
		  Nsyms = sr.getNClases();

		  duration_prob = new float [Nsyms][];
		  for(int i=0; i<Nsyms; i++) {
		    duration_prob[i] = new float[max_strokes];
		    for(int j=0; j<max_strokes; j++)
		      duration_prob[i][j] = 0;
		  }

		  try {
			loadModel(Util.toString(str),sr, flag);
		} catch (Exception e) {
		 	e.printStackTrace();
		}
 
	 }
	 public void destructor() {
		 
	 }

	 public float prob(int symclas, int size) {
		 float v = duration_prob[symclas][size-1];
		 return v;
		// return duration_prob[symclas][size-1];
	 }
}
