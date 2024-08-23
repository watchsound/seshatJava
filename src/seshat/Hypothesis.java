package seshat;

import seshat.Production.ProductionB;
import seshat.Production.ProductionT;

public class Hypothesis {
	 public int clase; //If the hypothesis encodes a terminal symbols this is the class id (-1 otherwise)
	 public double pr; //log-probability

	  //References to left-child (hi) and right-child (hd) to create the derivation tree
	 public Hypothesis  hi,  hd;

	  //The production used to create this hypothesis (either Binary or terminal)
	 public ProductionB  prod;
	 public ProductionT  pt;

	  //INKML_id for terminal symbols in order to create the InkML output
	 public String inkml_id;
	  //Auxiliar var to retrieve the used production in the special SSE treatment
	 public ProductionB  prod_sse;

	  //Vertical center left and right
	 public int lcen, rcen;

	 public  CellCYK  parent; //Parent cell
	 public  int ntid;        //Nonterminal ID in parent

	  //Methods
	 public  Hypothesis(int c, double p, CellCYK  cd, int nt) {
		  clase = c;
		  pr = p;
		  hi = hd = null;
		  prod = null;
		  prod_sse = null;
		  pt = null;
		  lcen = rcen = 0;
		  parent = cd;
		  ntid = nt;
		  inkml_id = "none";
	 }
	 public void  d_Hypothesis() {
		 
	 }

	 public  void copy(Hypothesis  H) {
		  clase = H.clase;
		  pr = H.pr;
		  hi = H.hi;
		  hd = H.hd;
		  prod = H.prod;
		  prod_sse = H.prod_sse;
		  pt = H.pt;
		  lcen = H.lcen;
		  rcen = H.rcen;
		  parent = H.parent;
		  ntid = H.ntid;
		  inkml_id = H.inkml_id;
	 }
}
