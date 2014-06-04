package it.enea.lecop.eca.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.Securable;
import it.enea.lecop.eca.model.User;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@Stateless
@LocalBean
public class SecurityService {
	
	 @Inject
	   private UserDao uM;;

	   @Inject
	   private CompanyDomainDao cdd;
	   
	   @Inject
	   private EntityManager em;
	
	/**
	 * Consente di verificare le credenaizli di login e di esistenza dell'utente nel dominio
	 * @param username
	 * @param pwd
	 * @param domainName
	 * @param cipher
	 * @return
	 */
   public OwnerId login(String username,String pwd,String domainName,Integer cipher)
   {
	   User user;
	   CompanyDomain cd;
	   OwnerId oid=null;
	   // cipher null o 1 vuol dire che la password sul db è in chiaro quindi non deve essere 
	   // cifrata priam di essere verificata   
	if (cipher==null || cipher==1)
	   {
		   // l'utente deve esistere
		   user=uM.findByUserName(username);
		   if (user==null)return null;
		   if(! user.isEnabled()) return null;
		   // deve esistere e deve avere la pwd passata
		   if (!user.getPassword().equals(pwd)) return null;
		   
		   // il dominio deve esistere
		   cd = cdd.findById(domainName);
		   if (cd==null)return null;
		   
		   // l'utente verificato deve far parte del dominio 
		  Set<User> usersOnDomain= cd.getDomainUsers();
		  
		  // l'utente deve appartenenre a quelli appartenenti  al dominio
		  if (usersOnDomain.contains(user))
		  {
			  oid=new OwnerId(user, cd);
			  
		  }
		  
	   }
	   
	   return oid;
   }
   
   public List<Securable>  sucurityFilter(List<Securable> sec,User loginUser,CompanyDomain loginDomain)
   {
	   ArrayList obj= new ArrayList();
	   
	   
	   
	   return null;
   }
}
