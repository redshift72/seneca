package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Valutazione;
import it.enea.lecop.eca.model.Member;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@RequestScoped
public class AnalisiListProducer {
	@Inject
	   private EntityManager em;

	   private List<Valutazione> valutazioni;

	   // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
	   // Facelets or JSP view)
	   @Produces
	   @Named
	   public List<Valutazione> getValutiazioni() {
	      return valutazioni;
	   }

	   public void onMemberListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Valutazione valutazione) {
	      retrieveAllMembersOrderedByName();
	   }

	   @PostConstruct
	   public void retrieveAllMembersOrderedByName() {
	      CriteriaBuilder cb = em.getCriteriaBuilder();
	      CriteriaQuery<Valutazione> criteria = cb.createQuery(Valutazione.class);
	      Root<Valutazione> val = criteria.from(Valutazione.class);
	      // Swap criteria statements if you would like to try out type-safe criteria queries, a new
	      // feature in JPA 2.0
	      // criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
	      criteria.select(val).orderBy(cb.asc(val.get("id")));
	      valutazioni = em.createQuery(criteria).getResultList();
	   }
}
