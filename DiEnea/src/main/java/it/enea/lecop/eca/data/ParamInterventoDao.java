package it.enea.lecop.eca.data;

import it.enea.lecop.eca.model.Azienda;
import it.enea.lecop.eca.model.ParamIntervento;
import it.enea.lecop.eca.model.User;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
public class ParamInterventoDao {
	
	@Inject
	  EntityManager man;
	@Inject
	  CompanyDomainDao cdd;
	
	
	public List<ParamIntervento> findAll()
	{
		return SecureGenericSelect.getAllNoSec(ParamIntervento.class, man);
	}


	public ParamIntervento findByName(String nome) {
		ParamIntervento param;
		try{
		param=man.find(ParamIntervento.class,nome);
		}catch (RuntimeException e) {
			e.printStackTrace();
			return null;
			// TODO: handle exception
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return param;
		
	}
    

}
