package it.enea.lecop.eca.model;

import java.io.Serializable;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class IdProfiloDusoCompEdifici implements Serializable {

	
	
	 @ManyToOne
	   @JoinColumn(name="name")
	   ComposizioneEdifici composizione;
	
	   @ManyToOne
	   @JoinColumn(name="Name")
	   ProfiloUsoConsumo  profiloUso;
}
