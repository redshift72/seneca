package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

/**
 * possono avere valori
 *  costanti
 * inseriti dall'utente
 * valutati da una named query sul dominio
 * 
 * @author fab
 *
 */
@Entity
public class ParamIntervento implements Serializable, Securable
{

  
   
   public ParamIntervento() {
		this.interventi = new HashSet<FunzioneDiValutazione>();
	}

public ParamIntervento(String nome, String unitaMisura, double fixValue,
			double usrValue, String jpqlString, String hsqlString,
			String descrizione, Double defaultValueOnError,
			FunzioneDiValutazione recuperoValoreIntervento,TipologiaParIntervento tipoPar) {
		
		this.nome = nome;
		this.unitaMisura = unitaMisura;
		this.fixValue = fixValue;
		this.usrValue = usrValue;
		this.jpqlString = jpqlString;
		this.hsqlString = hsqlString;
		this.descrizione = descrizione;
		this.defaultValueOnError = defaultValueOnError;
		this.recuperoValoreIntervento = recuperoValoreIntervento;
		this.tipoParametro=tipoPar;
	}

/**
	 * @return the hsqlString
	 */
	public String getHsqlString() {
		return hsqlString;
	}

	/**
	 * @param hsqlString the hsqlString to set
	 */
	public void setHsqlString(String hsqlString) {
		this.hsqlString = hsqlString;
	}

	/**
	 * @return the descrizione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param descrizione the descrizione to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/** 
    *  0 parametro immesso a valore fisso
    *  1 parametro immesso da utente mediante intefaccia
    *  2 parametro recuperato tramite esecuzione query dinamica Java Persistent QL
    *  3 parametro recuperato tramite esecuzione query dinamica Hibernate SQL    
    
	 * @return the tipoParametro
	 */
	//@NotNull
	public TipologiaParIntervento getTipoParametro() {
		return tipoParametro;
	}

	
	/**
	 * 0 parametro immesso a valore fisso
    *  1 parametro immesso da utente mediante intefaccia
    *  2 parametro recuperato tramite esecuzione query dinamica Java Persistent QL
    *  3 parametro recuperato tramite esecuzione query dinamica Hibernate SQL  
	 * @param tipoParametro the tipoParametro to set
	 */
	public void setTipoParametro(TipologiaParIntervento tipologiaParIntervento) {
		this.tipoParametro = tipologiaParIntervento;
	}

/**
    * @return the nome
    */
	@Id
   public String getNome()
   {
      return nome;
   }

   /**
    * @param nome the nome to set
    */
   public void setNome(String nome)
   {
      this.nome = nome;
   }

   /**
    * @return the unitaMisura
    */
   //@NotNull
   public String getUnitaMisura()
   {
      return unitaMisura;
   }

   /**
    * @param unitaMisura the unitaMisura to set
    */
   public void setUnitaMisura(String unitaMisura)
   {
      this.unitaMisura = unitaMisura;
   }

   /**
    * @return the fixValue
    */
   public double getFixValue()
   {
      return fixValue;
   }

   /**
    * @param fixValue the fixValue to set
    */
   public void setFixValue(double fixValue)
   {
      this.fixValue = fixValue;
   }

   /**
    * @return the usrValue
    */
   public double getUsrValue()
   {
      return usrValue;
   }

   /**
    * @param usrValue the usrValue to set
    */
   public void setUsrValue(double usrValue)
   {
      this.usrValue = usrValue;
   }

   /**
    * @return the jpqlString
    */
   public String getJpqlString()
   {
      return jpqlString;
   }

   /**
    * @param jpqlString the jpqlString to set
    */
   public void setJpqlString(String jpqlString)
   {
      this.jpqlString = jpqlString;
   }

   @ManyToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
   public Set<FunzioneDiValutazione> getInterventi() {
	
	   if(interventi==null)
		   this.interventi = new HashSet<FunzioneDiValutazione>();
	   
	   return interventi;
}

public void setInterventi(Set<FunzioneDiValutazione> interventi) {
	this.interventi = interventi;
}


   private String nome;

   
   private String unitaMisura;

   private double fixValue;
   private double usrValue;

   private String jpqlString;
   private String hsqlString;
   private String descrizione;

   private String UILabel;

    public  String getUILabel() {
	return UILabel;
}

public  void setUILabel(String uILabel) {
	UILabel = uILabel;
}


	/**
     * insieme di interventi migliorativi in cui questo parametro fa parte.
     * se questo parametro fosse il risultato  di un intervento migliorativo 
     * non potrebbe essere il risultato di uno di questi senza creare un loop infinto,
     * xke il parametro si troverebbe nello stesso tempo ad essere si l'input di un intervento migliorativo 
     * che l'out dello l stesso intervento.
     * 
     */
   
    private Set<FunzioneDiValutazione> interventi;
  
    @PrePersist
    @PreUpdate
   private void validaTipoParametro()
   {
	      if(tipoParametro==TipologiaParIntervento.IMPROVEMENT_ACTION_VALUE)
	      {
	    	  
	    	  if(recuperoValoreIntervento==null)
	    	  {
	    		  throw new ValidationException("Intervento migliorativo non definito:Il valore dall'intervento migliorativo non puo essere recuparato");
	    	      
	    	  }
	    	  else
	    	  {
	    		  if ((interventi!=null) && interventi.contains(recuperoValoreIntervento))
	    			  throw new ValidationException("Dipendenza ricorsiva: il parametro non puo essere conteporaneamente l'input di un intervento migliorativo è l'output dello stesso");
	    	  }
	      }
   }
   
    /*
    public static enum TipoPar {
        FIX_VALUE, USER_VALUE, JPQL_VALUE, HSQL_VALUE,
        IMPROVEMENT_ACTION_VALUE 
    }
    */
    
   /**
    *  0 parametro immesso a valore fisso
    *  1 parametro immesso da utente mediante intefaccia
    *  2 parametro recuperato tramite esecuzione query dinamica Java Persistent QL
    *  3 parametro recuperato tramite esecuzione query dinamica Hibernate SQL    
    *  4 parametro recuperato tramite risultato di un intervento migliorativo(funzione di valutazione)
    */
   
   private TipologiaParIntervento tipoParametro;
   
   private Double defaultValueOnError;
   
   private Double maxValue;
   
   /**
    * recupera il maxValue
    * se è diverso da null e il valore ritornato è superiore o uguale a tale valoreMax
    * ritornera' questo valMax
    * @return
    */
   @Nullable
   public Double getMaxValue() {
	return maxValue;
}

   /**
    * imposta il maxValue
    * se è diverso da null e il valore ritornato è superiore o uguale a tale valoreMax
    * ritornera' questo valMax
    * @return
    */
public void setMaxValue(Double maxValue) {
	this.maxValue = maxValue;
}
/**
 * recupera  il minValue
 * se è diverso da null e il valore ritornato è inferiore o uguale a tale minValue
 * ritornera' questo minValue
 * @return
 */
@Nullable
public Double getMinValue() {
	return minValue;
}
/**
 * imposta  il minValue
 * se è diverso da null e il valore ritornato è inferiore o uguale a tale minValue
 * ritornera' questo minValue
 * @return
 */
public void setMinValue(Double minValue) {
	this.minValue = minValue;
}


private Double minValue;
   
   /**
 * @param defaultValueOnError the defaultValueOnError to set
 */
public void setDefaultValueOnError(Double defaultValueOnError) {
	this.defaultValueOnError = defaultValueOnError;
}



 private FunzioneDiValutazione recuperoValoreIntervento;

/**
 * in caso il parametro sia una funzione di valutazione
 * qui c'è il riferimento a tale funzione
 * @return the recuperoValoreIntervento
 */
 @ManyToOne
public FunzioneDiValutazione getRecuperoValoreIntervento() {
	return recuperoValoreIntervento;
}

/**
 * @param recuperoValoreIntervento the recuperoValoreIntervento to set
 */
public void setRecuperoValoreIntervento(
		FunzioneDiValutazione recuperoValoreIntervento) {
	this.recuperoValoreIntervento = recuperoValoreIntervento;
}

public Double getDefaultValueOnError() {
	
	return this.defaultValueOnError;
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
