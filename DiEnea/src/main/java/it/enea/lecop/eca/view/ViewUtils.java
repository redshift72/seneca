package it.enea.lecop.eca.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinSession;

/**
 * Utilities for working with Java Server Faces views.
 */

public final class ViewUtils
{

   public static <T> List<T> asList(Collection<T> collection)
   {

      if (collection == null)
      {
         return null;
      }

      return new ArrayList<T>(collection);
   }

   private ViewUtils()
   {

      // Can never be called
   }
   
   public static void sendFacesMessage(String id, String message)
   {
	   FacesContext.getCurrentInstance().addMessage(id, new FacesMessage(message));
   }
   
   public static void setNewHandleErrorMessage()
   {
	   VaadinSession.getCurrent().getService().setSystemMessagesProvider(
				    new SystemMessagesProvider() {
	 				    @Override 
	 				    public SystemMessages getSystemMessages(
	 				        SystemMessagesInfo systemMessagesInfo) {
	 				    	

	 				    	CustomizedSystemMessages messages =
	 				                new CustomizedSystemMessages();
	 				        messages.setCommunicationErrorCaption("Comm Err");
	 				        messages.setCommunicationErrorMessage("This is bad.");
	 				        messages.setCommunicationErrorNotificationEnabled(true);
	 				        messages.setCommunicationErrorURL("../viewvaadin/back.html");
	 				        
	 				        
	 				        messages.setInternalErrorCaption("Errore interno");
	 				        messages.setInternalErrorMessage("errore interno");
	 				        messages.setInternalErrorNotificationEnabled(true);
	 				        messages.setInternalErrorURL("../viewvaadin/back.html");
	 				        
	 				        
	 				        messages.setSessionExpiredCaption("Sessione scaduta");
	 				        messages.setSessionExpiredMessage("sessione scaduta");
	 				        messages.setSessionExpiredNotificationEnabled(true);
	 				        messages.setSessionExpiredURL("../viewvaadin/back.html");
	 				        
	 				        return messages;
	 				    }
	 				});
   }
}
