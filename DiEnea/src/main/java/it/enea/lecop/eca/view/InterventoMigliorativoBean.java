package it.enea.lecop.eca.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.enea.lecop.eca.core.CalcolaIntervento;
import it.enea.lecop.eca.model.InterventoMigliorativo;
import it.enea.lecop.eca.model.ParamIntervento;

import it.enea.lecop.eca.model.TipologiaFunzioneDiValutazione;
import it.enea.lecop.eca.model.TipologiaValutazione;

import org.richfaces.component.UIDataTable;
/**
 * Backing bean for InterventoMigliorativo entities.
 * <p>
 * This class provides CRUD functionality for all InterventoMigliorativo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class InterventoMigliorativoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving InterventoMigliorativo entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }
   
   private UIDataTable tablePar;
   
   
   public UIDataTable getTablePar() {
	return tablePar;
}

public void setTablePar(UIDataTable tablePar) {
	this.tablePar = tablePar;
	
}

public void setId(Long id)
   {
      this.id = id;
   }

   private InterventoMigliorativo interventoMigliorativo;

   public InterventoMigliorativo getInterventoMigliorativo()
   {
      return this.interventoMigliorativo;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Inject
   private CalcolaIntervento intervento;
   
   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   /**
    * ci dice se il campo calcolo non validato, e' editabile oppure no
    */
   private boolean calcoloEdit;
   
   
   public boolean isCalcoloEdit() {
	return calcoloEdit;
}

public void setCalcoloEdit(boolean calcoloEdit) {
	this.calcoloEdit = calcoloEdit;
}

public void retrieve()
   {

      if (FacesContext.getCurrentInstance().isPostback())
      {
         return;
      }

      if (this.conversation.isTransient())
      {
         this.conversation.begin();
      }

      if (this.id == null)
      {
         this.interventoMigliorativo = this.search;
      }
      else
      {
         this.interventoMigliorativo = this.entityManager.find(InterventoMigliorativo.class, getId());
      }
   }

   /*
    * Support updating and deleting InterventoMigliorativo entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 System.out.println("persist quindi insert");
            this.entityManager.persist(this.interventoMigliorativo);
            return "search?faces-redirect=true";
         }
         else
         {
        	 System.out.println("merge quindi update");
            this.entityManager.merge(this.interventoMigliorativo);
            return "view?faces-redirect=true&id=" + this.interventoMigliorativo.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         System.out.println("Causa :"+e.getCause());
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(this.entityManager.find(InterventoMigliorativo.class, getId()));
         this.entityManager.flush();
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching InterventoMigliorativo entities with pagination
    */

   private int page;
   private long count;
   private List<InterventoMigliorativo> pageItems;

   private InterventoMigliorativo search = new InterventoMigliorativo();

   public int getPage()
   {
      return this.page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return 10;
   }

   public InterventoMigliorativo getSearch()
   {
      return this.search;
   }

   public void setSearch(InterventoMigliorativo search)
   {
      this.search = search;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<InterventoMigliorativo> root = countCriteria.from(InterventoMigliorativo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<InterventoMigliorativo> criteria = builder.createQuery(InterventoMigliorativo.class);
      root = criteria.from(InterventoMigliorativo.class);
      TypedQuery<InterventoMigliorativo> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<InterventoMigliorativo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String descrizione = this.search.getDescrizione();
      if (descrizione != null && !"".equals(descrizione))
      {
         predicatesList.add(builder.like(root.<String> get("descrizione"), '%' + descrizione + '%'));
      }
      TipologiaValutazione tipo = this.search.getApplicaTipoValutazione();
      if (tipo != null)
      {
         predicatesList.add(builder.equal(root.get("applicaTipoValutazione"), tipo));
      }
      String calcolo = this.search.getCalcolo();
      if (calcolo != null && !"".equals(calcolo))
      {
         predicatesList.add(builder.like(root.<String> get("calcolo"), '%' + calcolo + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<InterventoMigliorativo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back InterventoMigliorativo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<InterventoMigliorativo> getAll()
   {

      CriteriaQuery<InterventoMigliorativo> criteria = this.entityManager.getCriteriaBuilder().createQuery(InterventoMigliorativo.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(InterventoMigliorativo.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return InterventoMigliorativoBean.this.entityManager.find(InterventoMigliorativo.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((InterventoMigliorativo) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private InterventoMigliorativo add = new InterventoMigliorativo();

   public InterventoMigliorativo getAdd()
   {
      return this.add;
   }

   public InterventoMigliorativo getAdded()
   {
      InterventoMigliorativo added = this.add;
      this.add = new InterventoMigliorativo();
      return added;
   }
   
   public boolean checkCalcolo()
   {
	  String exprCalcolo= this.interventoMigliorativo.getCalcolo();
	  if ((exprCalcolo== null)|| exprCalcolo.trim().equals(""))
	  { 
		  System.out.println("il valore dell'espressione e' vuoto");
		 System.out.flush();
		  return false;
	  }
	  
	  if ((this.interventoMigliorativo.getCalcChecked()!= null) && (! this.interventoMigliorativo.getCalcChecked().trim().equals("")) && exprCalcolo.equals(this.interventoMigliorativo.getCalcChecked()))
	  {
		  System.out.println("l'epressione e' corretta e non è cambiata: se vuoi puoi rimappare i parametri");
			 System.out.flush();
		  return true;
	  }
	  
		  
		Set<String> par= intervento.setExpr(this.interventoMigliorativo, this.interventoMigliorativo.getId());
		  
		   if (par==null) return false;
		  // per logging
		   for(String p: par)  
			  {
			   System.out.println(p);
			   System.out.flush();
			  }
		   
		 if( par.equals(this.interventoMigliorativo.getParametri().keySet()))
		 {
			 // non devo fare nulla
			// devo avvertire che puo  rimappare 
			 System.out.println("l'epressione e' corretta ed ha gli stessi parametri: se vuoi puoi rimapparli");
			 System.out.flush();
			
			 ViewUtils.sendFacesMessage("interventoMigliorativoBeanInterventoMigliorativoCalcolo", "l'epressione e' corretta ed ha gli stessi parametri: se vuoi puoi rimapparli");
		 }
		 else
		 {
			 // devo avvertire che deve rimappare 
			 System.out.println("l'epressione e' corretta ma devi rimappare i parametri");
			 System.out.flush();
			 ViewUtils.sendFacesMessage("interventoMigliorativoBeanInterventoMigliorativoCalcolo", "l'epressione e' corretta ma devi rimappare i parametri");
			
		 }
		  return true;
	  
	   
   }
   
   /**
    * ci dice se è simbolicamente corretta
    * @param expr
    * @return
    */
   public Boolean checkSimbolicExpr(String expr)
   {
	   return intervento.checkSimbolicExpr(expr);
   }
   
   public Collection<Entry<String,ParamIntervento>> getAllPar()
   {
	   return getInterventoMigliorativo().getParametri().entrySet();
   }
   
   /**
    * recupera i parametri direttamente dall'espressione
    * L'espresiione deve essere simbolicamente valida
    * altrimenti ritorna null
    * @return collezione dei nomi dei parametri 
    */
   public Collection<String> retParFromNewExpr()
   {
	   return intervento.getParFromExpr(this.interventoMigliorativo.getCalcolo(),TipologiaFunzioneDiValutazione.FUNZ_MAT);
   }
   
   
   public ParamInterventoON daoParamIntervento(String parInExpr)
   {
	   ParamInterventoON parON=  new ParamInterventoON(parInExpr, interventoMigliorativo.getParametri());
	   //parON.getPar();
	   return parON;
   }
   
}