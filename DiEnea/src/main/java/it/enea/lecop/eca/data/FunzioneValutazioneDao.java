package it.enea.lecop.eca.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.ConsumoEnergetico;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.ParamIntervento;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.SelectDescr;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaFunzioneDiValutazione;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


@Stateless
@LocalBean
public class FunzioneValutazioneDao {

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
  	  Query qr=man.createNamedQuery("FunzioneDiValutazione.getForTipoValutazione_ID_NAME");
  	  qr.setParameter("tipovalutazione", tp) ; 
  	
  	
  	     
  	    return qr.getResultList();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
  }
	
	/**
	 * Long obj[0]    id
	 * String obj[1]  name
	 * String obj[2]  descrizione
	 * @param tp
	 * @return
	 */
	public List<Object[]> findForSelectedByTipo(TipologiaValutazione tp ,TipologiaEdifici tipoed)
	{
		if(tp==null || tipoed ==null) return null;
  	try
	     {
  	  Query qr=man.createNamedQuery("FunzioneDiValutazione.getForTipoValutazione_TipoEdificio_ID_NAME");
  	  qr.setParameter("tipovalutazione", tp) ; 
  	
  	
  	
  	     
  	    return qr.getResultList();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
  }
	
	public FunzioneDiValutazione findById(Long id)
	{
  	try
	     {
  	  TypedQuery<FunzioneDiValutazione> qr=man.createNamedQuery("FunzioneDiValutazione.findById",FunzioneDiValutazione.class);
  	  qr.setParameter("id", id) ; 
 	  
  	    return qr.getSingleResult();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
  }
	
	public List<FunzioneDiValutazione> findByTipoValutazione(TipologiaValutazione id)
	{
  	try
	     {
  	  TypedQuery<FunzioneDiValutazione> qr=man.createNamedQuery("FunzioneDiValutazione.getForTipoValutazione",FunzioneDiValutazione.class);
  	  qr.setParameter("tipovalutazione", id) ; 
 	  
  	    return qr.getResultList();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
  }
	
	
	 public List<FunzioneDiValutazione> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
		{
			return SecureGenericSelect.getAll(FunzioneDiValutazione.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
		}
	 
	 public List<FunzioneDiValutazione> findAll()
		{
			return SecureGenericSelect.getAllNoSec(FunzioneDiValutazione.class, man);
		}
	 
	 
	  public Set<TipologiaEdifici> getAllTipoEdifici(Long funcId)
	  {
		  FunzioneDiValutazione function = findById(funcId);
	      if (function==null)
	    	  return null;
		  
	      function.getApplicaTipoEdifici().size();
		  return function.getApplicaTipoEdifici();
	  }
	  
	  public Boolean save(FunzioneDiValutazione funz)
	    {
	    	try
		     {
	    		
	    	  if (findById(funz.getId())== null)	
	   	       {
	    		  man.persist(funz);
	   	       }
	    	  else {
	    		     return (update(funz)!=null);
	    	  }
		     }catch (RuntimeException e) 
		     {
		    	 e.printStackTrace();
				return false;
			 } catch (Exception e) {
				e.printStackTrace();
				 return false;
			}
	   
	       return true;
	  }
	  
	  public FunzioneDiValutazione update(FunzioneDiValutazione funz)
	  {
	    	FunzioneDiValutazione attachfunz=null;
	    	try
		     {
	    		attachfunz=man.merge(funz);
		     }catch (RuntimeException e) 
		     {
		    	 e.printStackTrace();
				return null;
			 } catch (Exception e) {
				
				 e.printStackTrace();
				 return null;
			}
	   
	       return attachfunz;
	    }
	    
	  public boolean save(Long idFunz, String nomeFunz, String descrizione, TipologiaValutazione tipoVal, TipologiaFunzioneDiValutazione tipoFunz, Set<TipologiaEdifici> tipoEdifici, String espressione, Map<String, ParamIntervento> parameters ,CompanyDomain ownLoginDomain, User loginUser ) {
		    FunzioneDiValutazione funz;
						
			funz=findById(idFunz);
										
			try{
				
				
				if (funz== null){
					funz=new FunzioneDiValutazione();
					funz.setName(nomeFunz);
					funz.setDescrizione(descrizione);
					funz.setApplicaTipoValutazione(tipoVal);
					funz.setTipoFunz(tipoFunz);
					funz.setApplicaTipoEdifici(tipoEdifici);
					funz.setCalcolo(espressione);
					//funz.setParametri(parameters);
					for (String key: parameters.keySet()){
						
						funz.addParametro(key, man.merge(parameters.get(key)));
						
					}
					funz.setOwnerid(new OwnerId(loginUser, ownLoginDomain));
					funz.setPermissionprop(new PermissionProp());
					
					man.persist(funz);
					
	  	       }else {
	  	    	 funz.setName(nomeFunz);
	  	    	    
					funz.setDescrizione(descrizione);
					funz.setApplicaTipoValutazione(tipoVal);
					funz.setTipoFunz(tipoFunz);
					funz.setApplicaTipoEdifici(tipoEdifici);
					funz.setCalcolo(espressione);
					funz.removeAllParameters();
					//funz.setParametri(parameters);
					for (String key: parameters.keySet()){
						funz.addParametro(key, parameters.get(key));
						
					}
					man.merge(funz);
	  	       }
				
			}catch (RuntimeException e){
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	  
		  return true;
	  }	
}
