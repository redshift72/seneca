package it.enea.lecop.eca.data;


import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.User;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;


@Stateless
@LocalBean
public class EdificioDao {
	@Inject
	  EntityManager man;
	
	public Edificio findById(Long id)
	{
    	try
	     {
    	  TypedQuery<Edificio> qr=man.createNamedQuery("Edificio.findById",Edificio.class);
    	  qr.setParameter("id", id) ; 
   	  
    	    return qr.getSingleResult();
	     }catch (Exception e) {
			 e.printStackTrace();
	    	 return null;
		} 
    }
	
	/*
	public List<Edificio> findAll_sec(String ownLoginUserName,String ownLoginDomainName,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(Edificio.class, man, ownLoginUserName, ownLoginDomainName, userSec, domainSec, otherSec);
	}
*/
	public List<Edificio> findAll_sec(String ownLoginUserName,CompanyDomain ownLoginDomain,SecAttrib[] userSec,SecAttrib[] domainSec, SecAttrib[] subDomSec,SecAttrib[] otherSec)
	{
		return SecureGenericSelect.getAll(Edificio.class, man, ownLoginUserName, ownLoginDomain, userSec, domainSec, subDomSec,otherSec);
	}

	public List<Edificio> findAll() {
		return SecureGenericSelect.getAllNoSec(Edificio.class, man);

	}

public void save(Edificio ed) {
		
		if (findById(ed.getId())== null){
  		  man.persist(ed);
		}else{
			man.merge(ed);
  	  	}
		
	}

public Boolean removeOwnerUser(User user)
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

}
