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

import it.enea.lecop.eca.model.ParamIntervento;
//import it.enea.lecop.eca.model.ParamIntervento.TipoPar;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaParIntervento;
import it.enea.lecop.eca.model.TipologiaValutazione;


/**
 * Backing bean for ParamIntervento entities.
 * <p>
 * This class provides CRUD functionality for all ParamIntervento entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ParamInterventoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving ParamIntervento entities
    */

   private String id;

   public String getId()
   {
      return this.id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   private ParamIntervento paramIntervento;

   public ParamIntervento getParamIntervento()
   {
      return this.paramIntervento;
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
         this.paramIntervento = this.search;
      }
      else
      {
         this.paramIntervento = this.entityManager.find(ParamIntervento.class, getId());
      }
   }

   /*
    * Support updating and deleting ParamIntervento entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.paramIntervento);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.paramIntervento);
            return "view?faces-redirect=true&id=" + this.paramIntervento.getNome();
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
         this.entityManager.remove(this.entityManager.find(ParamIntervento.class, getId()));
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
    * Support searching ParamIntervento entities with pagination
    */

   private int page;
   private long count;
   private List<ParamIntervento> pageItems;

   private ParamIntervento search = new ParamIntervento();

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
      return 15;
   }

   public ParamIntervento getSearch()
   {
      return this.search;
   }

   public void setSearch(ParamIntervento search)
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
      Root<ParamIntervento> root = countCriteria.from(ParamIntervento.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<ParamIntervento> criteria = builder.createQuery(ParamIntervento.class);
      root = criteria.from(ParamIntervento.class);
      TypedQuery<ParamIntervento> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<ParamIntervento> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.search.getNome();
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(root.<String> get("nome"), '%' + nome + '%'));
      }
      
      String descrizione = this.search.getDescrizione();
      if (descrizione != null && !"".equals(descrizione))
      {
         predicatesList.add(builder.like(root.<String> get("descrizione"), '%' + descrizione + '%'));
      }
      
      
      String unitaMisura = this.search.getUnitaMisura();
      if (unitaMisura != null && !"".equals(unitaMisura))
      {
         predicatesList.add(builder.like(root.<String> get("unitaMisura"), '%' + unitaMisura + '%'));
      }
      
      String jpqlString = this.search.getJpqlString();
      if (jpqlString != null && !"".equals(jpqlString))
      {
         predicatesList.add(builder.like(root.<String> get("jpqlString"), '%' + jpqlString + '%'));
      }
      
      String hsqlString = this.search.getHsqlString();
      if (hsqlString != null && !"".equals(hsqlString))
      {
         predicatesList.add(builder.like(root.<String> get("hsqlString"), '%' + hsqlString + '%'));
      }
      
      
      TipologiaParIntervento tipo = this.search.getTipoParametro();
      if (tipo != null)
      {
         predicatesList.add(builder.equal(root.get("tipoParametro"), tipo));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<ParamIntervento> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back ParamIntervento entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<ParamIntervento> getAll()
   {
	   System.out.println("chiamo getAll: ");
       System.out.flush();
      CriteriaQuery<ParamIntervento> criteria = this.entityManager.getCriteriaBuilder().createQuery(ParamIntervento.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(ParamIntervento.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {
              System.out.println("valore da convertire "+value);
              System.out.flush();
            return ParamInterventoBean.this.entityManager.find(ParamIntervento.class, value);
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((ParamIntervento) value).getNome());
         }
      };
   }
   
   public Converter getConverterTipo()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {
          //    TipoPar tipo;
           System.out.println("value passato per il tipopar : "+value);
           System.out.flush();
           
           int max =   TipologiaParIntervento.values().length;
           int index= Integer.parseInt(value);
           if ((index>-1) && (index<max))
           {
        	   return TipologiaParIntervento.values()[index] ;
           }else {
        	   return null;
           }
           
             
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {
        	 TipologiaParIntervento tiporet;
        	 System.out.println("classe passata "+value.getClass().getName());
        	 System.out.flush();
        	 String val;
        	
        	 
            if (value == null)
            {
               return "";
            }
           
              
            val=(String)value;
            System.out.println("valore passato "+val);
       	 System.out.flush();
            
            return val;
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private ParamIntervento add = new ParamIntervento();

   public ParamIntervento getAdd()
   {
      return this.add;
   }

   public ParamIntervento getAdded()
   {
      ParamIntervento added = this.add;
      this.add = new ParamIntervento();
      return added;
   }
   public List<TipologiaParIntervento> getTipologiaParInterventoAll()
   {
	   List<TipologiaParIntervento> tpe= new ArrayList<TipologiaParIntervento>();
	   for(TipologiaParIntervento tp: TipologiaParIntervento.values())
	   {
		   tpe.add(tp);
	   }
	   return tpe;
   }
   
}