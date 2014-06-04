package it.enea.lecop.eca.model;

public enum TipologiaFunzioneDiValutazione {
	FUNZ_MAT("funzione matematica"),
	FUNZ_FUZZY("funzione in fuzzy logic");
	 
	   
	   
	   private final String descrizione;
	   
	   TipologiaFunzioneDiValutazione(String descrizione)
	   {
		   this.descrizione=descrizione;
	   }
	   
	   public String descrizione(){
		   return descrizione;
	   }
}
