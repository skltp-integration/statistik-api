package se.inera.statistikapi.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import se.inera.statistikapi.criteria.AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria;
import se.inera.statistikapi.service.AnropPerKonsumentTjanstekontrakLogiskAdressatService;
import se.inera.statistikapi.service.TakApiRestComsumerService;
import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.takapi.ServiceProduction;
import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;

import static java.lang.Integer.MAX_VALUE;

@Service("anropPerKonsumentTjanstekontrakLogiskAdressatService")
public class AnropPerKonsumentTjanstekontrakLogiskAdressatServiceImpl implements AnropPerKonsumentTjanstekontrakLogiskAdressatService {

    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTICSEARCH_PORT = "elasticsearch-port";
    private static final String ELASTICSEARCH_SERVER = "elasticsearch-server";
    private static final String DEFAULT_TIME = "1w";

    @Autowired
    Environment env;

    @Autowired
    TakApiRestComsumerService takApiRestComsumerService;

    @Override
    public List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> findAll(AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {

        TransportClient client = getTransportClient();

        checkTime(criteria);

        SearchResponse resp = client.prepareSearch().setQuery(buildTerms(criteria)).setSize(0).addAggregation(buildAggregations()).execute()
                .actionGet();

        List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list = parseResponse(resp);

        client.close();

        addTakData(list);

        return list;
    }

    private TransportClient getTransportClient() {
        String clusterName = env.getProperty(CLUSTER_NAME);
        String serverName = env.getProperty(ELASTICSEARCH_SERVER);
        int serverPort = Integer.parseInt(env.getProperty(ELASTICSEARCH_PORT));

        Settings settings = Settings.builder().put(CLUSTER_NAME, clusterName).build();

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverName),
                    serverPort));
        } catch (UnknownHostException e) {
            // TODO what should we do here?
            e.printStackTrace();
        }
        return client;
    }

    private void addTakData(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list) {
        ServiceConsumer[] serviceConsumers = takApiRestComsumerService.getServiceConsumers();
        ServiceProduction[] serviceProductions = takApiRestComsumerService.getServiceProductions();

        for (AnropPerKonsumentTjanstekontrakLogiskAdressatDTO dto : list) {
            dto.setKonsumentBeskrivning(getServiceConsumerDescription(dto.getKonsumentHsaId(), serviceConsumers));

            ServiceProduction serviceProduction = getServiceProductionForDTO(dto, serviceProductions);
            if (serviceProduction != null) {
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

    private List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> parseResponse(SearchResponse resp) {
        List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list = new ArrayList<>();

        Aggregations aggregations = resp.getAggregations();
        Iterator<Aggregation> listAggregations = aggregations.iterator();
        while (listAggregations.hasNext()) {
            Aggregation agg = listAggregations.next();
            parseSenderIdsBuckets(list, ((UnmappedTerms) agg).getBuckets());
        }
        return list;
    }

    private void parseSenderIdsBuckets(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list, List<Bucket> senderIdsBuckets) {
        Iterator<Bucket> groupSenderIdsBuckets = senderIdsBuckets.iterator();
        while (groupSenderIdsBuckets.hasNext()) {
            Bucket senderIdsBucket = groupSenderIdsBuckets.next();
            String senderId = (String) senderIdsBucket.getKey();
            parseSenderIdsAggregations(list, senderId, senderIdsBucket.getAggregations());
        }
    }

    private void parseSenderIdsAggregations(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list, String senderId,
            Aggregations senderIdsAggregations) {

        Iterator<Aggregation> listSenderIdsAggregations = senderIdsAggregations.iterator();
        while (listSenderIdsAggregations.hasNext()) {
            Aggregation senderIdsAggregation = listSenderIdsAggregations.next();
            parseTjansteKontraktBuckets(list, senderId, ((UnmappedTerms) senderIdsAggregation).getBuckets());
        }
    }

    private void parseTjansteKontraktBuckets(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list, String senderId,
            List<Bucket> tjansteKontraktBuckets) {
        Iterator<Bucket> groupTjanstekontraktBuckets = tjansteKontraktBuckets.iterator();

        while (groupTjanstekontraktBuckets.hasNext()) {
            Bucket tjanstekontraktBucket = groupTjanstekontraktBuckets.next();
            parseTjanstekontraktAggregations(list, senderId, tjanstekontraktBucket);
        }
    }

    private void parseTjanstekontraktAggregations(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> list, String senderId,
            Bucket tjanstekontraktBucket) {
        Aggregations tjanstekontraktAggregations = tjanstekontraktBucket.getAggregations();
        String tjanstekontrakt = (String) tjanstekontraktBucket.getKey();
        Iterator<Aggregation> listTjanstekontraktAggregations = tjanstekontraktAggregations.iterator();

        while (listTjanstekontraktAggregations.hasNext()) {
            Aggregation tjanstekontraktAggregation = listTjanstekontraktAggregations.next();
            Iterator<Bucket> groupReceiverIdsBuckets = ((UnmappedTerms) tjanstekontraktAggregation).getBuckets().iterator();

            while (groupReceiverIdsBuckets.hasNext()) {
                Bucket receiverIdsBucket = groupReceiverIdsBuckets.next();
                String receiverId = (String) receiverIdsBucket.getKey();
                long antal = receiverIdsBucket.getDocCount();
                Aggregations receiverIdsAggregations = receiverIdsBucket.getAggregations();
                Iterator<Aggregation> listReceiverIdsAggregations = receiverIdsAggregations.iterator();

                while (listReceiverIdsAggregations.hasNext()) {
                    Aggregation receiverIdsAggregation = listReceiverIdsAggregations.next();
                    double avgTime = ((InternalAvg) receiverIdsAggregation).getValue();

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

    private void checkTime(AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {
        if (criteria.getTime() == null || criteria.getTime().isEmpty()) {
            criteria.setTime(DEFAULT_TIME);
        }
    }

    private BoolQueryBuilder buildTerms(AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria) {
        BoolQueryBuilder terms = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_type", "tp-track"))
                .must(QueryBuilders.termQuery("waypoint.raw", "resp-out")).must(QueryBuilders.termQuery("componentId.raw", "vp-services"))
                .must(QueryBuilders.rangeQuery("@timestamp").from("now-" + criteria.getTime().toLowerCase()).to("now"));

        if (criteria.getTjanstekontrakt() != null && !criteria.getTjanstekontrakt().isEmpty()) {
            terms = terms.must(QueryBuilders.termQuery("tjanstekontrakt.raw", criteria.getTjanstekontrakt()));
        }
        return terms;
    }

    private AggregationBuilder buildAggregations() {
        return AggregationBuilders
                .terms("group_senderids")
                .field("senderid.raw")
                .size(MAX_VALUE)
                .subAggregation(
                        AggregationBuilders
                                .terms("group_tjanstekontrakt")
                                .field("tjanstekontrakt.raw")
                                .size(MAX_VALUE)
                                .subAggregation(
                                        AggregationBuilders.terms("group_receiverids").field("receiverid.raw").size(MAX_VALUE)
                                                .subAggregation(AggregationBuilders.avg("group_avg_timeproducer").field("time_producer"))));
    }

    private ServiceProduction getServiceProductionForDTO(AnropPerKonsumentTjanstekontrakLogiskAdressatDTO dto, ServiceProduction[] serviceProductions) {
        for (ServiceProduction serviceProduction : serviceProductions) {
            if (dto.getLogiskAdressatHsaId().equals(serviceProduction.getLogicalAddress().getLogicalAddress())
                    && dto.getTjanstekontrakt().equals(serviceProduction.getServiceContract().getNamespace())) {
                return serviceProduction;
            }
        }
        return null;
    }

    private String getServiceConsumerDescription(String konsumentHsaId, ServiceConsumer[] serviceConsumers) {
        for (ServiceConsumer serviceConsumer : serviceConsumers) {
            if (konsumentHsaId.equals(serviceConsumer.getHsaId())) {
                return serviceConsumer.getDescription();
            }
        }
        return null;
    }

}
