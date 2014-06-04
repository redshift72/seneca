package it.enea.lecop.eca.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaEdifici;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Stateless
@LocalBean
public class ComposizioneEdificiDao {
	@Inject
	  EntityManager man;
	
	@Inject
	 AziendaDao azdao;
	
	@Inject
	ProfiloUsoConsumoDao pudao;
	
	@Inject
	ComuneDao  comdao;
	
	@Inject
	EdificioDao edidao;
	
	
		public ComposizioneEdifici findById(Long id) {
		ComposizioneEdifici ce;
		try{
		ce=man.find(ComposizioneEdifici.class,id);
		}catch (RuntimeException e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return ce;
	}
	
	/*
	public List<ComposizioneEdifici> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(ComposizioneEdifici.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
	}
	*/
	public List<ComposizioneEdifici> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
	{    try{
		return SecureGenericSelect.getAll(ComposizioneEdifici.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
	    }catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ComposizioneEdifici> findAll() {
		 try{
				return SecureGenericSelect.getAllNoSec(ComposizioneEdifici.class, man );
			    }catch (Exception e) {
					e.printStackTrace();
					return null;
				}

	}
	
	/**
	 * 
	 * @param allProfiliId 
	 * @param id
	 * @param name
	 * @param note
	 * @param edificiId
	 * @param profiliUsoId
	 * @param aziendaNameId
	 * @param comuneId
	 * @param login
	 * @return
	 */
	public boolean saveComposizioneEdifici(List<Long> allProfiliId, Long id,String name,String note,Set<Long> edificiId,Set<Long> profiliUsoId,  String aziendaNameId,TipologiaEdifici tipo, Long comuneId,Login login)
	{
		HashSet<Edificio> edificio= new HashSet<Edificio>();
		ComposizioneEdifici compEd;
		boolean isCompUpdate=false;
		
	 try{	
		
		if (id==null || id==0)
		{
			compEd= new ComposizioneEdifici();
			
		}else
		{
			compEd=this.findById(id);
			if (compEd==null)
			{
				System.out.println("la composizione da aggiornare  con id "+ id+" non esiste più");
				System.out.flush();
				return false;
			}
			// esiste gia quindi e' un update
			isCompUpdate=true;
		}
		
		if(isCompUpdate)
			{
			   compEd.getEdifici().clear();
			   compEd.getProfilo().clear();
			}
		
		for (Long ided: edificiId)
		{
		 //	System.out.println("ho selezionato l'edificio con id "+ ided);
		 //	System.out.flush();
			Edificio edf=edidao.findById(ided);
			if (edf==null)
			{
			//	System.out.println("l'edificio  con id "+ ided+" non esiste");
			//	System.out.flush();
				return false;
			}
			//edf.setComposizione(compEd);
			compEd.addEdificio(edf);
		}
		
		if(isCompUpdate)
		 {
			// cicla su tutti i profili inseriti
			for(Long allProfId: allProfiliId)
		    {
			  ProfiloUsoConsumo profilo=pudao.findById(allProfId);
			  if (profilo==null)
			  {
			   //	System.out.println("il profilo uso consumo  con id "+ allProfId+" non esiste");
			   //	System.out.flush();
				return false;
			  }
			  // non e' il profilo corrente appartenente a quelli a cui la composizione appartiene?
			 if(!profiliUsoId.contains(allProfId))
			 {   // non è uno di quelli selezionati
				// System.out.println("---il profilo uso consumo  con id "+ allProfId+" non e selezionato");
				//	System.out.flush();
				 
				// se tra quelli selezionati vi è uno che ha il riferimento a me lo devo togliere
				/*
			    if (profilo.getComposizioneEdificio().getId()== compEd.getId() )
				{
					System.out.println("---il profilo uso consumo  con id "+ allProfId+" fa riferimento alla composizione attuale"+compEd.getId());
					System.out.println("--- e va eliminato tale rif ---");
					System.out.flush();
					profilo.setComposizioneEdificio(null);
					man.merge(profilo);
				}
				*/
					compEd.removeProfilo(profilo);
					man.merge(profilo);
			 }else
			 {
				  //  System.out.println("---il profilo uso consumo  con id "+ allProfId+"  e' selezionato");
				  //  System.out.flush();
				// e' un elemento selezioanto
				profilo.setComposizioneEdificio(compEd);
				compEd.addProfilo(profilo);
				man.merge(profilo);
			 }
			
		    }
		 }else
		 {
			 
			 for(Long allProfId: allProfiliId)
			    {
				  ProfiloUsoConsumo profilo=pudao.findById(allProfId);
				  if (profilo==null)
				  {
				//	System.out.println("il profilo uso consumo  con id "+ allProfId+" non esiste");
				//	System.out.flush();
					return false;
				  }
				 if(!profiliUsoId.contains(allProfId))
				 {   // non è uno di quelli selezionati
					 compEd.removeProfilo(profilo);
						man.merge(profilo);
					
				 }else
				 {
					// e' un elemento selezioanto
					profilo.setComposizioneEdificio(compEd);
					compEd.addProfilo(profilo);
					man.merge(profilo);
					
				 }
				
			    }
			 
			 
			 
		 }
		// ciclo su i profili selezionati
		/**
		
		for (Long idprof: profiliUsoId)
		{
			System.out.println("ho selezionato il profilo con id "+ idprof);
			System.out.flush();
			ProfiloUsoConsumo profilo=pudao.findById(idprof);
			if (profilo==null)
			{
				System.out.println("il profilo uso consumo  con id "+ idprof+" non esiste");
				System.out.flush();
				return false;
			}
			
			
			// !!!!attenzione devo prima eliminare i profili selezionati da tutte le altre composizioni di edifici
			
			//profilo.setComposizioneEdificio(compEd);
			
			// recupera la composizione del profilo che dobbiamo aggiungere
			ComposizioneEdifici oldComp=profilo.getComposizioneEdificio();
			
			
			removeProfileFromAllComposizione(profilo.getId());
			
			profilo.setComposizioneEdificio(compEd);
			
			
			compEd.addProfilo(profilo);
			
		}
	 
	    **/
		Azienda az=azdao.findByName(aziendaNameId);
		if (az==null)
		{
			System.out.println("l'azienda   con nome "+ aziendaNameId+" non esiste");
			System.out.flush();
			return false;
		}
		compEd.setAzienda(az);

		
		Comune com=comdao.findById(comuneId);
		
		if(com==null)
		{
			System.out.println("il comune con id "+ comuneId+" non esiste");
			System.out.flush();
			return false;
		}
		
		compEd.setName(name);
		compEd.setComuneUbicazione(com);
		
		compEd.setNoteDellaComposizione(note);
		compEd.setTipo(tipo);
		
		if (isCompUpdate)
		{
			// System.out.println(">>>>>>>>>>> FACCIO MERGE FINALE!!!!!!!!! <<<<<<<<<<<< ");
			//	System.out.flush();
			man.merge(compEd);
			
			System.out.println(">>>>>>>>>>> FATTO MERGE FINALE!!!!!!!!! <<<<<<<<<<<< ");
			System.out.flush();
		}else
		{
			compEd.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain()));
			compEd.setPermissionprop(new PermissionProp(SecAttrib.CONTROL, SecAttrib.MODIFY, SecAttrib.MODIFY, SecAttrib.NONE, SecAttrib.NONE));
			
			man.persist(compEd);
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

	public boolean remove(ComposizioneEdifici user) {
		
	   
	    	ComposizioneEdifici mergedComp;
	    	try
		     {
	    	mergedComp=man.merge(user);
	    	if (mergedComp!=null) man.remove(mergedComp);
	    	
		     }catch (RuntimeException e) 
		     {
				e.printStackTrace();
		    	 return false;
			}  catch (Exception e) {
				e.printStackTrace();
				return false;
			}
	    
	        return true;
	   
		
	}
	
	public void removeProfileFromAllComposizione(Long profileId)
	{
		Query q=man.createNamedQuery("ComposizioneEdifici.findAll");
		List<ComposizioneEdifici>  comp = q.getResultList();
		ProfiloUsoConsumo prof=man.find(ProfiloUsoConsumo.class, profileId);
		for(ComposizioneEdifici ce:comp)
		{
			ce.removeProfilo(prof);
			man.merge(ce);
		}
	}

	public void save(ComposizioneEdifici compEd) {
		
		if (findById(compEd.getId())== null){
  		  man.persist(compEd);
		}else{
			man.merge(compEd);
  	  	}
		
	}

	
}
