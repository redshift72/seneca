package it.enea.lecop.eca.view;

import it.enea.lecop.eca.model.ParamIntervento;

import java.io.Serializable;
import java.util.Map;

public class ParamInterventoON implements Serializable{

	public String getParName() {
		return parName;
	}

	public void setParName(String parName) {
		this.parName = parName;
	}

	public Map<String, ParamIntervento> getParametri() {
		return parametri;
	}

	public void setParametri(Map<String, ParamIntervento> parametri) {
		this.parametri = parametri;
	}

	public ParamIntervento getPar() {
		return getParametri().get(getParName());
	}

	public void setPar(ParamIntervento par) {
		this.par = par;
		getParametri().put(getParName(), par);
	}

	public ParamInterventoON()
	{
		
	}
	
	public ParamInterventoON(String parName,
			Map<String, ParamIntervento> parametri) {
		
		this.parName = parName;
		this.parametri = parametri;
	}
	private String parName;
	private Map<String,ParamIntervento> parametri;
	private ParamIntervento par;
	
	
}
