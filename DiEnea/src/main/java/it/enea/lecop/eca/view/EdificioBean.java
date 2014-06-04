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

import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.EsposizioneSuperficiVetrate;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.SecAttrib;

import it.enea.lecop.eca.model.TipologiaEdifici;



/**
 * Backing bean for Edificio entities.
 * <p>
 * This class provides CRUD functionality for all Edificio entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class EdificioBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Edificio entities
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

   private Edificio edificio;

   public Edificio getEdificio()
   {
      return this.edificio;
   }

   @Inject
   private Conversation conversation;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;
   
   @Inject
   public Instance<Login> loginUser;
   
   @Inject
   public EdificioDao eddao;
   

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
         this.edificio = this.search;
      }
      else
      {
         this.edificio = this.entityManager.find(Edificio.class, getId());
      }
   }

   /*
    * Support updating and deleting Edificio entities
    */

   public String update()
   {
	   Login login=loginUser.get();
      //this.conversation.end();

      try
      {
    	  if(! (this.edificio.getSuperficieLordaPiani() > 0))
          {
    		  
    		  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La superficie  lorda hai piani deve essere maggiore di zero"));
              return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
    		  
       	   
          }else if(! (this.edificio.getAreaDisperdente() > 0))
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La superficie disperdente deve essere maggiore di zero"));
              return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }else if(! (this.edificio.getVolumetriaLordaRiscaldata() > 0))
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La volumetria lorda riscaldata deve essere maggiore di zero"));
              return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }else if((this.edificio.getVolumetriaLordaRiscaldata()< this.edificio.getAreaDisperdente() ))
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("La volumetria Lorda non può essere inferiore alla superfice disperdente"));
              return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }else if( !((this.edificio.getNome()!=null) && !this.edificio.getNome().trim().equals("")) )
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Devi immettere un nome "));
        	  return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }else if ( this.edificio.getTipologiaEdifici().equals(TipologiaEdifici.RESIDENZIALE) && (this.edificio.getSuperficieUtile() <= 0))
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Per gli edifici residenziali Devi immettere una superficie netta calpestabile non nulla"));
        	  return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }else if (this.edificio.getTipologiaEdifici().equals(TipologiaEdifici.AZIENDALE) && ((this.edificio.getAltezza() <=0) || (this.edificio.getSuperficieUtile() <=0) ) )
          {
        	  FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Per gli edifici aziendali Devi immettere una superficie netta calpestabile non nulla ed una altezza non nulla"));
        	  return (this.id == null)?"create":("create?&id=" + this.edificio.getId());
          }
    	  
    	  this.conversation.end();
         if (this.id == null)
         {
        	this.edificio.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain()));
        	this.edificio.setPermissionprop(new PermissionProp());
            this.entityManager.persist(this.edificio);
           
            
            return "search?faces-redirect=true";
         }
         else
         {
            this.entityManager.merge(this.edificio);
            return "view?faces-redirect=true&id=" + this.edificio.getId();
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
         this.entityManager.remove(this.entityManager.find(Edificio.class, getId()));
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
    * Support searching Edificio entities with pagination
    */

   private int page;
   private long count;
   private List<Edificio> pageItems;

   private Edificio search = new Edificio();

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

   public Edificio getSearch()
   {
      return this.search;
   }

   public void setSearch(Edificio search)
   {
      this.search = search;
   }

   public void search()
   {
      this.page = 0;
   }

   public void paginate()
   {
	   Long allCount;
	  List<Edificio> secAllAziende,resultAziende=new ArrayList<Edificio>();
      Login login=loginUser.get();
      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      
      SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      
      secAllAziende= eddao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomainSec,otherSec);
	   
      

      // Populate this.count

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      
      Root<Edificio> root = countCriteria.from(Edificio.class);
      boolean noWhere=false;
      // recupera i criteri di ricerca
      Predicate[] pred=getSearchPredicates(root);
      
      if (pred==null || pred.length==0)
      {
    	  noWhere=true;
      }
          if (noWhere)
          {
        	  countCriteria = countCriteria.select(builder.count(root));
          }else
           {
        	  countCriteria = countCriteria.select(builder.count(root)).where(pred);
           }
     
      allCount=this.entityManager.createQuery(countCriteria).getSingleResult();
     

      // Populate this.pageItems

      CriteriaQuery<Edificio> criteria = builder.createQuery(Edificio.class);
      root = criteria.from(Edificio.class);
      
      pred=getSearchPredicates(root);
      TypedQuery<Edificio> query;
      
      if (pred==null || pred.length==0)
      {
    	  noWhere=true;
    	  query = this.entityManager.createQuery(criteria.select(root));
      }else
    	  
      {
    	  noWhere=false;
    	  query = this.entityManager.createQuery(criteria.select(root).where(pred));
      }
      
      
      
      
      
      
      
      
      //query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
      
      
      if (secAllAziende!= null)
      {	  
    	 //System.out.println("Tutti gli edifici possibili secured : "+secAllAziende.size());
    	 // System.out.flush();
    	  List<Edificio> edList=query.getResultList();
    	  
    	  //System.out.println("Tutti gli edifici della query criteria sono : "+edList.size());
    	  //System.out.flush();
    	  
        for (Edificio ed:edList)
        {
    	   if (secAllAziende.contains(ed))
    	   {
    		  resultAziende.add(ed);
    	   }
        }
         
        
        
        this.count=resultAziende.size();
        int from,to;
        from=this.page*getPageSize();
        to=from+getPageSize();
        if(from<this.count && to<this.count)
        {	
         this.pageItems=resultAziende.subList(from, to);
        }else
        {
        	if(to>=this.count)
        	{
        		this.pageItems=resultAziende.subList(from, resultAziende.size());
        	}else
        	{
        		this.pageItems=resultAziende.subList(resultAziende.size(), resultAziende.size());
        	}
        }
        
      }else
      {
    	  query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
    	  
    	  this.count=allCount;
    	  this.pageItems = query.getResultList();
      }
      
      
      //this.pageItems = query.getResultList();
   }

   private Predicate[] getSearchPredicates(Root<Edificio> root)
   {

      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.search.getNome();
      
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(root.<String> get("nome"), '%' + nome + '%'));
        // System.out.println("-->Aggiunto predicato sul nome");
        // System.out.flush();
      }
      TipologiaEdifici tipoEdificio = this.search.getTipologiaEdifici();
      if (tipoEdificio != null)
      {
         predicatesList.add(builder.equal(root.get("tipologiaEdifici"), tipoEdificio));
         //System.out.println("-->Aggiunto predicato sul tipologia edifici");
         //System.out.flush();
      }
      
      Azienda azienda= this.search.getAzienda();
      
      if(azienda != null)
      {
    	  predicatesList.add(builder.equal(root.get("azienda"), azienda));
    	  //System.out.println("-->Aggiunto predicato sulla azienda");
          //System.out.flush();
      }
      /*
      ComposizioneEdifici composizione = this.search.getComposizione();
      if (composizione != null)
      {
         predicatesList.add(builder.equal(root.get("composizione"), composizione));
      }
      */
      System.out.println("-->Il predicato è lungo: "+predicatesList.size());
      System.out.flush();
      
      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Edificio> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Edificio entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Edificio> getAll()
   {

      CriteriaQuery<Edificio> criteria = this.entityManager.getCriteriaBuilder().createQuery(Edificio.class);
      return this.entityManager.createQuery(criteria.select(criteria.from(Edificio.class))).getResultList();
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {

            return EdificioBean.this.entityManager.find(Edificio.class, Long.valueOf(value));
         }

         @Override
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {

            if (value == null)
            {
               return "";
            }

            return String.valueOf(((Edificio) value).getId());
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Edificio add = new Edificio();

   public Edificio getAdd()
   {
      return this.add;
   }

   public Edificio getAdded()
   {
      Edificio added = this.add;
      this.add = new Edificio();
      return added;
   }
   
   public List<TipologiaEdifici> getTipologiaEdificioAll()
   {
	   List<TipologiaEdifici> tpe= new ArrayList<TipologiaEdifici>();
	   for(TipologiaEdifici tp: TipologiaEdifici.values())
	   {
		   tpe.add(tp);
	   }
	   return tpe;
   }
   public String newSupVetrate()
   {
	   this.getEdificio().setEsposizioneVetri(new EsposizioneSuperficiVetrate());
	   return "";
   }
}