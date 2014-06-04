package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * identifica l'intervento che si puo adottare a fronte di una analisi preventiva
 * @author fab
 *
 */

@Entity
//@DiscriminatorValue("I")
@NamedQueries({
	@NamedQuery(name="InterventoMigliorativo.getForTipoEdificio",
			query="SELECT fv FROM InterventoMigliorativo fv " +
					"WHERE ( :tipoedificio IN ELEMENTS ( fv.applicaTipoEdifici ))"),
	@NamedQuery(name="InterventoMigliorativo.getForTipoValutazione",
					query="SELECT fv FROM InterventoMigliorativo fv " +
							"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )"),
	@NamedQuery(name="InterventoMigliorativo.getForTipoValutazione_Edificio",
	query="SELECT fv FROM InterventoMigliorativo fv " +
			"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )" +
			"AND   ( :tipoedificio IN ELEMENTS( fv.applicaTipoEdifici ))"),
	@NamedQuery(name="InterventoMigliorativo.findAll",
            query="SELECT u FROM InterventoMigliorativo u"),
     @NamedQuery(name="InterventoMigliorativo.findById",
            query="SELECT u FROM InterventoMigliorativo u WHERE u.id = :id"),
    @NamedQuery(name="InterventoMigliorativo.getForTipoValutazione_ID_NAME",
			query="SELECT fv.id , fv.name , fv.descrizione FROM InterventoMigliorativo fv " +
					"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )")      
})


public class InterventoMigliorativo extends FunzioneDiValutazione implements Serializable, Securable
{
   


   public static void main(String[] args)
   {

      InterventoMigliorativo inter = new InterventoMigliorativo();
   }
   
   /**
    * aggiunge parametro param di nome  mapNomeParInExpr
    * @param mapNomeParInExpr
    * @param param
    */
   public synchronized void  addParametro(String mapNomeParInExpr,ParamIntervento param) {
	   
	   
		  
	  
	  
	   
	   if (getParametri().containsKey(mapNomeParInExpr))
	   {
		   ParamIntervento oldPar=   getParametri().get(mapNomeParInExpr);
		   oldPar.getInterventi().remove(this);
	   }   
		   param.getInterventi().add(this);
		   getParametri().put(mapNomeParInExpr, param);
	  
	   
    
    }

   
   private Set<Valutazione>  valutazioniMiglioramento;
   
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

	@ManyToMany   
	public Set<Valutazione> getValutazioniMiglioramento() {
		return valutazioniMiglioramento;
	}

	public void setValutazioniMiglioramento(Set<Valutazione> valutazioniMiglioramento) {
		this.valutazioniMiglioramento = valutazioniMiglioramento;
	}
   
}