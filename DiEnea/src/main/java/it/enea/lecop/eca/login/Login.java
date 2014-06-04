package it.enea.lecop.eca.login;

import it.enea.lecop.eca.controller.SecurityService;
import it.enea.lecop.eca.core.InitService;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.User;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named
public class Login implements Serializable {

   private static final long serialVersionUID = 7965455427888195913L;

   @Inject
   private Instance<Credentials> credentials;
   
   private Credentials actualCred;

  // @Inject
  // private UserDao userManager;

  // @Inject
  // private CompanyDomainDao cdd;
   
   @Inject
   private InitService initS;
   
   @Inject
   private SecurityService loginS;
   
   private User currentUser;
   
   /**
    * dominio di login quando verificato
    */
   private CompanyDomain currentDomain;

   /**
    * fa il login con le credenziali passate
    * @return
    * @throws Exception
    */
   public String login() throws Exception {
	   initS.foo();
	   setActualCredentials();
	   CompanyDomain cd=null;
	   String effettiveUserName,complexUserName,domainName;
	   String pwd;
	   OwnerId own=null;
	   
	   if (actualCred != null)
	   {
		   pwd=actualCred.getPassword();
		   
		   complexUserName=actualCred.getUsername();
	
	// log	   
	   System.out.println("-->>PROVO IL LOGINNNNNN con user: "+actualCred.getUsername()+" pwd : "+actualCred.getPassword());
	   System.out.flush();
	   
	   
	   
	   
	   // dalle credenziali estraggo il dominio sul quale si vuole loggare
	   // lo username non puo contenere il carattere @
	    if(complexUserName.contains("@"))
	    {
	      String[] nameDomain=complexUserName.split("@");
	    
	      if (nameDomain== null) return null;
	     
	      if(nameDomain.length<2)
		   {
			   return null;
		   }
	    
	      /*
	      // la forma del login  usename@domain
	      cd=cdd.findById(nameDomain[1]);
	     
	       if(cd==null)
	        {
	    	  return null;
	        }
	      */
	      effettiveUserName=nameDomain[0];
	      domainName=nameDomain[1];
	    }else
	     {
	    	//cd=cdd.findById("all");
	    	domainName="all";
	    	//if(cd==null) return null;
	    	effettiveUserName=complexUserName;
	     }
	   
	  /*
	    System.out.println("-->>DOMINIO VERIFICATO DI LOGIN ");
	    System.out.println(cd.getName());
	    System.out.flush();
	    
	   User user = userManager.findByUserName(effettiveUserName);
	   if(user==null)
	   {
		   System.out.println("-->>UTENTE NON TROVATO  ");      
	       System.out.flush();
	       return null;
	   }
	   
	   
	   
	   
	   if(user.getPassword().equals(pwd) )
	   {
		   
		   System.out.println("-->>PWD VERIFICATA  ");
		   System.out.flush();
	   }else {
		   System.out.println("-->>UTENTE NON TROVATO  ");
		   System.out.println("-->>E PWD NON VERIFICATA  ");
		   System.out.flush();
		   return null;
	   }
		   
	   */
	   
	   
	     // utente con la pwd deve essere verificato e l'utente deve essere membro del dominio di login
	     if ((own=loginS.login(effettiveUserName,pwd,domainName,null))!=null) 
	       {
	    	// log
    	    System.out.println("-->>LOGIN EFFETTUATOOOOOOOOOOOO");
            this.currentUser = own.getOwnUser();
            this.currentDomain= own.getOwnCompany();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Welcome, " + currentUser.getName()));
           }else
           {
        	   FacesContext.getCurrentInstance().addMessage("loginForm:newLoginPanel", new FacesMessage("Attention, invalid credentials"));
        	   currentUser = null;
 		      currentDomain=null;
 		      if (actualCred != null)
 		      {
 		    	  actualCred.setPassword("");
 		    	  actualCred.setUsername("");
 		    	  actualCred = null;
 		      }

        	   
        	   //logout();
        	   return null;
           }
	     
	   }else
	   {
		   // log
		   System.out.println("-->>UTENTE NON  E' MEMBRO DEL DOMINIO "+cd.getName());
		   
		   System.out.flush();
		   FacesContext.getCurrentInstance().addMessage("loginForm:newLoginPanel", new FacesMessage("Attention, invalid credentials"));  
		   currentUser = null;
		      currentDomain=null;
		      if (actualCred != null)
		      {
		    	  actualCred.setPassword("");
		    	  actualCred.setUsername("");
		    	  actualCred = null;
		      }
		   //logout();
		   return null;
	   }
       return null;
   }

   public synchronized String logout() {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Goodbye, " + currentUser.getName()));
      currentUser = null;
      currentDomain=null;
      
      if (actualCred != null)
      {
    	  actualCred.cancel(null);
    	  actualCred = null;
      }
      
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
      return "/index.jsf";
   }
   public synchronized String logoutNoSes() {
	      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Goodbye, " + currentUser.getName()));
	      currentUser = null;
	      currentDomain=null;
	      if (actualCred != null)
	      {
	    	  actualCred.cancel(null);
	    	  actualCred = null;
	      }
	      
	      
	      return "/index.jsf";
	   }
   public  synchronized boolean isLoggedIn() {
      return currentUser != null;
   }

   @Produces
   @LoggedIn
   public User getCurrentUser() {
      return currentUser;
   }
   
   synchronized private void setActualCredentials()
   {
	   
	   this.actualCred=credentials.get();
	   
	   
   }

public CompanyDomain getCurrentDomain() {
	return currentDomain;
}

public void setCurrentDomain(CompanyDomain currentDomain) {
	this.currentDomain = currentDomain;
}
   
    
}