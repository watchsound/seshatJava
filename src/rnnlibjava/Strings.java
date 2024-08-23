package rnnlibjava;

import lang.ArrayListObj;
import seshat.Util;

public class Strings {
	
 
//	    public static String str(int n) {
//	    	return n + "";
////	    	StringBuilder sb = new StringBuilder();
////	    	for(int i = 0; i < n; i++)
////	    		sb.append(" ");
////	    	return sb.toString();
//	    }
//	    public static String str(String token,int repeats) {
//	    	StringBuilder sb = new StringBuilder();
//	    	for(int i = 0; i < repeats; i++)
//	    		sb.append(token);
//	    	return sb.toString();
//	    }
	     public static String ordinal(int n) {
		  String s = n +"";
		  if (n < 100) {
		    char c = s.charAt(s.length()-1);
		    if(c == '1') {
		      return s + "st";
		    } else if(c == '2') {
		      return s + "nd";
		    } else if(c == '3') {
		      return s + "rd";
		    }
		  }
		  return s + "th";
		}
		public static String trim(String str) {
		   return str.trim();
		}
		 
		public static String lower(String s) {
		   return s.toLowerCase();
		}
		public static boolean in(String str, String search) {
			return str.indexOf(search)>=0;
		}
		public static boolean in(String str, char[] search) {
			return str.indexOf(Util.toString(search))>=0;
		}
	    public static boolean begins(String str, String search) {
	    	return str.startsWith(search);
	    }
	    public static boolean begins(String str, char[] search) {
	    	return str.startsWith(Util.toString(search));
	    } 
	    public static boolean ends(String str, String search) {
	    	return str.endsWith(search);
	    }
	    public static boolean ends(String str, char[] search) {
	    	return str.endsWith(Util.toString(search));
	    }
        public static ArrayListObj<String> split(String original ){
	    	return split(original,  ' ', 0);
	    } 
        public static ArrayListObj<String> split(String original, char delim ){
	    	return split(original, delim, 0);
	    } 
	    public static ArrayListObj<String> split(String original, char delim, int maxSplits){
	    	String[] ss = original.split(delim + "");
	    	ArrayListObj<String> rsult = new ArrayListObj<>();
	    	StringBuilder sb = new StringBuilder();
	    	//FIXME ?? 
//	    	while (delim == ' ' ? ss >> s : getline(ss, s, delim)) {
//	    	    vect += read<T>(s);
//	    	    if (vect.size() == maxSplits-1) {
//	    	      delim = '\0';
//	    	    }
//	    	  }
	    	
	    	for(int i = 0; i < ss.length; i++) { 
	    		if( i >= maxSplits ) { 
	    			sb.append(ss[i] + " ");
	    			continue;
	    		}
	    		rsult.add(ss[i]); 
	    	}
	    	if( sb.length() > 0)
	    		rsult.add(sb.toString().trim());
	    	return rsult;
	    }
	    public static ArrayListObj<String> split_with_repeat(String original ){
	    	return split_with_repeat(original,  ' ', '*');
	    } 
        public static ArrayListObj<String> split_with_repeat(String original, char delim ){
	    	return split_with_repeat(original, delim, '*');
	    } 
	    public static ArrayListObj<String> split_with_repeat(String original, char delim, char repeater){
	    	String[] ss = original.split(delim + "");
	    	ArrayListObj<String> rsult = new ArrayListObj<>();
	    	for(String s : ss) {
	    		String[] ss2 = s.split("\\" +repeater+"");
	    		
	    		int numRepeats = ss2.length <= 1? 1 : Integer.parseInt(ss2[1]);
	    		//FIXME ??? 
	    		// vect += val, repeat(numRepeats-1, val); ??
	    		for(int i = 0; i < numRepeats; i++) {
	    			rsult.add(ss2[0]);
	    		}
	    	}
	    	return rsult;
	    }
         public static String join(ArrayListObj r ) {
	    	return join(r, "");
	    }
	    public static String join(ArrayListObj r, String joinStr) {
	    	if( r.isEmpty() ) return "";
	    	StringBuilder sb = new StringBuilder();
	    	for(Object v : r) {
	    		sb.append(  joinStr + v);
	    	}
	    	return sb.substring(joinStr.length());
	    }
	    public static String left_pad(String val , int width) {
	    	return left_pad(val, width, '0');
	    }
	    public static String left_pad(String val, int width, char fill) {
	    	StringBuilder sb = new StringBuilder();
	    	for( int i = 0; i < width - val.length(); i++) {
	    		sb.append(fill+"");
	    	}
	    	sb.append(val);
	    	return sb.toString();
	    }
        public static String int_to_sortable_string(int num, int max) {
        	return left_pad(num +"", ((max-1) + "").length());
        }
	 
}
