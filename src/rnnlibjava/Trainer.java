package rnnlibjava;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import lang.ArrayListFloat;
import lang.ResizableListI;

public class Trainer extends DataExporter{
	 // data
	  PrintStream out;
	  Mdrnn  net;
	  Optimiser  optimiser;
	  ConfigFile  config;
	  ResizableListI<Float> wts;
	  ResizableListI<Float> derivs;
	  int epoch;
	  ResizableListI<String> criteria;
	  Map<String, Float>  netErrors;
	  Map<String, Float> netNormFactors;
	  ResizableListI<Float> distortedWeights;
	  String task;
	  float dataFraction;
	  int seqsPerWeightUpdate;
	  boolean batchLearn;
	  //DataList trainFiles;
	  //DataList testFiles;
	  //DataList valFiles;
	  //DatasetErrors trainErrors;
	  //DatasetErrors testErrors;
	  //DatasetErrors valErrors;
	  float inputNoiseDev;
	  float weightDistortion;
	  boolean testDistortions;
	  float l1;
	  float l2;
	  float invTrainSeqs;

	  // MDL parameters
	  boolean mdl;
	  float mdlWeight;
	  float mdlInitStdDev;
	  ResizableListI<Float> mdlStdDevs;
	  ResizableListI<Float> mdlStdDevDerivs;
	  ResizableListI<Float> weightCosts;
	  int mdlSamples;
	  boolean mdlSymmetricSampling;
	  ResizableListI<Float> mdlSeqDerivs;
	  ResizableListI<Float> mdlOldDerivs;
	  float mdlPriorMean;
	  float mdlPriorStdDev;
	  float mdlPriorVariance;
	  Optimiser  mdlOptimiser;
	  ResizableListI<Log> mdlMlErrors;
	  Map<String, Float> mdlSeqErrors;

	  
	  
	public Trainer(PrintStream o, 
			Mdrnn  n, ConfigFile  conf, WeightContainer  wc,
			DataExportHandler deh ) {
		this(o, n, conf, wc, deh, "trainer");
	}
	 // functions
	public Trainer(PrintStream o, 
			Mdrnn  n, ConfigFile  conf, WeightContainer  wc,
			DataExportHandler deh, String name) {
	    super(name, deh);
	    this.out = o;
	    this.net = n;
	    this.optimiser = null;
	    this.config = conf;
	    this.wts = wc.weights;
	    this.derivs = wc.derivatives;
	    this.epoch = 0;
	    this.criteria = net.criteria;
	    this.netErrors = net.errors;
	    this.netNormFactors = net.normFactors;
	    this.task = config.get("task");
	    this.dataFraction = Float.parseFloat( config.get("dataFraction", "1") );
	    this.seqsPerWeightUpdate = Integer.parseInt(  config.get("seqsPerWeightUpdate", "1")   );
	      
	      
	    boolean sss =
	    		config.get("optimiser", "steepest").equals( "rprop" ) && (seqsPerWeightUpdate == 1);
	    
	    this.batchLearn = Boolean.parseBoolean(  config.get("batchLearn", sss ? "true": "false")   );
	 
	    this.inputNoiseDev = Float.parseFloat( config.get("inputNoiseDev", "0") );  
	    this.weightDistortion = Float.parseFloat( config.get("weightDistortion", "0") );  
		     
	    this.testDistortions = Boolean.parseBoolean(  config.get("testDistortions",  "false")   );
		   
	    this.l1 = Integer.parseInt(  config.get("l1", "0")   );
	    this.l2 = Integer.parseInt(  config.get("l2", "0")   );
	    this.mdl = Boolean.parseBoolean(  config.get("mdl",  "false")   );
		 
	    this.mdlWeight = mdl ? Float.parseFloat( config.get("mdlWeight", "1") ) : 1; 
	    this.mdlInitStdDev = mdl ? Float.parseFloat( config.get("mdlInitStdDev", "0.075") ) : 0; 
	     
 	     this.mdlStdDevs =   ArrayListFloat.create(mdl ? wts.size() : 0, mdlInitStdDev);
         this.mdlStdDevDerivs = ArrayListFloat.create(mdlStdDevs.size(), 0);
         this. weightCosts =  ArrayListFloat.create(mdlStdDevs.size(), 0);
 	     this.mdlSamples = (mdl ? Integer.parseInt(  config.get("mdlSamples", "1")) : 0);
 	     
 	    this.mdlSymmetricSampling = (mdl ? Boolean.parseBoolean(  config.get("mdlSymmetricSampling", "false")) : false);
 
 	    this. mdlPriorMean= 0;
 	    this. mdlPriorStdDev= 0;
 	    this.  mdlPriorVariance= 0;
    	this.  mdlOptimiser = null; 
	    String optType = config.get ("optimiser", "steepest");
	    String optName = "weight_optimiser";
	    String ttt = config.get ("learnRate"); 
	    float learnRate = 1e-4f ;
	    if( ttt != null && ttt.length() > 0)
	    	learnRate = Float.parseFloat(ttt);
	    
	    float momentum =  Float.parseFloat( config.get("momentum", "0.9") )  ;
	    if (optType.equals( "rprop" ) ) {
	      optimiser = new Rprop(optName, out, wts, derivs, wc, deh, false);
	    } else {
	      optimiser = new SteepestDescent(
					      optName, out, wts, derivs, wc, deh, learnRate, momentum);
	    }
	    if (mdl) {
	      String mdlOptType = config.get ("mdlOptimiser", optType);
	      String mdlOptName = "mdl_dev_optimiser";
	      if (mdlOptType.equals( "rprop" ) )  {
	        mdlOptimiser = new Rprop(mdlOptName, out, mdlStdDevs, mdlStdDevDerivs, wc, deh, false);
	      } else {
	        mdlOptimiser = new SteepestDescent(
		    mdlOptName, out, mdlStdDevs, mdlStdDevDerivs, wc, deh, 
	            config.get ("mdlLearnRate", learnRate ),
	            config.get ("mdlMomentum", momentum ));
	      }
	       save(mdlPriorMean,"mdlPriorMean");
	       save(mdlPriorVariance,"mdlPriorVariance");
	      wc.save_by_conns(mdlStdDevs, "_mdl_devs");
	      wc.save_by_conns(
	          weightCosts, "_mdl_weight_costs");
	    }
	    save(epoch,"epoch");
	  }

	 public float mdl_mean(int i) {
	    return wts.at(i);
	  }

	 public  float mdl_std_dev(int i) {
	    return Math.abs(mdlStdDevs.at(i));
	  }

	 public  float mdl_variance(int i) {
	    return  (mdlStdDevs.at(i))* (mdlStdDevs.at(i));
	  }

	 public  void mdl_calculate_prior_params() {
	    float W = wts.size();
	    mdlPriorMean = Helpers.mean(wts);
	    mdlPriorVariance = 0;
	    for(int i = 0; i< wts.size(); i++) {
	      mdlPriorVariance += mdl_variance(i) +  (mdl_mean(i) - mdlPriorMean)*  (mdl_mean(i) - mdlPriorMean);
	    }
	    mdlPriorVariance /= W;
	    mdlPriorStdDev = (float) Math.sqrt(mdlPriorVariance);
	  }

	 public float mdl_evaluate() {
	    mdl_calculate_prior_params();
	    float weightNats = 0;
	    for(int i = 0; i < wts.size(); i++) {
	      weightNats += Helpers.KL_normal(
	          mdl_mean(i), mdl_variance(i), mdlPriorMean, mdlPriorVariance);
	    }
	    return weightNats * mdlWeight;
	  }

	 public  void mdl_differentiate( ) {
		 mdl_differentiate(1f);
	 }
	 public  void mdl_differentiate(float scaleFactor ) {
	    mdl_calculate_prior_params();
	    for(int i =0; i < derivs.size(); i++) {
	      float stdDev = mdl_std_dev(i);
	      float mean = mdl_mean(i);
	      derivs.set(i, derivs.at(i) + (scaleFactor * (mean - mdlPriorMean)) / mdlPriorVariance );
	      mdlStdDevDerivs.set(i, mdlStdDevDerivs.at(i) +
	          scaleFactor * ((stdDev/mdlPriorVariance) - (1/stdDev)));
	    }
	  }

	 public  void mdl_sample_weights(int sampleNum) {
	    if (mdlSymmetricSampling && (sampleNum&1) != 0) {
	      for(int i = 0; i < wts.size(); i++) {
	        distortedWeights.set(i,   (2*wts.at(i)) - distortedWeights.at(i) );
	      }
	      ResizableListI<Float> temp =   distortedWeights  ;
	      distortedWeights = wts;
	      wts = temp;
	    } else {
	      distortedWeights = wts;
	      for(int i = 0;  i <  distortedWeights.size(); i++) {
	          float wupdate = WeightContainer. perturb_weight(wts.at(i), mdl_std_dev(i), true);
	          wts.set(i, wupdate);
	      }
	    }
	  }

	  public float mdl_ml_error() {
	    float error = 0;
	    for(int i = 0; i < mdlMlErrors.size(); i++) {
	    	Log err = mdlMlErrors.at(i);
	    	 error -= err.log();
	    }
	   
	    error /= mdlMlErrors.size();
	   // mdlSeqErrors /= mdlSamples;
	    for(String k : mdlSeqErrors.keySet()) {
	    	mdlSeqErrors.put(k, mdlSeqErrors.get(k)/mdlSamples);
	    }
	    
	    netErrors.put( criteria.at(0), error);
	    netErrors = mdlSeqErrors;
	    return error;
	  }

	  public float weight_cost(int i) {
	    return Helpers.KL_normal(
	        mdl_mean(i), mdl_variance(i), mdlPriorMean, mdlPriorVariance);
	  }

//	  void mdl_print_stats() {
//	    mdl_calculate_prior_params();
//	    PRINT(minmax(wts), out);
//	    PRINT(std_dev(wts), out);
//	    PRINT(minmax(mdlStdDevs), out);
//	    PRINT(mean(mdlStdDevs), out);
//	    PRINT(std_dev(mdlStdDevs), out);
//	    PRINT(mdlPriorMean, out);
//	    PRINT(mdlPriorStdDev, out);
//	    PRINT(mdlPriorVariance, out);
//
//	    // store weight costs
//	    LOOP(int i, indices(wts)) {
//	      weightCosts[i] = weight_cost(i);
//	    }
//	    PRINT(minmax(weightCosts), out);
//	    PRINT(mean(weightCosts), out);
//	    PRINT(std_dev(weightCosts), out);
//	  }
//
//	  float evaluate(const DataSequence* seq) {
//	    float error = 0;
//	    if (mdl) {
//	      mdlSeqErrors.clear();
//	      mdlMlErrors.clear();
//	      FOR(s, mdlSamples) {
//	        seq = apply_distortions(seq);
//	        mdl_sample_weights(s);
//	        net.calculate_errors(*seq);
//	        mdlMlErrors += prob_t(-netErrors[criteria.front()], true);
//	        mdlSeqErrors += netErrors;
//	        distortedWeights.swap(wts);
//	      }
//	      error = mdl_ml_error();
//	    } else {
//	      seq = apply_distortions(seq);
//	      error =   net.calculate_errors(*seq);
//	      revert_distortions();
//	    }
//	    return error;
//	  }
//
//	  float differentiate(const DataSequence* seq) {
//	    float error = 0;
//	    if (mdl) {
//	      mdlSeqErrors.clear();
//	      mdlMlErrors.clear();
//	      if ((mdlSamples > 1) || (seqsPerWeightUpdate > 1)) {
//	        flood(mdlSeqDerivs, derivs.size(), 0);
//	        mdlOldDerivs = derivs;
//	      }
//	      FOR(s, mdlSamples) {
//	        if (verbose) {
//	          out << "sample " << s << endl;
//	        }
//	        seq = apply_distortions(seq);
//	        mdl_sample_weights(s);
//	        if ((mdlSamples > 1) || (seqsPerWeightUpdate > 1)) {
//	          fill(derivs, 0);
//	        }
//	        net.train(*seq);
//	        mdlSeqErrors += netErrors;
//	        float sampleError = netErrors[criteria.front()];
//	        if (verbose) {
//	          PRINT(sampleError, out);
//	        }
//	        mdlMlErrors += prob_t(-sampleError, true);
//	        FOR(i, wts.size()) {
//	          float curvature = squared(derivs[i]);
//	          float stdDev = mdl_std_dev(i);
//	          if ((mdlSamples == 1) && (seqsPerWeightUpdate == 1)) {
//	            mdlStdDevDerivs[i] += (curvature*stdDev);
//	          } else {
//	            mdlStdDevDerivs[i] += (curvature*stdDev) / mdlSamples;
//	            mdlSeqDerivs[i] += derivs[i];
//	          }
//	        }
//	        distortedWeights.swap(wts);
//	      }
//	      if (mdlSamples > 1) {
//	        derivs = mdlOldDerivs;
//	        range_divide_val(mdlSeqDerivs, mdlSamples);
//	        range_plus_equals(derivs, mdlSeqDerivs);
//	      }
//	      error = mdl_ml_error();
//	    } else {
//	      seq = apply_distortions(seq);
//	      error =   net.train(*seq);
//	      revert_distortions();
//	    }
//	    return error;
//	  }
//
//	  void regularise(float scaleFactor = 1) {
//	    if (mdl) {
//	      mdl_differentiate(scaleFactor);
//	    } else {
//	      if (l1) {
//	        FOR(i, derivs.size()) {
//	          derivs[i] += scaleFactor * l1 * sign(wts[i]);
//	        }
//	        //float l1Error = nats_to_bits(l1 * abs_sum(wts));
//	        //trainErrors.add_error("l1ErrorBits", l1Error);
//	      }
//	      if (l2) {
//	        FOR(i, derivs.size()) {
//	          derivs[i] += scaleFactor * l2 * wts[i];
//	        }
//	        //float l2Error = nats_to_bits(0.5 * l2 * inner_product(wts, wts));
//	        //trainErrors.add_error("l2ErrorBits", l2Error);
//	      }
//	    }
//	  }
//	  
//	  
//	  
//
//	  boolean print_distortions() {
//	    boolean distortions = false;
//	    if (inputNoiseDev != 0) {
//	      out << "adding noise to input data, std dev " << inputNoiseDev << endl
//	          << endl;
//	      distortions = true;
//	    }
//	    if (weightDistortion) {
//	      out << "adding  noise to weights every sequence, std dev "
//	          << weightDistortion << endl << endl;
//	      distortions = true;
//	    }
//	    if (mdl) {
//	      prt_line(out);
//	      out << "MDL training" << endl;
//	      PRINT(mdlWeight, out);
//	      PRINT(mdlInitStdDev, out);
//	      PRINT(mdlSamples, out);
//	      PRINT(mdlSymmetricSampling, out);
//	      distortions = true;
//	    }
//	    if (l1) {
//	      prt_line(out);
//	      PRINT(l1, out);
//	      distortions = true;
//	    }
//	    if (l2) {
//	      prt_line(out);
//	      PRINT(l2, out);
//	      distortions = true;
//	    }
//	    return distortions;
//	  }
//
//	  const DataSequence* apply_distortions(const DataSequence* seq) {
//	    if (inputNoiseDev) {
//	      seq = add_input_noise(seq);
//	    }
//	    if (weightDistortion) {
//	      distortedWeights = wts;
//	      perturb_weights(distortedWeights, weightDistortion, true);
//	      distortedWeights.swap(wts);
//	    }
//	    return seq;
//	  }
//
//	  const void revert_distortions() {
//	    if (weightDistortion) {
//	      distortedWeights.swap(wts);
//	    }
//	  }
//	  DataSequence* add_input_noise(const DataSequence* seq) {
//	    static DataSequence noisySeq;
//	    noisySeq = *seq;
//	    LOOP(float& f, noisySeq.inputs.data) {
//	      f += Random::normal(inputNoiseDev);
//	    }
//	    return &noisySeq;
//	  }
//
//	  // void print_datasets() const {
//	  //   if (trainFiles.size()) {
//	  //     out << "training data:" << endl << trainFiles;
//	  //   }
//	  //   if (valFiles.size()) {
//	  //     prt_line(out);
//	  //     out << "validation data:" << endl << valFiles;
//	  //   }
//	  //   if (testFiles.size())       {
//	  //     prt_line(out);
//	  //     out << "test data:" << endl << testFiles;
//	  //   }
//	  //   out << endl;
//	  // }
//
//	  void save_data(const string& filename, ConfigFile& conf) {
//	    ofstream fout(filename.c_str());
//	    if (fout.is_open()) {
//	      out << "saving to " << filename << endl;
//	      config.set_val<boolean>("loadWeights", true);
//	      //fout << config << DataExportHandler::instance();
//	    } else {
//	      out << "WARNING trainer unable to save to file " << filename << endl;
//	    }
//	  }
//
//	  void update_weights() {
//	    if (mdl) {
//	      optimiser.update_weights();
//	      if (mdlOptimiser) {
//	        mdlOptimiser.update_weights();
//	      }
//	      LOOP(float& d, mdlStdDevs) {
//	        d = abs(d);
//	      }
//	    } else {
//	      optimiser.update_weights();
//	    }
//	    reset_derivs();
//	  }
//
//	  void reset_derivs() {
//	    fill(derivs, 0);
//	    fill(mdlStdDevDerivs, 0);
//	  }
}
