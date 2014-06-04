package it.enea.lecop.eca.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

import it.enea.lecop.eca.model.CalcoloEconomico;
import it.enea.lecop.eca.model.RisultatoValutazioneIntervento;

import it.enea.lecop.eca.model.TipologiaValutazione;

/**
 * Backing bean for CalcoloEconomico entities.
 * <p>
 * This class provides CRUD functionality for all CalcoloEconomico entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class CalcoloEconomicoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving CalcoloEconomico entities
    */

   private Long id;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private CalcoloEconomico calcoloEconomico;

   public CalcoloEconomico getCalcoloEconomico()
   {
      return this.calcoloEconomico;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {

      this.conversation.begin();
      return "create?faces-redirect=true";
   }

   
   /**
    * recupera bean
    */
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
         this.calcoloEconomico = this.search;
      }
      else
      {
         this.calcoloEconomico = this.entityManager.find(CalcoloEconomico.class, getId());
      }
   }

   /*
    * Support updating and deleting CalcoloEconomico entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 // log
        	 System.out.println("persist quindi insert");
            this.entityManager.persist(this.calcoloEconomico);
            return "search?faces-redirect=true";
         }
         else
         {
        	 // log
        	 System.out.println("merge quindi update");
            this.entityManager.merge(this.calcoloEconomico);
            return "view?faces-redirect=true&id=" + this.calcoloEconomico.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         // log
         System.out.println("Causa :"+e.getCause());
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(this.entityManager.find(CalcoloEconomico.class, getId()));
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
   private List<CalcoloEconomico> pageItems;

   private CalcoloEconomico search = new CalcoloEconomico();

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

   public CalcoloEconomico getSearch()
   {
      return this.search;
   }

   public void setSearch(CalcoloEconomico search)
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

      //conta i bean
      
      
      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<CalcoloEconomico> root = countCriteria.from(CalcoloEconomico.class);
      //                                                        clausola where 
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<CalcoloEconomico> criteria = builder.createQuery(CalcoloEconomico.class);
      root = criteria.from(CalcoloEconomico.class);
      TypedQuery<CalcoloEconomico> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<CalcoloEconomico> root)
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
      
      
      RisultatoValutazioneIntervento dependsOn = this.search.getDependsOn();
      if (dependsOn != null )
      {
         predicatesList.add(builder.equal(root.get("dependsOn"), dependsOn));
      }
     
      for (Predicate pre: predicatesList)
      {
    	 // debug 
    	 // System.out.println("CalcoloEconomico ->predicato where : "+pre.toString());
    	 // System.out.flush();
      }
      
      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<CalcoloEconomico> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back CalcoloEconomico entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<CalcoloEconomico> getAll()
   {

      CriteriaQuery<CalcoloEconomico> criteria = this.entityManager.getCriteriaBuilder().createQuery(CalcoloEconomico.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(CalcoloEconomico.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return CalcoloEconomicoBean.this.entityManager.find(CalcoloEconomico.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((CalcoloEconomico) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private CalcoloEconomico add = new CalcoloEconomico();

   public CalcoloEconomico getAdd()
   {
      return this.add;
   }

   public CalcoloEconomico getAdded()
   {
      CalcoloEconomico added = this.add;
      this.add = new CalcoloEconomico();
      return added;
   }
}