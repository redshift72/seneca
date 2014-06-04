package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.MapKeyColumn;

import javax.persistence.*;

@Embeddable
public class PermissionProp implements Serializable
{
  
   @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((IDENTITYDOMAIN == null) ? 0 : IDENTITYDOMAIN.hashCode());
		result = prime
				* result
				+ ((INTERSECTIONDOMAIN == null) ? 0 : INTERSECTIONDOMAIN
						.hashCode());
		result = prime * result + ((OTHER == null) ? 0 : OTHER.hashCode());
		result = prime * result
				+ ((OWNERUSER == null) ? 0 : OWNERUSER.hashCode());
		result = prime * result
				+ ((SUBSETDOMAIN == null) ? 0 : SUBSETDOMAIN.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PermissionProp other = (PermissionProp) obj;
		if (IDENTITYDOMAIN != other.IDENTITYDOMAIN)
			return false;
		if (INTERSECTIONDOMAIN != other.INTERSECTIONDOMAIN)
			return false;
		if (OTHER != other.OTHER)
			return false;
		if (OWNERUSER != other.OWNERUSER)
			return false;
		if (SUBSETDOMAIN != other.SUBSETDOMAIN)
			return false;
		return true;
	}

public PermissionProp(SecAttrib oWNERUSER, SecAttrib iDENTITYDOMAIN,
			SecAttrib sUBSETDOMAIN, SecAttrib iNTERSECTIONDOMAIN,
			SecAttrib oTHER) {
		
		OWNERUSER = oWNERUSER;
		IDENTITYDOMAIN = iDENTITYDOMAIN;
		SUBSETDOMAIN = sUBSETDOMAIN;
		INTERSECTIONDOMAIN = iNTERSECTIONDOMAIN;
		OTHER = oTHER;
	}

   public PermissionProp()
   {
	   OWNERUSER = SecAttrib.CONTROL;
		IDENTITYDOMAIN = SecAttrib.MODIFY;
		SUBSETDOMAIN = SecAttrib.MODIFY;
		INTERSECTIONDOMAIN = SecAttrib.READ;
		OTHER = SecAttrib.NONE;
   }

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 /**
	   * sono permessi contestuali che si applicano a qualcuno
	   * NONE non puo fare nulla
	   * READ puo solo leggere l'oggetto
	   * MODIFY puo leggere e modificare l'oggetto ma non puo modificare i le proprietà dei permessi
	   * CONTROL puo fare tutto cambiare i permessi e il  anche prorpietario	
	   * @author fab
	   *
	   */
  public static enum secAttrib  {NONE,READ,MODIFY,CONTROL};	
  
  
  /**
   * ci dice il contesto di applicazione dei permessi
   * 
   * OWNUSER quando l'utente attuale  corrisponde con l'utente proprietario
   * IDENTITYDOMAIN  quando  company domain dell'utente attuale coincide con il company domain dell'oggetto
   * SUBSETDOMAIN     quando  il company domain dell'utente attuale contiene strettamente (e più grande) il company domain dell' oggetto
   * INTERSECTIONDOMAIN  quando l'insieme dei compamy domain ha almeno un'azienda in comune (intersezione non nulla) con il company domain dell'oggetto 
   * OTHER quando l'utente attuale non è il prorpietario dell'ogggetto e il suo company domain ha un intersezione nulla
   * @author fab
   *
   */
 // public static enum nameSecAttrib {OWNERUSER,IDENTITYDOMAIN,SUBSETDOMAIN,INTERSECTIONDOMAIN,OTHER}
  
  /*
  @ElementCollection
  @MapKeyColumn(name="name_permission")
  @Column(name="value_permission")
  @Enumerated(EnumType.STRING)
  private Map<NameSecContext, SecAttrib> permission = new HashMap<NameSecContext, SecAttrib>(); // maps from attribute name to valu
   static {
	   
   }
*/
   
   private SecAttrib OWNERUSER;
   
   private SecAttrib IDENTITYDOMAIN;
   
   private SecAttrib SUBSETDOMAIN;
   
   private SecAttrib INTERSECTIONDOMAIN;
   
   private SecAttrib OTHER;
   
   
   /*
public Map<NameSecContext, SecAttrib> getPermission() {
	return permission;
}



public void setPermission(Map<NameSecContext, SecAttrib> permission) {
	this.permission = permission;
}
*/
@Enumerated(EnumType.STRING)
public SecAttrib getOWNERUSER() {
	return OWNERUSER;
}


   
public void setOWNERUSER(SecAttrib oWNERUSER) {
	OWNERUSER = oWNERUSER;
}


@Enumerated(EnumType.STRING)
public SecAttrib getIDENTITYDOMAIN() {
	return IDENTITYDOMAIN;
}



public void setIDENTITYDOMAIN(SecAttrib iDENTITYDOMAIN) {
	IDENTITYDOMAIN = iDENTITYDOMAIN;
}


@Enumerated(EnumType.STRING)
public SecAttrib getSUBSETDOMAIN() {
	return SUBSETDOMAIN;
}



public void setSUBSETDOMAIN(SecAttrib sUBSETDOMAIN) {
	SUBSETDOMAIN = sUBSETDOMAIN;
}


@Enumerated(EnumType.STRING)
public SecAttrib getINTERSECTIONDOMAIN() {
	return INTERSECTIONDOMAIN;
}



public void setINTERSECTIONDOMAIN(SecAttrib iNTERSECTIONDOMAIN) {
	INTERSECTIONDOMAIN = iNTERSECTIONDOMAIN;
}


@Enumerated(EnumType.STRING)
public SecAttrib getOTHER() {
	return OTHER;
}



public void setOTHER(SecAttrib oTHER) {
	OTHER = oTHER;
}
  
  
 
  
  
}
