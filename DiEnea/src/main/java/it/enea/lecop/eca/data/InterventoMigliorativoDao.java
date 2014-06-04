package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.TipologiaValutazione;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class InterventoMigliorativoDao {

	
	@Inject
	  EntityManager man;
	
	/**
	 * Long obj[0]    id
	 * String obj[1]  name
	 * String obj[2]  descrizione
	 * @param tp
	 * @return
	 */
	public List<Object[]> findForSelectedByTipo(TipologiaValutazione tp )
	{
		
  	try
	     {
  	  TypedQuery<Object[]> qr=man.createNamedQuery("InterventoMigliorativo.getForTipoValutazione_ID_NAME",Object[].class);
  	  qr.setParameter("tipovalutazione", tp) ; 
  	
  	
  	     
  	    return qr.getResultList();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
  }
}
