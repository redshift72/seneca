package it.enea.lecop.eca.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import it.enea.lecop.eca.data.FattoriNormDao;
import it.enea.lecop.eca.model.*;
//import it.enea.lecop.eca.model.ParamIntervento.TipoPar;
import it.enea.lecop.eca.util.CheckParRecursion;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import net.sourceforge.jFuzzyLogic.FIS;
import it.enea.lecop.eca.model.FunzioneDiValutazione;

/**
 *
 * Session Bean implementation class CalcolaIntervento
 * Questa classe calcola il risultato di un intervento migliorativo
 * dato un profilo d'uso e dato un interventomigliorativo
 *  
 */
@Stateless
@LocalBean
public class CalcolaIntervento implements CalcolaInterventoRemote, CalcolaInterventoLocal {

    /**
     * Default constructor. 
     */
    public CalcolaIntervento() {
        // TODO Auto-generated constructor stub
    }

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;
    
    @Inject
    private MathExpressionService exprServ;
    
    @Inject
    FattoriNormDao  fattoridao;
    
    @Inject
    private Logger log;
    
    @Inject
    EvaluatorFuzzyMerit evalFuzzy;
    
    
    
    /**
     * compie il calcolo della funzionedivalutazione partendo dall'id del profilo uso consumo
     * diversamente dal'id della  valutazione
     * 
     * @param entityClass classe dell'entita' base da cui si ricavano i dati 
     * @param profiloUsoConsumoId key of entityClass
     * @param funzValutazioneID
     * @param check
     * @param userInsertValPar
     * @return
     */
    public Double calcolaDaPar(Class<? extends Object>  entityClass,Object profiloUsoConsumoId,Long funzValutazioneID,CheckParRecursion check, Map<String, Double> userInsertValPar)
    {
    	
    	
    	   FunzioneDiValutazione  funcVal = em.find(FunzioneDiValutazione.class,funzValutazioneID);
           
           if(funcVal == null)
           {
           	errore("Non esiste alcun funzione di intervento  per l'ID : "+funzValutazioneID);
           	return null;
           }
             
           Object usiconsumi = entityClass.cast(em.find(entityClass, profiloUsoConsumoId ));
           
           if(usiconsumi == null)
           {
           	errore("Non esiste l'entità con l'ID : "+profiloUsoConsumoId);
           	return null;
           }
           
          
           /**
            * espressione del calcolo
            */
           String calcolo = funcVal.getCalcolo();
           
           Map<String,ParamIntervento>  parametri = funcVal.getParametri();
        
         
         
         if (parametri== null || parametri.isEmpty())
         {
       	  errore("Non esistono i parametri associati all'intervento "+funcVal.getDescrizione());
       	  return null;
         }
        
        
         

    	// TipoPar tipo;
    	 ParamIntervento entryPar;
         String nomeParInExpr,jpql,hsql;
         
         // risultato in relazione ai nomi parametri mappati sui nomi par dell'espressione
         // quindi non per forza il campo nome del ParametroIntervento corrisponde al nome 
         // del parametro nell'espressione
         LinkedHashMap<String, Double> valPar=new LinkedHashMap<String, Double>();
        
        // LinkedHashMap<String, ParamIntervento> userInsertValPar= new LinkedHashMap<String, ParamIntervento>();
        
         Double risultatoPar;
         for (Entry<String,ParamIntervento> entry : parametri.entrySet())
         {
               
       	 	nomeParInExpr=entry.getKey();  
       	 	entryPar= entry.getValue();
       	   if (entryPar.getTipoParametro().equals(TipologiaParIntervento.UI_VALUE))
       	   {
       		  if (userInsertValPar!=null)
       		  {
       			risultatoPar=  userInsertValPar.get(entryPar.getNome());
       			if(risultatoPar==null)
       			 {
       				risultatoPar=entryPar.getDefaultValueOnError();
       				errore("parametro UI_VALUE "+entryPar.getNome()+" non ha un valore mappato corrispondente");
       		    	if (risultatoPar==null)return null;
       			 }
       			 //continue;
       		  }else
       		  {
       			  errore("i parametri UI_VALUE hanno abbinato una mappa di valori null");
       			  risultatoPar=entryPar.getDefaultValueOnError();
       			  if (risultatoPar==null)return null;
       			  
       		  }
       		  // salta i parametri user_value
       		  //userInsertValPar.put(nomeParInExpr, entryPar);
       		  
       	  }else
       	  {
       		  // calcolo il risultato passando l'id del profiloUsoConsumo
       		  risultatoPar=calcolaParametro(entityClass,entryPar, profiloUsoConsumoId, check,userInsertValPar);
       		  if(risultatoPar==null) risultatoPar=entryPar.getDefaultValueOnError();
       		  
       		  if (risultatoPar==null)return null;
       		  
       	  }
       	 	
            	
            	
            	// qui il risultato non sarà mai null
            	
            	valPar.put(nomeParInExpr,risultatoPar);
         }
         
         
         
        //LinkedHashMap<String, Double> valPar= recuperaParametri(  parametri,valutazioneID,check);
         
         Double val=null;
       
         if(funcVal.getTipoFunz().equals(TipologiaFunzioneDiValutazione.FUNZ_MAT))
         {
           Set<String> namePar=valPar.keySet();
         
            if (!checkNameNumVarOnExpr(calcolo,namePar))
            {
       	  errore("nome e numero dei parametri non coincidono");
       	  return null;
            }
          
            val=this.exprServ.eval(valPar, calcolo);
            if (val==null)
            {
       	   errore("Il risultato dell'espsessione di calcolo, passati tutti i parametri e' nullo");
       	   return null;
            }
       }else if(funcVal.getTipoFunz().equals(TipologiaFunzioneDiValutazione.FUNZ_FUZZY))
       {
       	val = evalFuzzy.calcFuzzy(funcVal, valPar, null);
       	
       	if (val==null)
           {
      	   errore("Il risultato della funzione fuzzy, passati tutti i parametri, e' nullo");
      	   return null;
           }
       }
          //RisultatoValutazioneIntervento ret= new RisultatoValutazioneIntervento(valutazione, intervento, val);
           message("funzione di valutazione: "+funcVal.getName()+" valore : "+val);
          return val;
    }
    
    /**
     * restituisce un valore calcolando dal modello (interventoMigliorativoID) con i dati del contesto
     * (valutazioneID)
     * 
     * se i ParamIntervento hanno valorizzato defaultvalueOnError con null, in caso di errore  per quel parametro
     * la funzione calcola ritorna tutta con null, xke non è ammesso un valore null 
     * 
     *  
     * @param valutazioneID ID dell'oggetto di Valutazione collegato
     * @param funzValutazioneID ID dell'oggetto Di InterventoMigliorativo 
     * @param check oggetto che controlla i livelli di ricorsione
     * @param userInsertValPar map ui_value parameters,   names with UI values, maybe null when there isnt UI_VALUE par 
     * @return oggetto bean detach con il risultato della valutazione dell'intervento
     */
    public <T> Double calcola(Class<T> entityClass,Object keyID,Long funzValutazioneID,CheckParRecursion check, Map<String, Double> userInsertValPar)
    {
      if (keyID == null || funzValutazioneID == null)
    	  errore("ID chiave dell'entità o l'id della fuzione di valutazione sono null");
    	
    	T valutazione  =	em.find(entityClass,keyID );
        
      
        if (valutazione== null) 
        {
    	  errore("Non esiste alcuna valutazione per ID : "+keyID);
    	  return null;
         }
    
          FunzioneDiValutazione      funcVal      = em.find(FunzioneDiValutazione.class,funzValutazioneID );
       
        if(funcVal == null)
        {
        	errore("Non esiste alcun intervento per ID : "+funzValutazioneID);
        	return null;
        }
          
        /*
        ProfiloUsoConsumo usiconsumi=valutazione.getProfiloUsoConsumo();
        
        if(usiconsumi==null)
        {
        	errore("Non esistono i profili di consumo ed uso associati alla  Valutazione "+valutazione.getId());
        	return null;
        }
        */
       
        /**
         * espressione del calcolo
         */
        String calcolo = funcVal.getCalcolo();
        
        // si ricorda che le chiavi di tale mappa
        // coincidono con i nomi dei parametri di input presenti nelle espressioni (matematiche o fuzzy)
        // tali nomi possono essere diversi dai nomi dei paramIntervento
        // anzi puo succedere che a nomi di parametri diversi corrisponda lo stesso ParamIntervento
      Map<String,ParamIntervento>  parametri = funcVal.getParametri();
     
      
      
      if (parametri== null || parametri.isEmpty())
      {
    	  errore("Non esistono i parametri associati all'intervento "+funcVal.getDescrizione());
    	  return null;
      }
     
     
      

 	// TipoPar tipo;
 	  ParamIntervento entryPar;
      String nomeParInExpr,jpql,hsql;
      
      // risultato in relazione ai nomi parametri mappati sui nomi par dell'espressione
      // quindi non per forza il campo nome del ParamIntervento corrisponde al nome 
      // del parametro nell'espressione
      LinkedHashMap<String, Double> valPar=new LinkedHashMap<String, Double>();
     
     // LinkedHashMap<String, ParamIntervento> userInsertValPar= new LinkedHashMap<String, ParamIntervento>();
     
      Double risultatoPar;
      HashMap<ParamIntervento,Double> paramInterventoCalcolati= new HashMap<ParamIntervento,Double>();
      for (Entry<String,ParamIntervento> entry:parametri.entrySet())
      {
            
    	  nomeParInExpr=entry.getKey();  
    	  entryPar= entry.getValue();
    	 
    	  if(paramInterventoCalcolati.containsKey(entryPar))
    	  {
    		  // evito il ricalcolo se gia è stato valutato un PramIntervento uguale
    		  risultatoPar=paramInterventoCalcolati.get(entryPar);
    		  
    		  
    	  }else 
    		  if 	(entryPar.getTipoParametro().equals(TipologiaParIntervento.UI_VALUE))
    	   {
    		  if (userInsertValPar!=null)
    		  {
    			risultatoPar=  userInsertValPar.get(entryPar.getNome());
    			 if(risultatoPar==null)
    			  {
    				risultatoPar=entryPar.getDefaultValueOnError();
    				errore("parametro UI_VALUE "+entryPar.getNome()+" non ha un valore mappato corrispondente");
    		    	       if (risultatoPar==null)return null;
    		    	       else
    		     		   {
    		     			  // memorizzo i risultati poiche per altri nomePar che fanno riferimento
    		     			  //allo stesso ParamIntervento non lo ricalcolo
    		     			  paramInterventoCalcolati.put(entryPar, risultatoPar);
    		     		   }
    			  }
    			 //continue;
    		  }else
    		  {
    			  errore("i parametri UI_VALUE hanno abbinato una mappa di valori null");
    			  risultatoPar=entryPar.getDefaultValueOnError();
    			  if (risultatoPar==null)return null;
    			  
    			  
    		  }
    		  // salta i parametri user_value
    		  //userInsertValPar.put(nomeParInExpr, entryPar);
    		  
    	   }else
    	   {
    		  risultatoPar=calcolaParametro( valutazione.getClass() ,entryPar, keyID, check,userInsertValPar);
    		  if(risultatoPar==null) risultatoPar=entryPar.getDefaultValueOnError();
    		
    		  if (risultatoPar==null)return null;
    		  
    	  }
    	 	
         	
         	
         	// qui il risultato non sarà mai null
    	  
			  // memorizzo i risultati poiche per altri nomePar che fanno riferimento
			  //allo stesso ParamIntervento non lo ricalcolo
			 if(!paramInterventoCalcolati.containsKey(entryPar)) 
				 {
				 message("parametro in espressione: "+nomeParInExpr+" valore : "+risultatoPar);
		         	valPar.put(nomeParInExpr,risultatoPar);
				 paramInterventoCalcolati.put(entryPar, risultatoPar);
				 }
				message("parametro in espressione: "+nomeParInExpr+" valore : "+risultatoPar);
         	valPar.put(nomeParInExpr,risultatoPar);
      }
      
      
      
     //LinkedHashMap<String, Double> valPar= recuperaParametri(  parametri,valutazioneID,check);
      
      Double val=null;
    
      if(funcVal.getTipoFunz().equals(TipologiaFunzioneDiValutazione.FUNZ_MAT))
      {
        Set<String> namePar=valPar.keySet();
      
         if (!checkNameNumVarOnExpr(calcolo,namePar))
         {
    	  errore("nome e numero dei parametri non coincidono");
    	  return null;
         }
       
         val=this.exprServ.eval(valPar, calcolo);
         if (val==null)
         {
    	   errore("Il risultato dell'espsessione di calcolo, passati tutti i parametri e' nullo");
    	   return null;
         }
    }else if(funcVal.getTipoFunz().equals(TipologiaFunzioneDiValutazione.FUNZ_FUZZY))
    {
    	val = evalFuzzy.calcFuzzy(funcVal, valPar, funcVal.getHowResultNamed());
    	
    	if (val==null)
        {
   	   errore("Il risultato della funzione fuzzy, passati tutti i parametri, e' nullo");
   	   return null;
        }
    }
       //RisultatoValutazioneIntervento ret= new RisultatoValutazioneIntervento(valutazione, intervento, val);
  	message("Funzione di valutazione: "+funcVal.getName()+" valore : "+val);
       return val;
    }
 
    
    /**
     * controlla il numero e il nome dei parametri se sono uguali tra il set passato
     * e il numero e nome dei parametri presenti nell'espressione di calcolo
     * 
     * @param exprCal espressione di calcolo dei parametri 
     * @param namePar  set con il nome di tutti i parametri
     *                
     * @return true se sono uguali, false altrimenti e anche se vi è un problema nella valutazione
     *         del set dei nomi dei parametri
     */
    public boolean checkNameNumVarOnExpr(String exprCal,Set<String> namePar)
    {
      Set<String> exprNamePar=	this.exprServ.retVarNameInExpression(exprCal);
      if (exprNamePar==null) 
      {
    	      errore("L'espressione "+exprCal+", dopo parsializzazione non restituisce parametri");
    	      return false;
      }
      
      if (exprNamePar.size() != namePar.size())
      {
    	  errore("L'espressione "+exprCal+" dopo parsializzazione restituisce "+exprNamePar.size()+" parametri, mentre i par passati sono "+namePar.size());
    	  return false;
      }
    	  
      else
      {
    	  return exprNamePar.containsAll(namePar);
      }
    }
    /**
     * recupera i valori dei parametri dell'intervento
     * @param userInsertValPar 
     * @param check 
     * @param keyID 
     * @param entryPar 
     * @param class1 
     * @param par ParametroIntervento
     * @param entityID  key id dell'entità
     * @return risultato
     */
    public <T>  Double calcolaParametro(Class<? extends Object> class1, ParamIntervento par, Object keyID, CheckParRecursion check, Map<String, Double> userInsertValPar)
    {
    	 TipologiaParIntervento tipo;
         String nomePar,jpql;
         
        // LinkedHashMap<String, Double> valParametri=new LinkedHashMap<String, Double>();
         Double ret=null;
         
       	nomePar=par.getNome();  
         	tipo=par.getTipoParametro();
       	 switch (tipo) {
   		 case FIX_VALUE :
   		  {  // valore fisso
   			//valParametri.put(nomePar, par.getFixValue());
   			ret=par.getFixValue();
   		  }
   			break;
         case UI_VALUE:
          {
           	// valore immesso da utente mediante interfaccia
    		//	valParametri.put(nomePar, par.getUsrValue());
    	    ret=par.getUsrValue();
          }
   		    break;
         case JPQL_VALUE:
          {
           	  jpql=par.getJpqlString();
           	  if (jpql==null) 
           	  {
           		 errore("Non è possibile calcolare il parametro "+par.getNome()+" di tipo JPQL perchè manca la query JPQL");
           		//valParametri.put(nomePar, null);
           		 ret=null;
           	  }
           	 
            try{
            	
            	Object obj;
                Query query = em.createQuery(jpql);
                Number result;
                // impongo che il primo parametro sia l'ID dell' obj di valutazione
                query.setParameter(1, keyID);
                query.setMaxResults(1);
             
                obj=query.getSingleResult(); 
                
                if (obj instanceof Enum)
                {
                	result =((Enum)obj).ordinal();
                }else
                {
                 result =(Number) query.getSingleResult(); 
                }
                
                
            // valParametri.put(nomePar, result.doubleValue());
             
                ret=result.doubleValue();
              }catch (Exception e) 
              {
			   errore("Non è possibile calcolare il parametro "+par.getNome()+" di tipo JPQL perchè la query solleva eccezione "+ e.getMessage() );
			   ret=null;
		      }
            }
    	   break;
           case HSQL_VALUE:
     			// non implementato ancora
     	    break;
           case IMPROVEMENT_ACTION_VALUE:
           {
        	  
        	   FunzioneDiValutazione intervento_da_calcolare=par.getRecuperoValoreIntervento();
        	   if (intervento_da_calcolare==null)
        	   {
        		   errore("Non è possibile calcolare il parametro "+par.getNome()+"ti tipo IMPROVEMENT_ACTION_VALUE perchè manca l'intervento migliorativo da calcolare");
        		   //valParametri.put(nomePar, null);
        		   return null;
        	   }
        	   if(!check.isNewRicursion())
        	   {
        		   // arrivato al limite massimo di ricorsioni
        		   errore("Non è possibile calcolare il parametro "+par.getNome()+" perchè è stato raggiunto il limite max di ricorsione pari a "+check.getNumMaxRecursive());
        		  // valParametri.put(nomePar, null);
        		   
        		   return null;
        	   }
        	/*   
        	 for(ParamIntervento parDaCalc :intervento_da_calcolare.getParametri())
        	 {
        		 if(par.getNome().equals(parDaCalc.getNome()))
        				 {
        			         // ricorsione infinita 
        			         errore();
        			         valParametri.put(nomePar, null);
        			         
        			         
        				 }
        	 }
        	  */
        	 ret=  this.calcolaDaPar(class1,keyID,intervento_da_calcolare.getId() , check,userInsertValPar);
        	 
           }   
     			break;	
           case CUSTOMFUNZ_VALUE:
           {
        	   String nome=par.getNome();
        	   Double val=null;
        	     if(nome.equals("FattNormConsumoTermicoForma"))
        	     {
        	    	val= fattoridao.fattoreE(keyID);
        	    	if(val==null){
        	    		errore("Non è stato possibile calcolare il valore di FattNormConsumoTermicoForma con valutazioneID "+keyID);
        	    	    ret=null;
        	    	}else
        	    	{
        	    		ret=val;
        	    	}
        	    	
        	    	
        	     }else 
        	    	 if(nome.equals("FattNormUso"))
        	    	 {
        	    		 val= fattoridao.fattoreH(keyID);
        	    		 
        	    		 if(val==null){
             	    		errore("Non è stato possibile calcolare il valore di FattNormUso con valutazioneID "+keyID);
             	    	    ret=null;
             	    	}else
             	    	{
             	    		ret=val;
             	    	}
        	    	 }
        	   
        	   
           }
             break;
   		default:
   			break;
   		}
         
       	message("Nome parametro: "+par.getNome()+" valore : "+ret);
         return ret;
    }
    
    public Boolean checkSimbolicExpr(String expr)
    {
    	if (exprServ.eval(expr)==null)
    		return false;
    	else return true;
    	
    }
     
    /**
     * recupera i parametri di input da una espressione che sia di tipo matematico o fuzzy
     * ma verificando prima la sua correttezza
     * Nel caso non sia simbolicamente corretta o non parsializzabile restituisce null
     * @param expr espressione matematica o fuzzy
     * @return set dei nome delle var di input
     */
    public Set<String> getParFromExpr(String expr,TipologiaFunzioneDiValutazione tipo)
    {
    	if(tipo.equals(TipologiaFunzioneDiValutazione.FUNZ_MAT))
    	{	
    	  if (checkSimbolicExpr(expr))
    	  return	this.exprServ.retVarNameInExpression(expr);
    	  else {
    		  errore("Espressione di funzione matematica simbolica non valida");
    		  return null;
    	  }
    	}else if (tipo.equals(TipologiaFunzioneDiValutazione.FUNZ_FUZZY))
    	{
    		FIS fis;
    		fis=evalFuzzy.create(expr);
    		if(fis==null) {
    			errore("Espressione blocco di inferenza fuzzy non valido");
    			return null;
    		}
    		return evalFuzzy.getInVarName(fis, null);
    	}else {
    		errore("Tipo di funzione di valutazione non riconosciuto");
    		return null;
    	}
    	
    }
    
	private void errore(String string) 
	{
		
		log.severe(string);
	}
	
	private void throwing(String sourceMethod,Throwable thrown) 
	{
		
		log.throwing(this.getClass().getCanonicalName(), sourceMethod, thrown);
	}
	
	private void message(String string)
	{
		log.info(string);
	}
	
	/**
	 * Data una funzionediValutazione o un id restituisce i parametri della funzione
	 * è un bean locale quindi posso pensare che l'oggetto sia passato per riferimento 
	 * visto che la mem e' la stessa, altrimenti ho sempre l'id
	 * @param funz
	 * @return
	 */
    public Set<String> setExpr(GenericFunz funz, Long id)
    {
    	GenericFunz funzE=funz;
    	FunzioneDiValutazione funzVal;
    	if ((funz== null) ||(funz.getCalcolo()== null) )
    	{
    		if (id==null) 
    			{
    			   this.errore("Entity della funzione di valutazione non recuperabile");
    			   return null;
    			}
    		
    		
    		funzE= em.<FunzioneDiValutazione>find(FunzioneDiValutazione.class, id);
    	    
    		if (funzE==null)
    		{
    	    	
    			this.errore("Entity della funzione di valutazione  non recuperabile");
    			return null;
    		}else 
    	    {
    	       if (funzE.getCalcolo()==null){
    	    	   this.errore("La funzione di valutazione ha il campo calcolo nullo");
    	    	   return null;	
    	       }
    	    }
    	}
    	
    	funzVal=(FunzioneDiValutazione)funzE;
    	
    		
    		
    		return getParFromExpr(funzE.getCalcolo(),funzVal.getTipoFunz());
    		
    	
    }
	
}
