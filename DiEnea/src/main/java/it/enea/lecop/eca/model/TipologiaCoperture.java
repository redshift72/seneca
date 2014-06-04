package it.enea.lecop.eca.model;

public enum TipologiaCoperture {
	   COPERTURA_SHED(""),
	   COPERTURA_PIANA(""),
	   COPERTURA_PIANA_VOLTINE("");
	   
	   private final String descrizione;
	   
	   TipologiaCoperture(String descrizione)
	   {
		   this.descrizione=descrizione;
	   }
	   
	   public String descrizione(){
		   return descrizione;
	   }
}
