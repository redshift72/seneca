package it.enea.lecop.eca.data;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import it.enea.lecop.eca.core.CalcolaIntervento;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.RisultatoValutazioneIntervento;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.util.CheckParRecursion;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.antlr.runtime.RecognitionException;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

@Stateless
@LocalBean
public class ValutazioneDao {

	
	@Inject
	  EntityManager man;
	
	@Inject
	  AziendaDao azdd;
	@Inject
	CalcolaIntervento calcola;
	
	
	
	 public Valutazione  findById(Long id)
	    {
	    	try
		     {
	    	  TypedQuery<Valutazione> qr=man.createNamedQuery("Valutazione.findById",Valutazione.class);
	    	  qr.setParameter("id", id) ; 
	   	  
	    	    return qr.getSingleResult();
		     }catch (Exception e) {
				 e.printStackTrace();
		    	 return null;
			} 
	    }
	 
	 /**
	  * recupera la tipologia edifici dalla valutazione ID
	  * quindi la valutazione deve essere gia persistente
	  * Il valore viene recuperato attraverso il profilo uso consumo
	  * @param id
	  * @return
	  */
	 public TipologiaEdifici  getTipoEdificiById(Long id)
	    {
		 Valutazione val;
	    	try
		     {
	    	  TypedQuery<Valutazione> qr=man.createNamedQuery("Valutazione.findById",Valutazione.class);
	    	  qr.setParameter("id", id) ; 
	   	        
	    	    val= qr.getSingleResult();
	    	    if(val==null) return null;
	    	    Iterator<Edificio> it=val.getProfiloUsoConsumo().getComposizioneEdificio().getEdifici().iterator();
	    	    if(it==null) return null;
	    	    Edificio ed=it.next();
	    	    if(ed==null) return null;
	    	    
	    	     return ed.getTipologiaEdifici();
		     }catch (Exception e) {
				 e.printStackTrace();
		    	 return null;
			} 
	    }
	 
	 
	 
	 public Valutazione  save(CompanyDomain ownLoginDomain,User loginUser,Long valutazioneID, Long profiloUsoConsumoId, Long funzioneDiValID, String aziendaName,String descrizione,TipologiaValutazione tipo,Double risultato,Double merito)
	 {
		 Valutazione val=null;
		 boolean isNew;
		 
		Azienda az=   man.find(Azienda.class, aziendaName);
		if (az==null) return null;
		ProfiloUsoConsumo uso= man.find(ProfiloUsoConsumo.class, profiloUsoConsumoId);
		if(uso==null) return null;
		FunzioneDiValutazione funz= man.find(FunzioneDiValutazione.class, funzioneDiValID);
		if(funz==null) return null;
		if(valutazioneID!=null) val=findById(valutazioneID);
		LinkedHashMap<FunzioneDiValutazione,RisultatoValutazioneIntervento> risultati = new LinkedHashMap<FunzioneDiValutazione,RisultatoValutazioneIntervento>();
		//RisultatoValutazioneIntervento risultato= new RisultatoValutazioneIntervento(val, funz, risultato)
		
		
		//result= calcola.calcola(id,funzValId , new CheckParRecursion(3),null);
		
		
		
		if (val==null)
		{
			val= new Valutazione();
			val.setAzienda(az);
			val.setTipo(tipo);
			val.setProfiloUsoConsumo(uso);
			val.setDescrizione(descrizione);
			val.setOwnerid(new OwnerId(loginUser, ownLoginDomain));
			val.setPermissionprop(new PermissionProp());
			val.setValutazioneIniziale(funz);
			if(risultato!=null)val.setRisultatoValutazioneIniziale(risultato);
			if(merito!=null)val.setPrestazioneIniziale(merito);
			
			//val.setRisultati(risultati);
			val.setCreazione(new GregorianCalendar());
			
			
			
			man.persist(val);
			
		}else
		{
			
			val.setAzienda(az);
			val.setTipo(tipo);
			val.setProfiloUsoConsumo(uso);
			val.setDescrizione(descrizione);
			val.setValutazioneIniziale(funz);
			if(risultato!=null)val.setRisultatoValutazioneIniziale(risultato);
			if(merito!=null)val.setPrestazioneIniziale(merito);
			val=man.merge(val);
			
		}
			
		
		 
		 
		 return val;
	 }
	 
	 
	 
	 public Boolean remove(Long valId)
	 { 
		 Valutazione val=null;
		 try{
		   if(valId!=null) val=findById(valId);
		   else return false;
		 
		   if (val==null) return false;
		 
		   Set<RisultatoValutazioneIntervento> risultiValutazione=val.getRisultati();
		 
		   if(risultiValutazione!=null  && risultiValutazione.size()>0)
		   {	 
		    for(RisultatoValutazioneIntervento ris:risultiValutazione)
		    {
			 man.remove(ris);
		     }
		 
		   } 
		 
		 man.remove(val);
		 }catch (Exception e) {
			return false;
		}
		 
		 return true;
	 }
	 
	 public int retInterventi(Long idVal)
	 {
		 
		 return 1;
	 }
	 
	 /*
	 public List<Valutazione> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
		{
			return SecureGenericSelect.getAll(Valutazione.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
		}
	 */
	 /**
	  * recupera tutte la valutazioni associate con un profilo uso consumo
	  * @param profilo
	  * @return
	  */
	 public List<Valutazione> findVal_byProfilo(Long profiloId)
	 {
		 List<Valutazione> result=null;
		 try{
			 TypedQuery<Valutazione> qr=man.createNamedQuery("Valutazione.findByProfiloUsoConsumoId",Valutazione.class);
		     qr.setParameter("id", profiloId);
		    result= qr.getResultList();
		    
		 } catch (Exception e) {
			return null;
		}
		 
		 return result;
	 }
	 
	 public List<Valutazione> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] subDomSec ,SecAttrib[] otherSec)
		{
		   try{
			return SecureGenericSelect.getAll(Valutazione.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec,  subDomSec,otherSec);
		    } catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		   }

	public List<Valutazione> findAll() {
		return SecureGenericSelect.getAllNoSec(Valutazione.class, man);

	}

	public void save(Valutazione ed) {
		
		if (findById(ed.getId())== null){
  		  man.persist(ed);
		}else{
			man.merge(ed);
  	  	}
		
	}
}
