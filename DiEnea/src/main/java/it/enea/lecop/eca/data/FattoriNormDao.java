package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.FattoreNormFormaTermico;
import it.enea.lecop.eca.model.ProfiloUsoConsumo;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.Valutazione;

import java.text.DecimalFormat;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;



@Stateless
@LocalBean
public class FattoriNormDao {
	@Inject
	  EntityManager man;
	
	// superficie disperdente totale
	public static final String SupDisp="Select sum(e.areaDisperdente) FROM ProfiloUsoConsumo p,IN (p.composizioneEdificio.edifici) e " +
			"WHERE p.id = :id" ;
	
	public static final String TipoEdifici="Select e.tipologiaEdifici FROM ProfiloUsoConsumo p,IN (p.composizioneEdificio.edifici) e " +
			"WHERE p.id = :id";
	
	// volumetria lorda riscaldata totale  volumetriaLordaRiscaldata
	/*public static final String VolLordaRisc="Select sum(e.volumetriaLordaRiscaldata) FROM Valutazione v,IN (v.consumiInUso.composizioneEdificio.edifici) e " +
	 *		"WHERE v.id = ?1";
	*/
	
	public static final String VolLordaRisc="Select sum(e.volumetriaLordaRiscaldata) FROM ProfiloUsoConsumo p,IN (p.composizioneEdificio.edifici) e " +
			"WHERE p.id = ?1";
	//public static final String oreUsoTermico="Select v.consumiInUso.oreSuGiorniTermico FROM Valutazione v WHERE v.id = :id";
	public static final String oreUsoTermico="Select p.oreSuGiorniTermico FROM ProfiloUsoConsumo p WHERE p.id = :id";
	
	//public static final String oreUsoElettrico="Select v.consumiInUso.oreSuGiorniElettrico FROM Valutazione v WHERE v.id = :id";
	
	public static final String oreUsoElettrico="Select p.oreSuGiorniElettrico FROM ProfiloUsoConsumo p WHERE p.id = :id";
	
	//public static final String oreUsoIdrico="Select v.consumiInUso.oreSuGiorniAcqua FROM Valutazione v WHERE v.id = :id";
	public static final String oreUsoIdrico="Select p.oreSuGiorniAcqua FROM ProfiloUsoConsumo p WHERE p.id = :id";
	
	
	
	public TipologiaEdifici getTipoEdifici(Object keyID)
	{
		
		Query tipoQuery = man.createQuery(TipoEdifici);
		tipoQuery.setParameter("id", keyID);
		TipologiaEdifici tipoE;
		try{
		List<TipologiaEdifici> result = (List<TipologiaEdifici>) tipoQuery.getResultList();

		tipoE=  (TipologiaEdifici)result.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return tipoE;
	}
	
	/**
	 * funziona solo per key id di ProfiloUsoConsumo
	 * @param ValutazioneID
	 * @return
	 */
	 public Double fattoreE(Object ValutazioneID)
	 {
		 Double resultVal;
		 
		Query supDisperdente= man.createQuery(SupDisp);
		double valSupDisp,valVolLorda,SsuV;
		
		supDisperdente.setParameter("id", ValutazioneID);
		valSupDisp=(Double)supDisperdente.getSingleResult();
		 
		Query volLorda= man.createQuery(VolLordaRisc);
		
		volLorda.setParameter(1,ValutazioneID );
		valVolLorda=(Double)volLorda.getSingleResult();
		
		if (valSupDisp==0 || valVolLorda==0) return null;
		
		SsuV=(valSupDisp/valVolLorda);
		// taglio ai 2 decimali
	    SsuV=((long) (SsuV * 1e2) )/ 1e2;
		
		
		

		TipologiaEdifici tipoE=  getTipoEdifici(ValutazioneID);
		if(tipoE==null) return null;
	// debug	
	//	System.out.println("tipo "+ tipoE );
	//	System.out.println("ssuv "+ SsuV );
	//	System.out.flush();
		
		
		Query fattoreq= man.createNamedQuery("Fe.valore");
		
		fattoreq.setParameter("tipo", tipoE);
		
		
		fattoreq.setParameter("ssuv", SsuV);
		resultVal=(Double)fattoreq.getSingleResult();
		
		
		
		
		
		
		
		
		 return resultVal;
	 }
	 
	 
	 /**
	  * funziona sul profilo uso consumo
	  * @param ValutazioneID
	  * @return
	  */
	 public Double fattoreH(Object ValutazioneID)
	 {
		 double oreuso;
		 Double resultVal=null;
		 ProfiloUsoConsumo val= man.find(ProfiloUsoConsumo.class,ValutazioneID );
		 if (val==null)
		 {
			 // log
			 System.out.println("Attenzione non recupero la valutazione");
			 return null;
		 }
		 
		 TipologiaEdifici tipoE=getTipoEdifici(ValutazioneID);
		 if(tipoE==null){
			 // log
			 System.out.println("Attenzione non recupero il tipo");
			 
			 return null;
		 }
		 Query oreSuGiorniQ=null;
		 
		 if(val.getTipo().equals(TipologiaValutazione.TERMICA))
		 {
			 oreSuGiorniQ= man.createQuery(oreUsoTermico);
		 }
		 else 
			 if (val.getTipo().equals(TipologiaValutazione.ELETTRICA))
			 {
				 oreSuGiorniQ= man.createQuery(oreUsoElettrico);
			 }else
				 if (val.getTipo().equals(TipologiaValutazione.IDRICA))
				 {
					 oreSuGiorniQ= man.createQuery(oreUsoIdrico);
				 }
		 
				if ( oreSuGiorniQ==null) return null;
		 
		 oreSuGiorniQ.setParameter("id",ValutazioneID );
		try{	
			
			oreuso=(Double)oreSuGiorniQ.getSingleResult();
			
		long valoreHsuG=	Math.round(oreuso);
		Double valToq=new Double(valoreHsuG);
// SELECT u.valoreFattoreH FROM FattoreNormUso u, IN (u.tipiApplicati) t WHERE (t = :tipo) AND ((:h >=  u.oreGiornoMIN) AND (:h <= u.oraGiornoMAX))"
	// debug
    //	System.out.println("---->> ore uso: "+valToq);
	//	System.out.println("---->> tipo   : "+tipoE);
	//	System.out.flush();
		
         Query fattoreq= man.createNamedQuery("Fh.valore");
		
		fattoreq.setParameter("tipo", tipoE);
		fattoreq.setParameter("h", valToq);
		
		resultVal=(Double)fattoreq.getSingleResult();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		 return resultVal;
	 }
}
