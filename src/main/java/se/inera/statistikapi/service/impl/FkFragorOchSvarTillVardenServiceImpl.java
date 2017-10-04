package se.inera.statistikapi.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistikapi.service.TakApiRestConsumerService;
import se.inera.statistikapi.service.helper.ElasticSearchHelper;
import se.inera.statistikapi.takapi.ServiceProduction;
import se.inera.statistikapi.web.rest.v1.dto.FkFragorOchSvarTillVarden;
import se.inera.statistikapi.web.rest.v1.dto.IntygGrupperatPaReceiverIds;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service("fkFragorOchSvarTillVardenService")
public class FkFragorOchSvarTillVardenServiceImpl {

    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_ANSWER_RESPONDER_1   = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateAnswerResponder:1";
    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_QUESTION_RESPONDER_1 = "urn:riv:insuranceprocess:healthreporting:ReceiveMedicalCertificateQuestionResponder:1";

    @Autowired
    TakApiRestConsumerService takApiRestConsumerService;

    private final static Logger LOGGER = LoggerFactory.getLogger(FkFragorOchSvarTillVardenServiceImpl.class);

    public FkFragorOchSvarTillVarden getFkFragorOchSvarTillVarden(String time, TransportClient client){
        FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden = new FkFragorOchSvarTillVarden();

        getFkFragorOchSvarTillVarden(fkFragorOchSvarTillVarden, time, client);

        long antalKomplettering = getAntalFkFragorOchSvarTillVardenKomplettering(time, client);
        long antalPaminnelse = getAntalFkFragorOchSvarTillVardenPaminnelse(time, client);
        long antalErrors = getAntalFkFragorOchSvarTillVardenErrors(time, client);
        long antalErrorsVP004 = getAntalFkFragorOchSvarTillVardenErrorsVP004(time, client);
        long antalErrorsVP007 = getAntalFkFragorOchSvarTillVardenErrorsVP007(time, client);

        fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().setVaravKomplettering(antalKomplettering);
        fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().setVaravPaminnelse(antalPaminnelse);
        fkFragorOchSvarTillVarden.setAntalFel(antalErrors);
        fkFragorOchSvarTillVarden.setAntalRoutingFel(antalErrorsVP004);
        fkFragorOchSvarTillVarden.setAntalBehorighetsFel(antalErrorsVP007);

        return fkFragorOchSvarTillVarden;
    }

    private long getAntalFkFragorOchSvarTillVardenErrors(String age, TransportClient client) {
        BoolQueryBuilder qb = buildQueryVPServiceTillVardenErrors(age);
        SearchResponse resp = client.prepareSearch()
                .setQuery(constantScoreQuery(qb))
                .setSize(0)
                .execute().actionGet();
        return resp.getHits().getTotalHits();
    }

    private long getAntalFkFragorOchSvarTillVardenErrorsVP004(String age, TransportClient client) {
        BoolQueryBuilder qb = buildQueryVPServiceTillVardenErrors(age)
                .must(termQuery("errorCode.keyword", "VP004"));
        SearchResponse resp = client.prepareSearch()
                .setQuery(constantScoreQuery(qb))
                .setSize(0)
                .execute().actionGet();
        return resp.getHits().getTotalHits();
    }

    private long getAntalFkFragorOchSvarTillVardenErrorsVP007(String age, TransportClient client) {
        BoolQueryBuilder qb = buildQueryVPServiceTillVardenErrors(age)
                .must(termQuery("errorCode.keyword", "VP007"));

        SearchResponse resp = client.prepareSearch()
                .setQuery(constantScoreQuery(qb))
                .setSize(0)
                .execute().actionGet();
        return resp.getHits().getTotalHits();
    }

    private long getAntalFkFragorOchSvarTillVardenKomplettering(String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch()
                .setQuery(buildQueryFragorSvarFkTillVardenOnAdapterSubject(time, "Komplettering_av_lakarintyg"))
                .setSize(0)
                .execute().actionGet();
        return resp.getHits().getTotalHits();
    }

    private long getAntalFkFragorOchSvarTillVardenPaminnelse(String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch().setQuery(buildQueryFragorSvarFkTillVardenOnAdapterSubject(time, "Paminnelse")).setSize(0)
                .execute().actionGet();
        return resp.getHits().getTotalHits();
    }


    public static BoolQueryBuilder buildQueryVPServiceTillVardenErrors(String age){
        return ElasticSearchHelper.buildQueryVPServiceErrors(age)
                .should(termQuery("tjanstekontrakt.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_ANSWER_RESPONDER_1))
                .should(termQuery("tjanstekontrakt.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_QUESTION_RESPONDER_1));
    }

    private QueryBuilder buildQueryFragorSvarFkTillVardenOnAdapterSubject(String age, String adapterSubject) {
        BoolQueryBuilder qb =
                boolQuery()
                .must(termQuery("_type", "tp-track"))
                .must(termQuery("waypoint.keyword", "req-out"))
                .must(termQuery("componentId.keyword", "FkEintygAdapterIC"))
                .must(termQuery("fkAdapterSubject.keyword", adapterSubject))
                .must(rangeQuery("@timestamp").from("now-" + age).to("now"));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private void getFkFragorOchSvarTillVarden(FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch()
                .setQuery(buildQueryFragorSvarFkTillVarden(time))
                .setSize(0)
                .addAggregation(ElasticSearchHelper.buildAggregationsGroupSendersAndReceivers())
                .execute().actionGet();

        parseFragorSvarFkTillVarden(resp.getAggregations(), fkFragorOchSvarTillVarden);
    }


    private QueryBuilder buildQueryFragorSvarFkTillVarden(String age) {

        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServicesInRequests(age)
                .should(termQuery("tjanstekontrakt.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_ANSWER_RESPONDER_1))
                .should(termQuery("tjanstekontrakt.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_RECEIVE_MEDICAL_CERTIFICATE_QUESTION_RESPONDER_1));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private void parseFragorSvarFkTillVarden(Aggregations aggregations, FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden) {
        ServiceProduction[] serviceProductions = takApiRestConsumerService.getServiceProductions();

        StringTerms group_senderids = aggregations.get("group_senderids");
        LOGGER.info("aggregations.get(\"group_senderids\") size=" + group_senderids.getBuckets().size());

        IntygGrupperatPaReceiverIds intygGrupperatPaReceiverIds = new IntygGrupperatPaReceiverIds();
        intygGrupperatPaReceiverIds.setAntal(group_senderids.getBuckets().size());
        for(Terms.Bucket bucket : group_senderids.getBuckets()){
                intygGrupperatPaReceiverIds.setIntygPerReceiverIdList(ElasticSearchHelper.createFkAntalIntygPerReceiverIdList(bucket, serviceProductions));
        }

        fkFragorOchSvarTillVarden.setFragorOchSvarFranFKTillVarden(intygGrupperatPaReceiverIds);
    }

}
