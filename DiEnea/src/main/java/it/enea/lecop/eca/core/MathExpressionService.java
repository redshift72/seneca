package it.enea.lecop.eca.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.matheclipse.basic.Config;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.StringBufferWriter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.eval.DoubleVariable;

import edu.jas.kern.ComputerThreads;

/**
 * Session Bean implementation class MathExpressionService
 */
@Singleton
@LocalBean
public class MathExpressionService {

	private DoubleEvaluator engine;
	private EvalUtilities symbolEngine;
	
    /**
     * Default constructor. 
     */
    public MathExpressionService() {
        
    }
@PostConstruct
 public void initialize()
 {

	 F.initSymbols(null);
	 symbolEngine = new EvalUtilities();
	    
	  
	
	
	 engine= new DoubleEvaluator();
	
	/*
	String stringResult = null;
    ScriptEngineManager scriptManager = new ScriptEngineManager();
     engine = scriptManager.getEngineByExtension("m");
     */
 }

   /**
    * passa le variabili con il nome ed il valore all'espressione
    * e valuta l'espressione
    * @param variabili
    * @param espressione
    * @return
    */
  public Double eval(Map<String,Double> variabili,String espressione)
  {
	  Double ret;
	  Object ret1;
	  IExpr ret2;
	  ASTNode node;
	  StringBufferWriter buf = new StringBufferWriter();     
	  
	  
	  try{
		  
		ret2=  eval(espressione);
		if(ret2==null)
		{
			System.out.println("espressione aritmetica non valida");
			System.out.flush();
			return null;
		}else
		{
			System.out.println("espressione aritmetica valida");
			System.out.flush();
		}
		
		  OutputFormFactory.get().convert(buf, ret2);
		  
		 String strVal=ret2.toString();
		 
		  for(Entry<String, Double> entry : variabili.entrySet())
		  {
			  
			  engine.defineVariable(entry.getKey(), new DoubleVariable(entry.getValue()));
		  }
		  
		  
		 System.out.println( "risultato dalla valutazione :"+strVal);
		  
		ret= engine.evaluate(strVal);
		engine.clearVariables();
		
		return ret;
	  }catch(ArithmeticException ex)
	  {    System.out.println("eccezione aritmetica :");
		  ex.printStackTrace();
		  return null;
		
	  }catch(Exception ex)
	  {
		  ex.printStackTrace();
		  return null;
	  }
  }
 
  /**
   * ritorna i nomi di variabili dall'espressione
   * 
   * Puo servire per verificare che tutte le varibili dell'espresione abbiano
   * un lo stesso identico insime di parametri definiti
   * @param expression
   * @return
   */
  public Set<String>  retVarNameInExpression(String expression)
  {
	  Set<String> varName= new HashSet<String>();
	  
	  
	  DoubleEvaluator.getVariables(expression, varName);
	  
	  return varName;
  }
  
  
  public static void main(String[] args) 
   {
	   MathExpressionService math= new  MathExpressionService();
	   math.initialize();
	   double ret2;
	   HashMap<String,Double> val= new HashMap<String, Double>();
	   val.put("a", 3.2);
	   val.put("b", 1.8);
	  // String expr="a+(b)*Sin[Pi]+D[Tan[a^2],a]+(a-3)^5";
	   String expr="ciccio+ç@@+-+-+-+-+@+a+b";
	   ret2=math.eval(val,expr);   
	   System.out.println("risultato :"+ ret2);
	  
	   ComputerThreads.terminate();
	  
    }
  
  public IExpr eval(String expr)
  {
	 try {
		return symbolEngine.evaluate(expr);
	} catch (Exception e) {
		return null;
		
	}
  }
}
