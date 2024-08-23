package rnnlibjava;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lang.ArrayListObj;

public class StringAlignment {
	//data
		 
		Map<Integer, Map<Integer, Integer>> subsMap = new HashMap<>();
		Map<Integer, Integer> delsMap = new HashMap<>();
		Map<Integer, Integer> insMap = new HashMap<>();
		int[][] matrix ;
		int substitutions;
		int deletions;
		int insertions;
		int distance;	
		int subPenalty;
		int delPenalty;
		int insPenalty;
		int n;
		int m;
		
		private void increaseSubsMap(ArrayListObj<Integer> reference_sequence, ArrayListObj<Integer> test_sequence, int i, int j) {
			Map<Integer, Integer> r = subsMap.get( reference_sequence.get(i) );
			if( r == null ) {
				r = new HashMap<>();
				subsMap.put( reference_sequence.get(i), r);
			}
			
			Integer r2 = r.get( test_sequence.get(j) );
			if( r2 == null ) {
				r2 = 1;
			} else {
				r2++;
			}
			r.put(test_sequence.get(j), r2) ;
		}
		private void increaseMap(Map<Integer, Integer> map, int i) {
			Integer r = map.get(i);
			if( r == null) {
				r = 1;
			} else {
				r++;
			}
			map.put(i, r);
		}
		
		//functions
		StringAlignment (ArrayListObj<Integer> reference_sequence, ArrayListObj<Integer> test_sequence, 
						 boolean trackErrors    ){
			subPenalty = 1;
			delPenalty = 1;
			insPenalty =1;
			boolean backtrace = true ;
			n = reference_sequence.size() ; 
			m = (test_sequence.size());
	 
			if (n == 0)
			{
				substitutions = 0;
				deletions = 0;
				insertions = m;
				distance = m;
			}
			else if (m == 0)
			{
				substitutions = 0;
				deletions = n;
				insertions = 0;
				distance = n;
			}
			else
			{
				//initialise the matrix
				matrix = new int[n+1][m+1];
				for(int i = 0; i < matrix.length; i++) {
					for(int j = 0; j < matrix[i].length; j++) {
						matrix[i][j] =0;
					}
				}
				for(int i = 0; i < n+1; i++) {
					matrix[i][0] = i;
				}
				for(int j = 0; j < m+1; j ++) {
					matrix[0][j]=j;
				} 
				 
				
				//calculate the insertions, substitutions and deletions 
				for(int i = 1; i < n+1; i++)  
				{	
					int s_i = reference_sequence.get(i-1);
					for(int j = 1; j < m+1; j++) 
					{
						int t_j = test_sequence.get(j-1); 
						int cost = ((s_i == t_j) ? 0 : 1);
						  int above = matrix[i-1][j];
						  int left = matrix[i][j-1];
						  int diag = matrix[i-1][j-1];
						  int cell = Math. min(above + 1,			// deletion
											Math. min(left + 1,			// insertion
												 diag + cost));		// substitution
						
						matrix[i][j]=cell;
					}
				}
				
				//N.B sub,ins and del penalties are all set to 1 if backtrace is ignored
				if (backtrace)
				{				
					int i = n;
					int j = m;
					substitutions = 0;
					deletions = 0;
					insertions = 0;
					
					// Backtracking
					while (i != 0 && j != 0) 
					{
						if (matrix[i][j] == matrix[i-1][j-1]) 
						{
							--i;
							--j;
						}
						else if (matrix[i][j] == matrix[i-1][j-1] + 1)
						{
							if (trackErrors)
							{
								increaseSubsMap(reference_sequence, test_sequence, i,j);
							}
							++substitutions;
							--i;
							--j;
						}
						else if (matrix[i][j] == matrix[i-1][j] + 1)
						{
							if (trackErrors)
							{
								increaseMap(delsMap, reference_sequence.get(i));
							}
							++deletions;
							--i;
						}
						else 
						{
							if (trackErrors)
							{
								increaseMap(insMap,  test_sequence.get(j));
							}
							++insertions;
							--j;
						}
					}
					while (i != 0)
					{
						if (trackErrors)
						{
							increaseMap(delsMap, reference_sequence.get(i));
						}
						++deletions;
						--i;
					}
					while (j != 0) 
					{
						if (trackErrors)
						{
							increaseMap(insMap,  test_sequence.get(j));
						}
						++insertions;
						--j;
					}
					
					// Sanity check:
				//	check((substitutions + deletions + insertions) == matrix[n][m], 
					//	  "Found path with distance " + str(substitutions + deletions + insertions) + 
					//	  " but Levenshtein distance is " + str(matrix[n][m])); 
					
					//scale individual errors by penalties
					distance = (subPenalty*substitutions) + (delPenalty*deletions) + (insPenalty*insertions);
				}
				else
				{
					distance = matrix[n][m];
				}
			}
		} 
}
