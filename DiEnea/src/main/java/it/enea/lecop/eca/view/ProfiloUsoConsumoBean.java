package it.enea.lecop.eca.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
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

import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.SecAttrib;

import it.enea.lecop.eca.model.TipologiaValutazione;

/**
 * Backing bean for ProfiloUsoConsumo entities.
 * <p>
 * This class provides CRUD functionality for all ProfiloUsoConsumo entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ProfiloUsoConsumoBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving ProfiloUsoConsumo entities
    */

   private Long id;
   
   @Inject 
   private Instance<Login> loginUser;

   public Long getId()
   {
      return this.id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   private ProfiloUsoConsumo profiloUsoConsumo;

   public ProfiloUsoConsumo getProfiloUsoConsumo()
   {
      return this.profiloUsoConsumo;
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
         this.profiloUsoConsumo = this.search;
      }
      else
      {
         this.profiloUsoConsumo = this.entityManager.find(ProfiloUsoConsumo.class, getId());
      }
   }

   /*
    * Support updating and deleting ProfiloUsoConsumo entities
    */

   public String update()
   {
	   Login login=loginUser.get(); 
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 this.profiloUsoConsumo.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain())) ;
             PermissionProp pp=new PermissionProp();
             pp.setSUBSETDOMAIN(SecAttrib.CONTROL);
             pp.setIDENTITYDOMAIN(SecAttrib.CONTROL);
             pp.setINTERSECTIONDOMAIN(SecAttrib.READ);
             pp.setOTHER(SecAttrib.NONE);
             
          	this.profiloUsoConsumo.setPermissionprop(pp); 
        	 
          	          	 
              ComposizioneEdifici comp=this.entityManager.merge(this.profiloUsoConsumo.getComposizioneEdificio());
              this.profiloUsoConsumo.setComposizioneEdificio(comp);
          	this.entityManager.persist(this.profiloUsoConsumo);
          	/*
          	ComposizioneEdifici comp=this.profiloUsoConsumo.getComposizioneEdificio();
        	 if (comp!=null) {
        		 comp=this.entityManager.find(ComposizioneEdifici.class, comp.getId()); 
        		  comp.addProfilo(this.profiloUsoConsumo);
        		   this.entityManager.merge(comp);
        	  }
          	 */
            
            return "search?faces-redirect=true";
         }
         else
         {
        	// salvo dall'altro lato della relazione
        	 ComposizioneEdifici comp=this.profiloUsoConsumo.getComposizioneEdificio();
    	/*
        	 if (comp!=null) 
    	    {
    		 comp=this.entityManager.find(ComposizioneEdifici.class, comp.getId()); 
  		     comp.addProfilo(this.profiloUsoConsumo);
  		      this.entityManager.merge(comp);
  	        }
        	*/ 
            this.entityManager.merge(this.profiloUsoConsumo);
            return "view?faces-redirect=true&id=" + this.profiloUsoConsumo.getId();
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
         this.entityManager.remove(this.entityManager.find(ProfiloUsoConsumo.class, getId()));
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
    * Support searching ProfiloUsoConsumo entities with pagination
    */

   private int page;
   private long count;
   private List<ProfiloUsoConsumo> pageItems;

   private ProfiloUsoConsumo search = new ProfiloUsoConsumo();

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

   public ProfiloUsoConsumo getSearch()
   {
      return this.search;
   }

   public void setSearch(ProfiloUsoConsumo search)
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
      Root<ProfiloUsoConsumo> root = countCriteria.from(ProfiloUsoConsumo.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<ProfiloUsoConsumo> criteria = builder.createQuery(ProfiloUsoConsumo.class);
      root = criteria.from(ProfiloUsoConsumo.class);
      TypedQuery<ProfiloUsoConsumo> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<ProfiloUsoConsumo> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.search.getNome();
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(root.<String> get("nome"), '%' + nome + '%'));
      }
      TipologiaValutazione tipo = this.search.getTipo();
      if (tipo != null)
      {
         predicatesList.add(builder.equal(root.get("tipo"), tipo));
      }
      String descrizione = this.search.getDescrizione();
      if (descrizione != null && !"".equals(descrizione))
      {
         predicatesList.add(builder.like(root.<String> get("descrizione"), '%' + descrizione + '%'));
      }
      ComposizioneEdifici composizioneEdificio = this.search.getComposizioneEdificio();
      if (composizioneEdificio != null)
      {
         predicatesList.add(builder.equal(root.get("composizioneEdificio"), composizioneEdificio));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<ProfiloUsoConsumo> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back ProfiloUsoConsumo entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<ProfiloUsoConsumo> getAll()
   {

      CriteriaQuery<ProfiloUsoConsumo> criteria = this.entityManager.getCriteriaBuilder().createQuery(ProfiloUsoConsumo.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(ProfiloUsoConsumo.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {
        	 System.out.println(">>>>>>>>>PROFILO OBJECT>>>> IN: "+value);
 	   	     System.out.flush();
        	 
        	Object res= ProfiloUsoConsumoBean.this.entityManager.find(ProfiloUsoConsumo.class, Long.valueOf(value));
            
        	System.out.println(">>>>>>>>>PROFILO OBJECT>>>> OUT: "+res);
	   	     System.out.flush();
        	return res;
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {
              String out;
              System.out.println(">>>>>>>>>PROFILO STRING>>>> IN_oggetto: "+value);
  	   	     System.out.flush();
              
            if (value == null)
            {
            	 System.out.println(">>>>>>>>>PROFILO STRING>>>> IN_oggetto: NULLO");
      	   	     System.out.flush();
            	
               return "";
            }
             out=String.valueOf(((ProfiloUsoConsumo) value).getId());
             System.out.println(">>>>>>>>>PROFILO STRING>>>>> OUT: "+out);
 	   	     System.out.flush();
            return out;
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private ProfiloUsoConsumo add = new ProfiloUsoConsumo();

   public ProfiloUsoConsumo getAdd()
   {
      return this.add;
   }

   public ProfiloUsoConsumo getAdded()
   {
      ProfiloUsoConsumo added = this.add;
      this.add = new ProfiloUsoConsumo();
      return added;
   }
}