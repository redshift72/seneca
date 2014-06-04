package it.enea.lecop.eca.model;

import java.io.Serializable;

public class SelectDescr implements Serializable{

	public SelectDescr(Long id, String name, String descrizione) {
		
		this.id = id;
		this.name = name;
		this.descrizione = descrizione;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	private Long id;
	private String name;
	private String descrizione;
	
}
