package rnnlibjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lang.ArrayListObj;
import seshat.meParser; 

public class ConfigFile {
	 //data
	  Set<String> used = new HashSet<String>();
	  Map<String, String> params = new HashMap<String,String>();
	  String filename;

	  //functions
	  public ConfigFile(String fname , boolean flag ) throws Exception  {
		  this(fname, '_', flag);
	  }
	 public ConfigFile(String fname, char readLineChar, boolean flag  ) throws Exception  {
		 this.filename = fname;
		 fname = fname.trim();
		 
		  BufferedReader  br = null;
		  if( flag ) {
			  if( !fname.startsWith("/"))
				  fname = "/" + fname;
			   InputStream is = meParser.class.getResourceAsStream(fname); 
			   br =new BufferedReader(new InputStreamReader(is));  
			
		  } else {
			  File f = new File(fname); 
			  if( !f.exists() ) {
			     System.err.println( "Error loading  config file: " +  fname);
			     throw new RuntimeException(  "Error loading  config file: " + fname );
			 } 
	          br =new BufferedReader(new FileReader(f));
		  }
		 
		  
		  String line ="";
	       
	    String name;
	    String val;
	    while( (line = br.readLine()) != null) {
	      String[] fs = line.trim().split(" ");
	      name = fs[0];
	      val = fs[1]; 
	      if(name.charAt(0) != '#') {
	    	 line = line.substring(name.length() + 1 + val.length());
	    	  if ( name.indexOf(readLineChar) >=0 && line.length() > 1) {
	          val += line;
	        }
	        params.put(name, val);
	      }
	    }
	  }
	  public boolean contains(String name)   {
	    return  params.containsKey(name);
	  }
	  public boolean remove(String name) {
	    if (contains(name)) {
	      params.remove(name);
	      used.remove(name);
	      return true;
	    }
	    return false;
	  }
	  public   String   set_val(String name,  String val ) {
		  return set_val(name, val, true);
	  }
	 public  String  set_val(String name,  String  val, boolean valUsed ) {
		String ss = "0".equals(val) || val.length() == 0 ? "false" : "true";
	    
	    params.put(name, ss);
	    if (valUsed) {
	      used.add(name);
	    }
	    return val;
	  }
	 public String get(String name, String defaultVal) {
	    String it = params.get(name);
	    if (it == null) {
	      set_val (name, defaultVal);
	      return defaultVal;
	    }
	    return it;
	  }
	 public boolean get(String name, boolean defaultVal) {
		    String it = params.get(name);
		    if (it == null) {
		        set_val (name, defaultVal +"");
		        return defaultVal;
		    }
		    return Boolean.parseBoolean(it);
		  }
	 public int get(String name, int defaultVal) {
		    String it = params.get(name);
		    if (it == null) {
		        set_val (name, defaultVal +"");
		        return defaultVal;
		    }
		    return Integer.parseInt(it);
		  }
	 public float get(String name, float defaultVal) {
		    String it = params.get(name);
		    if (it == null) {
		        set_val (name, defaultVal +"");
		        return defaultVal;
		    }
		    return Float.parseFloat(it);
		  }
	 public String get(String name) {
	    String it = params.get(name);
	    if( it == null ) {
	    	throw new RuntimeException("param '" + name + "' not found in config file '" + filename);
	    }
	    used.add(name);
	    return it;
	  }
	 public ArrayListObj<Integer> get_list_int( String name){
		 return get_list_int(name,  ',');
	 }
	  public ArrayListObj<Integer> get_list_int( String name,  char delim ) {
		    ArrayListObj<Integer> vect = new ArrayListObj<Integer>();
		    String it = params.get(name);
		    if (it != null ) {
		       String[] fs = it.split(delim +"");
		       vect.ensureCapacity(fs.length);
		       for(String f : fs) {
		    	   vect.add(Integer.parseInt(f));
		       }
		      used.add(name);
		    }
		    return vect;
		  }
	 
	  public ArrayListObj<String> get_list( String name){
			 return get_list(name,  ',');
		 }
		 
		  public ArrayListObj<String> get_list( String name,  char delim ) {
		    ArrayListObj<String> vect = new ArrayListObj<String>();
		    String it = params.get(name);
		    if (it != null ) {
		    	vect =  Strings.split_with_repeat(it, delim) ;
//		       String[] fs = it.split(delim +"");
//		       vect.ensureCapacity(fs.length);
//		       for(String f : fs) {
//		    	   vect.add(f);
//		       }
		      used.add(name);
		    }
		    return vect;
		  }
	  public ArrayListObj<String> get_list(String  name, String defaultVal, int length){
		  return get_list(name, defaultVal, length, ',');  
	  }
	  public ArrayListObj<String> get_list(String  name, String defaultVal, int length,
	      char delim  ) {
	    ArrayListObj<String> vect = get_list(name, delim);
	    if( vect.size() == 1)
	    	defaultVal = vect.at(0);
	    Helpers.resize_self(vect, length, defaultVal ); 
	    used.add(name);
	    return vect;
	  }
	  public ArrayListObj<Integer> get_list(String  name, int defaultVal, int length){
		  return get_list(name, defaultVal, length, ',');  
	  }
	  public ArrayListObj<Integer> get_list(String  name, int defaultVal, int length,
	      char delim  ) {
	    ArrayListObj<String> vect = get_list(name, delim);
	    ArrayListObj<Integer> vint = new ArrayListObj<>();
	    for(String v : vect) {
	    	vint.add(Integer.parseInt(v));
	    }
	    if( vint.size() == 1)
	    	defaultVal = vint.at(0);
	    
	    Helpers.resize_self(vint, length, defaultVal ); 
	    used.add(name);
	    return vint;
	  }
	  public ArrayListObj<Boolean> get_list(String  name, boolean defaultVal, int length){
		  return get_list(name, defaultVal, length, ',');  
	  }
	  public ArrayListObj<Boolean> get_list(String  name, boolean defaultVal, int length,
	      char delim  ) {
	    ArrayListObj<String> vect = get_list(name, delim);
	    ArrayListObj<Boolean> vint = new ArrayListObj<>();
	    for(String v : vect) {
	    	vint.add(Boolean.parseBoolean(v));
	    }
	    if( vint.size() == 1)
	    	defaultVal = vint.at(0);
	    Helpers.resize_self(vint, length, defaultVal ); 
	    used.add(name);
	    return vint;
	  }
	  
	
	  
	  
	  public ArrayListObj<ArrayListObj<String>> get_array(String name){
		  return get_array(name, ':', ',');
	  }
	  public ArrayListObj<ArrayListObj<String>> get_array(String name, char delim1){
		  return get_array(name, delim1, ',');
	  }
	  
	  
	  public ArrayListObj<ArrayListObj<String>> get_array(String name, char delim1, char delim2  ) {
	    ArrayListObj<ArrayListObj<String>> array = new ArrayListObj<ArrayListObj<String>>();
	    String it = params.get(name);
	    if (it != null) {
	        for(String f : it.split(delim1+"")) {
	        	array.add( Strings.split_with_repeat(f, delim2));
//	        	String[] ff = f.split(delim2 +"");
//	        	ArrayListObj<String> newrow = new ArrayListObj<String>(ff.length);
//	        	for(String fff : ff)
//	        		newrow.add(fff);
//	        	array.add(newrow); 
	      }
	      used.add(name);
	    }
	    return array;
	  }
	  public ArrayListObj<ArrayListObj<String>> get_array(String name, String defaultStr, int length){
		  return get_array(name, defaultStr, length,';', ',');
	  }
	  public ArrayListObj<ArrayListObj<String>> get_array(String name, String defaultStr, int length, char delim1){
		  return get_array(name, defaultStr, length,delim1, ',');
	  }
	  
	  public ArrayListObj<ArrayListObj<String>> get_array(String name, String defaultStr, int length, char delim1, char delim2 ) {
	    ArrayListObj<ArrayListObj<String>> array = get_array(name, delim1, delim2);
	    if( array.size() == 1) {
	    	 Helpers.resize_self(array, length, array.get(0)); 
	    }
	    else { 
        	ArrayListObj<String> newrow = Strings.split_with_repeat(defaultStr, delim2);
            Helpers.resize_self(array, length,newrow);
	    } 
	    used.add(name);
	    return array;
	  }
	  public void warn_unused(OutputStream out) {
		  warn_unused(out, true);
	  }
	 public void warn_unused(OutputStream out, boolean removeUnused) {
	    lang.ArrayListObj<String> unused = new ArrayListObj<String>();
	    for(String p : params.keySet()) {
	      if (! used.contains(p)) {
	         unused.add(p);
	      }
	    }
	    if (unused.size()>0) {
	      for(String p : unused){
	    	  try {
				out.write(("WARNING: " + p + " in config but never used\n").getBytes());
			} catch (IOException e) { 
				e.printStackTrace();
			} 
	        if (removeUnused) {
	          params.remove(p);
	        }
	      }
	      try {
			out.write("\n".getBytes());
		} catch (IOException e) { 
			e.printStackTrace();
		}
	    }
	  } 

	public static OutputStream print(OutputStream out, ConfigFile configFile){
		for(Entry<String, String> entry : configFile.params.entrySet())
			try {
				out.write( ("(" + entry.getKey() + "->" + entry.getValue() + ") ").getBytes());
			} catch (IOException e) { 
				e.printStackTrace();
			}
	  return out;
	}

}
