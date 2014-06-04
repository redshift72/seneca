package it.enea.lecop.eca.login;




import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.inject.Named;

import org.ajax4jsf.Messages;
import org.richfaces.application.FacesMessages;

import java.io.Serializable;
import java.util.Iterator;

@SessionScoped
@Named
public class Credentials implements Serializable {

  
private String username;
   private String password;

   public String getUsername() {
     // debug
	 // System.out.println("username restituito:"+ this.username);
     //  System.out.flush();
	   return username;
   }

   public void setUsername(String username) {
	   // debug
	   // System.out.println("username passato:"+ username);
	   // System.out.flush();
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }
   public void cancel( ActionEvent ae) {
	   // Set all values to null
	   this.username = null;
	   this.password = null;
	   
	   // Then clear message
	   
	   Iterator<FacesMessage> msgIterator = FacesContext.getCurrentInstance().getMessages();
	    while(msgIterator.hasNext())
	    {
	        msgIterator.next();
	        msgIterator.remove();
	    }
	    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	   }
}