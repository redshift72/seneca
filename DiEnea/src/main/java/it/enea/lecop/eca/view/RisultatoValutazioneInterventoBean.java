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

import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.RisultatoValutazioneIntervento;
import it.enea.lecop.eca.model.InterventoMigliorativo;
import it.enea.lecop.eca.model.Valutazione;

/**
 * Backing bean for RisultatoValutazioneIntervento entities.
 * <p>
 * This class provides CRUD functionality for all RisultatoValutazioneIntervento entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class RisultatoValutazioneInterventoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving RisultatoValutazioneIntervento entities
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

   private RisultatoValutazioneIntervento risultatoValutazioneIntervento;

   public RisultatoValutazioneIntervento getRisultatoValutazioneIntervento()
   {
      return this.risultatoValutazioneIntervento;
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
         this.risultatoValutazioneIntervento = this.search;
      }
      else
      {
         this.risultatoValutazioneIntervento = this.entityManager.find(RisultatoValutazioneIntervento.class, getId());
      }
   }

   /*
    * Support updating and deleting RisultatoValutazioneIntervento entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.risultatoValutazioneIntervento);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.risultatoValutazioneIntervento);
            return "view?faces-redirect=true&id=" + this.risultatoValutazioneIntervento.getId();
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
         this.entityManager.remove(this.entityManager.find(RisultatoValutazioneIntervento.class, getId()));
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
    * Support searching RisultatoValutazioneIntervento entities with pagination
    */

   private int page;
   private long count;
   private List<RisultatoValutazioneIntervento> pageItems;

   private RisultatoValutazioneIntervento search = new RisultatoValutazioneIntervento();

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

   public RisultatoValutazioneIntervento getSearch()
   {
      return this.search;
   }

   public void setSearch(RisultatoValutazioneIntervento search)
   {
      this.search = search;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {
       
	  try
	  {
      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<RisultatoValutazioneIntervento> root = countCriteria.from(RisultatoValutazioneIntervento.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<RisultatoValutazioneIntervento> criteria = builder.createQuery(RisultatoValutazioneIntervento.class);
      root = criteria.from(RisultatoValutazioneIntervento.class);
      TypedQuery<RisultatoValutazioneIntervento> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
	  }catch (Exception ex) {
		
	}
   }

   private Predicate[] getSearchPredicates(Root<RisultatoValutazioneIntervento> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      Valutazione valutazione = this.search.getValutazione();
      if (valutazione != null)
      {
         predicatesList.add(builder.equal(root.get("valutazione"), valutazione));
      }
      FunzioneDiValutazione intervento = this.search.getIntervento();
      if (intervento != null)
      {
         predicatesList.add(builder.equal(root.get("intervento"), intervento));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<RisultatoValutazioneIntervento> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back RisultatoValutazioneIntervento entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<RisultatoValutazioneIntervento> getAll()
   {

      CriteriaQuery<RisultatoValutazioneIntervento> criteria = this.entityManager.getCriteriaBuilder().createQuery(RisultatoValutazioneIntervento.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(RisultatoValutazioneIntervento.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return RisultatoValutazioneInterventoBean.this.entityManager.find(RisultatoValutazioneIntervento.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((RisultatoValutazioneIntervento) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private RisultatoValutazioneIntervento add = new RisultatoValutazioneIntervento();

   public RisultatoValutazioneIntervento getAdd()
   {
      return this.add;
   }

   public RisultatoValutazioneIntervento getAdded()
   {
      RisultatoValutazioneIntervento added = this.add;
      this.add = new RisultatoValutazioneIntervento();
      return added;
   }
}