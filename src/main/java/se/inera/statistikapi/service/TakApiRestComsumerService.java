package se.inera.statistikapi.service;

import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.takapi.ServiceProduction;

public interface TakApiRestComsumerService {

	public ServiceConsumer[] getServiceConsumers();
	
	public ServiceProduction[] getServiceProductions();
	
}
