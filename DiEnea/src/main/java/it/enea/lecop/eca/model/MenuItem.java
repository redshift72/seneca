package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name="MenuItem.findAll",
                query="SELECT u FROM MenuItem u"),
     @NamedQuery(name="MenuItem.findAllEnabled",
                query="SELECT u FROM MenuItem u WHERE u.position >= 0"),             
    @NamedQuery(name="MenuItem.findByName",
                query="SELECT u FROM MenuItem u WHERE u.name = :name"),
    @NamedQuery(name="MenuItem.findAllOWNERUSER",
         query="SELECT u FROM MenuItem u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"), 
    @NamedQuery(name="MenuItem.findAllOWNDOMAIN",
            query="SELECT u FROM MenuItem u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
    @NamedQuery(name="MenuItem.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM MenuItem u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
    @NamedQuery(name="MenuItem.findAllaccess",
              query="SELECT it FROM MenuItem it WHERE ( :user IN ELEMENTS( it.accessUser )) or ( :domain IN ELEMENTS(it.accessDomain)) ORDER BY it.position"),
   @NamedQuery(name="MenuItem.findAllForbiddenLink",
              query="SELECT it.link FROM MenuItem it WHERE NOT (( :user IN ELEMENTS( it.accessUser )) or ( :domain IN ELEMENTS(it.accessDomain))) ORDER BY it.position"),
    @NamedQuery(name="MenuItem.findAllaccessEnabled",
              query="SELECT it FROM MenuItem it WHERE (it.position >= 0) AND (( :user IN ELEMENTS( it.accessUser )) or ( :domain IN ELEMENTS(it.accessDomain))) ORDER BY it.position"),          
    @NamedQuery(name="MenuItem.findAllOWNERUSER_OWNDOMAIN",
                        query="SELECT u FROM MenuItem u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
                    		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                       		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                       		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                       		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                       		       " or (" +
                       		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                       		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                       		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                       		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                       		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))" +
                       		       "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4))")                		
}) 
public class MenuItem  implements Serializable, Securable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @return the name
	 */
	@Id
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the accessUser
	 */ 
	@ElementCollection(fetch=FetchType.EAGER,targetClass=String.class)
	public Set<String> getAccessUser() {
		return accessUser;
	}
	/**
	 * @param accessUser the accessUser to set
	 */
	public void setAccessUser(Set<String> accessUserName) {
		this.accessUser = accessUserName;
	}
	/**
	 * @return the accessDomain
	 */
	@ElementCollection
	public Set<String> getAccessDomain() {
		return accessDomain;
	}
	/**
	 * @param accessDomain the accessDomain to set
	 */
	public void setAccessDomain(Set<String> accessDomainName) {
		this.accessDomain = accessDomainName;
	}
	
	
	private String name;
	private String link;
	private String label;
	private Integer position;
	
	
	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}


	private Set<String> accessUser;
	private Set<String> accessDomain;
	
	
	public boolean addUserForAccess(String userName)
	{
		if (accessUser==null)
		{
			accessUser=new HashSet<String>();
		}
		
		
		return accessUser.add(userName);
	}
	
	public boolean addDomainForAccess(String domainName)
	{
		if (accessDomain==null)
		{
			accessDomain=new HashSet<String>();
		}
		
		
		return accessDomain.add(domainName);
	}
	
	
	public boolean removeDomainForAccess(CompanyDomain dom)
	{
		if (accessDomain==null)
		{
			accessDomain=new HashSet<String>();
		}
		
		
		return accessDomain.remove(dom);
	}
	
	public boolean removeUserForAccess(User user)
	{
		if (accessUser==null)
		{
			accessUser=new HashSet<String>();
		}
		
		
		return accessUser.remove(user);
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
