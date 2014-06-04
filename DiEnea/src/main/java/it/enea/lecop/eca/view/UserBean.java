package it.enea.lecop.eca.view;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.User;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;


@Named
@Stateful
@ConversationScoped
public class UserBean {

	
	 @PersistenceContext(type = PersistenceContextType.EXTENDED)
	   private EntityManager entityManager;
	
	 public Converter getConverter()
	   {

	      return new Converter()
	      {

	         @Override
	         public Object getAsObject(FacesContext context, UIComponent component, String value)
	         {
	        	 Object ret;
	        	  // debug
	              //  System.out.println("User bean gesAsObject");
	              //  System.out.println("IN "+value);
	              //  System.out.flush();
	            ret=UserBean.this.entityManager.find(User.class, value);
	           // debug
	           // System.out.println("OUT "+ret);
	           // System.out.flush();
	            return ret;
	         }

	         @Override
	         public String getAsString(FacesContext context, UIComponent component, Object value)
	         {
	        	 String ret;
	        	 
	        	 // debug
	        	 // System.out.println("User bean getAsString");
	        	 // System.out.println("IN "+value);
	             // System.out.flush();
	             
	            if (value == null)
	            {
	               return "";
	            }
	              
	            ret =((String) value);
	          //  System.out.println("OUT :"+ret);
	          //  System.out.flush();
	            
	            return ret;
	         }
	      };
	   }
}
