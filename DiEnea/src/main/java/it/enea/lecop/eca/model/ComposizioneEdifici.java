package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 * la composizione degli edifici e' un complesso di almeno uno o piu' edifici 
 * che viene fatta per aggregato di impianto
 * elettrico e termico. 
 * Di solito fanno parte della stesa composizione di edifici
 * quelli che  condividono lo stesso contatore del gas e contatore elettrico
 * 
 * E' evidente che ad ogni composizione devo poter assegnare un consumo totale termico ed elettrico
 *  
 * @author fab
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(name="ComposizioneEdifici.findAllAzienda",query="SELECT u FROM ComposizioneEdifici u" +
		" WHERE (u.azienda.nome = :azname)"),
	@NamedQuery(name="ComposizioneEdifici.deleteAllOwnerUser",query="DELETE FROM ComposizioneEdifici u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
	@NamedQuery(name="ComposizioneEdifici.findAllOwnerUser",query="SELECT u FROM ComposizioneEdifici u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
    @NamedQuery(name="ComposizioneEdifici.findAll",
                query="SELECT u FROM ComposizioneEdifici u"),
    @NamedQuery(name="ComposizioneEdifici.findById",
                query="SELECT u FROM ComposizioneEdifici u WHERE u.id = :id"),
    @NamedQuery(name="ComposizioneEdifici.findAllOWNERUSER",
         query="SELECT u FROM ComposizioneEdifici u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"),
    @NamedQuery(name="ComposizioneEdifici.findAllOWNDOMAIN",
            query="SELECT u FROM ComposizioneEdifici u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
      		       
    @NamedQuery(name="ComposizioneEdifici.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM ComposizioneEdifici u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
     @NamedQuery(name="ComposizioneEdifici.findAllOWNERUSER_OWNDOMAIN",
                         query="SELECT u FROM ComposizioneEdifici u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
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
                   		       "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4))")})
public class ComposizioneEdifici implements Serializable, Securable
{

   public ComposizioneEdifici() {
		
		this.edifici= new HashSet<Edificio>();
		this.profilo= new HashSet<ProfiloUsoConsumo>();
	}

public ComposizioneEdifici(String name, String noteDellaComposizione,
			Azienda azienda, Set<Edificio> edifici,
			Set<ProfiloUsoConsumo> profilo, Comune comuneUbicazione,
			String provinciaUbicazione, OwnerId ownerid,
			PermissionProp permissionprop) {
		
		this.name = name;
		this.noteDellaComposizione = noteDellaComposizione;
		this.azienda = azienda;
		this.edifici = edifici;
		this.profilo = profilo;
		this.comuneUbicazione = comuneUbicazione;
		this.provinciaUbicazione = provinciaUbicazione;
		this.ownerid = ownerid;
		this.permissionprop = permissionprop;
	}
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getNoteDellaComposizione()
   {
      return noteDellaComposizione;
   }

   public void setNoteDellaComposizione(String noteDellaComposizione)
   {
      this.noteDellaComposizione = noteDellaComposizione;
   }

   
// molte composizione potrebbero fa riferimento ad una azienda
   // ad una azienda potrebbero fa riferimento piÃ¹ composizioni
   // una azienda deve essere specificata
   @ManyToOne
   @NotNull
   public Azienda getAzienda()
   {
      return azienda;
   }

   public void setAzienda(Azienda azienda)
   {
      this.azienda = azienda;
   }

   /** 
    * ad una composizione appartengono uno o piu edifici
    * un edificio puo appartenere  a più composizioni 
    * perche potrebbe esswere utile per vautazioni diverse
   **/ 
   @ManyToMany(fetch=FetchType.EAGER)
   public Set<Edificio> getEdifici()
   {
      return edifici;
   }

   public Boolean addEdificio(Edificio ed)
   {
	   if (this.edifici== null) this.edifici = new HashSet<Edificio>();
	   //ed.setComposizione(this);
	   return  this.getEdifici().add(ed);
   }
   public Boolean removeEdificio(Edificio ed)
   {
	   if (this.edifici== null) this.edifici = new HashSet<Edificio>();
	  
	   return  this.getEdifici().remove(ed);
   }
   public void setEdifici(Set<Edificio> edifici)
   {
      this.edifici = edifici;
   }

   // ad una composizione possono essere abbinati piu' profili di utilizzo
   @OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="composizioneEdificio")
   public Set<ProfiloUsoConsumo> getProfilo()
   {
      return profilo;
   }

   public void setProfilo(Set<ProfiloUsoConsumo> profilo)
   {
      this.profilo = profilo;
   }

   /**
    * @return the comuneUbicazione
    */
// uno a molti in notazione db il contrario in jpa
   // molti edifici possono stare nello  stesso  comune
   // ad un comune potrebbero fare riferimento piÃ¹ edifici
   @ManyToOne
   @NotNull
   public Comune getComuneUbicazione()
   {
      return comuneUbicazione;
   }

   /**
    * @param comuneUbicazione the comuneUbicazione to set
    */
   public void setComuneUbicazione(Comune comuneUbicazione)
   {
      this.comuneUbicazione = comuneUbicazione;
      
   }

  
   private Long id;

   String name;

   String noteDellaComposizione;

   TipologiaEdifici tipo;
   
   @Enumerated(EnumType.STRING)
   @NotNull
   public TipologiaEdifici getTipo() {
	return tipo;
}

public void setTipo(TipologiaEdifici tipo) {
	this.tipo = tipo;
}


Azienda azienda;

  
   Set<Edificio> edifici;

  
   Set<ProfiloUsoConsumo> profilo;

   
   private Comune comuneUbicazione;

   
   private String provinciaUbicazione;
   
   
   
   
   public String getProvinciaUbicazione() {
	   
	   return provinciaUbicazione;
}

public void setProvinciaUbicazione(String provinciaUbicazione) {
	/*
	if (comuneUbicazione!=null)
		{
		  if (comuneUbicazione.getProvincia().equals(provinciaUbicazione))
			  this.provinciaUbicazione = provinciaUbicazione;
		  else this.provinciaUbicazione=comuneUbicazione.getProvincia();
		}else this.provinciaUbicazione=provinciaUbicazione;
		
		*/
	this.provinciaUbicazione=provinciaUbicazione;
}



/**
 * se trovo un bean con stesso id ma diverso contenuto prima lo rimuovo 
 * e poi aggiungo al set. Se ha tutti i parametri uguali tranne l'id non lo fa aggiungere
 * @param prof
 * @return
 */
public boolean addProfilo(ProfiloUsoConsumo prof)
   {
	 if (this.profilo== null) this.profilo = new HashSet<ProfiloUsoConsumo>();
	   //ed.setComposizione(this);
	   ProfiloUsoConsumo ret=null;
	 for(ProfiloUsoConsumo profile: this.profilo)
	 {
		 if (profile.getId()== prof.getId())
			 {
			 ret=profile;
			 break;
			 }
			 
	 }
	 if( ret!=null ) {
		      this.profilo.remove(ret) ;
		      this.profilo.remove(prof) ;
	 }
	   return  this.profilo.add(prof);
   }

public boolean removeProfilo(ProfiloUsoConsumo prof)
{
	 if (this.profilo== null) this.profilo = new HashSet<ProfiloUsoConsumo>();
	   //ed.setComposizione(this);
	  ProfiloUsoConsumo ret=null;
		 for(ProfiloUsoConsumo profile: this.profilo)
		 {
			 if (profile.getId()== prof.getId())
				 {
				 ret=profile;
				 break;
				 }
				 
		 }
		 if( ret!=null ) {
			                this.profilo.remove(ret) ;
		                  }
		 
	   return  this.profilo.remove(prof);
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

