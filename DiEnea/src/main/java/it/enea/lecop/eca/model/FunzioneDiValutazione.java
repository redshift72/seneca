package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.persistence.*;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
//@DiscriminatorColumn(name="FUNC_TYPE")
@Table(name="FunzioneValutazione")
@NamedQueries({
	@NamedQuery(name="FunzioneDiValutazione.getForTipoEdificio",
			query="SELECT fv FROM FunzioneDiValutazione fv " +
					"WHERE ( :tipoedificio IN ELEMENTS ( fv.applicaTipoEdifici ))"),
	@NamedQuery(name="FunzioneDiValutazione.getForTipoValutazione",
					query="SELECT fv FROM FunzioneDiValutazione fv " +
							"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )"),
	@NamedQuery(name="FunzioneDiValutazione.getForTipoValutazione_Edificio",
	query="SELECT fv FROM FunzioneDiValutazione fv " +
			"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )" +
			"AND   ( :tipoedificio IN ELEMENTS( fv.applicaTipoEdifici ))"),
	@NamedQuery(name="FunzioneDiValutazione.findAll",
            query="SELECT u FROM FunzioneDiValutazione u"),
     @NamedQuery(name="FunzioneDiValutazione.findById",
            query="SELECT u FROM FunzioneDiValutazione u WHERE u.id = :id"),
    @NamedQuery(name="FunzioneDiValutazione.getForTipoValutazione_ID_NAME",
			query="SELECT fv.id , fv.name , fv.descrizione FROM FunzioneDiValutazione fv " +
					"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )"),
	@NamedQuery(name="FunzioneDiValutazione.getForTipoValutazione_TipoEdificio_ID_NAME",
			query="SELECT fv.id , fv.name , fv.descrizione  FROM FunzioneDiValutazione fv " +
					"WHERE ( :tipovalutazione = fv.applicaTipoValutazione )" 
					)
	
})
public  class FunzioneDiValutazione implements Serializable, GenericFunz, Securable{
	
	
	/**
	 * crea una funzione matematica standard
	 */
	public FunzioneDiValutazione() {
	
		this.parametri = new HashMap<String, ParamIntervento>();
		this.applicaTipoEdifici = new HashSet<TipologiaEdifici>();
		this.setTipoFunz(TipologiaFunzioneDiValutazione.FUNZ_MAT);
	}

	
	
	/**
	    * aggiunge parametro param di nome  mapNomeParInExpr
	    * @param mapNomeParInExpr
	    * @param param
	    */
	   public synchronized void  addParametro(String mapNomeParInExpr,ParamIntervento param) 
	   {
		   		   
			  if(getParametri()==null)
			  {
				  this.parametri = new HashMap<String, ParamIntervento>();
			  }
		  		  
			  synchronized (getParametri()) 
			  {
		      if (getParametri().containsKey(mapNomeParInExpr))
		       {
			    ParamIntervento oldPar=   getParametri().get(mapNomeParInExpr);
			    oldPar.getInterventi().remove(this);
		       }   
			    param.getInterventi().add(this);
			    getParametri().put(mapNomeParInExpr, param);
			   
			    if(param.getTipoParametro().equals(TipologiaParIntervento.UI_VALUE))
			     {
				   synchronized (getUIParams()) 
				     {
				      UIParams.put(param.getNome(), param.getUILabel());
				     }
			     }   
			  }
	    
	    }
	   
	   
	   
	   /**
	    * aggiunge parametro param di nome  mapNomeParInExpr
	    * @param mapNomeParInExpr
	    * @param param
	    */
	   public synchronized boolean  removeParametro(String mapNomeParInExpr,ParamIntervento param) {
		   
		   
			  if(getParametri()==null)
			  {
				  this.parametri = new HashMap<String, ParamIntervento>();
				  return false;
			  }
		  
		  
		   synchronized (getParametri()) {
			   if (getParametri().containsKey(mapNomeParInExpr))
			   {
				   ParamIntervento oldPar=   getParametri().get(mapNomeParInExpr);
				   oldPar.getInterventi().remove(this);
				   getParametri().remove(mapNomeParInExpr);
				   if(param.getTipoParametro().equals(TipologiaParIntervento.UI_VALUE))
				     {
					   synchronized (getUIParams()) 
					     {
					      UIParams.remove(param.getNome());
					     }
				     }   
				   
				   
				   return true;
			   }   
			   return false;
		}
		   
			 
		  
		   
	    
	    }
	   
	   public synchronized void  removeAllParameters() {
		   	
		   	  
		   	
			  if(getParametri()==null)
			  {
				  this.parametri = new HashMap<String, ParamIntervento>();
				  return;
			  }
		  
		  
			  synchronized (getParametri()) 
			  { 
				  for(ParamIntervento  par:getParametri().values())par.getInterventi().remove(this);
				  
				  getParametri().clear();
			  }
			  
			  
			  
		   
		}
		   
			 
		  
		   
	    
	    
	   
	  
	   private void calculateUIParams()
	    {
	    	synchronized (getUIParams()) {
	    		if(UIParams==null) UIParams= new LinkedHashMap<String, String>();
	    		UIParams.clear();
	    		for( Entry<String, ParamIntervento> e:getParametri().entrySet())
		    	{
		    		if (e.getValue().getTipoParametro().equals(TipologiaParIntervento.UI_VALUE))
		    				{
		    			     UIParams.put(e.getKey(), e.getValue().getUILabel());
		    				}
		    	}
			}
	    	
	    }
	
	    
	   
	   public boolean isUIParam(String paramName)
	   {  
		   synchronized (UIParams)
		   {
		    return UIParams.containsKey(paramName);
		   }
		   
	   }
	    
	   
	    Map<String,String> UIParams;
	    
	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getValutazioniIniziali()
	 */
	
	   /**
	    * key nome del parametro e  UI label
	    * etichetta dell'intefaccia grafica che identifica il campo			
	    */
	   @Transient 			
	   public  synchronized Map<String, String> getUIParams() {
			if(UIParams==null) 
				{
				 UIParams=new LinkedHashMap<String, String>();
				}
		   return UIParams;
		}



		public  void setUIParams(Map<String, String> uIPar) {
			UIParams = uIPar;
		}



	@Override
	@OneToMany
	   public List<Valutazione> getValutazioniIniziali()
	   {
	      return valutazioniIniziali;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setValutazioniIniziali(java.util.List)
	 */
	   @Override
	public void setValutazioniIniziali(List<Valutazione> valutazioni)
	   {
	      this.valutazioniIniziali = valutazioni;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getId()
	 */
	   @Override
	   @Id
	   @GeneratedValue(strategy = GenerationType.IDENTITY)
	   public Long getId()
	   {
	      return id;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setId(java.lang.Long)
	 */
	   @Override
	public void setId(Long id)
	   {
	      this.id = id;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getDescrizione()
	 */
	   @Override
	@NotEmpty
	   public String getDescrizione()
	   {
	      return descrizione;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setDescrizione(java.lang.String)
	 */
	   @Override
	public void setDescrizione(String descrizione)
	   {
	      this.descrizione = descrizione;
	   }

	 
	   
	   /**
		   * la key della map e' mappata su una colonna chiave
		   * e sono i nomi dati alle variabili presenti nelle equazioni matematiche o funzioni fuzzy
		   *  
		   */
	@Override
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	   public Map<String,ParamIntervento> getParametri()
	   {
	      return parametri;
	   }

	  
	   @Override
	public void setParametri(Map<String,ParamIntervento> parametri)
	   {
		   calculateUIParams();
	      this.parametri = parametri;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getApplicaTipoEdifici()
	 */
	   @Override
	   @ElementCollection(targetClass=TipologiaEdifici.class,fetch=FetchType.EAGER)
	   public Set<TipologiaEdifici> getApplicaTipoEdifici()
	   {
	      return applicaTipoEdifici;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setApplicaTipoEdifici(java.util.Set)
	 */
	   @Override
	public void setApplicaTipoEdifici(Set<TipologiaEdifici> applicaTipoEdifici)
	   {
	      this.applicaTipoEdifici = applicaTipoEdifici;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getCalcolo()
	 */
	   @Override
	   @Column(length=8192)
	public String getCalcolo()
	   {
	      return calcolo;
	   }

	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setCalcolo(java.lang.String)
	 */
	   @Override
	public void setCalcolo(String calcolo)
	   {
	      this.calcolo = calcolo;
	      
	   }

	  
	   protected Long id;

	   
	   protected String descrizione;

	  
	   protected String name;
	   
	   
	   protected TipologiaFunzioneDiValutazione tipoFunz;
	  
	   @Enumerated(EnumType.STRING)
	   public TipologiaFunzioneDiValutazione getTipoFunz() {
		return tipoFunz;
	}



	public void setTipoFunz(TipologiaFunzioneDiValutazione tipo) {
		this.tipoFunz = tipo;
	}



	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getName()
	 */
	@Override
	@NotEmpty
	    public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}


		protected Map<String, ParamIntervento> parametri;


		protected TipologiaValutazione applicaTipoValutazione;
		private Set<Valutazione>  valutazioniMiglioramento;
	   
		@ManyToMany(mappedBy="interventi",fetch=FetchType.EAGER)
		public Set<Valutazione> getValutazioniMiglioramento() {
			return valutazioniMiglioramento;
		}

		public void setValutazioniMiglioramento(Set<Valutazione> valutazioniMiglioramento) {
			this.valutazioniMiglioramento = valutazioniMiglioramento;
		}
		/* (non-Javadoc)
		 * @see it.enea.lecop.eca.model.GenericFunz#getApplicaTipoValutazione()
		 */
		@Override
		@Enumerated(EnumType.STRING)
	   public TipologiaValutazione getApplicaTipoValutazione() {
			return applicaTipoValutazione;
		}

		/* (non-Javadoc)
		 * @see it.enea.lecop.eca.model.GenericFunz#setApplicaTipoValutazione(it.enea.lecop.eca.model.TipologiaValutazione)
		 */
		@Override
		public void setApplicaTipoValutazione(
				TipologiaValutazione applicaTipoValutazione) {
			this.applicaTipoValutazione = applicaTipoValutazione;
		}

		
		String howResultNamed;
		
		
		
    /**
     * come si chiama il risultato, la sigla che puà contraddistinguere 
     * il valore risultate
     * @return
     */
	public  String getHowResultNamed() {
			return howResultNamed;
		}



		public  void setHowResultNamed(String howResultNamed) {
			this.howResultNamed = howResultNamed;
		}


	protected Set<TipologiaEdifici> applicaTipoEdifici;

	   
	   protected List<Valutazione> valutazioniIniziali;

	  
	   protected String calcolo;

	   public static void main(String[] args)
	   {

	      InterventoMigliorativo inter = new InterventoMigliorativo();
	   }
	   
	 
	   
	   protected String calcChecked;
	   
	   
	   private Set<String> exprParam;
	   
	   /* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getExprParam()
	 */
	@Override
	@Transient
	   public Set<String> getExprParam() {
		return exprParam;
	}

	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setExprParam(java.util.Set)
	 */
	@Override
	public void setExprParam(Set<String> exprParam) {
		this.exprParam = exprParam;
	}

	
	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#setCalcChecked(java.lang.String)
	 */
	@Override
	public void setCalcChecked(String calcChecked) 
	   {
		this.calcChecked = calcChecked;
	    }
	/* (non-Javadoc)
	 * @see it.enea.lecop.eca.model.GenericFunz#getCalcChecked()
	 */
	   @Override
	   @Column(length=8192)
	public String getCalcChecked() 
	   {
		return calcChecked;
	   }

		
		/* (non-Javadoc)
		 * @see it.enea.lecop.eca.model.GenericFunz#hashCode()
		 */
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((calcolo == null) ? 0 : calcolo.hashCode());
			result = prime * result
					+ ((descrizione == null) ? 0 : descrizione.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
		//	result = prime * result + ((parametri == null) ? 0 : parametri.hashCode());
		//	result = prime * result + ((applicaTipoEdifici == null) ? 0 : applicaTipoEdifici.hashCode());
			
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
	
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InterventoMigliorativo other = (InterventoMigliorativo) obj;
			if (calcolo == null) {
				if (other.calcolo != null)
					return false;
			} else if (!calcolo.equals(other.calcolo))
				return false;
			if (descrizione == null) {
				if (other.descrizione != null)
					return false;
			} else if (!descrizione.equals(other.descrizione))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (parametri == null) {
				if (other.parametri != null)
					return false;
			} else if (!parametri.equals(other.parametri))
				return false;
			if (applicaTipoEdifici == null) {
				if (other.applicaTipoEdifici != null)
					return false;
			} else if (!applicaTipoEdifici.equals(other.applicaTipoEdifici))
				return false;
			
			return true;
		}

		 private long version;
		 /* (non-Javadoc)
		 * @see it.enea.lecop.eca.model.GenericFunz#getVersion()
		 */
		@Override
		@Version
			public long getVersion() {
				return version;
			}

			/* (non-Javadoc)
			 * @see it.enea.lecop.eca.model.GenericFunz#setVersion(long)
			 */
			@Override
			public void setVersion(long version) {
				this.version = version;
			}

			
			
			private OwnerId ownerid;
			private PermissionProp permissionprop;

			@Embedded
			public OwnerId getOwnerid()
			{
				  
				return ownerid;
			}

			public void setOwnerid(OwnerId ownerid) {
					this.ownerid = ownerid;
				}

			@Embedded
			public PermissionProp getPermissionprop()
			{
				   return permissionprop;
			}

			public void setPermissionprop(PermissionProp permissionprop) {
					this.permissionprop = permissionprop;
				}
}
