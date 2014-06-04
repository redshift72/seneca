package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.Comune;
import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.FunzioneDiValutazione;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.ParamIntervento;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.SecAttrib;
import it.enea.lecop.eca.model.Securable;
import it.enea.lecop.eca.model.TipologiaEdifici;
import it.enea.lecop.eca.model.TipologiaParIntervento;
import it.enea.lecop.eca.model.TipologiaValutazione;
import it.enea.lecop.eca.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NamedQuery;
import javax.persistence.Query;



public class SecureGenericSelect {

	public static String secQuery="SELECT u FROM ???? u WHERE (:username = 'admin') or ((u.ownerid.ownUser.username = :username) and (" +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec1) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec2) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec3) or " +
                   		       "(u.permissionprop.OWNERUSER = :ownuserSec4)))" +
                   		       " or (" +
                   		       "(u.ownerid.ownCompany.name = :domainname) and (" +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec1) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec2) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec3) or " +
                   		       "(u.permissionprop.IDENTITYDOMAIN = :owndomainSec4)))"       
                   		    +   "or (( SIZE(u.ownerid.ownCompany.aziende) = (SELECT COUNT(DISTINCT azi.nome) FROM CompanyDomain cd, IN(cd.aziende) azi, ???? az, IN (az.ownerid.ownCompany.aziende ) ownazi WHERE (cd.name = :domainname) and (az = u) and (ownazi.nome = azi.nome))) " +
                   		             "and (u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1 , :ownsubsetSec2 , :ownsubsetSec3 , :ownsubsetSec4 )))" +
                   		        "or (u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4)) ";
                   		//     +   "or  (( SIZE(u.ownerid.ownCompany.aziende) < (SELECT COUNT(DISTINCT az) FROM IN (u.ownerid.ownCompany.aziende) az WHERE az MEMBER OF :aziendeLogin)) " +
                   		//             "and (u.permissionprop.INTERSECTIONDOMAIN IN (:ownintersecSec1,:ownintersecSec2,:ownintersecSec3,:ownintersecSec4)))";
	                    // SELECT azi FROM CompanyDomain cd,IN (cd.aziende) azi WHERE cd.name = :domainname

	// query speciale che si usa qundo il num di aziede contenute nel dominio di login e' zero 
	public static String secZeroQuery="SELECT u FROM ???? u WHERE (:username = 'admin') " +
			 " or (" +
			 " (u.ownerid.ownUser.username = :username) and (" +
		       "(u.permissionprop.OWNERUSER IN (:ownuserSec1, :ownuserSec2, :ownuserSec3, :ownuserSec4)))) " +
		       " or (" +
		       "(u.ownerid.ownCompany.name = :domainname) and (" +
		       "(u.permissionprop.IDENTITYDOMAIN IN ( :owndomainSec1,:owndomainSec2,:owndomainSec3,:owndomainSec4)))) " +
		       "or (" +
		          "(( SIZE(u.ownerid.ownCompany.aziende) = 0 ) and (" +
		          "u.permissionprop.SUBSETDOMAIN IN (:ownsubsetSec1 , :ownsubsetSec2 , :ownsubsetSec3 , :ownsubsetSec4 ))))" +
		        "or (" +
		        "u.permissionprop.OTHER IN (:otherSec1 , :otherSec2 , :otherSec3 , :otherSec4)) ";
	
	public static String subQuery="Select z From Edificio z where SIZE(z.ownerid.ownCompany.aziende) =  (SELECT COUNT(DISTINCT azi.nome) FROM CompanyDomain cd, IN(cd.aziende) azi, Edificio az, IN (az.ownerid.ownCompany.aziende ) ownazi WHERE cd.name = :domainname and az = z and ownazi.nome = azi.nome)";
	
	public static String genericFindAllQuery="SELECT u FROM ???? u";
	
	static public <T> List<T> getAllNoSec(Class<T> classEntity,EntityManager em)
	{
		T obj;
		List<T> result=new ArrayList<T>();;
		
		String nameclass=classEntity.getName();
		// debug
		// System.out.println("--->>>Nome esteso della classe "+nameclass);
		// System.out.flush();  
		
		String nameClassNuova=nameclass.replace('.', ':');
		String[] nomeArr=nameClassNuova.split(":");
		
		if ((nomeArr == null)  ||  nomeArr.length == 0)
		{
			// log
			System.out.println("Non riesco a splittare il punto; lunghezza array "+nomeArr.length);
			System.out.flush();
			return null;
		}
		
		String simpleName=nomeArr[(nomeArr.length)-1];
		String usefullQuery;
		
		usefullQuery=genericFindAllQuery.replace("????",simpleName );
		// debug
		//System.out.println("----->>>>>>JPQL result query: "+usefullQuery);
		//System.out.flush();
		
		Query	query=em.createQuery(usefullQuery);
	   
		try{
			result =(List<T>)query.getResultList();
		}catch (EntityNotFoundException e) {
			// TODO: handle exception
			return new ArrayList<T>();
		}
		
		
		return result;
		
	}
	static public <T> List<T> getAll(Class<T> classEntity,EntityManager em,String ownLoginUserName,CompanyDomain loginDomain, SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] subDomainSec,SecAttrib[] otherSec)
	{
		
		T obj;
		
		
		String nameclass=classEntity.getName();
		List<T> result,resutlNoOwnerNoDomain,filtredResult= new ArrayList<T>();
		List<Azienda> aziendeDomainLogin;
		
		// debug
		//System.out.println("--->>>Nome esteso della classe "+nameclass);
		//System.out.flush();  
		
		
		
		
		String nameClassNuova=nameclass.replace('.', ':');
		String[] nomeArr=nameClassNuova.split(":");
		
		//System.out.println("Nome esteso della classe nuova con sostituzione di . con : "+nameClassNuova);
		//System.out.flush();  
	
		if ((nomeArr == null)  ||  nomeArr.length == 0)
		{
			System.out.println("Non riesco a splittare il punto; lunghezza array "+nomeArr.length);
			System.out.flush();
			return null;
		}
		String simpleName=nomeArr[(nomeArr.length)-1];
		String usefullSecQuery;
		boolean isZero=false;
		if(loginDomain.getAziende()==null || loginDomain.getAziende().size() == 0)
		{
			usefullSecQuery=secZeroQuery.replace("????",simpleName );
					isZero=true;
			
		}else
		{
		usefullSecQuery=secQuery.replace("????",simpleName );
		}
		// deve esistere questa query named per ogni classe entity che volgio cercare con i critari si sicurezza
	   // Query	query=em.createNamedQuery(simpleName+".findAllOWNERUSER_OWNDOMAIN");
		
		
	/*
		Query subquery=em.createQuery(subQuery);
		subquery.setParameter("domainname", loginDomain.getName());
		
		List<Edificio> aziendeLogDom = subquery.getResultList();
		System.out.println("---<<<<<< LISTA AZIENDE DEL DOMINIO DI LOGIN >>>>>>---");
		System.out.flush();
		for (Edificio azname:aziendeLogDom)
		{
			System.out.println(">>>>>>---"+azname.getNome());
			System.out.flush();
		}
		*/
		
		/*
		User userq=em.find(User.class, ownLoginUserName);
		
		Query comq=em.createNamedQuery("Comune.findAll");
		List<Comune> com=comq.getResultList();
		System.out.println("---<<<<<< modifico tutti i comuni >>>>>>---");
		System.out.flush();
		for(Comune cm:com)
		{
			
			cm.setOwnerid(new OwnerId(userq, loginDomain));
			cm.setPermissionprop(new PermissionProp(SecAttrib.CONTROL, SecAttrib.CONTROL, SecAttrib.READ, SecAttrib.READ, SecAttrib.READ));
			em.merge(cm);
		}
		System.out.println("---<<<<<< fatto >>>>>>---");
		
		
		*/
		
		
		/* Segue codice di inserimento parametri fondamentali e funzioni di valutazione    */
		
		/*
		User userq=em.find(User.class, ownLoginUserName);
		OwnerId own=new OwnerId(userq, loginDomain);
		PermissionProp prop= new PermissionProp(SecAttrib.CONTROL, SecAttrib.CONTROL, SecAttrib.READ, SecAttrib.READ, SecAttrib.READ);
		
		ParamIntervento totConsumoTermico= new ParamIntervento("TotConsumoTermico", "KW/h t", 0, 0, "Select v.consumiInUso.consumoTermicoTot FROM Valutazione v WHERE v.id = ?1", null, "Consumo termico annuo totale in kw/h termici", (double) 0, null,TipologiaParIntervento.JPQL_VALUE);
		totConsumoTermico.setOwnerid(own);
		totConsumoTermico.setPermissionprop(prop);
		ParamIntervento totConsumoElettrico= new ParamIntervento("TotConsumoElettrico", "KW/h e", 0, 0, "Select v.consumiInUso.consumoElettricoTot FROM Valutazione v WHERE v.id = ?1", null, "Consumo elettrico annuo totale in kw/h elettrici", (double) 0, null,TipologiaParIntervento.JPQL_VALUE);
		
		totConsumoElettrico.setOwnerid(own);
		totConsumoElettrico.setPermissionprop(prop);
		
		ParamIntervento  gradiGiornoDellaLoc = new ParamIntervento("GradiGiornoDellaLoc", "#", 0, 0, "Select v.consumiInUso.comuneUbicazione.gradigiorno FROM Valutazione v WHERE v.id = ?1", null, "Gradi giorno della localita", (double)0, null, TipologiaParIntervento.JPQL_VALUE);
		gradiGiornoDellaLoc.setOwnerid(own);
		gradiGiornoDellaLoc.setPermissionprop(prop);
		
		ParamIntervento  fattoreNormConsumoTermicoForma = new ParamIntervento("FattNormConsumoTermicoForma", "#", 0, 0, null, null, "Fattore di Normalizzazione del consumo termico dovuto alla forma (S/V) usato edifici scolastici", (double)0, null, TipologiaParIntervento.CUSTOMFUNZ_VALUE);
		fattoreNormConsumoTermicoForma.setOwnerid(own);
		fattoreNormConsumoTermicoForma.setPermissionprop(prop);
		
		ParamIntervento  fattoreNormUso = new ParamIntervento("FattNormUso", "#", 0, 0, null, null, "Fattore di Normalizzazione dell USO basato sulle ore giorno, usato per edifici scolastici", (double)0, null, TipologiaParIntervento.CUSTOMFUNZ_VALUE);
		fattoreNormUso.setOwnerid(own);
		fattoreNormUso.setPermissionprop(prop);
		
		ParamIntervento  volLordaRiscaldata = new ParamIntervento("VolLordaRiscaldataTot", "m^3", 0, 0, FattoriNormDao.VolLordaRisc, null, "Volumetria lorda riscaldata totale della somma degli edifici", (double)0, null, TipologiaParIntervento.JPQL_VALUE);
		volLordaRiscaldata.setOwnerid(own);
		volLordaRiscaldata.setPermissionprop(prop);
		
		ParamIntervento  supLordaPiani = new ParamIntervento("SupLordaPianiTot", "m^2", 0, 0, "Select sum(e.superficieLordaPiani) FROM Valutazione v,IN (v.consumiInUso.composizioneEdificio.edifici) e WHERE v.id = ?1", null, "Superficie lorda ai piani totale, somma degli edifici", (double)0, null, TipologiaParIntervento.JPQL_VALUE);
		supLordaPiani.setOwnerid(own);
		supLordaPiani.setPermissionprop(prop);
		
		
	    FunzioneDiValutazione funzValTermicaScuole = new FunzioneDiValutazione();
	    funzValTermicaScuole.addParametro("TotConsumoTermico", totConsumoTermico);
	    funzValTermicaScuole.addParametro("GradiGiornoDellaLoc", gradiGiornoDellaLoc);
	    funzValTermicaScuole.addParametro("FattNormConsumoTermicoForma", fattoreNormConsumoTermicoForma);
	    funzValTermicaScuole.addParametro("FattNormUso", fattoreNormUso);
	    funzValTermicaScuole.addParametro("VolLordaRiscaldataTot", volLordaRiscaldata);
	    
	    funzValTermicaScuole.setApplicaTipoValutazione(TipologiaValutazione.TERMICA);
	    funzValTermicaScuole.setCalcolo("(TotConsumoTermico * FattNormConsumoTermicoForma * FattNormUso * 1000) / (VolLordaRiscaldataTot * GradiGiornoDellaLoc)");
	    funzValTermicaScuole.setDescrizione("calcola l'indicatore energetico termico normalizzato per le scuole");
	    
	    funzValTermicaScuole.setName("IEN_TERMICO_SCUOLE");
	    HashSet<TipologiaEdifici> edificiTipo= new HashSet<TipologiaEdifici>();
	    edificiTipo.add(TipologiaEdifici.SCUOLA_MATERNA);
	    edificiTipo.add(TipologiaEdifici.SCUOLA_ELEMENTARE);
	    edificiTipo.add(TipologiaEdifici.SCUOLA_MEDIA);
	    edificiTipo.add(TipologiaEdifici.SCUOLA_SEC_SUP);
	    edificiTipo.add(TipologiaEdifici.SCUOLA_IST_TECN_PROF_IND);
	    
	    funzValTermicaScuole.setApplicaTipoEdifici(edificiTipo);
	    
	    funzValTermicaScuole.setOwnerid(own);
	    funzValTermicaScuole.setPermissionprop(prop);
	    
	    
	    FunzioneDiValutazione funzValElettricaScuole = new FunzioneDiValutazione();
	    funzValElettricaScuole.addParametro("TotConsumoElettrico", totConsumoElettrico);
	    funzValElettricaScuole.addParametro("SupLordaPianiTot", supLordaPiani);
	    funzValElettricaScuole.addParametro("FattNormUso", fattoreNormUso);
	    
	    funzValElettricaScuole.setApplicaTipoValutazione(TipologiaValutazione.ELETTRICA);
	    funzValElettricaScuole.setCalcolo("(TotConsumoElettrico * FattNormUso) / SupLordaPianiTot");
	    funzValElettricaScuole.setDescrizione("calcola l'indicatore energetico elettrico normalizzato per le scuole");
	    funzValElettricaScuole.setName("IEN_ELETTRICO_SCUOLE");
	    funzValElettricaScuole.setApplicaTipoEdifici(edificiTipo);
	    
	    funzValElettricaScuole.setOwnerid(own);
	    funzValElettricaScuole.setPermissionprop(prop);
	    
	    
	    em.persist(totConsumoTermico);
	    em.persist(totConsumoElettrico);
	    em.persist(gradiGiornoDellaLoc);
	    em.persist(fattoreNormConsumoTermicoForma);
	    em.persist(fattoreNormUso);
	    em.persist(volLordaRiscaldata);
	    em.persist(supLordaPiani);
	    
	    em.persist(funzValTermicaScuole);
	    em.persist(funzValElettricaScuole);
	    */
		
		// debug
		// System.out.println("----->>>>>>JPQL result query: "+usefullSecQuery);
		// System.out.flush();
		
		Query	query=em.createQuery(usefullSecQuery);
	    query.setParameter("username",ownLoginUserName );
	    query.setParameter("domainname",loginDomain.getName() );
	
	    // debug
	    // System.out.println("---->>>>>>Username "+ownLoginUserName +" Domainname: "+loginDomain.getName());
	    // System.out.flush();
	    
	    
	    // if(!isZero) query.setParameter("aziendeLogin",loginDomain.getAziende() );
	    
	   
	    for(int i=0;i<4;i++)
	    {
	    	query.setParameter("ownuserSec"+(i+1),userSec[i] );
	    	query.setParameter("owndomainSec"+(i+1),domainSec[i] );
	    	
	    	
	    	query.setParameter("ownsubsetSec"+(i+1),subDomainSec[i] );
	    	query.setParameter("otherSec"+(i+1),otherSec[i] );
	    }
	               
	    result =(List<T>)query.getResultList();
	    
	    /* basato su elaborazione in java del risultato di una named query che valuta solo l'utente owner e il dominio owner 
	    obj=result.get(0);
	      if (Securable.class.isInstance(obj))
	      {
	    	  
	    	  
	    	  // entita' che non hanno sia lo user di login come owner, sia il dominio di login con  OWNERUSEROWNDOMAIN
	    	  // vedi named Query di Azienda
	    	  Query	queryNonOwner_Domain=em.createNamedQuery(simpleName+".findAllNO_OWNERUSEROWNDOMAIN");
	    	  queryNonOwner_Domain.setParameter("username", ownLoginUserName);
	    	  queryNonOwner_Domain.setParameter("domainname",loginDomain.getName());
	    	  
	    	  resutlNoOwnerNoDomain=queryNonOwner_Domain.getResultList();
	    	  if(resutlNoOwnerNoDomain==null) return null;
	    	  else
	    	  {
	    		  if (resutlNoOwnerNoDomain.isEmpty())
	    		  {   // se e' vuoto vuol dire che tutte le entità hanno come proprietario username o domain di login
	    			  // e non vi e' alcuna entita' che non rientra in questa categoria
	    			  return result;
	    		  }else
	    		  {
	    			  // devo verificare subdomain e other
	    			  Query	queryC=em.createNamedQuery("CompanyDomain.findByName");  
	    	    	  queryC.setParameter("name", loginDomain.getName());
	    	    	  // aziende che appartengono al dominio di login
	    	    	  aziendeDomainLogin=queryC.getResultList();
	    	    	  if (aziendeDomainLogin==null) return null;
	    	    	  
	    	    	  for( T val: resutlNoOwnerNoDomain)
	    	    	  {
	    	    		  
	    	    		  if (aziendeDomainLogin.containsAll(((Securable)val).getOwnerid().getOwnCompany().getAziende()) )
	    	    		  {
	    	    			 SecAttrib secSub= ((Securable)val).getPermissionprop().getSUBSETDOMAIN();
	    	    			 if(secSub.equals(subDomainSec[0]) || secSub.equals(subDomainSec[1]) || secSub.equals(subDomainSec[2]) || secSub.equals(subDomainSec[3]))
	    	    			 {
	    	    				 result.add(val);
	    	    				 
	    	    			 }
	    	    			  
	    	    		  }	 else
	    	    		  {
	    	    			  SecAttrib secSub= ((Securable)val).getPermissionprop().getOTHER();
	    	    			  if(secSub.equals(otherSec[0]) || secSub.equals(otherSec[1]) || secSub.equals(otherSec[2]) || secSub.equals(otherSec[3]))
		    	    			 {
		    	    				 result.add(val);
		    	    			 }
	    	    		  }
	    	    			  
	    	    		  
	    	    	  }
	    		  }
	    	  }
	    	  
	    	  
	    	  
	    	  
	    	  
	    	  return result;
	      }else
	      {
	    	  return null;
	      }
		*/
		return result;
	}

// cambio stirng in Long per vedere dove uso il metodo
	static public <T> List<T> getAll(Class<T> classEntity,EntityManager em,Long ownLoginUserName,String ownLoginDomainName, SecAttrib[] userSec,SecAttrib[] domainSec,SecAttrib[] otherSec)
	{
		
		
		String nameclass=classEntity.getName();
		// debug
		//System.out.println("Nome esteso della classe "+nameclass);
		//System.out.flush();  
		
		
		String nameClassNuova=nameclass.replace('.', ':');
		String[] nomeArr=nameClassNuova.split(":");
		
		// debug
		//System.out.println("Nome esteso della classe nuova con sostituzione di . con : "+nameClassNuova);
		//System.out.flush();  
	
		if ((nomeArr == null)  ||  nomeArr.length == 0)
		{
			// log 
			System.out.println("Non riesco a splittare il punto; lunghezza array "+nomeArr.length);
			System.out.flush();
			return null;
		}
		
		String simpleName=nomeArr[(nomeArr.length)-1];
		
		
	    Query	query=em.createNamedQuery(simpleName+".findAllOWNERUSER_OWNDOMAIN");
	
	    query.setParameter("username",ownLoginUserName );
	    query.setParameter("domainname",ownLoginDomainName );
	    
	    for(int i=0;i<4;i++)
	    {
	    	query.setParameter("ownuserSec"+(i+1),userSec[i] );
	    	query.setParameter("owndomainSec"+(i+1),domainSec[i] );
	    	query.setParameter("otherSec"+(i+1),otherSec[i] );
	    }
	      
		
		return (List<T>)query.getResultList();
	}


}
