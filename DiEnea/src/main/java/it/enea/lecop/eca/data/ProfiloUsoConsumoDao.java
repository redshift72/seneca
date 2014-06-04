package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.ConsumoEnergetico;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.model.Valutazione;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.vaadin.ui.Notification;

@Stateless
@LocalBean
public class ProfiloUsoConsumoDao {

	@Inject
	EntityManager man;


	
	@Inject
	 ValutazioneDao  valDao;
	public static final String secQuery="SELECT u FROM ProfiloUsoConsumo u WHERE (u.tipo = :tipo) AND ((:username = 'admin') or ((u.ownerid.ownUser.username = :username) and (" +
		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
		       " or (" +
		       "(u.ownerid.ownCompany.name = :domainname) and (" +
		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))"       
		    +   "or (( SIZE(u.ownerid.ownCompany.aziende) = (SELECT COUNT(DISTINCT azi.nome) FROM CompanyDomain cd, IN(cd.aziende) azi, ProfiloUsoConsumo az, IN (az.ownerid.ownCompany.aziende ) ownazi WHERE (cd.name = :domainname) and (az = u) and (ownazi.nome = azi.nome))) " +
		             "and (u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1 , :ownsubsetSec2 , :ownsubsetSec3 , :ownsubsetSec4 )))" +
		        "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4))) ";
	
	public ProfiloUsoConsumo findById(Long id)
	{
		ProfiloUsoConsumo prof;
		try
	     {
    	  TypedQuery<ProfiloUsoConsumo> qr=man.createNamedQuery("ProfiloUsoConsumo.findById",ProfiloUsoConsumo.class);
    	  qr.setParameter("id", id) ; 
    	  prof=qr.getSingleResult();
    	  prof.getConsumi().size();
    	    return prof;
	     }catch (Exception e) {
			 //e.printStackTrace();
	    	 // log
	    	 System.out.println(e.getMessage());
	    	 return null;
		} 
    }
	/*
	public List<ProfiloUsoConsumo> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(ProfiloUsoConsumo.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
	}
	*/
	
	public List<ProfiloUsoConsumo> findAll_sec(TipologiaValutazione tipo,String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
	{
		List<ProfiloUsoConsumo> result;
		
		Query	query=man.createQuery(secQuery);
	    query.setParameter("username",ownLoginUserName );
	    query.setParameter("domainname",ownLoginDomain.getName() );
	    query.setParameter("tipo",tipo);
	 // if(!isZero) query.setParameter("aziendeLogin",loginDomain.getAziende() );
	    
	   
	    for(int i=0;i<4;i++)
	    {
	    	query.setParameter("ownuserSec"+(i+1),userSec[i] );
	    	query.setParameter("owndomainSec"+(i+1),domainSec[i] );
	    	
	    	
	    	query.setParameter("ownsubsetSec"+(i+1),subDomSec[i] );
	    	query.setParameter("otherSec"+(i+1),otherSec[i] );
	    }
	               
	    return result =(List<ProfiloUsoConsumo>)query.getResultList();
		
		
	}
	public List<ProfiloUsoConsumo> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(ProfiloUsoConsumo.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
	}
	
	public List<ProfiloUsoConsumo> findAll()
	{
		return SecureGenericSelect.getAllNoSec(ProfiloUsoConsumo.class, man);
	}
	
	public Boolean save(ProfiloUsoConsumo cd)
	{
	    	
	    	
	    	try
		     {
	    		
	    	  if (findById(cd.getId())== null)	
	   	       {
	    		  man.persist(cd);
	   	       }
	    	  else {
	    		  man.merge(cd);
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
	
	public Boolean remove(Long profId)
	{
		ProfiloUsoConsumo profile=this.findById(profId);
		ProfiloUsoConsumo mergedProfile;
	    	
	    	try
		     {
	    		//removeProfileFromAllComposizione(profId);
	    		List<Valutazione> result=valDao.findVal_byProfilo(profId);
    	 		if (result != null && !result.isEmpty())
    	 		{
    	 			// non posso eliminare un profiloUsoConsumo Usato da una valutazione
    	 			return false;
    	 			
    	 		}
	    		
	    		
	    		ComposizioneEdifici ce=profile.getComposizioneEdificio();
	    		ce.removeProfilo(profile);
	    		man.merge(ce);
	    	  	  /*mergedProfile=man.merge(profile);
	    		  if (mergedProfile!=null) man.remove(mergedProfile);
	   	      */
	    		
	    		man.remove(profile);
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

	public boolean save(Long idProfilo, String Name, TipologiaValutazione tipo, String descrizione, Long composizioneId, Double ore, List<ConsumoEnergetico> consumi,int numPersone, CompanyDomain ownLoginDomain, User loginUser ) {
		
		ProfiloUsoConsumo profilo;
		ComposizioneEdifici composizione;
		
		//TODO check for parameter congruency?
		
		profilo=findById(idProfilo);
		composizione= man.find(ComposizioneEdifici.class, composizioneId);
		
		if(composizione==null)
			return false;
		Azienda azienda=composizione.getAzienda();
						
		try{
			
			
			if (profilo== null)
			{
				profilo=new ProfiloUsoConsumo();
				profilo.setNome(Name);
				profilo.setTipo(tipo);
				profilo.setDescrizione(descrizione);
				profilo.setComposizioneEdificio(composizione);
				profilo.setAzienda(azienda);
				profilo.setOwnerid(new OwnerId(loginUser, ownLoginDomain));
				profilo.setPermissionprop(new PermissionProp());
				profilo.setNumPersone(numPersone);
				
				if(tipo==TipologiaValutazione.ELETTRICA){
					profilo.setOreSuGiorniElettrico(ore);
				}else if(tipo==TipologiaValutazione.IDRICA){
					profilo.setOreSuGiorniAcqua(ore);
				}if(tipo==TipologiaValutazione.TERMICA){
					profilo.setOreSuGiorniTermico(ore);
							
				} 
				
				 ConsumoEnergetico cons;
				 Iterator<ConsumoEnergetico> it= consumi.iterator();
				 while (it.hasNext())
				 {
					 cons=it.next();
					 profilo.addConsumo(cons);
					 
				 }
				/*
				for(ConsumoEnergetico idx: consumi){
					profilo.addConsumo(idx);
					
				}
				*/
				man.persist(profilo);
				
  	       }else {
  	    	   	profilo.setNome(Name);
				profilo.setTipo(tipo);
				
				if(tipo==TipologiaValutazione.ELETTRICA){
					profilo.setOreSuGiorniElettrico(ore);
				}else if(tipo==TipologiaValutazione.IDRICA){
					profilo.setOreSuGiorniAcqua(ore);
				}if(tipo==TipologiaValutazione.TERMICA){
					profilo.setOreSuGiorniTermico(ore);
							
				} 
				profilo.setComposizioneEdificio(composizione);
				profilo.setDescrizione(descrizione);
				profilo.setAzienda(azienda);
				profilo.setNumPersone(numPersone);
  	    	   
				List<ConsumoEnergetico> consToRem;
				consToRem=profilo.getConsumi();
			List<ConsumoEnergetico> synchConsumi=	Collections.synchronizedList(consToRem);
				synchronized (synchConsumi) {
					/*
					 ConsumoEnergetico cons;
					 Iterator<ConsumoEnergetico> it= synchConsumi.iterator();
					 while (it.hasNext())
					 {
						 cons=it.next();
						 profilo.removeConsumo(cons);
						 //man.remove(cons);
					 }
					 */
					 for(ConsumoEnergetico c:synchConsumi)man.remove(c);
					profilo.removeAllConsumo(null);
					 //synchConsumi.clear();
				}
				
				
				 ConsumoEnergetico cons;
				 Iterator<ConsumoEnergetico> it= consumi.iterator();
				 while (it.hasNext())
				 {
					 cons=it.next();
					 profilo.addConsumo(cons);
					 
				 }
				/*
				for(ConsumoEnergetico idx: consumi){
					profilo.addConsumo(idx);
					
				}
				*/
				man.merge(profilo);
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

}
