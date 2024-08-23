package seshat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Production {
	
	public static final double PSOLAP = 0.75;
	public static final double PENALTY = 0.05;
	
	//Binary productions of the grammar (2D-PCFG)
	public abstract static class ProductionB{
	  protected String  outStr;
	  protected char merge_cen;

	 public  int S;
	 public int A, B;
	 public float prior;

	 public ProductionB(int s, int a, int b) {
		  S = s;
		  A = a;
		  B = b;
		  outStr = null;
	 }
	 public ProductionB(int s, int a, int b, float pr, char[]  out) {
		 S = s;
		  A = a;
		  B = b;
		  prior = pr > 0.0 ? (float) Math.log(pr) : -Float.MAX_VALUE;

		  setMerges('C');

		  outStr = Util.toString(out);
		//  outStr = new char[out.length+1];
		//  System.arraycopy(out, 0, outStr, 0, out.length);
		  //strcpy(outStr, out); 
	 };
	 public void destructor() {
		 
	 }

	 public float solape(Hypothesis  a, Hypothesis  b) {
		  int x = Math.max(a.parent.x, b.parent.x);
		  int y = Math.max(a.parent.y, b.parent.y);
		  int s = Math.min(a.parent.s, b.parent.s);
		  int t = Math.min(a.parent.t, b.parent.t);
		  
		  if( s >= x && t >= y ) {
		    float aSolap =  (s-x+1.0f)*(t-y+1.0f);
		    float aTotal = (a.parent.s - a.parent.x+1.0f)*(a.parent.t - a.parent.y+1.0f);

		    return aSolap/aTotal;
		  }
		  
		  return 0.0f; 
	 };
	 public void printOut(Grammar  G, Hypothesis  H, PrintWriter pw) {
		 if( outStr != null && outStr.length() >0) { 
			    int pd1 = Util.check_str(outStr,  "$1" );
			    int pd2 = Util.check_str(outStr,  "$2" );
			    
			    int i=0;
			    if( pd2 >= 0 && pd1 >= 0 && pd2 < pd1 ) {
			      while( outStr.charAt(i)!='$' || outStr.charAt(i+1) != '2') {
				pw.print(outStr.charAt(i));
				i++;
			      }
			      i+=2;
			      
			      if( H.hd.clase < 0 )
				H.hd.prod.printOut( G, H.hd , pw);
			      else
			    	 pw.print(  H.hd.pt.getTeX( H.hd.clase ) );
			      
			      while( outStr.charAt(i)!='$' || outStr.charAt(i+1) != '1') {
				pw.print(outStr.charAt(i));
				i++;
			      }
			      i+=2;

			      if( H.hi.clase < 0 )
				H.hi.prod.printOut( G, H.hi  , pw);
			      else
				pw.print( H.hi.pt.getTeX( H.hi.clase ) );
			    }
			    else {
			      if( pd1 >= 0 ) {
				while( outStr.charAt(i)!='$' || outStr.charAt(i+1) != '1') {
				  pw.print(outStr.charAt(i));
				  i++;
				}
				i+=2;
				
				if( H.hi.clase < 0 )
				  H.hi.prod.printOut( G, H.hi  , pw);
				else
				  pw.print( H.hi.pt.getTeX( H.hi.clase ) );
			      }
			      if( pd2 >= 0 ) {
				while( outStr.charAt(i)!='$' || outStr.charAt(i+1) != '2') {
				  pw.print(outStr.charAt(i));
				  i++;
				}
				i+=2;
				
				if( H.hd.clase < 0 )
				  H.hd.prod.printOut( G, H.hd  , pw);
				else
				  pw.print( H.hd.pt.getTeX( H.hd.clase ) );
			      }
			    }
			    while( i < outStr.length()  ) {
			      pw.print(outStr.charAt(i));
			      i++;
			    }
			  }
	 }
	 public void setMerges(char c) {
		  merge_cen = c;
	 }
	 public void mergeRegions(Hypothesis a, Hypothesis  b, Hypothesis  s) {

		  switch( merge_cen ) {
		  case 'A': //Data Hypothesis a
		    s.lcen = a.lcen;
		    s.rcen = a.rcen;
		    break;
		  case 'B': //Data Hypothesis b
		    s.lcen = b.lcen;
		    s.rcen = b.rcen;
		    break;
		  case 'C': //Center point
		    s.lcen = (a.parent.y + a.parent.t)/2;
		    s.rcen = (b.parent.y + b.parent.t)/2;
		    break;
		  case 'M': //Mean of both centers
		    s.lcen = (a.lcen + b.lcen)/2; //a.lcen;
		    s.rcen = (a.rcen + b.rcen)/2; //b.rcen;
		    break;
		  default:
		    String m =  "Error: Unrecognized option '" + merge_cen + "' in merge regions\n";
		    throw new RuntimeException(m);
		  }
	 }
	 public boolean check_out() {
		  if( Util.check_str(outStr,  "$1" ) < 0 && Util.check_str(outStr, "$2" ) < 0 )
			    return false;
			  return true;
	 }
	 
	 public String get_outstr2() {
		 return Util.toString(outStr);
	 }
	  //Pure virtual functions
	  public abstract char tipo();
	  public abstract  void print()  ;
	  public abstract  void print_mathml(Grammar  G, Hypothesis  H, OutputStream  fout, int[]  nid)  ;
	};


	//Production S . A : B
	public static class ProductionH extends ProductionB{

	 public ProductionH(int s, int a, int b) {
		 super(s,a,b);
	 }
	 public  ProductionH(int s, int a, int b, float pr, char[]  out) {
		 super(s,a,b,pr,out);
	 }
	  
	 public void print() {
		 System.out.println(S + " . " + A + " : " + B);
	 };
	 public char tipo() {  
		 return 'H';
	 }
	 public void mergeRegions(Hypothesis  a, Hypothesis  b, Hypothesis  s) {
		 
	 }
	 public void print_mathml(Grammar  G, Hypothesis  H,  OutputStream fout, int[]  nid) {
		 if(  H.hi.pt == null && H.hi.prod.tipo() == 'P'
			      &&  H.hi.hi.pt == null &&  H.hi.hi.hi.prod == null
			      && H.hi.hi.hi.pt.getTeX( H.hi.hi.hi.clase )[0] == '('  ) {
			    
			    //Deal with a bracketed expression such that only the right parenthesis has a superscript
			    //this is because CROHME evaluation requires this representation... in a non-ambiguous
			    //evaluation escenario this must be removed

			    Hypothesis hip=H.hi.hi;
			    while( hip.prod != null && hip.hd.prod !=null && hip.hd.hd.prod !=null )
			      hip = hip.hd;

			    Hypothesis closep = hip.hd.hd;
			    Hypothesis  rest=hip.hd;
			    hip.hd = hip.hd.hi;

			    Hypothesis hsup = new Hypothesis(-1, 0, null, 0);
			    hsup.hi = closep;
			    hsup.hd = H.hi.hd;
			    hsup.prod = H.hi.prod;
			    
			    Hypothesis haux = new Hypothesis(-1, 0, null, 0);
			    haux.hi = hip;
			    haux.hd = hsup;
			    haux.prod = this;

			    Hypothesis  hbig = new Hypothesis(-1, 0, null, 0);
			    hbig.hi = haux;
			    hbig.hd = H.hd;
			    hbig.prod = this;

			    hbig.prod.print_mathml(G, hbig, fout, nid);
			     
			    //Restore the original tree
			    hip.hd=rest;
	    }
		 else {
			 try {
				fout.write("<mrow>\n".getBytes());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			  

			    if(  H.hi.pt == null )
			      H.hi.prod.print_mathml(G, H.hi, fout, nid);
			    else {
			      char tipo   = H.hi.pt.getMLtype( H.hi.clase );
			      char[] clase = H.hi.pt.getTeX( H.hi.clase );
			     // nid[0]++;
			      nid[0] ++;

			     // char[] inkid = new char[128]; 
			    //  sprintf(inkid, "%s_%d", clase, *nid);
			      String inkid = Util.toString(clase) + "_" + nid[0];
			      H.hi.inkml_id = inkid ;

			     // fprintf(fout, "<m%c xml:id=\"%s\">%s</m%c>\n", 
				 //     tipo, inkid, clase, tipo);
			      try {
					fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    }
			  
			    if(  H.hd.pt == null )
			      H.hd.prod.print_mathml(G, H.hd, fout, nid);
			    else {
			      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
			      char[] clase = H.hd.pt.getTeX( H.hd.clase );
			      nid[0] ++;

			    //  char inkid[128];
			    //  sprintf(inkid, "%s_%d", clase, *nid);
			    //  H.hd.inkml_id = inkid;
			      String inkid = Util.toString(clase) + "_" + nid[0];
			      H.hi.inkml_id = inkid ;

			  //    fprintf(fout, "<m%c xml:id=\"%s\">%s</m%c>\n", 
			   //     tipo, inkid, clase, tipo);
			      try {
					fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					  
			    }
			    
			    try {
					fout.write("</mrow>\n".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }

	};
 }

	//Production: S . A / B
	public static class ProductionV  extends ProductionB{
	  
	 public  ProductionV(int s, int a, int b) {
		 super(s,a,b);
	 }
	 public  ProductionV(int s, int a, int b, float pr, char[]  out) {
		 super(s,a,b,pr,out);
	 };
	  
	 public void print() {
		 System.out.println(S + " . " + A + " : " + B);
	 };
	 public char tipo() { return 'V'; }
 
	 public  void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid) {
		 char[] hdhiclass = null;
		  if(  H.hd.pt == null && H.hd.hi.prod == null )
		    hdhiclass = H.hd.hi.pt.getTeX( H.hd.hi.clase );

		  if( hdhiclass !=null && ( Util.equals(hdhiclass, "\\sum") ||
				  Util.equals(hdhiclass, "\\int") ||
				  Util.equals(hdhiclass, "-")   ) ) {
		    //Special cases: frac || msubsup bigop
		    if(  Util.equals(hdhiclass, "-")  ) {
		      nid[0] = nid[0] + 1;

		      
		      String inkid =   "-_" + nid[0];
		      H.hd.hi.inkml_id = inkid ;
		      
		      //Init mfrac
		     // fprintf(fout, "<mfrac xml:id=\"%s\">\n", inkid);
		      try {
				fout.write(  ("<mfrac xml:id=\""+ inkid +"\">\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		      //Numerator
		      if(  H.hi.pt == null )
		         H.hi.prod.print_mathml(G, H.hi, fout, nid);
		      else {
			char tipo   = H.hi.pt.getMLtype( H.hi.clase );
			char[] clase = H.hi.pt.getTeX( H.hi.clase );
			
			  nid[0] = nid[0] + 1;
		 	//sprintf(inkid, "%s_%d", clase, *nid);
			//H.hi.inkml_id = inkid;	
			   inkid =  Util.toString(clase) +  "_" + nid[0];
		      H.hi.inkml_id = inkid ;
		  	try {
				fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		   }

		      //Denominator
		      if(  H.hd.hd.pt == null )
		        H.hd.hd.prod.print_mathml(G, H.hd.hd, fout, nid);
		      else {
		        char tipo   = H.hd.hd.pt.getMLtype( H.hd.hd.clase );
		        char[] clase = H.hd.hd.pt.getTeX( H.hd.hd.clase );

		        nid[0] = nid[0] + 1; 
		        inkid =  Util.toString(clase) +  "_" + nid[0];
			      H.hd.hd.inkml_id = inkid ;
			    try {
					fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
		      }
		      //End mfrac
		      try {
				fout.write( "</mfrac>\n".getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
		    else {
		      //Cases Something V BigOp V Something      
		      try {
				fout.write( "<munderover>\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		      //Base: \sum or \lim
		      int tipo  = H.hd.hi.pt.getMLtype( H.hd.hi.clase );
		      nid[0] ++;

		   
		      String inkid = Util.toString( hdhiclass ) + "_" + nid[0];
		      H.hd.hi.inkml_id = inkid ;
		      
		      try {
				fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(hdhiclass) + "</m" + tipo + ">\n").getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				 
		      //Under
		      if( H.hd.hd.pt == null)
		        H.hd.hd.prod.print_mathml(G, H.hd.hd, fout, nid);
		      else {
		          tipo   = H.hd.hd.pt.getMLtype( H.hd.hd.clase );
			   char[] clase = H.hd.hd.pt.getTeX( H.hd.hd.clase );

			   nid[0]++;// = *nid + 1;
			   inkid = Util.toString( clase ) + "_" + nid[0];
			      H.hd.hd.inkml_id = inkid ;
			      
			  try {
				fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		      }

		      //Over
		      if(  H.hi.pt == null )
		        H.hi.prod.print_mathml(G, H.hi, fout, nid);
		      else {
		          tipo   = H.hi.pt.getMLtype( H.hi.clase );
		        char[] clase = H.hi.pt.getTeX( H.hi.clase );

		         nid[0]++;
		         inkid = Util.toString( clase ) + "_" + nid[0];
			      H.hi.inkml_id = inkid ;
		         
			     try {
					fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		      }
		      //End munderover
		      try {
				fout.write( "</munderover>\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    }

		    // End special cases
		  } else {

		    //Normal production print, munder
			  try {
				fout.write( "<munder>\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    if(  H.hi.pt == null )
		      H.hi.prod.print_mathml(G, H.hi, fout, nid);
		    else {
		      char tipo   = H.hi.pt.getMLtype( H.hi.clase );
		      char[] clase = H.hi.pt.getTeX( H.hi.clase );
		      
		       nid[0]++;
		      String inkid = Util.toString( clase ) + "_" + nid[0];
			      H.hi.inkml_id = inkid ; 
		     try {
				fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				 
		    }

		    if(  H.hd.pt == null )
		      H.hd.prod.print_mathml(G, H.hd, fout, nid);
		    else {
		      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
		      char[] clase = H.hd.pt.getTeX( H.hd.clase );
		      
		       nid[0]++;
		       String inkid = Util.toString( clase ) + "_" + nid[0];
			      H.hd.inkml_id = inkid ; 
		       
		     try {
				fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  Util.toString(clase) + "</m" + tipo + ">\n").getBytes() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
 
		    }
		    try {
				fout.write( "</munder>\n".getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		  }

		 
	 }
	};


	//Production: S . A /u B
	public static class ProductionU extends   ProductionB{
	  
	 public   ProductionU(int s, int a, int b) {
		 super(s,a,b);
	 }
	 public  ProductionU(int s, int a, int b, float pr, char[] out) {
		 super(s,a,b,pr,out);
	 }
	  
	 public void print() {}
	 public char tipo() { return 'U'; }
	 public  void mergeRegions(Hypothesis a, Hypothesis b, Hypothesis s) {}
	 public  void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid) {}
	};


	//Production: S . A /e B
	public static class ProductionVe extends   ProductionB{
	  
	 public  ProductionVe(int s, int a, int b) {
		 super(s,a,b);
	 }
	 public  ProductionVe(int s, int a, int b, float pr, char[] out) {
		 super(s,a,b,pr,out);
	 }
	  
	 public void print() {
		 System.out.print(S + " . " + A + " /e " + B + "\n");
	 }
	 public  char tipo() { return 'e'; }
	 public  void mergeRegions(Hypothesis a, Hypothesis b, Hypothesis s){}
	 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
		 
		 char[] hdhiclass = null;
		  if(  H.hd.pt == null && H.hd.hi.prod == null )
		    hdhiclass = H.hd.hi.pt.getTeX( H.hd.hi.clase );

		  if( hdhiclass != null &&  ( Util.equals(hdhiclass, "\\sum") ||
				  Util.equals(hdhiclass, "\\int") ||
				  Util.equals(hdhiclass, "-")   ) ) {
		    //Special cases: frac || msubsup bigop
		    if(  Util.equals(hdhiclass, "-")  ) {
		      nid[0]++;
 
		      String inkid =  "-_" + nid[0]; 
		      H.hd.hi.inkml_id = inkid;
		      
		     Util.printEntry(fout, "<mfrac xml:id=\"" +inkid+ "\">\n" );
           
		      //Numerator
		      if(  H.hi.pt == null )
			H.hi.prod.print_mathml(G, H.hi, fout, nid);
		      else {
			char tipo   = H.hi.pt.getMLtype( H.hi.clase );
			char[] clase = H.hi.pt.getTeX( H.hi.clase );
			
			nid[0]++;
			inkid = Util.toString(clase) + "_" + nid[0]; 
			H.hi.inkml_id = inkid;	
			Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
		 
		 }

		      //Denominator
		      if( H.hd.hd.pt == null )
		        H.hd.hd.prod.print_mathml(G, H.hd.hd, fout, nid);
		      else {
		        char tipo   = H.hd.hd.pt.getMLtype( H.hd.hd.clase );
		        char[] clase = H.hd.hd.pt.getTeX( H.hd.hd.clase );

		        nid[0]++;
		        inkid = Util.toString(clase) + "_" + nid[0]; 
		        H.hd.hd.inkml_id = inkid;
                Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase)); 
		      }
		      //End mfrac
		      Util.printEntry(fout, "</mfrac>\n");
		    }
		    else {
		      //Cases Something V BigOp V Something      
		    	Util.printEntry(fout, "<munderover>\n");

		      //Base: \sum or \lim
		      int tipo  = H.hd.hi.pt.getMLtype( H.hd.hi.clase );
		      nid[0]++;
 
		      
		      String inkid = Util.toString(hdhiclass) + "_" + nid[0];
		      H.hd.hi.inkml_id = inkid;

		      Util.printXmlEntry(fout, tipo, inkid, Util.toString( hdhiclass) );

		      //Under
		      if(  H.hd.hd.pt == null )
		        H.hd.hd.prod.print_mathml(G, H.hd.hd, fout, nid);
		      else {
		          tipo   = H.hd.hd.pt.getMLtype( H.hd.hd.clase );
			      char[] clase = H.hd.hd.pt.getTeX( H.hd.hd.clase );

			nid[0]++;
			inkid = Util.toString(clase) + "_" + nid[0]; 
			H.hd.hd.inkml_id = inkid;

			Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase) );
		      }

		      //Over
		      if(  H.hi.pt == null )
		        H.hi.prod.print_mathml(G, H.hi, fout, nid);
		      else {
		          tipo   = H.hi.pt.getMLtype( H.hi.clase );
		        char[] clase = H.hi.pt.getTeX( H.hi.clase );

		        nid[0]++;
		        inkid = Util.toString(clase) + "_" + nid[0]; 
		        H.hi.inkml_id = inkid;

		       Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
		      }
		      //End munderover
		      Util.printEntry(fout, "</munderover>\n");

		    }

		    // End special cases
		  } else {

		    //Normal production print, munder
		    Util.printEntry(fout, "<munder>\n");

		    if(  H.hi.pt == null)
		      H.hi.prod.print_mathml(G, H.hi, fout, nid);
		    else {
		      char tipo   = H.hi.pt.getMLtype( H.hi.clase );
		      char[] clase = H.hi.pt.getTeX( H.hi.clase );
		      
		      nid[0]++;
		      String inkid = Util.toString(clase) + "_" + nid[0]; 
		      H.hi.inkml_id = inkid;
		      
		      Util.printXmlEntry(fout,   tipo, inkid, Util.toString( clase ) );
		    }

		    if( H.hd.pt == null )
		      H.hd.prod.print_mathml(G, H.hd, fout, nid);
		    else {
		      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
		      char[] clase = H.hd.pt.getTeX( H.hd.clase );
		      
		      nid[0]++;
		      String inkid = Util.toString(clase) + "_" + nid[0]; 
		      H.hd.inkml_id = inkid;

		      Util.printXmlEntry(fout,   tipo, inkid, Util.toString( clase ) );
		    }
		    Util.printEntry(fout, "</munder>\n");

		  }
		 
	 }
	};



	//Production: S . A sse B
	public static class ProductionSSE extends ProductionB{
	  
	 public ProductionSSE(int s, int a, int b) {
		 super(s,a,b);
	 }
	 public  ProductionSSE(int s, int a, int b, float pr, char[] out) {
		 super(s,a,b,pr,out);
	 }
	  
	 public void print() {
		 System.out.print( S + " . " + A + " sse " + B + "\n");
	 }
	 public  char tipo() { return 'S'; } 
	 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
		 Util.printEntry(fout, "<msubsup>\n");

		  if( H.hi.hi.pt == null )
		    H.hi.hi.prod.print_mathml(G, H.hi.hi, fout, nid);
		  else {
		    char tipo   = H.hi.hi.pt.getMLtype( H.hi.hi.clase );
		    char [] clase = H.hi.hi.pt.getTeX( H.hi.hi.clase );
		    nid[0]++;
 
		    String inkid = Util.toString(clase) + "_" + nid[0];
		    H.hi.hi.inkml_id = inkid;

		    Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
		  }
		  
		  if(  H.hi.hd.pt == null )
		    H.hi.hd.prod.print_mathml(G, H.hi.hd, fout, nid);
		  else {
		    char tipo   = H.hi.hd.pt.getMLtype( H.hi.hd.clase );
		    char[]clase = H.hi.hd.pt.getTeX( H.hi.hd.clase );
		    nid[0]++;
 
		    String inkid = Util.toString(clase) + "_" + nid[0];
		    H.hi.hd.inkml_id = inkid;

		    Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
		  }

		  if(  H.hd.pt == null)
		    H.hd.prod.print_mathml(G, H.hd, fout, nid);
		  else {
		    char tipo   = H.hd.pt.getMLtype( H.hd.clase );
		    char[] clase = H.hd.pt.getTeX( H.hd.clase );
		    nid[0]++;

		  
		    String inkid = Util.toString(clase) + "_" + nid[0];
		    H.hd.inkml_id = inkid;

		    Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
		  }
		  
		  Util.printEntry(fout, "</msubsup>\n");
	 }
	};



	//Production: S . A ^ B
	public static class ProductionSup extends ProductionB{
	  
		public ProductionSup(int s, int a, int b) {
			 super(s,a,b);
		 }
		 public  ProductionSup(int s, int a, int b, float pr, char[] out) {
			 super(s,a,b,pr,out);
		 }
	  
	  public void print() {
		  System.out.print(S + " . " + A + " ^ " + B + "\n");
	  }
		 public  char tipo() { return 'P'; } 
		 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
	
			 if( H.hi.pt == null &&  H.hi.hi.prod == null
				      &&  H.hi.hi.pt.getTeX( H.hi.hi.clase )[0] == '('   ) {

				    //Deal with a bracketed expression such that only the right parenthesis has a superscript
				    //this is because CROHME evaluation requires this representation... in a non-ambiguous
				    //evaluation escenario this must be removed

				    Hypothesis hip=H.hi;
				    while(  hip.pt == null && hip.hd.pt == null && hip.hd.hd.pt == null)
				      hip = hip.hd;

				    Hypothesis  closep = hip.hd.hd;
				    Hypothesis  rest=hip.hd;
				    hip.hd = hip.hd.hi;

				    Hypothesis  haux = new Hypothesis(-1, 0, null, 0);
				    
				    haux.hi = closep;
				    haux.hd = H.hd;
				    haux.prod = this;

				    Hypothesis  hbig = new Hypothesis(-1, 0, null, 0);
				    hbig.hi = H.hi;
				    hbig.hd = haux;
				    hbig.prod = H.hi.prod;

				    hbig.prod.print_mathml(G, hbig, fout, nid);
				  
				    //Restore original tree
				    hip.hd=rest;
				  }
				  else if(  H.hi.pt == null && H.hi.prod.tipo() == 'V' && H.hi.hi.pt != null
						  && Util.equals(  H.hi.hi.pt.getTeX( H.hi.hi.clase ) , "\\int")  ) {
				    //Print as msubsup (BigOp [Below] Algo) [Sup] Algo
				    Util.printEntry(fout, "<msubsup>\n");

				    //\int
				    {
				      char tipo   = H.hi.hi.pt.getMLtype( H.hi.hi.clase );
				      char[] clase = H.hi.hi.pt.getTeX( H.hi.hi.clase );
				      nid[0]++;
				      
				     
				      String inkid = Util.toString(clase) + "_" + nid[0];
				      H.hi.hi.inkml_id = inkid;
				      
				      Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
				    }

				    //subscript
				    if(  H.hi.hd.pt == null )
				      H.hi.hd.prod.print_mathml(G, H.hi.hd, fout, nid);
				    else {
				      char tipo   = H.hi.hd.pt.getMLtype( H.hi.hd.clase );
				      char[] clase = H.hi.hd.pt.getTeX( H.hi.hd.clase );
				      nid[0]++;
				      
			 
				      String inkid = Util.toString(clase) + "_" + nid[0];
				      H.hi.hd.inkml_id = inkid;
				      
				      Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
				    }

				    //superscript
				    if(   H.hd.pt == null )
				      H.hd.prod.print_mathml(G, H.hd, fout, nid);
				    else {
				      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
				      char[] clase = H.hd.pt.getTeX( H.hd.clase );
				      nid[0]++;
				      
				      String inkid = Util.toString(clase) + "_" + nid[0];
				      H.hd.inkml_id = inkid;
				      
				      Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
				    }

				    Util.printEntry(fout, "</msubsup>\n");

				  }
				  else {

				    Util.printEntry(fout, "<msup>\n");

				    if(  H.hi.pt == null )
				      H.hi.prod.print_mathml(G, H.hi, fout, nid);
				    else {
				      char tipo   = H.hi.pt.getMLtype( H.hi.clase );
				      char[] clase = H.hi.pt.getTeX( H.hi.clase );
				      nid[0]++;

				      String inkid = Util.toString(clase) + "_" + nid[0];
				      H.hi.inkml_id = inkid;

				      Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
				    }
				  
				    if(  H.hd.pt == null )
				      H.hd.prod.print_mathml(G, H.hd, fout, nid);
				    else {
				      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
				      char[] clase = H.hd.pt.getTeX( H.hd.clase );
				      nid[0]++;

				      String inkid = Util.toString(clase) + "_" + nid[0];
				      H.hd.inkml_id = inkid;

				      Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
				    }
				  
				    Util.printEntry(fout, "</msup>\n");
				  }
			 
		 }
	};


	//Production: S . A _ B
	public static class ProductionSub extends ProductionB{
	  
		 public ProductionSub(int s, int a, int b) {
			 super(s,a,b);
		 }
		 public  ProductionSub(int s, int a, int b, float pr, char[] out) {
			 super(s,a,b,pr,out);
		 }
		  
		 public void print() {
			 System.out.print(S + " . " + A + " _ " + B);
		 }
		 public  char tipo() { return 'B'; }
		 
		 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
			 Util.printEntry(fout, "<msub>\n");

			  if(  H.hi.pt == null )
			    H.hi.prod.print_mathml(G, H.hi, fout, nid);
			  else {
			    char tipo   = H.hi.pt.getMLtype( H.hi.clase );
			    char[] clase = H.hi.pt.getTeX( H.hi.clase );
			    nid[0]++;

			     
			    String inkid = Util.toString(clase) + "_" + nid[0];
			    H.hi.inkml_id = inkid;
			    Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
			  }
			  
			  if(  H.hd.pt == null )
			    H.hd.prod.print_mathml(G, H.hd, fout, nid);
			  else {
			    char tipo   = H.hd.pt.getMLtype( H.hd.clase );
			    char[] clase = H.hd.pt.getTeX( H.hd.clase );
			    nid[0]++;

			    String inkid = Util.toString(clase) + "_" + nid[0];
			   
			    H.hd.inkml_id = inkid;

			    Util.printXmlEntry(fout,  tipo, inkid, Util.toString(clase));
			  }
			  
			  Util.printEntry(fout, "</msub>\n");
		 }
	};


	//Production: S . A ins B
	public static class ProductionIns extends ProductionB{
	  
		 public ProductionIns(int s, int a, int b) {
			 super(s,a,b);
		 }
		 public  ProductionIns(int s, int a, int b, float pr, char[] out) {
			 super(s,a,b,pr,out);
		 }
		  
		 public void print() {
			 System.out.print(S + " . " + A + " /e " + B);
		 }
		 public  char tipo() { return 'I'; }
		 
		 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
			 
			  nid[0]++;

			  if(  H.hi.pt == null && H.hi.prod.tipo() == 'M' ) {
			    //Mroot case
			   
			    String inkid = "\\sqrt_" + nid[0];
			    H.hi.hd.inkml_id = inkid;

			    Util.printEntry(fout, "<mroot xml:id=\"" +inkid + "\">\n" );

			    //Sqrt content
			    if(  H.hd.pt == null )
			      H.hd.prod.print_mathml(G, H.hd, fout, nid);
			    else {
			      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
			      char[] clase = H.hd.pt.getTeX( H.hd.clase );
			      nid[0]++;
			      
			      inkid = Util.toString(clase) + "_" + nid[0]; 
			      H.hd.inkml_id = inkid;
			      
			      Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
			    }
			    
			    //sqrt index
			    if(  H.hi.hi.pt == null )
			      H.hi.hi.prod.print_mathml(G, H.hi.hi, fout, nid);
			    else {
			      char tipo   = H.hi.hi.pt.getMLtype( H.hi.hi.clase );
			      char[] clase = H.hi.hi.pt.getTeX( H.hi.hi.clase );
			      nid[0]++;
			      
			      inkid = Util.toString(clase) + "_" + nid[0]; 
			      H.hi.hi.inkml_id = inkid;
			      
			      Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
			    }

			    Util.printEntry(fout, "</mroot>\n");
			  }
			  else {
			    //Regular msqrt
			  String inkid = "\\sqrt_" + nid[0];
			    H.hi.inkml_id = inkid;
			    Util.printEntry(fout, "<msqrt  id=\"" +inkid + "\">\n" );
			   
			    
			    if(  H.hd.pt == null )
			      H.hd.prod.print_mathml(G, H.hd, fout, nid);
			    else {
			      char tipo   = H.hd.pt.getMLtype( H.hd.clase );
			      char [] clase = H.hd.pt.getTeX( H.hd.clase );
			      nid[0]++;
			      
			      inkid = Util.toString(clase) + "_" + nid[0]; 
			      H.hd.inkml_id = inkid;
			      
			      Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
			    }
			    
			    Util.printEntry(fout, "</msqrt>\n");
			  } 
			 
		 }
	};


	//Production: S . A mroot B
	public static class ProductionMrt extends ProductionB{
	  
		 public ProductionMrt(int s, int a, int b) {
			 super(s,a,b);
		 }
		 public  ProductionMrt(int s, int a, int b, float pr, char[] out) {
			 super(s,a,b,pr,out);
		 }
		  
		 public void print() {
			 System.out.print(S + " . " + A + " /m " + B + "\n");
		 }
		 public  char tipo() { return 'M'; }
		 
		 public void print_mathml(Grammar G, Hypothesis H, OutputStream fout, int[] nid){
			 nid[0]++;

			  String inkid = "\\sqrt_" + nid[0];
			  H.hi.inkml_id = inkid;
			  Util.printEntry(fout, "<mroot xml:id=\"" +inkid + "\">\n" );
			   

			  if(  H.hd.pt == null )
			    H.hd.prod.print_mathml(G, H.hd, fout, nid);
			  else {
			    char tipo   = H.hd.pt.getMLtype( H.hd.clase );
			    char[] clase = H.hd.pt.getTeX( H.hd.clase );
			    nid[0]++;

			  
			    inkid = Util.toString(clase) + "_" + nid[0]; 
			    H.hd.inkml_id = inkid;

			    Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
			  }


			  if(  H.hi.hi.pt == null )
			    H.hi.hi.prod.print_mathml(G, H.hi.hi, fout, nid);
			  else {
			    char tipo   = H.hd.pt.getMLtype( H.hi.hi.clase );
			    char [] clase = H.hd.pt.getTeX( H.hi.hi.clase );
			    nid[0]++;

			    inkid = Util.toString(clase) + "_" + nid[0]; 
			    H.hd.inkml_id = inkid;

			    Util.printXmlEntry(fout, tipo, inkid, Util.toString(clase));
			  }

			  
			  Util.printEntry(fout, "</mroot>\n");
			 
		 }
	};


	//Production S . term ( N clases )
	public static class ProductionT{
	 public int S;
	 public boolean[] clases;
	 public  char[][] texStr;
	 public   char[] mltype;
	 public  float[] probs;
	 public   int N;

	 public ProductionT(int s, int nclases) {
		  S = s;
		  N = nclases;
		  texStr = new char[N][];
		  mltype = new char[N];
		  clases = new boolean[N];
		  probs = new float[N];
		  for(int i=0; i<N; i++) {
		    clases[i] = false;
		    texStr[i] = null;
		    mltype[i] = 'z';
		    probs[i] = 0;
		  }
	 }
	 public void destroy() {
		 
	 }
	  
	 public  void setClase(int k, float pr, char[] tex, char mlt) {
		  clases[k] = true;
		  if( texStr[k] != null)
		   System.err.println( "WARNING: Terminal " + k 
				   + " redefined with label '" + Util.toString(tex) + "'\n");
		  else {
		    texStr[k] = Util.toString(tex).toCharArray();
		    probs[k] = (float) (pr > 0.0 ? Math.log(pr) : -Float.MAX_VALUE);
		    mltype[k] = mlt;
		  }
	 }
	 public  boolean getClase(int k) {
		 return clases[k];
	 }
	 public  float getPrior(int k) {
		  return probs[k];
	 }
	 public char[] getTeX(int k) {
		 return texStr[k];
	 }
	 public  char getMLtype(int k) {
		 return mltype[k];
	 }
	 public int  getNoTerm() {
		  return S;
	 }

	 public void print() {
		 int nc=0;

		  for(int i=0; i<N; i++)
		    if( clases[i] )
		      nc++;
          System.out.print(S + " -> [" + nc + " clases]\n"  );  
	 }
	};
}
