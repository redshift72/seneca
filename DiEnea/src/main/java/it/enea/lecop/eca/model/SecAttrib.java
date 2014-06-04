package it.enea.lecop.eca.model;



/**
 * sono permessi contestuali che si applicano a qualcuno
 * NONE non puo fare nulla
 * READ puo solo leggere l'oggetto
 * MODIFY puo leggere e modificare l'oggetto ma non puo modificare i le proprietà dei permessi
 * CONTROL puo fare tutto cambiare i permessi e il  anche prorpietario	
 * @author fab
 *
 */
public enum SecAttrib {
	NONE,READ,MODIFY,CONTROL
}
