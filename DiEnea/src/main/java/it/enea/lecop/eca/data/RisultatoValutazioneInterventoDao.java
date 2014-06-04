package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.RisultatoValutazioneIntervento;
import it.enea.lecop.eca.model.Valutazione;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
@LocalBean
public class RisultatoValutazioneInterventoDao {

	@Inject
	  EntityManager man;
	
	@Inject
    private Logger log;
	
	
	public Boolean save(Long ValutazioneID,Double risultato,Long funzID,Map<String,Double> UIParValue,Long calcoloEconID,Double risultatoeconomico)
	{
		boolean isupdate=false;
		FunzioneDiValutazione calcoloEcom=null;
		RisultatoValutazioneIntervento risultatoValOld;
		if(risultato==null)return false;
		try{
		Valutazione val=man.find(Valutazione.class,ValutazioneID );
		
		
		if(val==null)return false;
		
		FunzioneDiValutazione funz=man.find(FunzioneDiValutazione.class,funzID);
		if(funz==null)return false;
		
		
		Set<RisultatoValutazioneIntervento>  risultati= val.getRisultati();
		RisultatoValutazioneIntervento risVal=new RisultatoValutazioneIntervento(val, funz, risultato);
		//RisultatoValutazioneIntervento risVal=new RisultatoValutazioneIntervento();
		if(risultati==null)
		{   // non vi sono risultati quindi dovrebbe essere nuovo
			isupdate=false;
		}else
		{
			for(RisultatoValutazioneIntervento e: risultati)
			{
				if(e.equals(risVal))
				{
					try{
						risultatoValOld=man.find(RisultatoValutazioneIntervento.class,e.getId());
					}catch (Exception ex) {
						risultatoValOld=null;
					}
					
					if(risultatoValOld!=null){
						isupdate=true;
						risVal=risultatoValOld;
						break;
					}
				   
				}
			}
						
		}
		
		
		
		if(calcoloEconID!=null)
		{
			calcoloEcom=man.find(FunzioneDiValutazione.class,calcoloEconID);
			if (calcoloEcom==null || risultatoeconomico==null) return false;
		}
		
		
		  if(calcoloEcom!=null)
		{
			risVal.setCalcoloEconomico(calcoloEcom);
			risVal.setRisultatoEconomico(risultatoeconomico);
		}
		
		  if(UIParValue!=null)
		  {
			  risVal.setUIVal(UIParValue);
		  }
		  
		  
		  if(isupdate)
		  {
			  note("merge");
			
			  man.merge(risVal);
		  }else
		  {
			  
			  note("persisto");
			//  val.getRisultati().add(risVal);
			  man.persist(risVal);
		  }
		 
		  
	}catch (Exception e) 
	{
			throwing("save method", e);
			errore("errore nel salvataggio");
			return false;
	}
		
		
		
		return true;
	}
	
	
	 
		private void errore(String string) 
		{
			
			log.severe(string);
		}
		
		private void throwing(String sourceMethod,Throwable thrown) 
		{
			
			log.throwing(this.getClass().getCanonicalName(), sourceMethod, thrown);
		}
		
		private void note(String msg) 
		{
			
			log.log(Level.INFO, msg);
		}
}
