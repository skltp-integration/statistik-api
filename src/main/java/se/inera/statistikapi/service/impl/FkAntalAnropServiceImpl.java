package se.inera.statistikapi.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import se.inera.statistikapi.service.TakApiRestConsumerService;
import se.inera.statistikapi.service.helper.ElasticSearchHelper;
import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.web.rest.v1.dto.FkAntalIntyg;
import se.inera.statistikapi.web.rest.v1.dto.IntygErrors;
import se.inera.statistikapi.web.rest.v1.dto.IntygGrupperatPaSenderIds;

@Service("fkAntalAnropService")
public class FkAntalAnropServiceImpl {

    private final static Logger LOGGER = LoggerFactory.getLogger(FkAntalAnropServiceImpl.class);

    public static final String LOGISK_ADRESS_INTYGSTJANSTEN = "5565594230";
    public static final String LOGISK_ADRESS_FK_NEW = "2021005521-nowiretap";
    public static final String LOGISK_ADRESS_FK_OLD = "2021005521";
    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_REGISTER_MEDICAL_CERTIFICATE_3_RIVTABP20 = "urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificate:3:rivtabp20";
    public static final String URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_RESPONDER_1 = "urn:riv:insuranceprocess:healthreporting:SendMedicalCertificateResponder:1";

    @Autowired
    TakApiRestConsumerService takApiRestConsumerService;

    @Autowired
    Environment env;

    public FkAntalIntyg getFkAntalAnrop(String time, TransportClient client) {

        FkAntalIntyg fkAntalIntyg = new FkAntalIntyg();
        getFkAntalAnropTillFK(fkAntalIntyg, time, client);
        getFkAntalAnropTillIntygTjansten(fkAntalIntyg, time, client);
        getFkAntalIntygErrors(fkAntalIntyg, time, client);

        return fkAntalIntyg;
    }

    private void getFkAntalAnropTillIntygTjansten(FkAntalIntyg fkAntalIntyg, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch()
                .setQuery(buildQueryTillIntygTjansten(time))
                .setSize(0)
                .addAggregation(ElasticSearchHelper.buildAggregationsGroupReceiverAndSenders())
                .execute().actionGet();

        parseTillIntygTjanstenResponse(resp.getAggregations(), fkAntalIntyg);
    }

    private void getFkAntalAnropTillFK(FkAntalIntyg fkAntalIntyg, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch()
                .setQuery(buildQuery(time))
                .setSize(0)
                .addAggregation(ElasticSearchHelper.buildAggregationsGroupReceiverAndSenders())
                .execute().actionGet();

        parseResponeAggregations(resp.getAggregations(), fkAntalIntyg);
    }

    private void getFkAntalIntygErrors(FkAntalIntyg fkAntalIntyg, String time, TransportClient client) {
        SearchResponse resp = client.prepareSearch()
                .setQuery(buildQueryAntalError(time))
                .setSize(0)
                .addAggregation(ElasticSearchHelper.buildAggregationsGroupPerDay())
                .execute().actionGet();

        parseAntalErrorPerDayResponse(resp.getAggregations(), fkAntalIntyg);
    }


    private QueryBuilder buildQuery(String age) {
        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServicesInRequests(age)
                .must(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_REGISTER_MEDICAL_CERTIFICATE_3_RIVTABP20));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private QueryBuilder buildQueryTillIntygTjansten(String age) {
        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServicesInRequests(age)
                .must(termQuery("tjanstekontrakt.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_SEND_MEDICAL_CERTIFICATE_RESPONDER_1));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }

    private QueryBuilder buildQueryAntalError(String age) {
        BoolQueryBuilder qb = ElasticSearchHelper.buildQueryVPServiceErrors(age)
                  .must(termQuery("tjansteinteraktion.keyword", URN_RIV_INSURANCEPROCESS_HEALTHREPORTING_REGISTER_MEDICAL_CERTIFICATE_3_RIVTABP20));

        ConstantScoreQueryBuilder constantScoreQueryBuilder = constantScoreQuery(qb);
        return constantScoreQueryBuilder;
    }


    private void parseResponeAggregations(Aggregations aggregations, FkAntalIntyg fkAntalIntyg) {

        ServiceConsumer[] serviceConsumers = takApiRestConsumerService.getServiceConsumers();

        StringTerms group_receiverids = aggregations.get("group_receiverids");
        LOGGER.info("aggregations.get(\"group_receiverids\") size=" + group_receiverids.getBuckets().size());

        StringTerms.Bucket registerMedicalCertificateToFKDirekt = group_receiverids.getBucketByKey(LOGISK_ADRESS_FK_OLD);
        IntygGrupperatPaSenderIds intygGrupperatPaSenderIds = ElasticSearchHelper.createFkIntygGrupperatPaSenderId(registerMedicalCertificateToFKDirekt, serviceConsumers);
        fkAntalIntyg.setRegisterMedicalCertificateToFKDirekt(intygGrupperatPaSenderIds);

        StringTerms.Bucket registerMedicalCertificateToFKViaIntygtj = group_receiverids.getBucketByKey(LOGISK_ADRESS_FK_NEW);
        intygGrupperatPaSenderIds = ElasticSearchHelper.createFkIntygGrupperatPaSenderId(registerMedicalCertificateToFKViaIntygtj, serviceConsumers);
        fkAntalIntyg.setRegisterMedicalCertificateToFKViaIntygstjansten(intygGrupperatPaSenderIds);

    }

    private void parseTillIntygTjanstenResponse(Aggregations aggregations, FkAntalIntyg fkAntalIntyg) {

        ServiceConsumer[] serviceConsumers = takApiRestConsumerService.getServiceConsumers();

        StringTerms group_receiverids = aggregations.get("group_receiverids");
        LOGGER.info("aggregations.get(\"group_receiverids\") size=" + group_receiverids.getBuckets().size());

        StringTerms.Bucket SendMedicalCertificateToIntygstjansten = group_receiverids.getBucketByKey(LOGISK_ADRESS_INTYGSTJANSTEN);
        IntygGrupperatPaSenderIds intygGrupperatPaSenderIds = ElasticSearchHelper.createFkIntygGrupperatPaSenderId(SendMedicalCertificateToIntygstjansten, serviceConsumers);
        fkAntalIntyg.setSendMedicalCertificateToIntygstjansten(intygGrupperatPaSenderIds);
    }

    private void parseAntalErrorPerDayResponse(Aggregations aggregations, FkAntalIntyg fkAntalIntyg) {
        IntygErrors intygErrors = ElasticSearchHelper.getIntygErrorsFromResponse(aggregations,fkAntalIntyg.getAntalRegisterMedicalCertificateToFKTot() );
        fkAntalIntyg.setFelRegisterMedicalCertificate(intygErrors);
    }

    private static BigDecimal getPercentWithScaleTwo(Long n, Long v) {
        Float percent = 0f;
        if (v > 0) {
            percent = (n * 100f) / v;
        }
        return new BigDecimal(percent).setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean isSunday(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr.substring(0, 10), formatter);
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SUNDAY;
    }



}
