package se.inera.statistikapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import se.inera.statistikapi.service.TakApiRestComsumerService;
import se.inera.statistikapi.takapi.ConnectionPoint;
import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.takapi.ServiceProduction;

@Service("takApiRestComsumerService")
public class TakApiRestComsumerServiceImpl implements TakApiRestComsumerService{

	private static final String ENVIRONMENT = "environment";
	private static final String PLATFORM = "platform";
	private static final String TAK_API_SERVER = "tak-api-server";

	@Autowired
	Environment env;
	
	private static final int RECHECK_INTERVAL_MILLIS = 3600000;
	
	private long lastTakUpdateCheck;
	private ServiceConsumer[] serviceConsumers;
	private ServiceProduction[] serviceProductions;
	private ConnectionPoint connectionPoint;
	private RestTemplate restTemplate;
	
	public TakApiRestComsumerServiceImpl() {
		restTemplate = new RestTemplate();
		lastTakUpdateCheck = 0;
	}
	
	@Override
	public ServiceConsumer[] getServiceConsumers() {
		checkAndUpdateDataFromTak();
		return serviceConsumers;
	}

	@Override
	public ServiceProduction[] getServiceProductions() {
		checkAndUpdateDataFromTak();
		return serviceProductions;
	}
	
	private void checkAndUpdateDataFromTak() {
		if(timeToUpdate()) {
			ConnectionPoint connectionPoint = getConnectionPoint();
			lastTakUpdateCheck = System.currentTimeMillis();
			System.out.println("Updated ConnectionPoint information from TAK-API");
			if(this.connectionPoint == null || !connectionPoint.getSnapshotTime().equals(this.connectionPoint.getSnapshotTime())) {
				this.connectionPoint = connectionPoint;
				updateServiceConsumers();
				updateServiceProductions();
				System.out.println("Updated data from TAK-API");
			}
		}
	}
	
	private boolean timeToUpdate() {
		return lastTakUpdateCheck < (System.currentTimeMillis() - RECHECK_INTERVAL_MILLIS);
	}
	
	private void updateServiceConsumers() {
		serviceConsumers = restTemplate.getForObject(env.getProperty(TAK_API_SERVER) + "/coop/api/v1/serviceConsumers.json?connectionPointId=" + connectionPoint.getId(), ServiceConsumer[].class);
	}

	private void updateServiceProductions() {
		serviceProductions = restTemplate.getForObject(env.getProperty(TAK_API_SERVER) + "/coop/api/v1/serviceProductions.json?connectionPointId=" + connectionPoint.getId() + "&include=logicalAddress,serviceContract,serviceProducer", ServiceProduction[].class);
	}
	
	private ConnectionPoint getConnectionPoint() {
		ConnectionPoint[] connectionPoints = restTemplate.getForObject(env.getProperty(TAK_API_SERVER) + "/coop/api/v1/connectionPoints.json?platform=" + env.getProperty(PLATFORM) + "&environment=" + env.getProperty(ENVIRONMENT), ConnectionPoint[].class);
		return connectionPoints[0];
	}
}
