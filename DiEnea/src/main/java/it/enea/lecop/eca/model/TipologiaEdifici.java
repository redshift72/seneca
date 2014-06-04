package it.enea.lecop.eca.model;

public enum TipologiaEdifici {
   SCUOLA_MATERNA("scuola primaria inferiore o dell'infanzia o materna"),
   SCUOLA_ELEMENTARE("scuola primaria superiore o elementare"),
   SCUOLA_MEDIA("scuola secondaria inferiore o media"),
   SCUOLA_SEC_SUP("scuola secondaria superiore ad esclusione degli istituti industriali professionali e statali"),
   SCUOLA_IST_TECN_PROF_IND("istituti industriale professionale e/o tecnico"),
   RESIDENZIALE("di civile abitazione"),
   AZIENDALE("di struttura aziendale");
   
   private final String descrizione;
   
   TipologiaEdifici(String descrizione)
   {
	   this.descrizione=descrizione;
   }
   
   public String descrizione(){
	   return descrizione;
   }
}
