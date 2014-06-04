package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Edificio;
import it.enea.lecop.eca.model.Valutazione;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class ListProducerEdificio {

	@Inject
	   private EntityManager em;

	   private List<Edificio> edifici;

	   // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
	   // Facelets or JSP view)
	   @Produces
	   @Named
	   public List<Edificio> getEdifici() {
	      return edifici;
	   }

	/*   public void onMemberListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Edificio edificio) {
	      retrieveAllEdificiOrderedByName();
	   }
*/
	   @PostConstruct
	   public void retrieveAllEdificiOrderedByName() {
	     
		   CriteriaBuilder  cb = em.getCriteriaBuilder();
	      CriteriaQuery<Edificio> criteria = cb.createQuery(Edificio.class);
	      Root<Edificio> val = criteria.from(Edificio.class);
	      // Swap criteria statements if you would like to try out type-safe criteria queries, a new
	      // feature in JPA 2.0
	      // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
	      criteria.select(val).orderBy(cb.asc(val.get("nome")));
	      edifici = em.createQuery(criteria).getResultList();
	   }
	
}
