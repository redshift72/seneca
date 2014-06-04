package it.enea.lecop.eca.view;

import it.enea.lecop.eca.core.CalcolaIntervento;
import it.enea.lecop.eca.core.EvaluatorFuzzyMerit;
import it.enea.lecop.eca.data.AziendaDao;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.ComposizioneEdificiDao;
import it.enea.lecop.eca.data.ComuneDao;
import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.data.FunzioneValutazioneDao;
import it.enea.lecop.eca.data.ProfiloUsoConsumoDao;
import it.enea.lecop.eca.data.RisultatoValutazioneInterventoDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.data.ValutazioneDao;
import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.InterventoMigliorativo;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.util.CheckParRecursion;
import it.enea.lecop.eca.util.MathUtil;
//import it.enea.lecop.eca.view.CdiVaadinCompEdificiUI.ContactFilter;

import java.util.ArrayList;
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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;

@CDIUI(value = "ValUI")
@Theme("mytheme")
public class ValutazioneUI extends UI {

	 @EJB
	   UserDao userdao;
	 	  
	 @Inject
	  CompanyDomainDao cdd;
	 	  
	
	@Inject
	EvaluatorFuzzyMerit fuzzyeval;
	 	  
	@Inject
	RisultatoValutazioneInterventoDao risdao;
	 	  
	@Inject
	   ComposizioneEdificiDao ced;
	 	  
	@Inject 	  
	  ValutazioneDao valdao; 
	
	@Inject
	CalcolaIntervento calcola;
	 	  
	 	  
	 	  @Inject
	   ProfiloUsoConsumoDao pusodao;
	 	  
	 	  @Inject
	   AziendaDao azdao;
	 	  
	
	 	  
	 	  @Inject
	  	FunzioneValutazioneDao fvdao;  
	 	  
	 	  @Inject
	   Instance<Login> loginUser;
	 	  
	 	  
	 	  private Table entityList = new Table();
	 	  private TextField searchField = new TextField();
	 	  private Button addNewEntityButton = new Button("Nuovo");
	 	  private Button saveEntityButton = new Button("Salva");
	 	  private Button removeEntityButton = new Button("Rimuove questa Valutazione");
	 	  private FormLayout editorLayout = new FormLayout();
	 	  private FieldGroup editorFields = new FieldGroup();
	 	  private HorizontalLayout buttonLayout = new HorizontalLayout();
	 	  
	 	  private HorizontalLayout calcolaLayout = new HorizontalLayout();
	 	  
	 	  private CalculatePanel valoreCalcolato= new CalculatePanel();
	 	  
	 	  
	 	
	 	  // profili in uso selezionabili in linea al tipo di valutazione
	 	  private ComboBox selProfiloUso= new ComboBox("Profilo Uso");
	 	  
	 	  private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
	 	  private VerticalLayout leftLayout = new VerticalLayout();
	 	  // selezione delle azinde possibili
	 	  private ComboBox aziende= new ComboBox("Seleziona una Azienda");
	 	  
	 	  // selezione il tipo di valutazione
	 	 private ComboBox tipoValutazione = new ComboBox("Seleziona un tipo di Valutazione");
	 	 
	 	 // seleziona la funzione di valutazione iniziale 
	 	 private ComboBox selFunzioneValutazioneIniziale= new ComboBox("Seleziona una Funz. di valutazione iniziale");
	 	 private Button calcolaButton= new Button("Calcola");
	 	
	 	 private TextField valCalcolato= new TextField();
	 	 
	 	 private TextField valMerito= new TextField();
              IndexedContainer contactContainer;

	 		private Object newContactId;

	 		private Object selectedId;

			private Valutazione selVal;
			
			private TipologiaValutazione selTipologia;
			private Azienda selAzienda;
			private String selAziendaId;

			private Long selFunzValId;

			private Long selProfiloId;
			

	 	  public synchronized String getSelAziendaId() {
				return selAziendaId;
			}

			public synchronized void setSelAziendaId(String selAziendaId) {
				this.selAziendaId = selAziendaId;
			}

		public synchronized Azienda getSelAzienda() {
				return selAzienda;
			}

			public synchronized void setSelAzienda(Azienda selAzienda) {
				this.selAzienda = selAzienda;
			}

		public synchronized TipologiaValutazione getSelTipologia() {
				return selTipologia;
			}

			public synchronized void setSelTipologia(TipologiaValutazione selTipologia) {
				this.selTipologia = selTipologia;
			}

		public Object getNewContactId() {
	 			return newContactId;
	 		}

      public void provaMetEL()
      {
    	  /*
    	//get current EL context
    	  javax.el.ELContext elContext = javax.faces.context.FacesContext.getCurrentInstance().getELContext();
    	   
    	  //get the expression factory (for seam). You can probably do ExpressionFactory.newInstance() if not using seam .
    	  javax.el.ExpressionFactory expressionFactory = javax.el.ExpressionFactory.newInstance();
    	   
    	  //Create value expression as the EL I'm evaluating is a value e.g. #{bean.property} . Create MethodExpression if the EL is a method e.g. #{bean.method()}
    	  javax.el.ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, "", WhateverYouAreExpecting.class);
    	   
    	  // get value and dont' forget to cast.
    	  whateverYouAreExpecting = (WhateverYouAreExpecting) valueExpression.getValue(elContext);
    	  
    	  */
      }
	 		public void setNewContactId(Object newContactId) {
	 			this.newContactId = newContactId;
	 		}

          
	 	private static final String FNAME = "Id";
	 	  private static final String TYPE = "Tipo Val";
	 	  private static final String NOTE = "Descrizione";
	 	  
	 	  private static final String AZIENDA ="Azienda";
	 	  
	 	  private static final String FUNZ_VAL_INIT="Funz Iniziale";
	 	  // id chiave del bean 
	 	  private static final String ID = "Id";
	 	  
	 	  private static final String INTERVENTI_MIGL="Valore Calcolato";
	 	  private static final String PROFILO_USO="Profilo Uso(Composizione)";
	 	  
	 	  private static final String CREATO="Creato Il";
	 	  private static final String RISULTATO_VAL_INIT="Risultato";
	 	  public static final String MERITO="Merito 0-10";
	 	  private static final String WIDTH="90%";
	 	  
	 	  private static final String[] fieldNames = new String[] { FNAME,NOTE,CREATO, AZIENDA,TYPE,PROFILO_USO,FUNZ_VAL_INIT,INTERVENTI_MIGL,RISULTATO_VAL_INIT,MERITO};
	 	  
	 	       protected void init(VaadinRequest request) {
	 	    	
	 	    	   contactContainer= createDummyDatasource();
	 	    	  initLayout();
	 	          initEntityList();
	 	          initEditor();
	 	          initSearch();
	 	          initAddRemoveButtons();
	 	          initComponentEvent();
	 	          // gestione della sessione scaduta 
	 	          ViewUtils.setNewHandleErrorMessage();
	 	          
	 	          /*
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
	  	 				
	  	 				*/
	 	  }

	 	       
	 	       
	 	       

	 	   /**
	 	    * inizializza gli eventi dei componenti (no bottoni)    
	 	    */
	 	  private void initComponentEvent() {
	 		  
	 		  
	 		  // selezionando un tipo inizializzo le funzioni di valutazione in base hai tipi
	 		 tipoValutazione.addValueChangeListener(new ValueChangeListener(){

	 			/**
				 * evento onchange sulla combo del tipo di valutazione
				 */
				

				@Override
	 			public void valueChange(ValueChangeEvent event) {
	 				TipologiaValutazione	tipo =(TipologiaValutazione)event.getProperty().getValue();
	 				if(tipo!=null)
	 				{
	 			      setSelTipologia(tipo);
	 				
	 				  populateFunzioniValutazioni(tipo, null);
	 				  Valutazione val=getSelVal();
	 				
	 				 findAndFillAllProfiliName(((val==null)?null:val.getId()) , getSelAziendaId(), tipo);
	 				}	
	 			}
	 			  
	 		  });
				
	 		 
	 		 this.selProfiloUso.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					// TODO Auto-generated method stub
					Object obj;
					Long profiloId=null;
					if((obj=selProfiloUso.getValue())!=null)
					{
						profiloId=(Long)obj;
					}
					
					setSelProfiloId(profiloId);
					populateFunzioniValutazioni(getSelTipologia(), null);
				}
			});
	 		 
	 		 
	 		 this.selFunzioneValutazioneIniziale.addValueChangeListener(new ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					
					Object obj;
					Long funzId=null;
					if ((obj=selFunzioneValutazioneIniziale.getValue()) != null)
					{
						funzId=(Long)obj;
						
					}
					setSelFunzValId(funzId);
				}
			});
	 		 
	 		 
	 		 
			}

		protected void setSelProfiloId(Long profiloId) 
		{
			selProfiloId=profiloId;
		}
	
		protected Long getSelProfiloId() 
		{
			return selProfiloId;	
		}
		
		protected void setSelFunzValId(Long funzId) {
			selFunzValId=funzId;
			
		}

		
		protected Long getSelFunzValId()
		{
			return selFunzValId;
		}
		private void initLayout() {

	 	          
	 	          setContent(splitPanel);

	 	          
	 	          splitPanel.addComponent(leftLayout);
	 	          splitPanel.addComponent(editorLayout);
	 	          splitPanel.addStyleName(Reindeer.PANEL_LIGHT);
	 	          
	 	          leftLayout.addComponent(entityList);
	 	          HorizontalLayout bottomLeftLayout = new HorizontalLayout();
	 	          leftLayout.addComponent(bottomLeftLayout);
	 	          bottomLeftLayout.addComponent(searchField);
	 	          bottomLeftLayout.addComponent(addNewEntityButton);

	 	          leftLayout.setSizeFull();

	 	          leftLayout.setExpandRatio(entityList, 1);
	 	          entityList.setSizeFull();

	 	          bottomLeftLayout.setWidth("100%");
	 	          searchField.setWidth("100%");
	 	          bottomLeftLayout.setExpandRatio(searchField, 1);

	 	          editorLayout.setMargin(true);
	 	          editorLayout.setVisible(false);
	 	         
	 	         calcolaButton.setDescription("Calcola Parametri");
	 	         calcolaButton.setCaption("Calcola");
	 	        calcolaButton.setWidth("80%");
	 	        valCalcolato.setWidth("80%");
	 	       valCalcolato.setDescription("Risultato della Valutazione");
	 	        valMerito.setWidth("80%");
	 	        valMerito.setDescription("Prestazione di Merito da 0 a 10");
	 	          calcolaLayout.addComponent(calcolaButton);
	 	          calcolaLayout.addComponent(valCalcolato);
	 	          calcolaLayout.addComponent(valMerito);
	 	  }

	 	/*
	 	Dynamic forms
	 	User interface can be created dynamically to reflect the underlying data. We use a FieldGroup to bind components to a data source.

	 	We choose to write changes through to data source instead of buffering and committing explicitly.*/

	 	  
	 	  /**
	 	   * crea l'editor con i componenti
	 	   * 
	 	   * { FNAME,NOTE,CREATO, AZIENDA,TYPE,"Profili Uso",FUNZ_VAL_INIT,INTERVENTI_MIGL};
	 	   */
	 	  private void initEditor() {
	 	          int i=0;
	 	          for (String fieldName : fieldNames) {
	 	                 if (fieldName.equalsIgnoreCase(FUNZ_VAL_INIT))
	 	                 {
	 	                	//populateFunzioniValutazioni();
	 	                	 
	 	                	 
	 	                	 editorLayout.addComponent(selFunzioneValutazioneIniziale,i);
	 	                     selFunzioneValutazioneIniziale.setWidth(WIDTH);
	 	                     selFunzioneValutazioneIniziale.setRequired(true);
	 	                     selFunzioneValutazioneIniziale.setImmediate(true);
	 	                     selFunzioneValutazioneIniziale.setTextInputAllowed(true);
	 	                     selFunzioneValutazioneIniziale.setNullSelectionAllowed(false);
	 	                     selFunzioneValutazioneIniziale.setInputPrompt("Nessuna Funzione selezionata");
	 	                     TextField tf= new TextField();
	 	                     tf.setPropertyDataSource(new Property<String>() {

	 							@Override
	 							public String getValue() {
	 								// TODO Auto-generated method stub
	 								return (String)selFunzioneValutazioneIniziale.getValue();
	 										
	 							}

	 							@Override
	 							public void setValue(String newValue)
	 									throws com.vaadin.data.Property.ReadOnlyException {
	 								
	 								
	 								
	 							}

	 							@Override
	 							public Class<? extends String> getType() {
	 								// TODO Auto-generated method stub
	 								return String.class;
	 							}

	 							@Override
	 							public boolean isReadOnly() {
	 								// TODO Auto-generated method stub
	 								return false;
	 							}

	 							@Override
	 							public void setReadOnly(boolean newStatus) {
	 								// TODO Auto-generated method stub
	 								
	 							}
	 						});
	 	                     //comuni.addValidator(validator);
	 	                     editorFields.bind(tf, fieldName);
	 	                     
	 	                     
	 	                 } else if(fieldName.equalsIgnoreCase(AZIENDA))
	 	                 {
	 	                	 populateAziende();
	 	                	 
	 	                	 editorLayout.addComponent(aziende,i);
	 	                	 aziende.setRequired(true);
	 	                	 aziende.setImmediate(true);
	 	                	 aziende.setTextInputAllowed(true);
	 	                     aziende.setWidth(WIDTH);
	 	                     aziende.setNullSelectionAllowed(false);
	 	                     aziende.setInputPrompt("Nessuna azienda selezionata");
	 	                     
	 	                     editorFields.bind(aziende, fieldName);
	 	                 }
	 	                 else if(fieldName.equalsIgnoreCase(RISULTATO_VAL_INIT))
	 	                 {
	 	                	// ComboBox cb = new ComboBox(fieldName);
	 	                	 
	 	                	 //composeSelEdifici();
	 	                	// editorLayout.addComponent(cb,i);
	 	                	
	 	                	calcolaLayout.setCaption(fieldName);
	 	                	calcolaLayout.setWidth(WIDTH);
	 	                	//calcolaLayout.addComponent(valoreCalcolato);
	 	                	editorLayout.addComponent(calcolaLayout,i);
	 	                	
	 	                	//valoreCalcolato.setVisible(true);
	 	                	//valoreCalcolato.setEnabled(true);
	 	                	
	 	                	valCalcolato.setEnabled(false);
	 	                	//valMerito.setEnabled(false);
	 	                	/*
	 	                	 multiSelInterventiMigliorativi.setLeftColumnCaption("All Edifici for this composition");
	 	                	 multiSelInterventiMigliorativi.setRightColumnCaption("Edifici which this composition belongs");
	 	                	 multiSelInterventiMigliorativi.setWidth("70%");
	 	                	 multiSelInterventiMigliorativi.setImmediate(true);
	 	                	 */
	 	                	// editorFields.bind(domain, fieldName);
	 	                	 
	 	                	 
	 	                 }else if(fieldName.equalsIgnoreCase(TYPE))
	 	                 {
	 	                	 //ComboBox cb = new ComboBox(fieldName, options)
	 	                	 
	 	                	 composeSelType(null);
	 	                	// composeSelEdifici();
	 	                	 editorLayout.addComponent(tipoValutazione,i);
	 	                	 
	 	                	
	 	                	 
	 	                	
	 	                	
	 	                	tipoValutazione.setRequired(true);
	 	                	tipoValutazione.setImmediate(true);
	 	                	tipoValutazione.setTextInputAllowed(true);
	 	                	tipoValutazione.setWidth(WIDTH);
	 	                	tipoValutazione.setNullSelectionAllowed(false);
	 	                	tipoValutazione.setInputPrompt("Nessun tipo selezionato");
	 	                	
	 	                	
	 	                	// editorFields.bind(domain, fieldName);
	 	                	 
	 	                	 
	 	                 }               
	 	                 else if (fieldName.equalsIgnoreCase(PROFILO_USO))
	 	                 {   
	 	                	 composeSelProfili();
	 	                	 editorLayout.addComponent(selProfiloUso,i);
	 		                	
	 	                	selProfiloUso.setVisible(true);
	 	                	 selProfiloUso.setWidth(WIDTH);
	 	                	 selProfiloUso.setImmediate(true);
	 	                 
	 	                 } else if (fieldName.equalsIgnoreCase(MERITO))
	 	                		 {
	 	                	       valMerito.setEnabled(false);
	 	                	       continue;
	 	                		 }
	 	                 else if (fieldName.equalsIgnoreCase(ID))
	 	                 {
	 	                	 // disabilito l'id poiche è autogenerato quindi non modificabile
	 	                	 
	 	                	 
	 	                	 TextField field = new TextField(fieldName);
	 		                  editorLayout.addComponent(field,i);
	 		                  field.setEnabled(false);
	 		                  field.setWidth(WIDTH);
	 		                  editorFields.bind(field, fieldName);
	 	                 }else if (fieldName.equalsIgnoreCase(INTERVENTI_MIGL))
	                		 {
	                	       //valMerito.setEnabled(false);
	 	                	 continue;
	                		 }
	 	                 else
	 	                 {	 
	 	        	      TextField field = new TextField(fieldName);
	 	                  editorLayout.addComponent(field,i);
	 	                
	 	                  field.setWidth(WIDTH);
	 	                  editorFields.bind(field, fieldName);
	 	                 }
	 	                  i++;
	 	          }
	 	          
	 	          
	 	          
	 	          saveEntityButton.setEnabled(true);
	 	          
	 	          buttonLayout.addComponent(saveEntityButton);
	 	          buttonLayout.addComponent(removeEntityButton);
	 	          editorLayout.addComponent(buttonLayout);
	 	          editorLayout.addStyleName(Reindeer.PANEL_LIGHT);
	               addStyleName(Reindeer.PANEL_LIGHT);
	 	          editorFields.setBuffered(false);
	 	          
	 	          
	 	         aziende.addValueChangeListener( new Property.ValueChangeListener() {
	 	            private static final long serialVersionUID = -5188369735622627751L;

	 	            public void valueChange(ValueChangeEvent event) {
	 	               
	 	            	String aziendaId; 
	 	            	if ((aziendaId=(String) aziende.getValue()) != null) {
	 	                   setSelAziendaId(aziendaId);
	 	                	
	 	                    selProfiloUso.removeAllItems();
	 	                    Valutazione val=getSelVal();
	 	                   findAndFillAllProfiliName(((val==null)?null:val.getId()),aziendaId , getSelTipologia());
	 	                    
	 	                }
	 	            }
	 	        });
	 	          
	 	  }

	@SuppressWarnings("serial")
	private void composeSelType(TipologiaValutazione selTipo) {
			
		
		for(TipologiaValutazione tipo : TipologiaValutazione.values())
		{
			tipoValutazione.addItem(tipo);
			tipoValutazione.setItemCaption(tipo, tipo.name());
		}
		
		  if(selTipo!=null)tipoValutazione.select(selTipo);
		  
		  
		  
		
		}

 	  private void populateFunzioniValutazioni(TipologiaValutazione tipo,Long selId) {
	 		
	 		  Login login=loginUser.get();
	 		 List<Object[]>  funz=null;
	 		 List<FunzioneDiValutazione> funzTipo=null;
	 		TipologiaEdifici tipoed=null;
	 		  Long proId;
	 		  if ((proId=getSelProfiloId())!=null)
	 		  {
	 			 ProfiloUsoConsumo prof=this.pusodao.findById(proId);
	 			 if(prof!=null)
	 			 {
	 				ComposizioneEdifici comp= prof.getComposizioneEdificio();
	 				if(comp!=null)
	 				{	
	 				  tipoed=comp.getTipo();
	 				  
	 				  System.out.println(">>TIPO EDIFICIO PROFILO:>>: "+tipoed.toString());
	 				 System.out.flush();
	 				 if(tipo!=null)
	 				 {	 
	 				  System.out.println(">>TIPO VALUTAZIONE :>>: "+tipo.toString());
	 				  System.out.flush();
	 				  funz=this.fvdao.findForSelectedByTipo(tipo, tipoed);
	 				 }
	 				}
	 			 }
	 		  }
	 		 
	 		  // filtro solo per tipo di valutazione
	 		//if (funz==null)funz=this.fvdao.findForSelectedByTipo(tipo);
	 		
	 		
	 		funzTipo=this.fvdao.findByTipoValutazione(tipo);
	 		  /*
	 			 List<Comune> secAllProfili;
	 			 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 			   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 			   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 			   secAllProfili= this.comdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain().getName(), userSec, domainSec,otherSec);
	 			*/  
	 		selFunzioneValutazioneIniziale.removeAllItems();
	 		
	 		   /*
	 			   for (Object[] obj: funz)
	 			   {
	 				   selFunzioneValutazioneIniziale.addItem((Long)obj[0]);
	 				   selFunzioneValutazioneIniziale.setItemCaption((Long)obj[0], (String)obj[2]+" ("+(String)obj[1]+") ");
	 			   }
	 			 */
	 		
	 		if (tipoed==null)
	 		{
	 		for (FunzioneDiValutazione obj: funzTipo)
			   {
	 			   
				   selFunzioneValutazioneIniziale.addItem((Long)obj.getId());
				   selFunzioneValutazioneIniziale.setItemCaption((Long)obj.getId(), (String)obj.getDescrizione()+" ("+(String)obj.getName()+") ");
			   }
	 		}else
	 		{
	 			for (FunzioneDiValutazione obj: funzTipo)
				   {
		 			   if(obj.getApplicaTipoEdifici().contains(tipoed))
		 			   {   
					    selFunzioneValutazioneIniziale.addItem((Long)obj.getId());
					    selFunzioneValutazioneIniziale.setItemCaption((Long)obj.getId(), (String)obj.getDescrizione()+" ("+(String)obj.getName()+") ");
		 			   }
		 		}
	 		}
	 			   if(selId!=null)selFunzioneValutazioneIniziale.select(selId);
	 				   
	 	}

 	  
 	  

	 	private void populateAziende() {
	 		Login login=loginUser.get();
	 		 List<Azienda> secAllProfili;
	 		 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 		  SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 		   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
	 		   secAllProfili= this.azdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomainSec,otherSec);
	 		  
	 		   for (Azienda a: secAllProfili)
	 		   {
	 			   aziende.addItem(a.getNome());
	 			   aziende.setItemCaption(a.getNome(), a.getNome());
	 		   }
	 		   
	 		
	 	}


	 	private void composeSelProfili() {
	 		
	 		  findAndFillAllProfiliName(null,null,null); 
	 	}



	 	
	  	  
	 	 /**
	 	   * recupera tutti i ProfiloUsoConsumo che,dall'utente di login attuale, possono essere modificati o controllati; non recupera quelli che possono essere solo letti
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
	 		   secAllProfili= this.pusodao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
	 	        
	 		   
	 		   return secAllProfili;
	 	}
	 	
	 
	 	
	 	
	 	  /**
	 	   * recupera tutti dati salienti delle funzioni di valutazione per mettere nella select
	 	   * @param id
	 	   */
	 	  protected void findAndFillAllFunzioniValutazione(TipologiaValutazione tp, Long id,Valutazione val)
	 	  {
	 		List<Object[]>  multiVal= fvdao.findForSelectedByTipo(tp);
	 		boolean prefillSelected=false;
	 		 LinkedHashMap<Long, String>  item= new LinkedHashMap<Long, String>();
	 		 
	 		//Valutazione val=null;
	 		// if(id!=null)
	 		 {
	 			// val=this.getSelVal();
	 			prefillSelected=(val!=null);
	 			
	 		 }
	 		 selFunzioneValutazioneIniziale.removeAllItems();
	 		 for(Object[] obj:multiVal)
 			 {
 				selFunzioneValutazioneIniziale.addItem((Long)obj[0]);
 				selFunzioneValutazioneIniziale.setItemCaption((Long)obj[0], (String)obj[1]);
 				item.put((Long)obj[0], (String)obj[1]);
 			 }
	 		 if (prefillSelected)
	 		 {
	 			
	 			selFunzioneValutazioneIniziale.select(val.getValutazioneIniziale().getId());
	 			 
	 		 }
	 		 
	 		
	 		 
	 		 
	 	  }
    	   protected void findAndFillAllInterventiMigliorativi(Long id)
    	   {
    		   
    	   }
	 	
	 protected LinkedHashMap<Long, String> findAndFillAllProfiliName(Long idValutazione,String nomeAzienda,TipologiaValutazione tipo) {
	 		
	 		boolean prefillSelected=false;
	 		boolean aziendaFiltered=false;
	 		boolean tipoFiltered=false;
	 		 
	 		 List<ProfiloUsoConsumo> secAllProfiliUso;
	 		 LinkedHashMap<Long, String>  item= new LinkedHashMap<Long, String>();
	 		   
	 		 // recupera profili che possono essere modificati e controllati no solo lettura		   
	 		 tipoFiltered=(tipo!=null);
	 		 if(!tipoFiltered)
	 		    secAllProfiliUso=  findAllProfiloUsoConsumo_Sec();
	 		 else
	 		 {
	 			secAllProfiliUso=  findAllProfiloUsoConsumo_Sec(tipo);
	 		 }
	 		
	 		 
	 		 
	 		 //Set<CompanyDomain> cdUser;
	 		   Valutazione val=null;
	 		   ProfiloUsoConsumo prof=null;
	 		   Azienda az=null;
	 		 
	 		   
	 		   
	 		   
	 		if(idValutazione!=null)
	 		{	
	 		 val=this.valdao.findById(idValutazione);
	 		 if (val!=null) 
	 			 {    
	 			      prof=val.getProfiloUsoConsumo();
	 			      prefillSelected=true;
	 			      System.out.println("prefillselected profilo selezionato: "+prof.getNome());
	 			      System.out.flush();
	 			 }
	 		}
	 		
	 		
	 		if(nomeAzienda!=null)
	 		{
	 			az=this.azdao.findByName(nomeAzienda);
	 			
	 			if(az!=null)
	 			{
	 				aziendaFiltered=true;
	 			}
	 		}
	 		
	 		
	 		selProfiloUso.removeAllItems();
	 		if (prefillSelected)
	 			{
	 			  // la composizione gia esiste quindi potrei avere degli edifici che già appartengono 
	 			  // alla composizione
	 			 
	 			  for (ProfiloUsoConsumo ed: secAllProfiliUso)
	 			    {
	 				  
	 				     // devo evitare profili che non hanno associato alcuna composizione di edifici
	 				    if (ed.getComposizioneEdificio()==null)
	 				    {
	 				    	continue;
	 				    }
	 				    if(aziendaFiltered  )
	 				    {	
	 				    	
	 				        if(az.getNome().equals(ed.getComposizioneEdificio().getAzienda().getNome()))
	 				        {	
	 				        	
	 				         
	 				            selProfiloUso.addItem(ed.getId());
	 				            selProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				            item.put(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				        }
	 				        
	 				        
	 				    }   
	 				    else
	 				    {
	 				    	selProfiloUso.addItem(ed.getId());
	 				        selProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				       item.put(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				    }
	 				  //user.getDomainsOfMembership()
	 				  
	 				  
	 				  System.out.println("profilo "+ed.getNome() + "con id "+ ed.getId());
	 				  System.out.flush();
	 				  
	 				  
	 				// --->> fine for per i profili d'uso		  
	 			}
	 			  if(aziendaFiltered)
	 			     {
	 			      if(az.getNome().equals(prof.getComposizioneEdificio().getAzienda().getNome()))
	 			      {
	 			    	  selProfiloUso.select(prof.getId());
	 			      }
	 			      
	 			    }else
	 			    {
	 			    	selProfiloUso.select(prof.getId());
	 			    }
	 			}
	 		else{
	 		     for (ProfiloUsoConsumo ed: secAllProfiliUso)
	 		      {
	 		    	// devo evitare profili che non hanno associato alcuna composizione di edifici
	 		    	 if (ed.getComposizioneEdificio()==null)
	 				    {
	 				    	continue;
	 				    }
	 		    	 
	 		    	 if(aziendaFiltered  )
	 				    {	
	 				    	
	 				        if(az.getNome().equals(ed.getComposizioneEdificio().getAzienda().getNome()))
	 				        {	
	 				         selProfiloUso.addItem(ed.getId());
	 				         selProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				        item.put(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				        }
	 				        
	 				        
	 				    }   
	 				    else
	 				    {
	 				    	selProfiloUso.addItem(ed.getId());
	 				        selProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				       item.put(ed.getId(), ed.getNome()+"("+ed.getComposizioneEdificio().getName()+")");
	 				    }
	 		    	 
	 			  
	 			 
	 			  
	 			   
	 			   
	 		     }
	 		}
	 		  
	 		  
	 		return item;
	 	}
	 	
	 
	   private List<ProfiloUsoConsumo> findAllProfiloUsoConsumo_Sec(TipologiaValutazione tipo) {
		
		   Login login=loginUser.get();
	 		 List<ProfiloUsoConsumo> secAllProfili;
	 		 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		  SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
             SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		   secAllProfili= this.pusodao.findAll_sec(tipo,login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
	 	        
	 		   
	 		   return secAllProfili;
		

	}

	public synchronized final void setSelVal(Valutazione val)
	   {
		   this.selVal=val;
	   }
	 	
	   public synchronized final Valutazione getSelVal()
	   {
		   return this.selVal;
	   }
	 	
	 	private void initSearch() {

	 		  /*
	 	Event handling
	 	Granularity for sending events over the wire can be controlled. By default simple changes like writing a text in TextField are sent to server with the next Ajax call. You can configure your component to send the changes to server immediately after focus leaves the field. Here we choose to send the text over the wire as soon as user stops writing for short a moment.

	 	When the event happens, we handle it in the anonymous inner class. You may instead choose to use a separate named controller class. In the end, the preferred application architecture is up to you. */
	 		  

	 	          searchField.setInputPrompt("Trova Valutazioni");

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
	 	                               + item.getItemProperty(AZIENDA).getValue() + item
	 	                               .getItemProperty(NOTE).getValue()).toLowerCase();
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
	 	 
	 	   
	 	   
	 	   
	 	   /**
	 	    * recupera i parametri dalla ui e salva il risulato
	 	    * in risultato valutazione intervento
	 	    * @return
	 	    */
	 	  protected boolean salvaRisultatoValutazione(Double risultato)
	 	  {
	 		 Long id=null;
	 		String	idStr=((TextField)editorLayout.getComponent(0)).getValue();
	 		  
	 		if(idStr==null || (idStr.trim().equals("")))
			{
		      id=null;
			}else
			{
				try
				{ 
					id= Long.valueOf(idStr);
				
				}catch(NumberFormatException nf)
				{
					id=null;
				}
			}
	 		
	 		Long funzValId=(Long)((ComboBox)editorLayout.getComponent(6)).getValue();
	 		
	 		if(funzValId== null )
			{
				Notification.show("Devi selezionare una Funzione di valutazione iniziale", Notification.Type.ERROR_MESSAGE);
				return false;
			}
	 		
	 		
	 		 if(risdao.save(id, risultato,funzValId , null, null, null))
	    	 {
	    		 Notification.show("Salvato nuovo risultato", Notification.Type.HUMANIZED_MESSAGE);
	    	     return true;
	    	 }
	 		 return false;
	 	  }
	 	   

	 	protected Double calcolaFunzioneMerito(Double indice,Long profiloUsoId)
	 	{
	 		TipologiaEdifici tipoEdificio;
	 		Double merito;
	    	ProfiloUsoConsumo profilo=		 pusodao.findById(profiloUsoId);
		    if(profilo==null)
		    {
		    	Notification.show("Il profilo selezionato non esiste più", Notification.Type.ERROR_MESSAGE);
		    	entityList.setVisible(false);
		    	refreshContactList(); 
		    	return null;
		    }
		    tipoEdificio=	profilo.getComposizioneEdificio().getTipo();
	    	
	    	 if (tipoEdificio==null)
		     {
		    	 Notification.show("Sembra che il profilo selezionato abbia la sua composizione priva delle tipologia edificio", Notification.Type.ERROR_MESSAGE);
		    	 entityList.setVisible(false);
			     refreshContactList(); 
		    	 return null;
		     }
	    	 
	    	 merito=fuzzyeval.indiceDiMeritoIENt(indice, tipoEdificio);
	    	
	    	 return merito;
	 	}
	 	  
	 	  
	 	   /**
	 	    * 
	 	    * @param event
	 	    * @return
	 	    */
	 	protected Double calcolaFunzione(ClickEvent event,Long profiloUsoId,Long funzValId)   
	 	{
	 		
			Double result=null;
			
			//Long profiloUsoId=(Long)((ComboBox)editorLayout.getComponent(5)).getValue();
			//TipologiaValutazione tipoL=getSelTipologia();
			
			
			 
			
			
			
			if(profiloUsoId== null )
				{
					Notification.show("Devi selezionare un Profilo d'uso", Notification.Type.ERROR_MESSAGE);
					return null;
				}
			
			/*
			if(tipoL==null)
			{
				Notification.show("Devi selezionare una tipologia di valutazione", Notification.Type.ERROR_MESSAGE);
				return null;
			}
			*/
			if (profiloUsoId!=null && funzValId!=null) 
				{
				   result= calcola.calcola(ProfiloUsoConsumo.class,profiloUsoId,funzValId , new CheckParRecursion(3),null);
				}else
				{
					Notification.show("Non posso calcolare, mancano dei parametri: prova prima a salvare la valutazione", Notification.Type.ERROR_MESSAGE);
				    return null;
				}
			
			
			     
			     
			     
			     
			    	 
			    	
			    	
			    	 return result;
			    	
			    	// FunzioneDiValutazione funz=fvdao.findById(funzValId);
			    	// funz.getApplicaTipoValutazione().equals(TipologiaValutazione.TERMICA)
			    	
			    	
			    	 
			     
	 	}
	 	   private void cssinjection() {
	 		   
	 	   /**
	 		          // BEGIN-EXAMPLE: component.table.styling.cssinjection
	 		 	        Table table = new Table("Colorful Table");
	 		 	        table.addStyleName("colorful");
	 		 	        table.setPageLength(16);
	 		 	
	 		 	        // We wrap cell contents inside a CssLayout, which
	 		 	        // allows CSS injection.
	 		 	        table.addContainerProperty("Color", CssLayout.class, null);
	 		 2433	       
	 		 2434	        for (int i=0; i<16; i++) {
	 		 2435	            final int color = 255-i*16;
	 		 2436	           
	 		 2437	            // Get hexadecimal representation
	 		 2438	            StringBuilder sb = new StringBuilder();
	 		 2439	            new Formatter(sb).format("#%1$02x%1$02xff", color);
	 		 2440	            final String colorcode = sb.toString();
	 		 2441	
	 		 2442	            // Stylable wrapper for the cell content
	 		 2443	            CssLayout content = new CssLayout() {
	 		 2444	                private static final long serialVersionUID = -376388455069345789L;
	 		 2445	
	 		 2446	                @Override
	 		 2447	                public String getCss(Component c) {
	 		 2448	                    return "background: " + colorcode + ";";
	 		 2449	                }
	 		 2450	            };
	 		 2451	
	 		 2452	            // The actual cell content
	 		 2453	            Label label = new Label("Here's color " + colorcode);
	 		 2454	            label.setSizeUndefined();
	 		 2455	            content.addComponent(label);
	 		 2456	
	 		 2457	            table.addItem(new Object[] {content}, new Integer(i));
	 		 
	 		 **/
	 		         }
	 	   
	 	private void initAddRemoveButtons() {
	 		
	 		final Object contactId;
	 		
	 		calcolaButton.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
				
					Double indice; 
					Long funzValId=getSelFunzValId();
					Long profiloUsoId=getSelProfiloId();
					
					
					if(funzValId== null )
					{
						Notification.show("Devi selezionare una funzione di valutazione iniziale", Notification.Type.ERROR_MESSAGE);
						return ;
					}
					
					
					if(profiloUsoId==null)
					{
						Notification.show("Devi selezionare un profilo uso consumo", Notification.Type.ERROR_MESSAGE);
						return ;
					}
					//Long funzValId=(Long)((ComboBox)editorLayout.getComponent(6)).getValue();
					 // 1 è l'id del IEN_TERMICO_SCUOLE
			    
					
					indice=calcolaFunzione(event, profiloUsoId, funzValId);
				    indice= MathUtil.arrotonda(indice, 4);
					
					if(indice==null)
				     {
				    	 Notification.show("Problema nel calcolo, il risultato non è applicabile", Notification.Type.ERROR_MESSAGE);
				    	 valCalcolato.setEnabled(false);
						 valCalcolato.setValue("-1");
						 valMerito.setEnabled(false);
						 valMerito.setValue("-1");
				    	 return ;
				     }
					
					valCalcolato.setEnabled(false);
					valCalcolato.setValue(indice.toString());
			    
					
					Double merito;
					
					
					// valuto il merito
					// solo relativo all'IEN elettrico e termico
					
					
					if((funzValId==3L))
			    	 {	 
			         //Double merito=	calcolaFunzioneMerito(indice, profiloUsoId);
						merito=	calcolaFunzione(event,profiloUsoId,5L);
						merito= MathUtil.arrotonda(merito, 2);
			    
			    	 MySub sub = new MySub(UI.getCurrent(),false,"Valore Di Merito per IEN termico",merito,"Prestazione (/10) : ",1);
			         
			         // Add it to the root component
			          sub.show();
			          valMerito.setEnabled(false);
			    		 valMerito.setValue(merito.toString());
			    	 }else if (funzValId==4L)
			    	 {
			    		 merito=	calcolaFunzione(event,profiloUsoId,6L);
			    		 merito= MathUtil.arrotonda(merito, 2);
			    		 MySub sub = new MySub(UI.getCurrent(),false,"Valore Di Merito per IEN elettrico",merito,"Prestazione (/10) : ",1);
			    		 sub.show();
			    		 valMerito.setEnabled(false);
			    		 valMerito.setValue(merito.toString());
			    	 }else if (funzValId==7L)
			    	 {
			    		 merito=	calcolaFunzione(event,profiloUsoId,8L);
			    		 merito= MathUtil.arrotonda(merito, 2);
			    		 MySub sub = new MySub(UI.getCurrent(),false,"Valore Di Merito per Residenziale consumo su sup utile",merito,"Prestazione (/10) : ",1);
			    		 sub.show();
			    		 valMerito.setEnabled(false);
			    		 valMerito.setValue(merito.toString());
			    		 
			    	 }
			    	 else
			    	 {
			    		 MySub sub = new MySub(UI.getCurrent(),false,"Valore Di Merito",null,"Prestazione non supportato per questa valutazione",1);
			    		 sub.show();
			    		 valMerito.setEnabled(false);
			    		 valMerito.setValue("-1");
				         // Add it to the root component
				         
			    	 }
				}
			});
	 		   
	 		
	 		
	 		
	 		
	 		
	 		
	 		
	 		
	 	       addNewEntityButton.addClickListener(new ClickListener() {
	 	               public void buttonClick(ClickEvent event) {

	 	/*
	 	Data model access
	 	Rows in the Container data model are called Items. Here we add a new row to the beginning of the list.

	 	Each Item has a set of Properties that hold values. Here we set the value for two of the properties. */
	 	            	 
	 	            	   contactContainer.removeAllContainerFilters();
	 	            	   Object itemId=newItem();
	 	            	
	 	            	   // nuova entita' quindi non ho selezionato nulla
	 	                   setSelTipologia(null);
	 	                   setSelVal(null);
	 	                   setSelAzienda(null);
	 	                   
	 	            	   editorFields.setItemDataSource(entityList.getItem(itemId));
	 	            	   
	 	            	   
	 	            	   
	 	                   // sto creando nuovo
	 	            	   setContactId(null);
	 	            	   
	 	                    editorLayout.setVisible(true);
	 	                   aziende.select(null);
	 	                   valCalcolato.setValue("---");
	 	                   valMerito.setValue("---");
	 	                   tipoValutazione.select(null);
	 	                   selFunzioneValutazioneIniziale.select(null);
	 	                   
	 	                    // va messo dopo il setVisible
	 	                   TextField tf=  (TextField)editorLayout.getComponent(0);
		 	            	tf.setEnabled(false);
	 	            	   
	 	               }
	 	       });

	 	       removeEntityButton.addClickListener(((new MySub(UI.getCurrent(),true,"Cancella",null,"Se sicuro di voler cancellare l'entità selezionata?",3))
		    		   .setListener(new ClickListener() {
	 	               public void buttonClick(ClickEvent event) {
	 	                      Object contactId = entityList.getValue();
	 	                      Set<Object> multiContact;
	 	                      if(contactId instanceof Set)
	 	                      {
	 	                    	 multiContact=(Set<Object>) contactId;
	 	                    	 contactId = multiContact.iterator().next();
	 	                      }
	 	                      
	 	                    Item it=  entityList.getItem(contactId);
	 	                   String stringId=(String) it.getItemProperty(ID).getValue();
	 	                  Long id=null;
	 	                
	 	                   try
	 	                     {
	 	                	   id=Long.parseLong(stringId);
	 	                     }catch (NumberFormatException e) {
	 	                    	Notification.show("Non posso rimuovere la valutazione selezionata", Notification.Type.ERROR_MESSAGE);
	 	                    	editorLayout.setVisible(false);
	  				            refreshContactList();
	 	                    	return;
	 	                     }
	 	                   
	 	                       Valutazione valutaz=valdao.findById(id);
	 	                       
	 	                       if(valutaz!=null)
	 	                       {
	 	                    	  
	 	                    	  if( valdao.remove(id))               	  
	 	                    	  { 
	 	                    	     Notification.show("Valutazione rimossa Attenzione!! la rimozione dell'eventuale risultato non è ancora implementata", Notification.Type.HUMANIZED_MESSAGE);
	 	                    	     
	 	                    	     editorLayout.setVisible(false);
	 	  				             refreshContactList();
	 	                    	  }else
	 	                    	   {
	 	                    		   Notification.show("La valutazione non puo' essere rimossa", Notification.Type.ERROR_MESSAGE);
	 	                    	   }
	 	                    		   
	 	                     }else
	 	                       {
	 	                    	 entityList.removeItem(contactId);
	 	                    	   // Notification.show("User: "+username+" non posso rimuovere", Notification.Type.ERROR_MESSAGE);
	 	                    	Notification.show("La valutazione che si voleva rimuovere non esiste gia più", Notification.Type.ERROR_MESSAGE);
	 	                    	editorLayout.setVisible(false);
	  				             refreshContactList();
	 	                       }
	 	                       
	 	                       
	 	               }
	 	       })
	 	       ));
	 	       
	 	       saveEntityButton.addClickListener(new ClickListener() {
	 			
	 	    	  // { FNAME,LNAME,NOTE, "Azienda","Edifici","Profilo Uso","Comune"};
	 	    	   
	 			@SuppressWarnings("unchecked")
	 			@Override
	 			public void buttonClick(ClickEvent event) {
	 				Object contactId = getContactId();
	 				
	 				System.out.println("id contact selezionato selezionato: "+ contactId);
	 				System.out.flush();
	 				String idStr;
	 				Long id;
	 				TipologiaValutazione tipo;
	 				String tipoStr;
	 				String note;
	 				String nomeAzienda;
	 				Long profiloUsoId;
	 				Long funzValId;
	 				
	 				String password;
	 				String retypePassword;
	 				Set<Long>	selValue;
	 				Set<Long>	selProfiliValue;
	 				
	 				// qui potrei inserire il recupero 
	 				if (contactId!=null)
	 				{	
	 				  idStr=(String)entityList.getContainerProperty(contactId , FNAME).getValue();
	 				  tipoStr=(String)entityList.getContainerProperty(contactId , TYPE).getValue();
	 				  note=(String)entityList.getContainerProperty(contactId , NOTE).getValue();
	 				  nomeAzienda=(String)entityList.getContainerProperty(contactId , AZIENDA).getValue();
	 				  
	 				  //  password=(String)entityList.getContainerProperty(contactId , "Password").getValue();
	 				//  retypePassword=(String)entityList.getContainerProperty(contactId, "Verifica Password").getValue();
	 				
	 				 tipo=TipologiaValutazione.valueOf(tipoStr);
	 				}
	 				else
	 				{
	 					idStr=((TextField)editorLayout.getComponent(0)).getValue();
	 					note=   (String)((TextField)editorLayout.getComponent(1)).getValue();
	 					tipo=    (TipologiaValutazione)((ComboBox)editorLayout.getComponent(4)).getValue();
	 					
	 					nomeAzienda=(String)((ComboBox)editorLayout.getComponent(3)).getValue();
	 					
	 					//password=(String)((PasswordField)editorLayout.getComponent(5)).getValue();
	 					//retypePassword=(String)((PasswordField)editorLayout.getComponent(6)).getValue();
	 				}
	 				
	 				
	 				
	 				profiloUsoId=(Long)((ComboBox)editorLayout.getComponent(5)).getValue();
	 			//TwinColSelect edificiSel=(TwinColSelect)editorLayout.getComponent(4);
	 		    //		TwinColSelect profiliSel=(TwinColSelect)editorLayout.getComponent(5);
	 				funzValId=(Long)((ComboBox)editorLayout.getComponent(6)).getValue();
	 				
	 				
	 			//	ComboBox selComune=(ComboBox)editorLayout.getComponent(5);
	 				
	 				if(idStr==null || (idStr.trim().equals("")))
	 						{
	 					      id=null;
	 						}else
	 						{
	 							try
	 							{ 
	 								id= Long.valueOf(idStr);
	 							
	 							}catch(NumberFormatException nf)
	 							{
	 								id=null;
	 							}
	 						}

                    String valorecalcolato=valCalcolato.getValue();
                    String valoremerito=valMerito.getValue();
                    Double valNumCalcolato,valNumMerito;
	 				if(valorecalcolato==null || (valorecalcolato.trim().equals("")))
						{
	 					valNumCalcolato=null;
						}else
						{
							try
							{ 
								valNumCalcolato= Double.valueOf(valorecalcolato);
							
							}catch(NumberFormatException nf)
							{
								valNumCalcolato=null;
							}
						}
	 				
	 				if(valoremerito==null || (valoremerito.trim().equals("")))
					{
 					valNumMerito=null;
					}else
					{
						try
						{ 
							valNumMerito= Double.valueOf(valoremerito);
						
						}catch(NumberFormatException nf)
						{
							valNumMerito=null;
						}
					}
	 				
	 				
	 				if(profiloUsoId== null )
	 				{
	 					Notification.show("Devi selezionare un profilo d'uso", Notification.Type.ERROR_MESSAGE);
	 					return;
	 				}
	 				if(funzValId== null )
	 				{
	 					Notification.show("Devi selezionare una funzione di valutazione iniziale", Notification.Type.ERROR_MESSAGE);
	 					return;
	 				}
	 				if(nomeAzienda== null || nomeAzienda.trim().isEmpty() )
	 				{
	 					Notification.show("Devi selezionare una azienda", Notification.Type.ERROR_MESSAGE);
	 					return;
	 				}
	 				System.out.println("l'id con "+ id);
	 		//		System.out.println("il comune con id "+ comuneId);
	 	    //			System.out.println("il name "+ name);
	 				System.out.println("le note "+ note);
	 				System.out.println("il nome dell'azienda "+ nomeAzienda);
	 				
	// 	Notification.show("Save", " id:"+id+", comune id "+comuneId+", name: "+name+", note: "+note+", nome azienda: "+nomeAzienda,Notification.Type.HUMANIZED_MESSAGE);
	 				/*
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
	 				*/
	 				
	 			
	 		
	 			
	 				
	 			
	 				
	 				
	 				
	 				
	 				
	 				//{ FNAME,LNAME,"Password",EMAIL, "Phone Num"};
	 				
	 				/*
	 				ComposizioneEdifici newComp = new ComposizioneEdifici(name, noteDellaComposizione, azienda, edifici, profilo, comuneUbicazione, provinciaUbicazione, ownerid, permissionprop)		
	 						CompanyDomain cd;
	 				
	 				
	 				Set<String> domainNames= new HashSet<String>();
	 				List<CompanyDomain> selDomains= new ArrayList<CompanyDomain>();
	 				String dname;
	 				 for(Integer idSelItem: selValue)
	 					{
	 					 edificiSel.getItem(idSelItem);
	 					 domainNames.add(dname);			
	 					}	
	 					*/
	 				
	 	        /*
	 				if ( ced.saveComposizioneEdifici(id, name, note,selValue , selProfiliValue, nomeAzienda, comuneId))
	 				{
	 					 Notification.show("Save", " Nuova composizione salvata!",Notification.Type.HUMANIZED_MESSAGE);
	 			          setNewContactId(null);
	 				}else
	 				{
	 					Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
	 				}
	 				*/ 
	 				
	 	
	 	            if(valdao.save(loginUser.get().getCurrentDomain(), loginUser.get().getCurrentUser(), id,profiloUsoId , funzValId,nomeAzienda ,note, tipo,valNumCalcolato,valNumMerito)!=null)
	 	            {
	 	            	 Notification.show("Save", " Valutazione salvata!",Notification.Type.HUMANIZED_MESSAGE);
	 			          setNewContactId(null);
	 			         
				         editorLayout.setVisible(false);
				         refreshContactList();
	 	            }
	 	            else
	 	            {
	 	            	Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
	 	            }
	 			}
	 		});
	 	}

         /**
          *  recupera la lista iniziale delle entità da mostrare
          * 
          */
	 	@SuppressWarnings("serial")
	 	private void initEntityList() {
	 	    
	 		
	 		entityList.setContainerDataSource(contactContainer);
	 	    
	 	    // { FNAME,NOTE,CREATO, AZIENDA,TYPE,"Profili Uso",FUNZ_VAL_INIT,INTERVENTI_MIGL};
	 	    entityList.setVisibleColumns(new String[] { NOTE,RISULTATO_VAL_INIT,MERITO,TYPE, AZIENDA,CREATO,PROFILO_USO,FUNZ_VAL_INIT});
	 	    entityList.setSelectable(true);
	 	    entityList.setImmediate(true);
	 	    entityList.addStyleName(Reindeer.PANEL_LIGHT);
	 	    entityList.setMultiSelect(true);

	 	    
	 	   entityList.setCellStyleGenerator(new Table.CellStyleGenerator() {
	 		   
				@Override
				public String getStyle(Table source, Object itemId,
						Object propertyId) {
					// TODO Auto-generated method stub
					 // Row style setting, not relevant in this example.
	 		        if (propertyId == null)
	 		            return "white"; // Will not actually be visible
	 		      String strVal;
	 		        int row = ((Integer)itemId).intValue();
	 		        if(((String)propertyId).equals(MERITO))
	 		        {
	 		        	
	 		        	strVal=(String)source.getContainerProperty(itemId , MERITO).getValue();
	 		              try{
	 		        	   double val=Double.parseDouble(strVal);
	 		        	   
	 		        	 // System.out.println("Colonna:  "+MERITO+" righa: "+row+" Valore: "+val);
	 		              if((val>0)&& (val<=3.0) )
	 		              {
	 		            	// System.out.println("Colore ROSSO");
	 		            	  return "rosso";
	 		              }else if((val>0) && (val<=4.5))
	 		              {
	 		            	 //System.out.println("Colore ARANCIO");
	 		            	  return "arancio";
	 		              }else if((val>0) && (val<6.0))
	 		              {
	 		            	 //System.out.println("Colore GIALLO");
	 		            	  return "giallo";
	 		              }else if((val>0) && (val<7.0))
	 		              {
	 		            	 //System.out.println("Colore VERDINO");
	 		            	  return "verdino";
	 		              }else if((val>0)&& (val<8.5))
	 		              {
	 		            	 //System.out.println("Colore VERDE");
	 		            	  return "verde";
	 		            	  
	 		              }else if((val>0)&&(val <=10))
	 		              {
	 		            	 //System.out.println("Colore VERDISSIMO");
	 		            	  return "verdissimo";
	 		              }else return "white"; 
	 		            	  
	 		            	  
	 		        	  
	 		        	  
	 		        	  
	 		              }catch (Exception e) {
	 		            	 return "white";
						}
	 		        
	 		        }
	 		        
	 		       return "white";
					
					
					
				}
	 		});
	 	    
	 	    entityList.addValueChangeListener(new ValueChangeListener() {
	 	            public void valueChange(ValueChangeEvent event) 
	 	            {
	 	                    Set<Object> MultiContactId = (Set<Object>)entityList.getValue();
	 	/*
	 	   Binding data
	 	   When a contact is selected from the list, we want to show that in our editor on the right. 
	 	   This is nicely done by the FieldGroup that binds all the fields to the
	 	   corresponding Properties in our contact at once.     
	 	*/
	 	                    Object contactId=null;
	 	                    if(MultiContactId!=null && MultiContactId.size()==1)
	 	                    {
	 	                    	Iterator<Object> it=MultiContactId.iterator();
	 	                    	if(it.hasNext()) contactId=it.next();
	 	                    }else
	 	                    {
	 	                    	if(MultiContactId!=null && MultiContactId.size()>1)
	 	                    	{	
	 	                    	 
	 	                    	 Notification.show("Multiselezione..: "+MultiContactId.size(), Notification.Type.WARNING_MESSAGE);
	 	                    	}
	 	                    	return;
	 	                    }
	 	                    
	 	                    System.out.println("entita' selezionata "+contactId );
	 	                    System.out.flush();
	 	                   Valutazione val=null;
	 	                           if (contactId != null)
	 	                           {
	 	                        	   Long id;
	 	                        	   String strId;
	 	                        	   editorFields.setItemDataSource(entityList.getItem(contactId));
	 	                        	   Field<TextField> tf= (Field<TextField>) editorFields.getField(ID);
	 	                        	   //valdao.findById(id);
	 	                        	   try{
	 	                        		 strId= (String)entityList.getContainerProperty(contactId , ID).getValue();
	 	                        	     id=Long.valueOf(strId);
	 	                        	     }
	 	                        	   catch (Exception e) 
	 	                        	     {
	 	                        		  refreshContactList();
	 	                        		  editorLayout.setVisible(false);
										return;
									     }
	 	                        	   val=valdao.findById(id);
	 	                        	   if (val==null)
	 	                        	   {
	 	                        		 Notification.show("L'entità che si voleva editare e' stata gia' eliminata: ", Notification.Type.ERROR_MESSAGE);
	 	                        		 entityList.removeAllItems();
	 	                        		 contactContainer= createDummyDatasource();
	 	                        		 
	 	                        		   return;
	 	                        	   }else {
	 	                        		   // salvo entità selezionata
	 	                        		   setSelVal(val);
	 	                        	   }
	 	                        	   
	 	                        	  
	 	                        	   
	 	                        	   System.out.println("chiave selezioanta: "+id);
	 	                        	   System.out.flush();
	 	                        	   // qui posso popolare eventuali oggetti correlati
	 	                        	   
	 	                        	  
	 	                        	   
	 	                        	   findAndFillAllProfiliName(id, val.getAzienda().getNome(), val.getTipo());
	 	                        	   
                                      findAndFillAllInterventiMigliorativi(id);
                                   
                                      composeSelType(val.getTipo());
                                 
                                      populateAziende();
                                      
                                      
                                      aziende.select(val.getAzienda().getNome());
                                      
                                      findAndFillAllFunzioniValutazione(val.getTipo(),id,val);
                                      Double risVal=val.getRisultatoValutazioneIniziale();
                                      valCalcolato.setValue(risVal==null?"----":risVal.toString());
	 	                              Double meritoVal=val.getPrestazioneIniziale();
	 	                              valMerito.setValue(meritoVal==null?"----":meritoVal.toString());
	 	                              
                                      
                                      //   findAndFillAllEdificiName(id);
	 	                        	   
	 	                        	// deve essere compilata all'occorrenza   
	 	                            //	   findAndFillAllProfiliName(id); 
	 	                        	   setContactId(contactId);
	 	                        	   tf.setEnabled(false);
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
	 	  
	 	   // qui recupero tutte le entità
	 	       
	 	       Login login=loginUser.get();
	 	       
	 	       
	 	       
	 	       // recupero oggetti che possono essere modificati e controllati no solo lettura
	 	       SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		  SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		   
	 		   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
	 		      
	 	    List<Valutazione> valutazioni= this.valdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
	 	
	 	   if (valutazioni==null)
	 	   {
	 		  Notification.show("Non trovo alcuna entità di Valutazione valida", Notification.Type.ERROR_MESSAGE);
	 		   return ic;
	 	   }
	 	    
	 	 // { FNAME,NOTE,CREATO, AZIENDA,TYPE,"Profili Uso",FUNZ_VAL_INIT,INTERVENTI_MIGL};
	 	        for (Valutazione val: valutazioni)
	 	        {
	 	        	Object id = ic.addItem();
	 	        	List<FunzioneDiValutazione> intervMigl=val.getInterventi();
	 	        	
	 	        	ProfiloUsoConsumo puc= val.getProfiloUsoConsumo();
	 	        	
	 	        	// id 
	 	         ic.getContainerProperty(id, FNAME).setValue(val.getId().toString());
	 	       
	 	         
	 	        	// descrizione della valutazione
	 	         ic.getContainerProperty(id, NOTE).setValue(val.getDescrizione());
	 	         
	 	         // data di creazione
	 	        ic.getContainerProperty(id, CREATO).setValue(val.getCreazione().getTime().toString());
	 	     
	 	        // azienda a cui si fa la valutazione
	 	        ic.getContainerProperty(id, AZIENDA).setValue(val.getAzienda().getNome());
	 	         
	 	        // tipo di valutazione
	 	        ic.getContainerProperty(id, TYPE).setValue(val.getTipo().toString());
	 	        
	 	        
	 	        // # di profili d'uso
	 	         ic.getContainerProperty(id, PROFILO_USO).setValue((puc==null)?"--":puc.getNome()+"("+puc.getComposizioneEdificio().getName()+")");
	 	       
	 	       // nome della funzione di valutazione iniziale
	 	   ic.getContainerProperty(id, FUNZ_VAL_INIT).setValue((val.getValutazioneIniziale()==null)?"--":(val.getValutazioneIniziale().getId()+":"+val.getValutazioneIniziale().getName()));
	 	        
	 	      // # di interventi migliorativi
	 	         ic.getContainerProperty(id, INTERVENTI_MIGL).setValue((intervMigl==null)?"--":String.valueOf(intervMigl.size()));
	 	      
	 	      // valore calcolato dell'intervento   
	 	         ic.getContainerProperty(id, RISULTATO_VAL_INIT).setValue((val.getRisultatoValutazioneIniziale()==null)?"--":(val.getRisultatoValutazioneIniziale().toString()));
	 	       
	 	      // valore di prestazione iniziale o merito   
	 	         ic.getContainerProperty(id, MERITO).setValue((val.getPrestazioneIniziale()==null)?"---":(val.getPrestazioneIniziale().toString()));
	 	        }
	 	    
	 	    
	 	     

	 	       return ic;
	 	}

	 	/**
		    * ricalcola e rivisualizza la lista dei contatti
		    */
		   public synchronized void refreshContactList()
		   {
			   //entityList.setVisible(false);
			   
			   contactContainer.removeAllItems();
	  	       contactContainer= createDummyDatasource();
	  	       initEntityList();
		   }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
