package it.enea.lecop.eca.model;

public class PareteEdificio {
    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/**
	 * misurato in m 
	 * @return
	 */
	public Double getSpessore() {
		return spessore;
	}
	public void setSpessore(Double spessore) {
		this.spessore = spessore;
	}
	public Double getTrasmittanza() {
		return trasmittanza;
	}
	/**
	 * W/m^2 * K
	 * @param trasmittanza
	 */
	public void setTrasmittanza(Double trasmittanza) {
		this.trasmittanza = trasmittanza;
	}
	public Double getMassaFrontale() {
		return massaFrontale;
	}
	
	/**
	 * misurata in Kg/m^2
	 * @param massaFrontale
	 */
	public void setMassaFrontale(Double massaFrontale) {
		this.massaFrontale = massaFrontale;
	}
	public Double getSmorzamento() {
		return smorzamento;
	}
	
	/**
	 * detto anche fattore di decremento
	 * @param smorzamento
	 */
	public void setSmorzamento(Double smorzamento) {
		this.smorzamento = smorzamento;
	}
	public Double getSfasamento() {
		return sfasamento;
	}
	public void setSfasamento(Double sfasamento) {
		this.sfasamento = sfasamento;
	}
	private Long id;
    private String nome;
    
    private Double spessore;
    private Double trasmittanza;
    private Double massaFrontale;
    private Double smorzamento;
    private Double sfasamento;
	
}
