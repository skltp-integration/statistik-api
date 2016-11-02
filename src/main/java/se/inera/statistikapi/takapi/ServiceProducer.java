package se.inera.statistikapi.takapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProducer {

	private int id;
	private String description;
	private String hsaId;
	
	public ServiceProducer() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHsaId() {
		return hsaId;
	}
	public void setHsaId(String hsaId) {
		this.hsaId = hsaId;
	}
	
}
