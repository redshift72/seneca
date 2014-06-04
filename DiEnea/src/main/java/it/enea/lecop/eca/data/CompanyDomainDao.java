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
import it.enea.lecop.eca.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Session Bean implementation class UserDao
 */
@Stateless
@LocalBean
public class CompanyDomainDao {
	@Inject
	  EntityManager man;

		
	public CompanyDomain findById(String name) {
		CompanyDomain cd;
		try{
		cd=man.find(CompanyDomain.class,name);
		}catch (RuntimeException e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return cd;
		
	}
	
	
	/**
     * Persiste, crea una nuova entità con l'id passato, dentro il contesto di persistenza, e poi sul DB a fine metodo (cioè fine transazione)
     * L'entità non deve esistere gia nel contesto di persistenza.
     * Il metodo persist non fa insert, ma è l'entity managar che quando esce dal suo scope, (di tipo TRANSACTIONAL di default, quindi esce dal metodo dell'EJB) riporta lo stato del contesto di persistenza 
     * su DB .
     * @param Azienda
     * @return
     */
    public Boolean save(CompanyDomain cd)
    {
    	
    	
    	try
	     {
    		
    	  if (findById(cd.getName())== null)	
   	       {
    		  man.persist(cd);
   	       }
    	  else {
    		     return (update(cd)!=null);
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

	
	
public CompanyDomain update(CompanyDomain cod) {
		
		CompanyDomain cd=null;
    	try
	     {
    		cd=man.merge(cod);
	     }catch (RuntimeException e) 
	     {
	    	 e.printStackTrace();
			return null;
		 } catch (Exception e) {
			
			 e.printStackTrace();
			 return null;
		}
   
       return cd;
	}

public List<CompanyDomain> retrieveAllCompanyDomainOrderedByName() {
    
	   CriteriaBuilder  cb = man.getCriteriaBuilder();
   CriteriaQuery<CompanyDomain> criteria = cb.createQuery(CompanyDomain.class);
   Root<CompanyDomain> val = criteria.from(CompanyDomain.class);
  
   // Swap criteria statements if you would like to try out type-safe criteria queries, a new
   // feature in JPA 2.0
   // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
   
   //criteria.select(val).
   
   criteria.select(val).orderBy(cb.asc(val.get("name")));
   return man.createQuery(criteria).getResultList();
}

	
	 @PostConstruct
	    public void init()
	    {
		 
		 // all deve sempre esistere
		 
		 /*
	    	CompanyDomain cd=new CompanyDomain("all");
	    	
	    	if (findById(cd.getName())== null)	
	   	       {
	    		  man.persist(cd);
	   	       }
	    	*/
	    }
	 public List<CompanyDomain> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
		{
			return SecureGenericSelect.getAll(CompanyDomain.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
		}
	 
	 public List<CompanyDomain> findAll()
		{
			return SecureGenericSelect.getAllNoSec(CompanyDomain.class, man);
		}
/**
 * update Company Domain in the persistence context according data passed in parameter list
 * if Company Domain Does Not exist it will be created
 * 
 * @param id CompanyDomain ID
 * @param aziendeNames names Set of Aziende to be linked with the company domain
 * @param userNames Set of User to be linked with the company domain
 * @param login
 * @param aziende_domain Aziende set to be unlinked with company domain unless present in the to be linekd list
 * @param users_domain Aziende set to be unlinked with company domain unless present in the to be linekd list
 * @return
 */
	 public boolean saveCompanyDomain(String id,Set<String> aziendeNames,Set<String> userNames, Login login, List<String> aziende_domain, List<String> users_domain)
		{
			
			CompanyDomain compDom;
			boolean isCompUpdate=false;
			
			System.out.println("<<<<<<<<saveCompanyDomain>>>>>>>>>");
			System.out.flush();
			
		try{	
			compDom=this.findById(id);
			if ( compDom==null )
			{
				System.out.println("new case");
				System.out.flush();
				compDom= new CompanyDomain(id);
				compDom.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain()));
				// TODO How we can set and change the security policies?
				compDom.setPermissionprop(new PermissionProp(SecAttrib.CONTROL, SecAttrib.MODIFY, SecAttrib.READ, SecAttrib.NONE, SecAttrib.NONE));
				man.persist(compDom);
				
			}else
			{
				System.out.println("update case");
				System.out.flush();
					
				// esiste gia quindi e' un update
				isCompUpdate=true;
			}
			
			
			
			// Update
			if(isCompUpdate){
				
				for (String azid: aziende_domain)
				{
					
				  Azienda az1=	man.find(Azienda.class, azid);
					
					if(aziendeNames.contains(azid)){
						az1.addInCompanyDomain(compDom);
						System.out.println("Dominio "+compDom.getName()+" reinserito in azienda"+ azid);
						System.out.flush();
						
					}else
					{
						az1.removeCompanyDomain(compDom);
						System.out.println("Dominio "+compDom.getName()+" rimosso da azienda "+azid );
						System.out.flush();
					}
				//	man.merge(compDom);
					man.merge(az1);
			
				}
			
				for (String usid: users_domain)
				{
					
					User us1=	man.find(User.class, usid);
					
					if(userNames.contains(usid)){
						us1.addCompanyDomain(compDom);
						
						System.out.println("Dominio "+compDom.getName()+" reinserito in utente"+usid);
						System.out.flush();
					}else
					{
						us1.removeCompanyDomain(compDom);
						System.out.println("Dominio "+ compDom.getName()+" rimosso da utente"+usid);
						System.out.flush();
					}
					
					//man.merge(compDom);
					man.merge(us1);
			
				}
				
				
			}		
			// new Domain
			else{
								
				// let's add aziende contained in 
				for (String ided: aziendeNames)
				{
				//	System.out.println("ho selezionato l'azienda con id "+ ided);
				//	System.out.flush();
					Azienda az=man.find(Azienda.class, ided);
					if (az==null)
					{
					//	System.out.println("l'azienda  con id "+ ided+" non esiste");
					//	System.out.flush();
						return false;
					}
					
					// shall be added ONLY on the owner side
					az.addInCompanyDomain(compDom);
					man.merge(az);
			
			
				}
				
				
				for (String userId: userNames)
				{
				// debug	
				//	System.out.println("ho selezionato l'utente con id "+ userId);
				//	System.out.flush();
					User user=man.find(User.class,userId);
					if (user==null)
					{
					  // debug	
					  //	System.out.println("l'utente  con id "+ userId+" non esiste");
					  //	System.out.flush();
						return false;
					}
					
					user.addCompanyDomain(compDom);
					man.merge(user);
					
					
				}
						
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


public boolean remove(String domainName) {
	CompanyDomain domain=this.findById(domainName);
	CompanyDomain mergedDomain;
	
	try
     {
		mergedDomain=man.merge(domain);
		if (mergedDomain!=null) man.remove(mergedDomain);
	
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

}
