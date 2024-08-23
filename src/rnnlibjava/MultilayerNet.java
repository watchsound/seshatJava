package rnnlibjava;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import lang.ArrayListObj;
import rnnlibjava.DataExporter.DataExportHandler;

public class MultilayerNet extends Mdrnn{
	 public MultilayerNet(PrintStream out, 
			 ConfigFile  conf,  DataHeader  data, 
			 WeightContainer weight, DataExportHandler  deh) { 
		    super(out, conf, data, weight, deh);
		 
		    String task = conf.get("task");
		    ArrayListObj<Integer>  hiddenSizes = conf.get_list_int ("hiddenSize" );
		   // assert(hiddenSizes.size());
		    ArrayListObj<String>   hiddenTypes =
		        conf.get_list("hiddenType", "lstm", hiddenSizes.size());
		    ArrayListObj<ArrayListObj<Integer>> hiddenBlocks = Helpers.str2int_2d( conf.get_array("hiddenBlock"));
		  //  assert(hiddenBlocks.size() < hiddenSizes.size());
		    
		    ArrayListObj<Integer> subsampleSizes = conf.get_list_int("subsampleSize");
		 //   assert(subsampleSizes.size() < hiddenSizes.size());
		    String subsampleType = conf.get ("subsampleType", "tanh");
		    boolean subsampleBias = Boolean.parseBoolean( conf.get ("subsampleBias", "false") );
		    ArrayListObj<Boolean> recurrent =  conf.get_list("recurrent", true, hiddenSizes.size());
		    Layer  input = this.get_input_layer();
		    for(int i = 0; i < hiddenSizes.length(); i++) { 
		      String level_suffix = Strings.int_to_sortable_string(i, hiddenSizes.size());
		      this.add_hidden_level(
		          hiddenTypes.at(i), (int) hiddenSizes.at(i), (boolean)recurrent.at(i),
		          "hidden_" + level_suffix, true);
		      this.connect_to_hidden_level(input, i);
		      Vector<Layer> blocks = new Vector<>();
		      if (i < hiddenBlocks.size()) {
		        for(Layer  l : hiddenLevels.get(i)) {
		          blocks.add(  this.add_layer(new BlockLayer(l, hiddenBlocks.at(i), wc, deh)) );
		        }
		      }
		     Vector<Layer> topLayers = blocks.size() >0 ? blocks : hiddenLevels.get(i);
		      if (i < subsampleSizes.size()) {
		    	  int[] ddd = new int[this.num_seq_dims()];
		    	  for(int d = 0; d < ddd.length; d++) {
		    		  ddd[i] = 1;
		    	  }
		        input = this.add_layer(
		            subsampleType, "subsample_" + level_suffix, subsampleSizes.at(i), ddd, subsampleBias, false);
		        for(Layer  l : topLayers) {
		          this.connect_layers(l, input, new int[0]);
		        }
		      } else if (i <  hiddenSizes.size()-1 ) {
		        input = this.add_layer(new GatherLayer(
								"gather_" + level_suffix, topLayers, wc, deh));
		      }
		    }
		    conf.set_val("inputSize", inputLayer.output_size() +"");
		    if (data.targetLabels.size() >0) {
		      String labelDelimiters = ",.;:|+&_~*%$#^=-<>/?{}[]()" ;
		      for(char c : labelDelimiters.toCharArray()) {
		        boolean goodDelim = true;
		        for(String s : data.targetLabels) {
		          if (  s.indexOf(c+"") >=0)	{
		            goodDelim = false;
		            break;
		          }
		        }
		        if (goodDelim) {
		        	 final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        	    try (PrintStream ps = new PrintStream(baos, true, "UTF-8")) {
		        	    	 Helpers. print_range(ps, data.targetLabels, c +"");
		        	    }catch(Exception e){
		        	    	e.printStackTrace();
		        	    }
		        	    String ss = new String(baos.toByteArray(), StandardCharsets.UTF_8);
			            conf.set_val("targetLabels", ss );
			           conf.set_val("labelDelimiter", c +"");
			          break;
		        }
		      }
		    }
		    String outputName = "output";
		    Layer  output = null;
		    int outSeqDims = ( task.indexOf("sequence_") >=0 ) ? 0 : num_seq_dims() ;
		    if (  task.indexOf(  "classification") >=0 ) {
		      output = add_output_layer(ClassificationLayer.make_classification_layer(
			  out, outputName, outSeqDims, data.targetLabels, wc, deh));
		    } else if (task.equals( "transcription")   ) {
		     // check(this.num_seq_dims(), "cannot perform transcription wth 0D net");
		      output = add_output_layer(new TranscriptionLayer(
		          out, outputName, data.targetLabels, wc, deh, Boolean.parseBoolean( conf.get(
		              "confusionMatrix", "false")) ));
		      if (this.num_seq_dims() > 1) {
		    	  boolean[] blist = new boolean[1];
		    	  blist[0] = true;
		        output = this.collapse_layer(
		            hiddenLayers.get(hiddenLayers.size()-1), output,blist);
		      }
		    } else {
		     // check(false, "unknown task '" + task + "'");
		    }
		    if(this.num_seq_dims() != 0 &&  task.indexOf( "sequence_") >=0 ) {
		      output = this.collapse_layer(hiddenLayers.get(hiddenLayers.size()-1), output, new boolean[0]);
		    }
		    connect_from_hidden_level(  hiddenLevels.size()-1, output);
		  }
}
