package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Set;


import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@NamedQueries({
	@NamedQuery(name="Edificio.findAllAzienda",query="SELECT u FROM Edificio u" +
		" WHERE (u.azienda.nome = :azname)"),
	@NamedQuery(name="Edificio.deleteAllOwnerUser",query="DELETE FROM Edificio u" +
			" WHERE (u.ownerid.ownUser.username = :username)"),
	@NamedQuery(name="Edificio.findAllOwnerUser",query="SELECT u FROM Edificio u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
    @NamedQuery(name="Edificio.findAll",
                query="SELECT u FROM Edificio u"),
    @NamedQuery(name="Edificio.findById",
                query="SELECT u FROM Edificio u WHERE u.id = :id"),
    @NamedQuery(name="Edificio.findAllOWNERUSER",
         query="SELECT u FROM Edificio u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"), 
    @NamedQuery(name="Edificio.findAllOWNDOMAIN",
            query="SELECT u FROM Edificio u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
    @NamedQuery(name="Edificio.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM Edificio u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
    @NamedQuery(name="Edificio.findAllOWNERUSER_OWNDOMAIN",
                        query="SELECT u FROM Edificio u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
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
public class Edificio implements Serializable,  Securable
{

   private Azienda azienda;

/**
    * @return the nome
    */
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
    * @return the areaCalpestabile
    */
   @NotNull
   public Double getSuperficieLordaPiani()
   {
      return this.superficieLordaPiani;
   }

   /**
    * @param areaCalpestabile the areaCalpestabile to set
    */
   public void setSuperficieLordaPiani(Double superficieLordaPiani)
   {
      this.superficieLordaPiani = superficieLordaPiani;
   }

   /**
    * @return the areaDisperdente
    */
   @NotNull
   public Double getAreaDisperdente()
   {
      return areaDisperdente;
   }

   /**
    * @param areaDisperdente the areaDisperdente to set
    */
   public void setAreaDisperdente(Double areaDisperdente)
   {
      this.areaDisperdente = areaDisperdente;
   }

   /**
    * @return the volumeTotale
    */
   @NotNull
   public Double getVolumetriaLordaRiscaldata()
   {
      return volumetriaLordaRiscaldata;
   }

   /**
    * @param volumeTotale the volumeTotale to set
    */
   public void setVolumetriaLordaRiscaldata(Double volumeTotale)
   {
      this.volumetriaLordaRiscaldata = volumeTotale;
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
    * @return the consumoIdricoTot
    */
   public Double getConsumoIdricoTot()
   {
      return consumoIdricoTot;
   }

   /**
    * @param consumoIdricoTot the consumoIdricoTot to set
    */
   public void setConsumoIdricoTot(Double consumoIdricoTot)
   {
      this.consumoIdricoTot = consumoIdricoTot;
   }

   /**
    * @return the tipoEdificio
    */
   @Enumerated(EnumType.STRING)
   //@NotNull
   public TipologiaEdifici getTipologiaEdifici()
   {
      return tipologiaEdifici;
   }

   /**
    * @param tipoEdificio the tipoEdificio to set
    */
   public void setTipologiaEdifici(TipologiaEdifici tipologiaEdifici)
   {
      this.tipologiaEdifici = tipologiaEdifici;
   }

   // molte composizione potrebbero fa riferimento ad una azienda
   // ad una azienda potrebbero fa riferimento piÃ¹ composizioni
   // una azienda deve essere specificata
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
    * @return the composizione
    */
   
   @ManyToMany
   public Set<ComposizioneEdifici> getComposizione()
   {
      return composizione;
   }

   /**
    * @param composizione the composizione to set
    */
   public void setComposizione(Set<ComposizioneEdifici> composizione)
   {
      this.composizione = composizione;
   }

   /**
    * @return the esposizioneVetri
    */
   @Embedded
   public EsposizioneSuperficiVetrate getEsposizioneVetri()
   {
      return esposizioneVetri;
   }

   /**
    * @param esposizioneVetri the esposizioneVetri to set
    */
   public void setEsposizioneVetri(EsposizioneSuperficiVetrate esposizioneVetri)
   {
      this.esposizioneVetri = esposizioneVetri;
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

   public Edificio(String nome, Double superficieLordaPiani, Double areaDisperdente, Double volumetriaLordaRiscaldata, Double consumoElettricoTot, Double consumoTermicoTot, Double consumoIdricoTot, TipologiaEdifici tipologiaEdifici, Set<ComposizioneEdifici> composizione, EsposizioneSuperficiVetrate esposizioneVetri)
   {
     
      this.nome = nome;
      this.superficieLordaPiani = superficieLordaPiani;
      this.areaDisperdente = areaDisperdente;
      this.volumetriaLordaRiscaldata = volumetriaLordaRiscaldata;
      this.consumoElettricoTot = consumoElettricoTot;
      this.consumoTermicoTot = consumoTermicoTot;
      this.consumoIdricoTot = consumoIdricoTot;
      this.tipologiaEdifici = tipologiaEdifici;
      this.composizione = composizione;
      this.esposizioneVetri = esposizioneVetri;
   }

   public Edificio()
   {

   }

  

   private static final long serialVersionUID = 1L;
  
   
   private Long id;

   public void setId(Long id) {
	this.id = id;
}

private String nome;
   // in m^2
   private Double superficieLordaPiani;
   // in m^2
   private Double areaDisperdente;
   // in m^3
   private Double volumetriaLordaRiscaldata;
   
   
   private Double superficieUtile;
  
   
   
/**
 * recupoera superfice netta calpestabile o superfice Utile
 * @return
 */
   public Double getSuperficieUtile() {
	return superficieUtile;
}

   /**
    * imposta la superfice netta calpestabile o superfice Utile
    * @param superficieUtile
    */
public void setSuperficieUtile(Double superficieUtile) {
	this.superficieUtile = superficieUtile;
}



// media degli ultimi 3-5 anni in kWhe
   // se e' conosciuto il consumo di questo edificio singolo
   private Double consumoElettricoTot;

   // media degli ultimi 3-5 anni kWht
   // se e' conosciuto il consumo di questo edificio singolo
   private Double consumoTermicoTot;

   // media degli ultimi 3-5 anni in m^3
   // se Ã¨ conosciuto il consumo di questo edificio singolo
   private Double consumoIdricoTot;

   // molti edifici posso essere delle stesso tipo
   // un tipo puo far riferimento a piÃ¹ edifici
   
   private TipologiaEdifici tipologiaEdifici;

   // molti edifici posso stare nella stessa composizione
   // una composizione puo avere piÃ¹ edifici
   private Set<ComposizioneEdifici> composizione;

   /**
    * superfici vetrate per esposizione in m^2:  nord - nordest- est -sudest-sud- sudovest-ovest-nordovest
    * 
    * non mandatario
    */
   
   private EsposizioneSuperficiVetrate esposizioneVetri;

   private TipologiaCoperture copertura;
   
   @Enumerated(EnumType.STRING)
   public TipologiaCoperture getCopertura() {
	return copertura;
}

public void setCopertura(TipologiaCoperture copertura) {
	this.copertura = copertura;
}



private Double altezza;
     
 /**
  * recupera l'altezza media dell'edificio
  */
public Double getAltezza() {
	return altezza;
}

/**
 * imposta l'altezza media dell'edificio
 * @param altezza
 */
public void setAltezza(Double altezza) {
	this.altezza = altezza;
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
