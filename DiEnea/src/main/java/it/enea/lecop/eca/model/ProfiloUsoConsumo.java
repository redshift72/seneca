package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.apache.tools.ant.taskdefs.UpToDate;



/**
 * 
 * lega la ComposizioneDiEdifici con i consumi di tale composizione per un dato uso 
 * @author fab
 *
 */
@Entity
@NamedQueries({@NamedQuery(name="ProfiloUsoConsumo.findAllAzienda",query="SELECT u FROM ProfiloUsoConsumo u" +
		" WHERE (u.azienda.nome = :azname)"),
	@NamedQuery(name="ProfiloUsoConsumo.deleteAllOwnerUser",query="DELETE FROM ProfiloUsoConsumo u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
	@NamedQuery(name="ProfiloUsoConsumo.findAllOwnerUser",query="SELECT u FROM ProfiloUsoConsumo u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
    @NamedQuery(name="ProfiloUsoConsumo.findAll",
                query="SELECT u FROM ProfiloUsoConsumo u"),
    @NamedQuery(name="ProfiloUsoConsumo.findById",
                query="SELECT u FROM ProfiloUsoConsumo u WHERE u.id = :id"),
    @NamedQuery(name="ProfiloUsoConsumo.findAllOWNERUSER",
         query="SELECT u FROM ProfiloUsoConsumo u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"),
    @NamedQuery(name="ProfiloUsoConsumo.findAllOWNDOMAIN",
            query="SELECT u FROM ProfiloUsoConsumo u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
      		       
    @NamedQuery(name="ProfiloUsoConsumo.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM ProfiloUsoConsumo u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
     @NamedQuery(name="ProfiloUsoConsumo.findAllOWNERUSER_OWNDOMAIN",
                         query="SELECT u FROM ProfiloUsoConsumo u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                   		       " or (" +
                   		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))" +
                   		       "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4))") 
               /*    		    +   " or (( SIZE(u.ownerid.ownCompany.aziende) = (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az MEMBER OF :aziendeLogin)) " +
                   		       "and (" +
                   		       "u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1,:ownsubsetSec2,:ownsubsetSec3,:ownsubsetSec4)))" +
                   		       "or  (( SIZE(u.ownerid.ownCompany.aziende) < (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az MEMBER OF :aziendeLogin)) " +
                   		       "and (" +
                   		       "u.permissionprop.INTERSECTIONDOMAIN IN (:ownintersecSec1,:ownintersecSec2,:ownintersecSec3,:ownintersecSec4)))")             		
*/
})
public class ProfiloUsoConsumo implements Serializable, Securable
{

   private Azienda azienda;

public ProfiloUsoConsumo() {
		
	}

public ProfiloUsoConsumo(String nome, TipologiaValutazione tipo,
			 String descrizione, Double consumoElettricoTot,
			Double consumoTermicoTot, Double consumoAcquaTot,
			Double oreSuGiorniTermico, Double oreSuGiorniAcqua,
			Double oreSuGiorniElettrico,
			ComposizioneEdifici composizioneEdificio, Azienda az,OwnerId ownerid,
			PermissionProp permissionprop) {
		
		this.nome = nome;
		this.tipo = tipo;
		
		this.descrizione = descrizione;
		this.consumoElettricoTot = consumoElettricoTot;
		this.consumoTermicoTot = consumoTermicoTot;
		this.consumoAcquaTot = consumoAcquaTot;
		this.oreSuGiorniTermico = oreSuGiorniTermico;
		this.oreSuGiorniAcqua = oreSuGiorniAcqua;
		this.oreSuGiorniElettrico = oreSuGiorniElettrico;
		this.composizioneEdificio = composizioneEdificio;
		this.ownerid = ownerid;
		this.permissionprop = permissionprop;
		this.azienda=az;
	}

/*
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime
				* result
				+ ((composizioneEdificio == null) ? 0 : composizioneEdificio
						.hashCode());
		result = prime * result
				+ ((consumoAcquaTot == null) ? 0 : consumoAcquaTot.hashCode());
		result = prime
				* result
				+ ((consumoElettricoTot == null) ? 0 : consumoElettricoTot
						.hashCode());
		result = prime
				* result
				+ ((consumoTermicoTot == null) ? 0 : consumoTermicoTot
						.hashCode());
		result = prime * result
				+ ((descrizione == null) ? 0 : descrizione.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime
				* result
				+ ((oreSuGiorniAcqua == null) ? 0 : oreSuGiorniAcqua.hashCode());
		result = prime
				* result
				+ ((oreSuGiorniElettrico == null) ? 0 : oreSuGiorniElettrico
						.hashCode());
		result = prime
				* result
				+ ((oreSuGiorniTermico == null) ? 0 : oreSuGiorniTermico
						.hashCode());
		result = prime * result + ((ownerid == null) ? 0 : ownerid.hashCode());
		result = prime * result
				+ ((permissionprop == null) ? 0 : permissionprop.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}
*/

/*
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
		ProfiloUsoConsumo other = (ProfiloUsoConsumo) obj;
		
		if (composizioneEdificio == null) {
			if (other.composizioneEdificio != null) {
				return false;
			}
		} else if (!composizioneEdificio.equals(other.composizioneEdificio)) {
			return false;
		}
		if (consumoAcquaTot == null) {
			if (other.consumoAcquaTot != null) {
				return false;
			}
		} else if (!consumoAcquaTot.equals(other.consumoAcquaTot)) {
			return false;
		}
		if (consumoElettricoTot == null) {
			if (other.consumoElettricoTot != null) {
				return false;
			}
		} else if (!consumoElettricoTot.equals(other.consumoElettricoTot)) {
			return false;
		}
		if (consumoTermicoTot == null) {
			if (other.consumoTermicoTot != null) {
				return false;
			}
		} else if (!consumoTermicoTot.equals(other.consumoTermicoTot)) {
			return false;
		}
		if (descrizione == null) {
			if (other.descrizione != null) {
				return false;
			}
		} else if (!descrizione.equals(other.descrizione)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (oreSuGiorniAcqua == null) {
			if (other.oreSuGiorniAcqua != null) {
				return false;
			}
		} else if (!oreSuGiorniAcqua.equals(other.oreSuGiorniAcqua)) {
			return false;
		}
		if (oreSuGiorniElettrico == null) {
			if (other.oreSuGiorniElettrico != null) {
				return false;
			}
		} else if (!oreSuGiorniElettrico.equals(other.oreSuGiorniElettrico)) {
			return false;
		}
		if (oreSuGiorniTermico == null) {
			if (other.oreSuGiorniTermico != null) {
				return false;
			}
		} else if (!oreSuGiorniTermico.equals(other.oreSuGiorniTermico)) {
			return false;
		}
		if (ownerid == null) {
			if (other.ownerid != null) {
				return false;
			}
		} else if (!ownerid.equals(other.ownerid)) {
			return false;
		}
		if (permissionprop == null) {
			if (other.permissionprop != null) {
				return false;
			}
		} else if (!permissionprop.equals(other.permissionprop)) {
			return false;
		}
		if (tipo == null) {
			if (other.tipo != null) {
				return false;
			}
		} else if (!tipo.equals(other.tipo)) {
			return false;
		}
		return true;
	}
*/


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
    * @return the nome
    */
   @NotNull
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
    * @return the tipo
    */
   
   @NotNull
   @Enumerated(EnumType.STRING)
   public TipologiaValutazione getTipo()
   {
	   //System.out.println("GET TIPO------"+this.tipo);
	   //System.out.flush();
      return tipo;
   }

   /**
    * @param tipo the tipo to set
    */
  
   public void setTipo(TipologiaValutazione tipo)
   {
	   //System.out.println("SET TIPO------"+tipo);
	   //System.out.flush();
      this.tipo = tipo;
   }

   @ManyToOne
   // @NotNull
    public Azienda getAzienda()
    {
       return azienda;
    }

    public void setAzienda(Azienda azienda)
    {
       this.azienda = azienda;
    }
   
   
   /**
    * @return the descrizione
    */
   public String getDescrizione()
   {
      return descrizione;
   }

   /**
    * @param descrizione the descrizione to set
    */
   public void setDescrizione(String descrizione)
   {
      this.descrizione = descrizione;
   }

   /**
    * @return the consumoElettricoTot
    */
   public Double getConsumoElettricoTot()
   {
      return consumoElettricoTot;
   }

   /**
    * @param consumoElettricoTot the consumoElettricoTot to set
    */
   public void setConsumoElettricoTot(Double consumoElettricoTot)
   {
      this.consumoElettricoTot = consumoElettricoTot;
   }

   /**
    * @return the consumoTermicoTot
    */
   public Double getConsumoTermicoTot()
   {
  	   return consumoTermicoTot;
		
   }

   /**
    * @param consumoTermicoTot the consumoTermicoTot to set
    */
   public void setConsumoTermicoTot(Double consumoTermicoTot)
   {
    
		   this.consumoTermicoTot = consumoTermicoTot;
       
    }

   /**
    * @return the consumoAcquaTot
    */
   public Double getConsumoAcquaTot()
   {
      return consumoAcquaTot;
   }

   /**
    * @param consumoAcquaTot the consumoAcquaTot to set
    */
   public void setConsumoAcquaTot(Double consumoAcquaTot)
   {
      this.consumoAcquaTot = consumoAcquaTot;
   }

   /**
    * @return the composizioneEdificio
    */
   @ManyToOne(cascade = {CascadeType.PERSIST}) 
   public ComposizioneEdifici getComposizioneEdificio()
   {
      return composizioneEdificio;
   }

   /**
    * @param composizioneEdificio the composizioneEdificio to set
    */
   public void setComposizioneEdificio(ComposizioneEdifici composizioneEdificio)
   {
      this.composizioneEdificio = composizioneEdificio;
   }

  
   private Long id;

   
   private String nome;

   
   private TipologiaValutazione tipo;
   
   private int numPersone;

   public int getNumPersone() {
	return numPersone;
}

public void setNumPersone(int numPersone) {
	this.numPersone = numPersone;
}


/*
    * 
    * puntando ad una composizione
    * la composizione è legata ad una azienda quindi non ha senso ripetere il
    * collegamento relazionale
    * 
   private Azienda   azienda;
   
   @ManyToOne
   @NotNull
   public Azienda getAzienda() {
	return azienda;
}

public void setAzienda(Azienda azienda) {
	this.azienda = azienda;
}
*/
private String descrizione;

   // media degli ultimi 3-5 anni in kWhe
   private Double consumoElettricoTot=0.0;

   // media degli ultimi 3-5 anni kWht
   private Double consumoTermicoTot=0.0;

   // media degli ultimi 3-5 anno in m^3
   private Double consumoAcquaTot=0.0;
   
// WARNING DO NOT USE !!!!!
   @OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.ALL})
   public List<ConsumoEnergetico> getConsumi() {
	synchronized (consumi) {
		  return consumi;
		   	
		}
	}
	
   public static class ConsumoComplessivo {
	   public ConsumoComplessivo(Double elettrico, Double idrico, Double termico) {
		super();
		this.elettrico = elettrico;
		this.idrico = idrico;
		this.termico = termico;
	}
	Double elettrico;
	   public Double getElettrico() {
		return elettrico;
	}
	public void setElettrico(Double elettrico) {
		this.elettrico = elettrico;
	}
	public Double getIdrico() {
		return idrico;
	}
	public void setIdrico(Double idrico) {
		this.idrico = idrico;
	}
	public Double getTermico() {
		return termico;
	}
	public void setTermico(Double termico) {
		this.termico = termico;
	}
	Double idrico;
	   Double termico;
   }

	public Double addConsumo(ConsumoEnergetico consumo) 
	{
		// passo il riferimento a consumi xke dentro la struttura viene modificata
		// in questo caso con l'aggiunta di un elemento
	  ConsumoComplessivo cc=	calcoloNuoviConsumi(true,consumo,this.consumi);
		
	  setConsumoAcquaTot(cc.getIdrico());
	  setConsumoElettricoTot(cc.getElettrico());
	  setConsumoTermicoTot(cc.getTermico());
		if (tipo.equals(TipologiaValutazione.TERMICA))
		{
			
			return cc.getTermico();
		}
		if (tipo.equals(TipologiaValutazione.IDRICA))
		{
			return cc.getIdrico();
		}
		if (tipo.equals(TipologiaValutazione.ELETTRICA))
		{
			return cc.getElettrico();
		}
		else return cc.getTermico();
	
	
	}
	
	/**
	 * Calcola i nuovi consumi totali sia idrico che termico che elettrico 
	 * calcolando la lista dei consumi a cui sarà  preventivamente aggiunto o tolto consumo
	 * In caso di isAdd a null, il calcolo viene fatto su consumi, non considerando il consumo 
	 * In caso di consumo a null oppure di consumi a null, viene restituita una struttura di ConsumoComplessivo con 0 su tutti i campi
	 * 
	 * @param isAdd
	 * @param consumo consumo attuale da aggiungere o togliere preventivamente
	 * @param consumi lista di tutti i consumi prima dell'eventuale aggiunta o eliminazione
	 * @return ConsumoComplessivo
	 */
	static public ConsumoComplessivo calcoloNuoviConsumi(Boolean isAdd,ConsumoEnergetico consumo,List<ConsumoEnergetico> consumi)
	{
		
		double  val=0;
		double valIdrico=0;
		double valElettrico=0;
		
		if(consumo!=null && consumi!=null){
			
		
		
		//prendo il lock su consumi
		  synchronized (consumi) {
		
			if(isAdd!=null)
			{	
			  if(isAdd)
			  {
				consumi.add(consumo);
			  }
			  else 
			  {
				consumi.remove(consumo);
			  }
			}
			 for(ConsumoEnergetico idx: consumi)
			 {
			    if(idx.getTipo().tipo().equals(TipologiaValutazione.TERMICA))	
				   {
				     val=val+(idx.getValue()*idx.getTipo().fattore2KWH_T()*idx.getEfficienzaConversione());
				   }
			    else if(idx.getTipo().tipo().equals(TipologiaValutazione.IDRICA))
			    {
				   valIdrico=valIdrico+(idx.getValue()*idx.getEfficienzaConversione());
			    }else if(idx.getTipo().tipo().equals(TipologiaValutazione.ELETTRICA))
			    {
				   valElettrico=valElettrico+(idx.getValue()*idx.getTipo().fattore2KWH_T()*idx.getEfficienzaConversione());
			    }
			}
			
			/*
			setConsumoTermicoTot(val);
			setConsumoAcquaTot(valIdrico);
			setConsumoElettricoTot(valElettrico);
			*/
		}
			
		}
		
		return new ProfiloUsoConsumo.ConsumoComplessivo(valElettrico,valIdrico,val);
		
		
	// TODO Check	
	}
	

	public Double removeConsumo(ConsumoEnergetico consumo) 
	{
		this.consumi.remove(consumo);
		ConsumoComplessivo cc=	calcoloNuoviConsumi(null,consumo,this.consumi);
		
		  setConsumoAcquaTot(cc.getIdrico());
		  setConsumoElettricoTot(cc.getElettrico());
		  setConsumoTermicoTot(cc.getTermico());
			if (tipo.equals(TipologiaValutazione.TERMICA))
			{
				
				return cc.getTermico();
			}
			if (tipo.equals(TipologiaValutazione.IDRICA))
			{
				return cc.getIdrico();
			}
			if (tipo.equals(TipologiaValutazione.ELETTRICA))
			{
				return cc.getElettrico();
			}
			else return cc.getTermico();
		
	}
	
/**
 * cancella un tipo di consumi e pone a zero il risultato delc consumo totale relativo
 * Se tipo==null cancella tutti i consumi di ogni tipo	
 * @param tipo
 */
 public void removeAllConsumo(TipologiaValutazione tipo)
 {
	 List<ConsumoEnergetico> consumiDaEliminare=new ArrayList<ConsumoEnergetico>();
	synchronized (this.consumi) 
	{
		if(tipo!=null) 
		{ 
		  for (ConsumoEnergetico ce:this.consumi)
		   {
			 if (ce.getTipo().tipo().equals(tipo))consumiDaEliminare.add(ce);				 
		   }
		 
		   for (ConsumoEnergetico ce:consumiDaEliminare)this.consumi.remove(ce);
		   
		   if (tipo.equals(TipologiaValutazione.TERMICA))
			{
			   this.setConsumoTermicoTot(0.0);
			}
			if (tipo.equals(TipologiaValutazione.IDRICA))
			{
				this.setConsumoAcquaTot(0.0);
			}
			if (tipo.equals(TipologiaValutazione.ELETTRICA))
			{
				this.setConsumoElettricoTot(0.0);
			}
		   
		}else
		{
			this.consumi.clear();
			this.setConsumoAcquaTot(0.0);
			this.setConsumoElettricoTot(0.0);
			this.setConsumoTermicoTot(0.0);
		}
	} 
	  
	 
	 
	 
 }
public void calcolaConsumiTotali(){
	double  val=0;
	//prendo il lock su consumi
	synchronized (consumi) {
	
		for(ConsumoEnergetico idx: consumi){
			val=val+(idx.getValue()*idx.getTipo().fattore2KWH_T()*idx.getEfficienzaConversione());
		}

		setConsumoTermicoTot(val);
		

	}
	
}	

public void setConsumi(List<ConsumoEnergetico> consumi) {
	synchronized (consumi) {
		this.consumi = consumi;	
	}
	
}


private List<ConsumoEnergetico> consumi=new ArrayList<ConsumoEnergetico>();
   
   
   public Double getOreSuGiorniElettrico() {
	return oreSuGiorniElettrico;
}

public void setOreSuGiorniElettrico(Double oreSuGiorniElettrico) {
	this.oreSuGiorniElettrico = oreSuGiorniElettrico;
}

public Double getOreSuGiorniTermico() {
	return oreSuGiorniTermico;
}

public void setOreSuGiorniTermico(Double oreSuGiorniTermico) {
	this.oreSuGiorniTermico = oreSuGiorniTermico;
}

public Double getOreSuGiorniAcqua() {
	return oreSuGiorniAcqua;
}

public void setOreSuGiorniAcqua(Double oreSuGiorniAcqua) {
	this.oreSuGiorniAcqua = oreSuGiorniAcqua;
}

private Double oreSuGiorniTermico;
private Double oreSuGiorniAcqua;
private Double oreSuGiorniElettrico;



  
   /**
    * lega il profilo d'uso ad una composizione di edifici
    */
   private ComposizioneEdifici composizioneEdificio;

   
   
   
   
   
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
