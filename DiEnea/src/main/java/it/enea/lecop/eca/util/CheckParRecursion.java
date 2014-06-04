package it.enea.lecop.eca.util;

public class CheckParRecursion {
 
	/**
	 * @return the numMaxRecursive
	 */
	public int getNumMaxRecursive() {
		return numMaxRecursive;
	}

	/**
	 * 
	 * @param numMaxRecursive deve essere > 0
	 */
	public CheckParRecursion(int numMaxRecursive) {
		
		this.numMaxRecursive = numMaxRecursive;
		index=0;
	}

  private int numMaxRecursive;
  private int index=0;
  
 synchronized public boolean isNewRicursion()
  {
	  if(index==numMaxRecursive)
		  return false;
	  else
	  {
		  index++;
		  return true;
	  }
  }
    
  
}
