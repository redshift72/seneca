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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.enea.lecop.eca.model.Comune;

/**
 * Backing bean for Comune entities.
 * <p>
 * This class provides CRUD functionality for all Comune entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ComuneBean implements Serializable
{

   /**
	 * @param comuniPerProvincia the comuniPerProvincia to set
	 */
	public void setComuniPerProvincia(List<Comune> comuniPerProvincia) {
		this.comuniPerProvincia = comuniPerProvincia;
	}

private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Comune entities
    */

   
   private List<Comune> comuniPerProvincia;
   
   
   private Long id;

   public Long getId()
   {
      return this.id;
   }

   private List<String> province= new ArrayList<String>();
   
   
   private String provincia;
   
   /**
 * @return the provincia
 */
public String getProvincia() {
	return provincia;
}

@SuppressWarnings("unchecked")
public List<Comune> getComuniPerProvincia()
{
	
	System.out.println("get comuni per  Provicia :"+provincia);
    System.out.flush();
	List<Comune> comPrv=new ArrayList<Comune>();
	if (provincia==null)
	{
		
		System.out.println("provincia nulla");
		return comPrv;
	}
	
	String queryS="SELECT  c FROM Comune c WHERE c.provincia='"+provincia+"' ORDER BY descrizione";

	 Query query = this.entityManager.createQuery(queryS);
	 
	    comPrv= (List<Comune>) query.getResultList();

	    this.comuniPerProvincia=comPrv;
	    
	    return comPrv;
}

/**
 * @param provincia the provincia to set
 */
public void setProvincia(String provincia) 
{
	System.out.println("----->>set Provicia :"+provincia);
    System.out.flush();
	this.provincia = provincia;
	
}

/**
 * @return the province
 */
public List<String> getProvince() {
	
	
	 System.out.println("--->>> getall Province");
     System.out.flush();
    
    if (province==null || province.isEmpty())
    {
    	 String queryS="SELECT DISTINCT c.provincia FROM Comune c ORDER BY provincia";
    	    Query query = this.entityManager.createQuery(queryS);
    	    province= (List<String>) query.getResultList();
    }
    	
   
    
    
    
    System.out.println("--->>>fine getall province");
    System.out.flush();
   
	
	return province;
}

/**
 * @param province the province to set
 */
public void setProvince(List<String> province) {
	System.out.println("setProvince");
	System.out.flush();
	this.province = province;
}

public void setId(Long id)
   {
      this.id = id;
   }

   private Comune comune;
   
   private List<Comune> comuni;

   public Comune getComune()
   {
      return this.comune;
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
         this.comune = this.search;
      }
      else
      {
         this.comune = this.entityManager.find(Comune.class, getId());
      }
   }

   /*
    * Support updating and deleting Comune entities
    */

   public String update()
   {
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
            this.entityManager.persist(this.comune);
          if(this.comuni!= null)  this.comuni.add(this.comune);
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.comune);
            if(this.comuni!= null)
            {
            	int i=0;
                for (Comune com:  comuni)
            	{
            	   if (com.getId() == this.id)break;
            	   i++;
            	}
            
                this.comuni.set(i, this.comune);
            }
            return "view?faces-redirect=true&id=" + this.comune.getId();
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
         this.entityManager.remove(this.entityManager.find(Comune.class, getId()));
         this.entityManager.flush();
         if(this.comuni!= null)
         {
         int i=0;
         for (Comune com:  comuni)
         	{
         	   if (com.getId() == getId() ) break;
         	   i++;
         	}
         comuni.remove(i);
         }
         return "search?faces-redirect=true";
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return null;
      }
   }

   /*
    * Support searching Comune entities with pagination
    */

   private int page;
   private long count;
   private List<Comune> pageItems;

   private Comune search = new Comune();

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

   public Comune getSearch()
   {
      return this.search;
   }

   public void setSearch(Comune search)
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
      Root<Comune> root = countCriteria.from(Comune.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<Comune> criteria = builder.createQuery(Comune.class);
      root = criteria.from(Comune.class);
      TypedQuery<Comune> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Comune> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String descrizione = this.search.getDescrizione();
      if (descrizione != null && !"".equals(descrizione))
      {
         predicatesList.add(builder.like(root.<String> get("descrizione"), '%' + descrizione + '%'));
      }
      String provincia = this.search.getProvincia();
      if (provincia != null && !"".equals(provincia))
      {
         predicatesList.add(builder.like(root.<String> get("provincia"), '%' + provincia + '%'));
      }
      String zona = this.search.getZona();
      if (zona != null && !"".equals(zona))
      {
         predicatesList.add(builder.like(root.<String> get("zona"), '%' + zona + '%'));
      }
      String prov1 = this.search.getProv1();
      if (prov1 != null && !"".equals(prov1))
      {
         predicatesList.add(builder.like(root.<String> get("prov1"), '%' + prov1 + '%'));
      }
      String prov2 = this.search.getProv2();
      if (prov2 != null && !"".equals(prov2))
      {
         predicatesList.add(builder.like(root.<String> get("prov2"), '%' + prov2 + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Comune> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Comune entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Comune> getAll()
   {
	   System.out.println("getall comuni");
       System.out.flush();
      CriteriaQuery<Comune> criteria = this.entityManager.getCriteriaBuilder().createQuery(Comune.class);
      if (comuni==null) 
    	  {
    	  this.comuni=this.entityManager.createQuery(criteria.select(criteria.from(Comune.class))).getResultList();
    	 
    	  }
      
      System.out.println("--->>>fine getall comuni");
      System.out.flush();
      return this.comuni;
   }

   public Converter getConverter()
   {

      return new Converter()
      {
             
         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {
        	 System.out.println("--------->>>>>>>>>converter object");
        	 System.out.println(value);
        	 System.out.flush();
            return ComuneBean.this.entityManager.find(Comune.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {
        	 String out;
        	 System.out.println("------->>>>>>>>converter string");
        	 System.out.println("IN: "+value);
        	 
        	
            if (value == null)
            {
               return "";
            }
            out=String.valueOf(((Comune) value).getId());
            
            System.out.println("OUT: "+out);
       	    System.out.flush();
            return out;
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Comune add = new Comune();

   public Comune getAdd()
   {
      return this.add;
   }

   public Comune getAdded()
   {
      Comune added = this.add;
      this.add = new Comune();
      return added;
   }
}