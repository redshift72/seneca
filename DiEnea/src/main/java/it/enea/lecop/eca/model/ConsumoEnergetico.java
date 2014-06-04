package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
    @NamedQuery(name="ConsumoEnergetico.findAll",
                query="SELECT u FROM ConsumoEnergetico u"),
    @NamedQuery(name="ConsumoEnergetico.findById",
                query="SELECT u FROM ConsumoEnergetico u WHERE u.id = :id")
    
}) 
public class ConsumoEnergetico implements Serializable{
	
	public ConsumoEnergetico() {
		
	}
	
	public ConsumoEnergetico(TipologiaConsumi tipo, Double value, Double efficienzaConversione) {
		
		this.tipo = tipo;
		this.value = value;
		this.efficienzaConversione= efficienzaConversione;
		
	}

	private Long id;

	private TipologiaConsumi tipo=TipologiaConsumi.KWH_T;
	//valore espresso nella unità di misura definita dal tipo
	private Double value;
	//indica con quale efficienza avviene la conversione in energia termica partendo dal potere calorifero
	// del cobustibile
	private Double efficienzaConversione=1.0;
	private String descrizione;
	
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public  Double getEfficienzaConversione() {
		if (efficienzaConversione==null) return 1.0;
		return efficienzaConversione;
	}

	public  void setEfficienzaConversione(Double efficienzaConversione) {
		this.efficienzaConversione = efficienzaConversione;
	}

	@Enumerated(EnumType.STRING)
	public TipologiaConsumi getTipo() {
		return tipo;
	}

	public void setTipo(TipologiaConsumi tipo) {
		this.tipo = tipo;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	
	
	
	public void setId(Long id) {
		this.id = id;
	}

	/**
	    * @return the id
	    */
	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   public Long getId()
	   {
	      return id;
	   }

}
