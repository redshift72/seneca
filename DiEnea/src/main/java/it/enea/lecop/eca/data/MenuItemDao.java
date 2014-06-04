package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;


@Stateless
@LocalBean
public class MenuItemDao {

	@Inject
	  EntityManager man;
	@Inject
	CompanyDomainDao cdd;
	@Inject
	UserDao         userdao;
	
	public List<MenuItem> findAllaccess(String userloginName,String domainloginName)
	{
		Query	query=man.createNamedQuery("MenuItem.findAllaccessEnabled");
		
	    query.setParameter("user",userloginName );
	    query.setParameter("domain",domainloginName );
	    
	    return (List<MenuItem>)query.getResultList();
	}
	
	public List<MenuItem> findAll()
	{
		Query	query=man.createNamedQuery("MenuItem.findAll");
		
	   
	    
	    return (List<MenuItem>)query.getResultList();
	}
	
	/*
	public List<MenuItem> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(MenuItem.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
	}
    */
	
	public boolean addUserOnSelectedItem(Set<String> selectedItems,String userNameToAdd ,String loginUsername,CompanyDomain loginDomain)
	{
		if(selectedItems== null || selectedItems.isEmpty()) return true;
		
		try{		
		  if (loginUsername.equals("admin") || loginDomain.getName().equals("all") )
		   {	
			  
			  SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			   SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};		   
			   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY}; 
		       List<MenuItem>  mall = findAll_sec(loginUsername, loginDomain, userSec, domainSec, subDomSec, otherSec);  
			  
			  
		       for(MenuItem m: mall)
		        {
		    	  // potrei prendere il look 
		    	  if (selectedItems.contains(m.getName()))
		    		  {
		    		  m.addUserForAccess(userNameToAdd);
		    		  }
		    	  else
		    	  {
		    	      m.getAccessUser().remove(userNameToAdd);
		    	  }
		    	  man.merge(m);
		    	  
		        }
			  
			  
			  
			
		    }
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	
	public boolean removeUserFromAllItems(String userNameToRemove,String loginUserName,CompanyDomain loginDomain)
	{
		
	   try{	
		 if (loginUserName.equals("admin") || loginDomain.getName().equals("all") )
		  {	  
			 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			   SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};		   
			   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY}; 
		       List<MenuItem>  mi = findAll_sec(loginUserName, loginDomain, userSec, domainSec, subDomSec, otherSec);  
		  
	      for(MenuItem m: mi)
	        {
	    	  // potrei prendere il look 
	    	  m.getAccessUser().remove(userNameToRemove);
	    	  man.merge(m);
	    	  
	        }
		     
		  }
	   }catch (Exception e) {
		e.printStackTrace();
		return false;
	}
		 return true;
	}
	
	
	public List<MenuItem> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] subDomaSec,SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(MenuItem.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomaSec,otherSec);
	}

	public List<String> getForbiddenLink(String username, String name) {
		
		
		Query	query=man.createNamedQuery("MenuItem.findAllForbiddenLink");
		
		query.setParameter("user", username);
		query.setParameter("domain", name);
		
		
		return (List<String>) query.getResultList();
	}
}
