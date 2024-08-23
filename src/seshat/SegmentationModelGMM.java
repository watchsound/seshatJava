package seshat;

import java.io.File;

public class SegmentationModelGMM {
	public GMM  model;

	 public SegmentationModelGMM(char[] mod , boolean flag) {
		 String path = Util.toString(mod).trim();
		  
		 model =   new GMM(path, flag)  ;
	 }
	  

	 public  float prob(CellCYK cd, Sample  m) {
		 int Nstrokes=0, nps=0;
		  float dist=0, delta=0, sigma=0, mind=0, avgsize=0;

		  for(int i=0; i<cd.nc; i++)
		    if( cd.ccc[i] )
		      Nstrokes++;

		  int[] strokes_list = new int[Nstrokes];
		  Nstrokes = 0;
		  for(int i=0; i<cd.nc; i++)
		    if( cd.ccc[i] )
		      strokes_list[Nstrokes++] = i;

		  //For every stroke
		  for(int i=0; i<Nstrokes; i++) {
		    Stroke  Si = m.getStroke( strokes_list[i] );

		    float size_i =Math. max(Si.rs - Si.rx, Si.rt - Si.ry);
		    avgsize += size_i;

		    for(int j=i+1; j<Nstrokes; j++) {
		      Stroke  Sj = m.getStroke( strokes_list[j] );

		      //distance between stroke Si and Sj
		      mind  += Si.min_dist( Sj );

		      dist  += Math.abs( (Si.rs + Si.rx)/2.0 - (Sj.rs + Sj.rx)/2.0 );
		      sigma += Math.abs( (Si.rt + Si.ry)/2.0 - (Sj.rt + Sj.ry)/2.0 );
		      
		      float size_j = Math.max( Sj.rt - Sj.ry, Sj.rs - Sj.rx);
		      delta += Math. abs( size_i - size_j );

		      nps++;
		    }

		  }

		  float[] avgw = new float[1];
		  float[] avgh = new float[1];
		  float nf;
		  m.getAVGstroke_size( avgw,  avgh);
		  nf = (float) Math.sqrt(avgw[0]*avgw[0] + avgh[0]*avgh[0]);

		  mind  /= nps*nf;
		  dist  /= nps*nf;
		  delta /= nps*nf;
		  sigma /= nps*nf;

		  float[] sample = new float[4];
		  float[] probs = new float[2];

		  sample[0] = mind;
		  sample[1] = dist;
		  sample[2] = delta;
		  sample[3] = sigma;

		  model.posterior(sample, probs);

		  
		  //Return probability of being a proper segmentation hypothesis
		  return probs[1];
	 }
}
