package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * sede legale dell'azienda o dell'ente o dell'istituzione
 * come indirizzo a cui mandare eventualmente le lettere
 */
@Embeddable
public class SedeLegale implements Serializable {

	@Override
	public String toString() {
		return "SedeLegale [city=" + city + ", via=" + via + ", nazione="
				+ nazione + ", cap=" + cap + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cap == null) ? 0 : cap.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((nazione == null) ? 0 : nazione.hashCode());
		result = prime * result + ((via == null) ? 0 : via.hashCode());
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
		SedeLegale other = (SedeLegale) obj;
		if (cap == null) {
			if (other.cap != null)
				return false;
		} else if (!cap.equals(other.cap))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (nazione == null) {
			if (other.nazione != null)
				return false;
		} else if (!nazione.equals(other.nazione))
			return false;
		if (via == null) {
			if (other.via != null)
				return false;
		} else if (!via.equals(other.via))
			return false;
		return true;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getNazione() {
		return nazione;
	}
	public void setNazione(String nazione) {
		this.nazione = nazione;
	}
	public Integer getCap() {
		return cap;
	}
	public void setCap(Integer cap) {
		this.cap = cap;
	}
	private String city;
	private String via;
	private String nazione;
	private Integer cap;
}
