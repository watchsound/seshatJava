package seshat;

import java.io.*;
import java.io.FileInputStream;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * String[] cmd1 = { "./seshat", "-c",  "Config/CONFIG", "-i",  "tmp/" + fname  };
 * 
 *
 */
public class Seshat2 {
  
	
	public static void main(String[] args) {
		 File file = new File("inputfile");
		 String input = fileToString(file);
		 String r = parse(input);
		// String r = parse(TestData.T1);
		 System.out.println(r);
	}
	
	public static String parse(String input) {
 
		String config = "/Config/CONFIG";
		
		  //Because some of the feature extraction code uses std::cout/cin
		 // ios_base::sync_with_stdio(true);

		  //Load sample and system configuration
		  Sample m = new Sample( input , true);
		  meParser seshat = new meParser(config, true);

		 
		  //Print sample information
		  m.print();
		  System.out.println( );

		  //Parse math expression
		  return seshat.parse_me( m);
		  
	}
	
	 public static String fileToString(File file)  {
	        try{
	         	 return fileToString( new FileInputStream(file) );
	        }catch(Exception ex){
	        	
	        } 
	        return "";
	    }
	    
	    
	    
	    public static String fileToString(InputStream in) throws IOException {
	        Reader reader = new InputStreamReader(in);
	        StringWriter writer = new StringWriter();
	        char[] buf = new char[1024];
	        while(true) {
	            int n = reader.read(buf);
	            if(n == -1) {
	                break;
	            }
	            writer.write(buf,0,n);
	        }
	        return writer.toString();
	    }

}
