package seshat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;

import lang.SimpleListI;

public class Util {
	public static boolean debug = false;
	
	public static void print(int[] data, PrintStream out) {
		for(int i : data) {
			out.print(i + " ");
		}
		out.println();
	}
	public static void print(String label, int[] data, PrintStream out) {
		out.print(label + ": ");
		print(data, out);
	}
	public static void print(String label, SimpleListI data, PrintStream out) {
		out.print(label + ": ");
		print(data, out);
	}
	public static void print(SimpleListI data, PrintStream out) {
		for(int i = 0; i < data.size(); i++) {
			out.print(data.at(i) + " ");
		}
		out.println();
	}
	/**
	 * %4d
	 * @param format
	 * @return  -1 not parsed,  0  no limit, ....
	 */
	public static int intFixLen(String format) {
		if( format == null || format.length() == 0 || format.charAt(0) != '#' || format.charAt(format.length()-1) != 'd')
			return -1;
		if( format.length() == 2  )
			return 0;
		try {
			return Integer.parseInt(format.substring(1, format.length()-1));
		} catch(Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
	public static String padding(int length) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < length; i++)
			sb.append(" ");
		return sb.toString();
	}
	public static String padding(String value, int length) {
		if( value.length() >= length)
			return value.substring(0, length);
	
		return padding(length - value.length()) + value; 
	}
	public static String formatInt(int value, String format) {
		int length = intFixLen(format);
		if( length <= 0 )
			return value +"";
		return formatInt(value,length);
	}
	public static String formatInt(int value,  int length) {
		 NumberFormat numberFormat = NumberFormat.getInstance();
		 numberFormat.setMinimumIntegerDigits(length);
		 numberFormat.setMaximumIntegerDigits(length);
		 return numberFormat.format(value);
	}
	
	public static String format(float value,  int fractions) {
		 NumberFormat numberFormat = NumberFormat.getInstance();
		 numberFormat.setMaximumFractionDigits(fractions);
		 return numberFormat.format(value);
	}
	
	public static boolean isFillChar(char c) {
		  switch(c) {
		  case ' ':
		  case '\t':
		  case '\n':
		  case '\r':
		    return true;
		  default: 
		    return false;
		  } 
	  }
	
	public static boolean equals(char[] str1, String str2) {
		if( Util.strlen(str1) != str2.length() )
			return false;
		for(int i = 0; i < str2.length(); i++) {
			if( str2.charAt(i) != str1[i])
				return false;
		}
		return true;
	}
	
	public static int strlen(char[] data ) {
		return strlen(data,0);
	}
	
	public static int strlen(char[] data, int startPos) {
		for(int i = startPos; i < data.length; i++) {
			if( data[i] == (char)'\0' )
				return i - startPos;
		}
		return data.length - startPos;
	}
	
	public static void  strcpy(char[] to, char[] from, int startPos) {
		int flength = strlen(from, startPos);
		System.arraycopy(from, startPos, to, 0, flength);
	}
	
	public static void  strcpy(char[] to, String from, int startPos) { 
		System.arraycopy(from.toCharArray(), startPos, to, 0, from.length() - startPos);
		to[from.length()] = '\0';
	}
	
	public static void strcat(char[] master, char[] added) {
		System.arraycopy(added, 0, master, strlen(master), strlen(added));
	}
	public static int strcmp(char[] one, String two) { 
		return strcmp(one, two.toCharArray());
	}
	public static int strcmp(char[] one, char[] two) {
		if( one == null && two == null) 
			return 0;
		if( one == null )
			return -1;
		if( two == null)
			return 1;
		int len1 = strlen(one);
		int len2 = strlen(two);
		for(int i = 0; i < len1 && i < len2; i++) {
			if( one[i] < two[i])
				return -1;
			if( one[i] > two[i])
				return 1;
		}
		if( len1 < len2 )
			return -1;
		if( len1 > len2 )
			return 1;
		return 0;
	}
	
	public static float atof(char[] data) {
		String str = Util.toString(data); 
		return Float.parseFloat(str);
	}
	
	public static String toString(char[] source) {
		int len = Util.strlen(source);
		return new String(source).substring(0,len);
	}
	public static String toString(String source) {
		return source;
	}
	/**
	 * 
	 * @param source
	 * @param format
	 * @param parameters  array or ObjectWrapper
	 */
	public static void sscanf(char[] source, String format, Object... parameters) { 
		String sourceStr = toString(source) ;
		String[] stoken = sourceStr.split("\\s+");
		String[] fs = format.split("\\s+");
		for(int i = 0; i < fs.length; i++) {
			Object pobj =  parameters[i];
			if( fs[i].equals("%d") ) { //十进制整数
				if( pobj instanceof int[]) {
					int[] p = (int[])pobj;
					p[0] = Integer.parseInt(stoken[i]);
				}else {
					ObjectWrapper p = (ObjectWrapper) parameters[i];
					p.obj = Integer.parseInt(stoken[i]);
				} 
			}
			if( fs[i].equals("%c") ) { //单个字符
				if( pobj instanceof char[]) {
					char[] p = (char[])pobj;
					p[0] =  stoken[i].charAt(0);
				}else {
					ObjectWrapper p = (ObjectWrapper) parameters[i];
					p.obj = stoken[i].charAt(0);
				} 
			}
			if( fs[i].equals("%s") ) { //字符串。这将读取连续字符，直到遇到一个空格字符（空格字符可以是空白、换行和制表符）。
				if( pobj instanceof char[]) {
					char[] p = (char[])pobj;
				    strcpy(p, stoken[i],0);
				}else {
					ObjectWrapper p = (ObjectWrapper) parameters[i];
					p.obj = stoken[i] ;
				} 
			}
			if( fs[i].equals("%f") ) { // 浮点数
				if( pobj instanceof float[]) {
					float[] p = (float[])pobj;
				    p[0] = Float.parseFloat(stoken[i]);
				}else {
					ObjectWrapper p = (ObjectWrapper) parameters[i];
					p.obj = Float.parseFloat( stoken[i] ) ;
				} 
			}
		} 
	}

	public static void printEntry(OutputStream fout, String out) {
		try {
			fout.write(out.getBytes());
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

	public static void printArray(OutputStream fout, char[] out) {
		try {
			fout.write( new String(out).getBytes() );
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}

//	public static int check_str(char[] str, char[] pat) {
//	  for(int i=0; i < str.length; i++ ) 
//	    if( str[i] == pat[0] ) {
//	      int j=1;
//	      while( i < str.length-1 && j < pat.length) {
//		if( str[i+j] != pat[j] )
//		  break;
//		j++;
//	      }
//	      if( j >= pat.length )
//		return i;
//	    }
//	
//	  return -1;
//	}
	public static int check_str(String str, String pat) {
		  for(int i=0; i < str.length(); i++ ) 
		    if( str.charAt(i) == pat.charAt(0)) {
		      int j=1;
		      while( i+1 < str.length() && j < pat.length()) {
			if( str.charAt(i+j) != pat.charAt(j) )
			  break;
			j++;
		      }
		      if( j >= pat.length() )
			return i;
		    }
		
		  return -1;
		}

	public static void printXmlEntry(OutputStream fout, int tipo, String inkid, String clase) {
		try {
			fout.write( ("<m" +tipo + " xml:id=\"" + inkid + "\">" +  clase + "</m" + tipo + ">\n").getBytes() );
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	public static boolean nextLine(BufferedReader fd, char[] lin) {
		     String line = null;
		     do{
		    	 try {
					line = fd.readLine();
				} catch (IOException e) {
					 line = null;
				}
		    	 if( line == null ) {
		    		 lin[0] = (char)'\0';
		    		 return false;
		    	 }
			     strcpy(lin, line, 0);
			  }while( line.length() <= 1 || line.charAt(0) == '#' ); 
			  return true; 
	  }
}
