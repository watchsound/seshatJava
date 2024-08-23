package seshat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import seshat.Production.ProductionB;
import seshat.Production.ProductionT;

public class Grammar {
	public static final float MIN_SPR_PR = 0.01f;
	
	 public  Map<String,Integer> noTerminales = new HashMap<>();
	 public List<Integer> initsyms = new ArrayList<>();
	 public boolean[] esInit = new boolean[0];
	 public  SymRec sym_rec;

	 public List<ProductionB> prodsH = new ArrayList<>();
	 public List<ProductionB> prodsSup = new ArrayList<>();
	 public List<ProductionB> prodsSub = new ArrayList<>(); 
		
	 public List<ProductionB> prodsV = new ArrayList<>();
	 public List<ProductionB> prodsVe = new ArrayList<>();
	 public List<ProductionB> prodsIns = new ArrayList<>();
	 public List<ProductionB> prodsMrt = new ArrayList<>();
	 public List<ProductionB> prodsSSE = new ArrayList<>();
				
	 public List<ProductionT> prodTerms = new ArrayList<>();
		 

	 public static void error(String msg) {
		  System.err.print ( "Grammar err[" + msg + "]\n" );
		  throw new RuntimeException(  "Grammar err[" + msg + "]\n"  );
	 }

	 public static	void error(String msg, char[]str) {
		 error(msg);
		 //FIXME???
//		  char linea[1024];
//		  sprintf(linea, "Grammar err[%s]\n", msg);
//		  fprintf(stderr, linea, str);
//		  exit(-1);
		}

	 
	 
	 public Grammar(String path, SymRec  sr, boolean flag) {
		  //Load grammar file
		 path = path.trim();
		  
		 BufferedReader breader = null;
		 if( flag ) {
			  try {
		    	  if( !path.startsWith("/"))
		    		  path = "/" + path;
		    	  InputStream is = meParser.class.getResourceAsStream(path);
		 		  
					try {
						breader = new BufferedReader(new InputStreamReader(is));
					} catch ( Exception e) { 
						  throw new RuntimeException( e);
					}
					 
		      }catch(Exception ex) {
		    	  throw new RuntimeException( ex );  
		      } 	 
		 } else {
			  File fd = new File(path);
			  if( !fd.exists() ) {
				  System.err.println("Error loading grammar '"+ path + "' \n"  );
			      throw new RuntimeException(" Error loading grammar '"+ path + "'\n");
			  }
			  try {
				  breader = new BufferedReader(new FileReader(fd));
				} catch (FileNotFoundException e) {
				   throw new RuntimeException(e);
				}
		 }
		 

		  //Get the path's prefix to determine relative locations
		  int i = path.length()-1;
		  while( i>=0 && path.charAt(i) != '/' )
		    i--;

		  String subPath = path.substring(0,i);

		  //Save the symbol recognizer to convert between LaTeX and symbol id
		  sym_rec = sr;

		  gParser GP = new gParser(this, breader, subPath);

		  

		  esInit = new boolean[noTerminales.size()];
		  for( i=0; i<(int)noTerminales.size(); i++)
		    esInit[i] = false;

		  for(Integer p : initsyms) 
		    esInit[ p ] = true;
	 }
	 
	 public void destructor() {}

	 public final String key2str(int k) {
		  for(Entry<String, Integer> e : noTerminales.entrySet()) {
			  if( e.getValue() == k )
				  return e.getKey();
		  }
		  return "NULL";
	 }
	 
	 public  void addInitSym(String str) {
		  if( noTerminales.get(str)  == null )
			  System.err.println("addInitSym: Non-terminal '" + str + "' not defined.");

		   initsyms.add( noTerminales.get( str ) );
	 }
	 
	 public void addNoTerminal(String str) {
		  int key = noTerminales.size();
		  noTerminales.put(str, key);
	 }
	 
	 public void addTerminal(float pr, String S, char[] ts, char[] tex) {
		  if( noTerminales.get(S) == null )
			  System.err.println("addInitSym: Non-terminal '" + S + "' not defined.");

		  String tsst = Util.toString(ts);
			  boolean create=true;
			  for( ProductionT it : prodTerms) { 
			    if( it.getNoTerm() == noTerminales.get(S)) {
                   int id=sym_rec.keyClase(tsst);
			      if( id >= 0 )
				    it.setClase(id , pr, tex, 'i');
			      else
				    System.err.print( "ERROR: " + S + " -> " + tsst + " (id < 0)\n");

			      create = false;
			      break;
			    }
			  }

			  if( create ) {
			    ProductionT pt = new ProductionT(noTerminales.get(S), sym_rec.getNClases());

			    int id=sym_rec.keyClase(tsst);
			    if( id >= 0 )
				   pt.setClase(id , pr, tex, 'i');
			    else
			       System.err.print( "ERROR: " + S + " -> " + tsst + " (id < 0)\n");
			    
			    prodTerms.add( pt );
			  }
	 }

	 public void addRuleH(float pr, String S, String A, String B, char[] out, char[] merge) {
		      if( noTerminales.get(S) == null )
			     error("Rule: Non-terminal '" + S + "' not defined." );
			  if( noTerminales.get(A) == null )
				  error("Rule: Non-terminal '" + A + "' not defined." );
			  if( noTerminales.get(B) == null )
				  error("Rule: Non-terminal '" + B + "' not defined." );
			  
			  ProductionB  pd = new Production.ProductionH(noTerminales.get(S),
							    noTerminales.get(A), noTerminales.get(B), pr, out);

			  pd.setMerges( merge[0] );

			  prodsH.add( pd );
	 }
	 public  void addRuleV(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionV(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsV.add( pd );
	 }
	 public  void addRuleVe(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionVe(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsVe.add( pd );
	 }
	 
	 
	 public  void addRuleSup(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionSup(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsSup.add( pd );
	 }
	 public  void addRuleSub(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionSub(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsSub.add( pd );
	 }
	 
	 
	 public  void addRuleSSE(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionSSE(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsSSE.add( pd );
	 }
	 public  void addRuleIns(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionIns(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsIns.add( pd );
		 
	 }
	 public  void addRuleMrt(float pr, String S, String A, String B, char[] out, char[] merge) {
	      if( noTerminales.get(S) == null )
		     error("Rule: Non-terminal '" + S + "' not defined." );
		  if( noTerminales.get(A) == null )
			  error("Rule: Non-terminal '" + A + "' not defined." );
		  if( noTerminales.get(B) == null )
			  error("Rule: Non-terminal '" + B + "' not defined." );
		  
		  ProductionB  pd = new Production.ProductionMrt(noTerminales.get(S),
						    noTerminales.get(A), noTerminales.get(B), pr, out);

		  pd.setMerges( merge[0] );

		  prodsMrt.add( pd );
		 
	 }
}
