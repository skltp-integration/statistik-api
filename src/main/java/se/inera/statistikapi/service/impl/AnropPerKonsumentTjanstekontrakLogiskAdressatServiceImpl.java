package se.inera.statistikapi.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import se.inera.statistikapi.criteria.AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria;
import se.inera.statistikapi.service.AnropPerKonsumentTjanstekontrakLogiskAdressatService;
import se.inera.statistikapi.service.TakApiRestComsumerService;
import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.takapi.ServiceProduction;
import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;

@Service("anropPerKonsumentTjanstekontrakLogiskAdressatService")
public class AnropPerKonsumentTjanstekontrakLogiskAdressatServiceImpl implements
		AnropPerKonsumentTjanstekontrakLogiskAdressatService {

	private static final String CLUSTER_NAME = "cluster.name";
	private static final String ELASTICSEARCH_PORT = "elasticsearch-port";
	private static final String ELASTICSEARCH_SERVER = "elasticsearch-server";
	private static final String DEFAULT_TIME = "1w";
	
	@Autowired
	Environment env;
	
	@Autowired
	TakApiRestComsumerService takApiRestComsumerService;
	
	@Override
	public List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> findAll(
			AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {
		
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put(CLUSTER_NAME, env.getProperty(CLUSTER_NAME)).build();
		Client client = new TransportClient(settings).addTransportAddress(
				new InetSocketTransportAddress(env.getProperty(ELASTICSEARCH_SERVER), 
						Integer.parseInt(env.getProperty(ELASTICSEARCH_PORT))));
		
		checkTime(criteria);
		
		SearchResponse resp = client.prepareSearch()
				.setQuery(buildTerms(criteria))
				.setSize(0)
				.addAggregation(buildAggregations())
				.execute().actionGet();
		
		List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list = parseResponse(resp);
		
		client.close();
		
		addTakData(list);

		return list;
	}

	private void addTakData(
			List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list) {
		ServiceConsumer[] serviceConsumers = takApiRestComsumerService.getServiceConsumers();
		ServiceProduction[] serviceProductions = takApiRestComsumerService.getServiceProductions();
		
		for (AnropPerKonsumentTjanstekontrakLogiskAdressatDTO dto : list) {
			dto.setKonsumentBeskrivning(getServiceConsumerDescription(dto.getKonsumentHsaId(), serviceConsumers));
			
			ServiceProduction serviceProduction = getServiceProductionForDTO(dto, serviceProductions);
			if(serviceProduction != null) {
				dto.setLogiskAdressatBeskrivning(serviceProduction.getLogicalAddress().getDescription());
				dto.setProducentHsaId(serviceProduction.getServiceProducer().getHsaId());
				dto.setProducentBeskrivning(serviceProduction.getServiceProducer().getDescription());
			} else {
				System.out.println("Ingen träff för");
				System.out.println(dto.getLogiskAdressatHsaId());
				System.out.println(dto.getTjanstekontrakt());
			}
		}
	}

	private List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> parseResponse(
			SearchResponse resp) {
		List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list = new ArrayList<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO>();
		
		Aggregations aggregations = resp.getAggregations();
		Iterator<Aggregation> listAggregations = aggregations.iterator();
		while(listAggregations.hasNext()) {
			Aggregation agg = listAggregations.next();
			String nameSenderId = agg.getName();
			Iterator<Bucket> groupSenderIdsBuckets = ((StringTerms)agg).getBuckets().iterator();
			
			while(groupSenderIdsBuckets.hasNext()) {
				Bucket senderIdsBucket = groupSenderIdsBuckets.next();
				String senderId = senderIdsBucket.getKey();
				Aggregations senderIdsAggregations = senderIdsBucket.getAggregations();
				Iterator<Aggregation> listSenderIdsAggregations = senderIdsAggregations.iterator();
				
				while(listSenderIdsAggregations.hasNext()) {
					Aggregation senderIdsAggregation = listSenderIdsAggregations.next();
					String nameTjanstekontrakt = senderIdsAggregation.getName();
					Iterator<Bucket> groupTjanstekontraktBuckets = ((StringTerms)senderIdsAggregation).getBuckets().iterator();
					
					while(groupTjanstekontraktBuckets.hasNext()) {
						Bucket tjanstekontraktBucket = groupTjanstekontraktBuckets.next();
						String tjanstekontrakt = tjanstekontraktBucket.getKey();
						Aggregations tjanstekontraktAggregations = tjanstekontraktBucket.getAggregations();
						Iterator<Aggregation> listTjanstekontraktAggregations = tjanstekontraktAggregations.iterator();
						
						while(listTjanstekontraktAggregations.hasNext()) {
							Aggregation tjanstekontraktAggregation = listTjanstekontraktAggregations.next();
							String nameReceiverId = tjanstekontraktAggregation.getName();
							Iterator<Bucket> groupReceiverIdsBuckets = ((StringTerms)tjanstekontraktAggregation).getBuckets().iterator();
							
							while(groupReceiverIdsBuckets.hasNext()) {
								Bucket receiverIdsBucket = groupReceiverIdsBuckets.next();
								String receiverId = receiverIdsBucket.getKey();
								long antal = receiverIdsBucket.getDocCount();
								Aggregations receiverIdsAggregations = receiverIdsBucket.getAggregations();
								Iterator<Aggregation> listReceiverIdsAggregations = receiverIdsAggregations.iterator();
								
								while(listReceiverIdsAggregations.hasNext()) {
									Aggregation receiverIdsAggregation = listReceiverIdsAggregations.next();
									double avgTime = ((InternalAvg)receiverIdsAggregation).getValue();
									
									AnropPerKonsumentTjanstekontrakLogiskAdressatDTO e = new AnropPerKonsumentTjanstekontrakLogiskAdressatDTO();
									e.setKonsumentHsaId(senderId);
									e.setAntalAnrop(antal);
									e.setTjanstekontrakt(tjanstekontrakt);
									e.setLogiskAdressatHsaId(receiverId);
									e.setSnittsvarstid(avgTime);
									list.add(e);
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	private void checkTime(
			AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {
		if(criteria.getTime() == null || criteria.getTime().isEmpty()) {
			criteria.setTime(DEFAULT_TIME);
		}
	}

	private BoolQueryBuilder buildTerms(
			AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {
		BoolQueryBuilder terms = QueryBuilders.boolQuery()
			.must(QueryBuilders.termQuery("_type", "tp-track"))
			.must(QueryBuilders.termQuery("waypoint.raw", "resp-out"))
			.must(QueryBuilders.termQuery("componentId.raw", "vp-services"))
			.must(QueryBuilders.rangeQuery("@timestamp").from("now-"+criteria.getTime().toLowerCase()).to("now"));
		
		if(criteria.getTjanstekontrakt() != null && !criteria.getTjanstekontrakt().isEmpty()) {
			terms = terms.must(QueryBuilders.termQuery("tjanstekontrakt.raw", criteria.getTjanstekontrakt()));
		}
		return terms;
	}
	
	private TermsBuilder buildAggregations() {
		return AggregationBuilders.terms("group_senderids").field("senderid.raw").size(0)
				.subAggregation(AggregationBuilders.terms("group_tjanstekontrakt").field("tjanstekontrakt.raw").size(0)
						.subAggregation(AggregationBuilders.terms("group_receiverids").field("receiverid.raw").size(0)
								.subAggregation(AggregationBuilders.avg("group_avg_timeproducer").field("time_producer"))
								)
						);
	}

	private ServiceProduction getServiceProductionForDTO(
			AnropPerKonsumentTjanstekontrakLogiskAdressatDTO dto,
			ServiceProduction[] serviceProductions) {
		for (ServiceProduction serviceProduction : serviceProductions) {
			if(dto.getLogiskAdressatHsaId().equals(serviceProduction.getLogicalAddress().getLogicalAddress())
					&& dto.getTjanstekontrakt().equals(serviceProduction.getServiceContract().getNamespace())) {
				return serviceProduction;
			}
		}
		return null;
	}

	private String getServiceConsumerDescription(String konsumentHsaId,
			ServiceConsumer[] serviceConsumers) {
		for (ServiceConsumer serviceConsumer : serviceConsumers) {
			if(konsumentHsaId.equals(serviceConsumer.getHsaId())) {
				return serviceConsumer.getDescription();
			}
		}
		return null;
	}

}
