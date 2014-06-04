package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: FattoreNormFormaTermico
 *   rappresenta il fattore forma di normalizzazione di normalizzazione
 *   per gli edifici scolastici (alcuni)
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Fe.valore",
                query="SELECT u.valFe FROM FattoreNormFormaTermico u, IN (u.tipiApplicati) t WHERE (t = :tipo) AND ((:ssuv >=  u.supDispVoluRiscMIN) AND (:ssuv <= u.supDispVoluRiscMAX))")})

public class FattoreNormFormaTermico implements Serializable {

	
	
	public FattoreNormFormaTermico(Set<TipologiaEdifici> tipiApplicati,
			Double supDispVoluRiscMIN, Double supDispVoluRiscMAX, Double valFe) {
		
		this.tipiApplicati = tipiApplicati;
		this.supDispVoluRiscMIN = supDispVoluRiscMIN;
		this.supDispVoluRiscMAX = supDispVoluRiscMAX;
		this.valFe = valFe;
	}
	/**
	 * @return the tipo
	 */
	@ElementCollection(targetClass = TipologiaEdifici.class) 
	@Enumerated(EnumType.STRING)
	public Set<TipologiaEdifici> getTipiApplicati() {
		return tipiApplicati;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipiApplicati(Set<TipologiaEdifici> tipo) {
		this.tipiApplicati = tipo;
	}
	/**
	 * @return the supDispVoluRiscMIN
	 */
	public Double getSupDispVoluRiscMIN() {
		return supDispVoluRiscMIN;
	}
	/**
	 * @param supDispVoluRiscMIN the supDispVoluRiscMIN to set
	 */
	public void setSupDispVoluRiscMIN(Double supDispVoluRiscMIN) {
		this.supDispVoluRiscMIN = supDispVoluRiscMIN;
	}
	/**
	 * @return the supDispVoluRiscMAX
	 */
	public Double getSupDispVoluRiscMAX() {
		return supDispVoluRiscMAX;
	}
	/**
	 * @param supDispVoluRiscMAX the supDispVoluRiscMAX to set
	 */
	public void setSupDispVoluRiscMAX(Double supDispVoluRiscMAX) {
		this.supDispVoluRiscMAX = supDispVoluRiscMAX;
	}
	/**
	 * @return the valFe
	 */
	public Double getValFe() {
		return valFe;
	}
	/**
	 * @param valFe the valFe to set
	 */
	public void setValFe(Double valFe) {
		this.valFe = valFe;
	}


	private static final long serialVersionUID = 1L;

	public FattoreNormFormaTermico() {
		
	}
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
	      return this.id;
	}

	private Long id;
	public void setId(Long id) {
		this.id = id;
	}


	private Set<TipologiaEdifici> tipiApplicati;
	private Double  supDispVoluRiscMIN;
	private Double  supDispVoluRiscMAX;
	private Double   valFe;
	
}
