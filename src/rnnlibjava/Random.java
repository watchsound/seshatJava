package rnnlibjava;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class Random {
	
	////normal distribution with mean 0 std dev 1
	public static float normal()
	{
		NormalDistribution normalDistributioin = new NormalDistribution(0,1); 
		return (float) normalDistributioin.sample();
	}
	
	////normal distribution with user defined mean, dev
	public static float  normal(float dev, float mean)
	{
		return (normal() * dev) + mean;
	}
//	unsigned int Random::set_seed(unsigned int seed)
//	{
//		if (seed == 0)
//		{
//			time_period p (ptime(date(1970, Jan, 01)), microsec_clock::local_time());
//			seed = (unsigned int)p.length().ticks();
//		}
//		srand(seed);
//		generator.seed(seed);
//		return seed;
//	}
	
	//uniform real in (-range, range)
	public static float uniform(float range)
	{
		return (uniform()*2*range) - range;
	}
	////uniform real in (0,1)
	public static float uniform()
	{
		UniformRealDistribution d = new UniformRealDistribution(0,1);
		return (float) d.sample();
	}

}
