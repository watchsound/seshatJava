package rnnlibjava;

import java.util.*;

 

public class Pair<T,K> {
	 

//	public static void main(String[] arg) {
//		String test = "abc*def*gg";
//		String[] ts = test.split("\\*");
//		System.out.println(ts.length);
//	}
	public T first;
	public K second;
	
	public Pair() {}
	
	public Pair(T t, K k) {
		this.first = t;
		this.second = k;    
	}
	public String toString() {
		return first + " " + second;
	}
	 
	public static class PairInt { 
		public int first;
		public int second;
		
		public PairInt() {} 
		public PairInt(int t, int k) {
			this.first = t;
			this.second = k;    
		} 
		public String toString() {
			return first + " " + second;
		}
	}
	public static class PairFloat { 
		public float first;
		public float second;
		
		public PairFloat() {} 
		public PairFloat(float t, float k) {
			this.first = t;
			this.second = k;    
		} 
		public String toString() {
			return first + " " + second;
		}
	}
	public static class PairLog { 
		public Log first;
		public Log second;
		
		public PairLog() {} 
		public PairLog(Log t, Log k) {
			this.first = t;
			this.second = k;    
		} 
		public String toString() {
			return first + " " + second;
		}
	}
	public static class PairLog2 { 
		public float first;
		public float second;
		
		public PairLog2() {} 
		public PairLog2(float t, float k) {
			this.first = t;
			this.second = k;    
		} 
		public String toString() {
			return first + " " + second;
		}
	}
}
