package it.enea.lecop.eca.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;

/**
 * Backing bean for ComposizioneEdifici entities.
 * <p>
 * This class provides CRUD functionality for all ComposizioneEdifici entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ComposizioneEdificiBean implements Serializable
{

   private   String src="./../VAADIN/cEd";
	
   public void setSrc(String src) {
	
}

public String getSrc() {
	String newSrc=src+"?id="+getId();
	System.out.println(">>>>get src : "+newSrc);
	   System.out.flush();
	
	return newSrc;
}

private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving ComposizioneEdifici entities
    */

   private Long id;

   public Long getId()
   {
	   System.out.println("get id");
	   System.out.flush();
      return this.id;
   }

   public void setId(Long id)
   {
	   System.out.println("set id");
	   System.out.flush();
      this.id = id;
   }

   private ComposizioneEdifici composizioneEdifici;

   public ComposizioneEdifici getComposizioneEdifici()
   {
	   System.out.println("get ComposizioneEdifici ");
	   System.out.flush();
      return this.composizioneEdifici;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   public String create()
   {
	   System.out.println("Create");
	   System.out.flush();
      this.conversation.begin();
      return "../viewvaadin/compedifici.jsf?faces-redirect=true";
      //return "create?faces-redirect=true";
   }

   public void retrieve()
   {
	   
	   System.out.println("Retrive");
   System.out.flush();

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
         this.composizioneEdifici = this.search;
      }
      else
      {
         this.composizioneEdifici = this.entityManager.find(ComposizioneEdifici.class, getId());
      }
   }

   /*
    * Support updating and deleting ComposizioneEdifici entities
    */

   public String update()
   {
	   System.out.println("Update");
	   System.out.flush();
      this.conversation.end();

      try
      {
         if (this.id == null)
         {
        	 // nuovo
            this.entityManager.persist(this.composizioneEdifici);
            return "search?faces-redirect=true";
         }
         else
         {
        	 // update
            this.entityManager.merge(this.composizioneEdifici);
            return "view?faces-redirect=true&id=" + this.composizioneEdifici.getId();
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
	   System.out.println("Delete");
	   System.out.flush();
      this.conversation.end();

      try
      {
         this.entityManager.remove(this.entityManager.find(ComposizioneEdifici.class, getId()));
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
    * Support searching ComposizioneEdifici entities with pagination
    */

   private int page;
   private long count;
   private List<ComposizioneEdifici> pageItems;

   private ComposizioneEdifici search = new ComposizioneEdifici();

   private String provinciaUbicazione;
   
   public String getProvinciaUbicazione()
{
	   if(provinciaUbicazione== null)
	   {
		   if(this.composizioneEdifici.getComuneUbicazione()!=null)
		   {
			   this.provinciaUbicazione=this.composizioneEdifici.getComuneUbicazione().getProvincia();
		   }
	   }
	   
	return provinciaUbicazione;
}

public void setProvinciaUbicazione(String provinciaUbicazione) {
	this.provinciaUbicazione = provinciaUbicazione;
}

public int getPage()
   {
	   System.out.println("get page");
	   System.out.flush();
      return this.page;
   }

   public void setPage(int page)
   {
	   System.out.println("set page");
	   System.out.flush();
      this.page = page;
   }

   public int getPageSize()
   {
	   System.out.println("get page size");
	   System.out.flush();
      return 10;
   }

   public ComposizioneEdifici getSearch()
   {
	   System.out.println("Call GETsearch ");
   System.out.flush();
      return this.search;
   }

   public void setSearch(ComposizioneEdifici search)
   {
	   System.out.println("Call SETsearch ");
	   System.out.flush();
      this.search = search;
   }

   
   public void setComuneUbucazione(Comune com)
   {
	   System.out.println("Call SET comune ubicazione ");
       System.out.println(com.getDescrizione());
	   System.out.flush();
	   
	   this.search.setComuneUbicazione(com);
   }
   
   public Comune getComuneUbucazione()
   {
	   System.out.println("Call GET comune ubicazione ");
       
	   
	   
	  Comune com = this.search.getComuneUbicazione();
	  if (com!=null) System.out.println(com.getDescrizione());
	  else System.out.println("NULL");
	  
	  System.out.flush();
	  
	   return com;
   }
   public void search()
   {
	  System.out.println("---->>> search");
	  System.out.println("---->>> richiama se stesso ");
	  System.out.flush();
      this.page = 0;
   }

   public void paginate()
   {

	   System.out.println("paginate listener");
	   System.out.flush();
      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<ComposizioneEdifici> root = countCriteria.from(ComposizioneEdifici.class);
      countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
      this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

      // Populate this.pageItems

      CriteriaQuery<ComposizioneEdifici> criteria = builder.createQuery(ComposizioneEdifici.class);
      root = criteria.from(ComposizioneEdifici.class);
      TypedQuery<ComposizioneEdifici> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<ComposizioneEdifici> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String name = this.search.getName();
      if (name != null && !"".equals(name))
      {
         predicatesList.add(builder.like(root.<String> get("name"), '%' + name + '%'));
      }
      String noteDellaComposizione = this.search.getNoteDellaComposizione();
      if (noteDellaComposizione != null && !"".equals(noteDellaComposizione))
      {
         predicatesList.add(builder.like(root.<String> get("noteDellaComposizione"), '%' + noteDellaComposizione + '%'));
      }
      Azienda azienda = this.search.getAzienda();
      if (azienda != null)
      {
         predicatesList.add(builder.equal(root.get("azienda"), azienda));
      }
      Comune comuneUbicazione = this.search.getComuneUbicazione();
      if (comuneUbicazione != null)
      {
         predicatesList.add(builder.equal(root.get("comuneUbicazione"), comuneUbicazione));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<ComposizioneEdifici> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back ComposizioneEdifici entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<ComposizioneEdifici> getAll()
   {
         System.out.println("getAll");
         System.out.flush();
      CriteriaQuery<ComposizioneEdifici> criteria = this.entityManager.getCriteriaBuilder().createQuery(ComposizioneEdifici.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(ComposizioneEdifici.class))).getResultList();
   }

   public Converter getConverter()
   {
	   System.out.println("getConverter");
	   System.out.flush();
      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return ComposizioneEdificiBean.this.entityManager.find(ComposizioneEdifici.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((ComposizioneEdifici) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private ComposizioneEdifici add = new ComposizioneEdifici();

   public ComposizioneEdifici getAdd()
   {
      return this.add;
   }

   @SuppressWarnings("unchecked")
public List<Edificio> getEdificiNonInclusi()
   {
	   List<Edificio> edNonInclusi= new ArrayList<Edificio>();
	   List<Edificio> resEdNonInclusi= new ArrayList<Edificio>();
	   String queryAll="SELECT ed FROM Edificio ed";
	   Query query = this.entityManager.createQuery(queryAll);
	   List<Edificio> tuttiEdifici=(List<Edificio>) query.getResultList();
	   if ((tuttiEdifici==null)|| tuttiEdifici.isEmpty() )
	   {
		   // array vuoto
		   System.out.println("non trovo alcun edificio:  restiruisco array di edifici vuoto");
	   	    System.out.flush();
		   return edNonInclusi;
	   }
	   /*
	   String queryS="SELECT  ed FROM Edificio ed,ComposizioneEdifici comp, IN(comp.edifici) compEd WHERE compEd.id <> ed.id ";
	    "SELECT comp FROM ComposizioneEdifici comp WHERE comp.id ="+id+"" 
	   */
	   
	   Set<Edificio> Edifici_Composizione= this.composizioneEdifici.getEdifici();
	   if ((Edifici_Composizione==null) || Edifici_Composizione.isEmpty() )
	   {
		   // array pieno con tutti
		   
		   
		   return tuttiEdifici;
	   }
	    
	    for(Edificio ed: tuttiEdifici )
	    {
	    	if (!Edifici_Composizione.contains(ed))
	    	{
	    		resEdNonInclusi.add(ed);
	    	}
	    }
	    
	  
	   
	  
	   
	   return resEdNonInclusi;
   }
   
   
   
   @SuppressWarnings("unchecked")
   public List<Comune> getComuniPerProvincia()
   {
	   String provincia=this.getProvinciaUbicazione();
   	System.out.println("get comuni per  Provicia :"+provincia);
       System.out.flush();
   	List<Comune> comPrv=new ArrayList<Comune>();
   	if (provincia==null)
   	{
   		
   		System.out.println("provincia nel session bean nulla: provo a recuperare quella del comune di ubicazione");
   	    System.out.flush();
   		if (this.composizioneEdifici.getComuneUbicazione()!= null)
   		{
   			provincia=this.composizioneEdifici.getComuneUbicazione().getProvincia();
   		}else 
   			{
   			System.out.println("provincia nel session bean nulla, comune ubicazione nullo : ritorno una lista vuota di province");
   	   	    System.out.flush();
   			return comPrv;
   			}
   	}
   	
   	String queryS="SELECT  c FROM Comune c WHERE c.provincia='"+provincia+"' ORDER BY descrizione";

   	 Query query = this.entityManager.createQuery(queryS);
   	 
   	    comPrv= (List<Comune>) query.getResultList();

   	  //  this.comuniPerProvincia=comPrv;
   	    
   	    return comPrv;
   }
   
   
   public ComposizioneEdifici getAdded()
   {
      ComposizioneEdifici added = this.add;
      this.add = new ComposizioneEdifici();
      return added;
   }
}