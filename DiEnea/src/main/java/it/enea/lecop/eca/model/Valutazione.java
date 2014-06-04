package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import javax.persistence.CascadeType;
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
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import java.util.Calendar;
import javax.persistence.Cacheable;

/**
 * valutazione elettrica o termica o idrica di un valutatore riferita ad una azienda
 * @author fab
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Valutazione.deleteAllOwnerUser",query="DELETE FROM Valutazione u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
	@NamedQuery(name="Valutazione.findAllOwnerUser",query="SELECT u FROM ComposizioneEdifici u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
    @NamedQuery(name="Valutazione.findAll",
                query="SELECT u FROM Valutazione u"),
    @NamedQuery(name="Valutazione.findById",
                query="SELECT u FROM Valutazione u WHERE u.id = :id"),
    @NamedQuery(name="Valutazione.findByProfiloUsoConsumoId",
                query="SELECT u FROM Valutazione u WHERE u.profiloUsoConsumo.id = :id"),            
    @NamedQuery(name="Valutazione.findAllOWNERUSER",
         query="SELECT u FROM Valutazione u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"), 
    @NamedQuery(name="Valutazione.findAllOWNDOMAIN",
            query="SELECT u FROM Valutazione u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
    @NamedQuery(name="Valutazione.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM Valutazione u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
       @NamedQuery(name="Valutazione.findAllOWNERUSER_OWNDOMAIN",
                        query="SELECT u FROM Valutazione u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
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
}) 
public class Valutazione implements Serializable, Securable
{

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
    * data di apertura della valutazione
    */
   @Temporal(value = TemporalType.TIMESTAMP)
   public Calendar getCreazione()
   {
      return creazione;
   }

   /**
    * @param creazione the creazione to set
    */
   public void setCreazione(Calendar creazione)
   {
      this.creazione = creazione;
   }

   /**
    * restituisce l'azienda a cui si fa la valutazione
    * @return the azienda
    */
   @ManyToOne
   @NotNull
   public Azienda getAzienda()
   {
      return azienda;
   }

   /**
    * @param azienda the azienda to set
    */
   public void setAzienda(Azienda azienda)
   {
      this.azienda = azienda;
   }

   
   
   /**
    * riferimento ad un uso di una  composizione specifica dell'azienda 
    * nel profilo d'uso vi e' una composizione di edifici dell'azienda.
    * perchè ad ogni profilo è associata una composizione
    * Di solito i valori dello stesso tipo sono sommabili tra i profili, ed è il totale dei consumi che interessa
    * di solito. Tale somma potra' cmq essere fatta da una opportuna query jpql nei parametri che la necessitano
    */

   @ManyToOne(fetch=FetchType.EAGER)
   @NotNull
   public ProfiloUsoConsumo getProfiloUsoConsumo()
   {
      return profiloUsoCOnsumo;
   }

   /**
    * @param consumiInUso the consumiInUso to set
    */
   public void setProfiloUsoConsumo(ProfiloUsoConsumo consumiInUso)
   {
      this.profiloUsoCOnsumo = consumiInUso;
   }

   /**
    * @return the tipo
    */
   @Enumerated(EnumType.STRING)
   public TipologiaValutazione getTipo()
   {
      return tipo;
   }

   /**
    * @param tipo the tipo to set
    */
   public void setTipo(TipologiaValutazione tipo)
   {
      this.tipo = tipo;
   }

   
   /**
    * lista di interventi migliorativi congrui al tipo di valutazione.
    * nella lista ci saranno solo gli interventi migliorativi dello stesso tipo di tipoValutazione.
    * 
    */
   @ManyToMany(fetch=FetchType.EAGER)
   public List<FunzioneDiValutazione> getInterventi()
   {
	  
      return interventi;
   }

   /**
    * @param interventi the interventi to set
    */
   public void setInterventi(List<FunzioneDiValutazione> interventi)
   {
      this.interventi = interventi;
   }

   
   
   /**
    * ogni intervento migliorativo puo portare a risultaTI
    */

   @OneToMany(mappedBy="valutazione",fetch=FetchType.EAGER,cascade=CascadeType.ALL)
   public Set<RisultatoValutazioneIntervento> getRisultati()
   {
      return risultati;
   }

   /**
    * @param risultati the risultati to set
    */
   public void setRisultati(Set<RisultatoValutazioneIntervento> risultati)
   {
      this.risultati = risultati;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((azienda == null) ? 0 : azienda.hashCode());
      result = prime * result + ((profiloUsoCOnsumo == null) ? 0 : profiloUsoCOnsumo.hashCode());
      result = prime * result + ((creazione == null) ? 0 : creazione.hashCode());
      result = prime * result + ((interventi == null) ? 0 : interventi.hashCode());
     
      result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Valutazione other = (Valutazione) obj;
      if (azienda == null)
      {
         if (other.azienda != null)
            return false;
      }
      else if (!azienda.equals(other.azienda))
         return false;
      if (profiloUsoCOnsumo == null)
      {
         if (other.profiloUsoCOnsumo != null)
            return false;
      }
      else if (!profiloUsoCOnsumo.equals(other.profiloUsoCOnsumo))
         return false;
      if (creazione == null)
      {
         if (other.creazione != null)
            return false;
      }
      else if (!creazione.equals(other.creazione))
         return false;
      if (interventi == null)
      {
         if (other.interventi != null)
            return false;
      }
      else if (!interventi.equals(other.interventi))
         return false;
     
      if (tipo == null)
      {
         if (other.tipo != null)
            return false;
      }
      else if (!tipo.equals(other.tipo))
         return false;
      return true;
   }

  
   private Long id;

   
   
   private Calendar creazione;

   /**
    * azienda privata o pubblica,  o privato, a cui e' riferita la valutazione
    * piÃ¹ valutazioni possomo essere fatte alla stessa azienda
    * l'azienza puÃ² avere aperto piÃ¹ valutazioni
    */
   
   private Azienda azienda;

   private String descrizione;
   
   
   private ProfiloUsoConsumo profiloUsoCOnsumo;

   /**
    * tipo di valutazione   termica, elettrica idrica ecc
    * 
    *
    */
   
   private TipologiaValutazione tipo;

   private FunzioneDiValutazione valutazioneIniziale;
   
   private List<FunzioneDiValutazione> interventi;

  
   private Set<RisultatoValutazioneIntervento> risultati;
   
   
   private Double risultatoValutazioneIniziale;
   
   // indice di prestazione iniziale è un voto da 0 a 10 
   private Double prestazioneIniziale;
 

    public Double getPrestazioneIniziale() {
	return prestazioneIniziale;
}

public void setPrestazioneIniziale(Double prestazioneIniziale) {
	this.prestazioneIniziale = prestazioneIniziale;
}

	public synchronized Double getRisultatoValutazioneIniziale() {
	return risultatoValutazioneIniziale;
}

public synchronized void setRisultatoValutazioneIniziale(Double risultatoIniziale) {
	this.risultatoValutazioneIniziale = risultatoIniziale;
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

	 /**
	  * selezione una funzione di valutazione tra quelle delle stesso tipo 
	  * della valutazione   (sidovrebbero controllare anche gli edifici se rientrano tra quelli
	  * permessi dalla fuzione di valutazione)
	  * @return
	  */
	@ManyToOne   
	public FunzioneDiValutazione getValutazioneIniziale() {
		return valutazioneIniziale;
	}

	public void setValutazioneIniziale(FunzioneDiValutazione funzValutazione) {
		this.valutazioneIniziale = funzValutazione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
