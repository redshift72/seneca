package it.enea.lecop.eca.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import it.enea.lecop.eca.data.AziendaDao;
import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.ComposizioneEdificiDao;
import it.enea.lecop.eca.data.ComuneDao;
import it.enea.lecop.eca.data.EdificioDao;
import it.enea.lecop.eca.data.ProfiloUsoConsumoDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.data.ValutazioneDao;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.vaadin.annotations.PreserveOnRefresh;
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

import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.ComposizioneEdifici;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;
//import it.enea.lecop.eca.view.CdidimeeverdeUI.ContactFilter;
import it.enea.lecop.eca.model.Valutazione;


@CDIUI(value = "cEd")
@Theme("mytheme")
public class CdiVaadinCompEdificiUI extends UI {

	  @EJB
  UserDao userdao;
	  
	  @Inject
  CompanyDomainDao cdd;
	  
	  
	  @Inject
    ComuneDao comdao;
	  
	  @Inject
   ComposizioneEdificiDao ced;
	  
	  @Inject
 	  ProfiloUsoConsumoDao pusodao;
	  
	  @Inject
 	  AziendaDao azdao;
	  
	  @Inject
 	  EdificioDao   eddao;
	  
	  @Inject
    Instance<Login> loginUser;
	  
	  @Inject
	  ValutazioneDao valu_d;
	  
	  private Table entityList = new Table();
	  private TextField searchField = new TextField();
	  private Button addNewEntityButton = new Button("Nuovo");
	  private Button saveEntityButton = new Button("Salva");
	  private Button removeEntityButton = new Button("Elimina questa composizione");
	  private FormLayout editorLayout = new FormLayout();
	  private FieldGroup editorFields = new FieldGroup();
	  private HorizontalLayout buttonLayout = new HorizontalLayout();
	  private TwinColSelect multiSelEdifici= new TwinColSelect("Edifici");
	  private TwinColSelect multiSelProfiloUso= new TwinColSelect("Profilo Uso");
	  private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
	  private VerticalLayout leftLayout = new VerticalLayout();
	  private ComboBox aziende= new ComboBox("Seleziona una Azienda");
	  private ComboBox comuni= new ComboBox("Seleziona un Comune");
	       IndexedContainer contactContainer;

	  private ComboBox  tipoEdificio=new ComboBox("Seleziona un Tipo");   
	  
	  private HashMap<Long,Object>    mappakey= new HashMap<Long, Object>(); 
	       
		private Object newContactId;

		private Object selectedId;

		private Long composizioneId;

		private String selNomeAzienda;

		private boolean invalidAz;

	  public Object getNewContactId() {
			return newContactId;
		}


		public void setNewContactId(Object newContactId) {
			this.newContactId = newContactId;
		}


	private static final String FNAME = "Id";
	  private static final String LNAME = "Nome";
	  private static final String NOTE = "Note";
	  private static final String NAZ = "Azienda";
	  
	  private static final String TIPO = "Tipo";
	  
	  
	  // id chiave del bean 
	  private static final String ID = "Id";
	  private static final String[] fieldNames = new String[] { FNAME,LNAME,NOTE,NAZ,TIPO, "Edifici","Profilo Uso","Comune"};
	  
	       protected void init(VaadinRequest request) {
	    	   
	    	   
	    	   contactContainer= createDummyDatasource();
	    	  initLayout();
	          initEntityList();
	          initEditor();
	          initSearch();
	          initAddRemoveButtons();
	          initComponentEvent();
	          ViewUtils.setNewHandleErrorMessage();
	          String id = request.getParameter("id");
	          /*
	    	  System.out.println(">>>>>--->>Id param: "+id);
	    	  System.out.flush();
	    	  */
	          
	          
	          /*
	           * si puo preselezionare un elemento della lista recuperando l'id  dall'id passato come
	           * par di query string
	           */
	    	  if (id !=null && !id.trim().equals("") && !id.trim().equals("null"))
	    	  {
	    		  Long idKey= Long.decode(id);
	    		  Object idItem=getMappakey().get(idKey);
	    		  if(idItem!=null) entityList.select(idItem);
	    	  }
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
	  }

	  /** 
	   * recupera le valutazioni che hanno un certo profilo
	   * 
	   * @param login
	   * @return
	   */
	  public List<Valutazione> getValutazioniPerProfilo(ProfiloUsoConsumo prof)
	  {
		  // recupero oggetti che possono essere modificati e controllati no solo lettura
		  /*
	       SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		  SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   
		   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		    */  
		  List<Valutazione> filteredVal=new ArrayList<Valutazione>();
	    List<Valutazione> valutazioni= this.valu_d.findAll();
	      for (Valutazione val: valutazioni)
	      {
	    	  if(prof.getId()==val.getProfiloUsoConsumo().getId())
	    	  {
	    		  filteredVal.add(val);
	    	  }
	      }
	
	    return filteredVal;
	  }
	  
	/*
	Dynamic forms
	User interface can be created dynamically to reflect the underlying data. We use a FieldGroup to bind components to a data source.

	We choose to write changes through to data source instead of buffering and committing explicitly.*/

	  private void initEditor() {
	          int i=0;
	          for (String fieldName : fieldNames) {
	                 if (fieldName.equalsIgnoreCase("Comune"))
	                 {
	                	// populateComuni(null);
	                	 
	                	 
	                	 editorLayout.addComponent(comuni,i);
	                     comuni.setWidth("100%");
	                     comuni.setRequired(true);
	                     comuni.setImmediate(true);
	                     comuni.setTextInputAllowed(true);
	                     comuni.setNullSelectionAllowed(false);
	                     comuni.setInputPrompt("Nessun comune selezionato");
	                     TextField tf= new TextField();
	                     tf.setPropertyDataSource(new Property<String>() {

							@Override
							public String getValue() {
								// TODO Auto-generated method stub
								return (String)comuni.getValue();
										
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
	                     
	                     
	                 } else if(fieldName.equalsIgnoreCase("Azienda"))
	                 {
	                	 //populateAziende();
	                	 
	                	 editorLayout.addComponent(aziende,i);
	                	 aziende.setRequired(true);
	                	 aziende.setImmediate(true);
	                	 aziende.setTextInputAllowed(true);
	                     aziende.setWidth("100%");
	                     aziende.setNullSelectionAllowed(false);
	                     aziende.setInputPrompt("Nezzuna azienda selezionata");
	                     
	                     editorFields.bind(aziende, fieldName);
	                 }else if(fieldName.equalsIgnoreCase(TIPO)){
	                	 
	                	 editorLayout.addComponent(tipoEdificio,i);
			        	  tipoEdificio.setWidth("100%");
		                
		                  for (TipologiaEdifici idx: TipologiaEdifici.values()){
		                	  tipoEdificio.addItem(idx);
		              
		                	  tipoEdificio.setItemCaption(idx, idx.descrizione());
		                	  
		                	  
		                  }
		                  
		                  tipoEdificio.setRequired(true);
		                  tipoEdificio.setImmediate(true);
		                  tipoEdificio.setNullSelectionAllowed(false);
		                  
		                 // Bind doesn't make sense except for text field 
		                 //editorFields.bind(tipoProfilo, fieldName); 
			        	
	                	 
	                 } else if(fieldName.equalsIgnoreCase("Edifici"))
	                 {
	                	 //ComboBox cb = new ComboBox(fieldName, options)
	                	 //composeSelEdifici();
	                	 editorLayout.addComponent(multiSelEdifici,i);
	                	 
	                	
	                	 multiSelEdifici.setLeftColumnCaption("Edifici Disponibili");
	                	 multiSelEdifici.setRightColumnCaption("Edifici della Composizione");
	                	 multiSelEdifici.setWidth("100%");
	                	 multiSelEdifici.setImmediate(true);
	                	// editorFields.bind(domain, fieldName);
	                	 
	                	 
	                 }else if (fieldName.equalsIgnoreCase("Profilo uso"))
	                 {   
	                	 //composeSelProfili();
	                	 editorLayout.addComponent(multiSelProfiloUso,i);
		                	
	                	 multiSelProfiloUso.setLeftColumnCaption("Profili d'uso disponibili");
	                	 multiSelProfiloUso.setRightColumnCaption("Profili d'uso della Composizione");
	                	 multiSelProfiloUso.setWidth("100%");
	                	 multiSelProfiloUso.setImmediate(true);
	                	 multiSelProfiloUso.setEnabled(false);
	                 
	                 }else if (fieldName.equalsIgnoreCase(ID))
	                 {
	                	 // disabilito l'id poiche è autogenerato quindi non modificabile
	                	 TextField field = new TextField(fieldName);
		                  editorLayout.addComponent(field,i);
		                  field.setEnabled(false);
		                  field.setWidth("100%");
		                  editorFields.bind(field, fieldName);
	                 }
	                 else
	                 {	 
	        	      TextField field = new TextField(fieldName);
	                  editorLayout.addComponent(field,i);
	                
	                  field.setWidth("100%");
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
	  }

	  private void populateComuni(Long id) {
		
		  Login login=loginUser.get();
		  ComposizioneEdifici compe=null;
		  if(id!=null)
		   {
			   compe=this.ced.findById(id);
			   if(compe==null) 
			   {
				   editorLayout.setVisible(false);
				   Notification.show("La composizione che si vuole editare non esiste più", Notification.Type.ERROR_MESSAGE);
				   createDummyDatasource();
				   return;
			   }
			   
		   }
		  
		  
			 List<Comune> secAllComuni;
			 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			 SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			 SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			 SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.READ};
			 secAllComuni= this.comdao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
			  
			   
			   for (Comune c: secAllComuni)
			   {
				   comuni.addItem(c.getId());
				   comuni.setItemCaption(c.getId(), c.getDescrizione()+" ("+c.getProvincia()+") ");
			   }
			   if (compe!=null)
			   {
				   comuni.select(compe.getComuneUbicazione().getId());
			   }
			   
	}


	private void populateAziende(Long id) {
		Login login=loginUser.get();
		ComposizioneEdifici compe=null;
		 if(id!=null)
		   {
			   compe=this.ced.findById(id);
			   if(compe==null) 
			   {
				   editorLayout.setVisible(false);
				   Notification.show("La composizione che si vuole aditare non esiste più", Notification.Type.ERROR_MESSAGE);
				   createDummyDatasource();
				   return;
			   }
			   
		   }
		
		
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
		if(compe!=null)
		{
			  aziende.select(compe.getAzienda().getNome());
		}
		
	}

/**
 * cerca tutti i profili senza filtro di selezione ComposizioneEdifici e Azienda
 */
	private void composeSelProfili() {
		
		  findAndFillAllProfiliName(null,null); 
	}


	private void composeSelEdifici() {
		  
		  
		 // con null carica solo tutti i valori possibili nel selettore
		 findAndFillAllEdificiName(null,null,null); 
		
	}

	  /**
	   * recupera tutti gli edifici che,dall'utente di login attuale, possono essere modificati o controllati; non recupera quelli che possono essere solo letti
	   * Tali entità potrebbero avere relazioni a 2 vie, quindi la modifica della relazione da un lato potrebbe
	   * significare una modifica della realzione anche dall'altro lato, quindi trattasi di modifica 
	   * @return
	   */
	private   List<Edificio>  findAllEdificio_Sec()
	{
		 Login login=loginUser.get();
		 List<Edificio> secAllEdifici;
		 SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] subDomSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   
		   secAllEdifici= eddao.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomSec,otherSec);
	
		   return secAllEdifici;
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
	
/*
	private LinkedHashMap<Integer, String> findAndFillAllDomainName() {
		LinkedHashMap<Integer,String> item= new LinkedHashMap<Integer, String>();  
		List<CompanyDomain> cds=cdd.retrieveAllCompanyDomainOrderedByName();
		int i=1;  
		for (CompanyDomain cd: cds)
		  {
			  multiSelEdifici.addItem(i);
			  multiSelEdifici.setItemCaption(i, cd.getName());
			  
			  
			  item.put(i, cd.getName());
			  i++;
		  }
		  
		  
		  
		return item;
	}
*/	
	protected LinkedHashMap<Long, String> findAndFillAllEdificiName(Long id,String nomeAz, TipologiaEdifici tipoEd) {
		
		boolean prefillSelected=false;
		boolean aziendaSelected=false;
		boolean tipoSelected=false;
		 
		 List<Edificio> secAllEdifici,resultEdifici=new ArrayList<Edificio>();
		 LinkedHashMap<Long, String>  item= new LinkedHashMap<Long, String>();
		   
		 // recupera edifici che possono essere modificati e controllati no solo lettura		   
		   secAllEdifici= findAllEdificio_Sec();
		//Set<CompanyDomain> cdUser;
		   ComposizioneEdifici compe=null;
		   Azienda az=null;
		   if (nomeAz!=null && !nomeAz.trim().equals("") )
		   {
			   az=this.azdao.findByName(nomeAz);
			   if (az!=null) 
				   {
				     aziendaSelected=true;
				   }
			   else
			   {
				   editorLayout.setVisible(false);
				   Notification.show("L'azienda selezionata "+nomeAz+",ora non esiste più", Notification.Type.ERROR_MESSAGE);
				   createDummyDatasource();
				   return null;
			   }
		   }
		if(tipoEd!=null){
			tipoSelected=true;
			
		}   
		
		if(id!=null)
		{	
		 compe=this.ced.findById(id);
		 if (compe!=null) prefillSelected=true;
		 else
		 {
			 editorLayout.setVisible(false);
			   Notification.show("La composizione che si vuole editare non esiste più", Notification.Type.ERROR_MESSAGE);
			   createDummyDatasource();
			   return null;
		 }
		}
		
		int i=1;  
		multiSelEdifici.removeAllItems();
		if (prefillSelected)
			{
			  // la composizione gia esiste quindi potrei avere degli edifici che già appartengono 
			  // alla composizione
			 
			  for (Edificio ed: secAllEdifici)
			    {
				  if (aziendaSelected)
				  {
					  System.out.println("SELEZIONE EDIFICI CASO UPDATE");
					  if (!ed.getAzienda().getNome().equals(nomeAz))
					  continue;
					  
					  if (!ed.getTipologiaEdifici().equals(tipoEd)){
						  System.out.println("Edificio "+ed.getNome() + "scartato !"+ "Azienda: "+ ed.getAzienda().getNome()+ "Tipo: " + ed.getTipologiaEdifici());
						  System.out.flush();
						  
						  continue;
					  }
				  }
				  multiSelEdifici.addItem(ed.getId());
				  multiSelEdifici.setItemCaption(ed.getId(), ed.getNome());
				  //user.getDomainsOfMembership()
				  
				  
				  System.out.println("Edificio "+ed.getNome() + "con id "+ ed.getId());
				  System.out.flush();
				  
				  
				  
				  Set<Edificio> cduser=compe.getEdifici();
				  for(Edificio cdu:cduser)if(cdu.getId() == ed.getId())
				  {
					  System.out.println("fa parte della composizioneEdifici "+compe.getName());
					  System.out.flush();
					  
					  multiSelEdifici.select(ed.getId());
					  break;
				  }else
				     {
				    	 System.out.println("NON fa parte della composizione "+compe.getName());
						  System.out.flush();
				     }
				
				  
				  item.put(ed.getId(), ed.getNome());
				  i++;
			     }
			  
			}
		else{
			  System.out.println("SELEZIONE EDIFICI CASO NEW");
			  System.out.flush();
				
		     for (Edificio ed: secAllEdifici)
		      {
		    	 if (aziendaSelected)
				  {
					  if (!ed.getAzienda().getNome().equals(nomeAz))
					  continue;
					  
					  if (!ed.getTipologiaEdifici().equals(tipoEd)){
						  System.out.println("Edificio "+ed.getNome() + "scartato !"+ "Azienda: "+ ed.getAzienda().getNome()+ "Tipo: " + ed.getTipologiaEdifici());
						  System.out.flush();
						  
						  continue;
					  }
						  
				  }
		    	 
			   multiSelEdifici.addItem(ed.getId());
			   multiSelEdifici.setItemCaption(ed.getId(), ed.getNome());
			  
			  
			   item.put(ed.getId(), ed.getNome());
			   i++;
		     }
		}
		  
		  
		return item;
	}

	
	
protected LinkedHashMap<Long, String> findAndFillAllProfiliName(Long id, String nomeAz) {
		
		boolean prefillSelected=false;
		 
		 List<ProfiloUsoConsumo> secAllProfili,resultEdifici=new ArrayList<ProfiloUsoConsumo>();
		 LinkedHashMap<Long, String>  item= new LinkedHashMap<Long, String>();
		   
		 // recupera edifici che possono essere modificati e controllati no solo lettura		   
		   secAllProfili=  findAllProfiloUsoConsumo_Sec();
		//Set<CompanyDomain> cdUser;
		   ComposizioneEdifici compe=null;
		if(id!=null)
		{	
		 compe=this.ced.findById(id);
		 if (compe!=null) prefillSelected=true;
		 else{
			 editorLayout.setVisible(false);
			   Notification.show("La composizione che si vuole editare non esiste più", Notification.Type.ERROR_MESSAGE);
			   refreshContactList();
			   return null;
		    }
		}
		
		int i=1;  
		multiSelProfiloUso.removeAllItems();
		if (prefillSelected)
			{
			  // la composizione gia esiste quindi potrei avere degli edifici che già appartengono 
			  // alla composizione
			 
			  for (ProfiloUsoConsumo ed: secAllProfili)
			    {
				  String name;
			    	 if (ed.getComposizioneEdificio()==null)
			    		 name="----";
			    	 else
			    		 name=ed.getComposizioneEdificio().getName();
				  
				    multiSelProfiloUso.addItem(ed.getId());
				    multiSelProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+name+")");
				  //user.getDomainsOfMembership()
				  
				  
				  System.out.println("profilo "+ed.getNome() + "con id "+ ed.getId());
				  System.out.flush();
				  
				  
				  
				  Set<ProfiloUsoConsumo> profUso=compe.getProfilo();
				  for(ProfiloUsoConsumo cdu:profUso)
				  {    // 
					  if(cdu.getId() == ed.getId())
				      {
					  System.out.println("fa parte della composizioneEdifici "+compe.getName());
					  System.out.flush();
					  
					  multiSelProfiloUso.select(ed.getId());
					  break;
				      }else
				       {
				    	 System.out.println("NON fa parte della composizione "+compe.getName());
						  System.out.flush();
				       }
				  }
				  
				  item.put(ed.getId(), ed.getNome());
				  i++;
			     }
			  
			}
		else{
		     for (ProfiloUsoConsumo ed: secAllProfili)
		      {
		    	 String name;
		    	 if (ed.getComposizioneEdificio()==null)
		    		 name="----";
		    	 else
		    		 name=ed.getComposizioneEdificio().getName();
			   multiSelProfiloUso.addItem(ed.getId());
			   multiSelProfiloUso.setItemCaption(ed.getId(), ed.getNome()+"("+name+")");
			   
			   
			  
			  
			   item.put(ed.getId(), ed.getNome());
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
		  

	          searchField.setInputPrompt("Trova Composizioni");

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
	                               .getItemProperty(NOTE).getValue()).toLowerCase();
	               return haystack.contains(needle);
	       }

	       public boolean appliesToProperty(Object id) {
	               return true;
	       }
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
 	    * inizializza gli eventi dei componenti (no bottoni)    
 	    */
 	  private void initComponentEvent() {
 		  
 		  
 		  // selezionando un tipo inizializzo le funzioni di valutazione in base hai tipi
 		 aziende.addValueChangeListener(new ValueChangeListener(){

 			/**
			 * evento onchange sulla combo del tipo di valutazione
			 */
			

			@Override
 			public void valueChange(ValueChangeEvent event) {
				Object contactId = entityList.getValue();
				TipologiaEdifici tipo;
				ComposizioneEdifici composizione=null;
				
				// azienda vecchia gia selezionata
				String oldSelAz=getSelNomeAzienda();
				
				if (contactId != null)
				{
					Long id;
					id=Long.valueOf((String)entityList.getContainerProperty(contactId , ID).getValue());
					composizione =ced.findById(id); 
					if(composizione==null){
						tipo=null;
           		   
					}else{
						tipo=composizione.getTipo();
						
					}
              	   
                 }else{
                	 
                	 tipo=(TipologiaEdifici) tipoEdificio.getValue();
                 }
				
				
				String	nomeAz=(String)aziende.getValue();
				Set<Long> idValutazioniBreak=new HashSet<Long>();
 				if(nomeAz!=null && !nomeAz.trim().equals("") )
 				{
 			      setSelNomeAzienda(nomeAz);
 				  if(composizione!=null)
 				  {
 					  // sono tutti i profili uso consumo a cui questa composizione appartiene
 					  Set<ProfiloUsoConsumo> profili =composizione.getProfilo();
 					  if(profili!=null && profili.size()>0)
 					  {
 						  List<Valutazione> vals;   
 						  for(ProfiloUsoConsumo pru:profili)
 						  {
 							  vals=getValutazioniPerProfilo(pru);
 							  for(Valutazione val:vals)
 							  {  
 							  if(!nomeAz.equals(val.getAzienda().getNome()))
 							  {
 								 idValutazioniBreak.add(val.getId());
 							  }
 							  }
 						  }
 					  }
 					  
 				  }
 				  //populateFunzioniValutazioni(tipo, null);
 				 findAndFillAllEdificiName(getComposizioneId(), nomeAz,tipo);
 				if(!idValutazioniBreak.isEmpty())
 				{
 					setInvalidAz(true);
 					StringBuffer buff= new StringBuffer("IDs :");
 					for(Long id:idValutazioniBreak)
 					{
 						buff.append(" ").append(id);
 					}
 					Notification.show("Devi selezionare un'altra azienda perchè questa composizione è già associata, e rendi invalide le valutazioni "+buff, Notification.Type.ERROR_MESSAGE);
 				    aziende.select(oldSelAz);
 				}else
 				{
 					setInvalidAz(false);
 				}
 				}
 				
 			}
 			  
 		  });
			
 		 
 		
 		// selezionando un tipo inizializzo le funzioni di valutazione in base hai tipi
 		 tipoEdificio.addValueChangeListener(new ValueChangeListener(){

 			/**
			 * evento onchange sulla combo del tipo di valutazione
			 */
			

			@Override
 			public void valueChange(ValueChangeEvent event) {
				Object contactId = entityList.getValue();
				TipologiaEdifici tipo;
				
				if (contactId != null){
					Long id;
					id=Long.valueOf((String)entityList.getContainerProperty(contactId , ID).getValue());
					ComposizioneEdifici composizione =ced.findById(id); 
					if(composizione==null){
						tipo=null;
           		   
					}else{
						tipo=composizione.getTipo();
						
					}
              	   
                 }else{
                	 
                	 tipo=(TipologiaEdifici) tipoEdificio.getValue();
                     
                 }
				
				
				String	nomeAz=(String)aziende.getValue();
				
 				if(nomeAz!=null && !nomeAz.trim().equals("") )
 				{
 			      //setSelNomeAzienda(nomeAz);
 				
 				  //populateFunzioniValutazioni(tipo, null);
 				 findAndFillAllEdificiName(getComposizioneId(), nomeAz,tipo);
 				}	
 			}
 			  
 		  });
			
 		 
 		 
 		 
		}
	   
	   
	   protected void setInvalidAz(boolean b) {
		this.invalidAz=b;
		
	}
	   protected boolean getInvalidAz() {
			return this.invalidAz;
			
		}


	protected void setSelNomeAzienda(String nomeAz) {
		this.selNomeAzienda=nomeAz;
		
	}
   
	protected String getSelNomeAzienda() {
		return this.selNomeAzienda;
		
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
		
		
	       addNewEntityButton.addClickListener(new ClickListener() {
	               public void buttonClick(ClickEvent event) {

	/*
	Data model access
	Rows in the Container data model are called Items. Here we add a new row to the beginning of the list.

	Each Item has a set of Properties that hold values. Here we set the value for two of the properties. */
	                     
	            	   contactContainer.removeAllContainerFilters();
	            	   Object itemId=newItem(); 
	                
	                   
	            	   editorFields.setItemDataSource(entityList.getItem(itemId));
	                   // sto creando nuovo
	            	   populateComuni(null);
	            	   populateAziende(null);
	            	   composeSelEdifici();
	            	   composeSelProfili();
	            	   
	            	   setComposizioneId(null);
	            	   setContactId(null);
	            	   
	                    editorLayout.setVisible(true);
	                    
	                    // va messo dopo
	 	                   TextField tf=  (TextField)editorLayout.getComponent(0);
		 	            	tf.setEnabled(false);
	            	   
	               }
	       });

	       removeEntityButton.addClickListener(((new MySub(UI.getCurrent(),true,"Cancella",null,"Se sicuro di voler cancellare L'entità selezionata?",3))
	    		   .setListener(new ClickListener() {
	               public void buttonClick(ClickEvent event) {
	                      
	            	//   MySub sub = new MySub(UI.getCurrent(),true,"Delete",null,"Se sicuro di voler cancellare L'entità selezionata?",3);
	            	  
	            	//   if(sub.getResult()==4) return;
	            		   
	            	   
	            	   Object contactId = entityList.getValue();
	                      
	                    Item it=  entityList.getItem(contactId);
	                   String idStr=(String) it.getItemProperty(ID).getValue();
	      	         
	                   if(idStr == null || idStr.trim().equals("")) return;
	                    
	                   Long id=Long.valueOf(idStr);
	          
	                   ComposizioneEdifici comp= ced.findById(id);
	                       
	                   Set<ProfiloUsoConsumo> profili;
	                       if(comp!=null && !(profili=comp.getProfilo()).isEmpty())
	                       {
	                    	   
	                    	     
	                    	   
	                    	   
	                    	   List<Valutazione> valutazioni=valu_d.findAll();
	                    	   
	                    	  
			                      Set<String> nomevalBusy=new HashSet<String>();
			                      
	                    	   // se trovo valutazioni che hanno profili d'uso a cui la composizione corrente partecipa
	                    	   for(ProfiloUsoConsumo pr:profili)
	                    	   {
	                    		   for(Valutazione idx: valutazioni){
			                    	   //System.out.println("Checking Valutazione "+idx.getDescrizione());
			                    		  
			                    	   if (idx.getProfiloUsoConsumo().getId().equals(pr.getId())){
			                    		   nomevalBusy.add("id: "+pr.getId()+" '"+pr.getNome()+"' in valutazione id: "+ String.valueOf(idx.getId())+" '"+idx.getDescrizione()+"'");
			                    	   }
			                       }
	                    	   }
	                    	   
	                    	  
	                    	   if(!nomevalBusy.isEmpty())
			                      {
			                    	        StringBuffer strb= new StringBuffer();
			                    	        for (String val:nomevalBusy) strb.append(" ").append(val);
			                                Notification.show("'"+comp.getName()+"' non può essere rimosso perchè in uso da " + nomevalBusy.size()+" entità valutazione \n" +
			                                		"Per rimuovere eliminare profili con"+strb.toString(), 
		                    				Notification.Type.ERROR_MESSAGE);
			                   			   
		                    				return;  
			                      }
	                    	   
	                    	   
	                    	   
	                    	   
	                    	  if( ced.remove(comp))
	                    	  { 
	                    		
	                    	     Notification.show("Composizione: "+comp.getName()+" rimossa", Notification.Type.HUMANIZED_MESSAGE);
	                    	     refreshContactList();
	                    	     
	                    	  }else
	                    	   {
	                    		   Notification.show("Composizione: "+comp.getName()+" non posso rimuoverlo", Notification.Type.ERROR_MESSAGE);
	                    	   }
	                    		   
	                     }else
	                       {
	                    	 entityList.removeItem(contactId);
	                    	   // Notification.show("User: "+username+" non posso rimuovere", Notification.Type.ERROR_MESSAGE);
	                    	 
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
				Login login=loginUser.get();
				System.out.println("id contact selezionato selezionato: "+ contactId);
				System.out.flush();
				String idStr;
				Long id;
				String name;
				String note;
				String nomeAzienda;
				String password;
				String retypePassword;
				Set<Long>	selValue;
				Set<Long>	selProfiliValue;
				
				TipologiaEdifici tipoEd;
				
				// qui potrei inserire il recupero 
				if (contactId!=null)
				{	
				  idStr=(String)entityList.getContainerProperty(contactId , FNAME).getValue();
				  name=(String)entityList.getContainerProperty(contactId , LNAME).getValue();
				  note=(String)entityList.getContainerProperty(contactId , NOTE).getValue();
				  nomeAzienda=(String)entityList.getContainerProperty(contactId , NAZ).getValue();
				 
				  //  password=(String)entityList.getContainerProperty(contactId , "Password").getValue();
				//  retypePassword=(String)entityList.getContainerProperty(contactId, "Verifica Password").getValue();
				}
				else
				{
					idStr= (String)editorFields.getField(FNAME).getValue(); 
					name= (String)editorFields.getField(LNAME).getValue();// FNAME   (String)((TextField)editorLayout.getComponent(1)).getValue();
					note=  (String)editorFields.getField(NOTE).getValue();// (String)((TextField)editorLayout.getComponent(2)).getValue();
					nomeAzienda=(String)editorFields.getField(NAZ).getValue();//(String)((ComboBox)editorLayout.getComponent(3)).getValue();
					//password=(String)((PasswordField)editorLayout.getComponent(5)).getValue();
					//retypePassword=(String)((PasswordField)editorLayout.getComponent(6)).getValue();
				}
				
				tipoEd=(TipologiaEdifici) tipoEdificio.getValue();
				
				//TwinColSelect edificiSel=.getComponent(4);
				//TwinColSelect profiliSel=(TwinColSelect)editorLayout.getComponent(5);
				//ComboBox selComune=(ComboBox)editorLayout.getComponent(6);
				
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
				
				Long comuneId=(Long)    comuni.getValue();
				selValue= (Set<Long>) multiSelEdifici.getValue();
				
				
				selProfiliValue= (Set<Long>)multiSelProfiloUso.getValue();
				// seleziona tutti i profili inseriti
				List<Long> allProfiliId=(List<Long>) multiSelProfiloUso.getItemIds();
				
				
				/*
				if (selValue.size() == 0)
				{
					Notification.show("L'utente deve appartenere almeno ad un dominio", Notification.Type.ERROR_MESSAGE);
					return;
				}
				*/
				
				
				if(nomeAzienda== null || nomeAzienda.trim().isEmpty() )
				{
					Notification.show("Devi selezionare una azienda", Notification.Type.ERROR_MESSAGE);
					return;
				}
				if (comuneId==null)
				{
					Notification.show("Devi selezionare un comune", Notification.Type.ERROR_MESSAGE);
					return;
				}
				/*
				System.out.println("l'id con "+ id);
				System.out.println("il comune con id "+ comuneId);
				System.out.println("il name "+ name);
				System.out.println("le note "+ note);
				System.out.println("il nome dell'azienda "+ nomeAzienda);
				*/
	//Notification.show("Save", " id:"+id+", comune id "+comuneId+", name: "+name+", note: "+note+", nome azienda: "+nomeAzienda,Notification.Type.HUMANIZED_MESSAGE);
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
				
				if ( ced.saveComposizioneEdifici(allProfiliId,id, name, note,selValue , selProfiliValue, nomeAzienda,tipoEd, comuneId,login))
				{
					if(selValue.isEmpty())
					{
						Notification.show("Salva", "Salvata, ma non avendo edifici sarà inutilizzabile!",Notification.Type.WARNING_MESSAGE);
					}
					else {
						Notification.show("Salva", "Salvato!",Notification.Type.HUMANIZED_MESSAGE);
					}
			         setNewContactId(null);
			         editorLayout.setVisible(false);
			         refreshContactList();
				}else
				{
					Notification.show("Problema nel salvataggio!!", Notification.Type.ERROR_MESSAGE);
				}
				 
				
			}
		});
	}


	@SuppressWarnings("serial")
	private void initEntityList() {
	    
		
		entityList.setContainerDataSource(contactContainer);
	    
	    // { FNAME,LNAME,NOTE, "Azienda","N. Edifici","Profilo Uso","Comune"};
	    entityList.setVisibleColumns(new String[] { LNAME, NOTE, "Azienda", "Edifici" ,"Profilo Uso","Comune"});
	    entityList.setSelectable(true);
	    entityList.setImmediate(true);
	    entityList.addStyleName(Reindeer.PANEL_LIGHT);
	    
	    entityList.addValueChangeListener(new ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) 
	            {
	                    Object contactId = entityList.getValue();
	/*
	   Binding data
	   When a contact is selected from the list, we want to show that in our editor on the right. 
	   This is nicely done by the FieldGroup that binds all the fields to the
	   corresponding Properties in our contact at once.     
	*/
	                    
	                    
	                    System.out.println("entita' selezionata "+contactId );
	                    System.out.flush();
	                    
	                           if (contactId != null)
	                           {
	                        	   Long id;
	                        	   
	                        	   editorFields.setItemDataSource(entityList.getItem(contactId));
	                        	   Field<TextField> tf= (Field<TextField>) editorFields.getField(ID);
	                        	   
	                        	   id=Long.valueOf((String)entityList.getContainerProperty(contactId , ID).getValue());
	                        	   System.out.println("chiave selezioanta: "+id);
	                        	   System.out.flush();
	                        	   // qui posso popolare eventuale oggetti correlati
	                        	   ComposizioneEdifici composizione =ced.findById(id); 
	                        	   if(composizione==null){
	                        		   return;
	                        		   
	                        	   }
	                        	   populateComuni(id);
	                        	   populateAziende(id);
	                        	   setSelNomeAzienda(composizione.getAzienda().getNome());
	                        	   tipoEdificio.select(composizione.getTipo());
	                        	   
	                        	   findAndFillAllEdificiName(id,(String)aziende.getValue(),composizione.getTipo());
	                        	   findAndFillAllProfiliName(id,null); 
	                        	   setComposizioneId(id);
	                        	   setContactId(contactId);
	                        	   tf.setEnabled(false);
	                           }
	                            
	                   // findAndFillAllDomainNameOnUser(username);
	                    editorLayout.setVisible(contactId != null);
	                    
	            }
	    });
	}


	  protected void setComposizioneId(Long id2) {
		composizioneId=id2;
		
	}

	  protected Long getComposizioneId() {
			return composizioneId;
			
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
           HashMap<Long,Object> mappaKey= new HashMap<Long, Object>();
	       
	       for (String p : fieldNames) {
	           
	    	    ic.addContainerProperty(p, String.class, "");
	       }
	  
	   // qui recupero tutte le entità
	       
	       Login login=loginUser.get();
	       
	       // recupero oggetti che possono essere modificati e controllati no solo lettura
	       SecAttrib[] userSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] domainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] subDomainSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		   SecAttrib[] otherSec= new SecAttrib[]{SecAttrib.CONTROL,SecAttrib.MODIFY,SecAttrib.CONTROL,SecAttrib.MODIFY};
		      
	    List<ComposizioneEdifici> composizioni= this.ced.findAll_sec(login.getCurrentUser().getUsername(), login.getCurrentDomain(), userSec, domainSec,subDomainSec,otherSec);
	
	    if (composizioni==null)
	 	   {
	 		  Notification.show("Errore nel recupero delle entità ComposizioneEdifici", Notification.Type.ERROR_MESSAGE);
	 		   return ic;
	 	   }
	    
	 // { FNAME,LNAME,NOTE, "Azienda","N. Edifici","Profilo Uso","Comune"};
	        for (ComposizioneEdifici us: composizioni)
	        {
	        	Object id = ic.addItem();
	        	Set<Edificio> se=us.getEdifici();
	        	Set<ProfiloUsoConsumo> puc= us.getProfilo();
	        	
	         ic.getContainerProperty(id, FNAME).setValue(us.getId().toString());
	         ic.getContainerProperty(id, LNAME).setValue(us.getName());
	        	
	         ic.getContainerProperty(id, NOTE).setValue(us.getNoteDellaComposizione());
	         ic.getContainerProperty(id, "Azienda").setValue((us.getAzienda()==null)?"--":us.getAzienda().getNome());
	         ic.getContainerProperty(id, "Edifici").setValue((se==null)?"--":String.valueOf(se.size()));
	         ic.getContainerProperty(id, "Profilo Uso").setValue((puc==null)?"--":String.valueOf(puc.size()));
	        	
	         ic.getContainerProperty(id, "Comune").setValue(us.getComuneUbicazione().getDescrizione()+ " ("+us.getComuneUbicazione().getProvincia()+")");
	     
	         mappaKey.put(us.getId(), id);
	         
	         
	        }
	    
	    
	        setMappakey(mappaKey);

	       return ic;
	}


	private void setMappakey(HashMap<Long, Object> mappaKey) {
		// TODO Auto-generated method stub
		 mappakey=mappaKey;
	}
	
	public HashMap<Long, Object> getMappakey()
	{
		return mappakey;
	}










}
