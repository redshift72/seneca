package it.enea.lecop.eca.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import it.enea.lecop.eca.model.Azienda;

import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.Securable;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.data.AziendaDao;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.login.*;


/**
 * Backing bean for Azienda entities.
 * <p>
 * This class provides CRUD functionality for all Azienda entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class AziendaBean implements Serializable
{

   private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Azienda entities
    */

   private String id;
   private boolean isDisabledName=false;
   public boolean isDisabledName() {
	return isDisabledName;
}

public void setDisabledName(boolean isDisabledName) {
	this.isDisabledName = isDisabledName;
}

public String getId()
   {
      return this.id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   private Azienda azienda;

   public Azienda getAzienda()
   {
      return this.azienda;
   }

   @Inject
   private Conversation conversation;
   
   @Inject 
   private Instance<Login> loginUser;
  
   @Inject 
   private CompanyDomainDao cpp;
   
   
   @Inject
   private AziendaDao  azdao;

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
         this.azienda = this.search;
      }
      else
      {
    	  this.setDisabledName(true);
         this.azienda = this.entityManager.find(Azienda.class, getId());
      }
   }

   /*
    * Support updating and deleting Azienda entities
    */

   public String update()
   {
	   Login login=loginUser.get(); 
      //this.conversation.end();

      try
      {
    	  
    	  if(this.azienda.getNome().contains("@") || this.azienda.getNome().contains(" ") || this.azienda.getNome().contains("	") || this.azienda.getNome().trim().equals("") )
       	{	
       	
       	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Scegli un altro nome: non è consentito il campo vuoto o il carattere 'spazio', 'tab', '@' "));
       	 this.setDisabledName(false);
       	 return (this.id == null)?"create":("create?&id=" + this.azienda.getNome());
       	}else
       	{
       		Azienda az;
       		if( !this.azienda.getNome().trim().equals("") && ((az=entityManager.find(Azienda.class, this.azienda.getNome())) != null) && !this.isDisabledName )
       		{
       			this.azienda.setNome("");
       			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Scegli un altro nome: "+az.getNome()+" gia' esite!"));
       			this.setDisabledName(false);
       			return (this.id == null)?"create":("create?&id=" + this.azienda.getNome());
       		}
       	}
    	  this.setDisabledName(true);
    	  
         if (this.id == null)
         {
        	 // nuovo oggetto
        	 this.setDisabledName(false);
        	
        	 
        	 
        	this.azienda.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain())) ;
           PermissionProp pp=new PermissionProp();
           pp.setSUBSETDOMAIN(SecAttrib.CONTROL);
           pp.setIDENTITYDOMAIN(SecAttrib.CONTROL);
           pp.setINTERSECTIONDOMAIN(SecAttrib.READ);
           pp.setOTHER(SecAttrib.NONE);
           
        	this.azienda.setPermissionprop(pp);
            
        	
            this.entityManager.persist(this.azienda);
            this.conversation.end(); 
           
            // Aggiungo al dominio all l'azieda appena creata
            
            CompanyDomain seamless=null;
            CompanyDomain All=null;
            All=this.entityManager.find(CompanyDomain.class,"all");
            if(All!=null)
             {
            	this.azienda.addInCompanyDomain(All);
            	this.entityManager.merge(All);
             }
             
            // creo un dominio di aziende con il nome dell'azienda stessa e con la sola azienda
            // come componente
            if (getMakeAz() && (seamless=this.entityManager.find(CompanyDomain.class,this.azienda.getNome()))==null)
            {
            
            	
           
            // creo una domimio con lo stesso nome dell'azienda	ma con le stesse credenziali del 
            // l'utente loggato e con i permessi standard
            	
            seamless=new CompanyDomain(this.azienda.getNome());
            seamless.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain())) ;
            PermissionProp pp1=new PermissionProp();
            pp1.setSUBSETDOMAIN(SecAttrib.READ);
            pp1.setIDENTITYDOMAIN(SecAttrib.MODIFY);
            pp1.setINTERSECTIONDOMAIN(SecAttrib.NONE);
            pp1.setOWNERUSER(SecAttrib.CONTROL);
            pp1.setOTHER(SecAttrib.NONE);
            
            seamless.setPermissionprop(pp1);
            
            this.entityManager.persist(seamless);
            this.azienda.addInCompanyDomain(seamless);           
            this.entityManager.merge(this.azienda);
            
            //seamless.addAzienda(this.azienda);
            
           
            
            
            }
            
            return "search?faces-redirect=true";
         }
         else
         {
        	 // ID È QUELLO VECCHIO
        	/* 
        	if(!this.id.equals(this.azienda.getNome()))
        	{
        		String newName=this.azienda.getNome();
        		// imposto id vecchio
        		this.azienda.setNome(this.id);
        		// rimuovo quello vecchio
        		this.entityManager.remove(this.azienda);
        		this.azienda.setNome(newName);
        		
        		this.entityManager.persist(this.azienda);
        	}
        	 */
        	 String lUser=login.getCurrentUser().getUsername();
        	 String ownerUser=this.azienda.getOwnerid().getOwnUser().getName();
        	 
        	 String loginDomain=login.getCurrentDomain().getName();
        	 String ownerDomain=this.azienda.getOwnerid().getOwnCompany().getName();
        	 
        	 Set<Azienda> AzLoginDomain=login.getCurrentDomain().getAziende();
        	 Set<Azienda> AzOwnerDomain=this.azienda.getOwnerid().getOwnCompany().getAziende();
        	
        		 boolean intersection=false;;
        	 
        	 SecAttrib permission=null;
        	 
        	if (lUser.equals(ownerUser)) 
        	{
               permission= this.azienda.getPermissionprop().getOWNERUSER();
        	}else if (loginDomain.equals(ownerDomain)) 
        	  {
        		permission= this.azienda.getPermissionprop().getIDENTITYDOMAIN();
        	  } else if (AzLoginDomain.containsAll(AzOwnerDomain))
        	  {
        		  permission= this.azienda.getPermissionprop().getSUBSETDOMAIN();
        	  } else 
        	  {
        		 
             	 
             	 for (Azienda az:AzOwnerDomain)
             	 {
             		 if (AzLoginDomain.contains(az))
             		 {
             			 intersection=true;
             			permission= this.azienda.getPermissionprop().getINTERSECTIONDOMAIN();
             		    break;
             		 }
             	 }
        		  
             	 if (!intersection)
             	 {
             		permission= this.azienda.getPermissionprop().getOTHER();
             	 }
        		  
        		  
        	  } 
        		  
        	// se sono admin e ho il permesso di  modificare i dati dell'azienda MODIFY o il permesso di modificare i dati e la sicurezza (owneship sucurity)
                   
        		if ((lUser.equals("admin")) || ((permission!=null) && (permission.equals(SecAttrib.MODIFY) ||   permission.equals(SecAttrib.CONTROL))  )  )
          		      {
        			         
        			// gestisco cambio id: se sono diversi l'id è l'id vecchio
        			/*
        			   if(!this.id.equals(this.azienda.getNome()))
                	    {
                		 String newName=this.azienda.getNome();
                		// imposto id vecchio
                		  this.azienda.setNome(this.id);
                		// rimuovo quello vecchio
                		  this.entityManager.remove(this.azienda);
                		  this.azienda.setNome(newName);
                		
                		  this.entityManager.persist(this.azienda);
                	   }else */
                		   {
                		   this.entityManager.merge(this.azienda);
                		   }
        			
        			     
        			
        					
        					String nomeAz=this.azienda.getNome();
        					this.conversation.end(); 
        		            return "view?faces-redirect=true&id=" + nomeAz;
          		      }		else {
          		    	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permesso Negato"));
          		    	 return (this.id == null)?"create":("create?&id=" + this.azienda.getNome());
          		      }
        	
         }
      }
      catch (Exception e)
      {
         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
         return (this.id == null)?"create":("create?&id=" + this.azienda.getNome());
         
      }
   }

   public String toEdit()
   {
	   this.setDisabledName(true);
	   return "create";
   }
   
   public String toCreateNew()
   {
	   this.setDisabledName(false);
	   return "create";
   }
   
   public SecAttrib getPermission(Securable secEntity)
   {
	   Login login=loginUser.get(); 
	   
	   String lUser=login.getCurrentUser().getUsername();
  	   String ownerUser=this.azienda.getOwnerid().getOwnUser().getName();
  	 
  	   String loginDomain=login.getCurrentDomain().getName();
  	   String ownerDomain=this.azienda.getOwnerid().getOwnCompany().getName();
  	 
  	   Set<Azienda> AzLoginDomain=login.getCurrentDomain().getAziende();
  	   Set<Azienda> AzOwnerDomain=this.azienda.getOwnerid().getOwnCompany().getAziende();
  	
  		 boolean intersection=false;;
  	 
  	 SecAttrib permission=null;
  	 
  	if (lUser.equals(ownerUser)) 
  	{
         permission= this.azienda.getPermissionprop().getOWNERUSER();
  	}else if (loginDomain.equals(ownerDomain)) 
  	  {
  		permission= this.azienda.getPermissionprop().getIDENTITYDOMAIN();
  	  } else if (AzLoginDomain.containsAll(AzOwnerDomain))
  	  {
  		  permission= this.azienda.getPermissionprop().getSUBSETDOMAIN();
  	  } else 
  	  {
  		 
       	 
       	 for (Azienda az:AzOwnerDomain)
       	 {
       		 if (AzLoginDomain.contains(az))
       		 {
       			 intersection=true;
       			 permission= this.azienda.getPermissionprop().getINTERSECTIONDOMAIN();
       		    break;
       		 }
       	 }
  		  
       	 if (!intersection)
       	 {
       		permission= this.azienda.getPermissionprop().getOTHER();
       	 }
  		  
  		  
  	  } 
   
     return permission;
   
   }
   
   
   public String delete()
   {
      this.conversation.end();

      try
      {
    	  
    	  
    	  if (azdao.remove(getId()))
    	  {
    		  // log
    		  System.out.println("---->>azienda rimossa");
    	  }else System.out.println("---->>azienda NON rimossa");
    		  
    	  
    	/*  
    	  Azienda az=this.entityManager.find(Azienda.class, getId());
    	  System.out.println("Rimuovo "+az.getNome());
    	  
         this.entityManager.remove(az);
         */
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
    * Support searching Azienda entities with pagination
    */

   private int page;
   private long count;
   private List<Azienda> pageItems;
   private Boolean makeAz=false;

   public synchronized Boolean getMakeAz() {
	return makeAz;
}

public synchronized void setMakeAz(Boolean makeAz) {
	this.makeAz = makeAz;
}

private Azienda search = new Azienda();

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

   public Azienda getSearch()
   {
      return this.search;
   }

   public void setSearch(Azienda search)
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
	   List<Azienda> secAllAziende,resultAziende=new ArrayList<Azienda>();
       Login login=loginUser.get();
       CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      
      SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      
      secAllAziende= azdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomainSec,otherSec);

      //this.entityManager.createNamedQuery("Azienda");
      // Populate this.count
      
      /*
      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      Root<Azienda> root = countCriteria.from(Azienda.class);
     
      
      
      // Populate this.pageItems

      CriteriaQuery<Azienda> criteria = builder.createQuery(Azienda.class);
      root = criteria.from(Azienda.class);
      TypedQuery<Azienda> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));     
      query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
     */
      
      

      CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
      
      Root<Azienda> root = countCriteria.from(Azienda.class);
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

      CriteriaQuery<Azienda> criteria = builder.createQuery(Azienda.class);
      root = criteria.from(Azienda.class);
      
      pred=getSearchPredicates(root);
      TypedQuery<Azienda> query;
      
      if (pred==null || pred.length==0)
      {
    	  noWhere=true;
    	  query = this.entityManager.createQuery(criteria.select(root));
      }else
    	  
      {
    	  noWhere=false;
    	  query = this.entityManager.createQuery(criteria.select(root).where(pred));
      }
      
      
      
      
      
      
      
      
      /*
      if (secAllAziende!= null)
      {	  
    	  List<Azienda> filteredAz=query.getResultList();
    	  
        for (Azienda az:filteredAz)
        {
        	
    	   if (secAllAziende.contains(az))
    	   {
    		  resultAziende.add(az);
    	   }
        }
         
        for (Azienda azSec: secAllAziende)
        {
        	
        }
        
        this.pageItems=resultAziende;
        this.count=resultAziende.size();
      }else
      {
    	  this.pageItems = query.getResultList();
    	  this.count=this.pageItems.size();
      }
      */
      
      
      if (secAllAziende!= null)
      {	  
    	 //System.out.println("Tutti gli edifici possibili secured : "+secAllAziende.size());
    	 // System.out.flush();
    	  List<Azienda> edList=query.getResultList();
    	  
    	  //System.out.println("Tutti gli edifici della query criteria sono : "+edList.size());
    	  //System.out.flush();
    	  
        for (Azienda ed:edList)
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
      
      
      //this.pageItems = query.getResultList();
     // this.pageItems=resultAziende;
   }

   
   
   private Predicate[] getSearchPredicates(Root<Azienda> root)
   {
      
      CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      List<Predicate> predicatesList = new ArrayList<Predicate>();

      String nome = this.search.getNome();
      if (nome != null && !"".equals(nome))
      {
         predicatesList.add(builder.like(root.<String> get("nome"), '%' + nome + '%'));
      }
      String tipo = this.search.getTipo();
      if (tipo != null && !"".equals(tipo))
      {
         predicatesList.add(builder.like(root.<String> get("tipo"), '%' + tipo + '%'));
      }
      String CFoPI = this.search.getCFoPI();
      if (CFoPI != null && !"".equals(CFoPI))
      {
         predicatesList.add(builder.like(root.<String> get("CFoPI"), '%' + CFoPI + '%'));
      }
      String descrizione = this.search.getDescrizione();
      if (descrizione != null && !"".equals(descrizione))
      {
         predicatesList.add(builder.like(root.<String> get("descrizione"), '%' + descrizione + '%'));
      }

      return predicatesList.toArray(new Predicate[predicatesList.size()]);
   }

   public List<Azienda> getPageItems()
   {
      return this.pageItems;
   }

   public long getCount()
   {
      return this.count;
   }

   /*
    * Support listing and POSTing back Azienda entities (e.g. from inside an
    * HtmlSelectOneMenu)
    */

   public List<Azienda> getAll()
   {
	   User currentUser;
	   Login login=loginUser.get();
	   List<Azienda> secAllAziende,resultAziende=new ArrayList<Azienda>();
     //  Login login=loginUser.get();
     // CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
      SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
      secAllAziende= azdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomainSec,otherSec);
	   
	   
	   List<Azienda> laz;
	   
	     if (login==null ) return null;
	     if (!login.isLoggedIn()) return null;
	     if ((currentUser=login.getCurrentUser())==null) return null;
	     
	   //  CriteriaQuery<Azienda> criteria = this.entityManager.getCriteriaBuilder().createQuery(Azienda.class);
	   //   laz=this.entityManager.createQuery(criteria.select(criteria.from(Azienda.class))).getResultList();
	    /*
	      for (Azienda az:laz)
	        {
	    	   if (secAllAziende.contains(az))
	    	   {
	    		  resultAziende.add(az);
	    	   }
	        }
	         */
	        //this.pageItems=resultAziende; 
	      
	      
	      return secAllAziende;
	    	 
	    	
	     /*
	   
      CriteriaBuilder cb=this.entityManager.getCriteriaBuilder();
    // jpql for read "SELECT a FROM Azienda a WHERE :luser  MEMBER OF a.defaultSecurityDomain.aclForRead "
    // jpql for modify  "SELECT a FROM Azienda a WHERE ( :luser  MEMBER OF a.defaultSecurityDomain.aclForRead) AND "
    // (:luser MEMBER OF a.defaultSecurityDomain.aclForModify ) 
     CriteriaQuery<Azienda> cq = cb.createQuery(Azienda.class);
     Root<Azienda> pet = cq.from(Azienda.class);
     cq.where(cb.(pet.get(Azienda_.defaultSecurityDomain.), ""));
     
     
     
      return this.entityManager.createQuery(criteria.select().where(cb.equal(, arg1))).getResultList();
       */
   }

   public Converter getConverter()
   {

      return new Converter()
      {

         @Override
         
         /**
          * value e la chiave
          */
         public Object getAsObject(FacesContext context, UIComponent component, String value)
         {
        	 Object ret;
        	   // debug
               // System.out.println("azienda bean gesAsObject");
               // System.out.println("IN "+value);
               // System.out.flush();
            ret=AziendaBean.this.entityManager.find(Azienda.class, value);
           // debug
            //  System.out.println("OUT "+ret);
           //  System.out.flush();
            return ret;
         }

         @Override
         /**
          * entra l'oggetto e deve uscire la chiave o una stringa che se entra nella precendete restituisce l'oggetto stesso
          * che è entrato qui
          */
         public String getAsString(FacesContext context, UIComponent component, Object value)
         {
        	 String ret;
        	// debug
        	// System.out.println("azienda bean getAsString");
        	// System.out.println("IN "+value);
            //  System.out.flush();
             
            if (value == null)
            {
               return "";
            }
              
            ret =((String) value);
           // debug  
          //  System.out.println("OUT :"+ret);
          //  System.out.flush();
            
            return ret;
         }
      };
   }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

   private Azienda add = new Azienda();

   public Azienda getAdd()
   {
      return this.add;
   }

   public Azienda getAdded()
   {
      Azienda added = this.add;
      this.add = new Azienda();
      return added;
   }
}