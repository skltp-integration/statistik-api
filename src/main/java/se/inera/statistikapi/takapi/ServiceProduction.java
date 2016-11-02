package se.inera.statistikapi.takapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProduction {

	private int id;
	private String physicalAddress;
	private String rivtaProfile;
	private ServiceProducer serviceProducer;
	private LogicalAddress logicalAddress;
	private ServiceContract serviceContract;
	
	public ServiceProduction() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPhysicalAddress() {
		return physicalAddress;
	}
	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}
	public String getRivtaProfile() {
		return rivtaProfile;
	}
	public void setRivtaProfile(String rivtaProfile) {
		this.rivtaProfile = rivtaProfile;
	}
	public ServiceProducer getServiceProducer() {
		return serviceProducer;
	}
	public void setServiceProducer(ServiceProducer serviceProducer) {
		this.serviceProducer = serviceProducer;
	}
	public LogicalAddress getLogicalAddress() {
		return logicalAddress;
	}
	public void setLogicalAddress(LogicalAddress logicalAddress) {
		this.logicalAddress = logicalAddress;
	}
	public ServiceContract getServiceContract() {
		return serviceContract;
	}
	public void setServiceContract(ServiceContract serviceContract) {
		this.serviceContract = serviceContract;
	}
}
