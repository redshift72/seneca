package it.enea.lecop.eca.data;

import java.util.List;

import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.SecAttrib;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@Stateless
@LocalBean
public class ComuneDao {

	 /**
     * Default constructor. 
     */
	
	@Inject
	  EntityManager man;
	@Inject
	  CompanyDomainDao cdd;
	
    public ComuneDao() {
       
    }
	
    public Comune findById(Long id)
	{
    	try
	     {
    	  TypedQuery<Comune> qr=man.createNamedQuery("Comune.findById",Comune.class);
    	  qr.setParameter("id", id) ; 
   	  
    	    return qr.getSingleResult();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
    }
    
   /* 
    public List<Comune> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(Comune.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
	}
    */
    
    public List<Comune> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
   	{
   		return SecureGenericSelect.getAll(Comune.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
   	}
    @PostConstruct
    public void init()
    {
    	/*
    String	query="SELECT u FROM Comune u WHERE (u.ownerid.ownUser.username = :username) and (" +
    		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
    		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
    		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
    		       "(u.permissionprop.OWNERUSER = :ownuserSec4))";
    	
        TypedQuery<Comune> result = man.createNamedQuery(query, Comune.class);
    	result.setParameter("username", "admin");
    	result.setParameter("ownuserSec1", SecAttrib.NONE);
    	result.setParameter("ownuserSec2", SecAttrib.NONE);
    	result.setParameter("ownuserSec3", SecAttrib.NONE);
    	result.setParameter("ownuserSec4", SecAttrib.NONE);
    	
    	result.getResultList();
    	*/
    }

}
