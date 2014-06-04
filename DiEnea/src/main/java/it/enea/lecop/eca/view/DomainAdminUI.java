package it.enea.lecop.eca.view;

import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.ComposizioneEdificiDao;
import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.data.InterventoMigliorativoDao;
import it.enea.lecop.eca.data.MenuItemDao;
import it.enea.lecop.eca.data.ProfiloUsoConsumoDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.data.AziendaDao;
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
import it.enea.lecop.eca.model.PermissionProp.secAttrib;
//@@TODO capì import it.enea.lecop.eca.view.;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
@CDIUI(value = "domadm")
@Theme("mytheme")

public class DomainAdminUI extends UI{

	@EJB
	  UserDao userd;
	
	 @Inject
	  private Instance<Login> loginUser;
	  
	  
	  @Inject
	  MenuItemDao   midao;
	  
	  // here we are
	  @Inject
	  AziendaDao   azd;
	  
	  @Inject
	  CompanyDomainDao cdd;
	  
	  @Inject
	  ComposizioneEdificiDao compoEd_d;
	  
	  @Inject
	  EdificioDao ed_d;
	  
	  @Inject
	  InterventoMigliorativoDao intMigl_d;
	  
	  @Inject
	  ProfiloUsoConsumoDao profUC_d;
	  
	  @Inject
	  ValutazioneDao valu_d;
	  
	  
	  private Table contactList = new Table();
	  private TextField searchField = new TextField();
	  private Button addNewContactButton = new Button("New");
	  private Button saveContactButton = new Button("Save");
	  private Button removeContactButton = new Button("Remove this contact");
	  private FormLayout editorLayout = new FormLayout();
	  private FieldGroup editorFields = new FieldGroup();
	  private HorizontalLayout buttonLayout = new HorizontalLayout();
	  private TwinColSelect select_companies= new TwinColSelect(COMPANIES);
	  private TwinColSelect select_users= new TwinColSelect(USERS);
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


	private static final String DOM_NAME = "Domain Name";
		private static final String  NUM_USERS= "#Users";
	private static final String NUM_COMPANIES = "#Companies";
	private static final String USERS = "Users";
	private static final String COMPANIES = "Companies";

	 
	// id chiave del bean 
	  private static final String ID = DOM_NAME;
	  private static final String[] fieldNames = new String[] { DOM_NAME,NUM_USERS,NUM_COMPANIES,USERS,COMPANIES};
	  
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
	                 else if(fieldName.equalsIgnoreCase("COMPANIES"))
	                 {
	                	 //ComboBox cb = new ComboBox(fieldName, options)
	                	// composeDomain();
	                	 editorLayout.addComponent(select_companies,i);
	                	
	                	 select_companies.setLeftColumnCaption("All companies available to this Domain");
	                	 select_companies.setRightColumnCaption("All companies belonging to this Domain");
	                	 select_companies.setWidth("70%");
	                	 select_companies.setImmediate(true);
	                	 select_companies.setEnabled(false);
	                	 select_companies.setVisible(false);
	                	// editorFields.bind(domain, fieldName);
	                	 
	                	 
	                 }
	                 
	                 else if(fieldName.equalsIgnoreCase("USERS"))  {
	   	        	  
	                  editorLayout.addComponent(select_users,i);         
		              select_users.setLeftColumnCaption("All users available to this Domain");
		              select_users.setRightColumnCaption("All users belonging to this Domain");
		              select_users.setWidth("70%");
		              select_users.setImmediate(true);
		              select_users.setEnabled(false);
		              select_users.setVisible(false);
		             
		              
		          }           
	                 
	                 
	                 else{	 
	        	      TextField field = new TextField(fieldName);
	                  editorLayout.addComponent(field,i);
	                
	                  field.setWidth("70%");
	                  editorFields.bind(field, fieldName);
	                 }
	                  i++;
	          }
	             
	          
	          saveContactButton.setEnabled(true);
	          
	          buttonLayout.addComponent(saveContactButton);
	          buttonLayout.addComponent(removeContactButton);
	          editorLayout.addComponent(buttonLayout);
	          editorLayout.addStyleName(Reindeer.PANEL_LIGHT);

	          editorFields.setBuffered(false);
	  }


	
	  
	  protected LinkedHashMap<Integer, String> findAndFillAllCompaniesOnDomain(String domName) {
		
		boolean prefillSelected=false;
		
		CompanyDomain domain=cdd.findById(domName);
		System.out.println("select Companies on ==== DOMINIO "+domName);
		if (domain!=null) prefillSelected=true;
		
	       
	    
		LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
		
		// recupero oggetti che possono essere modificati e controllati no solo lettura
		List<Azienda> aziende=findAllCompany_Sec();	
		
		int i=1;  
		select_companies.removeAllItems();
		

     				  
						  
		if(!prefillSelected){
			for (Azienda azidx: aziende){
				
				select_companies.addItem(azidx.getNome());
				select_companies.setItemCaption(azidx.getNome(), azidx.getNome());
				
				System.out.println("NO prefill Azienda "+azidx.getNome());
				System.out.flush();
				
				  
				item.put(i, azidx.getNome());
				i++;
		    }
			
			
			
		}else{
			
			for (Azienda azidx: aziende){
				select_companies.addItem(azidx.getNome());
				select_companies.setItemCaption(azidx.getNome(), azidx.getNome());
				
				System.out.println("Azienda "+azidx.getNome());
				System.out.flush();
				
			
				Set<CompanyDomain> cdazienda=azidx.getDomainsOfAziendaMembership();
				for(CompanyDomain cdIdx:cdazienda){
					System.out.println(azidx.getNome());
					System.out.println(cdIdx.getName());
					
					if(cdIdx.getName().equals(domName)){
						System.out.println(azidx.getNome()+" fa parte del dominio "+cdIdx.getName());
						System.out.flush();
						  
						select_companies.select(azidx.getNome());
						break;
					}else {
				   	 System.out.println("  NON fa parte del dominio "+cdIdx.getName());
					 System.out.flush();
					}
				}	 
						  
				item.put(i, azidx.getNome());
				i++;
		    }
			
			
		}
		
		
	
		return item;
	}
	
	protected LinkedHashMap<Integer, String> findAndFillAllUserOnDomain(String domName) {
		
		boolean prefillSelected=false;
		
		CompanyDomain domain=cdd.findById(domName);
		System.out.println("select users on ==== DOMINIO "+domName);
		if (domain!=null) prefillSelected=true;
		
		
	       
	    
		LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
		
		// recupero oggetti che possono essere modificati e controllati no solo lettura
		List<User> users= findAllUser_Sec();
		int i=1;  
		select_users.removeAllItems();
		

       		  
		if(!prefillSelected){
			for (User usidx: users){
				
				select_users.addItem(usidx.getUsername());
				select_users.setItemCaption(usidx.getUsername(), usidx.getUsername());
				
				System.out.println("NO prefill User "+usidx.getUsername());
				System.out.flush();
				
				  
				item.put(i, usidx.getName());
				i++;
		    }
			
			
			
		}else{
			
			for (User usidx: users){
				select_users.addItem(usidx.getUsername());
				select_users.setItemCaption(usidx.getUsername(), usidx.getUsername());
				
				System.out.println("User "+usidx.getUsername());
				System.out.flush();
				
			
				Set<CompanyDomain> cduser=usidx.getDomainsOfMembership();
				
				for(CompanyDomain cdu:cduser){
					System.out.println(usidx.getUsername());
					System.out.println(cdu.getName());
					
					if(cdu.getName().equals(domName)){
						System.out.println(usidx.getUsername()+" fa parte del dominio "+cdu.getName());
						System.out.flush();
						select_users.select(usidx.getUsername());
						break;
					}else{
				   	 	System.out.println(usidx.getUsername()+"  NON fa parte del dominio "+cdu.getName());
				   	 	System.out.flush();
				   	 	
					}
					 
				}		  
				item.put(i, usidx.getName());
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
	               String haystack = ("" + item.getItemProperty(DOM_NAME).getValue()).toLowerCase();
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
	   
		   System.out.println(" VADA DTRONX");
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
	            	  
	            	   System.out.println("Button Listener AddNew ");
	            	 
	            	   contactContainer.removeAllContainerFilters();
	            	   Object itemId=newItem(); 
	                
	                   
	            	   editorFields.setItemDataSource(contactList.getItem(itemId));
	                  
	            	   Field<TextField> tf=(Field<TextField>) editorFields.getField(NUM_USERS);
                	   tf.setEnabled(false);
                	   
                	   tf=(Field<TextField>) editorFields.getField(NUM_COMPANIES);
                	   tf.setEnabled(false);
	            	   
	            	   // sto creando nuovo
	            	   setDomainId(null);
	                    
	            	   
	                   findAndFillAllUserOnDomain(null);
	                   select_users.setVisible(true);
	                   select_users.setEnabled(true);
	                    
	                   findAndFillAllCompaniesOnDomain(null);
	                   select_companies.setVisible(true);
	                   select_companies.setEnabled(true);
	                    
	                                  
	                    
	                    editorLayout.setVisible(true);
	               	 
	               }
	       });

	       removeContactButton.addClickListener(((new MySub(UI.getCurrent(),true,"Delete",null,"Se sicuro di voler cancellare l'entità selezionata?",3))
	    		   .setListener(new ClickListener() {
	               public void buttonClick(ClickEvent event) {
	            	
	            	   //TODO REMOVE FUNCTIONALITY NOT IMPLEMENTED 
	            	   System.out.println("Button Listener removeContact ");
	            	   
	            	   Object contactId = contactList.getValue();
	            	   if(contactId==null){
	            		   return;
	            	   }
	            	   
	            	   //MySub sub = new MySub(UI.getCurrent(),true,"Delete",null,"Se sicuro di voler cancellare L'entità selezionata?",3);
	            	  
	            	   //if(sub.getResult()==4) return;
	            	   
	                   Login login=loginUser.get();
	                   Item it=  contactList.getItem(contactId);
	                   String domainName=(String) it.getItemProperty(DOM_NAME).getValue();
	          
	                   CompanyDomain domain=cdd.findById(domainName);
	                
	                       if(domain!=null)
	                       {
	                    	   // all non può essere rimosso
	                    	   if(domain.getName().equalsIgnoreCase("all")){
	                    		   
	                    		   Notification.show("Il Dominio 'all' non può essere cancellato", Notification.Type.ERROR_MESSAGE);
	           						return;
	                    		   
	                    	   }
	                    	   // TODO what about security ???
	                    	   
	                    	   // TODO do we need to work under transaction?
	                    	   // rollback shall be managed CHECK EXCEPTION HANDLING FROM METHODS
	                    	   
	                    	   // potrei prendere il look su user
	                    	   
	                    	   System.out.println("Deleting Domain "+domainName);
	                    		  
	                    	   //cancellazione in 4 fasi
	                    	   //  0) passaggio di ownership di tutti gli ogetti posseduti ad admin
	                    	   //  1) eliminazione da tutti gli utenti
	                    	   //  2) eliminazione da tutte le aziende
	                    	   //  3) cancellazione del dominio
	                    	   CompanyDomain domainAll=cdd.findById("all");
	                    	   PermissionProp strict_pp= new PermissionProp(SecAttrib.CONTROL, SecAttrib.READ , SecAttrib.NONE, SecAttrib.NONE, SecAttrib.NONE);                   	   
	                    	   // azienda
	                    	   List<Azienda> aziende=azd.findAll();
	                    	   if(aziende!=null)
	                    	   {	   
	                    	   for(Azienda idx: aziende){
	                    		   System.out.println("Checking Azienda "+idx.getNome());
	                    		   if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
	                    			   
	                    			   System.out.println("Changing Azienda "+idx.getNome()+" owning domain to all" );
		                    	  	                    			   
	                    			   idx.getOwnerid().setOwnCompany(domainAll);
	                    		                  			   
	                    			   idx.setPermissionprop(strict_pp);
	                    			   
	                    			   azd.save(idx);
	                    		   }
	                    		   
	                    	   }
	                    	   }
	                    	 //TODO  calcolo economico Manca DAO Verificare
	                    	   
	                    	  //companyDomanin 
	                    	   List<CompanyDomain> domains=cdd.findAll();
	                    	   for(CompanyDomain idx: domains){
	                    		   System.out.println("Checking Dominio "+idx.getName());
	                    		   if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
	                    			   
	                    			   System.out.println("Changing Domain "+idx.getName()+" owning domain to all" );
		                    	  	                    			   
	                    			   idx.getOwnerid().setOwnCompany(domainAll);
	                    			   idx.setPermissionprop(strict_pp);          			   
	                    			   
	                    			  
	                    			   cdd.save(idx);
	                    		   }
	                    		   
	                    	   }
	                    	  
	                        //composizione edifici  
			              	List<ComposizioneEdifici> composizioni=compoEd_d.findAll();
			              	if(composizioni!=null)
			              	{	
			              	 for(ComposizioneEdifici idx: composizioni){
		                    	  System.out.println("Checking Composizioneedifici "+idx.getName());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing Composizioneedifici "+idx.getName()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  compoEd_d.save(idx);
		                    	   }
		                    		   
		                      }
			              	}
	                    	   
	                    	 // comuni OK perchè sempre admin e all
	                    	   
	                    	 // Edificio
			              	List<Edificio> edifici=ed_d.findAll();
			              	if(edifici!=null)
			              	{	
			              	 for(Edificio idx: edifici){
		                    	  System.out.println("Checking edifici "+idx.getNome());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing edifici "+idx.getNome()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  ed_d.save(idx);
		                    	   }
		                    		   
		                      }
			              	}          	 
	                    	 
			              	 // TODO intervento migliorativo NON E' UN DAO cherè?
			              	
			              	 // menuitem cannot be created yet from user interface 
			              	
			              	 // TODO ParamIntevento Manca DAO Verificare
			              	
			              	// ProfiloConsumo  	
			              	List<ProfiloUsoConsumo> profiles=profUC_d.findAll();
			              	
			              	// potrebbe non esserci alcun profilo uso consumo
			              	if(profiles!=null)
			              	{	
			              	 for(ProfiloUsoConsumo idx: profiles)
			              	 {
		                    	  System.out.println("Checking profiles "+idx.getNome());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())) {
		                   			   
		                   			   System.out.println("Changing profilo uso consumo "+idx.getNome()+" owning domain all" );
			                    	  	                    			   
		                   			   idx.getOwnerid().setOwnCompany(domainAll);
		                  			               			   
		                   			   idx.setPermissionprop(strict_pp);
		                   			   
		                   			   profUC_d.save(idx);                			  
		                   			   
		                   		    }
	                    		   
		                       }
	                       }
			              	
			              	// TODO Risultato Valutazione NON E' UN DAO cherè?
			              	// TODO non ha neppure sicurezza settata
			              	
			              	// TODO TipoEdificio  NON E' UN DAO cherè? 
			              
			            	//  User 	
			              	List<User> users=userd.findAll();
			              	for(User idx: users){
		                    	  System.out.println("Checking users "+idx.getName());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing users "+idx.getName()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  userd.save(idx);
		                    	   }
		                    		   
		                      }
		             
			              	//  Valutazione 
			             	List<Valutazione> valutazioni=valu_d.findAll();
			             	if(valutazioni!=null)
			             	{	
			              	 for(Valutazione idx: valutazioni)
			              	 {
		                    	  System.out.println("Checking valutazioni "+idx.getDescrizione());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing valutazioni "+idx.getDescrizione()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  valu_d.save(idx);
		                    	   }
		                    		   
		                       }
			             	}
		             
			              	  if( userd.removeDomainFromAll(domainName, login.getCurrentUser().getUsername(),login.getCurrentDomain().getName()) 
	                    			  &&  azd.removeDomainFromAll(domainName, login.getCurrentUser().getUsername(),login.getCurrentDomain().getName())
	                    			  &&  cdd.remove(domainName))
	                    	  {
	                    	   		contactList.removeItem(contactId);
	                    	   		Notification.show("Dominio: "+domainName+" rimosso", Notification.Type.HUMANIZED_MESSAGE);
	                    	   }else
	                    	   {
	                    		   Notification.show("Dominio: "+domainName+" non posso rimuoverlo", Notification.Type.ERROR_MESSAGE);
	                    	   }
	                    		  
	                    	  
	                    	  
	                    	  
	                    	  
	                     }else
	                       {
	                    	 contactList.removeItem(contactId);
	                    	   // Notification.show("User: "+username+" non posso rimuovere", Notification.Type.ERROR_MESSAGE);
	                       }
	                       
	                       
	               }
	       })
	       
	       ));
	       
	       
	       saveContactButton.addClickListener(new ClickListener() {
			
	    	 
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				
				
         	   System.out.println("Button Listener save Domain ");
         	   
				Object contactId = getDomainId();
				Login login=loginUser.get();
				System.out.println("id contact selezionato selezionato: "+ contactId);
				System.out.flush();
				String domainName;
				Set<String> selectedDomainUsers;
				Set<String> selectedDomainCompanies;
				List<Azienda> aziende;
				List<User> users;
				List<String> domainUsers=new ArrayList<String>();
				List<String> domainCompanies=new ArrayList<String>();

				
				if (contactId!=null)
				{	
				  domainName=(String)contactList.getContainerProperty(contactId , DOM_NAME).getValue();	  				  
				  
				}
				else
				{
					domainName=(String) editorFields.getField(DOM_NAME).getValue();
							
				}
				
				// Selected item list
				selectedDomainUsers=(Set<String>)select_users.getValue();
				selectedDomainCompanies=(Set<String>)select_companies.getValue();
				 
				if(domainName==null || domainName.trim().equals(""))
				{
					Notification.show("INSERIRE NOME DI DOMINIO", Notification.Type.ERROR_MESSAGE);
					return;
				
				}
						
				aziende= findAllCompany_Sec();
				users= findAllUser_Sec();
				for (Azienda azid: aziende)
				{
					domainCompanies.add(azid.getNome());
				}
				
				for (User usid: users)
				{
					domainUsers.add(usid.getUsername());
				}
				
				
				if (!cdd.saveCompanyDomain(domainName, selectedDomainCompanies, selectedDomainUsers,login, domainCompanies,domainUsers) ){
					Notification.show("Salvataggio fallito");
					
				}
				else{
					Notification.show("Salvataggio riuscito");
					
				}
				
						
					
			}
	   	});
}



	private void initContactList() {
	    
		System.out.println("imposto il container");
		System.out.flush();
		contactList.setContainerDataSource(contactContainer);
	    
	    
	    contactList.setVisibleColumns(new String[] { DOM_NAME,NUM_USERS, NUM_COMPANIES});
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
	                        	   String domName;
	                        	  
	                        	   editorFields.setItemDataSource(contactList.getItem(contactId));
	                        	   Field<TextField> tf= (Field<TextField>) editorFields.getField(ID);
	                        	   
	                        	   setDomainId(contactId);
	                        	   tf.setEnabled(false);
	                        	   
	                        	   tf=(Field<TextField>) editorFields.getField(NUM_USERS);
	                        	   tf.setEnabled(false);
	                        	   
	                        	   tf=(Field<TextField>) editorFields.getField(NUM_COMPANIES);
	                        	   tf.setEnabled(false);
	                        	   
	                        	   
	                        	   domName=(String)contactList.getContainerProperty(contactId , ID).getValue();
	                        	   System.out.println("Dominio selezionato: "+domName);
	                        	   System.out.flush();
	                        	   
	                        	   findAndFillAllUserOnDomain(domName);
	                        	   
	                        	   
	                        	   
	                        	   findAndFillAllCompaniesOnDomain(domName);
	                        	   select_companies.setVisible(true);
	                        	   select_companies.setEnabled(true);
	                        	   
	                        	   select_users.setVisible(true);
	                        	   select_users.setEnabled(true);
	                               
	                        	   
	                        	   
	                           }
	                            
	                   
	                    editorLayout.setVisible(contactId != null);
	                    
	            }
	    });
	}


	  protected synchronized void setDomainId(Object domainId) {
		selectedId=domainId;
		
	}

	  protected synchronized Object getDomainId() {
			return selectedId;
			
		}


	 @SuppressWarnings("unchecked")
	private  IndexedContainer createDummyDatasource() {
	       IndexedContainer ic = new IndexedContainer();

	       for (String p : fieldNames) {
	           
	    	    ic.addContainerProperty(p, String.class, "");
	       }
	      
	    
	    List<CompanyDomain> domains= findAllCompanyDomain_Sec();
	    
	    for (CompanyDomain dm: domains)
	    {
	    	Object id = ic.addItem();
	        Set<User> su =dm.getDomainUsers();
	        Set<Azienda> sa=dm.getAziende();
	  
	    	         	 
	      	 
	        ic.getContainerProperty(id, DOM_NAME).setValue(dm.getName());
	        	 
	        	 
	        ic.getContainerProperty(id, NUM_USERS).setValue((su==null)?"--":String.valueOf(su.size()));
		    ic.getContainerProperty(id, NUM_COMPANIES).setValue((sa==null)?"--":String.valueOf(sa.size()));
			        
	    }
	    
	    
	     

	       return ic;
	}

	//TODO Security Methods to be moved in the right classes
		
	 
	 /**
	   * recupera tutti i dpmini che,dall'utente di login attuale, possono essere modificati o controllati; non recupera quelli che possono essere solo letti
	   * Tali entità potrebbero avere relazioni a 2 vie, quindi la modifica della relazione da un lato potrebbe
	   * significare una modifica della realzione anche dall'altro lato, quindi trattasi di modifica 
	   * @return
	   */
	 private   List<CompanyDomain>  findAllCompanyDomain_Sec()
	{
		
		Login login=loginUser.get();
		List<CompanyDomain> secAllDomain;
		SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   
		secAllDomain= cdd.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
	
		return secAllDomain;
	}

	private   List<Azienda>  findAllCompany_Sec()
	{
		
		Login login=loginUser.get();
		SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		
		List<Azienda> aziende=azd.findAll_sec(login.getCurrentUser().getUsername(),login.getCurrentDomain(), userSec, domainSec, subDomainSec , otherSec);
				
		
		return aziende;
	}

	
	private   List<User>  findAllUser_Sec()
	{
		Login login=loginUser.get();
		SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		
		
		List<User> users=userd.findAll_sec(login.getCurrentUser().getUsername(),login.getCurrentDomain(), userSec, domainSec, subDomainSec , otherSec);
		
		return users;
	}
}
