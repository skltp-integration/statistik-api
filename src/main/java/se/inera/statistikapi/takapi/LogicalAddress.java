package se.inera.statistikapi.takapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogicalAddress {

	private int id;
	private String description;
	private String logicalAddress;
	
	public LogicalAddress() {
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
	public String getLogicalAddress() {
		return logicalAddress;
	}
	public void setLogicalAddress(String logicalAddress) {
		this.logicalAddress = logicalAddress;
	}
	
	
}
