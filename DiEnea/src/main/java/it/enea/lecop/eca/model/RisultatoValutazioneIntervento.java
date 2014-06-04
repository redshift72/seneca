package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.persistence.UniqueConstraint;

/**
 * restituisce un risultato di una valutazione con un intervento
 * un risultato completo ha una valutazione economica
 * Quindi una valutazione ha almeno un risultato di default.
 * @author fab
 *
 */

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "valutazione_id", "intervento_id" }))
public class RisultatoValutazioneIntervento implements Serializable, Securable
{

	/**
	 * @return the calcoloEconomico
	 */
	 @ManyToOne
	public FunzioneDiValutazione getCalcoloEconomico() {
		return calcoloEconomico;
	}

	/**
	 * @param calcoloEconomico the calcoloEconomico to set
	 */
	public void setCalcoloEconomico(FunzioneDiValutazione calcoloEconomico) {
		this.calcoloEconomico = calcoloEconomico;
	}

	/**
	 * @return the risultatoEconomico
	 */
	public Double getRisultatoEconomico() {
		return risultatoEconomico;
	}

	/**
	 * @param risultatoEconomico the risultatoEconomico to set
	 */
	public void setRisultatoEconomico(Double risultatoEconomico) {
		this.risultatoEconomico = risultatoEconomico;
	}

	public RisultatoValutazioneIntervento()
	{
		
	}
	
   public RisultatoValutazioneIntervento(Valutazione valutazione,
			FunzioneDiValutazione intervento, Double risultato) {
		
		setValutazione(valutazione);
		setIntervento(intervento);
		setRisultato(risultato);
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

   /**
    * @param id the id to set
    */
   public void setId(Long id)
   {
      this.id = id;
   }

   /**
    * @return the valutazione
    */
   @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE})
   @NotNull
   public Valutazione getValutazione()
   {
      return valutazione;
   }

   /**
    * @param valutazione the valutazione to set
    */
   public void setValutazione(Valutazione valutazione)
   {
      this.valutazione = valutazione;
   }

   /**
    * @return the intervento
    */
   @ManyToOne
   @NotNull
   public FunzioneDiValutazione getIntervento()
   {
      return intervento;
   }

   /**
    * @param intervento the intervento to set
    */
   public void setIntervento(FunzioneDiValutazione intervento)
   {
      this.intervento = intervento;
   }

   /**
    * @return the risultato
    */
   @NotNull
   public Double getRisultato()
   {
      return risultato;
   }

   /**
    * @param risultato the risultato to set
    */
   public void setRisultato(Double risultato)
   {
      this.risultato = risultato;
   }

   @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
			+ ((intervento == null) ? 0 : intervento.hashCode());
	result = prime * result + ((risultato == null) ? 0 : risultato.hashCode());
	result = prime * result
			+ ((valutazione == null) ? 0 : valutazione.hashCode());
	return result;
}

   @Override
public boolean equals(Object obj) {
	if (this == obj) {
		return true;
	}
	if (obj == null) {
		return false;
	}
	if (getClass() != obj.getClass()) {
		return false;
	}
	RisultatoValutazioneIntervento other = (RisultatoValutazioneIntervento) obj;
	if (intervento == null) {
		if (other.intervento != null) {
			return false;
		}
	} else if (!intervento.equals(other.intervento)) {
		return false;
	}
	if (risultato == null) {
		if (other.risultato != null) {
			return false;
		}
	} else if (!risultato.equals(other.risultato)) {
		return false;
	}
	if (valutazione == null) {
		if (other.valutazione != null) {
			return false;
		}
	} else if (!valutazione.equals(other.valutazione)) {
		return false;
	}
	return true;
}

  
   private Long id;

   
   private Valutazione valutazione;

   
   private FunzioneDiValutazione intervento;

   
   private Map<String,Double> UIVal;
   
   
   @ElementCollection(fetch=FetchType.EAGER,targetClass=Double.class)
   @MapKeyColumn(name="parameterName")
   public  Map<String, Double> getUIVal() {
	return UIVal;
}

public  void setUIVal(Map<String, Double> uIVal) {
	UIVal = uIVal;
}


private Double risultato;
   
  
   private FunzioneDiValutazione  calcoloEconomico;    
   
   private Double risultatoEconomico;
   
   /**
    * permette di verificare se i parametri legati al calcolo sono cambiati
    * 
    */
   private void calcolaVersione()
   {
	   Valutazione val=null;
	   if ((val=getValutazione())!=null)
	   {
		   long versionProfilo=val.getProfiloUsoConsumo().getVersion();
		   long versionComposizione=val.getProfiloUsoConsumo().getComposizioneEdificio().getVersion();
	   }
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
	   
	   
	   private long version;
		 @Version
			public long getVersion() {
				return version;
			}

			public void setVersion(long version) {
				this.version = version;
			}
   
}
