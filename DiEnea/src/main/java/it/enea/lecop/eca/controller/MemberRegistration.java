package it.enea.lecop.eca.controller;

import it.enea.lecop.eca.model.Member;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.richfaces.cdi.push.Push;

// The @Stateful annotation eliminates the need for manual transaction demarcation
@Stateful
// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
@Model
public class MemberRegistration {

   public static final String PUSH_CDI_TOPIC = "pushCdi";

   @Inject
   private Logger log;

   @Inject
   private FacesContext facesContext;

   @Inject
   private EntityManager em;

   @Inject
   private Event<Member> memberEventSrc;

   @Inject
   @Push(topic = PUSH_CDI_TOPIC) Event<String> pushEvent;

   private Member newMember;
   private Member member;

   @Produces
   @Named
   public Member getNewMember() {
      return newMember;
   }

   @Produces
   @Named
   public Member getMember() {
      return member;
   }

   public void setMember(Member member) {
      this.member = member;
   }

   public void register() throws Exception {
      log.info("Registering " + newMember.getName());
      em.persist(newMember);
      facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful"));
      memberEventSrc.fire(newMember);
      pushEvent.fire(String.format("New member added: %s (id: %d)", newMember.getName(), newMember.getId()));
      initNewMember();
   }

   @PostConstruct
   public void initNewMember() {
      newMember = new Member();
   }
}
