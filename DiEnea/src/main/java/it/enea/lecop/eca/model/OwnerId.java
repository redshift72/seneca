package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * questa classe rappresenta l'utente proprietario dell'oggetto
 * e il dominio dell'utente proprietario
 * Puo essere embeddato in ogni oggetto
 * 
 * @author fab
 *
 */
@Embeddable
// @Table(uniqueConstraints = {@UniqueConstraint(columnNames={"ownuser_id", "owncompany_id"})})
public class OwnerId implements Serializable{

	
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE")
	public synchronized Date getCreationDate() {
		return creationDate;
	}

	public synchronized void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	
	  private Date creationDate;
	
	
	public OwnerId() {
		this.creationDate= new Date();
	}

	public OwnerId(User ownUser, CompanyDomain ownCompany) {
		
		this.ownUser = ownUser;
		this.ownCompany = ownCompany;
		this.creationDate= new Date();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ownCompany == null) ? 0 : ownCompany.hashCode());
		result = prime * result + ((ownUser == null) ? 0 : ownUser.hashCode());
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
		OwnerId other = (OwnerId) obj;
		if (ownCompany == null) {
			if (other.ownCompany != null)
				return false;
		} else if (!ownCompany.equals(other.ownCompany))
			return false;
		if (ownUser == null) {
			if (other.ownUser != null)
				return false;
		} else if (!ownUser.equals(other.ownUser))
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private User ownUser;
	
	
	private CompanyDomain ownCompany;

	@ManyToOne
	@JoinColumn(name="ownuser_id",nullable=true)
	public User getOwnUser() {
		return ownUser;
	}
	
	
	public void setOwnUser(User ownUser) {
		this.ownUser = ownUser;
	}

	@ManyToOne
	@JoinColumn(name="owncompany_id",nullable=true)
	public CompanyDomain getOwnCompany() {
		return ownCompany;
	}

	public void setOwnCompany(CompanyDomain ownCompany) {
		this.ownCompany = ownCompany;
	}
}
