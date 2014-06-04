package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.*;
@Entity

@DiscriminatorValue("E")
public class CalcoloEconomico extends FunzioneDiValutazione implements Serializable, Securable {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calcolo == null) ? 0 : calcolo.hashCode());
		result = prime * result
				+ ((descrizione == null) ? 0 : descrizione.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((parametri == null) ? 0 : parametri.hashCode());
		result = prime * result + ((applicaTipoValutazione == null) ? 0 : applicaTipoValutazione.hashCode());
		result = prime * result
				+ ((valutazioniIniziali == null) ? 0 : valutazioniIniziali.hashCode());
		result = prime * result
				+ ((dependsOn == null) ? 0 : dependsOn.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CalcoloEconomico other = (CalcoloEconomico) obj;
		if (calcolo == null) {
			if (other.calcolo != null)
				return false;
		} else if (!calcolo.equals(other.calcolo))
			return false;
		if (descrizione == null) {
			if (other.descrizione != null)
				return false;
		} else if (!descrizione.equals(other.descrizione))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (parametri == null) {
			if (other.parametri != null)
				return false;
		} else if (!parametri.equals(other.parametri))
			return false;
		if (applicaTipoValutazione == null) {
			if (other.applicaTipoValutazione != null)
				return false;
		} else if (!applicaTipoValutazione.equals(other.applicaTipoValutazione))
			return false;
		if (valutazioniIniziali == null) {
			if (other.valutazioniIniziali != null)
				return false;
		} else if (!valutazioniIniziali.equals(other.valutazioniIniziali))
			return false;
		if (dependsOn == null) {
			if (other.dependsOn != null)
				return false;
		} else if (!dependsOn.equals(other.dependsOn))
			return false;
		return true;
	}

	@OneToOne
	private RisultatoValutazioneIntervento dependsOn;

	/**
	 * @return the dependsOn
	 */
	public RisultatoValutazioneIntervento getDependsOn() {
		return dependsOn;
	}

	/**
	 * @param dependsOn the dependsOn to set
	 */
	public void setDependsOn(RisultatoValutazioneIntervento dependsOn) {
		this.dependsOn = dependsOn;
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
