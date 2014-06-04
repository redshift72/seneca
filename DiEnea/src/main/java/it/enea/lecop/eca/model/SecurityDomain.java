package it.enea.lecop.eca.model;

import java.util.List;

import javax.persistence.ManyToMany;

public class SecurityDomain {

	
	

	
	@ManyToMany
	List<User>  aclForRead;
	
	public List<User> getAclForRead() {
		return aclForRead;
	}
	public void setAclForRead(List<User> aclForRead) {
		this.aclForRead = aclForRead;
	}
	public List<User> getAclForModify() {
		return aclForModify;
	}
	public void setAclForModify(List<User> aclForModify) {
		this.aclForModify = aclForModify;
	}
	public List<User> getAclForControl() {
		return aclForControl;
	}
	public void setAclForControl(List<User> aclForControl) {
		this.aclForControl = aclForControl;
	}

	@ManyToMany
	List<User>  aclForModify;
	
	
	/**
	 * la capability del controllo permette di cambiare i permessi
	 * quindi di aggiungere e rimuovere gli utenti dalle acl di lettura dati e modifica dati e controllo permessi
	 */
	@ManyToMany 
	List<User>  aclForControl;

	
	
	public Boolean addUserForRead(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForRead.add(user);
		}
		else return false;
	}
	
	public Boolean removeUserForRead(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForRead.remove(user);
		}
		else return false;
	}
	
	
	public Boolean addUserForModify(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForModify.add(user);
		}
		else return false;
	}
	
	public Boolean removeUserForModify(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForModify.remove(user);
		}
		else return false;
	}
	
	
	public Boolean addUserForControl(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForControl.add(user);
		}
		else return false;
	}
	
	
	public Boolean removeUserForControl(User user,User principal)
	{
		if(aclForControl.contains(principal))
		{
			return aclForControl.remove(user);
		}
		else return false;
	}
}
