package it.enea.lecop.eca.model;

import java.io.Serializable;

import java.util.HashSet;

import java.util.Set;

import javax.persistence.*;



/**
 * Entity implementation class for Entity: CompanyDomain
 * Sono un ragruppamento di aziende. esiste per ogni azienda un raggruppamento 
 * formato solo dall'azienda stessa. Esiste poi sempre un raggruppamento chiamato all 
 * in cui ci sono tutte le aziende
 * Ad ogni dominio posso far parte più utenti, e un utente puo appartenere a più domini
 */
@Entity
@NamedQueries({
    @NamedQuery(name="CompanyDomain.findAll",
                query="SELECT u FROM CompanyDomain u"),
    @NamedQuery(name="CompanyDomain.findByName",
                query="SELECT u FROM CompanyDomain u WHERE u.name = :name"),
    @NamedQuery(name="CompanyDomain.findAllOWNERUSER",
         query="SELECT u FROM CompanyDomain u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"), 
    @NamedQuery(name="CompanyDomain.findAllOWNDOMAIN",
            query="SELECT u FROM CompanyDomain u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
    @NamedQuery(name="CompanyDomain.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM CompanyDomain u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
    @NamedQuery(name="CompanyDomain.findAllOWNERUSER_OWNDOMAIN",
                        query="SELECT u FROM CompanyDomain u WHERE ((u.ownerid.ownUser.username = :username) and (" +
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
public class CompanyDomain  implements Serializable, Securable{

	 
	
	/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aziende == null) ? 0 : aziende.hashCode());
		result = prime * result + ((domainUsers == null) ? 0 : domainUsers.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerid == null) ? 0 : ownerid.hashCode());
		result = prime * result
				+ ((permissionprop == null) ? 0 : permissionprop.hashCode());
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
		CompanyDomain other = (CompanyDomain) obj;
		if (aziende == null) {
			if (other.aziende != null)
				return false;
		} else if (!aziende.equals(other.aziende))
			return false;
		if (domainUsers == null) {
			if (other.domainUsers != null)
				return false;
		} 
		//else if (!domainUsers.equals(other.domainUsers))
	    //		return false;
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
		if (permissionprop == null) {
			if (other.permissionprop != null)
				return false;
		} else if (!permissionprop.equals(other.permissionprop))
			return false;
		return true;
	}
*/

	public CompanyDomain(String name, Set<Azienda> aziende,
			Set<User> domainUsers, OwnerId ownerid,
			PermissionProp permissionprop) {
		
		this.name = name;
		this.aziende = aziende;
		this.domainUsers = domainUsers;
		this.ownerid = ownerid;
		this.permissionprop = permissionprop;
	}
	
	
	public CompanyDomain(String name, Set<Azienda> aziende) {
		
		this.name = name;
		this.aziende = aziende;
	}

public CompanyDomain(String name) {
		
		this.name = name;
		this.aziende = new HashSet<Azienda>();
		domainUsers= new HashSet<User>();
	}
	
	
	
	private String name; 
	 
	 
	
	private Set<Azienda> aziende;
	
	
	private Set<User> domainUsers;
	
	
	@ManyToMany(fetch = FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH},mappedBy="domainsOfMembership")
	public Set<User> getDomainUsers() {
		return domainUsers;
	}
	public void setDomainUsers(Set<User> domainUsers) {
		this.domainUsers = domainUsers;
	}


	private static final long serialVersionUID = 1L;

	public CompanyDomain() {
		
	}   
	
	@ManyToMany(fetch = FetchType.EAGER,cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH},mappedBy="domainsOfAziendaMembership")
	public Set<Azienda> getAziende() {
		return this.aziende;
	}

	public void setAziende(Set<Azienda> aziende) {
		this.aziende = aziende;
	}
	
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
   
	
	
	public Boolean addAzienda(Azienda az)
	{
		if (aziende==null) 
		{aziende= new HashSet<Azienda>(); }
		
		return aziende.add(az);
	}
	public Boolean addUser(User user)
	{  
		if (domainUsers==null) 
		{domainUsers = new HashSet<User>();}
		
		return domainUsers.add(user);
	}
	
	public Boolean removeUser(User us)
	{
		if (domainUsers==null) 
		{domainUsers = new HashSet<User>();}
		
		
		return domainUsers.remove(us);
	}

	public Boolean removeAzienda(Azienda az)
	{
		if (aziende==null) 
		{aziende = new HashSet<Azienda>();}
		
		
		return   this.aziende.remove(az);
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
