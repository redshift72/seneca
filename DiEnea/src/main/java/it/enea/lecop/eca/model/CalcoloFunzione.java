package it.enea.lecop.eca.model;

import java.io.Serializable;
import java.util.Set;

public class CalcoloFunzione implements Serializable{

	private String calcolo;
	private long id;
	private GenericFunz funzVal;
	
	public String getCalcolo() {
		return calcolo;
	}

	public void setCalcolo(String calcolo) {
		this.calcolo = calcolo;
	}

	public Set<String> getParametri() {
		return parametri;
	}

	private void setParametri(Set<String> parametri) {
		this.parametri = parametri;
	}

	private Set<String> parametri;
	
}
