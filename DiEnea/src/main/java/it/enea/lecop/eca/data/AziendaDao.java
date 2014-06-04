package it.enea.lecop.eca.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Session Bean implementation class AziendaDao
 */
@Stateless
@LocalBean
public class AziendaDao {

	
	@Inject
	  EntityManager man;
	
	@EJB
	CompanyDomainDao cdd;
	
    /**
     * Default constructor. 
     */
    public AziendaDao() {
        
    }

    
    @PostConstruct
    public void init()
    {
    	
    }

    /**
     * Persiste, crea una nuova entità con l'id passato, dentro il contesto di persistenza, e poi sul DB a fine metodo (cioè fine transazione)
     * L'entità non deve esistere gia nel contesto di persistenza.
     * Il metodo persist non fa insert, ma è l'entity managar che quando esce dal suo scope, (di tipo TRANSACTIONAL di default, quindi esce dal metodo dell'EJB) riporta lo stato del contesto di persistenza 
     * su DB .
     * @param Azienda
     * @return
     */
    public Boolean save(Azienda azienda)
    {
    	
    	
    	try
	     {
    		
    	  if (findByName(azienda.getNome())== null)	
   	       {
    		  
    		  
    		  man.persist(azienda);
    		  
    		  // verifico se esiste un CompanyDomain con lo stesso nome del nnome delle stessa azienda nuova azienda 
    		  CompanyDomain cd= cdd.findById(azienda.getNome());
    		  if(cd==null)
    		  {
    			  
    			  cd= new CompanyDomain(azienda.getNome());
    			  cd.addAzienda(azienda);
    			  
    			  cdd.save(cd);
    		  }
    		  
    		  // verifico se esiste il companydomain all
    		  CompanyDomain cdall= cdd.findById("all");
    		  if(cdall==null)
    		  {
    			  
    			  cdall= new CompanyDomain("all");
    			  cd.addAzienda(azienda);
    			  
    			  cdd.save(cdall);
    		  }else
    		  {
    			  // esiste ma verifico se ha gia l'azienda in se
    			  // se non l'ha l'aggiungo
    			  if (!cdall.getAziende().contains(azienda))
    			  {
    				  cdall.addAzienda(azienda);
    				  cdd.save(cdall);
    			  }
    			  
    		  }
    			  
   	       }
    	  else {
    		     return (update(azienda)!=null);
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


	private Azienda update(Azienda azienda) {
		
		Azienda attachUser=null;
    	try
	     {
    		attachUser=man.merge(azienda);
	     }catch (RuntimeException e) 
	     {
	    	 e.printStackTrace();
			return null;
		 } catch (Exception e) {
			
			 e.printStackTrace();
			 return null;
		}
   
       return attachUser;
	}


	public Azienda findByName(String nome) {
		Azienda az;
		try{
		az=man.find(Azienda.class,nome);
		}catch (RuntimeException e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return az;
		
	}


	public List<Azienda> findAll_sec(String ownLoginUserName,CompanyDomain loginDomain,SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] subDomainSec ,SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(Azienda.class, man, ownLoginUserName,loginDomain , userSec, domainSec,subDomainSec ,otherSec);
	}
	
	public List<Azienda> findAll()
	{
		return SecureGenericSelect.getAllNoSec(Azienda.class, man);
	}
	/**
     * findAllRetriveEagerCompanyDomain retrieve all Users bean
     * populated with referenced Domain entities
     * @return
     */
    public List<Azienda>  findAllRetriveEagerCompanyDomain()
    {
    	List<Azienda> companies;
    	Set<CompanyDomain> cd;
    
    	  TypedQuery<Azienda> qr=man.createNamedQuery("Azienda.findAll",Azienda.class);
    	  companies=qr.getResultList();
    	  for (Azienda a: companies)if((cd=a.getDomainsOfAziendaMembership())!=null)cd.size();
    	  
    	    return companies;
    }
	
	public boolean remove(String id)
	{
		Azienda az=findByName(id);
		
		if(az==null) return false;
		
		Set<CompanyDomain> names = az.getDomainsOfAziendaMembership();
				Iterator<CompanyDomain> i = names.iterator();
				while (i.hasNext()) {
				   CompanyDomain s = i.next(); // must be called before you can call i.remove()
				   // Do something
				   i.remove();
				}
		
		
		
		
		man.remove(az);
		man.flush();
		if(findByName(id)==null) return true;
		else return false;
		
	}


public boolean removeDomainFromAll(String domainNameToRemove, String loginUserName, String loginDomainName) {
		
		CompanyDomain loginDomain=cdd.findById(loginDomainName);
		CompanyDomain domainToRemove=cdd.findById(domainNameToRemove);
		// TODO cancellazzione possibile solo da amministratore? ??
		
		try{	
			 if (loginUserName.equals("admin") || loginDomainName.equals("all") )
			  {	  
				 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
				   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
				   SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};		   
				   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY}; 
			       List<Azienda>  aziende = findAll_sec(loginUserName, loginDomain , userSec, domainSec, subDomSec, otherSec);  
			  
		 
				for(Azienda azidx: aziende)
		        {
		    	  // potrei prendere il look 
		    	  azidx.removeCompanyDomain(domainToRemove);
		    	  man.merge(azidx);
		    	  
		        }
			
			 }else{
				 return false;
				 
			 }
		
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
			 
		
		return true;
		
		
	}
	
}
