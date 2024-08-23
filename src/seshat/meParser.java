package seshat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import rnnlibjava.Helpers;
import seshat.Production.ProductionB;
import seshat.Production.ProductionT;

public class meParser {
	public static int NB = 10;
	  Grammar  G;

	  int   max_strokes;
	  float clusterF, segmentsTH;
	  float ptfactor, pbfactor, rfactor;
	  float qfactor, dfactor, gfactor, InsPen;

	  SymRec  sym_rec;
	  GMM gmm_spr;
	  DurationModel  duration;
	  SegmentationModelGMM  segmentation;

	  //Private methods
	  public   void loadSymRec(String config, boolean flag) {
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
			  if(!f.exists()) {
				  System.err.print("Error: loading config file '" + Util.toString(config) + "'\n");
				  throw new RuntimeException( "Error: loading config file '" + Util.toString(config) + "'\n"  );
			  } 
				try {
					scanner = new Scanner(f);
				} catch (FileNotFoundException e) { 
					  throw new RuntimeException( e);
				}
		  }
		
		 String auxstr = null;
		 String dur_path = null;
		 String seg_path = null;
		 while(scanner.hasNext()) {
			 String token = scanner.next().trim();
			 if( token.equals("Duration")) {
				 dur_path = scanner.next(); 
			 }
			 else if( token.equals("Segmentation")) {
				 seg_path = scanner.next();
			 }
			 else {
				 scanner.next();
			 }
		 }
 

		  if( dur_path == null ) {
		      System.err.print("Error:  Duration field not found in config fil '" + config + "'\n");
			  throw new RuntimeException( "Error:  Duration field not found in config fil'" + config+ "'\n"  );
		  }
		  if( seg_path == null) {
			  System.err.print("Error:  Segmentation field not found in config fil '" +  config + "'\n");
			  throw new RuntimeException( "Error:  Segmentation field not found in config fil'" + config + "'\n"  );
		  
		  }
		  
		  scanner.close();
		  
		  //Load symbol recognizer
		  try {
			sym_rec = new SymRec(config, flag);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		  //Load duration and segmentation model
		  duration     = new DurationModel(dur_path.toCharArray(),max_strokes,sym_rec, flag);
		  segmentation =   new SegmentationModelGMM(seg_path.toCharArray(), flag)  ;
	  }
	  public  int  tree2dot(OutputStream fd, Hypothesis  H, int id) {
		  int nid;

		  if(  H.pt == null) {
		    //Binary production
		    int a = H.prod.A;
		    int b = H.prod.B;
		    
		    Util.printEntry(fd,  G.key2str(H.ntid) + id + " -> " + G.key2str(a) +  (id+1) + " [label=" + H.prod.tipo() + "]\n");
		    
		     nid = tree2dot(fd, H.hi, id+1);
		     
		     Util.printEntry(fd,  G.key2str(H.ntid) + id + " -> " + G.key2str(b) +  nid + " [label=" + H.prod.tipo() + "]\n");
			   
		   
		    nid = tree2dot(fd, H.hd, nid);
		  }
		  else {
		    char[] aux = H.pt.getTeX(H.clase);
		    for(int i=0;  i < aux.length && aux[i] != 0; i++) {
		      if( aux[i] == '\\' )aux[i]='s';
		      if( aux[i] == '+' ) aux[i]='p';
		      if( aux[i] == '-' ) aux[i]='m';
		      if( aux[i] == '(' ) aux[i]='L';
		      if( aux[i] == ')' ) aux[i]='R';
		      if( aux[i] == '=' ) aux[i]='e';
		    }

		    //Terminal production
		    Util.printEntry(fd, "T" + Util.toString(aux) + id + " [shape=box,label=\"" + Util.toString( H.pt.getTeX(H.clase)) + "\"]\n");
		    Util.printEntry(fd,  G.key2str(H.pt.getNoTerm()) + id + " -> T" + Util.toString( aux ) + id + "\n");
		  
		    nid = id+1;
		  }

		  return nid;
	  }

	  public  void initCYKterms(Sample  M, TableCYK  tcyk, int N, int K) {

		  for(int i=0; i<M.nStrokes(); i++) {
		      
		    int cmy;
		    int[] asc = new int[1];
		    int[]des  = new int[1];
		    
		    System.out.println("Stroke " + i + ":\n");
		  
		      
		    int[] clase = new int[NB];
		    float[] pr = new float[NB];
		    
		    cmy = sym_rec.clasificar( M, i, NB, clase, pr, asc,  des );
		      
			if(Helpers.verbose)  System.err.println("after..... sym_rec.clasificar.......");
			  
		    CellCYK  cd = new CellCYK(G.noTerminales.size(), N);
		      
		    M.setRegion(cd, i);
		    
		    boolean insertar=false;
		    for( ProductionT   it : G.prodTerms ) {
		      ProductionT  prod =  it;
		      
		      for(int k=0; k<NB; k++) {
					if( pr[k] > 0.0 && prod.getClase( clase[k] ) && prod.getPrior(clase[k]) > -Float.MAX_VALUE ) {
					  
					  float prob = (float) (Math.log(InsPen) 
					    + ptfactor * prod.getPrior(clase[k])
					    + qfactor  * Math.log(pr[k])
					    + dfactor  * Math.log(duration.prob(clase[k],1)));
					  
					  if( cd.noterm[prod.getNoTerm()] != null ) {
					    if( cd.noterm[prod.getNoTerm()].pr > prob + prod.getPrior(clase[k]))
					      continue;
					    else
					      cd.noterm[prod.getNoTerm()] = null;  
					  }
					  
					  insertar=true;
					  
					  //Create new symbol
					  cd.noterm[prod.getNoTerm()] = new Hypothesis(clase[k], prob, cd, prod.getNoTerm() );
					  cd.noterm[prod.getNoTerm()].pt = prod;
					  
					  //Compute the vertical centroid according to the type of symbol
					  int cen, type = sym_rec.symType(clase[k]);
					  if( type==0 )       cen = cmy; //Normal
					  else if ( type==1 ) cen = asc[0]; //Ascendant
					  else if ( type==2 ) cen = des[0]; //Descendant
					  else                cen = (int) ((cd.t+cd.y)*0.5); //Middle point
					  
					  //Vertical center
					  cd.noterm[prod.getNoTerm()].lcen = cen;
					  cd.noterm[prod.getNoTerm()].rcen = cen;
					}
		      }
		      
		    }
		    
		    if( insertar ) {
		   
		    if( Util.debug ) {
		    	   for(int j=0; j<K; j++) {
		   			if( cd.noterm[j] != null ) {
		   				System.out.println(  Util.padding( sym_rec.strClase(cd.noterm[j].clase), 12)
		   						+ " [" +  G.key2str(j) + "] " + Math.exp(cd.noterm[j].pr) );
		   			 	}
		   		    }
		    } 
		      
		      //Add to parsing table (size=1)
		      tcyk.add(1, cd, -1, G.esInit);
		    } 
		  }
	  }

	  public  void combineStrokes(Sample  M, TableCYK  tcyk, LogSpace[] LSP, int N) {
		  if( N<=1 ) return;

		  int[] asc = new int[1];
		  int[] des = new int[1];
		  int cmy;
		  int[] clase = new int[NB];
		  float[] pr = new float[NB];
		  int ntested=0;

		  //Set distance threshold
		  float distance_th = segmentsTH;

		  //For every single stroke
		  for(int stkc1=1; stkc1<N; stkc1++) {

		    CellCYK  c1 = new CellCYK(G.noTerminales.size(), N);
		    M.setRegion(c1, stkc1);
		    
		    for(int size=2; size<=Math.min(max_strokes,N); size++) {
		      
		      List<Integer> close_list = new ArrayList<Integer>();

		      //Add close and visible strokes to the closer list
		      if( size==2 ) {
			
			  	for(int i=0; i<stkc1; i++) {
				  if( M.getDist(stkc1, i) < distance_th )
				    close_list.add(i);
			  	}
		      }
		      else {
		     	M.get_close_strokes( stkc1,  close_list, distance_th );
		      }
		      //If there are not enough strokes to compose a hypothesis of "size", continue
		      if( (int)close_list.size() < size-1 )
			      continue;
		      
		      int[]  stkvec = new int[close_list.size()];
		      int VS=0;
		      for(Integer it : close_list )
		      	  stkvec[VS++] =  it;
		      
		      Arrays.sort(stkvec);
		      
		      
		      for(int i=size-2; i<VS; i++) {
		    	List<Integer> stks_list = new ArrayList<Integer>();
			
			//Add stkc1 and current stroke (ith)
			stks_list.add( stkvec[i] );
			stks_list.add( stkc1 );
			
			//Add strokes up to size
			for(int j=i-(size-2); j<i; j++)
			  stks_list.add( stkvec[j] );
			
			//Sort list (stroke's order is important in online classification)
			Collections.sort(stks_list);
			 
			
			CellCYK  cd = new CellCYK(G.noTerminales.size(), N);
			M.setRegion(cd,  stks_list);
			
			//Print hypothesis information
			if(  Util.debug ) {
				System.out.print( "Multi-stroke (" + size + ") hypothesis: {"  );
				for(Integer it : stks_list  )
					System.out.print( " " + it); 
				System.out.print(" }\n");
			}
			
			float seg_prob = segmentation.prob(cd,M);
			
			cmy = sym_rec.clasificar(M,  stks_list, NB, clase, pr, asc,  des);
			
			ntested++;
			
			//Add to parsing table
			boolean insertar=false;
			for( ProductionT  it : G.prodTerms ) {
			  ProductionT  prod =  it;
			  
			  for(int k=0; k<NB; k++)
			    if( pr[k] > 0.0 && prod.getClase( clase[k] ) && prod.getPrior(clase[k]) > -Float.MAX_VALUE ) {
			      
			      float prob = (float) (Math.log(InsPen)
				+ ptfactor * prod.getPrior(clase[k])
				+ qfactor  * Math.log(pr[k])
				+ dfactor  * Math.log( duration.prob(clase[k],size) )
				+ gfactor  * Math.log( seg_prob ));

			      if( cd.noterm[prod.getNoTerm()] != null ) {
					if( cd.noterm[prod.getNoTerm()].pr > prob )
					  continue;
					else
					    cd.noterm[prod.getNoTerm()] = null;
			      }
			      
			      insertar=true;
			      
			      cd.noterm[prod.getNoTerm()] = new Hypothesis(clase[k], prob, cd, prod.getNoTerm() );
			      cd.noterm[prod.getNoTerm()].pt = prod;
			      
			      int cen, type = sym_rec.symType(clase[k]);
			      if( type==0 )       cen = cmy; //Normal
			      else if ( type==1 ) cen = asc[0]; //Ascendant
			      else if ( type==2 ) cen = des[0]; //Descendant
			      else                cen = (int) ((cd.t+cd.y)*0.5); //Middle point
			      
			      //Vertical center
			      cd.noterm[prod.getNoTerm()].lcen = cen;
			      cd.noterm[prod.getNoTerm()].rcen = cen;
			    }
			}
			
			if( insertar ) {
              if( Util.debug ) {
    			  for(int j=0; j<cd.nnt; j++) {
    				    if( cd.noterm[j] != null) {
    				    	System.out.println(  Util.padding(sym_rec.strClase(cd.noterm[j].clase),12 ) +
    				    			" [" +   G.key2str(j) + "] " + Math.exp(cd.noterm[j].pr));
    				   
    				    }
    				  }
              }

			  tcyk.add(size, cd, -1, G.esInit);

			}
//			else
//			  delete cd;
			
	         }//end for close_list (VS)
		      
		   //   delete[] stkvec;
		      
		    }//end for size
		    
		  }//end for stroke stkc1

	  }
	  
	  public  CellCYK  fusion(Sample  M, ProductionB  pd, Hypothesis  A, Hypothesis B, 
			  int N, double prob) {
		  CellCYK  S=null;

		  if( !A.parent.compatible(B.parent) || pd.prior == -Float.MAX_VALUE )
		    return S;

		  //Penalty according to distance between strokes
		  float grpen;

		  if( clusterF > 0.0 ) {

		    grpen = M.group_penalty(A.parent, B.parent);
		    //If distance is infinity . not visible
		    if( grpen >= M.INF_DIST )
		      return null;
		    
		    //Compute penalty
		    grpen = (float) (1.0/(1.0 + grpen));
		    grpen = (float) Math.pow( grpen, clusterF );
		  }
		  else
		    grpen = 1.0f;

		  //Get nonterminal
		  int ps = pd.S;
		  
		  //Create new cell
		  S = new CellCYK(G.noTerminales.size(), N);
		  
		  //Compute the (log)probability
		  prob = pbfactor * pd.prior + rfactor * Math.log(prob * grpen) + A.pr + B.pr;
		  
		  //Copute resulting region
		  S.x =  Math.min(A.parent.x, B.parent.x);
		  S.y =  Math.min(A.parent.y, B.parent.y);
		  S.s =  Math.max(A.parent.s, B.parent.s);
		  S.t =  Math.max(A.parent.t, B.parent.t);
		  
		  //Set the strokes covered
		  S.ccUnion(A.parent,B.parent);
		  
		  int clase=-1;
		  if( !pd.check_out() && sym_rec.checkClase( pd.get_outstr2() ) )
		    clase = sym_rec.keyClase( pd.get_outstr2() );
		  
		  //Create hypothesis
		  S.noterm[ps] = new Hypothesis(clase, prob, S, ps);
		  
		  pd.mergeRegions(A, B, S.noterm[ps]);
		  
		  //Save the tree path
		  S.noterm[ps].hi = A;
		  S.noterm[ps].hd = B;
		  S.noterm[ps].prod = pd;
		  
		  //Special treatment for binary productions that compose terminal symbols (e.g. Equal --V-. Hline Hline)
		  if( clase >= 0 ) {
		    for( ProductionT  it : G.prodTerms ) {
		      ProductionT  prod =  it;
		      
		      if( prod.getClase( clase ) && prod.getPrior(clase) > -Float.MAX_VALUE ) {
			S.noterm[ps].pt = prod;
			break;
		      }
		    }
		  }

		  return S;
	  }

	 public  meParser(String config) {
		 
		  File f = new File(Util.toString(config));
		  if(!f.exists()) {
			  System.err.print("Error: loading config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error: loading config file '" + Util.toString(config) + "'\n"  );
		  }
	 
		  Scanner scanner = null;
			try {
				scanner = new Scanner(f);
			} catch (FileNotFoundException e) { 
				  throw new RuntimeException( e);
			}
			parse(scanner, config, false);
	 }
	 public  meParser(String config, boolean flag) {
		 config = config.trim();
		 InputStream is = meParser.class.getResourceAsStream(config);
		 
		   Scanner scanner = null;
			try {
				scanner = new Scanner(is);
			} catch ( Exception e) { 
				  throw new RuntimeException( e);
			}
			parse(scanner, config, true);
	 }
	 
	 public void parse(Scanner scanner, String config, boolean flag) {
		  //Read configuration file
		  String auxstr= null;
		  String path = null;

		  clusterF = -1;
		  segmentsTH = -1;
		  max_strokes = -1;
		  ptfactor = -1;
		  pbfactor = -1;
		  qfactor = -1;
		  dfactor = -1;
		  gfactor = -1;
		  rfactor = -1; 
		  gmm_spr = null;

		  
		  while(scanner.hasNext()) {
			 auxstr = scanner.next();
		    if(  auxstr.startsWith("GRAMMAR") )
		    	path = scanner.next(); //Grammar path
		    else if(  auxstr.startsWith("MaxStrokes") ) {
		      auxstr = scanner.next(); //Info
		      max_strokes = Integer.parseInt( auxstr );
		    }
		    else if(  auxstr.startsWith( "SpatialRels") ) {
		      auxstr = scanner.next(); //Info
		      gmm_spr =  new GMM(auxstr,flag)  ;
		    }
		    else if(  auxstr.startsWith( "InsPenalty") ) {
		       auxstr = scanner.next(); //Info
		      InsPen = Float.parseFloat( auxstr );;
		    }
		    else if( auxstr.startsWith("ClusterF") ) {
		    	 auxstr = scanner.next(); //Info
		    	 clusterF =  Float.parseFloat( auxstr );; 
		    }
		    else if( auxstr.startsWith("SegmentsTH") ) {
		    	 auxstr = scanner.next(); //Info
		    	 segmentsTH = Float.parseFloat( auxstr );; 
		       
		    }
		    else if( auxstr.startsWith("ProductionTSF") ) {
		    	 auxstr = scanner.next(); //Info
		    	 ptfactor = Float.parseFloat( auxstr );; 
		      
		    }
		    else if(auxstr.startsWith("ProductionBSF") ) {
		    	auxstr = scanner.next(); //Info
		    	pbfactor = Float.parseFloat( auxstr );; 
		       
		    }
		    else if( auxstr.startsWith("RelationSF") ) {
		    	auxstr = scanner.next(); //Info
		    	rfactor = Float.parseFloat( auxstr );; 
		       
		    }
		    else if( auxstr.startsWith("SymbolSF") ) {
		    	auxstr = scanner.next(); //Info
		    	qfactor = Float.parseFloat( auxstr );; 
		       
		    }
		    else if(auxstr.startsWith("DurationSF") ) {
		    	auxstr = scanner.next(); //Info
		    	dfactor = Float.parseFloat( auxstr );; 
		       
		    }
		    else if( auxstr.startsWith("SegmentationSF") ) {
		    	auxstr = scanner.next(); //Info
		    	gfactor = Float.parseFloat( auxstr );;
		       
		    }
		    else
		    	auxstr = scanner.next(); //Info
		    
		   // auxstr = scanner.next(); //Next field id
		  }

		  if( path == null) {
			  System.err.print(" Error: GRAMMAR field not found in config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error: GRAMMAR field not found in config file  '" + Util.toString(config) + "'\n"  );
			 
		  }

		  if( gmm_spr == null ) {
			  System.err.print(" Error:  Loading GMM modelin config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error:  Loading GMM model in config file  '" + Util.toString(config) + "'\n"  );
			 
		  }

		  if( max_strokes <= 0 || max_strokes > 10 ) {
			  System.err.print(" Error:  Wrong MaxStrokes value config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error:  Wrong MaxStrokes valuein config file  '" + Util.toString(config) + "'\n"  );
		   }

		  if( clusterF < 0 ) {
			  System.err.print(" Error:  Wrong ClusterF value config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error:  Wrong ClusterF valuein config file  '" + Util.toString(config) + "'\n"  );
		   
		  }

		  if( segmentsTH <= 0 ) {
			  System.err.print(" Error:  Wrong SegmentsTH value config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error:  Wrong SegmentsTH valuein config file  '" + Util.toString(config) + "'\n"  );
		  
		  }

		  if( InsPen <= 0 ) {
			  System.err.print(" Error:  Wrong InsPenalty value config file '" + Util.toString(config) + "'\n");
			  throw new RuntimeException( "Error:  Wrong InsPenalty valuein config file  '" + Util.toString(config) + "'\n"  );
		   
		  }

		  if( qfactor <= 0 )
			  System.err.print(" WARNING:    SymbolSF value config file '" + Util.toString(config) + "'\n");
		  
		  if( ptfactor <= 0 )
			  System.err.print(" Error:    ProductionTSF value config file '" + ptfactor+ "'\n");
		   
		  if( pbfactor <= 0 )
			  System.err.print(" Error:    ProductionBSF value config file '" + pbfactor+ "'\n");
		   
		  if( rfactor <= 0 )
			  System.err.print(" Error:    RelationSF value config file '" + rfactor+ "'\n");
		    
		  if( dfactor < 0 )
			  System.err.print(" Error:    DurationSF value config file '" + dfactor+ "'\n");
		   
		  if( gfactor < 0 )
			  System.err.print(" Error:    SegmentationSF value config file '" + gfactor+ "'\n");
		   

		  //Read grammar path
		  //FIXME ??
		 // path = scanner.next();
		  path =  path.trim();
	 

		  //Load symbol recognizer
		  loadSymRec(config, flag);

		  //Load grammar
		  G = new Grammar(path, sym_rec, flag);
	 }
	 
	  //Parse math expression
	 public String parse_me(Sample  M) {

		  M.setSymRec( sym_rec );

		  //Compute the normalized size of a symbol for sample M
		  M.detRefSymbol();

		  int N = M.nStrokes();
		  int K = G.noTerminales.size();

		  //Cocke-Younger-Kasami (CYK) algorithm for 2D-SCFG
		  TableCYK tcyk = new TableCYK( N, K );

		 if( Util.debug ) System.out.print("CYK table initialization:\n");
		  initCYKterms(M,  tcyk, N, K);

		  //Compute distances and visibility among strokes
		  M.compute_strokes_distances(M.RX, M.RY);

		  //Spatial structure for retrieving hypotheses within a certain region
		  LogSpace[] logspace = new LogSpace[N];
		  List<CellCYK> c1setH = new ArrayList<CellCYK>();
		  List<CellCYK> c1setV = new ArrayList<CellCYK>();
		  List<CellCYK> c1setU = new ArrayList<CellCYK>();
		  List<CellCYK> c1setI = new ArrayList<CellCYK>();
		  List<CellCYK> c1setM = new ArrayList<CellCYK>();
		  List<CellCYK> c1setS = new ArrayList<CellCYK>();
			 
		  SpaRel SPR = new SpaRel(gmm_spr, M);

		  //Init spatial space for size 1
		  logspace[1] = new LogSpace(tcyk.get(1), tcyk.size(1), M.RX, M.RY);

		  //Init the parsing table with several multi-stroke symbol segmentation hypotheses
		  combineStrokes(M,  tcyk, logspace, N);
		  
		if(Util.debug)  System.out.print("\nCYK parsing algorithm\n");
		if(Util.debug)   System.out.print("Size 1: Generated " + tcyk.size(1) + "\n" );
		  
		  //CYK algorithm main loop
		  for(int talla=2; talla<=N; talla++) {

		    for(int a=1; a<talla; a++) {
		      int b = talla-a;

		      for(CellCYK  c1=tcyk.get(a); c1 != null; c1=c1.sig) {
				//Clear lists
				c1setH.clear();
				c1setV.clear();
				c1setU.clear();
				c1setI.clear();
				c1setM.clear();
				c1setS.clear();
	
				//Get the subset of regions close to c1 according to different spatial relations
				logspace[b].getH(c1,  c1setH); //Horizontal (right)
				logspace[b].getV(c1,  c1setV); //Vertical (down)
				logspace[b].getU(c1,  c1setU); //Vertical (up)
				logspace[b].getI(c1,  c1setI); //Inside (sqrt)
				logspace[b].getM(c1, c1setM); //mroot (sqrt[i])

			for( CellCYK  c2 : c1setH ) {

			  for( ProductionB  it : G.prodsH ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;

			    if( c1.noterm[ pa ] != null && ( c2).noterm[ pb ] != null ) {
			      double cdpr = SPR.getHorProb(c1.noterm[pa], ( c2).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M,  it, c1.noterm[ pa ], ( c2).noterm[ pb ], M.nStrokes(), cdpr);

			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null ) {
				    tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table (size=talla)
			      }
			      else {
				    tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
			    }
			  }

			  for( ProductionB  it : G.prodsSup ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;
			      
			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;
			      
			    if( c1.noterm[ pa ] != null && ( c2).noterm[ pb ] != null) {
			      double cdpr = SPR.getSupProb(c1.noterm[pa], ( c2).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;
				
			      CellCYK  cd = fusion(M,  it, c1.noterm[ pa ], ( c2).noterm[ pb ], M.nStrokes(), cdpr);
				  
			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null ) {
			    	tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				    tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
			      
			    }
			  }


			  for( ProductionB  it : G.prodsSub ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;

			    if( c1.noterm[ pa ] != null && ( c2).noterm[ pb ] != null ) {
			      double cdpr = SPR.getSubProb(c1.noterm[pa], ( c2).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;
		      
			      CellCYK cd = fusion(M,  it, c1.noterm[ pa ], ( c2).noterm[ pb ], M.nStrokes(), cdpr);

			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null ) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
			      
			      
			    }
			  }

			}//end c2=c1setH

			for( CellCYK  c2 : c1setV ) {
			  
			  for( ProductionB   it : G.prodsV ) {
			    if(   it.prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;

			    if( c1.noterm[ pa ] != null && ( c2).noterm[ pb ] != null ) {
			      double cdpr = SPR.getVerProb(c1.noterm[pa], (c2 ).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M,  it, c1.noterm[ pa ], ( c2).noterm[ pb ], M.nStrokes(), cdpr);

			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null )
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      else
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			    }
			  }

			  //prodsVe
			  for( ProductionB  it : G.prodsVe ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;

			    if( c1.noterm[ pa ] != null && ( c2).noterm[ pb ]  != null ) {
			      double cdpr = SPR.getVerProb(c1.noterm[pa], ( c2).noterm[ pb ], true);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M,  it, c1.noterm[ pa ], ( c2).noterm[ pb ], M.nStrokes(), cdpr);

			      if(  cd == null) continue;

			      if( cd.noterm[ps]  != null ) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else  {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
				
			    }
			  }

			}//for in c1setV


			for( CellCYK c2 : c1setU ) {
			  
			  for( ProductionB it : G.prodsV ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB)it).S;
			    int pa = ((ProductionB)it).A;
			    int pb = ((ProductionB)it).B;
			    
			    if( c1.noterm[ pb ]  != null  && (c2).noterm[ pa ]  != null ) {
			      double cdpr = SPR.getVerProb((c2).noterm[pa], c1.noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M, it, (c2).noterm[ pa ], c1.noterm[ pb ], M.nStrokes(), cdpr);
				
			      if(  cd == null ) continue;

			      if( cd.noterm[ps]  != null ) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }

			    }
			  }


			  //ProdsVe
			  for(ProductionB it: G.prodsVe ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB ) it).S;
			    int pa = ((ProductionB ) it).A;
			    int pb = ((ProductionB ) it).B;
			    
			    if( c1.noterm[ pb ]  != null && ( c2).noterm[ pa ]  != null ) {
			      double cdpr = SPR.getVerProb(( c2).noterm[pa], c1.noterm[ pb ], true);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M,  it, ( c2).noterm[ pa ], c1.noterm[ pb ], M.nStrokes(), cdpr);
				
			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null ) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }

			    }
			  }

			}

			for( CellCYK  c2 : c1setI ) {
			  
			  for( ProductionB  it : G.prodsIns ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB)it).S;
			    int pa = ((ProductionB)it).A;
			    int pb = ((ProductionB)it).B;
			      
			    if( c1.noterm[ pa ]  != null && (c2).noterm[ pb ]  != null ) {
			      double cdpr = SPR.getInsProb(c1.noterm[pa], (c2).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK  cd = fusion(M, it, c1.noterm[ pa ], (c2).noterm[ pb ], M.nStrokes(), cdpr);
				
			      if(  cd == null ) continue;

			      if( cd.noterm[ps]  != null) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
				
			    }
			  }
			}


			//Mroot
			for(CellCYK c2 : c1setM ) {
			  
			  for( ProductionB  it : G.prodsMrt ) {
			    if( ( it).prior == -Float.MAX_VALUE ) continue;

			    //Production S . A B
			    int ps = ((ProductionB)it).S;
			    int pa = ((ProductionB)it).A;
			    int pb = ((ProductionB)it).B;
			      
			    if( c1.noterm[ pa ]  != null && (c2).noterm[ pb ]   != null ) {
			      double cdpr = SPR.getMrtProb(c1.noterm[pa], (c2).noterm[ pb ]);
			      if( cdpr <= 0.0 ) continue;

			      CellCYK cd = fusion(M, it, c1.noterm[ pa ], (c2).noterm[ pb ], M.nStrokes(), cdpr);
				
			      if(  cd == null ) continue;

			      if( cd.noterm[ps] != null ) {
				tcyk.add(talla, cd, ps, G.esInit); //Add to parsing table
			      }
			      else {
				tcyk.add(talla, cd, -1, G.esInit); //Add to parsing table
			      }
				
			    }
			  }
			}
			//End Mroot


			//Look for combining {x_subs} y {x^sups} in {x_subs^sups}
			for(int pps=0; pps<c1.nnt; pps++) {

			  //If c1.noterm[pa] is a Hypothesis of a subscript (parent_son)
			  if( c1.noterm[pps]  != null && c1.noterm[pps].prod  != null && c1.noterm[pps].prod.tipo() == 'B' ) {

			    logspace[b+c1.noterm[pps].hi.parent.talla].getS(c1,  c1setS); //sup/sub-scripts union
			    
			    for( CellCYK  c2 : c1setS ) {
			      
			      if( ( c2).x == c1.x && c1 !=  c2 ) {
				
				for( ProductionB it : G.prodsSSE ) {
				  if( ( it).prior == -Float.MAX_VALUE ) continue;
				  
				  //Production S . A B
				  int ps = ((ProductionB)it).S;
				  int pa = ((ProductionB)it).A;
				  int pb = ((ProductionB)it).B;
				  
				  if( c1.noterm[pa]  != null && (c2).noterm[pb]   != null
				      && c1.noterm[pa].prod  != null && (c2).noterm[pb].prod  != null
				      && c1.noterm[pa].hi == (c2).noterm[pb].hi
				      && c1.noterm[pa].prod.tipo() == 'B'
				      && (c2).noterm[pb].prod.tipo() == 'P'
				      && c1.noterm[pa].hd.parent.compatible( (c2).noterm[pb].hd.parent ) ) {
				    
				    
				    //Subscript and superscript should start almost vertically aligned
				    if( Math. abs(c1.noterm[pa].hd.parent.x - ( c2).noterm[pb].hd.parent.x) > 3*M.RX ) continue;
				    //Subscript and superscript should not overlap
				    if( Math.max(( it).solape(c1.noterm[pa].hd, ( c2).noterm[pb].hd),
					    ( it).solape(( c2).noterm[pb].hd, c1.noterm[pa].hd) ) > 0.1 ) continue;
				    
				    float prob = (float) (c1.noterm[pa].pr + ( c2).noterm[pb].pr - c1.noterm[pa].hi.pr);
				    
				    CellCYK  cd = new CellCYK(G.noTerminales.size(), M.nStrokes() );
				    
				    cd.x = Math.min(c1.x, ( c2).x);
				    cd.y = Math.min(c1.y, ( c2).y);
				    cd.s = Math.max(c1.s, ( c2).s);
				    cd.t = Math.max(c1.t, ( c2).t);
				    
				    cd.noterm[ ps ] = new Hypothesis(-1, prob, cd, ps);
				    
				    cd.noterm[ ps ].lcen = c1.noterm[pa].lcen;
				    cd.noterm[ ps ].rcen = c1.noterm[pa].rcen;
				    cd.ccUnion(c1,( c2));
				    
				    cd.noterm[ ps ].hi = c1.noterm[pa];
				    cd.noterm[ ps ].hd = ( c2).noterm[pb].hd;
				    cd.noterm[ ps ].prod =  it;
				    //Save the production of the superscript in order to recover it when printing the used productions
				    cd.noterm[ ps ].prod_sse = ( c2).noterm[pb].prod;
				    
				    tcyk.add(talla, cd, ps, G.esInit);
				  }
				}
			      }

			    }//end for c2 in c1setS

			    c1setS.clear();
			  }
			}//end for(int pps=0; pps<c1.nnt; pps++)


		      } //end for(CellCYK *c1=tcyk.get(a); c1; c1=c1.sig)

		    } //for 1 <= a < talla
		    
		    if( talla < N ) {
		      //Create new logspace structure of size "talla"
		      logspace[talla] = new LogSpace(tcyk.get(talla), tcyk.size(talla), M.RX, M.RY);
		    }

		    if(Util.debug)  System.out.print("Size " + talla + ": Generated " + tcyk.size(talla) + "\n" );

		    if( Util.debug ) {
//				#ifdef VERBOSE
			    for(CellCYK  cp=tcyk.get(talla); cp !=null ; cp=cp.sig) {
			    	System.out.print("  (" + Util.formatInt(cp.x, 3)  + "," + 
			    			Util.formatInt(cp.y, 3)  + ")-(" +
			    			Util.formatInt(cp.s, 3) +"," + 
			    			Util.formatInt(cp.t, 3) + ") { ");
			  //    printf("  (%3d,%3d)-(%3d,%3d) { ", cp.x, cp.y, cp.s, cp.t);
			      for(int i=0; i<cp.nnt; i++) {
			      	 if( cp.noterm[i] != null)
			      		 System.out.print( cp.noterm[i].pr + "[" + G.key2str(i) + "] " );
			      	//	 printf("%g[%s] ", cp.noterm[i].pr, G.key2str(i));
			      }
			      System.out.print("}\n");
			    }
			    System.out.print("\n");
//			#endif
		    } 

		  } //for 2 <= talla <= N


		  //Free memory
	 

		  //Get Most Likely Hypothesis
		  Hypothesis  mlh = tcyk.getMLH();

		  if(  mlh == null ) {
			  System.err.print("\nNo hypothesis found!!\n");
			  throw new RuntimeException( "\nNo hypothesis found!!\n"  ); 
			 
		  }

		  System.out.print("\nMost Likely Hypothesis (" + mlh.parent.talla + " strokes)\n\n" );

		  System.out.print("Math Symbols:\n");
		  print_symrec(mlh);
		  System.out.print("\n");

		  StringBuilder sb = new StringBuilder();
		  
		  
		  System.out.print("LaTeX:\n");
		  StringWriter sw = new StringWriter();
		  PrintWriter pw = new PrintWriter(sw);
		  print_latex( mlh , pw);

		  System.out.print( sw.toString() );
		  //Save InkML file of the recognized expression
		//ni  M.printInkML( G, mlh );

		//ni  if( M.getOutDot() != null )
		//ni    save_dot( mlh, M.getOutDot() );
		  
		  return sw.toString();
	  }
	  
	  //Output formatting methods
	 public  void print_symrec(Hypothesis  H) {
		 if(  H.pt == null ) {
			    print_symrec( H.hi );
			    print_symrec( H.hd );
			  }
			  else {
			    char[] clatex = H.pt.getTeX( H.clase );
			    
			    System.out.print( Util.toString(clatex) + " {");
			    
			    for(int i=0; i<H.parent.nc; i++)
			      if( H.parent.ccc[i] )
				System.out.print(" " + i);
			    System.out.print(" }\n");
			  }
	  }
	 public  void print_latex(Hypothesis  H, PrintWriter pw) {
		 //printf("\\displaystyle ");
		  if(  H.pt == null )
		    H.prod.printOut( G, H, pw );
		  else {
		   char[] clatex = H.pt.getTeX( H.clase );
		    pw.print( Util.toString( clatex ) );
		  }
		  pw.print("\n");
	  }
	 public   void save_dot( Hypothesis  H, char[] outfile ) {
		  File file = new File(Util.toString(outfile));
		  if( !file.exists() ) {
			  System.err.print( "Error creating '" + Util.toString(outfile)+ "' file\n");
		  } 
		  else {
			 OutputStream fd = null;
			try {
				fd = new FileOutputStream(file);
				  Util.printEntry(fd, "digraph mathExp{\n");
				    tree2dot(fd, H, 0);
				    Util.printEntry(fd, "}\n");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				 try {
					fd.close();
				} catch (IOException e) { 
					e.printStackTrace();
				}
			}
		   
		  } 
	  }
}
