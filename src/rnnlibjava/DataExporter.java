package rnnlibjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import lang.ArrayListObj;
import lang.ResizableListI;

public class DataExporter {
    public static interface Val {
	    void print(PrintStream out) ;
	    boolean load(Scanner in, PrintStream out ) ;
	};
	
	public static class RangeVal  implements Val{ 
		ResizableListI<Float> range;
		public RangeVal(ResizableListI<Float> r) {
			this.range = r;
		}
		@Override
		public void print(PrintStream out) {
			out.print(Helpers.size(range) + " ");
			Helpers.print_range(out, range, "");
		}
		@Override
		public boolean load(Scanner in, PrintStream out) {
			int size = in.nextInt();
			if( size == Helpers.size(range)) {
				//FIXME
				for(int i = 0; i < range.size(); i++) {
					try { 
						float v = in.nextFloat();// System.out.println(i + " " + v);
						range.set(i, v);
					}catch(Exception ex) {
						ex.printStackTrace();
					} 
				}
				
				return true;
			}
			else {
				out.println(  "ERROR saved size " + 
			       size + " != current size " + Helpers.size(range) );
			}
			
			return false;
		} 
	}
	public static class ParamVal<T> implements Val{ 
		T param;
		public ParamVal(T r) {
			this.param = r;
		}
		@Override
		public void print(PrintStream out) {
			out.print( param ); 
		}
		@Override
		public boolean load(Scanner in, PrintStream out) {
			if( param instanceof Integer) { 
				param = (T)(Integer) in.nextInt(); 
			}
			if( param instanceof Float) {
				param = (T)(Float) in.nextFloat(); 
			}
			if( param instanceof String) {
				param = (T)(String) in.next();
			} 
			if( param instanceof Log) {
				float f = in.nextFloat();
				param = (T) new Log(f,true);
			} 
			return true;
		} 
	}
	public static class SeqBufferVal<T> implements Val{ 
		SeqBuffer<T> array;
		ArrayListObj<String> labels;
		public SeqBufferVal(SeqBuffer<T> a ) {
			this.array = a;
			this.labels = new ArrayListObj<String>();
		}
		public SeqBufferVal(SeqBuffer<T> a, ArrayListObj<String> labels) {
			this.array = a;
			this.labels = labels;
		}
		@Override
		public void print(PrintStream out) {
			if( !array.empty() ) {
				if( labels != null && labels.size()>0) {
					out.print("LABELS: ");
					for(String l : labels)
						out.print(l + " ");
					out.println();
				}
				array.print(out);
			}; 
		}
		@Override
		public boolean load(Scanner in, PrintStream out) {
			// TODO Auto-generated method stub
			return false;
		}
	 
	}
	public static class SeqBufferVal_float implements Val{ 
		SeqBuffer_float array;
		ArrayListObj<String> labels;
		public SeqBufferVal_float(SeqBuffer_float a ) {
			this.array = a;
			this.labels = new ArrayListObj<String>();
		}
		public SeqBufferVal_float(SeqBuffer_float a, ArrayListObj<String> labels) {
			this.array = a;
			this.labels = labels;
		}
		@Override
		public void print(PrintStream out) {
			if( !array.empty() ) {
				if( labels != null && labels.size()>0) {
					out.print("LABELS: ");
					for(String l : labels)
						out.print(l + " ");
					out.println();
				}
				array.print(out);
			}; 
		}
		@Override
		public boolean load(Scanner in, PrintStream out) {
			// TODO Auto-generated method stub
			return false;
		}
	 
	}
	public static class SeqBufferVal_log implements Val{ 
		SeqBuffer_log array;
		ArrayListObj<String> labels;
		public SeqBufferVal_log(SeqBuffer_log a ) {
			this.array = a;
			this.labels = new ArrayListObj<String>();
		}
		public SeqBufferVal_log(SeqBuffer_log a, ArrayListObj<String> labels) {
			this.array = a;
			this.labels = labels;
		}
		@Override
		public void print(PrintStream out) {
			if( !array.empty() ) {
				if( labels != null && labels.size()>0) {
					out.print("LABELS: ");
					for(String l : labels)
						out.print(l + " ");
					out.println();
				}
				array.print(out);
			}; 
		}
		@Override
		public boolean load(Scanner in, PrintStream out) {
			// TODO Auto-generated method stub
			return false;
		}
	 
	}
	
	public static class DataExportHandler {
	  // data
	  Map<String, DataExporter> dataExporters = new HashMap<>();

	  // functions
	  public void save(PrintStream out) {
		  for(DataExporter ex : dataExporters.values()) {
			  ex.save(out);
		  }
	  }
	  public void load(ConfigFile conf, PrintStream out) {
		  for(String ex : dataExporters.keySet()) {
			  DataExporter de = dataExporters.get(ex);
			  if(!de.load(conf)) {
				  out.print(" for '" + ex + "' in confi file " + conf.filename + ", existing.." );
				  throw new RuntimeException( " for '" + ex + "' in confi file " + conf.filename + ", existing.."  );
			  }
		  }
	  }
	  public void display(String path) {
		  for(String ex : dataExporters.keySet()) {
			  DataExporter de = dataExporters.get(ex);
			  for(String val : de.displayVals.keySet()) {
				  String filename = path + ex+ "_" + val;
				  File f = new File(filename);
				  try {
					PrintStream out = new PrintStream(f);
					Val v = de.displayVals.get(val);
					v.print(out);
				} catch (FileNotFoundException e) { 
					e.printStackTrace();
				}
			  }
		  }
	  }
	  public void register(DataExporter dataExporter) {
		  dataExporters.put(dataExporter.name, dataExporter);
	  }
	  
	};
		
		
	 Map<String, Val> saveVals = new HashMap<>();
	 Map<String, Val> displayVals = new HashMap<>();	
	
	 String name;
	 
	 public DataExporter(String name, DataExportHandler  DEH) {
		 this.name = name; 
	     DEH.register( this ) ;
	  }
		 
	 public void save(PrintStream out)   {
		 for(String it : saveVals.keySet()) {
			 out.println( name + "_" + it + " " + saveVals.get(it));
		 }
	  }
	 public boolean load(ConfigFile conf) {
		 return load(conf, Helpers.cout);
	 }
	 public boolean load(ConfigFile conf, PrintStream out) { 
		 for(String val : saveVals.keySet()) {
			 String lookupName = name + "_" + val;
			 String displayName = name + "." + val;
			 if ( Helpers.verbose) {
				 out.println("loading " + displayName);
			 }
			 String stringIt = conf.params.get(lookupName);
			 if( stringIt == null ) {
				 out.println("WARNING: unable to find '" + displayName + "'" );
			 } else {
				 boolean s = saveVals.get(val).load(new Scanner(stringIt), out);
				 if( s ) {
					 conf.params.remove(stringIt);
				 } else {
					 out.println("WARNING: unable to find '" + displayName + "'" );
					 return false;
				 }
			 }
		 }
		 return true;
	 }
		   
		   
	 public void delete_val(Map<String, Val>  vals, String name) {
		 if( vals.containsKey(name))
			 vals.remove(name); 
     }
	 public void save(int  param, String name) {
		    delete_val(saveVals, name);
		    saveVals.put(name , new ParamVal<Integer>(param));
	 }
	 public void save(float  param, String name) {
		    delete_val(saveVals, name);
		    saveVals.put(name , new ParamVal<Float>(param));
	 }
	 public void save(String  param, String name) {
		    delete_val(saveVals, name);
		    saveVals.put(name , new ParamVal<String>(param));
	 }
	 public void save(Log  param, String name) {
		    delete_val(saveVals, name);
		    saveVals.put(name , new ParamVal<Log>(param));
	 }
	 
	 public void save_range(ResizableListI<Float>  range, String name) {
		    delete_val(saveVals, name);
		    saveVals.put(name , new RangeVal(range));
	 }
	 
	 
	 public void display(SeqBuffer array, String name) {
		 display(array, name , new ArrayListObj<String>());
	 }
	 public void display(SeqBuffer array, String name, ArrayListObj<String> labels) {
		 delete_val(displayVals, name);
		 displayVals.put(name, new SeqBufferVal(array, labels));
	 }
	 public void display(SeqBuffer_float array, String name) {
		 display(array, name , new ArrayListObj<String>());
	 }
	 public void display(SeqBuffer_float array, String name, ArrayListObj<String> labels) {
		 delete_val(displayVals, name);
		 displayVals.put(name, new SeqBufferVal_float(array, labels));
	 } 
	 public void display(SeqBuffer_log array, String name) {
		 display(array, name , new ArrayListObj<String>());
	 }
	 public void display(SeqBuffer_log array, String name, ArrayListObj<String> labels) {
		 delete_val(displayVals, name);
		 displayVals.put(name, new SeqBufferVal_log(array, labels));
	 } 
}
