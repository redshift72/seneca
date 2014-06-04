package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: FattoreNormUso
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Fh.valore",
                query="SELECT u.valoreFattoreH FROM FattoreNormUso u, IN (u.tipiApplicati) t WHERE (t = :tipo) AND ((:h >=  u.oreGiornoMIN) AND (:h <= u.oraGiornoMAX))")})
public class FattoreNormUso implements Serializable {

	
	
	/**
	 * @return the oreGiornoMIN
	 */
	public double getOreGiornoMIN() {
		return oreGiornoMIN;
	}
	/**
	 * @param oreGiornoMIN the oreGiornoMIN to set
	 */
	public void setOreGiornoMIN(double oreGiornoMIN) {
		this.oreGiornoMIN = oreGiornoMIN;
	}
	/**
	 * @return the oraGiornoMAX
	 */
	public double getOraGiornoMAX() {
		return oraGiornoMAX;
	}
	/**
	 * @param oraGiornoMAX the oraGiornoMAX to set
	 */
	public void setOraGiornoMAX(double oraGiornoMAX) {
		this.oraGiornoMAX = oraGiornoMAX;
	}
	/**
	 * @return the valoreFattoreH
	 */
	public double getValoreFattoreH() {
		return valoreFattoreH;
	}
	/**
	 * @param valoreFattoreH the valoreFattoreH to set
	 */
	public void setValoreFattoreH(double valoreFattoreH) {
		this.valoreFattoreH = valoreFattoreH;
	}

	private static final long serialVersionUID = 1L;

	public FattoreNormUso() {
		
	}
   
  private Set<TipologiaEdifici> tipiApplicati;	
  /**
 * @return the tipiApplicati
 */
  
  @ElementCollection(targetClass = TipologiaEdifici.class) 
  @Enumerated(EnumType.STRING)
public Set<TipologiaEdifici> getTipiApplicati() {
	return tipiApplicati;
}
/**
 * @param tipiApplicati the tipiApplicati to set
 */
public void setTipiApplicati(Set<TipologiaEdifici> tipiApplicati) {
	this.tipiApplicati = tipiApplicati;
}

private Long id;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}

private double oreGiornoMIN;
  private double oraGiornoMAX;
  private double valoreFattoreH;
  
	
}
