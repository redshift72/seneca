package it.enea.lecop.eca.util;

public class MathUtil {
	public static double arrotonda(double value, int numCifreDecimali) {
	      double temp = Math.pow(10, numCifreDecimali);
	      return Math.round(value * temp) / temp; 
	   }
	
	public static double bound(double value,Double min,Double max)
	{
		if(min!=null && (value<=min))
		{
			return min;
		}
		if(max!=null &&(value >= max))
		{
			return max;
		}
		
		return value;
	}
}
