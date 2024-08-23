package seshat;

public class SpaRel {
	 public  static final  int NRELS = 6;
	 public	  static  final int NFEAT = 9;

	 
	 public static  Hypothesis  leftmost(Hypothesis h) {
		  if( h.pt != null)
		    return h;

		  Hypothesis  izq = leftmost(h.hi);
		  Hypothesis  der = leftmost(h.hd);

		  return izq.parent.x < der.parent.x ? izq : der;
		}
	 public static Hypothesis  rightmost(Hypothesis  h) {
		  if( h.pt != null )
		    return h;

		  Hypothesis  izq = rightmost(h.hi);
		  Hypothesis  der = rightmost(h.hd);

		  return izq.parent.s > der.parent.s ? izq : der;
		}

		//Percentage of the area of region A that overlaps with region B
	 public static	float solape(CellCYK  a, CellCYK  b) {
		  int x = Math.max(a.x, b.x);
		  int y =  Math.max(a.y, b.y);
		  int s =  Math.min(a.s, b.s);
		  int t =  Math.min(a.t, b.t);

		  if( s >= x && t >= y ) {
		    float aSolap = (float) ((s-x+1.0)*(t-y+1.0));
		    float aTotal = (float) ((a.s - a.x+1.0)*(a.t - a.y+1.0));

		    return aSolap/aTotal;
		  }

		  return 0.0f;
		}
		
		
	 
	  private   GMM  model;
	  private	  Sample  mue;
	  private	  float[] probs  = new float[NRELS];

	 public	  double compute_prob(Hypothesis  h1, Hypothesis  h2, int k) {
		 //Set probabilities according to spatial constraints  

		  if( k<=2 ) {
		    //Check left-to-right order constraint in Hor/Sub/Sup relationships
		    Hypothesis  rma = rightmost(h1);
		    Hypothesis  lmb =  leftmost(h2);
		    
		    if( lmb.parent.x < rma.parent.x || lmb.parent.s <= rma.parent.s )
		      return 0.0;
		  }

		  //Compute probabilities
		  float[] sample = new float[NFEAT];

		  getFeas(h1,h2,sample,mue.RY);

		  //Get spatial relationships probability from the model
		  model.posterior(sample, probs);

		  //Slightly smooth probabilities because GMM classifier can provide
		  //to biased probabilities. Thsi way we give some room to the
		  //language model (the 2D-SCFG grammar)
		  smooth(probs);

		  return probs[k];
		  
	 }
	 public  void smooth(float[]  post) {
		  for(int i=0; i<NRELS;i++)
			    post[i] = (float) ((post[i]+0.02)/(1.00 + NRELS*0.02));
	 }

	  public SpaRel(GMM  gmm, Sample  m) {
		  model = gmm;
		  mue = m;
	  }
	  
	  public void destructor() {}

	 public	  void getFeas(Hypothesis  a, Hypothesis  b, float[] sample, int ry) {
		 //Normalization factor: combined height
		  float F = Math.max(a.parent.t, b.parent.t) - Math.min(a.parent.y, b.parent.y) + 1;

		  sample[0] = (b.parent.t-b.parent.y+1)/F;
		  sample[1] = (a.rcen - b.lcen)/F;
		  sample[2] = (float) (((a.parent.s+a.parent.x)/2.0 - (b.parent.s+b.parent.x)/2.0)/F);
		  sample[3] = (b.parent.x-a.parent.s)/F;
		  sample[4] = (b.parent.x-a.parent.x)/F;
		  sample[5] = (b.parent.s-a.parent.s)/F;
		  sample[6] = (b.parent.y-a.parent.t)/F;
		  sample[7] = (b.parent.y-a.parent.y)/F;
		  sample[8] = (b.parent.t-a.parent.t)/F;
	 }

	 public  double getHorProb(Hypothesis  ha, Hypothesis  hb) {
		  return compute_prob(ha,hb,0);
	 }
	 public  double getSubProb(Hypothesis  ha, Hypothesis  hb) {
		 return compute_prob(ha,hb,1);
	 }
	 public  double getSupProb(Hypothesis  ha, Hypothesis  hb) {
		 return compute_prob(ha,hb,2);
	 }
	 public  double getVerProb(Hypothesis  ha, Hypothesis  hb ) {
		 return getVerProb(ha, hb, false);
	 }
	 
	 public  double getVerProb(Hypothesis  ha, Hypothesis  hb, boolean strict) {
		//Pruning
		  if( hb.parent.y < (ha.parent.y + ha.parent.t)/2
		      || Math.abs((ha.parent.x+ha.parent.s)/2 - (hb.parent.x+hb.parent.s)/2) > 2.5*mue.RX 
		      || (hb.parent.x > ha.parent.s || hb.parent.s < ha.parent.x) )
		    return 0.0;

		  if( !strict )
		    return compute_prob(ha,hb,3);

		  //Penalty for strict relationships
		  float penalty = (float) (Math.abs(ha.parent.x - hb.parent.x)/(3.0*mue.RX)
		    + Math.abs(ha.parent.s - hb.parent.s)/(3.0*mue.RX));

		  if( penalty > 0.95 ) penalty = 0.95f;

		  return (1.0 - penalty) * compute_prob(ha,hb,3);
	 }
	 public  double getInsProb(Hypothesis  ha, Hypothesis  hb) {
		 if( solape(hb.parent,ha.parent) < 0.5 || 
			      hb.parent.x < ha.parent.x || hb.parent.y < ha.parent.y )
			    return 0.0;

			  return compute_prob(ha,hb,4);
	 }
	 public  double getMrtProb(Hypothesis  ha, Hypothesis  hb) {
		  return compute_prob(ha,hb,5);
	 }
	 
 
}
