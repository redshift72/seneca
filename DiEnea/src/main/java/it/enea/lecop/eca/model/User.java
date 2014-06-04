package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;



/**
 *   classe che rappresenta gli utenti dell'applicazione 
 *   ogni utente puo appartenere a più domini e ogni dominio puo avere piu utenti.
 *    quando si fa il login  se ne sceglie uno. 
 *    Con questo dominio si verificheranno le credenziali di dominio.
 *    
 *   Se nel login non se ne sceglie uno, allora si intennde il dominio all.
 *   L'utente deve appartenere al dominio di login,  e poi deve verificare la pwd relativa
 *   
 */

@Entity
@NamedQueries({
    @NamedQuery(name="User.findAll",
                query="SELECT u FROM User u"),
    @NamedQuery(name="User.findByUserName",
                query="SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name="User.findAllOWNERUSER",
         query="SELECT u FROM User u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"), 
    @NamedQuery(name="User.findAllOWNDOMAIN",
            query="SELECT u FROM User u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
    @NamedQuery(name="User.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM User u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
    @NamedQuery(name="User.findAllOWNERUSER_OWNDOMAIN",
                        query="SELECT u FROM User u WHERE ((u.ownerid.ownUser.username = :username) and (" +
                  		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                  		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                  		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                  		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                  		       " or (" +
                  		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                  		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                  		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                  		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                  		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))")             		
}) 

public class User implements Serializable, Securable{

	/*
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result + ((domainsOfMembership == null) ? 0 : domainsOfMembership.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerid == null) ? 0 : ownerid.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((permissionprop == null) ? 0 : permissionprop.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
*/


/*

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (domainsOfMembership == null) {
			if (other.domainsOfMembership != null)
				return false;
		} else if (!domainsOfMembership.equals(other.domainsOfMembership))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerid == null) {
			if (other.ownerid != null)
				return false;
		} else if (!ownerid.equals(other.ownerid))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (permissionprop == null) {
			if (other.permissionprop != null)
				return false;
		} else if (!permissionprop.equals(other.permissionprop))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

*/



public User(String username, String name, String password, String email,
			String phoneNumber, Set<CompanyDomain> domainsOfMembership,
			OwnerId ownerid, PermissionProp permissionprop) {
		
		this.username = username;
		this.name = name;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.domainsOfMembership = domainsOfMembership;
		this.ownerid = ownerid;
		this.permissionprop = permissionprop;
	}





public User() {
	this.domainsOfMembership=new HashSet<CompanyDomain>();
	}

public User(String username, String name, String password, String email,
			String phoneNumber) {
		
		this.username = username;
		this.name = name;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}


   private String username;
   private String name;
   private String password;
   private String email;
   private String phoneNumber;
   private boolean enabled=true;
  
   
   public boolean isEnabled() {
	return enabled;
}





public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}


/**
	  * domini di sicurezza di appartenenza
	  */
   
	private Set<CompanyDomain> domainsOfMembership=new HashSet<CompanyDomain>();
	 

@ManyToMany(fetch=FetchType.EAGER,cascade={CascadeType.ALL})
public Set<CompanyDomain> getDomainsOfMembership() {
	return domainsOfMembership;
}

public void setDomainsOfMembership(Set<CompanyDomain> domainsOfMembership) {
	this.domainsOfMembership = domainsOfMembership;
}

public boolean addCompanyDomain(CompanyDomain cd)
{
	if (domainsOfMembership==null)
	{
		setDomainsOfMembership(new HashSet<CompanyDomain>());
		
	}
	
	return domainsOfMembership.add(cd);
}
public boolean removeCompanyDomain(CompanyDomain cd)
{
	if (domainsOfMembership==null)
	{
		setDomainsOfMembership(new HashSet<CompanyDomain>());
		
	}
	
	return domainsOfMembership.remove(cd);
}
public String getPhoneNumber() {
	return phoneNumber;
}

public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

@Id
public String getUsername() {
      return username;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getName() {
      return name;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getPassword() {
      return password;
   }

   @Override
   public String toString() {
      return "User (username = " + username + ", name = " + name + ")";
   }

   
  
   
private OwnerId ownerid;
private PermissionProp permissionprop;
   
   @Embedded
   public OwnerId getOwnerid()
   {
	  
	return ownerid;
   }
   
   public void setOwnerid(OwnerId ownerid) {
		this.ownerid = ownerid;
	}
   
   @Embedded
   public PermissionProp getPermissionprop()
   {
	   return permissionprop;
   }
   
   public void setPermissionprop(PermissionProp permissionprop) {
		this.permissionprop = permissionprop;
	}
   
}
