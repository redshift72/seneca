package it.enea.lecop.eca.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;

public interface GenericFunz {

	/**
	 * @return the valutazioni
	 */

	@OneToMany
	public abstract List<Valutazione> getValutazioniIniziali();

	/**
	 * @param valutazioni the valutazioni to set
	 */
	public abstract void setValutazioniIniziali(List<Valutazione> valutazioni);

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public abstract Long getId();

	/**
	 * @param id the id to set
	 */
	public abstract void setId(Long id);

	/**
	 * @return the descrizione
	 */
	@NotEmpty
	public abstract String getDescrizione();

	/**
	 * @param descrizione the descrizione to set
	 */
	public abstract void setDescrizione(String descrizione);

	/**
	 * parametri della funzione coppia nome-parametro
	 * sono i parametri necessari per poter fare i calcoli dell'intervento 
	 * migliorativo
	 * Ad un intervento migliorativo sono associati piÃ¹ parametri
	 * 
	 */
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public abstract Map<String, ParamIntervento> getParametri();

	/**
	 * @param parametri the parametri to set
	 */
	public abstract void setParametri(Map<String, ParamIntervento> parametri);

	/**
	 * @return the tipo
	 */
	@ElementCollection(targetClass = TipologiaEdifici.class)
	public abstract Set<TipologiaEdifici> getApplicaTipoEdifici();

	/**
	 * @param applicaTipoValutazione the tipo to set
	 */
	public abstract void setApplicaTipoEdifici(
			Set<TipologiaEdifici> applicaTipoEdifici);

	/**
	 * algoritmo di calcolo simbolico.
	 * I nomi dei simboli sono contenuti nell'hashMap   
	 * 
	 * con symja  
	 */
	public abstract String getCalcolo();

	/**
	 * algoritmo di calcolo simbolico.
	 * I nomi dei simboli sono contenuti nell'hashMap   
	 * 
	 * con symja  
	 */
	public abstract void setCalcolo(String calcolo);

	@NotEmpty
	public abstract String getName();

	public abstract void setName(String name);

	@Enumerated(EnumType.STRING)
	public abstract TipologiaValutazione getApplicaTipoValutazione();

	public abstract void setApplicaTipoValutazione(
			TipologiaValutazione applicaTipoValutazione);

	@Transient
	public abstract Set<String> getExprParam();

	public abstract void setExprParam(Set<String> exprParam);

	/**
	 *  algoritmo di calcolo simbolico verificato. E' simbolicamente verificato
	 * ma potrebbe non essere 
	 * 
	 */
	public abstract void setCalcChecked(String calcChecked);

	/**
	 *  algoritmo di calcolo simbolico verificato. E' simbolicamente verificato
	 * ma potrebbe non essere 
	 * 
	 */
	public abstract String getCalcChecked();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public abstract int hashCode();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public abstract boolean equals(Object obj);

	@Version
	public abstract long getVersion();

	public abstract void setVersion(long version);

}