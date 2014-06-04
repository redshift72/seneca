package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * persona all'interno dell'azienda a cui fare riferimento 
 * inviare i responsi o fare domande
 * @author fab
 *
 */
@Embeddable
public class Referente implements Serializable{
  
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurename() {
		return surename;
	}
	public void setSurename(String surename) {
		this.surename = surename;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getTelCell() {
		return telCell;
	}
	public void setTelCell(String telCell) {
		this.telCell = telCell;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((surename == null) ? 0 : surename.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Referente other = (Referente) obj;
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
		if (surename == null) {
			if (other.surename != null)
				return false;
		} else if (!surename.equals(other.surename))
			return false;
		return true;
	}
	private String name;
	private String surename;
	private String email;
	private String tel;
	private String telCell;
	private String fax;
	
	
}
