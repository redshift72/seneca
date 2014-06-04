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
import it.enea.lecop.eca.model.ConsumoEnergetico;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaConsumi;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;
import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.model.PermissionProp.secAttrib;
//@@TODO capì import it.enea.lecop.eca.view.;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
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
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
@CDIUI(value = "profili")
@Theme("mytheme")
public class ProfiloUsoConsumoUI extends UI {
	
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
		  
		  protected  TextField totaleConsumo = new TextField("Consumo");
		  private Table profileList = new Table();
		  private TextField searchField = new TextField();
		  private Button addNewContactButton = new Button("Nuovo");
		  protected Button saveContactButton = new Button("Salva");
		  protected Button removeContactButton = new Button("Rimuove questo Profilo");
		  private Button addConsumoButton = new Button("+");
		  private Button delConsumoButton = new Button("-");
		  //private Button savConsumoButton = new Button("v");
		  private HorizontalLayout consumiButtonLayout = new HorizontalLayout();
		  private HorizontalLayout newConsumiRowLayout = new HorizontalLayout();
		  private ComboBox tipoCombustibile= new ComboBox();
		  private ComboBox composEdifici = new ComboBox();
          private TextField unitaMisuraCombustibile= new TextField();
          private TextField valoreConsumo=new TextField();
          private TextField efficienzaConv=new TextField();
         
		  
		  
		  private FormLayout editorLayout = new FormLayout();
		  private FieldGroup editorFields = new FieldGroup();
		  private HorizontalLayout buttonLayout = new HorizontalLayout();
		  private static final String PROF_CONS_TOT_EQ_KWH_T = "Tot Consumo Kwht/Kwhe/m^3";
		  private Table consumiList= new Table("Consumi Kwht/Kwhe/m^3");
		  protected ComboBox tipoProfilo= new ComboBox(PROF_TYPE);
     	 
		  private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		  private VerticalLayout leftLayout = new VerticalLayout();
		
		  //TODO REMOVE
		//  private TwinColSelect select_companies= new TwinColSelect(COMPANIES);
		//  private TwinColSelect select_users= new TwinColSelect(USERS);
	
		  IndexedContainer profileContainer;
		  IndexedContainer consumiContainer;

		  
		  private Object newProfileId;
		  public Object getNewProfileId() {
			  return newProfileId;
		  }
		  public void setNewProfileId(Object newProfileId) {
			  this.newProfileId = newProfileId;
		  }
		  /**
		   * restituisce uno nuovo se non c'è ne gia uno
		   * @return
		   */
		  protected Object newProfile()
		  {
			  Object localNewContactId= getNewProfileId();
			   
			  //System.out.println(" VADA DTRONX");
			  if( localNewContactId==null) 
			  {
				  profileContainer.removeAllContainerFilters();
				  localNewContactId=profileContainer.addItemAt(0);
				  setNewProfileId(localNewContactId);
			  }
				   
			  return localNewContactId;
		  }
			
		  // TODO perchè syncronized?
		  private Object selectedProfileId;
		  protected synchronized void setProfileId(Object profileId) {
			  selectedProfileId=profileId;
				
		  }
		  protected synchronized Object getProfileId() {
			  return selectedProfileId;
					
		  }

		  
		  	  
		
		  /**
		   * restituisce un Id selezionato.  se non c'è uno nuovo
		   * @return
		   */
		  protected Object newConsumo()
		  {
			  Object localnewConsumoId= getSelectedConsumoId();
			   
			  //System.out.println(" VADA DTRONX");
			  if( localnewConsumoId==null) 
			  {
				  //TODO a che serve?
				  //profileContainer.removeAllContainerFilters();
				  localnewConsumoId=consumiContainer.addItem();
				 
			  }
				   
			  return localnewConsumoId;
		  }
			
		  
		  // TODO perchè syncronized?
		  private Object selectedConsumoId;

		private TipologiaValutazione selTipo;

		private Long selProfiloUsoId;
		  protected  void setSelectedConsumoId(Object consumoId) {
			  selectedConsumoId=consumoId;
				
		  }
		  protected Object getSelectedConsumoId() {
			  return selectedConsumoId;
					
		  }


		private static final String PROF_ID = "Id";
		private static final String PROF_NAME = "Nome";
		private static final String PROF_TYPE= "Tipo";
		private static final String PROF_DESCRIZIONE= "Descrizione";
		private static final String PROF_COMPOS = "Comp. Edifici (Azienda)";
		private static final String PROF_CONSUMI = "Consumi";
		private static final String PROF_ORE = "Ore d'uso giornaliere";
		
		//private static final String PROF_CONS_TOT_EQ_KWH_T = "Tot Consumo Kwht/Kwhe/m^3";
		
		private static final String CONSUMO_TYPE = "Tipo";
		private static final String CONSUMO_UM = "U.M.";
		private static final String CONSUMO_VALORE = "Valore";
		private static final String CONSUMO_OBJBIN = "objbin";
		private static final String CONSUMO_EFFICIENZA = "Efficienza";
		
		private static final String EDITOR_WIDTH = "80%";
				
		// TODO TO ELIMINATE
	//	private static final String USERS = "Users";
	//	private static final String COMPANIES = "Companies";

		private static final String[] fieldNames = new String[] {PROF_ID,PROF_NAME,PROF_TYPE,PROF_DESCRIZIONE,PROF_COMPOS,PROF_ORE,PROF_CONS_TOT_EQ_KWH_T,PROF_CONSUMI};
		private static final String[] consumiFieldNames = new String[] {CONSUMO_TYPE,CONSUMO_UM,CONSUMO_VALORE,CONSUMO_EFFICIENZA};
		  
		protected void init(VaadinRequest request)
		     {
		    	  profileContainer=createDummyDatasource();
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
		          
		          leftLayout.addComponent(profileList);
		          HorizontalLayout bottomLeftLayout = new HorizontalLayout();
		          leftLayout.addComponent(bottomLeftLayout);
		          bottomLeftLayout.addComponent(searchField);
		          bottomLeftLayout.addComponent(addNewContactButton);

		          leftLayout.setSizeFull();

		          leftLayout.setExpandRatio(profileList, 1);
		          profileList.setSizeFull();

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
		        	  /*
		                 if (fieldName.equalsIgnoreCase("password"))
		                 {
		                	 PasswordField field = new PasswordField(fieldName);
		                	 editorLayout.addComponent(field,i);
		                     field.setWidth(EDITOR_WIDTH);
		                     editorFields.bind(field, fieldName);
		                 } else if(fieldName.equalsIgnoreCase("Verifica Password"))
		                 {
		                	 PasswordField pwdfield = new PasswordField(fieldName);
		                	 editorLayout.addComponent(pwdfield,i);
		                     pwdfield.setWidth(EDITOR_WIDTH);
		                     editorFields.bind(pwdfield, fieldName);
		                 }
		                else if(fieldName.equalsIgnoreCase("COMPANIES"))
		                 {
		                	 //ComboBox cb = new ComboBox(fieldName, options)
		                	 // composeDomain();
		                	 editorLayout.addComponent(select_companies,i);
		                	
		                	 select_companies.setLeftColumnCaption("All companies available to this Domain");
		                	 select_companies.setRightColumnCaption("All companies belonging to this Domain");
		                	 select_companies.setWidth(EDITOR_WIDTH);
		                	 select_companies.setImmediate(true);
		                	 select_users.setEnabled(false);
		                	 select_users.setVisible(false);
		                	// editorFields.bind(domain, fieldName);
		                	 
		                	 
		                 }
		               
		                 else if(fieldName.equalsIgnoreCase("USERS"))  {
		   	        	  
		                  editorLayout.addComponent(select_users,i);         
			              select_users.setLeftColumnCaption("All users available to this Domain");
			              select_users.setRightColumnCaption("All users belonging to this Domain");
			              select_users.setWidth(EDITOR_WIDTH);
			              select_users.setImmediate(true);
			              select_users.setEnabled(false);
			              select_users.setVisible(false);
			            } 
			              else */  
			            if (fieldName.equalsIgnoreCase(PROF_TYPE)){
			        	  
			        	  editorLayout.addComponent(tipoProfilo,i);
			        	  tipoProfilo.setWidth(EDITOR_WIDTH);
		                
		                  for (TipologiaValutazione idx: TipologiaValutazione.values()){
		                	  tipoProfilo.addItem(idx);
		              
		                	  tipoProfilo.setItemCaption(idx, idx.toString());
		                	  
		                	  
		                  }
		                  
		                  tipoProfilo.setRequired(true);
		                  tipoProfilo.setImmediate(true);
		                  tipoProfilo.setNullSelectionAllowed(false);
		                  
		                 // Bind doesn't make sense except for text field 
		                 //editorFields.bind(tipoProfilo, fieldName); 
			        	
			        	  
			          }else if (fieldName.equalsIgnoreCase(PROF_COMPOS)){
			        	  findAndFillAllComposizioni();
			        	  editorLayout.addComponent(composEdifici,i);
			        	 
			        	  composEdifici.setCaption(PROF_COMPOS);
			        	  composEdifici.setWidth(EDITOR_WIDTH);
		                  
			        	  composEdifici.setRequired(true);
			        	  composEdifici.setImmediate(true);
			        	  composEdifici.setNullSelectionAllowed(false);
			        	  composEdifici.setInputPrompt("Nessuna Composizione selezionata");
	 	                     
			           
		                  //editorFields.bind(composEdifici, fieldName); 
			        	
			        	  
			          }
			          
			          
			          else if(fieldName.equalsIgnoreCase(PROF_CONSUMI))  {
		   	        	  
			        	 
			        	  editorLayout.addComponent(consumiList,i);
			        	  consumiContainer=createConsumiDatasource(null);
			        	   
		                  consumiList.setContainerDataSource(consumiContainer);
           
		                  consumiList.setVisibleColumns(consumiFieldNames);
		                  
		                  consumiList.setSelectable(true);
		                  consumiList.setImmediate(true);
		                  consumiList.addStyleName(Reindeer.PANEL_LIGHT);
		                 
		                
		                  
		                  // Edit-New profile Row 
		                  newConsumiRowLayout.addComponent(tipoCombustibile);
		                  newConsumiRowLayout.addComponent(unitaMisuraCombustibile);
		                  newConsumiRowLayout.addComponent(valoreConsumo);
		                  newConsumiRowLayout.addComponent(efficienzaConv);
		                 
		                  for (TipologiaConsumi idx: TipologiaConsumi.values()){
		                	  tipoCombustibile.addItem(idx);
		              
		                	  tipoCombustibile.setItemCaption(idx, idx.descrizione());
		                	  
		                	  
		                  }
		                  
		                 
		                 		                  
		                  tipoCombustibile.setImmediate(true);
		                  tipoCombustibile.setEnabled(true);
		                  tipoCombustibile.setWidth("95%");
		                  // TODO binding make sense?
		               //  consumiFields.bind(tipoCombustibile, CONSUMO_TYPE);  
		                  
		                  unitaMisuraCombustibile.setImmediate(true);
                          unitaMisuraCombustibile.setValue("U.M.");
		                  unitaMisuraCombustibile.setEnabled(false);
		                  unitaMisuraCombustibile.setWidth("95%");
		                  
		                  
			               
		                  
		                  valoreConsumo.setImmediate(true);
		                  valoreConsumo.setEnabled(true);
		                  valoreConsumo.setWidth("100%");
		                  
		                  
		                  efficienzaConv.setImmediate(true);
		                  efficienzaConv.setEnabled(true);
		                  efficienzaConv.setWidth("100%");
		                  
		                  // TODO binding make sense?       
		                 // consumiFields.bind(valoreConsumo, CONSUMO_VALORE);  
			              
		                 
		                  // Delete Add Save Button Row
		                  consumiButtonLayout.addComponent(addConsumoButton);
		                  consumiButtonLayout.addComponent(delConsumoButton);
		                  delConsumoButton.setEnabled(false);
		                  // consumiButtonLayout.addComponent(savConsumoButton);
		                  
		                  // Full Container?
		                  //VerticalLayout consumiTotLayout =new VerticalLayout();
		                  
		                  
		                  
		                  consumiList.setWidth(EDITOR_WIDTH);
		                  
		                  
		                  
		                  
		                  newConsumiRowLayout.setWidth(EDITOR_WIDTH);
		                  editorLayout.addComponent(newConsumiRowLayout);
		                  
		                  editorLayout.addComponent(consumiButtonLayout);
		                
		                  //consumiTotLayout.setWidth(EDITOR_WIDTH);
		                 // editorLayout.addComponent(consumiTotLayout, i);
		                  
			              
			          }  else if (fieldName.equalsIgnoreCase(PROF_ID))
		                 {
		                	 // disabilito l'id poiche è autogenerato quindi non modificabile
		                	 TextField field = new TextField(fieldName);
			                  editorLayout.addComponent(field,i);
			                  field.setEnabled(false);
			                  System.out.println("prof id disabled");
			                  System.out.flush();
			                  field.setWidth(EDITOR_WIDTH);
			                  editorFields.bind(field, fieldName);
		                 }
			          	   else if (fieldName.equalsIgnoreCase(PROF_CONS_TOT_EQ_KWH_T))
		                 {
		                	 // disabilito poiche è autogenerato quindi non modificabile
		                	// TextField field = new TextField(fieldName);
			                  editorLayout.addComponent(totaleConsumo,i);
			                  totaleConsumo.setEnabled(false);
			                  System.out.println("Cons tot disabled");
			                  System.out.flush();
			                  totaleConsumo.setWidth(EDITOR_WIDTH);
			                  //editorFields.bind(totaleConsumo, fieldName);
		                 }else{	 
			        	      TextField field = new TextField(fieldName);
			                  editorLayout.addComponent(field,i);
			                  System.out.println(fieldName+" enabled");
			                  System.out.flush();
			                  field.setWidth(EDITOR_WIDTH);
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


		
	
		private void initSearch() {

			  /*
		Event handling
		Granularity for sending events over the wire can be controlled. By default simple changes like writing a text in TextField are sent to server with the next Ajax call. You can configure your component to send the changes to server immediately after focus leaves the field. Here we choose to send the text over the wire as soon as user stops writing for short a moment.

		When the event happens, we handle it in the anonymous inner class. You may instead choose to use a separate named controller class. In the end, the preferred application architecture is up to you. */
			  

		          searchField.setInputPrompt("Trova profili");

		          searchField.setTextChangeEventMode(TextChangeEventMode.LAZY);

		          searchField.addTextChangeListener(new TextChangeListener() {
		                  public void textChange(final TextChangeEvent event) {

		                          profileContainer.removeAllContainerFilters();
		                          profileContainer.addContainerFilter(new ContactFilter(event.getText()));
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
		               String haystack = ("" + item.getItemProperty(PROF_NAME).getValue()).toLowerCase();
		               return haystack.contains(needle);
		       }

		       public boolean appliesToProperty(Object id) {
		               return true;
		       }
		}

		   
		private void initAddRemoveButtons() {
			
			final Object contactId;
			
			addConsumoButton.addClickListener(new ClickListener() {
		               @SuppressWarnings("unchecked")
					public void buttonClick(ClickEvent event){
		            	   //System.out.println("Button Listener addConsumo");
		            	        	   	            	  
		            	  
		            	   TipologiaConsumi ou=(TipologiaConsumi)tipoCombustibile.getValue();
		            	   String val=valoreConsumo.getValue();
		            	   String valEfficienza=efficienzaConv.getValue();
		            	   List<ConsumoEnergetico> consumiTabella=recuperaListaConsumiDaTabella();
		            	   if(!addConsumoOnTable(ou, val,valEfficienza,consumiTabella)){
		            		   return;
		            	   }
		            	   
		            		TipologiaValutazione tipoVal=getSelTipologia(); 
			           		if(tipoVal!=null)
			           		{	
			           		 ProfiloUsoConsumo.ConsumoComplessivo cc=ProfiloUsoConsumo.calcoloNuoviConsumi(null, new ConsumoEnergetico(), recuperaListaConsumiDaTabella());
			           		  if (tipoVal.equals(TipologiaValutazione.TERMICA))
			     			  {
			     				
			     				   totaleConsumo.setValue(String.valueOf(cc.getTermico()));
			     				   totaleConsumo.setCaption("Consumo Termico: Kwh T");
			     			  }
			     			 if (tipoVal.equals(TipologiaValutazione.IDRICA))
			     			  {
			     				 totaleConsumo.setValue(String.valueOf(cc.getIdrico()));
			     				 totaleConsumo.setCaption("Consumo Idrico: m^3");
			     				
			     			  }
			     		  	 if (tipoVal.equals(TipologiaValutazione.ELETTRICA))
			     		  	  {
			     		  		totaleConsumo.setValue(String.valueOf(cc.getIdrico()));
			     		  		totaleConsumo.setCaption("Consumo Elettrico: Kwh EL");
			     			  }
			           		 
			           		}

		            	   /*
		            	   if((ou=(TipologiaConsumiEnergetico)tipoCombustibile.getValue())!=null){
		            		  if((val=valoreConsumo.getValue())!=null  && !val.trim().equals("")){
		            			  Double valDoubl;
		            			  
			            		  
			            		  try {
									valDoubl=Double.valueOf(val);
								} catch (Exception e) {
									// TODO: handle exception
									return;
								}
			            		  // TODO da pulire al new
			            		  // da pulire e riempire all update modifica
			            		  //consumiTermici.clear();
			            		  
			            		  Object itemId=getConsumoId();
			            		   if(itemId==null){
				            		  itemId=newConsumo();
				            		} ;
			            		  
			            		  ConsumoEnergetico cons=new ConsumoEnergetico();
			            		  cons.setTipo(ou);
			            		  cons.setValue(valDoubl);
			            		  
			            		  
			            		  consumiContainer.getContainerProperty(itemId, CONSUMO_TYPE).setValue(cons.getTipo().toString());
			            		  consumiContainer.getContainerProperty(itemId, CONSUMO_UM).setValue(cons.getTipo().unitaMisura());
			            		  consumiContainer.getContainerProperty(itemId, CONSUMO_VALORE).setValue(cons.getValue().toString());
			            		  consumiContainer.getContainerProperty(itemId, CONSUMO_OBJBIN).setValue(cons);
			            		  
		            		  }else{
		            			return;  
		            		  }
		            		   
		            	      
		            	   }else {
		            		   //TODO message
		            		   return;
		            	   } 
		            	  
		            	             	
		            	  setNewConsumoId(null);
		                */
		               
		               }
		    });
		
			delConsumoButton.addClickListener(new ClickListener() {
	               public void buttonClick(ClickEvent event){
	            	   System.out.println("Button Listener removeConsumo");
	            	   
	            	   Object itemId= getSelectedConsumoId();
	            	   
	            	
	            	   
	            	   if(itemId!=null){
	            		   consumiContainer.removeItem(itemId);
	            		   setSelectedConsumoId(null);
	            	   }
	            	   
	            		  // segue calcolo provvisorio ed aggiornamento del campo consumo
		           		  // prelevando il riusltato in base alla tipologia valutazione rilevata
	            	      // in questo caso, visto che la tabella è stata gia aggiornata, è sufficiente dare il flag null ed un oggetto consumo non null 
	            	      //  per calcolare su la lista consumi i vari consumi totali aggiornati 
		           		TipologiaValutazione tipoVal=getSelTipologia(); 
		           		if(tipoVal!=null)
		           		{	
		           		 ProfiloUsoConsumo.ConsumoComplessivo cc=ProfiloUsoConsumo.calcoloNuoviConsumi(null, new ConsumoEnergetico(), recuperaListaConsumiDaTabella());
		           		  if (tipoVal.equals(TipologiaValutazione.TERMICA))
		     			  {
		     				
		     				   totaleConsumo.setValue(String.valueOf(cc.getTermico()));
		     				   totaleConsumo.setCaption("Consumo Termico: Kwh T");
		     			  }
		     			 if (tipoVal.equals(TipologiaValutazione.IDRICA))
		     			  {
		     				 totaleConsumo.setValue(String.valueOf(cc.getIdrico()));
		     				 totaleConsumo.setCaption("Consumo Idrico: m^3");
		     				
		     			  }
		     		  	 if (tipoVal.equals(TipologiaValutazione.ELETTRICA))
		     		  	  {
		     		  		totaleConsumo.setValue(String.valueOf(cc.getElettrico()));
		     		  		totaleConsumo.setCaption("Consumo Elettrico: Kwh EL");
		     			  }
		           		 
		           		}
	            	   
	            	   
	            	   
	            	   
	            	   delConsumoButton.setEnabled(false);
	            	   
	               }
	            	        	   	            	  
	        });
			consumiList.addValueChangeListener(new Property.ValueChangeListener() {
	            @SuppressWarnings("unchecked")
				public void valueChange(ValueChangeEvent event) 
	            {
	                    Object itemId = consumiList.getValue();
	                   
	                    // consumo selezionato
	                    if(itemId!=null){
	                    	System.out.println("Consumo selezionato " + itemId );
	                    	System.out.flush();
	                    	ConsumoEnergetico cons= (ConsumoEnergetico)(consumiList.getItem(itemId).getItemProperty(CONSUMO_OBJBIN).getValue());
	                    	tipoCombustibile.select(cons.getTipo());
		                    valoreConsumo.setValue((String)consumiList.getItem(itemId).getItemProperty(CONSUMO_VALORE).getValue());
		                  	efficienzaConv.setValue((String)consumiList.getItem(itemId).getItemProperty(CONSUMO_EFFICIENZA).getValue());
		                    
		                    setSelectedConsumoId(itemId);
	                        
	                        delConsumoButton.setEnabled(true);
	                    	
	                    // consumo deselezionato	
	                    }else{

	                    	System.out.println("Consumo deselezionato " + itemId );
	                    	System.out.flush();
	                    	tipoCombustibile.setValue(null);
		                    valoreConsumo.setValue(null);
		                    efficienzaConv.setValue(null);
		                  	                    
		                    setSelectedConsumoId(null);
	                        
	                        delConsumoButton.setEnabled(false);
	                    	
	                    	
	                    }
	                    
	                    
	            }
			});    
			
			tipoCombustibile.addValueChangeListener(new ValueChangeListener(){

		 			/**
					 * evento onchange sulla combo del tipo di valutazione
					 */
					

					@Override
		 			public void valueChange(ValueChangeEvent event) {
						
						TipologiaConsumi 	tipo=(TipologiaConsumi)tipoCombustibile.getValue();
						if(tipo!=null){
							unitaMisuraCombustibile.setValue(tipo.unitaMisura());
						}else{
							unitaMisuraCombustibile.setValue(null);
						}
						
					}
					
			 });
					
			
						
		       addNewContactButton.addClickListener(new ClickListener() {
		               public void buttonClick(ClickEvent event) {

		/*
		Data model access
		Rows in the Container data model are called Items. Here we add a new row to the beginning of the list.

		Each Item has a set of Properties that hold values. Here we set the value for two of the properties. */
		            	  
		            	   System.out.println("Button Listener AddNew ");
		            	 
		            	   // a che serve?
		            	   profileContainer.removeAllContainerFilters();
		            	   
		            	   Object itemId=newProfile(); 
		            	   
		            	   consumiList.removeAllItems();
		                   
		            	   editorFields.setItemDataSource(profileList.getItem(itemId));
		                  
		            	   findAndFillAllComposizioni();
	                	   	            	   
		            	   // sto creando nuovo
		            	   setProfileId(null);
		                   
		            	   // TODO eliminate 
		            	   // findAndFillAllUserOnDomain(null);
		                   // select_users.setVisible(true);
		                   // select_users.setEnabled(true);
		                    
		                   // findAndFillAllCompaniesOnDomain(null);
		                   // select_companies.setVisible(true);
		                   // select_companies.setEnabled(true);
		                    
		                                  
		                    
		                    editorLayout.setVisible(true);
		                    tipoProfilo.select(null);
		                    //valoreConsumo.setValue("");
		                    totaleConsumo.setValue("0.0");
		                 Field<String>  fieldName= (Field<String>)editorFields.getField(PROF_NAME);
		                 fieldName.setValue("");
							
							((Field<String>) editorFields.getField(PROF_DESCRIZIONE)).setValue("");
							
							((Field<String>)editorFields.getField(PROF_ORE)).setValue("");
		                    
		                    
		                    // va messo dopo
		 	                TextField tf=  (TextField)editorLayout.getComponent(0);
			 	            tf.setEnabled(false);
		               }
		       });

		       removeContactButton.addClickListener(((new MySub(UI.getCurrent(),true,"Cancella",null,"Se sicuro di voler cancellare l'entità selezionata?",3))
		    		   .setListener(new ClickListener() {
		               public void buttonClick(ClickEvent event) {
		            	
		            	   //TODO REMOVE FUNCTIONALITY NOT IMPLEMENTED 
		            	   System.out.println("Button Listener removeContact ");
		            	   
		            	   Object profileId = profileList.getValue();
		            	   
		            	   String idStr;
						   Long id; 
						   ProfiloUsoConsumo profilo;
						  
						 /*  Login login=loginUser.get();
						   System.out.println("id profilo selezionato selezionato: "+ profileId);
						   System.out.flush();
						   */
							
						   if (profileId==null){	
								return;
							   
						   }else{
							   idStr=(String)profileList.getContainerProperty(profileId , PROF_ID).getValue();	  				  
														
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
						   
						   profilo=profUC_d.findById(id);
						   					    
		                   if(profilo!=null){
		                     
		                	   // TODO what about security ???
		                    	   
		                    	// TODO do we need to work under transaction?
		                    	// rollback shall be managed CHECK EXCEPTION HANDLING FROM METHODS
		                    	   
		                    	// potrei prendere il look su user
		                    	   
		                    //	System.out.println("Deleting Profile "+ profilo.getNome());
		                    		  
		                    	//  1) eliminazione da tutte le valutazioni
		                        //  2) cancellazione del profilo
		                    	//  3) cacnellazione dei consumi automatica
		                    	 
		                      // Valutazioni
		                	   
		                	   
		                      List<Valutazione> valutazioni=valu_d.findVal_byProfilo(profilo.getId());
		                      if(valutazioni!=null && !valutazioni.isEmpty() )
		                      { 
		                    	Set<String> nomevalBusy=new HashSet<String>();
		                        for(Valutazione idx: valutazioni)
		                         {
		                    	   //System.out.println("Checking Valutazione "+idx.getDescrizione());
		                    		  
		                    	   
		                    		   nomevalBusy.add("id: "+ String.valueOf(idx.getId())+" '"+idx.getDescrizione()+"'");
		                    	   
		                         }
		                      
		                        if(!nomevalBusy.isEmpty())
		                         {
		                    	        StringBuffer strb= new StringBuffer();
		                    	        for (String val:nomevalBusy) strb.append(" ").append(val);
		                                Notification.show("'"+profilo.getNome()+"' non può essere rimosso perchè in uso da " + nomevalBusy.size()+" Valutazione \n" +
		                                		"Per rimuovere cancellare Valutazioni con "+strb.toString(), 
	                    				Notification.Type.ERROR_MESSAGE);
		                   			   
	                    				return;  
		                          }
		                      }
		                      
		                      if( profUC_d.remove(profilo.getId())){ 
		                    	  refreshContactList();
		                    	  Notification.show("Profilo: " + " rimosso", Notification.Type.HUMANIZED_MESSAGE);
				              }else{
		                    	   Notification.show("Profilo: "+profilo.getNome()+" non posso rimuoverlo", Notification.Type.ERROR_MESSAGE);
		                    	   refreshContactList();
				              }
		                 }
		               
		               
		               }
		       })
		       
		       ));
		      
		       
		       saveContactButton.addClickListener(new ClickListener() {
				
		    	 
				@SuppressWarnings("unchecked")
				@Override
				public void buttonClick(ClickEvent event) {
					
					
	         	   System.out.println("Button Listener save Profilo Uso Consumo ");
	         	   
	         	   	// identifier for container
					Object profileId = getProfileId(); 
					// identifier for DAO long and string versione
					String idStr;
					Long id; 
					String profileName;
					TipologiaValutazione tipo;
					String descr;
					Long composizioneId;
					List<ConsumoEnergetico> consumi=  Collections.synchronizedList(new ArrayList<ConsumoEnergetico>());;
					String oreStr;
					Double ore;
					int numPersone=0;
					String strNumPersone;
					//Login login=loginUser.get();
					System.out.println("id profilo selezionato selezionato: "+ profileId);
					System.out.flush();
					
					// TODO remove
					//Set<String> selectedDomainUsers;
					//Set<String> selectedDomainCompanies;
					//List<Azienda> aziende;
					//List<User> users;
					//List<String> domainUsers=new ArrayList<String>();
					//List<String> domainCompanies=new ArrayList<String>();

					
					if (profileId!=null)
					{	
						// TODO serve qualche altra cosa?
						idStr=(String)profileList.getContainerProperty(profileId , PROF_ID).getValue();	  				  
						/*
						profileName=(String)profileList.getContainerProperty(profileId , PROF_NAME).getValue();	  				  
						tipo=(TipologiaValutazione)profileList.getContainerProperty(profileId , PROF_TYPE).getValue();	  				  
						descr=(String)profileList.getContainerProperty(profileId , PROF_DESCRIZIONE).getValue();	  				  
						composizione=(ComposizioneEdifici)profileList.getContainerProperty(profileId , PROF_COMPOS).getValue();	  				  	
						 */
					}
					else
					{
						// to be onest it should be null...
						//idStr=(String) editorFields.getField(PROF_ID).getValue();
						idStr=null;	
						
					}
					
					profileName=(String) editorFields.getField(PROF_NAME).getValue();
					tipo=(TipologiaValutazione) tipoProfilo.getValue();
					descr=(String) editorFields.getField(PROF_DESCRIZIONE).getValue();
					composizioneId=(Long)composEdifici.getValue();
					oreStr=(String)editorFields.getField(PROF_ORE).getValue();
					
					//consumiList.g
					/*
					List<Object> ids=(List<Object>)consumiContainer.getItemIds();
					ConsumoEnergetico consVal;
					System.out.println("get da containere 1");
					System.out.flush();
					for(Object idx: ids){
						
						consVal=(ConsumoEnergetico)consumiContainer.getContainerProperty(idx, CONSUMO_OBJBIN).getValue();
				        consumi.add(consVal);
					}
					*/
					
					consumi=recuperaListaConsumiDaTabella();
					
					// Selected item list
					//selectedDomainUsers=(Set<String>)select_users.getValue();
					//selectedDomainCompanies=(Set<String>)select_companies.getValue();
					 
					if(profileName==null || profileName.trim().equals(""))
					{
						Notification.show("INSERIRE NOME DI PROFILO", Notification.Type.ERROR_MESSAGE);
						return;
					
					}
					if(tipo==null )
					{
						Notification.show("INSERIRE TIPO", Notification.Type.ERROR_MESSAGE);
						return;
					
					}
					if(composizioneId==null  )
					{
						Notification.show("Inserire composizione", Notification.Type.ERROR_MESSAGE);
						return;
					
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

					if(oreStr==null || (oreStr.trim().equals("") )){
						Notification.show("Inserire ore (0-24)", Notification.Type.ERROR_MESSAGE);
						return;
					
					}else{
						try{
							ore= Double.valueOf(oreStr);
							if(ore>24 || ore<0)
							{
								Notification.show("Inserire ore (0-24)", Notification.Type.ERROR_MESSAGE);
								return;
							
							}
						}catch(NumberFormatException nf){
							Notification.show("Inserire ore (0-24)", Notification.Type.ERROR_MESSAGE);
							return;
						
						}
					}

							
					
					// TODO check security prop assignment
					System.out.println(" sto per salvare");
					System.out.flush();
					if (!profUC_d.save(id, profileName, tipo, descr,composizioneId ,ore, consumi, numPersone,loginUser.get().getCurrentDomain(), loginUser.get().getCurrentUser())){
						
						Notification.show("Salvataggio fallito");
						 
					}
					else{
						Notification.show("Salvataggio riuscito");
						System.out.println(" sto per refreshare");
						System.out.flush();
						setNewProfileId(null);
						consumiList.setVisible(false);
						editorLayout.setVisible(false);
						
						refreshContactList();
						
					}
					
							
				System.out.println(" save completato!!!");
				}
				
		   	});
	}


		
		 public List<ConsumoEnergetico> recuperaListaConsumiDaTabella()
	       {
	    	   List<ConsumoEnergetico> consumi=  Collections.synchronizedList(new ArrayList<ConsumoEnergetico>());
	    		List<Object> ids=(List<Object>)consumiContainer.getItemIds();
				ConsumoEnergetico consVal;
				System.out.println("get da containere 1");
				System.out.flush();
				for(Object idx: ids){
					
					consVal=(ConsumoEnergetico)consumiContainer.getContainerProperty(idx, CONSUMO_OBJBIN).getValue();
			        consumi.add(consVal);
				}
				
				return consumi;
	       }
		

		private void initContactList() {
		    
			System.out.println("imposto il container");
			System.out.flush();
			profileList.setContainerDataSource(profileContainer);
		    
		    profileList.setVisibleColumns(new String[] { PROF_NAME,PROF_TYPE, PROF_COMPOS, PROF_ORE, PROF_CONS_TOT_EQ_KWH_T});
			
			profileList.setSelectable(true);
		    profileList.setImmediate(true);
		    profileList.addStyleName(Reindeer.PANEL_LIGHT);

		    profileList.addValueChangeListener(new Property.ValueChangeListener() {
		            @SuppressWarnings("unchecked")
					public void valueChange(ValueChangeEvent event) 
		            {
		            	Object profileId = profileList.getValue();
		        	 	/*
		        	 	   Binding data
		        	 	   When a contact is selected from the list, we want to show that in our editor on the right. 
		        	 	   This is nicely done by the FieldGroup that binds all the fields to the
		        	 	   corresponding Properties in our contact at once.     
		        	 	*/
		        	 	                    
		        	 	                    
		        	 	System.out.println("entita' selezionata "+profileId );
		        	 	System.out.flush();
		        	 	ProfiloUsoConsumo profilo=null;
		        	 	if (profileId == null){
		        	 		editorLayout.setVisible(false);
		        	 		setSelProfiloUsoId(null);
		        	 		setProfileId(null);

		        	 	}else{
		        	 		Long id;
		        	 		editorFields.setItemDataSource(profileList.getItem(profileId));
		        	 		Field<TextField> tf= (Field<TextField>) editorFields.getField(PROF_ID);
		        	 	    tf.setEnabled(false);
		        	 		
		        	 	    try{
		        	 	    	id=Long.valueOf((String)profileList.getContainerProperty(profileId , PROF_ID).getValue());
		        	 	    }catch(Exception e){
		        	 	    	id=null;
		        	 	    }      
		        	 		
		        	 		if ((id==null) || (profilo=profUC_d.findById(id))==null) {
		        	 			Notification.show("L'entità che si voleva editare e' stata gia' eliminata: ", Notification.Type.ERROR_MESSAGE);
		        	 	        // refresh profiles
		        	 			profileList.removeAllItems();
		        	 			
		        	 	        profileContainer=createDummyDatasource();
		        	 	        
		        	 	       setSelProfiloUsoId(null);
		        	 	       
		        	 	        // todo resed editor data 
		        	 	        return;
		        	 		}
		        	 		boolean enableComp=true;
		        	 		List<Valutazione> result=valu_d.findVal_byProfilo(profilo.getId());
		        	 		if (result != null && !result.isEmpty())
		        	 		{
		        	 			Notification.show("Questo profilo è usato da una valutazione: non puo' essere eliminato", Notification.Type.ERROR_MESSAGE);
		        	 		    removeContactButton.setEnabled(false);
		        	 		    
		        	 		    enableComp=false;
		        	 		    
		        	 		}else
		        	 		{
		        	 			removeContactButton.setEnabled(true);
		        	 		   enableComp=true;
		        	 			
		        	 		}
		        	 		// salvo entità selezionata
		        	 		// e salvo anche le varie selezioni
		        	 		setSelProfiloUsoId(id);
		        	 		setProfileId(profileId);
		        	 		setSelTipologia(profilo.getTipo());
		        	 		
		        	 		// non ho alcun consumo selezionato
		        	 		setSelectedConsumoId(null);
		        	 		
		        	 		//System.out.println("chiave selezionata: "+id);
		        	 		//System.out.flush();
		        	 		
		        	 		
							
							tipoProfilo.select(profilo.getTipo());
						       
									        	 	
		        	 		findAndFillAllComposizioni();
		        	 		composEdifici.select(profilo.getComposizioneEdificio().getId());
		        	 		
		        	 		consumiList.removeAllItems();
		        	 		consumiContainer.removeAllItems();
		        	 		List<ConsumoEnergetico> consumi=profilo.getConsumi();
		        	 		
		        	 		for( ConsumoEnergetico idx: consumi)
		        	 		{
		        	 		  
		        	 		  if(idx.getTipo().tipo().equals(profilo.getTipo()))addConsumoOnTable(idx.getTipo(), idx.getValue().toString(),idx.getEfficienzaConversione().toString(),null);
		        	 		
		        	 		
		        	 		}
		        	 	
		        	 		 tipoCombustibile.removeAllItems();
							 for (TipologiaConsumi idx: TipologiaConsumi.values()){
			                	 
								 if(idx.tipo().equals(profilo.getTipo()))
								 {
									 tipoCombustibile.addItem(idx);
					              
			                	     tipoCombustibile.setItemCaption(idx, idx.descrizione());
								 }
			                	  
			                  }
		        	 		
		        	 		editorLayout.setVisible(true);
		        	 		consumiList.setVisible(true);
		        	 		editorLayout.setEnabled(true);
		        	 		editorFields.setEnabled(true);
		        	 		
		        	 		composEdifici.setEnabled(enableComp);
		        	 		
		        	 		
                            
		        	 		TextField tf2=  (TextField)editorLayout.getComponent(0);
			 	            tf2.setEnabled(false);
			 	            
			 	            
			 	           valoreConsumo.setValue("");
		        	 	   efficienzaConv.setValue("1.0");
		        	 		/*
		        	 		findAndFillAllProfiliName(id, null, null);
		 	               
		 	                findAndFillAllInterventiMigliorativi(id);
		                                           
		 	                composeSelType(profilo.getTipo());
		                                         
		                                              
		                                              
		                    aziende.select(profilo.getAzienda().getNome());
		                                              
		                    findAndFillAllFunzioniValutazione(profilo.getTipo(),id,profilo);
		                    Double risVal=profilo.getRisultatoValutazioneIniziale();
		                    valCalcolato.setValue(risVal==null?"----":risVal.toString());
		        	 	    
		                      findAndFillAllEdificiName(id);
		        	 	    findAndFillAllProfiliName(id); 
		        	 	    findAndFillAllDomainNameOnUser(username);
		        	 	                   
		        	 	    */                    	   
		        	 	    
			 	         TipologiaValutazione tipoVal= profilo.getTipo();
			 	           if (tipoVal.equals(TipologiaValutazione.TERMICA))
			    			  {
			    				
			    				   totaleConsumo.setValue(String.valueOf(profilo.getConsumoTermicoTot()));
			    				   totaleConsumo.setCaption("Consumo Termico: Kwh T");
			    			  }
			    			 if (tipoVal.equals(TipologiaValutazione.IDRICA))
			    			  {
			    				 totaleConsumo.setValue(String.valueOf(profilo.getConsumoAcquaTot()));
			    				 totaleConsumo.setCaption("Consumo Idrico: m^3");
			    				
			    			  }
			    		  	 if (tipoVal.equals(TipologiaValutazione.ELETTRICA))
			    		  	  {
			    		  		totaleConsumo.setValue(String.valueOf(profilo.getConsumoElettricoTot()));
			    		  		totaleConsumo.setCaption("Consumo Elettrico: Kwh EL");
			    			  }
		        	 		
		        	 	}
		        	 	
		        	 			        }
		    });
		    
		    tipoProfilo.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					TipologiaValutazione	tipo =(TipologiaValutazione)event.getProperty().getValue();
					System.out.println("tipo valutazione selezionato: "+tipo);
					System.out.flush();
					if(tipo!=null)
	 				{
						TipologiaValutazione selVal;
					 if ((selVal=getSelTipologia())==null || !selVal.equals(tipo))
					 {
						 // sono diversi
						 System.out.println("Sono diversi: il tipo che ERA selezionato "+selVal);
							System.out.flush();
						 setSelTipologia(tipo);
						
						 
						 setSelectedConsumoId(null);
					//	Object ob= consumiList.addItem();
						
						 consumiList.removeAllItems();
						 consumiContainer.removeAllItems();
						 consumiContainer=createConsumiDatasource(null);
						 consumiList.setContainerDataSource(consumiContainer);
						 consumiList.setVisibleColumns(consumiFieldNames);
						// consumiList.removeItem(ob);
						 tipoCombustibile.removeAllItems();
						 for (TipologiaConsumi idx: TipologiaConsumi.values()){
		                	 
							 if(idx.tipo().equals(tipo))
							 {
								 tipoCombustibile.addItem(idx);
				              
		                	     tipoCombustibile.setItemCaption(idx, idx.descrizione());
							 }
		                	  
		                  }
						 editorLayout.setVisible(true);
						 Long id=getSelProfiloUsoId();
						 if(id!=null)
						 {	 
							 // se sono in una situazione di selezione di entita da lista
							 // devo ripopolare la tabella cosnumi con i nuovi tipi
							 // e mettere il consumo totale pari al nuovo tipo
						   ProfiloUsoConsumo profilo=profUC_d.findById(id);
						   if(profilo!=null)
						   {
							   List<ConsumoEnergetico> consumi=profilo.getConsumi();
			        	 		
							   System.out.println("Recuperato profilo su change tipoValutazione");
							   System.out.flush();
			        	 		for( ConsumoEnergetico idx: consumi)
			        	 		{
			        	 		  
			        	 		  if(idx.getTipo().tipo().equals(tipo))addConsumoOnTable(idx.getTipo(), idx.getValue().toString(),idx.getEfficienzaConversione().toString(),null);
			        	 		
			        	 		
			        	 		}
			        	 		
			        	 		
			        	 		 TipologiaValutazione tipoVal= tipo;
					 	           if (tipoVal.equals(TipologiaValutazione.TERMICA))
					    			  {
					    				
					    				   totaleConsumo.setValue(String.valueOf(profilo.getConsumoTermicoTot()));
					    				   totaleConsumo.setCaption("Consumo Termico: Kwh T");
					    			  }
					    			 if (tipoVal.equals(TipologiaValutazione.IDRICA))
					    			  {
					    				 totaleConsumo.setValue(String.valueOf(profilo.getConsumoAcquaTot()));
					    				 totaleConsumo.setCaption("Consumo Idrico: m^3");
					    				
					    			  }
					    		  	 if (tipoVal.equals(TipologiaValutazione.ELETTRICA))
					    		  	  {
					    		  		totaleConsumo.setValue(String.valueOf(profilo.getConsumoElettricoTot()));
					    		  		totaleConsumo.setCaption("Consumo Elettrico: Kwh EL");
					    			  }
						   }
						   
						 }
						 unitaMisuraCombustibile.setValue("");
		                 valoreConsumo.setValue("");
		                 efficienzaConv.setValue("1.0");
						 
		                 
					 }
						 
						 
	 			      
	 			     /*	
	 				  populateFunzioniValutazioni(tipo, null);
	 				  Valutazione val=getSelVal();
	 				
	 				 findAndFillAllProfiliName(((val==null)?null:val.getId()) , getSelAziendaId(), tipo);
	 				*/
	 				}	
					
					
				}
			});
		    
		}


		  

		 protected void setSelProfiloUsoId(Long id) {
			this.selProfiloUsoId=id;
			
		}
		 
		 protected Long getSelProfiloUsoId() {
				return this.selProfiloUsoId;
				
			}
		protected synchronized void setSelTipologia(TipologiaValutazione tipo) {
			this.selTipo=tipo;
		}
		 
		 protected synchronized TipologiaValutazione  getSelTipologia() {
				return this.selTipo;
			}
		@SuppressWarnings("unchecked")
		private IndexedContainer createConsumiDatasource(Long id)
		{   IndexedContainer ic= new IndexedContainer();
			
		    for (String p : consumiFieldNames) {
	           
   		    ic.addContainerProperty(p, String.class, "");
   		  
   		     
	       }
   			                  
                        
          ic.addContainerProperty(CONSUMO_OBJBIN, Object.class, null);
          
          if (id!=null)
          {
        	 ProfiloUsoConsumo profilo= this.profUC_d.findById(id);
        	 if(profilo!=null)
        	 {
        		 List<ConsumoEnergetico> consumi=profilo.getConsumi();
        		 for(ConsumoEnergetico cons: consumi)
        		 {
        			 Object idx = ic.addItem();
        			 
        			 ic.getContainerProperty(idx, CONSUMO_TYPE).setValue(cons.getTipo().toString());
             		 ic.getContainerProperty(idx, CONSUMO_UM).setValue(cons.getTipo().unitaMisura());
             		 ic.getContainerProperty(idx, CONSUMO_VALORE).setValue(cons.getValue().toString());
             		 ic.getContainerProperty(idx, CONSUMO_EFFICIENZA).setValue(cons.getEfficienzaConversione().toString());
             		 ic.getContainerProperty(idx, CONSUMO_OBJBIN).setValue(cons);
             		  
        		 }
        	 }
        	 
          }
          
          return ic;
		}
		
		
		
		private  IndexedContainer createDummyDatasource() {
		       IndexedContainer ic= new IndexedContainer();
		       for (String p : fieldNames) {
		           
		    	    ic.addContainerProperty(p, String.class, "");
		       }
		      
		    
		    List<ProfiloUsoConsumo> profili= findAllProfiloUsoConsumo_Sec();
		    
		    for (ProfiloUsoConsumo idx: profili)
		    {
		    	Object id = ic.addItem();
		    	// TODO
		       // Set<User> su =idx.getDomainUsers();
		       // Set<Azienda> sa=idx.getAziende();
		        Double valOre;
		    	
		    	ic.getContainerProperty(id, PROF_ID).setValue(idx.getId().toString());
		        
		        ic.getContainerProperty(id, PROF_NAME).setValue(idx.getNome());
		        	        	 
		        ic.getContainerProperty(id, PROF_TYPE).setValue(idx.getTipo().name());
		        ic.getContainerProperty(id, PROF_DESCRIZIONE).setValue(idx.getDescrizione());
		        
		        ic.getContainerProperty(id, PROF_COMPOS).setValue(idx.getComposizioneEdificio().getName());
		     //   ic.getContainerProperty(id, PROF_CONS_TOT_EQ_KWH_T).setValue(idx.getConsumoTermicoTot().toString());
		    
		        if(idx.getTipo()==TipologiaValutazione.TERMICA)
		       {
		        ic.getContainerProperty(id, PROF_ORE).setValue(((valOre=idx.getOreSuGiorniTermico())==null)?"0.0":valOre.toString());
		     
		        ic.getContainerProperty(id, PROF_CONS_TOT_EQ_KWH_T).setValue(idx.getConsumoTermicoTot().toString());
		       }else if (idx.getTipo()==TipologiaValutazione.ELETTRICA)
		       {
		    	   ic.getContainerProperty(id, PROF_ORE).setValue(((valOre=idx.getOreSuGiorniElettrico())==null)?"0.0":valOre.toString());  
		    	   ic.getContainerProperty(id, PROF_CONS_TOT_EQ_KWH_T).setValue(idx.getConsumoElettricoTot().toString());
		       
		       }else if (idx.getTipo()==TipologiaValutazione.IDRICA)
		       {
		    	   ic.getContainerProperty(id, PROF_ORE).setValue(((valOre=idx.getOreSuGiorniAcqua())==null)?"0.0":valOre.toString());
		    	   ic.getContainerProperty(id, PROF_CONS_TOT_EQ_KWH_T).setValue(idx.getConsumoAcquaTot().toString()); 
		       }
		        //ic.getContainerProperty(id, PROF_CONSUMI).setValue(idx.getConsumi());
				        
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
		 private   List<ProfiloUsoConsumo>  findAllProfiloUsoConsumo_Sec()
		{
			
			Login login=loginUser.get();
			List<ProfiloUsoConsumo> secAllProfili;
			SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
			   
			secAllProfili= profUC_d.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
		
			return secAllProfili;
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
		
		
		// TODO what about to migrate in DAO??
		private List<ComposizioneEdifici> findAllComposizioni_Sec(){
			Login login=loginUser.get();
			
			//TODO check security property logic
			SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			
			
			List<ComposizioneEdifici> composizioni=compoEd_d.findAll_sec(login.getCurrentUser().getUsername(),login.getCurrentDomain(), userSec, domainSec, subDomainSec , otherSec);
			
			return composizioni;
			
		}
		
		/**
		 * aggiungo composizioni alla combo
		 * Non aggiungo composizioni di edifici con zero edifici
		 * Se il nome della composizione è nullo o vuoto, aggiungo come label
		 * il suo id il num di edifici e al luogo in cui sono situti gli stabili
		 */
		private void findAndFillAllComposizioni() {
			
			composEdifici.removeAllItems();
			
			List<ComposizioneEdifici> composizioni;
			composizioni= findAllComposizioni_Sec();
			String compName;
			int ned;
			for (ComposizioneEdifici idx: composizioni){
				ned=idx.getEdifici().size();
				if(ned==0) continue;
				composEdifici.addItem(idx.getId());
				compName=idx.getName();
				if((compName==null) || compName.trim().equals(""))
				{
				 compName="id: "+idx.getId()+" #ed: "+ned+" in "+idx.getComuneUbicazione().getDescrizione();
				    
				}
				compName=compName+" ("+idx.getAzienda().getNome()+")";
				composEdifici.setItemCaption(idx.getId(), compName);
             
            	  
			}
		}
		
		@SuppressWarnings({ "unchecked", "unchecked" })
		private boolean addConsumoOnTable(TipologiaConsumi tipo, String val, String valEfficienza,List<ConsumoEnergetico> consumi)
		{
						
      	   if(tipo!=null)
      	    {
      		 if((val!=null  && valEfficienza!=null) &&  !val.trim().equals("") && !valEfficienza.trim().equals("") )
      		  {	 
      			  Double valDoubl;
      			  Double valEff;  
          		  try {
						valDoubl=Double.valueOf(val);
					} catch (Exception e) {
						Notification.show("Inserire Valore Consumo Valido", Notification.Type.WARNING_MESSAGE);
						
						return false;
					}
          		  
          		  try {
						valEff=Double.valueOf(valEfficienza);
					} catch (Exception e) {
						Notification.show("Inserire Valore di Conversione Valido del tipo x.x", Notification.Type.WARNING_MESSAGE);
						
						return false;
					} 
          		  
          		  
          		  
          		  
          		  Object itemId=getSelectedConsumoId();
          		  if(itemId==null){
          			  itemId=newConsumo();
          		  }
          		  
          		  ConsumoEnergetico cons=new ConsumoEnergetico();
          		  cons.setTipo(tipo);
          		  cons.setValue(valDoubl);
          		  cons.setEfficienzaConversione(valEff);
          		  
          		  
          		  // segue calcolo provvisorio ed aggiornamento del campo consumo
          		  // prelevando il riusltato in base alla tipologia valutazione rilevata
          		TipologiaValutazione tipoVal=getSelTipologia(); 
          		if(tipoVal!=null && consumi!=null)
          		{	
          		 ProfiloUsoConsumo.ConsumoComplessivo cc=ProfiloUsoConsumo.calcoloNuoviConsumi(true, cons, consumi);
          		  if (tipoVal.equals(TipologiaValutazione.TERMICA))
    			  {
    				
    				   totaleConsumo.setValue(String.valueOf(cc.getTermico()));
    				   totaleConsumo.setCaption("Consumo Termico: Kwh T");
    			  }
    			 if (tipoVal.equals(TipologiaValutazione.IDRICA))
    			  {
    				 totaleConsumo.setValue(String.valueOf(cc.getIdrico()));
    				 totaleConsumo.setCaption("Consumo Idrico: m^3");
    				
    			  }
    		  	 if (tipoVal.equals(TipologiaValutazione.ELETTRICA))
    		  	  {
    		  		totaleConsumo.setValue(String.valueOf(cc.getElettrico()));
    		  		totaleConsumo.setCaption("Consumo Elettrico: Kwh EL");
    			  }
          		 
          		}
          		
          		  
          		  
          		  consumiContainer.getContainerProperty(itemId, CONSUMO_TYPE).setValue(tipo.toString());
          		  consumiContainer.getContainerProperty(itemId, CONSUMO_UM).setValue(tipo.unitaMisura());
          		  consumiContainer.getContainerProperty(itemId, CONSUMO_VALORE).setValue(valDoubl.toString());
          		  consumiContainer.getContainerProperty(itemId, CONSUMO_EFFICIENZA).setValue(valEff.toString());
          		  consumiContainer.getContainerProperty(itemId, CONSUMO_OBJBIN).setValue(cons);
          		  
          		  // calcolo consumi termico totale e setting del valore sull 'interfaccia (NO DB)
          		  /*double valTot;
          		  valTot =Double.valueOf((String)editorFields.getField(PROF_CONS_TOT_EQ_KWH_T).getValue());
          		  valTot=valTot+valDoubl*tipo.fattore2KWH_T();
          		  ((TextField)editorFields.getField(PROF_CONS_TOT_EQ_KWH_T)).setValue((String)String.valueOf(valTot));
          		  */
          		
      		  }else{
      			Notification.show("Inserire Valore Consumo Valido", Notification.Type.WARNING_MESSAGE);
				
      			return false;  
      		  }
      		   
      	      
      	   }else {
      			Notification.show("Selezionare Tipo Consumo ", Notification.Type.WARNING_MESSAGE);
				
      		   return false;
      	   } 
      	  
      	   
			return true;
		}
	 	
		private synchronized void refreshContactList()
		   {
			   //entityList.setVisible(false);
			   consumiList.setVisible(false);
			   profileContainer.removeAllItems();
			   
	  	       profileContainer=createDummyDatasource();
	  	       initContactList();
		   }
}





