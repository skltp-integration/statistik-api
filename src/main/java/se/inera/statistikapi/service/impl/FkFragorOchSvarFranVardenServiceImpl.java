package se.inera.statistikapi.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.statistikapi.service.TakApiRestConsumerService;
import se.inera.statistikapi.service.helper.ElasticSearchHelper;
import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.web.rest.v1.dto.FkFragorOchSvarFranVarden;
import se.inera.statistikapi.web.rest.v1.dto.IntygErrors;
import se.inera.statistikapi.web.rest.v1.dto.IntygGrupperatPaSenderIds;

import static org.elasticsearch.index.query.QueryBuilders.*;

@Service("fkFragorOchSvarFranVardenService")
public class FkFragorOchSvarFranVardenServiceImpl {

    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_ANSWER_1_RIVTABP20 = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateAnswer:1:rivtabp20";
    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_QUESTION_1_RIVTABP20 = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateQuestion:1:rivtabp20";

    public static final String LOGISK_ADRESS_FK_OLD = "2021005521";


    @Autowired
    TakApiRestConsumerService takApiRestConsumerService;

    private final static Logger LOGGER = LoggerFactory.getLogger(FkFragorOchSvarFranVardenServiceImpl.class);


    public FkFragorOchSvarFranVarden getFkFragorOchSvar(String time, TransportClient client){
        FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden = new FkFragorOchSvarFranVarden();
        
        getFkFragorOchSvarFranVardenTillFK(fkFragorOchSvarFranVarden, time, client);
        getFkAntalErrors(fkFragorOchSvarFranVarden, time, client);

        return fkFragorOchSvarFranVarden;
    }

    private void getFkAntalErrors(FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch().setQuery(buildQueryAntalError(time)).setSize(0).addAggregation(ElasticSearchHelper.buildAggregationsGroupPerDay())
                .execute().actionGet();

        parseAntalErrorResponse(resp.getAggregations(), fkFragorOchSvarFranVarden);
    }

    private void parseAntalErrorResponse(Aggregations aggregations, FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden) {
        IntygErrors intygErrors = ElasticSearchHelper.getIntygErrorsFromResponse(aggregations, fkFragorOchSvarFranVarden.getFragorOchSvarFranVardenTillFK().getAntal());
        fkFragorOchSvarFranVarden.setFragorOchSvarFel(intygErrors);
    }

    private QueryBuilder buildQueryAntalError(String age) {
        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServiceErrors(age)
                .should(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_ANSWER_1_RIVTABP20))
                .should(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_QUESTION_1_RIVTABP20));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private void getFkFragorOchSvarFranVardenTillFK(FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch().setQuery(buildQueryFragorSvarVardenTillFk(time)).setSize(0)
                .addAggregation(ElasticSearchHelper.buildAggregationsGroupReceiverAndSenders()).execute().actionGet();

        parseFragorSvarVardenTillFk(resp.getAggregations(), fkFragorOchSvarFranVarden);
    }

    private QueryBuilder buildQueryFragorSvarVardenTillFk(String age) {

        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServicesInRequests(age)
                .should(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_ANSWER_1_RIVTABP20))
                .should(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_QUESTION_1_RIVTABP20));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private void parseFragorSvarVardenTillFk(Aggregations aggregations, FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden) {

        ServiceConsumer[] serviceConsumers = takApiRestConsumerService.getServiceConsumers();

        StringTerms group_receiverids = aggregations.get("group_receiverids");
        LOGGER.info("aggregations.get(\"group_receiverids\") size=" + group_receiverids.getBuckets().size());

        StringTerms.Bucket fragorSvarVardenTillFk = group_receiverids.getBucketByKey(LOGISK_ADRESS_FK_OLD);
        IntygGrupperatPaSenderIds intygGrupperatPaSenderIds = ElasticSearchHelper.createFkIntygGrupperatPaSenderId(fragorSvarVardenTillFk, serviceConsumers);
        fkFragorOchSvarFranVarden.setFragorOchSvarFranVardenTillFK(intygGrupperatPaSenderIds);
    }

}
