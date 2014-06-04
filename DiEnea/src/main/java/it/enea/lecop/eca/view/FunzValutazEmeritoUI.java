package it.enea.lecop.eca.view;

import it.enea.lecop.eca.core.CalcolaIntervento;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.ComposizioneEdificiDao;
import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.data.FunzioneValutazioneDao;
import it.enea.lecop.eca.data.InterventoMigliorativoDao;
import it.enea.lecop.eca.data.MenuItemDao;
import it.enea.lecop.eca.data.ParamInterventoDao;
import it.enea.lecop.eca.data.ProfiloUsoConsumoDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.data.AziendaDao;
import it.enea.lecop.eca.data.ValutazioneDao;
import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.ConsumoEnergetico;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.ParamIntervento;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;

import it.enea.lecop.eca.model.TipologiaConsumi;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaFunzioneDiValutazione;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.model.PermissionProp.secAttrib;
//@@TODO capì import it.enea.lecop.eca.view.;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.collections.map.HashedMap;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
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
@CDIUI(value = "funzVal")
@Theme("mytheme")

public class FunzValutazEmeritoUI extends UI{

	@EJB
	  UserDao userd;
		
	 @Inject
	  private Instance<Login> loginUser;
	  
	 @Inject
	 CalcolaIntervento   calc_d;
	 
	 
	  @Inject
	  MenuItemDao   midao;
	  
	  @Inject
	  FunzioneValutazioneDao   funz_d;
	  
	  @Inject
	  ParamInterventoDao   param_d;
	 
	  
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
	  
	  private Button checkExpressionButton = new Button("Check");
	  private Button saveContactButton = new Button("Save");
	  private Button removeContactButton = new Button("Remove this contact");
	  private FormLayout editorLayout = new FormLayout();
	  private FieldGroup editorFields = new FieldGroup();
	  private HorizontalLayout buttonLayout = new HorizontalLayout();
	  private TwinColSelect select_tipoEdifici= new TwinColSelect(FUNZ_TIPOEDIFICI);
	  private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
	  private VerticalLayout leftLayout = new VerticalLayout();
	  private TextArea expressionTextArea = new TextArea(FUNZ_EXPRESS);
	  private Table parametersTable = new Table();
		 
	  private Map<String, ComboBox>  simbolParamCombo=new HashMap<String, ComboBox>();
	  
	  IndexedContainer contactContainer;
	 
	  private Object newContactId;

	  private Object selectedId;

	  private ComboBox tipoFunzione= new ComboBox(FUNZ_TIPOFUNZ);
	  private ComboBox tipoValutazione= new ComboBox(FUNZ_TIPOVAL);

	  
	  public Object getNewContactId() {
			return newContactId;
	  }


	  public void setNewContactId(Object newContactId) {
			this.newContactId = newContactId;
	  }

	private static final String FUNZ_ID = "Id";
	private static final String FUNZ_NAME = "Name";
	private static final String FUNZ_DESCR = "Descrizione";
	private static final String FUNZ_TIPOVAL= "Valutazione";
	private static final String FUNZ_TIPOFUNZ = "Tipo Funz";
	private static final String FUNZ_EXPRESS = "Expr Funz";
	
	private static final String FUNZ_TIPOEDIFICI = "Tipo Edifici ";
	
	private static final String FUNZ_PARAMS = "Parametri";
	private static final String TABLE_SYMB = "SIMBOLO";
	private static final String TABLE_PARAM = "PARAMETRO";
	
	
	//private static final String USERS = "Users";
	//private static final String COMPANIES = "Companies";

	 
	// id chiave del bean 
	private static final String ID = FUNZ_ID;
	private static final String[] fieldNames = new String[] { FUNZ_ID, FUNZ_NAME,FUNZ_DESCR, FUNZ_TIPOVAL,FUNZ_TIPOFUNZ,FUNZ_TIPOEDIFICI,FUNZ_EXPRESS,FUNZ_PARAMS};
	private static final String[] fieldOnThePanel = new String[] { FUNZ_NAME, FUNZ_DESCR, FUNZ_TIPOVAL, FUNZ_TIPOFUNZ };
	private static final String[] parametersFieldNames = new String[] { TABLE_SYMB, TABLE_PARAM };

	 
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
private final static String WIDTH="100%";

	  private void initEditor() {
	          int i=0;
	          for (String fieldName : fieldNames) {
	                 if (fieldName.equalsIgnoreCase("password"))
	                 {
	                	 PasswordField field = new PasswordField(fieldName);
	                	 editorLayout.addComponent(field,i);
	                     field.setWidth(WIDTH);
	                     editorFields.bind(field, fieldName);
	                 } else if(fieldName.equalsIgnoreCase("Verifica Password"))
	                 {
	                	 PasswordField pwdfield = new PasswordField(fieldName);
	                	 editorLayout.addComponent(pwdfield,i);
	                     pwdfield.setWidth(WIDTH);
	                     editorFields.bind(pwdfield, fieldName);
	                 }
	                 else if(fieldName.equalsIgnoreCase(FUNZ_TIPOEDIFICI))
	                 {
	                	 //ComboBox cb = new ComboBox(fieldName, options)
	                	// composeDomain();
	                	 editorLayout.addComponent(select_tipoEdifici,i);
	                	
	                	 select_tipoEdifici.setLeftColumnCaption("Tutte le tipologie di edificio disponibili ");
	                	 select_tipoEdifici.setRightColumnCaption("Tutte le tipologie di edificio a cui la funzione è applicabile");
	                	 select_tipoEdifici.setWidth(WIDTH);
	                	 select_tipoEdifici.setImmediate(true);
	                	 select_tipoEdifici.setEnabled(false);
	                	 select_tipoEdifici.setVisible(false);
	                	// editorFields.bind(domain, fieldName);
	                	 
	                	 
	                 }
	                 else if (fieldName.equalsIgnoreCase(FUNZ_TIPOVAL)){
			        	  
			        	  editorLayout.addComponent(tipoValutazione,i);
			        	  tipoValutazione.setWidth(WIDTH);
		                
		                  for (TipologiaValutazione idx: TipologiaValutazione.values()){
		                	  tipoValutazione.addItem(idx);
		              
		                	  tipoValutazione.setItemCaption(idx, idx.toString());
		              	  
		                	  
		                  }
	                      
		                  tipoValutazione.setRequired(true);
		                  tipoValutazione.setImmediate(true);
		                  tipoValutazione.setNullSelectionAllowed(false);
	                 	                 
	                 }
	                 else if (fieldName.equalsIgnoreCase(FUNZ_TIPOFUNZ)){
			        	  
			        	  editorLayout.addComponent(tipoFunzione,i);
			        	  tipoFunzione.setWidth(WIDTH);
		                
		                  for (TipologiaFunzioneDiValutazione idx: TipologiaFunzioneDiValutazione.values()){
		                	  tipoFunzione.addItem(idx);
		              
		                	  tipoFunzione.setItemCaption(idx, idx.toString());
		              	  
		                	  
		                  }
	                      
		                  tipoFunzione.setRequired(true);
		                  tipoFunzione.setImmediate(true);
		                  tipoFunzione.setNullSelectionAllowed(false);
	                 	                 
	                 }
	                 
	                 else if(fieldName.equalsIgnoreCase(FUNZ_TIPOEDIFICI))
	                 {
	                	 //ComboBox cb = new ComboBox(fieldName, options)
	                	// composeDomain();
	                	 editorLayout.addComponent(select_tipoEdifici,i);
	                	
	                	 select_tipoEdifici.setLeftColumnCaption("Tutte le tipologie di edificio disponibili ");
	                	 select_tipoEdifici.setRightColumnCaption("Tutte le tipologie di edificio a cui la funzione è applicabile");
	                	 select_tipoEdifici.setWidth(WIDTH);
	                	 select_tipoEdifici.setHeight("50%");
	                	 select_tipoEdifici.setImmediate(true);
	                	 select_tipoEdifici.setEnabled(false);
	                	 select_tipoEdifici.setVisible(false);
	                	// editorFields.bind(domain, fieldName);
	                	 
	                	 
	                 }
	                 else if(fieldName.equalsIgnoreCase(FUNZ_EXPRESS))
	                 {
	                	 
	                	 editorLayout.addComponent(expressionTextArea,i);
		                
	                	 expressionTextArea.setWidth(WIDTH);
	                	 editorFields.bind(expressionTextArea, fieldName);
		                
	                	 
	                 }
	                 
	                 else if(fieldName.equalsIgnoreCase(FUNZ_PARAMS))  {
		   	        	  
			        	 
			        	  editorLayout.addComponent(parametersTable,i);
			        	  
			        	  parametersTable.addContainerProperty(TABLE_SYMB, String.class, null);
			     		  parametersTable.addContainerProperty(TABLE_PARAM, ComboBox.class, null);
			     		      
			        	  
			        	  parametersTable.setVisibleColumns(parametersFieldNames);
		                  
		                 
		                  parametersTable.setWidth(WIDTH);
		                  parametersTable.setEnabled(false);
		                  
		                               
		                
			              
			          }
	                 
	                 else{	 
	                	
	        	      TextField field = new TextField(fieldName);
	                  editorLayout.addComponent(field,i);
	                
	                  field.setWidth(WIDTH);
	                  editorFields.bind(field, fieldName);
	                 }
	                 
	                  i++;
	          }
	             
	          
	          saveContactButton.setEnabled(true);
	          
	          buttonLayout.addComponent(saveContactButton);
	          buttonLayout.addComponent(removeContactButton);
	          buttonLayout.addComponent(checkExpressionButton);
	          editorLayout.addComponent(buttonLayout);
	          editorLayout.addStyleName(Reindeer.PANEL_LIGHT);

	          editorFields.setBuffered(false);
	  }


	
	  
	  protected LinkedHashMap<Integer, String> findAndFillTipoEdificioOnFunction(Long funcId) {
		
		boolean prefillSelected=false;
		
		// TODO TEMP 
		FunzioneDiValutazione function=funz_d.findById(funcId);
		
		if (function!=null){ 
			System.out.println("select Types on ==== Function "+function.getId() + " "+ function.getName());
			prefillSelected=true;
		}
	       
	    
		LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
		
		
		int i=1;  
		select_tipoEdifici.removeAllItems();
		
						  
		if(!prefillSelected){
			for (TipologiaEdifici tipEd_idx: TipologiaEdifici.values()){
				
				select_tipoEdifici.addItem(tipEd_idx.descrizione());
				select_tipoEdifici.setItemCaption(tipEd_idx.descrizione(), tipEd_idx.descrizione());
				
				System.out.println("NO Prefill tipo edificio"+tipEd_idx.descrizione());
				System.out.flush();
				
				  
				item.put(i, tipEd_idx.descrizione());
				i++;
		    }
			
			
			
		}
		
		else{

			for (TipologiaEdifici tipEd_idx: TipologiaEdifici.values()){
				select_tipoEdifici.addItem(tipEd_idx.descrizione());
				select_tipoEdifici.setItemCaption(tipEd_idx.descrizione(), tipEd_idx.descrizione());
				
				System.out.println("prefill tipo edificio"+tipEd_idx.descrizione());
				System.out.flush();
				
			
				Set<TipologiaEdifici> tipiEdificioFunc=funz_d.getAllTipoEdifici(funcId);
					
				
				for(TipologiaEdifici tipiFuncIdx: tipiEdificioFunc){
					System.out.println(tipiFuncIdx.descrizione());
					
					if(tipiFuncIdx.descrizione().equals(tipEd_idx.descrizione())){
						System.out.println(tipEd_idx.descrizione()+" fa parte della funzione "+ function.getName());
						System.out.flush();
						  
						select_tipoEdifici.select(tipEd_idx.descrizione());
						break;
					}else {
						System.out.println(tipEd_idx.descrizione()+" NON fa parte della funzione "+ function.getName());
						System.out.flush();
						  
					}
				}	 
						  
				item.put(i, tipEd_idx.descrizione());
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
	               String haystack = ("" + item.getItemProperty(FUNZ_NAME).getValue()).toLowerCase();
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
	   
		//   System.out.println(" VADA DTRONX");
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
	                  
	            	   Field<TextField> tf=(Field<TextField>) editorFields.getField(FUNZ_ID);
                	   tf.setEnabled(false);
                	   
                	
	            	   // sto creando nuovo
	            	   setDomainId(null);
	                    
	            	   tipoFunzione.select(TipologiaFunzioneDiValutazione.FUNZ_MAT);
	            	   tipoValutazione.select(TipologiaValutazione.TERMICA);
	            	   
	                   findAndFillTipoEdificioOnFunction(null);
	                   select_tipoEdifici.setVisible(true);
	                   select_tipoEdifici.setEnabled(true);
	                    
	                   saveContactButton.setEnabled(false);               
	                   checkExpressionButton.setEnabled(true);
	                   
	                   editorLayout.setVisible(true);
	               	 
	               }
	       });

	       removeContactButton.addClickListener(new ClickListener() {
	               public void buttonClick(ClickEvent event) {
	            	
	            	   //TODO REMOVE FUNCTIONALITY NOT IMPLEMENTED 
	            	   System.out.println("Button Listener removeContact ");
	            	   
	            	   Object contactId = contactList.getValue();
	            	   if(contactId==null){
	            		   return;
	            	   }
	            	   
	                   Login login=loginUser.get();
	                   Item it=  contactList.getItem(contactId);
	                   String domainName=(String) it.getItemProperty(FUNZ_NAME).getValue();
	          
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
	                    	   for(Azienda idx: aziende){
	                    		   System.out.println("Checking Azienda "+idx.getNome());
	                    		   if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
	                    			   
	                    			   System.out.println("Changing Azienda "+idx.getNome()+" owning domain to all" );
		                    	  	                    			   
	                    			   idx.getOwnerid().setOwnCompany(domainAll);
	                    		                  			   
	                    			   idx.setPermissionprop(strict_pp);
	                    			   
	                    			   azd.save(idx);
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
			              	for(ComposizioneEdifici idx: composizioni){
		                    	  System.out.println("Checking Composizioneedifici "+idx.getName());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing Composizioneedifici "+idx.getName()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  compoEd_d.save(idx);
		                    	   }
		                    		   
		                      }
		                    
	                    	   
	                    	 // comuni OK perchè sempre admin e all
	                    	   
	                    	 // Edificio
			              	List<Edificio> edifici=ed_d.findAll();
			              	for(Edificio idx: edifici){
		                    	  System.out.println("Checking edifici "+idx.getNome());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing edifici "+idx.getNome()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  ed_d.save(idx);
		                    	   }
		                    		   
		                      }
              	                       	 
	                    	 
			              	 // TODO intervento migliorativo NON E' UN DAO cherè?
			              	
			              	 // menuitem cannot be created yet from user interface 
			              	
			              	 // TODO ParamIntevento Manca DAO Verificare
			              	
			              	// ProfiloConsumo  	
			              	List<ProfiloUsoConsumo> profiles=profUC_d.findAll();
			              	for(ProfiloUsoConsumo idx: profiles){
		                    	  System.out.println("Checking profiles "+idx.getNome());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())) {
		                   			   
		                   			   System.out.println("Changing profilo uso consumo "+idx.getNome()+" owning domain all" );
			                    	  	                    			   
		                   			   idx.getOwnerid().setOwnCompany(domainAll);
		                  			               			   
		                   			   idx.setPermissionprop(strict_pp);
		                   			   
		                   			   profUC_d.save(idx);                			  
		                   			   
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
			              	for(Valutazione idx: valutazioni){
		                    	  System.out.println("Checking valutazioni "+idx.getDescrizione());
		                    	  if (idx.getOwnerid().getOwnCompany().getName().equals(domain.getName())){
		                    			   
		                    		  System.out.println("Changing valutazioni "+idx.getDescrizione()+" owning domain to all" );
			                    	  	                    			   
		                    		  idx.getOwnerid().setOwnCompany(domainAll);
		                    		                  			   
		                    			   
		                    		  idx.setPermissionprop(strict_pp);
		                    		  valu_d.save(idx);
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
	       });
	       
	       
	       saveContactButton.addClickListener(new ClickListener() {
			
	    	 
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				
				
         	   	System.out.println("Button Listener save Function ");
         	   
				Object funzPanelId = getDomainId();
				Login login=loginUser.get();
				System.out.println("id funzione selezionato selezionato: "+ funzPanelId);
				System.out.flush();
				String idStr;
				Long id;
				
				String nomeFunz;
				String descrizione;
				TipologiaValutazione tipoVal;
				TipologiaFunzioneDiValutazione tipoFunz;
				Set<TipologiaEdifici> tipoEdifici;
				String espressione;
				Map<String,ParamIntervento> parameters=new HashMap<String,ParamIntervento>();
				
				
				
				if (funzPanelId!=null)
				{	
					idStr= (String) contactList.getContainerProperty(funzPanelId , FUNZ_ID).getValue();	  				  
				  
				}
				else
				{
					idStr=null;
							
				}
				nomeFunz=(String) editorFields.getField(FUNZ_NAME).getValue();
				descrizione=(String) editorFields.getField(FUNZ_NAME).getValue();
				tipoVal=(TipologiaValutazione)tipoValutazione.getValue();
				tipoFunz=(TipologiaFunzioneDiValutazione)tipoFunzione.getValue();
				tipoEdifici= (Set<TipologiaEdifici>) select_tipoEdifici.getValue();
				espressione=expressionTextArea.getValue();
				
				
				if(tipoEdifici.size()==0 )
				{
					Notification.show("Selezionare almeno un tipo di edificio", Notification.Type.ERROR_MESSAGE);
					return;
				
				}
			
				if(nomeFunz==null || nomeFunz.trim().equals(""))
				{
					Notification.show("INSERIRE NOME FUNZIONE", Notification.Type.ERROR_MESSAGE);
					return;
				
				}
			
			
				
				for(String keyIdx :simbolParamCombo.keySet()){
					
					String paramName=(String)simbolParamCombo.get(keyIdx).getValue();
					ParamIntervento param= param_d.findByName(paramName);
					if(param==null )
	       			{
	       				Notification.show("Selezionare parametro corrispondende a simbolo: "+ keyIdx, Notification.Type.ERROR_MESSAGE);
	       				return;
		
	       			}
		       
					parameters.put(keyIdx, param);
					
				}
					
						  		
						
						
				if(idStr==null || (idStr.trim().equals(""))){
					id=null;
				}else{
					try{
						id= Long.valueOf(idStr);
					}catch(NumberFormatException nf){
						id=null;
					}
				}
				
				if (!funz_d.save(id,nomeFunz,descrizione,tipoVal,tipoFunz,tipoEdifici,espressione,parameters,loginUser.get().getCurrentDomain(), loginUser.get().getCurrentUser())){
					Notification.show("Salvataggio fallito");
					
				}
				else{
					Notification.show("Salvataggio riuscito");
					
				}
				
				 	
					
			}
	   	});
	       
	       checkExpressionButton.addClickListener(new ClickListener() {
               public void buttonClick(ClickEvent event) {
            	   
            	   TipologiaFunzioneDiValutazione tipofunz=(TipologiaFunzioneDiValutazione)tipoFunzione.getValue();
            	   if(tipofunz==null){
            		   
            		   Notification.show("SELEZIONARE TIPO FUNZIONE", Notification.Type.ERROR_MESSAGE);
   					   return;
   				
            	   }
            	   
            	   Set<String> exprPars= calc_d.getParFromExpr(expressionTextArea.getValue() , tipofunz );
            	   if(exprPars==null){
            		   Notification.show("INSERISCI UNA ESPRESSIONE VALIDA", Notification.Type.ERROR_MESSAGE);
   					   return;
   				
            	   }
            	  
            	             	   
            	   createParamatersDataSource(null,exprPars);
            	   
            	   saveContactButton.setEnabled(true);               
                   checkExpressionButton.setEnabled(false);
                   parametersTable.setEnabled(true);
	               parametersTable.setImmediate(true);   
	                  
               }
       });   
}


	List<ParamIntervento> parameters=new ArrayList<ParamIntervento>();
	protected void populateParamCombo(ComboBox combo) {
		
		 if(parameters.isEmpty()){
			 parameters=param_d.findAll();
			 
			 
		 }
		 
		 for (ParamIntervento par:parameters){
			  combo.addItem(par.getNome());
			  combo.setItemCaption(par.getNome(), par.getNome());
			  System.out.println("NOME PARAMETRO "+ par.getNome());
			  System.out.flush();
		 }
		
	}


	private void initContactList() {
	    
		System.out.println("imposto il container");
		System.out.flush();
		contactList.setContainerDataSource(contactContainer);
	    
	    
	    //contactList.setVisibleColumns(new String[] { FUNZ_NAME,FUNZ_TIPOVAL, FUNZ_TYPE});
		contactList.setVisibleColumns((Object[])fieldOnThePanel);
	    contactList.setSelectable(true);
	    contactList.setImmediate(true);
	    contactList.addStyleName(Reindeer.PANEL_LIGHT);

	    contactList.addValueChangeListener(new Property.ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) 
	            {
	                    Object functionPanelId = contactList.getValue();
	/*
	   Binding data
	   When a contact is selected from the list, we want to show that in our editor on the right. 
	   This is nicely done by the FieldGroup that binds all the fields to the
	   corresponding Properties in our contact at once.     
	*/
	                    FunzioneDiValutazione func;
	                    
	                    System.out.println("funzione selezionato "+functionPanelId );
	                    System.out.flush();
	                    
	                           if (functionPanelId != null)
	                           {
	                        	   String funcIdStr;
	                        	   Long funcId;
	                        	  
	                        	   editorFields.setItemDataSource(contactList.getItem(functionPanelId));
	                        	   Field<TextField> tf= (Field<TextField>) editorFields.getField(ID);
	                        	   
	                        	   setDomainId(functionPanelId);
	                        	   tf.setEnabled(false);
	                        	   
	                        	   
	                        	   
	                        	  
	                        	   
	                        	   
	                        	   funcIdStr=(String)contactList.getContainerProperty(functionPanelId , FUNZ_ID).getValue();
	                        	   System.out.println("Funzione selezionata: "+ funcIdStr );
	                        	   System.out.flush();
	                        	   if(funcIdStr==null || (funcIdStr.trim().equals(""))){
	                        		   funcId=null;
	                        	   }else{
	                        			try{
	                        				funcId= Long.valueOf(funcIdStr);
	                        			}catch(NumberFormatException nf){
	                        				funcId=null;
	                        			}
	                        	   }
	                        	 
	                        	   func = funz_d.findById(funcId);
	                        	   
	                        	   tipoFunzione.select(func.getTipoFunz());	                     			   
	                        	   tipoValutazione.select(func.getApplicaTipoValutazione());
	                        	   
	                        	   findAndFillTipoEdificioOnFunction(funcId);
	                        	   select_tipoEdifici.setVisible(true);
	                        	   select_tipoEdifici.setEnabled(true);                        
	                        	   
	                        	   Set<String> exprPars= calc_d.getParFromExpr(func.getCalcolo() , func.getTipoFunz() );
	                        	   if(exprPars==null){
	                        		   Notification.show("ATTENZIONE ESPRESSIONE NON VALIDA", Notification.Type.ERROR_MESSAGE);
	               					   return;
	               				
	                        	   }
	                        	   
	                        	   createParamatersDataSource(func, exprPars);
	                        	   
	                        	   
	                           }
	                            
	                    saveContactButton.setEnabled(true);               
	    	            checkExpressionButton.setEnabled(false); 
	    	            parametersTable.setEnabled(true);
	                    editorLayout.setVisible(functionPanelId != null);
	                    
	            }
	    });
	    
	    
	    expressionTextArea.addTextChangeListener(new FieldEvents.TextChangeListener(){
	    
			@Override
			public void textChange(TextChangeEvent event) {
				saveContactButton.setEnabled(false);               
                checkExpressionButton.setEnabled(true);
                parametersTable.setEnabled(false);
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
	      
	    
	    List<FunzioneDiValutazione> funzioni= funz_d.findAll();
	    
	    for (FunzioneDiValutazione f_idx: funzioni)
	    {
	    	Object id = ic.addItem();
	      	    	         	 
	    	ic.getContainerProperty(id, FUNZ_ID).setValue(f_idx.getId().toString());
		        
	        ic.getContainerProperty(id, FUNZ_NAME).setValue(f_idx.getName());
	        	 
	        	 
	        ic.getContainerProperty(id, FUNZ_DESCR).setValue(f_idx.getDescrizione());
		    ic.getContainerProperty(id, FUNZ_TIPOVAL).setValue(f_idx.getApplicaTipoValutazione().toString());
		    ic.getContainerProperty(id, FUNZ_TIPOFUNZ).setValue(f_idx.getTipoFunz().toString());
		    ic.getContainerProperty(id, FUNZ_EXPRESS).setValue(f_idx.getCalcolo());
			        
	    }
	    
	    
	     

	       return ic;
	}
	 
	 private void createParamatersDataSource(FunzioneDiValutazione func, Set<String> exprPars)
	{   
		Map<String,ParamIntervento> ParametersMap = null;
		 
		simbolParamCombo.clear();
  	    parametersTable.removeAllItems();
  	    parameters.clear();	
		
  		if(func!=null){
  			ParametersMap=func.getParametri();
  			exprPars=ParametersMap.keySet();
  			
  	   		
  	   		
  	   	}
  	   	
  	   	int i=0;
  	   	for(String simbIdx :exprPars ){
  		   
  	   	   Object itemId = new Integer(i);
   		   ComboBox combo=new ComboBox("seleziona un parametro");
	   
  		   populateParamCombo(combo);
  		   combo.setImmediate(true);
  		   combo.setTextInputAllowed(false);
  		   parametersTable.addItem(new Object[] {simbIdx,combo}, itemId);
  		   
  		   if(func!=null){
  			
  			   System.out.println("Symbol:" + simbIdx + " Param:" + ParametersMap.get(simbIdx));
  			   combo.select(ParametersMap.get(simbIdx).getNome());
  			   
  		   }
  		   
  		   simbolParamCombo.put(simbIdx,combo);
   		 
  		   i++;
   } 
   	
  	   		
  	
	   
  	   	
		
	                        
      
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

	
	
}

