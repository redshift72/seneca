package it.enea.lecop.eca.model;


/*
 * 
    *  0 parametro immesso a valore fisso
    *  1 parametro immesso da utente mediante intefaccia
    *  2 parametro recuperato tramite esecuzione query dinamica Java Persistent QL
    *  3 parametro recuperato tramite esecuzione query dinamica Hibernate SQL    
    *  4 parametro calcolato in sede di calcolo della valutazione da funzione custom
    *  5 parametro risultato da funzione di valutazione o intervento migliorativo
    *  i parametri sono prima valutati o recuperati mediante interfaccia 
    *  poi sono valutati nella forma finale
 */
public enum TipologiaParIntervento {
	 FIX_VALUE("parametro immesso a valore fisso"),
	 UI_VALUE("parametro immesso da utente medinate UI"),
	 JPQL_VALUE("parametro recuperato tramite esecuzione query JPQL"),
	 HSQL_VALUE("parametro recuaperato tramite esecuzione quesry HSQL"),
	 CUSTOMFUNZ_VALUE("parametro recuperato mediante esecuzione funzione customizzata interna"),
	 IMPROVEMENT_ACTION_VALUE("parametro risultato di una funzione di valutazione o intervento migliorativo");
	   
	   private final String descrizione;
	   
	   TipologiaParIntervento(String descrizione)
	   {
		   this.descrizione=descrizione;
	   }
	   
	   public String descrizione(){
		   return descrizione;
	   }
}
