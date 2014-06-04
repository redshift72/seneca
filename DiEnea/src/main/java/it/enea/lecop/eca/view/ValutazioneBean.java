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

import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.model.Azienda;

import it.enea.lecop.eca.model.User;

/**
 * Backing bean for Valutazione entities.
 * <p>
 * This class provides CRUD functionality for all Valutazione entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ValutazioneBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Valutazione entities
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

   private Valutazione valutazione;

   public Valutazione getValutazione()
   {
      return this.valutazione;
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
         this.valutazione = this.search;
      }
      else
      {
         this.valutazione = this.entityManager.find(Valutazione.class, getId());
      }
   }

   /*
    * Support updating and deleting Valutazione entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.valutazione);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.valutazione);
            return "view?faces-redirect=true&id=" + this.valutazione.getId();
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   public String delete()
   {
      this.conversation.end();

      try
      {
         this.entityManager.remove(this.entityManager.find(Valutazione.class, getId()));
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
    * Support searching Valutazione entities with pagination
    */

   private int page;
   private long count;
   private List<Valutazione> pageItems;

   private Valutazione search = new Valutazione();

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

   public Valutazione getSearch()
   {
      return this.search;
   }

   public void setSearch(Valutazione search)
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
      Root<Valutazione> root = countCriteria.from(Valutazione.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Valutazione> criteria = builder.createQuery(Valutazione.class);
      root = criteria.from(Valutazione.class);
      TypedQuery<Valutazione> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Valutazione> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      User owner = this.search.getOwnerid().getOwnUser();
      if (owner != null)
      {
         predicatesList.add(builder.equal(root.get("owner"), owner));
      }
      Azienda azienda = this.search.getAzienda();
      if (azienda != null)
      {
         predicatesList.add(builder.equal(root.get("azienda"), azienda));
      }
      TipologiaValutazione tipo = this.search.getTipo();
      if (tipo != null)
      {
         predicatesList.add(builder.equal(root.get("tipo"), tipo));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Valutazione> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Valutazione entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Valutazione> getAll()
   {

      CriteriaQuery<Valutazione> criteria = this.entityManager.getCriteriaBuilder().createQuery(Valutazione.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(Valutazione.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return ValutazioneBean.this.entityManager.find(Valutazione.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Valutazione) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Valutazione add = new Valutazione();

   public Valutazione getAdd()
   {
      return this.add;
   }

   public Valutazione getAdded()
   {
      Valutazione added = this.add;
      this.add = new Valutazione();
      return added;
   }
}