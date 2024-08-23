package seshat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class GMM {
	public static final double PI = 3.14159265359;
	 public int C, D, G;
	 public float[][] invcov, mean, weight;
	 public float[] prior, det;

	 
	 public  GMM(String fileName, boolean flag) {
		 fileName = fileName.trim();
		 if( flag ) {
		      try {
		    	  if( !fileName.startsWith("/"))
		    		  fileName = "/" + fileName;
		    	  InputStream is = meParser.class.getResourceAsStream(fileName);
		 		 
				   Scanner scanner = null;
					try {
						scanner = new Scanner(is);
					} catch ( Exception e) { 
						  throw new RuntimeException( e);
					}
					 loadModel(scanner, fileName);
		      }catch(Exception ex) {
		    	  throw new RuntimeException( ex );  
		      } 	 
		 }
		 else { 
				File f = new File(fileName);
				if( !f.exists()) {
					System.err.println("Error loading segmentation model '" + fileName+ "'\n");
					throw new RuntimeException("Error loading segmentation model '" + fileName + "'\n");
				} 
				  try {
			    	  loadModel(fileName);
			      }catch(Exception ex) {
			    	  throw new RuntimeException( ex );  
			      } 
		 }

	 }
	 public void destructor() {}

     public void loadModel( String fileName ) throws Exception {
    	 File fd = new File(fileName);
    	 if(! fd.exists() ) {
    	   System.err.println("Error loading GMM model file " + fileName + "\n");	
    	   throw new RuntimeException("Error loading GMM model file " + fileName + "\n" );
    	 }
    	  Scanner fscan = new Scanner(fd);
          loadModel(fscan, fileName);
     }
     public void loadModel(Scanner fscan, String fileName) {
    	  //Read parameters
    	  C = fscan.nextInt();
    	  D = fscan.nextInt();
    	  G = fscan.nextInt();
    	   
    	  //Read prior probabilities
    	  prior = new float[C];
    	  for(int i=0; i<C; i++) {
    		  prior[i] = fscan.nextFloat();
    	  }
    	 //   fscanf(fd, "%f", &prior[i]);
    	    
    	  invcov  = new float [C*G][];
    	  mean    = new float [C*G][];
    	  weight  = new float [C][];
    	  det     = new float[C*G];

    	  //Read a GMM for each class
    	  for(int c=0; c<C; c++) {

    	    //Read diagonal covariances
    	    for(int i=0; i<G; i++) {
    	      invcov[c*G+i] = new float[D];
    	      det[c*G+i] = 1.0f;

    	      for(int j=0; j<D; j++) {
    		   //   fscanf(fd, "%f", &invcov[c*G+i][j]);
    		      invcov[c*G+i][j] = fscan.nextFloat();

    		//Compute determinant of convariance matrix (diagonal)
    		det[c*G+i] *= invcov[c*G+i][j];

    		//Save the inverse of the convariance to save future computations
    		if( invcov[c*G+i][j] == 0.0 ) {
    		  System.err.print( "Warning: covariance value equal to zero in GMM\n");
    		  invcov[c*G+i][j] = (float) (1.0/1.0e-10);
    		}
    		else
    		  invcov[c*G+i][j] = (float) (1.0/invcov[c*G+i][j]);
    	      }
    	    }

    	    //Read means
    	    for(int i=0; i<G; i++) {
    	      mean[c*G+i] = new float[D];
    	      for(int j=0; j<D; j++) {
    	    	  mean[c*G+i][j] = fscan.nextFloat();
    	      } 
    	    }

    	    //Read mixture weights
    	    weight[c] = new float[G];
    	    for(int i=0; i<G; i++)
    	    	weight[c][i] = fscan.nextFloat(); 
    	  }

    	  fscan.close();
	 }
	 public float pdf(int c, float[] v) {
		  float pr = 0.0f;

		  for(int i=0; i<G; i++) {

		    float exponent = 0.0f;
		    for(int j=0; j<D; j++)
		      exponent += (v[j] - mean[c*G+i][j]) * invcov[c*G+i][j] * (v[j] - mean[c*G+i][j]);

		    exponent *= -0.5;

		    pr +=  weight[c][i] * Math.pow(2 * PI, -D/2.0) * Math.pow(det[c*G+i], -0.5) * Math.exp( exponent );
		  }

		  return prior[c] * pr; 
	 }

	 
	 
	 public  void posterior(float[]  x, float[] pr) {
		  float total=0.0f;
		  for(int c=0; c<C; c++) {
		    pr[c]  = pdf(c, x);
		    total += pr[c];
		  }

		  for(int c=0; c<C; c++)
		    pr[c] /= total;
	 }
}
