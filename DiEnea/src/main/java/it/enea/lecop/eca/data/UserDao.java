package it.enea.lecop.eca.data;

import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * Session Bean implementation class UserDao
 */
@Stateless
@LocalBean
public class UserDao {

    /**
     * Default constructor. 
     */
	
	@Inject
	  EntityManager man;
	@Inject
	  CompanyDomainDao cdd;
	
    public UserDao() {
       
    }

    
    public List<User>  findAll()
    {
    	  TypedQuery<User> qr=man.createNamedQuery("User.findAll",User.class);
    	    
    	    return qr.getResultList();
    }
    
    /**
     * findAllRetriveEagerCompanyDomain retrieve all Users bean
     * populated with referenced Domain entities
     * @return
     */
    public List<User>  findAllRetriveEagerCompanyDomain()
    {
    	List<User> users;
    	Set<CompanyDomain> cd;
    
    	  TypedQuery<User> qr=man.createNamedQuery("User.findAll",User.class);
    	  users=qr.getResultList();
    	  for (User u: users)if((cd=u.getDomainsOfMembership())!=null)cd.size();
    	  
    	    return users;
    }
    
    public User  findByUserName(String username)
    {
    	try
	     {
    	  TypedQuery<User> qr=man.createNamedQuery("User.findByUserName",User.class);
    	  qr.setParameter("username", username) ; 
   	  
    	    return qr.getSingleResult();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
    }
    
    public List<User> findAll_sec(String ownLoginUserName,CompanyDomain loginDomain,SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] subDomainSec ,SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(User.class, man, ownLoginUserName,loginDomain , userSec, domainSec,subDomainSec ,otherSec);
	}

    
    public Boolean remove(User user)
    { 
    	User mergedUser;
    	try
	     {
    	mergedUser=man.merge(user);
    	if (mergedUser!=null) man.remove(mergedUser);
    	
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
    
    /**
     * Persiste, crea una nuova entità con l'id passato, dentro il contesto di persistenza, e poi sul DB a fine metodo (cioè fine transazione)
     * L'entità non deve esistere gia nel contesto di persistenza.
     * Il metodo persist non fa insert, ma è l'entity managar che quando esce dal suo scop, (TRANSACTIONAL di default quindi esce dal metodo dell'EJB) riporta lo stato del contesto di persistenza 
     * su DB .
     * @param user
     * @return
     */
    public Boolean save(User user)
    {
    	try
	     {
    		
    	  if (findByUserName(user.getUsername())== null)	
   	       {
    		  man.persist(user);
   	       }
    	  else {
    		     return (update(user)!=null);
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
    
    
    /**
     * Fa il merge, quindi mette nel contesto dell'entity manager l'entità. L'entità detached  passata, deve esistere nel contesto di persistenza in termini di id e tipo dell'id
     *  Al termine delle metodo dell'EJB(al termine del contesto di vita dell'entity managar che di default e' TRANSACTIONAL, quindi al termine della transazione) l'entity manager andra' a copiare lo stato delle entità dal contesto al DB
     * Merge non fa update ma rende solo attached l'entita'. 
     * Da errore se l'entità è stata eliminata dal contesto, quindi non esiste più con quell'id
     * @param user
     * @return restituiece l'entità attached aggiornata, null in caso di problemi
     */
    public User update(User user)
    {
    	User attachUser=null;
    	try
	     {
    		attachUser=man.merge(user);
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
    
    
    
    
    @PostConstruct
    public void init()
    {
    	/*
    	if(findByUserName("admin")==null)
    	{
    		User user= new User("admin", "admin", "adminpwd", "f.redshift72@gmail.com", "+393495157233");
    		save(user);
    	}
    	*/
    }

     /**
      * persiste o aggiorna lo stato dell'user con i nuoivi domini
      * @param newUser
      * @param domainNames
      * @return
      */
	public Boolean setDomainOnUser(User newUser, Set<String> domainNames) 
	{
		
		
		CompanyDomain  cd;
		User attachUser,prevUser;
		Set<CompanyDomain> cdSet= new HashSet<CompanyDomain>();
	
		
			
		/*
		if((attachUser= update(newUser))==null)
		  {
			  man.persist(newUser);
			  attachUser=newUser;
		  }
		*/
		/*
		if((prevUser=findByUserName(newUser.getUsername()))==null)
		{  // completamente nuovo
			
		}else
		{  // esiste gia quindi è un update
			if (((prevCdSet=prevUser.getDomainsOfMembership())==null) || prevCdSet.isEmpty()  )
			{
				// il mio utente non apparteneva a nessun dominio
				// quindi operando sui domini passati a cui ora devo appartenere
				// posso solo aggiungere a tali domini l'utente corrente
			}
		}
			
		*/
		newUser.getDomainsOfMembership().clear();
		
		if (findByUserName(newUser.getUsername())!=null)
		  {   // trattasi di un aggiornamento
			  newUser.getDomainsOfMembership().clear();
			  
			  attachUser=update(newUser);
			  //attachUser.addCompanyDomain(cd);
		  }else
		  {   // nuovo utente
			  man.persist(newUser);
			  attachUser=newUser;
		  }

		
		
		
		for(String named: domainNames)
		{
		  cd= cdd.findById(named);	 
		  
		  if (cd!= null)
		  {
			// log
			  System.out.println("provo a salvare il dominio: "+named);
			  System.out.flush();
			  attachUser.addCompanyDomain(cd);
		  } else
			  {  // il dominio non esiste 
			    continue;
			    
			  }
		}
		
		// log
		System.out.println("Faccio update: ");
		System.out.flush();
		  
		   
		  
		return  (update(attachUser)!=null);
		
		
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
			       List<User>  users = findAll_sec(loginUserName, loginDomain , userSec, domainSec, subDomSec, otherSec);  
			  
		 
				for(User usidc: users)
		        {
		    	  // potrei prendere il look 
		    	  usidc.removeCompanyDomain(domainToRemove);
		    	  man.merge(usidc);
		    	  
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
