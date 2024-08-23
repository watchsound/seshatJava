package seshat;

import java.io.*;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.Scanner;

import lang.ArrayListObj;
import rnnlibjava.DataExporter.DataExportHandler;
import rnnlibjava.DataHeader;
import rnnlibjava.DataSequence;
import rnnlibjava.Helpers;
import rnnlibjava.Layer;
import rnnlibjava.Mdrnn;
import rnnlibjava.Pair;
import rnnlibjava.Trainer;
import rnnlibjava.WeightContainer;
import seshat.Sample.SegmentHyp;
import seshat.Stroke.Punto;

public class SymRec {

	public static final int TSIZE = 2048;
	
	  SymFeatures  FEAS;
	  DataHeader header_on = new DataHeader();
	  DataHeader header_off = new DataHeader();
	 
	  DataExportHandler deh_on = new DataExportHandler();
	  DataExportHandler deh_off = new DataExportHandler();
	  WeightContainer  wc_on,  wc_off;
	  Mdrnn  blstm_on,  blstm_off;
	  float RNNalpha;
	  
	  //Symbol classes and types information
	  int[] type;
	  Map<String,Integer> cl2key = new HashMap<String,Integer>();
	  String[]  key2cl;
	  
	  int C; //Number of classes
	  
	public SymRec(String config, boolean flag) throws Exception{
		  Scanner scanner = null;
		  config = config.trim();
		  if( flag ) {
			  if( !config.startsWith("/"))
				  config = "/" + config;
			   InputStream is = meParser.class.getResourceAsStream(config); 
				try {
					scanner = new Scanner(is);
				} catch ( Exception e) { 
					  throw new RuntimeException( e);
				}
		  } else {
			  File f = new File(Util.toString(config)); 
			  if( !f.exists() ) {
			     System.err.println( "Error loading  config file: " + Util.toString(config) );
			     throw new RuntimeException(  "Error loading  config file: " + Util.toString(config) );
			  }

				try {
					scanner = new Scanner(f);
				} catch (FileNotFoundException e) {
					 e.printStackTrace();
				}
		  }
         
		  //RNN classifier configuration
		  char[]  RNNon = new char[TSIZE];
		  char[]  RNNoff = new char[TSIZE];
		  char[]  RNNmavON = new char[TSIZE];
		  char[]  RNNmavOFF = new char[TSIZE];
			  
		  char[]  id = new char[TSIZE];
		  char[]  info = new char[TSIZE];
		  char[]  path = new char[TSIZE];
			  

		  RNNon[0] = RNNoff[0] = RNNmavON[0] = RNNmavOFF[0] = 0;
		  path[0] = 0;
		  RNNalpha=-1.0f;
		  
		
			
		  while( scanner.hasNext() ) {
			  String sid = scanner.next();
			  id = sid.toCharArray();
			  info = scanner.next().trim().toCharArray();
		   
		     
		    
		    if(      sid.startsWith("RNNon") )       Util.strcpy(RNNon,     info, 0);
		    else if( sid.startsWith("RNNoff") )     Util. strcpy(RNNoff,    info,0);
		    else if( sid.startsWith("RNNmavON") )   Util. strcpy(RNNmavON,  info,0);
		    else if( sid.startsWith("RNNmavOFF") )  Util. strcpy(RNNmavOFF, info,0);
		    else if( sid.startsWith("RNNalpha")  )   RNNalpha = Float.parseFloat(Util.toString(info));
		    else if( sid.startsWith("SymbolTypes") ) Util.strcpy(path, info,0);
		  }
		  
		  if( RNNalpha <= 0.0 || RNNalpha >= 1.0 ) {
			   System.err.println( "Error loading config file '" + Util.toString(config) +"': must be 0 < RNNalpha < 1 \n "   );
			  throw new RuntimeException(  "Error loading config file '" + Util.toString(config) +"': must be 0 < RNNalpha < 1 \n " );
			  
		  }
		  if( RNNon[0] == 0 ) {
			   System.err.println( "Error loading RNNon  config file  "     );
			   throw new RuntimeException(  "Error loading RNNon config file  " ); 
		  }
		  if( RNNoff[0] == 0 ) {
			   System.err.println( "Error loading RNNoff  config file  "     );
			   throw new RuntimeException(  "Error loading RNNoff config file  " );  
		  }
		  if( RNNmavON[0] == 0 ) {
			  System.err.println( "Error loading RNNmavON  config file  "     );
			   throw new RuntimeException(  "Error loading RNNmavON config file  " );  
			   
		  }
		  if( RNNmavOFF[0] == 0 ) {
			  System.err.println( "Error loading RNNmavOFF  config file  "     );
			   throw new RuntimeException(  "Error loading RNNmavOFF config file  " );   
		  }

		  //Close config file
		 scanner.close();

		 BufferedReader  br = null;
		 String spath = Util.toString(path);
		 spath = spath.trim();
		 if( flag ) { 
			  if( !spath.startsWith("/"))
				  spath = "/" + spath;
			  InputStream is = meParser.class.getResourceAsStream(spath); 
			  br =new BufferedReader(new InputStreamReader(is)); 
		 } else {
			  //Load symbol types info
			  File f = new File(spath); 
			  if( !f.exists() ) {
			     System.err.println( "Error loading  SymbolTypes file: " + Util.toString(config) );
			     throw new RuntimeException(  "Error loading  SymbolTypes file: " + Util.toString(config) );
			  }
			   br =new BufferedReader(new FileReader(f)); 
		 }
		
		  
		  String line = br.readLine();
		  //Number of classes
		  C = Integer.parseInt(line.trim());
		 

		  key2cl = new String[C];
		  type   = new int[C];
		  String clase = null;
		  char T=0;
		  String linea= null; // aux[256];

		  //Load classes and symbol types
		  int idclase=0;
		  while( (linea = br.readLine())!= null ) {
			  String[] fs = linea.trim().split(" ");
			  clase = fs[0];
			  T = fs[1].charAt(0);
		   

		    key2cl[idclase] = clase;
		    cl2key.put( clase,  idclase);
		    idclase++;
		    
		    if( T=='n' )       type[ cl2key.get(clase) ] = 0; //Centroid
		    else if( T=='a' )  type[ cl2key.get(clase) ] = 1; //Ascender
		    else if( T=='d' )  type[ cl2key.get(clase) ] = 2; //Descender
		    else if( T=='m' )  type[ cl2key.get(clase) ] = 3; //Middle
		    else {
		    	  System.err.println(   "SymRec: Error reading symbol types\n" );
				     throw new RuntimeException(    "SymRec: Error reading symbol types\n"); 
		    }
		  }

		  //Features extraction
		  FEAS = new SymFeatures(Util.toString(RNNmavON), Util.toString(RNNmavOFF), flag);
		  
		  //Create and load BLSTM models
		  
		  //Online info
		  rnnlibjava.ConfigFile conf_on = null;
		try {
			conf_on = new rnnlibjava.ConfigFile(Util.toString(RNNon), flag);
		} catch (Exception e1) { 
			e1.printStackTrace();
		}
		  header_on.targetLabels = conf_on.get_list("targetLabels");
		  header_on.inputSize    = Integer.parseInt( conf_on.get("inputSize"));
		  header_on.outputSize   = header_on.targetLabels.size();
		  header_on.numDims      = 1;

		  //Create WeightContainer online
		  wc_on = new WeightContainer(  deh_on );

		  PrintStream cout = System.out;
		  //Load online BLSTM
		  blstm_on = new rnnlibjava.MultilayerNet(cout, conf_on, header_on, wc_on,  deh_on);

		  //build weight container after net is created
		  wc_on.build();
		  
		  //build the network after the weight container
		  blstm_on.build();
		  
		  //create trainer
		  Trainer trainer_on = new Trainer(cout, blstm_on, conf_on, wc_on, deh_on);
		  
		  String v = conf_on.get("loadWeights", "false");
		  if( Boolean.parseBoolean(v)) {
			  deh_on.load(conf_on, cout);
		  }
		 

		  //Offline info
		  rnnlibjava.ConfigFile conf_off = null;
		  try {
			conf_off = new rnnlibjava.ConfigFile(Util.toString(RNNoff), flag);
		  } catch (Exception e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  
		  //Check if the targetLabels are the same for both online and offline RNN-BLSTM
		  ArrayListObj<String> aux = conf_off.get_list ("targetLabels");
		  if( aux.size() != header_on.targetLabels.size() ) {
			  System.err.println( "Error: Target labels of online and offline symbol classifiers do not match "     );
			   throw new RuntimeException(  "Error: Target labels of online and offline symbol classifiers do not match  " );   
		 
		  }
          for(int i = 0, j = 0; i < aux.size() && j < header_on.targetLabels.size(); i++, j++) {
        	  if(! aux.get(i).equals(header_on.targetLabels.get(j))) {
        		  System.err.println( "Error: Target labels of online and offline symbol classifiers do not match "     );
   			      throw new RuntimeException(  "Error: Target labels of online and offline symbol classifiers do not match" );   
   		 
        	  }
          }
		 

		  header_off.targetLabels = conf_off.get_list ("targetLabels");
		  header_off.inputSize    = Integer.parseInt( conf_off.get ("inputSize") );
		  header_off.outputSize   = header_off.targetLabels.size();
		  header_off.numDims      = 1;

		  //Create WeightContainer offline
		  wc_off = new WeightContainer(  deh_off );

		  //Load offline BLSTM
		  blstm_off = new rnnlibjava.MultilayerNet(cout, conf_off, header_off, wc_off,  deh_off);

		  //build weight container after net is created
		  wc_off.build();

		  //build the network after the weight container
		  blstm_off.build();

		  //create trainer
		  Trainer trainer_off = new Trainer(cout, blstm_off, conf_off, wc_off,  deh_off);
		  String lw = conf_off.get ("loadWeights", "false");
		  if( Boolean.parseBoolean(lw)) {
			  deh_off.load(conf_off, cout);
		  } 
	}

	public int clasificar(Sample M, int ncomp, int NB, int[] vclase, float[] vpr, int[] as , int[] ds) {
		List<Integer> aux = new ArrayList<Integer>();
		  aux.add( ncomp );

		  return clasificar(M, aux, NB, vclase, vpr, as, ds);
	}

	public int symType(int i) {
		return type[i];
	}

	public String strClase(int clase) {
		 return  (key2cl[clase]) ;
	}

	public int clasificar(Sample M, List<Integer> LT, int nB, int[] vclase, float[] vpr, int[] as , int[] ds) {
		  SegmentHyp aux = new SegmentHyp();

		  aux.rx = aux.ry = Integer.MAX_VALUE;
		  aux.rs = aux.rt = -Integer.MAX_VALUE;

		  for(Integer it : LT) {
		    aux.stks.add(  it );

		    if( M.getStroke(it).rx < aux.rx ) aux.rx = M.getStroke(it).rx;
		    if( M.getStroke(it).ry < aux.ry ) aux.ry = M.getStroke(it).ry;
		    if( M.getStroke(it).rs > aux.rs ) aux.rs = M.getStroke(it).rs;
		    if( M.getStroke(it).rt > aux.rt ) aux.rt = M.getStroke(it).rt;
		  }
		  
		  return classify(M,  aux, nB, vclase, vpr, as, ds);
	}

	@SuppressWarnings("unchecked")
	public int classify(Sample M, SegmentHyp  SegHyp, int NB, int[] vclase, float[] vpr, int[] as , int[] ds) {

		  int regy = Integer.MAX_VALUE, regt=-Integer.MAX_VALUE, N=0;

		  //First compute the vertical centroid (cen) and the ascendant/descendant centroids (as/ds)
		  SegHyp.cen=0;
		  for(Integer it : SegHyp.stks ) {
		    for(int j=0; j<M.getStroke( it).getNpuntos(); j++) {
		      Punto p = M.getStroke( it).get(j);
			
		      if( M.getStroke( it).ry < regy )
			regy = M.getStroke( it).ry;
		      if( M.getStroke( it).rt > regt )
			regt = M.getStroke( it).rt;
		      
		      SegHyp.cen += p.y;
			
		      N++;
		    }

		  }
		  SegHyp.cen /= N;
		   as[0] = (SegHyp.cen+regt)/2;
		   ds[0] = (regy+SegHyp.cen)/2;

		  //Feature extraction of hypothesis
		  DataSequence  feat_on;

		  //Online features extraction: PRHLT (7 features)
		  feat_on = FEAS.getOnline( M, SegHyp );

		  //Render the image representing the set of strokes SegHyp.stks
		  ObjectWrapper<int[][]> img = new ObjectWrapper<int[][]>(null);
	 
		  int[] Rows = new int[1];
		  int[] Cols = new int[1];
		  M.renderStrokesPBM( SegHyp.stks, img,  Rows, Cols);

		  DataSequence  feat_off;
		  //Offline features extraction: FKI (9 features)
		  feat_off = FEAS.getOfflineFKI(img, Rows[0], Cols[0]);
		  
		  //cout << feat_off.inputs;
 

		  //n-best classification 
		Pair<Float,Integer>[] clason = new Pair[NB];
		  Pair<Float,Integer>[] clasoff = new Pair[NB];
		  Pair<Float,Integer>[] clashyb = new Pair[2*NB];
			   
		    
		  for(int i=0; i<NB; i++) {
			  clason[i] = new Pair<Float,Integer>();
			  clasoff[i] = new Pair<Float,Integer>();
			  clashyb[i] = new Pair<Float,Integer>();
		    clason[i].first = 0.0f; //probability
		    clason[i].second = -1; //class id
		    clasoff[i].first = 0.0f;
		    clasoff[i].second = -1;
		    clashyb[i].first = 0.0f;
		    clashyb[i].second = -1;
		  }
          for(int i = NB; i < 2*NB; i++) {
        	  clashyb[i] = new Pair<Float,Integer>();
        	    clashyb[i].first = 0.0f;
    		    clashyb[i].second = -1;
          }
		  //Online/offline classification
		  BLSTMclassification( blstm_on,  feat_on,  clason,  NB);
		  if( Helpers.verbose )  System.err.println("after..... BLSTMclassification.......");
		  BLSTMclassification( blstm_off, feat_off, clasoff, NB);

		  if( Helpers.verbose )  System.err.println("after..... BLSTMclassification.......");
		  //Online + Offline n-best linear combination
		  //alpha * pr(on) + (1 - alpha) * pr(off)
		    
		  for(int i=0; i<NB; i++) {
		     clason[i].first   =  ((Float) clason[i].first) * RNNalpha;       //online  *    alpha
		     clasoff[i].first  =   ((Float) clasoff[i].first) * (1.0f - RNNalpha); //offline * (1-alpha)
		  }

		  int hybnext=0;
		  for(int i=0; i<NB; i++) {
		    if( (clason[i].second) >= 0 ) {
		      
		      clashyb[hybnext].first  = clason[i].first;
		      clashyb[hybnext].second = clason[i].second;
		      
		      for(int j=0; j<NB; j++)
			if( clason[i].second == clasoff[j].second ) {
			     clashyb[hybnext].first  = ((Float)clashyb[hybnext].first) + ((Float) clasoff[j].first);
			    break;
			}
		      
		      hybnext++;
		    }
		    
		    //FIXME
		    if( hybnext >= 2*NB )
		    	break;
		    
		    if( (float)clasoff[i].second < 0 ) continue;
		    boolean found=false;
		    for(int j=0; j<NB && !found; j++)
		      if( clasoff[i].second == clason[j].second )
			found = true;
		      
		    //Add the (1-alpha) probability if the class is in OFF but not in ON
		    if( !found ) {
		      clashyb[hybnext].first  = clasoff[i].first;
		      clashyb[hybnext].second = clasoff[i].second;
		      hybnext++;
		    }
		    
		    //FIXME
		    if( hybnext >= 2*NB )
		    	break;
		  }
		  Arrays.sort(clashyb, new Comparator<Pair>(){ 
			@Override
			public int compare(Pair o1, Pair o2) {
				if( o1 == null || o2 == null )
					return 0;
				 float o11 = (float)o1.first;
				 int   o12 = (int)o1.second;
				 float o21 = (float)o2.first;
				 int   o22 = (int)o2.second;
			     if( o11 < o21 )
			    	 return 1;
			     if (o11 > o21 )
			    	 return -1;
			     if( o12 < o22 )
			    	 return 1;
			     if (o12 > o22 )
			    	 return -1;
			     return 0;
			     
			}}) ; 
			
			 
		  for(int i=0; i< Math.min(hybnext, NB); i++) {
		    vpr[i]    = (float) clashyb[i].first;
		    vclase[i] = (int) clashyb[i].second;
		  }
		    
		  return SegHyp.cen;	
	}
	
	public boolean checkClase(String str) {
		  if( cl2key.get(str)  == null )
			    return false;
			  return true;
	}

	public int keyClase(String str) {
		 if( cl2key.get(str) == null ) {
			    System.err.print(  "WARNING: Class '" + str + "' doesn't appear in symbols database\n" );
			    return -1;
		  }
		  return cl2key.get(str);
	}
	
	public int getNClases() {
		  return C;
	 }

 

    public void BLSTMclassification( Mdrnn  net, DataSequence  seq, Pair<Float,Integer>[]  claspr,   int NB ) {
		  //Classify sample with net
		  net.train( seq);
		  if( Helpers.verbose ) System.err.println("after..... train.......");
		  
		  //Get output layer and its shape
		  Layer  L = (Layer )net.outputLayers.get(0);
		  int NVEC=L.outputActivations.shape.at(0);
		  int NCLA=L.outputActivations.shape.at(1);
		  
		  Pair[] prob_class = new Pair[NCLA];
		  for(int i=0; i<NCLA; i++) {
			  prob_class[i] = new Pair<Float,Integer>();
		    prob_class[i].second = (int) cl2key.get( header_on.targetLabels.get(i) ); //targetLabels on = targetLabels off
		  }
		  for(int i=0; i<NCLA; i++)
		    prob_class[i].first = 0.0f;
		  
		  //Compute the average posterior probability per class
		  for(int nvec=0; nvec<NVEC; nvec++)
		    for(int ncla=0; ncla<NCLA; ncla++)
		      prob_class[ncla].first =   (float)prob_class[ncla].first + L.outputActivations.at(nvec*NCLA + ncla);
		  
		  for(int ncla=0; ncla<NCLA; ncla++)
		    prob_class[ncla].first = (float) prob_class[ncla].first / NVEC;
		
		  //Sort classification result by its probability
		  if( Helpers.verbose )  System.err.println("start..... sort.......");  
		  Arrays.sort(prob_class, new Comparator<Pair>(){ 
				@Override
				public int compare(Pair o1, Pair o2) {
					 float o11 = (float)o1.first;
					 int   o12 = (int)o1.second;
					 float o21 = (float)o2.first;
					 int   o22 = (int)o2.second;
				     if( o11 < o21 )
				    	 return 1;
				     if (o11 > o21 )
				    	 return -1;
				     if( o12 < o22 )
				    	 return 1;
				     if (o12 > o22 )
				    	 return -1;
				     return 0;
				     
				}}) ; 
		   
		  //Copy n-best to output vector
		  for(int i=0; i<NB; i++)
		    claspr[i] = prob_class[i];
		 
    }

}
