package it.enea.lecop.eca.model;

public enum TipologiaConsumi {
	
	   KWH_EL(2.17,"Kwh", "Kwh el.",TipologiaValutazione.TERMICA ),
	   KWH_T(1.0, "Kwh", "Kwh term.",TipologiaValutazione.TERMICA),
	   GAS_METANO(9.59, "m^3", "Metano",TipologiaValutazione.TERMICA),
	   GASOLIO(11.86, "Lt", "Gasolio",TipologiaValutazione.TERMICA),
	   OLIO_FLUIDO(11.40,"Lt", "Olio Fluido",TipologiaValutazione.TERMICA),
	   GPL(12.79,"Lt", "Gpl",TipologiaValutazione.TERMICA ),
	   LEGNA(4.77,"Kg","Legna",TipologiaValutazione.TERMICA),
	   CARBONE_FOSSILE(8.15,"Kg", "Carbone",TipologiaValutazione.TERMICA),
	   CALORE_RETE(1.55E11,"Mcal", "Tele risc.",TipologiaValutazione.TERMICA),
	   KWH_E(1.0,"Kwh","Kwh elettrico",TipologiaValutazione.ELETTRICA ),
	   ACQUA_FREDDA(1.0,"m^3","Acqua fredda",TipologiaValutazione.IDRICA );

	   
	   private final Double fattore2KWH_T;
	   private final String unitaMisura; 
	   private final String descrizione;
	   private final TipologiaValutazione tipo;
	   
	   TipologiaConsumi(Double fatt, String unita, String descr,TipologiaValutazione tipo)
	   {
		   this.fattore2KWH_T=fatt;
		   this.unitaMisura=unita;
		   this.descrizione=descr;
		   this.tipo=tipo;
	   }
	   
	   public Double fattore2KWH_T(){
		   return fattore2KWH_T;
	   }

	   
	   public String unitaMisura(){
		   return unitaMisura;
	   }
	   
	   public String descrizione(){
		   
		   return descrizione;
	   }
	   public TipologiaValutazione tipo()
	   {
		   return tipo;
	   }
}
	