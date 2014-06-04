package it.enea.lecop.eca.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.TipologiaEdifici;

import javax.ejb.LocalBean;

import javax.ejb.Stateless;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import org.antlr.runtime.RecognitionException;
@Stateless
@LocalBean
public class EvaluatorFuzzyMerit {

	
public EvaluatorFuzzyMerit() {
		
		// TODO Auto-generated constructor stub
	}

 public FIS	create(String FISStr)
	{
	try {
		FIS fis= FIS.createFromString(FISStr, true);
		
		return fis;
	} catch (RecognitionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	}

 public FIS	createFromFileName(String fileName)
	{
	
		FIS fis= FIS.load(fileName,true);
		
		return fis;
	
	}
 
 
 public boolean checkFuzzyFromString(String fuzzyExpr)
 {
	 try {
			FIS fis= FIS.createFromString(fuzzyExpr, true);
			if (fis!=null) return true;
			else return false;
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
 }
 
 /**
  * recupera il nome delle variabili di Input 
  * dalla blocco funzione passato per nome 
  * @param fis oggetto Fuzzy Inference System 
  * @param functionBlockName nome del blocco funzione dal quale vogliamo estrarre i nomi delle varibili
  *         di input; se è null prende il primo blocco funzione
  * @return null in caso di problemi
  */
 public Set<String> getInVarName(FIS fis,String functionBlockName)
 {
	Set<String> retName = new HashSet<String>(); 
	if(fis==null) return null;
	HashMap<String, Variable>  vars=fis.getFunctionBlock(functionBlockName).getVariables();
	 if (vars==null) return null;
	 for(Entry<String,Variable> entry: vars.entrySet())
	 {
		 if(entry.getValue().isInput())
		 {
			 retName.add(entry.getKey());
		 }
	 }
   return retName;
 }
 
 /**
  * recupera il nome delle variabili di OutPut 
  * dalla blocco funzione passato per nome 
  * @param fis oggetto Fuzzy Inference System 
  * @param functionBlockName nome del blocco funzione dal quale vogliamo estrarre i nomi delle varibili
  *         di input; se è null prende il primo blocco funzione
  * @return null in caso di problemi
  */
 public Set<String> getOutVarName(FIS fis,String functionBlockName)
 {
	Set<String> retName = new HashSet<String>(); 
	if(fis==null) return null;
	HashMap<String, Variable>  vars=fis.getFunctionBlock(functionBlockName).getVariables();
	 if (vars==null) return null;
	 for(Entry<String,Variable> entry: vars.entrySet())
	 {
		 if(entry.getValue().isOutput())
		 {
			 retName.add(entry.getKey());
		 }
	 }
   return retName;
 }
 
 
 /**
  * valore di ingresso è il risultato calcolato e la tipologia edifici
  * e il tipo di adifici su cui viene fatto 
  * @param valore
  * @param tipo
  * @return ritorna un valore di merito tra 0 e 10
  */
 public Double indiceDiMeritoIENt(Double valore, TipologiaEdifici tipo)
 {
	 FIS fis=createFromFileName("/home/fab/progetti/SeNeca/fuzzy.fcl");
	 
	 
	 if(fis==null) return null;
	 fis.setVariable("tipoedifici", tipo.ordinal());
	  fis.setVariable("merito_medie_mediesup_tecnici", valore);
	  fis.setVariable("merito_materne", valore);
	  fis.setVariable("merito_elementari", valore);
	  
	  /*
	for(Entry<String,Variable> entry:fis.getFunctionBlock(null).getVariables().entrySet())
	{
		entry.getKey();
	}
	 */
	 fis.evaluate();
	 return fis.getVariable("merit").getValue();
 }
 
 /**
  * il valore di getHowResultNamed() e il nome della variabile controllata risultate 
  * delle regole fuzzy: e' fondamentale specificarla!!
  * 
  * @param funcVal
  * @param varValue
  * @return
  */
 public Double calcFuzzy(FunzioneDiValutazione funcVal,Map<String,Double> varValue, String resultVarControl)
 {
	 Double var;
	 Variable varControll;
	 FIS fis=create(funcVal.getCalcolo());
	 if(fis==null) return null;
	 
	 for(Entry<String,Double> entry: varValue.entrySet())
	 {
		 fis.setVariable(entry.getKey(), entry.getValue());
	 }
	 fis.evaluate();
	 if(resultVarControl==null)
	    {
		   
		     varControll = fis.getVariable(funcVal.getHowResultNamed());
	    }else varControll = fis.getVariable(resultVarControl);
	  
	if (varControll==null) 
		{   
		// recupera la prima var di output
		    Set<String> varOut=getOutVarName(fis, null);
		    if(varOut==null || varOut.isEmpty()) return null;
		    String firstOutVarName=varOut.iterator().next();
		    
		    varControll=fis.getVariable(firstOutVarName);
		    
		    if(varControll==null)
		    return null;
		}
	
	 return varControll.defuzzify();
	 
 }
 
 
 public Double calcFuzzy(String fuzzyExpr ,Map<String,Double> varValue, String resultVarControl)
 {
	 Double var;
	 Variable varControll;
	 FIS fis=create(fuzzyExpr);
	 if(fis==null) return null;
	 
	 for(Entry<String,Double> entry: varValue.entrySet())
	 {
		 fis.setVariable(entry.getKey(), entry.getValue());
	 }
	 fis.evaluate();
	 if(resultVarControl==null)
	    {
		 // recupera la prima var di output
		    Set<String> varOut=getOutVarName(fis, null);
		    if(varOut==null || varOut.isEmpty()) return null;
		    String firstOutVarName=varOut.iterator().next();
		    
		    varControll=fis.getVariable(firstOutVarName);
		    
		    if(varControll==null)
		    return null;
		 
		 
	    }else varControll = fis.getVariable(resultVarControl);
	  
	if (varControll==null) return null;
	
	 return varControll.getValue();
	 
 }
 
 
 public static void main(String[] args) 
    {
	 EvaluatorFuzzyMerit fuzzy=new EvaluatorFuzzyMerit();
	  FIS fis=fuzzy.createFromFileName("/home/fab/progetti/SeNeca/fuzzy.fcl");
	  
	  fis.setVariable("tipoedifici", 2.0);
	  fis.setVariable("merito_medie_mediesup_tecnici",23.39606490184795 );
	  fis.setVariable("merito_materne", 23.39606490184795);
	  fis.setVariable("merito_elementari", 23.39606490184795);
	  fis.evaluate();
	  System.out.println("Output value:" + fis.getVariable("merit").defuzzify()); // Show output variable
	  fis.chart();
	  fis.getVariable("merit").chartDefuzzifier(true);
       
    }
}
