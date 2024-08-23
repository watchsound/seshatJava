package seshat;

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
public class Seshat {
 
	public static final int  MAXS = 4096;
	
	public static void usage(String str) {
	  System.err.print( "SESHAT - Handwritten math expression parser\nhttps://github.com/falvaro/seshat\n");
	  System.err.print( "Copyright (C) 2014, Francisco Alvaro\n\n");
	  System.err.print( "Usage: " + str + " -c config -i input [-o output] [-r render.pgm]\n\n" );
	  System.err.print( "  -c config: set the configuration file\n");
	  System.err.print( "  -i input:  set the input math expression file\n");
	  System.err.print( "  -o output: save recognized expression to 'output' file (InkML format)\n");
	  System.err.print( "  -r render: save in 'render' the image representing the input expression (PGM format)\n");
	  System.err.print( "  -d graph:  save in 'graph' the description of the recognized tree (DOT format)\n");
	}

	
	public static void main(String[] args) {
		String input = "";
		String output = "";
		String config = "";
		String render = "";
		String dot = ""; 
		  boolean rc=false,ri=false,ro=false,rr=false,rd=false;
		   

		  
		  Options options = new Options();
	        options.addOption("c", true, "config");
	        options.addOption("i", true, "input");
	        options.addOption("o", true, "output");
	        options.addOption("r", true, "render");
	        options.addOption("d", true, "graph");
	        
	        
	        
	        CommandLineParser parser = new DefaultParser();
	        CommandLine cmd = null;
			try {
				cmd = parser.parse( options, args);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	        if(cmd.hasOption("c")) {
	        	config = cmd.getOptionValue("c");
	        	rc=true;
	        }
	        if(cmd.hasOption("i")) {
	        	input = cmd.getOptionValue("i");
	        	ri=true;
	        }	
	        if(cmd.hasOption("o")) {
	        	output = cmd.getOptionValue("o");
	        	ro=true;
	        }	
	        if(cmd.hasOption("r")) {
	        	render = cmd.getOptionValue("r");
	        	rr=true;
	        }	
	        if(cmd.hasOption("d")) {
	        	dot = cmd.getOptionValue("d");
	        	rd=true;
	        }	
		 
		  
		  //Check mandatory args
		  if( !rc || !ri ) {
		    usage(args[0]);
		    return ;
		  }

		  //Because some of the feature extraction code uses std::cout/cin
		 // ios_base::sync_with_stdio(true);

		  //Load sample and system configuration
		  Sample m = new Sample( input );
		  meParser seshat = new meParser(config);

		  //Render image to file
		  if( rr ) m.render_img(render);

		  //Set output InkML file
		  if( ro ) m.set_out_inkml( output );

		  //Set output DOT graph file
		  if( rd ) m.set_out_dot( dot );

		  //Print sample information
		  m.print();
		  System.out.print("\n");

		  //Parse math expression
		  seshat.parse_me( m);
		  
	}

}
