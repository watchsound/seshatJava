package seshat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.sound.sampled.Line;

public class gParser {
	public static final int SIZE = 1024;
			
	  public Grammar g;
	  public char[] pre;

	  public boolean isFillChar(char c) {
		 return Util.isFillChar(c);
	  }
	  public int  split(String str, ObjectWrapper<char[][]> res) {
		  char[] tokensaux = new char[2*SIZE];
		  int n=0, i=0, j=0;
		 // 0.06462269 H Term Term OpTerm "$1 $2" M
		  while( isFillChar(str.charAt(i)) )  i++;

		  while( i < str.length() ) {
		    if( str.charAt(i) == '\"' ) {
		      i++;
		      while(i < str.length() && str.charAt(i) != '\"' ) {
			    tokensaux[j] = str.charAt(i);
			    i++; j++;
		      }
		      i++;
		    }
		    else {
		      while( i < str.length() && !isFillChar(str.charAt(i)) ) {
			tokensaux[j] = str.charAt(i);
			i++; j++;
		      }
		    }
		    tokensaux[j++] = 0;
		    n++;
		    while( i < str.length() && isFillChar(str.charAt(i)) )  i++;
		  }

		  char[][] toks=new char[n][];
		  for(i=0, j=0; i<n; i++) {
		    int tlen = Util.strlen(tokensaux, j)+1;
		    toks[i] = new char[tlen];
		    Util.strcpy(toks[i], tokensaux, j);
		    j += tlen;
		  }

		   res.obj = toks;

		  return n;
	  }
	  public void solvePath(char[]in, char[] out) {
		  Util.strcpy(out, pre, 0); //Copy prefix
		  Util.strcat(out, in);  //Add the remainding path
		  out[Util.strlen(out)-1] = 0; //Remove the final '\n'
	  }
	public  gParser(Grammar  gram, BufferedReader fd, String path) {
		  g = gram;

		  int n = path.length();

		  if( n > 0 ) {
		    pre = new char[n+1];
		    for(int i = 0; i < n ; i++) {
		    	pre[i] = path.charAt(i);
		    }
		  }
		  else {
		    pre = new char[1];
		    pre[0] = 0;
		  }

		  parse( fd );
	}
	public void destructor() {}

	public  void parse(BufferedReader fd) {
		  char[] linea = new char[SIZE];
		  char[] tok1 = new char[SIZE];
		  char[] tok2 = new char[SIZE];
		  char[] aux = new char[SIZE];
//		  BufferedReader fd = null;
//		try {
//			fd = new BufferedReader(new FileReader(file));
//		} catch (FileNotFoundException e) {
//		   throw new RuntimeException(e);
//		}
		  
		  //Read nonterminal symbols
		  while( Util.nextLine(fd, linea) && Util.strcmp(linea, "START") != 0 ) {
		    Util.sscanf(linea, "%s", tok1);
		    g.addNoTerminal(Util.toString(tok1));
		  }

		  //Read start symbol(s) of the grammar
		  while( Util.nextLine(fd, linea) && Util.strcmp(linea, "PTERM") != 0 ) {
		    Util.sscanf(linea, "%s", tok1);
		    g.addInitSym(Util.toString(tok1));
		  }

		  //Read terminal productions
		  while( Util.nextLine(fd, linea) && Util.strcmp(linea, "PBIN") != 0 ) {
		    float pr = 0;
            ObjectWrapper<Float> prw = new ObjectWrapper<Float>(pr);
		    Util.sscanf(linea, "%f %s %s %s", prw, tok1, tok2, aux);
		    
		    g.addTerminal(prw.obj.floatValue(), Util.toString(tok1), tok2, aux);
		  }

		  //Read binary productions
		  while( Util.nextLine(fd, linea) ) {
		    char[][]  tokens = new char[7][];
		    ObjectWrapper<char[][]> tokensw = new ObjectWrapper<char[][]>(tokens);
		    int ntoks = split(Util.toString(linea), tokensw);
		    tokens = tokensw.obj;
		    if( ntoks != 7 ) {
		       System.err.print( "Error: Grammar not valid (PBIN)\n");
		       throw new RuntimeException("Error: Grammar not valid (PBIN)\n" );
		    }

		    if( Util.strcmp(tokens[1], "H") == 0 )
		      g.addRuleH(Util.atof(tokens[0]), Util.toString(tokens[2]), Util.toString(tokens[3]), Util.toString(tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "V") == 0 )
		      g.addRuleV(Util.atof(tokens[0]), Util.toString(tokens[2]), Util.toString(tokens[3]), Util.toString(tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "Ve") == 0 )
		      g.addRuleVe(Util.atof(tokens[0]), Util.toString(tokens[2]), Util.toString(tokens[3]),Util.toString( tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "Sup") == 0 )
		      g.addRuleSup(Util.atof(tokens[0]), Util.toString(tokens[2]),Util.toString( tokens[3]),Util.toString( tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "Sub")  == 0)
		      g.addRuleSub(Util.atof(tokens[0]), Util.toString(tokens[2]),Util.toString( tokens[3]),Util.toString( tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "SSE") == 0 )
		      g.addRuleSSE(Util.atof(tokens[0]), Util.toString(tokens[2]),Util.toString( tokens[3]), Util.toString(tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "Ins")  == 0)
		      g.addRuleIns(Util.atof(tokens[0]),Util.toString( tokens[2]),Util.toString( tokens[3]),Util.toString( tokens[4]), tokens[5], tokens[6]);
		    else if( Util.strcmp(tokens[1], "Mrt")  == 0)
		      g.addRuleMrt(Util.atof(tokens[0]),Util.toString( tokens[2]),Util.toString( tokens[3]),Util.toString( tokens[4]), tokens[5], tokens[6]);
		    else { 
		       System.err.print(  "Error: Binary rule type '" + tokens[1] + "' nor valid\n"  );
		       throw new RuntimeException( "Error: Binary rule type '" + tokens[1] + "' nor valid\n"  );
		    }
		  }
		  
		  try {
			fd.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
}
