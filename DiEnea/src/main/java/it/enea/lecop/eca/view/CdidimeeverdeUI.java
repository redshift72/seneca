package it.enea.lecop.eca.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Collection;


import it.enea.lecop.eca.data.AziendaDao;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.ComposizioneEdificiDao;
import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.data.InterventoMigliorativoDao;
import it.enea.lecop.eca.data.MenuItemDao;
import it.enea.lecop.eca.data.ProfiloUsoConsumoDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.data.ValutazioneDao;
import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.model.Valutazione;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.liferay.portal.util.comparator.UserLoginDateComparator;
import com.vaadin.annotations.Theme;


import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.cdi.CDIUI;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
@CDIUI(value = "vUser")
@Theme("mytheme")
public class CdidimeeverdeUI extends UI {

	@EJB
	UserDao userd;
	  
	@Inject
	CompanyDomainDao cdd;
	  	
  
	@Inject
	MenuItemDao   midao;
  

	@Inject
	private Instance<Login> loginUser;
  
//here we are DAO
  @Inject
  AziendaDao   azd;
	  
  @Inject
  ComposizioneEdificiDao compoEd_d;
	  
  @Inject
  EdificioDao ed_d;
	  
  @Inject
  InterventoMigliorativoDao intMigl_d;
  
  @Inject
  ProfiloUsoConsumoDao  profUC_d;
  
  @Inject
  ValutazioneDao  	valu_d;
  
///here we are BEANS
  

  
  private Table contactList = new Table();
  private TextField searchField = new TextField();
  private Button addNewContactButton = new Button("New");
  private Button saveContactButton = new Button("Save");
  private Button removeContactButton = new Button("Remove this contact");
  private FormLayout editorLayout = new FormLayout();
  private FieldGroup editorFields = new FieldGroup();
  private HorizontalLayout buttonLayout = new HorizontalLayout();
  private TwinColSelect domain= new TwinColSelect("Dominio Aziendale");
  private TwinColSelect menuAcces= new TwinColSelect(" Privilegi Accesso ");
  private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
  private VerticalLayout leftLayout = new VerticalLayout();
       IndexedContainer contactContainer;

	private Object newContactId;

	private Object selectedId;

  public Object getNewContactId() {
		return newContactId;
	}


	public void setNewContactId(Object newContactId) {
		this.newContactId = newContactId;
	}


  private static final String FNAME = "UserName";
  private static final String LNAME = "Name";
  private static final String EMAIL = "Email";
  // id chiave del bean 
  private static final String ID = "UserName";
  private static final String[] fieldNames = new String[] { FNAME,LNAME,EMAIL, "Phone Num","Dominio Aziendale","Password","Verifica Password"};
  
       protected void init(VaadinRequest request) {
    	  contactContainer= createDummyDatasource();
    	  initLayout();
          initContactList();
          initEditor();
          initSearch();
          initAddRemoveButtons();
          ViewUtils.setNewHandleErrorMessage();
  }


  private void initLayout() {

          
          setContent(splitPanel);

          
          splitPanel.addComponent(leftLayout);
          splitPanel.addComponent(editorLayout);
          splitPanel.addStyleName(Reindeer.PANEL_LIGHT);
          
          leftLayout.addComponent(contactList);
          HorizontalLayout bottomLeftLayout = new HorizontalLayout();
          leftLayout.addComponent(bottomLeftLayout);
          bottomLeftLayout.addComponent(searchField);
          bottomLeftLayout.addComponent(addNewContactButton);

          leftLayout.setSizeFull();

          leftLayout.setExpandRatio(contactList, 1);
          contactList.setSizeFull();

          bottomLeftLayout.setWidth("100%");
          searchField.setWidth("100%");
          bottomLeftLayout.setExpandRatio(searchField, 1);

          editorLayout.setMargin(true);
          editorLayout.setVisible(false);
  }

/*
Dynamic forms
User interface can be created dynamically to reflect the underlying data. We use a FieldGroup to bind components to a data source.

We choose to write changes through to data source instead of buffering and committing explicitly.*/

  private void initEditor() {
          int i=0;
          for (String fieldName : fieldNames) {
                 if (fieldName.equalsIgnoreCase("password"))
                 {
                	 PasswordField field = new PasswordField(fieldName);
                	 editorLayout.addComponent(field,i);
                     field.setWidth("70%");
                     editorFields.bind(field, fieldName);
                 } else if(fieldName.equalsIgnoreCase("Verifica Password"))
                 {
                	 PasswordField pwdfield = new PasswordField(fieldName);
                	 editorLayout.addComponent(pwdfield,i);
                     pwdfield.setWidth("70%");
                     editorFields.bind(pwdfield, fieldName);
                 }
                 else if(fieldName.equalsIgnoreCase("Dominio Aziendale"))
                 {
                	 //ComboBox cb = new ComboBox(fieldName, options)
                	 composeDomain();
                	 editorLayout.addComponent(domain,i);
                	
                	 domain.setLeftColumnCaption("All the domains available to this user");
                	 domain.setRightColumnCaption("All domains which user belongs");
                	 domain.setWidth("70%");
                	 domain.setImmediate(true);
                	// editorFields.bind(domain, fieldName);
                	 
                	 
                 }
                 
                 else
                 {	 
        	      TextField field = new TextField(fieldName);
                  editorLayout.addComponent(field,i);
                
                  field.setWidth("70%");
                  editorFields.bind(field, fieldName);
                 }
                  i++;
          }
          // abilito il controllo all'accesso per admin e per il dominio all
          Login login=this.loginUser.get();
          if (login.getCurrentUser().getUsername().equals("admin") || login.getCurrentDomain().getName().equals("all") )
          {
        	  editorLayout.addComponent(menuAcces,i);
              composeMenu(null);
              menuAcces.setLeftColumnCaption("All  menu items to this user");
              menuAcces.setRightColumnCaption("All items which user access");
              menuAcces.setWidth("70%");
              menuAcces.setImmediate(true);
              menuAcces.setEnabled(false);
              menuAcces.setVisible(false);
             
              
          }   
          
          saveContactButton.setEnabled(true);
          
          buttonLayout.addComponent(saveContactButton);
          buttonLayout.addComponent(removeContactButton);
          editorLayout.addComponent(buttonLayout);
          editorLayout.addStyleName(Reindeer.PANEL_LIGHT);

          editorFields.setBuffered(false);
  }

  private void composeDomain() {
	  
	  
	 findAndFillAllDomainName();
	
}
   private void composeMenu(String username)
   { 
	   Login login=loginUser.get();
	   boolean prefillSelected=false;
	   User user=null;
		
		if(username != null) user=userd.findByUserName(username);
		if (user!=null) prefillSelected=true;
		  SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		  SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		  SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};		   
		  SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY}; 
	   List<MenuItem>  mi = midao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec, subDomSec, otherSec);
   
	   
	   if(mi==null || mi.isEmpty())
	   {
		   // log
		   System.out.println("non ci sono elementi menuitem selezionabili");
		   System.out.flush();
			  return;
	   }
	   
	    int i=1;  
		this.menuAcces.removeAllItems();
		
		if (prefillSelected)
		{
		  
		  for (MenuItem cd: mi)
		    {
			  // elementi disabilitati
			  if (cd.getPosition() < 0 ) 
				  {
				    continue;
				  }
			  menuAcces.addItem(cd.getName());
			  menuAcces.setItemCaption(cd.getName(), cd.getLabel()+"("+cd.getName() +")");		 
			  
			  if (cd.getAccessUser().contains(user.getUsername()))
			  {
				  menuAcces.select(cd.getName());
				  // log
				  System.out.println("L'utente "+user.getUsername() +" ha gia accesso al MenuItem "+cd.getName());
				  System.out.flush();
			  }else 
			  {
				  // log
				  System.out.println("L'utente "+user.getUsername() +"NON ha  accesso al MenuItem "+cd.getName());
				  System.out.flush();
			  }
			  		  
			 // item.put(i, cd.getName());
			  i++;
		    }
		  
		}
	else{
	     for (MenuItem cd: mi)
	      {
	    	 // elementi disabilitati
			  if (cd.getPosition() < 0 ) 
				  {
				    continue;
				  }
	    	 menuAcces.addItem(cd.getName());
	    	 menuAcces.setItemCaption(cd.getName(), cd.getLabel()+"("+cd.getName() +")");
		  
		  
		  // item.put(i, cd.getName());
		   i++;
	     }
	}
		
		
   }

private LinkedHashMap<Integer, String> findAndFillAllDomainName() {
	LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
	List<CompanyDomain> cds=cdd.retrieveAllCompanyDomainOrderedByName();
	int i=1;  
	for (CompanyDomain cd: cds)
	  {
		  domain.addItem(i);
		  domain.setItemCaption(i, cd.getName());
		  
		  
		  item.put(i, cd.getName());
		  i++;
	  }
	  
	  
	  
	return item;
}
protected LinkedHashMap<Integer, String> findAndFillAllDomainNameOnUser(String username) {
	
	boolean prefillSelected=false;
	
	//Set<CompanyDomain> cdUser;
	User user=userd.findByUserName(username);
	if (user!=null) prefillSelected=true;
	
	
	LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
	List<CompanyDomain> cds=cdd.retrieveAllCompanyDomainOrderedByName();
	int i=1;  
	domain.removeAllItems();
	if (prefillSelected)
		{
		  //cdUser=user.getDomainsOfMembership();
		  for (CompanyDomain cd: cds)
		    {
			  
			  domain.addItem(i);
			  domain.setItemCaption(i, cd.getName());
			  //user.getDomainsOfMembership()
			  
			  // debug
			  // System.out.println("User "+user.getUsername());
			  // System.out.flush();
			  
			  
			  
			  Set<CompanyDomain> cduser=user.getDomainsOfMembership();
			  for(CompanyDomain cdu:cduser)if(cdu.getName().equals(cd.getName()))
			  {
				  // log
				  // System.out.println("fa parte del dominio "+cd.getName());
				  // System.out.flush();
				  
				  domain.select(i);
				  break;
			  }else
			     {
				    //log
			    	 System.out.println("NON fa parte del dominio "+cd.getName());
					 System.out.flush();
			     }
			 /* 
			  if (user.getDomainsOfMembership().contains(cd))
			     { 
				  // se non è tra quelli gia presenti tra i domini cui partecipa l'utente 
				  // devo metterlo dai potenziali a sx
				  System.out.println("fa parte del dominio "+cd.getName());
				  System.out.flush();
				  
				  domain.select(i);
				  
			     }else
			     {
			    	 System.out.println("NON fa parte del dominio "+cd.getName());
					  System.out.flush();
			     }
			       
			  */
			  
			  item.put(i, cd.getName());
			  i++;
		     }
		  
		}
	else{
	     for (CompanyDomain cd: cds)
	      {
		  
		   domain.addItem(i);
		   domain.setItemCaption(i, cd.getName());
		  
		  
		   item.put(i, cd.getName());
		   i++;
	     }
	}
	  
	  
	return item;
}

private void initSearch() {

	  /*
Event handling
Granularity for sending events over the wire can be controlled. By default simple changes like writing a text in TextField are sent to server with the next Ajax call. You can configure your component to send the changes to server immediately after focus leaves the field. Here we choose to send the text over the wire as soon as user stops writing for short a moment.

When the event happens, we handle it in the anonymous inner class. You may instead choose to use a separate named controller class. In the end, the preferred application architecture is up to you. */
	  

          searchField.setInputPrompt("Search contacts");

          searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);

          searchField.addTextChangeListener(new TextChangeListener() {
                  public void textChange(final TextChangeEvent event) {

                          contactContainer.removeAllContainerFilters();
                          contactContainer.addContainerFilter(new ContactFilter(event.getText()));
                  }
          });
          
  }
	/*
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		final User user=man.find(User.class, "ciccio");
		
       
       
       
		Button button = new Button("Click Me");
		
		
		
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				handleClick( event, layout, user.getName());
			}
		});
		layout.addComponent(button);
	}
	*/
	
   final public void handleClick(ClickEvent event,VerticalLayout layout,String name)
   {
	   Login login;
	   
	   login=loginUser.get();
	   
	   if (login!=null)
       {
    	     if (login.isLoggedIn())
    	     {
    	    	 if (login.getCurrentUser().getName().equals(name))
    	    	 {
    	    		 layout.addComponent(new Label("ciao, "+name+" is logged in"));
    	    	 }else
    	    	 {
    	    		 layout.addComponent(new Label(" c'è un utente loggato: "+login.getCurrentUser().getName()+".. non e' ciccio"));
    	    	 }
    	     }else
    	     {
    	    	 layout.addComponent(new Label("non c'è alcun utente loggato...la sessione  è iniziata "));
    	    	 
    	     }
       }else
       {
	   layout.addComponent(new Label("non c'è alcun utente loggato...la sessione non è iniziata "));
       }
   }



   private class ContactFilter implements Filter {
       private String needle;

       public ContactFilter(String needle) {
               this.needle = needle.toLowerCase();
       }

       public boolean passesFilter(Object itemId, Item item) {
               String haystack = ("" + item.getItemProperty(FNAME).getValue()
                               + item.getItemProperty(LNAME).getValue() + item
                               .getItemProperty(EMAIL).getValue()).toLowerCase();
               return haystack.contains(needle);
       }

       public boolean appliesToProperty(Object id) {
               return true;
       }
}

   /**
    * restituisce uno nuovo se non c'è ne gia uno
    * @return
    */
   protected Object newItem()
   {
	   Object localNewContactId= getNewContactId();
   
	   
	   if( localNewContactId==null) 
	   {
		   contactContainer.removeAllContainerFilters();
		   localNewContactId=contactContainer.addItemAt(0);
	       setNewContactId(localNewContactId);
	   }
	   
	   return localNewContactId;
   }
   
private void initAddRemoveButtons() {
	
	final Object contactId;
	
	
       addNewContactButton.addClickListener(new ClickListener() {
               public void buttonClick(ClickEvent event) {

/*
Data model access
Rows in the Container data model are called Items. Here we add a new row to the beginning of the list.

Each Item has a set of Properties that hold values. Here we set the value for two of the properties. */
                     
            	   contactContainer.removeAllContainerFilters();
            	   Object itemId=newItem(); 
                
                   
            	   editorFields.setItemDataSource(contactList.getItem(itemId));
                   // sto creando nuovo
            	   setContactId(null);
                    
                    Login login=loginUser.get();
                    if (login.getCurrentUser().getUsername().equals("admin") || login.getCurrentDomain().getName().equals("all") )
                    {
                    	// log
                    	System.out.println("sono utente Admin");
                    	System.out.flush();
                    	
                      composeMenu(null)	;
                      menuAcces.setVisible(true);
                      menuAcces.setEnabled(true);
                      (editorLayout.getComponent(7)).setVisible(true);
                      (editorLayout.getComponent(7)).setEnabled(true);
                      
                    }else
                    {
                 	   menuAcces.setVisible(false);
                 	  menuAcces.setEnabled(false);
                    }
                    
                    editorLayout.setVisible(true);
               }
       });

       removeContactButton.addClickListener(new ClickListener() {
               public void buttonClick(ClickEvent event) {
                      Object contactId = contactList.getValue();
                    
                      if(contactId==null){
                    	  return;
                      }
                      Login login=loginUser.get();
                      Item it=  contactList.getItem(contactId);
                      String username=(String) it.getItemProperty(FNAME).getValue();
          
                      User user=userd.findByUserName(username);
                       
                       if(user!=null)
                       {
                    	   // all non può essere rimosso
                    	   if(user.getName().equalsIgnoreCase("admin")){
                    		   
                    		   Notification.show("L utente 'admin' non può essere cancellato", Notification.Type.ERROR_MESSAGE);
           						return;
                    		   
                    	   }
                    	   
                    	   
                    	    // TODO what about security ???
                    	   
                    	   // TODO do we need to work under transaction?
                    	   // rollback shall be managed CHECK EXCEPTION HANDLING FROM METHODS
                    	   
                    	   // potrei prendere il look su user
                    	   
                    	   // log
                    	   System.out.println("Deleting User "+username);
                    		  
                    	   //cancellazione in 4 fasi
                    	   //  0) passaggio di ownership di tutti gli ogetti posseduti ad admin
                    	   //  1) eliminazione da tutti gli utenti
                    	   //  2) eliminazione da tutte le aziende
                    	   //  3) cancellazione dell'utente
                    	   User userAdmin=userd.findByUserName("admin");
                    	   PermissionProp strict_pp= new PermissionProp(SecAttrib.CONTROL, SecAttrib.READ , SecAttrib.NONE, SecAttrib.NONE, SecAttrib.NONE);                   	   
                    	   
                    	   // azienda
                    	   List<Azienda> aziende=azd.findAll();
                    	   for(Azienda idx: aziende){
                    		   // debug
                    		   // System.out.println("Checking Azienda "+idx.getNome());
                    		   if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
                    			   
                    			   // log
                    			   System.out.println("Changing Azienda "+idx.getNome()+" owning user to admin" );
	                    	  	                    			   
                    			   idx.getOwnerid().setOwnUser(userAdmin);
                    		                  			   
                    			   idx.setPermissionprop(strict_pp);
                    			   
                    			   azd.save(idx);
                    		   }
                    		   
                    	   }
                    	 //TODO  calcolo economico NON E' UN DAO cherè?
                    	   
                    	  //companyDomanin 
                    	   List<CompanyDomain> domains=cdd.findAll();
                    	   for(CompanyDomain idx: domains){
                    		   // log
                    		   // System.out.println("Checking Dominio "+idx.getName());
                    		   if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
                    			   
                    			   System.out.println("Changing Dominio "+idx.getName()+" owning user to admin" );
	                    	  	                    			   
                    			   idx.getOwnerid().setOwnUser(userAdmin);
                    		                  			   
                    			   idx.setPermissionprop(strict_pp);
                    			   
                    			   cdd.save(idx);
                    		   }
                    		   
                    	   }
                    	  
                        //composizione edifici  
		              	List<ComposizioneEdifici> composizioni=compoEd_d.findAll();
		              	for(ComposizioneEdifici idx: composizioni){
	                    	
		              		System.out.println("Checking Composizioneedifici "+idx.getName());
	                    	  if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
                   			   
                   			   System.out.println("Changing ComposizioneEdifici "+idx.getName()+" owning user to admin" );
	                    	  	                    			   
                   			   idx.getOwnerid().setOwnUser(userAdmin);
                   		                  			   
                   			   idx.setPermissionprop(strict_pp);
                   			   
                   			   compoEd_d.save(idx);
                   		   }
	                    		   
	                      }
	                    
                    	   
                    	 // comuni OK perchè sempre admin e all
                    	   
                    	 // Edificio
		              	List<Edificio> edifici=ed_d.findAll();
		              	for(Edificio idx: edifici){
	                    	  System.out.println("Checking edifici "+idx.getNome());
	                    	  if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
	                   			   
	                   			   System.out.println("Changing edifici "+idx.getId()+" owning user to admin" );
		                    	  	                    			   
	                   			   idx.getOwnerid().setOwnUser(userAdmin);
	                   		                  			   
	                   			   idx.setPermissionprop(strict_pp);
	                   			   
	                   			   ed_d.save(idx);
	                   		   }
	                    		   
	                      }
          	                       	 
                    	 
		              	 // TODO intervento migliorativo NON E' UN DAO 
		              	 // cannot be create by user interface   
		              	
		              	 // menuitem cannot be created yet from user interface 
		              	
		              	 // TODO ParamIntevento NON HA DAO
		              	 // Creazione crash
		              	
		              	// ProfiloConsumo 
		              	List<ProfiloUsoConsumo> profiles=profUC_d.findAll();
		              	for(ProfiloUsoConsumo idx: profiles){
	                    	  System.out.println("Checking profiles "+idx.getNome());
	                    	  if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
	                   			   
	                   			   System.out.println("Changing profilo uso consumo "+idx.getNome()+" owning user to admin" );
		                    	  	                    			   
	                   			   idx.getOwnerid().setOwnUser(userAdmin);
	                   		                  			   
	                   			   idx.setPermissionprop(strict_pp);
	                   			   
	                   			   profUC_d.save(idx);                			  
	                   			   
	                   		   }
                    		   
	                      }
	             
		              	
		              	// TODO Risultato Valutazione NON E' UN DAO 
		              	// TODO non ha neppure ne owner ne sicurezza settata
		              	
		              	// TipoEdificio cannot be created yet from user interface 
		              	
		            	//  User 	
		              	List<User> users=userd.findAll();
		              	for(User idx: users){
	                    	  System.out.println("Checking users "+idx.getName());
	                    	  if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
	                   			   
	                   			   System.out.println("Changing users "+idx.getName()+" owning user to admin" );
		                    	  	                    			   
	                   			   idx.getOwnerid().setOwnUser(userAdmin);
	                   		                  			   
	                   			   idx.setPermissionprop(strict_pp);
	                   			   
	                   			   userd.save(idx);
	                   		   }
                    		   
	                      }
	             
		              	//  Valutazione  	
		              	List<Valutazione> valutazioni=valu_d.findAll();
		              	for(Valutazione idx: valutazioni){
	                    	  System.out.println("Checking valutazioni "+idx.getDescrizione());
	                    	  if (idx.getOwnerid().getOwnUser().getUsername().equals(user.getUsername())) {
	                   			   
	                   			   System.out.println("Changing valutazioni "+idx.getDescrizione()+" owning user to admin" );
		                    	  	                    			   
	                   			   idx.getOwnerid().setOwnUser(userAdmin);
	                   		                  			   
	                   			   idx.setPermissionprop(strict_pp);
	                   			   
	                   			   valu_d.save(idx);
	                   		   }
                    		   
	                      }
                    	   
          	                       	   
		              	 if( userd.remove(user) &&  midao.removeUserFromAllItems(user.getUsername(),login.getCurrentUser().getUsername(),login.getCurrentDomain()) )
                    	  {
                    		 contactList.removeItem(contactId);
                    	     Notification.show("User: "+username+" rimosso", Notification.Type.HUMANIZED_MESSAGE);
                    	   }else
                    	   {
                    		   Notification.show("User: "+username+" non posso rimuoverlo", Notification.Type.ERROR_MESSAGE);
                    	   }
                    		  
                    	  
                    	  
                    	  
                    	  
                     }else
                       {
                    	 contactList.removeItem(contactId);
                    	   // Notification.show("User: "+username+" non posso rimuovere", Notification.Type.ERROR_MESSAGE);
                       }
                       
                       
               }
       });
       
       
       saveContactButton.addClickListener(new ClickListener() {
		
    	   //{ FNAME,LNAME,EMAIL, "Phone Num","Dominio Aziendale","Password","Verifica Password"};
    	   
		@SuppressWarnings("unchecked")
		@Override
		public void buttonClick(ClickEvent event) {
			Object contactId = getContactId();
			Login login=loginUser.get();
			System.out.println("id contact selezionato selezionato: "+ contactId);
			System.out.flush();
			String username;
			String name;
			String email;
			String phoneNum;
			String password;
			String retypePassword;
			Set<Integer>	selValue;
			if (contactId!=null)
			{	
			  username=(String)contactList.getContainerProperty(contactId , FNAME).getValue();
			  name=(String)contactList.getContainerProperty(contactId , LNAME).getValue();
			  email=(String)contactList.getContainerProperty(contactId , EMAIL).getValue();
			  phoneNum=(String)contactList.getContainerProperty(contactId , "Phone Num").getValue();
			  password=(String)contactList.getContainerProperty(contactId , "Password").getValue();
			  retypePassword=(String)contactList.getContainerProperty(contactId, "Verifica Password").getValue();
			}
			else
			{
				username=(String)((TextField)editorLayout.getComponent(0)).getValue();
				name=    (String)((TextField)editorLayout.getComponent(1)).getValue();
				email=   (String)((TextField)editorLayout.getComponent(2)).getValue();
				phoneNum=(String)((TextField)editorLayout.getComponent(3)).getValue();
				password=(String)((PasswordField)editorLayout.getComponent(5)).getValue();
				retypePassword=(String)((PasswordField)editorLayout.getComponent(6)).getValue();
			}
			
			TwinColSelect mSel=(TwinColSelect)editorLayout.getComponent(4);
			
			selValue= (Set<Integer>) mSel.getValue();
			
			if (selValue.size() == 0)
			{
				Notification.show("L'utente deve appartenere almeno ad un dominio", Notification.Type.ERROR_MESSAGE);
				return;
			}
			
			if(password.trim().isEmpty())
			{
				Notification.show("La password non può essere vuota e gli spazi all'inizio e alla fine saranno ignorati", Notification.Type.ERROR_MESSAGE);
				return;
			}
			if(password.trim().length()<4)
			{
				Notification.show("La password non può essere più corta di 4 caratteri e gli spazi all'inizio e alla fine saranno ignorati", Notification.Type.ERROR_MESSAGE);
				return;
			}
			
			if (!password.equals(retypePassword))
			{
				Notification.show("La password deve essere uguale nei 2 campi e gli spazi all'inizio e alla fine saranno ignorati", Notification.Type.ERROR_MESSAGE);
				return;
			}
			
			//return ((Set<?>) getValue()).contains(itemId);
			
			// recupero il componente 
	
		
	
		
			
		//	String dominioName=(String)contactList.getContainerProperty(contactId, "Dominio Aziendale").getValue();
			
	    //		System.out.println("Dominio selezionato: "+ dominioName);
			
	    //		System.out.flush();
			
			
			
			Set<String> valItemMenu=null;
			 if (login.getCurrentUser().getUsername().equals("admin") || login.getCurrentDomain().getName().equals("all") )
			 {	 
				 valItemMenu= (Set<String>) menuAcces.getValue();
			 }
			
			//{ FNAME,LNAME,"Password",EMAIL, "Phone Num"};
			User newUser = new User(username,name,password,email,phoneNum);
			
				
			// set permissions, owneship and creation date to default
			newUser.setPermissionprop(new PermissionProp());
			newUser.setOwnerid(new OwnerId(login.getCurrentUser(), login.getCurrentDomain()));
			
			// imposto per ora sempre ad abilitato poi lo faro con la UI
			newUser.setEnabled(true);
			
			//	CompanyDomain cd;
			
			
			Set<String> domainNames= new HashSet<String>();
			//List<CompanyDomain> selDomains= new ArrayList<CompanyDomain>();
			String dname;
			 for(Integer idSelItem: selValue)
				{
				 dname=mSel.getItemCaption(idSelItem);
				 domainNames.add(dname);			
				}	
					
			if ( userd.setDomainOnUser(newUser,domainNames) 
					&& midao.addUserOnSelectedItem(valItemMenu, newUser.getUsername(), login.getCurrentUser().getUsername(), login.getCurrentDomain()))
			{
				 Notification.show("Save", " Nuovo utente salvato!",Notification.Type.HUMANIZED_MESSAGE);
		          setNewContactId(null);
			}else
			{
				Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
			}
			 
			//cdd.findById(dominioName) ;
			
			/*
			     if (userd.save(newUser))
			     {
			    	 
			         for(Integer idSelItem: selValue)
						{
							Item item=mSel.getItem(idSelItem);
						     //item.getClass();
							dname=mSel.getItemCaption(idSelItem);
							domainNames.add(dname);
							
						    cd=cdd.findById(dname);
						    
							if(cd!=null)
								{
								selDomains.add(cd);
								
								
								
								}
								
							System.out.println("item n "+idSelItem+" item value "+dname);
							System.out.flush();
						  }
			         
			         newUser.setDomainsOfMembership(selDomains);
			         
			        // Notification.show("Save", " Nuovo utente salvato!",Notification.Type.HUMANIZED_MESSAGE);
			        //  setNewContactId(null);
			         
			         if (userd.save(newUser))
				     {
			          Notification.show("Save", " Nuovo utente salvato!",Notification.Type.HUMANIZED_MESSAGE);
			          setNewContactId(null);
				     }else
				     {
				    	 Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
				     }
				    
			         
			     }
			     else Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
			*/
		}
	});
}


private void initContactList() {
    
	System.out.println("imposto il container");
	System.out.flush();
	contactList.setContainerDataSource(contactContainer);
    
    
    contactList.setVisibleColumns(new String[] { FNAME, LNAME, EMAIL });
    contactList.setSelectable(true);
    contactList.setImmediate(true);
    contactList.addStyleName(Reindeer.PANEL_LIGHT);

    contactList.addValueChangeListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) 
            {
                    Object contactId = contactList.getValue();
/*
   Binding data
   When a contact is selected from the list, we want to show that in our editor on the right. 
   This is nicely done by the FieldGroup that binds all the fields to the
   corresponding Properties in our contact at once.     
*/
                    
                    
                    System.out.println("contatto selezionato "+contactId );
                    System.out.flush();
                           if (contactId != null)
                           {
                        	   String username;
                        	   editorFields.setItemDataSource(contactList.getItem(contactId));
                        	   Field<TextField> tf= (Field<TextField>) editorFields.getField(ID);
                        	   
                        	   username=(String)contactList.getContainerProperty(contactId , ID).getValue();
                        	   System.out.println("Username selezioanto: "+username);
                        	   System.out.flush();
                        	   findAndFillAllDomainNameOnUser(username);
                        	   setContactId(contactId);
                        	   tf.setEnabled(false);
                        	   Login login=loginUser.get();
                               if (login.getCurrentUser().getUsername().equals("admin") || login.getCurrentDomain().getName().equals("all") )
                               {
                                 composeMenu(username)	;
                                 menuAcces.setVisible(true);
                                
                                 menuAcces.setEnabled(true);
                               
                               }else
                               {
                            	   menuAcces.setVisible(false);
                            	   menuAcces.setEnabled(false);
                               }
                        	   
                        	   
                           }
                            
                   // findAndFillAllDomainNameOnUser(username);
                    editorLayout.setVisible(contactId != null);
                    
            }
    });
}


  protected synchronized void setContactId(Object contactId) {
	selectedId=contactId;
	
}

  protected synchronized Object getContactId() {
		return selectedId;
		
	}


 @SuppressWarnings("unchecked")
private  IndexedContainer createDummyDatasource() {
       IndexedContainer ic = new IndexedContainer();

       for (String p : fieldNames) {
           
    	    ic.addContainerProperty(p, String.class, "");
       }
    /*
       String[] fnames = { "Peter", "Alice", "Joshua", "Mike", "Olivia",
                       "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Rene",
                       "Lisa", "Marge" };
       String[] lnames = { "Smith", "Gordon", "Simpson", "Brown", "Clavel",
                       "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling",
                       "Barks", "Ross", "Schneider", "Tate" };
                       
                       
                       */
   
    
    List<User> users= userd.findAllRetriveEagerCompanyDomain();
    Set<CompanyDomain> cds;
    String multiVal;
  //  { FNAME,LNAME,"Password",EMAIL, "Phone Num"};
        for (User us: users)
        {
        	Object id = ic.addItem();
        	 ic.getContainerProperty(id, FNAME).setValue(us.getUsername());
        	 ic.getContainerProperty(id, LNAME).setValue(us.getName());
        	
        	 ic.getContainerProperty(id, EMAIL).setValue(us.getEmail());
        	 ic.getContainerProperty(id, "Phone Num").setValue(us.getPhoneNumber());
        	 ic.getContainerProperty(id, "Password").setValue(us.getPassword());
        	  ic.getContainerProperty(id, "Verifica Password").setValue(us.getPassword());
        	if ((cds=us.getDomainsOfMembership()) != null)
        	{
        		StringBuffer sb= new StringBuffer("");
        		int i=cds.size();
        		if (i==0)
        		{
        			multiVal="--";
        			
        		}else
        		{	
        			Iterator<CompanyDomain> it=cds.iterator();
        			while(it.hasNext())
        			{
        				sb.append((it.next()).getName()).append(";");
        			}
        		/*	
        		  for (int a =0; a<(i-1) ;a++)
        		  {
        			sb.append(cds.get(a).getName()).append(";");
        		  }
        		  sb.append(cds.get(i-1).getName());
        		  */
        		  multiVal=sb.toString();
        		} 
        	}else {
        		multiVal="--";
        	}
        	  ic.getContainerProperty(id, "Dominio Aziendale").setValue(multiVal);
        }
    
    
     

       return ic;
}









}