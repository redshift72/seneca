package it.enea.lecop.eca.core;

import it.enea.lecop.eca.data.CompanyDomainDao;
import it.enea.lecop.eca.data.MenuItemDao;
import it.enea.lecop.eca.data.UserDao;
import it.enea.lecop.eca.model.CompanyDomain;
import it.enea.lecop.eca.model.MenuItem;
import it.enea.lecop.eca.model.OwnerId;
import it.enea.lecop.eca.model.PermissionProp;
import it.enea.lecop.eca.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;


/**
 * Session Bean implementation class MathExpressionService
 */
@Singleton
@LocalBean
public class InitService {

	@Inject
	  UserDao usd;
	@Inject
	  CompanyDomainDao cdd;
	@Inject
	   MenuItemDao  itemdao;
	@Inject
	  EntityManager eman;
	
	
	/**
	 * serve solo per forzare l'As a costruire il componente InitService
	 * chiamando il metodo @Postconstructor
	 */
	public void foo()
	{
		init();
		return;
	}
/*	
	<li>
	<h:link outcome="/azienda/search" value="Azienda"/>
</li>
<li>
	<h:link outcome="/viewvaadin/compedifici" value="Composizione Edifici"/>
</li>
<li>
	<h:link outcome="/comune/search" value="Comune"/>
</li>
<li>
	<h:link outcome="/edificio/search" value="Edificio"/>
</li>
<!-- 	<li>   -->
<!--		<h:link outcome="/gradigiorno/search" value="Gradigiorno"/>   -->
<!--	</li>   -->
<li>
	<h:link outcome="/interventoMigliorativo/search" value="Intervento Migliorativo"/>
</li>
<!-- 	<li> -->
<!--		<h:link outcome="/member/search" value="Member"/>  -->
<!--	</li>	-->			
<li>
	<h:link outcome="/paramIntervento/search" value="Param Intervento"/>
</li>
<li>
	<h:link outcome="/profiloUsoConsumo/search" value="Profilo Uso Consumo"/>
</li>
<li>
	<h:link outcome="/risultatoValutazioneIntervento/search" value="Risultato Valutazione Intervento"/>
</li>
<!--	<li>  -->
<!--		<h:link outcome="/tipoEdificio/search" value="Tipo Edificio"/>  -->
<!--	</li>  -->
<li>
	<h:link outcome="/tipoParIntervento/search" value="Tipo Par Intervento"/>
</li>
<li>
	<h:link outcome="/tipoValutazione/search" value="Tipo Valutazione"/>
</li>
<li>
	<h:link outcome="/valutazione/search" value="Valutazione"/>
</li>
<li>
	<h:link outcome="/viewvaadin/search" value="Users Admin"/>
</li>
*/
	
	public void init()
	 {
		CompanyDomain cdPe;
		User user=null;
		User useref=null;
		
		CompanyDomain cd=new CompanyDomain("all");
    	cd.setOwnerid(new OwnerId(user, cd)) ;
        cd.setPermissionprop(new PermissionProp());
		
		if((useref=usd.findByUserName("admin"))==null)
    	{
    		 user= new User("admin", "admin", "adminpwd", "f.redshift72@gmail.com", "+393495157233");
    		
    		//System.out.println("provo a salvare l'utente admin che non esiste ");
    		//System.out.flush();
    		
    		eman.persist(user);
    		
    		
    		
    	}else
    	{
    		user=useref;
    	}
		
		// qui comunque user c'è
		
		
		
		
    	if ((cdPe=cdd.findById("all"))== null)	
   	       {
    	  //	System.out.println("provo ad aggiungere  il nuovo dominio ai domini a cui partecipa l'utente admin");
    	  //	System.out.flush();
    		  
    		  eman.persist(cd);
    		  
              user.addCompanyDomain(cd)   ;		
              user.setOwnerid(new OwnerId(user, cd));
              user.setPermissionprop(new PermissionProp());
              
    		//  cd.addUser(user);
    		  usd.update(user);
    		  
    		  // aggiungo il dominio all e l'utente admin a tutti gli item del menu 
    		  /*
    		  for (MenuItem mi:itemdao.findAll())
      		   {
    			//mi.getAccessDomain().clear();
    			mi.addUserForAccess("admin");  
      			mi.addDomainForAccess("all");
      			eman.merge(mi);
      		   }
    		  */
   	       }else
   	       {
   	    	   if (!cdPe.getDomainUsers().contains(user))
   	    	   {
   	    		   
   	    		//   System.out.println("il dominio all eiste sul db, ma non contiene admin tra i suoi membri");
   	    		//   System.out.println("provo ad aggiungere all ai domini a cui partecipa admin");
       		    //   System.out.flush();
   	    		   
   	    		   user.addCompanyDomain(cdPe) ;
   	    		   usd.update(user);
   	    		   //cdPe.addUser(user);
   	    		   
   	    		   // aggiundo il dominio all tra 
   	    		 /*  
   	    		 for (MenuItem mi:itemdao.findAll())
        		   {
        			mi.addDomainForAccess(cdPe.getName());
        			eman.merge(mi);
        		   }
   	    		   */
   	    	   }
   	    	   
   	    	
       		

   	       
   	       }
    	
    	// aggiunge admin a tutti gli item del menu
    	for (MenuItem mi:itemdao.findAll())
   		{
   			//mi.getAccessUser().clear();
   			//mi.getAccessDomain().clear();
   			mi.addUserForAccess("admin");
   			mi.addDomainForAccess("all");

   			eman.merge(mi);
   		}
		
	 }
}
