package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@NamedQueries({
	@NamedQuery(name="Azienda.deleteAllOwnerUser",query="DELETE FROM Azienda u" +
		" WHERE (u.ownerid.ownUser.username = :username)"),
	@NamedQuery(name="Azienda.findAllOwnerUser",query="SELECT u FROM Azienda u" +
			" WHERE (u.ownerid.ownUser.username = :username)"),
    @NamedQuery(name="Azienda.findAll",
                query="SELECT u FROM Azienda u"),
    @NamedQuery(name="Azienda.findByNome",
                query="SELECT u FROM Azienda u WHERE u.nome = :nome"),
    @NamedQuery(name="Azienda.findAllOWNERUSER",
         query="SELECT u FROM Azienda u WHERE (u.ownerid.ownUser.username = :username) and (" +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
   		       "(u.permissionprop.OWNERUSER = :ownuserSec4))"),
    @NamedQuery(name="Azienda.findAllOWNDOMAIN",
            query="SELECT u FROM Azienda u WHERE (u.ownerid.ownCompany.name = :domainname) and (" +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
      		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4))"),
      		       
    @NamedQuery(name="Azienda.findAllNO_OWNERUSEROWNDOMAIN",
                 query="SELECT u FROM Azienda u WHERE (u.ownerid.ownCompany.name <> :domainname) " +
                 		"and (u.ownerid.ownUser.username <> :username)"),
     @NamedQuery(name="Azienda.findAllOWNERUSER_OWNDOMAIN",
                         query="SELECT u FROM Azienda u WHERE (:username = 'admin') or((u.ownerid.ownUser.username = :username) and (" +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                   		       " or (" +
                   		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))"),
              @NamedQuery( name="Azienda.selectAllSec", 
                      query="SELECT u FROM Azienda u WHERE (:username = 'admin') " +
                      		   "or (" +
                      		   "(u.ownerid.ownUser.username = :username) and (" +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                   		       " or (" +
                   		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))"   +    
                   		        "or (" +
                   		        "( SIZE(u.ownerid.ownCompany.aziende) = (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az IN (" +
                   		        "SELECT azi  FROM CompanyDomain cd,IN (cd.aziende) azi WHERE cd.name = :domainname ))) " +
                   		             "and (u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1 , :ownsubsetSec2 , :ownsubsetSec3 , :ownsubsetSec4 )))" +
                   		        "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4)) ")
                //  		    +   "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4))") 
               /*    		    +   " or (( SIZE(u.ownerid.ownCompany.aziende) = (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az MEMBER OF :aziendeLogin)) " +
                   		       "and (" +
                   		       "u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1,:ownsubsetSec2,:ownsubsetSec3,:ownsubsetSec4)))" +
                   		       "or  (( SIZE(u.ownerid.ownCompany.aziende) < (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az MEMBER OF :aziendeLogin)) " +
                   		       "and (" +
                   		       "u.permissionprop.INTERSECTIONDOMAIN IN (:ownintersecSec1,:ownintersecSec2,:ownintersecSec3,:ownintersecSec4)))")             		
*/
}) 
public class Azienda  implements Serializable, Securable 
{

	
	
   
	 public Azienda() {
		
	}

	public Azienda(Set<CompanyDomain> domainsOfMembership, String nome,
			String descrizione, String tipo, String cFoPI, String city,
			String via, String nazione, Integer cap, String nomeReferente,
			String cognomeReferente, String email, String tel, String telCell,
			String fax, OwnerId ownerid, PermissionProp permissionprop) {
		
		this.domainsOfAziendaMembership = domainsOfMembership;
		this.nome = nome;
		this.descrizione = descrizione;
		this.tipo = tipo;
		CFoPI = cFoPI;
		this.city = city;
		this.via = via;
		this.nazione = nazione;
		this.cap = cap;
		this.nomeReferente = nomeReferente;
		this.cognomeReferente = cognomeReferente;
		this.email = email;
		this.tel = tel;
		this.telCell = telCell;
		this.fax = fax;
		this.ownerid = ownerid;
		this.permissionprop = permissionprop;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


/*
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Azienda other = (Azienda) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
*/

	/**
	  * domini di sicurezza di appartenenza
	  */
	
	private Set<CompanyDomain> domainsOfAziendaMembership= new HashSet<CompanyDomain>();
	 
	@ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH})
 public Set<CompanyDomain> getDomainsOfAziendaMembership() {
		return domainsOfAziendaMembership;
	}

	public void setDomainsOfAziendaMembership(
			Set<CompanyDomain> domainsOfAziendaMembership) {
		this.domainsOfAziendaMembership = domainsOfAziendaMembership;
	}

@Id	
   public String getNome()
   {
      return nome;
   }

   public void setNome(String nome)
   {
      this.nome = nome;
   }

   public String getTipo()
   {
      return tipo;
   }

   public void setTipo(String tipo)
   {
      this.tipo = tipo;
   }

   
   public String getCFoPI()
   {
      return CFoPI;
   }

   public void setCFoPI(String cFoPI)
   {
      CFoPI = cFoPI;
   }

  
   private String nome;

   private String descrizione;

   public String getDescrizione()
   {
      return descrizione;
   }

   public void setDescrizione(String descrizione)
   {
      this.descrizione = descrizione;
   }
   

  
   

  

   

   // tipo di azienda o ente o privato
   private String tipo;

   /**
    * codice fiscale o partita iva dell'azianda o ente o privato
    */
   
  
   private String CFoPI;

   

  

   
   public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public String getNazione() {
		return nazione;
	}
	public void setNazione(String nazione) {
		this.nazione = nazione;
	}
	public Integer getCap() {
		return cap;
	}
	public void setCap(Integer cap) {
		this.cap = cap;
	}
	private String city;
	private String via;
	private String nazione;
	private Integer cap;
	
	
	private String nomeReferente;
	private String cognomeReferente;
	
	/**
	 * @return the nomeReferente
	 */
	public String getNomeReferente() {
		return nomeReferente;
	}

	/**
	 * @param nomeReferente the nomeReferente to set
	 */
	public void setNomeReferente(String nomeReferente) {
		this.nomeReferente = nomeReferente;
	}

	/**
	 * @return the cognomeReferente
	 */
	public String getCognomeReferente() {
		return cognomeReferente;
	}

	/**
	 * @param cognomeReferente the cognomeReferente to set
	 */
	public void setCognomeReferente(String cognomeReferente) {
		this.cognomeReferente = cognomeReferente;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the tel
	 */
	public String getTel() {
		return tel;
	}

	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * @return the telCell
	 */
	public String getTelCell() {
		return telCell;
	}

	/**
	 * @param telCell the telCell to set
	 */
	public void setTelCell(String telCell) {
		this.telCell = telCell;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	private String email;
	private String tel;
	private String telCell;
	private String fax;

	
	
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

	
	
	public boolean addInCompanyDomain(CompanyDomain cd)
	{
		if (domainsOfAziendaMembership==null)
		{
			setDomainsOfAziendaMembership(new HashSet<CompanyDomain>());
			
		}
		      
		return domainsOfAziendaMembership.add(cd);
	}
	
	public boolean removeCompanyDomain(CompanyDomain cd)
	{
		if (domainsOfAziendaMembership==null)
		{
			setDomainsOfAziendaMembership(new HashSet<CompanyDomain>());
			
		}
		       
		//return domainsOfAziendaMembership.remove(cd.removeAzienda(this));
		return domainsOfAziendaMembership.remove(cd);
	}

	

}
