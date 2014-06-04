package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
/**
 * questa classe definisce le superfici vetrate in m^2 esposte per ogni 
 * esposizione
 * @author fab
 *
 */
@Embeddable
public class EsposizioneSuperficiVetrate implements Serializable
{
public EsposizioneSuperficiVetrate() {
		
		// TODO Auto-generated constructor stub
	this.supVetrateNord = (double) 0;
	this.supVetrateNordEst = (double) 0;
	this.supVetrateEst = (double) 0;
	this.supVetrateSudEst = (double) 0;
	this.supVetrateSud = (double) 0;
	this.supVetrateSudOvest = (double) 0;
	this.supVetrateOvest = (double) 0;
	this.supVetrateNordOvest = (double) 0;
	
	}


public EsposizioneSuperficiVetrate(Double supVetrateNord,
			Double supVetrateNordEst, Double supVetrateEst,
			Double supVetrateSudEst, Double supVetrateSud,
			Double supVetrateSudOvest, Double supVetrateOvest,
			Double supVetrateNordOvest) {
		
		this.supVetrateNord = supVetrateNord;
		this.supVetrateNordEst = supVetrateNordEst;
		this.supVetrateEst = supVetrateEst;
		this.supVetrateSudEst = supVetrateSudEst;
		this.supVetrateSud = supVetrateSud;
		this.supVetrateSudOvest = supVetrateSudOvest;
		this.supVetrateOvest = supVetrateOvest;
		this.supVetrateNordOvest = supVetrateNordOvest;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EsposizioneSuperficiVetrate [supVetrateNord=" + supVetrateNord
				+ ", supVetrateNordEst=" + supVetrateNordEst
				+ ", supVetrateEst=" + supVetrateEst + ", supVetrateSudEst="
				+ supVetrateSudEst + ", supVetrateSud=" + supVetrateSud
				+ ", supVetrateSudOvest=" + supVetrateSudOvest
				+ ", supVetrateOvest=" + supVetrateOvest
				+ ", supVetrateNordOvest=" + supVetrateNordOvest + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((supVetrateEst == null) ? 0 : supVetrateEst.hashCode());
		result = prime * result
				+ ((supVetrateNord == null) ? 0 : supVetrateNord.hashCode());
		result = prime
				* result
				+ ((supVetrateNordEst == null) ? 0 : supVetrateNordEst
						.hashCode());
		result = prime
				* result
				+ ((supVetrateNordOvest == null) ? 0 : supVetrateNordOvest
						.hashCode());
		result = prime * result
				+ ((supVetrateOvest == null) ? 0 : supVetrateOvest.hashCode());
		result = prime * result
				+ ((supVetrateSud == null) ? 0 : supVetrateSud.hashCode());
		result = prime
				* result
				+ ((supVetrateSudEst == null) ? 0 : supVetrateSudEst.hashCode());
		result = prime
				* result
				+ ((supVetrateSudOvest == null) ? 0 : supVetrateSudOvest
						.hashCode());
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
		EsposizioneSuperficiVetrate other = (EsposizioneSuperficiVetrate) obj;
		if (supVetrateEst == null) {
			if (other.supVetrateEst != null)
				return false;
		} else if (!supVetrateEst.equals(other.supVetrateEst))
			return false;
		if (supVetrateNord == null) {
			if (other.supVetrateNord != null)
				return false;
		} else if (!supVetrateNord.equals(other.supVetrateNord))
			return false;
		if (supVetrateNordEst == null) {
			if (other.supVetrateNordEst != null)
				return false;
		} else if (!supVetrateNordEst.equals(other.supVetrateNordEst))
			return false;
		if (supVetrateNordOvest == null) {
			if (other.supVetrateNordOvest != null)
				return false;
		} else if (!supVetrateNordOvest.equals(other.supVetrateNordOvest))
			return false;
		if (supVetrateOvest == null) {
			if (other.supVetrateOvest != null)
				return false;
		} else if (!supVetrateOvest.equals(other.supVetrateOvest))
			return false;
		if (supVetrateSud == null) {
			if (other.supVetrateSud != null)
				return false;
		} else if (!supVetrateSud.equals(other.supVetrateSud))
			return false;
		if (supVetrateSudEst == null) {
			if (other.supVetrateSudEst != null)
				return false;
		} else if (!supVetrateSudEst.equals(other.supVetrateSudEst))
			return false;
		if (supVetrateSudOvest == null) {
			if (other.supVetrateSudOvest != null)
				return false;
		} else if (!supVetrateSudOvest.equals(other.supVetrateSudOvest))
			return false;
		return true;
	}

public Double getSupVetrateNord() {
		return supVetrateNord;
	}

	public void setSupVetrateNord(Double supVetrateNord) {
		this.supVetrateNord = supVetrateNord;
	}

	public Double getSupVetrateNordEst() {
		return supVetrateNordEst;
	}

	public void setSupVetrateNordEst(Double supVetrateNordEst) {
		this.supVetrateNordEst = supVetrateNordEst;
	}

	public Double getSupVetrateEst() {
		return supVetrateEst;
	}

	public void setSupVetrateEst(Double supVetrateEst) {
		this.supVetrateEst = supVetrateEst;
	}

	public Double getSupVetrateSudEst() {
		return supVetrateSudEst;
	}

	public void setSupVetrateSudEst(Double supVetrateSudEst) {
		this.supVetrateSudEst = supVetrateSudEst;
	}

	public Double getSupVetrateSud() {
		return supVetrateSud;
	}

	public void setSupVetrateSud(Double supVetrateSud) {
		this.supVetrateSud = supVetrateSud;
	}

	public Double getSupVetrateSudOvest() {
		return supVetrateSudOvest;
	}

	public void setSupVetrateSudOvest(Double supVetrateSudOvest) {
		this.supVetrateSudOvest = supVetrateSudOvest;
	}

	public Double getSupVetrateOvest() {
		return supVetrateOvest;
	}

	public void setSupVetrateOvest(Double supVetrateOvest) {
		this.supVetrateOvest = supVetrateOvest;
	}

	public Double getSupVetrateNordOvest() {
		return supVetrateNordOvest;
	}

	public void setSupVetrateNordOvest(Double supVetrateNordOvest) {
		this.supVetrateNordOvest = supVetrateNordOvest;
	}

private Double supVetrateNord;
	
	private Double supVetrateNordEst;
	
	private Double supVetrateEst;
	 private Double supVetrateSudEst;
	 
	 private Double supVetrateSud;
	 
	 private Double supVetrateSudOvest;

	 private Double supVetrateOvest;
	 
	 private Double supVetrateNordOvest;
}
