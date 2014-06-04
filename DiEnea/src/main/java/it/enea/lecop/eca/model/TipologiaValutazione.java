package it.enea.lecop.eca.model;

public enum TipologiaValutazione {
	 TERMICA("valutazione dei consumi termici"),
	 ELETTRICA("valutazione dei consumi elettrici"),
	 IDRICA("valutazione dei consumi idrici");
	   
	   
	   private final String descrizione;
	   
	   TipologiaValutazione(String descrizione)
	   {
		   this.descrizione=descrizione;
	   }
	   
	   public String descrizione(){
		   return descrizione;
	   }
}
