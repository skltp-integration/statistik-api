package se.inera.statistikapi.service.helper;

import static java.lang.Integer.MAX_VALUE;
import static org.elasticsearch.index.query.QueryBuilders.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.core.env.Environment;

import se.inera.statistikapi.takapi.ServiceConsumer;
import se.inera.statistikapi.web.rest.v1.dto.FkAntalIntyg;
import se.inera.statistikapi.web.rest.v1.dto.IntygErrors;
import se.inera.statistikapi.web.rest.v1.dto.IntygPerRecieverId;
import se.inera.statistikapi.web.rest.v1.dto.IntygPerSenderId;

public class ElasticSearchHelper {

    private static final String CLUSTER_NAME = "cluster.name";
    private static final String ELASTICSEARCH_PORT = "elasticsearch-port";
    private static final String ELASTICSEARCH_SERVER = "elasticsearch-server";

    public static TransportClient getTransportClient(Environment env) {
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

    public static BoolQueryBuilder buildQueryVPServicesInRequests(String age) {
        return boolQuery()
                .must(termQuery("_type", "tp-track"))
                .must(termQuery("waypoint.keyword", "req-in"))
                .must(termQuery("componentId.keyword", "vp-services"))
                .must(rangeQuery("@timestamp").from("now-" + age).to("now"));
    }

    public static BoolQueryBuilder buildQueryVPServiceErrors(String age) {
        return boolQuery()
                .must(termQuery("_type", "tp-track"))
                .must(termQuery("log-level.keyword", "ERROR"))
                .must(termQuery("componentId.keyword", "vp-services"))
                .must(rangeQuery("@timestamp").from("now-" + age).to("now"));
    }
    public static AggregationBuilder buildAggregationsGroupRecieverAndSenders() {
        return AggregationBuilders
                .terms("group_receiverids")
                .field("receiverid.keyword")
                .size(MAX_VALUE)
                .subAggregation(AggregationBuilders.terms("group_senderids")
                        .field("senderid.keyword"));
    }

    public static AggregationBuilder buildAggregationsGroupPerDay() {
        return AggregationBuilders
                .dateHistogram("group_per_day")
                .field("@timestamp")
                .dateHistogramInterval(DateHistogramInterval.DAY);
    }


    public static IntygPerRecieverId createFkIntygPerRecieverId(Bucket bucket, ServiceConsumer[] serviceConsumers) {
        IntygPerRecieverId intygPerRecieverId = new IntygPerRecieverId();
        if (bucket != null) {
            intygPerRecieverId.setIntygPerSenderIdList(createFkAntalIntygPerSenderIdList(bucket, serviceConsumers));
            intygPerRecieverId.setAntal(bucket.getDocCount());
        }
        return intygPerRecieverId;
    }

    public static List<IntygPerSenderId> createFkAntalIntygPerSenderIdList(Bucket bucket, ServiceConsumer[] serviceConsumers) {
        List<IntygPerSenderId> intygPerSenderIdList = new ArrayList();
        StringTerms group_senderids = bucket.getAggregations().get("group_senderids");
        for (Bucket senderIdBucket : group_senderids.getBuckets()) {

            String description = mapTakConsumerIdToDescription(senderIdBucket.getKeyAsString(), serviceConsumers);
            intygPerSenderIdList.add(new IntygPerSenderId(senderIdBucket.getKeyAsString(), senderIdBucket.getDocCount(), description));
        }
        return intygPerSenderIdList;
    }

    public static IntygErrors getIntygErrorsFromResponse(Aggregations aggregations, Long totalAntalAnrop) {
        IntygErrors errors = new IntygErrors();
        InternalDateHistogram group_per_day = aggregations.get("group_per_day");

        Long antalSundayErrors = 0L;
        Long antalErrors = 0L;
        for (Histogram.Bucket onedayErrors : group_per_day.getBuckets()) {
            antalErrors += onedayErrors.getDocCount();
            if (isSunday(onedayErrors.getKeyAsString())) {
                antalSundayErrors += onedayErrors.getDocCount();
            }
        }

        errors.setAntalFelTotal(antalErrors);
        errors.setAntalFelOnSundays(antalSundayErrors);
        errors.setAntalFelNotSundays(antalErrors-antalSundayErrors);
        errors.setAntalFelAvTotal(getPercentWithScaleTwo(antalErrors, totalAntalAnrop).toString() + "%");

        return errors;
    }

    private static String mapTakConsumerIdToDescription(String hsaId, ServiceConsumer[] serviceConsumers) {
        return Arrays.stream(serviceConsumers).filter(serviceConsumer -> serviceConsumer.getHsaId().equals(hsaId)).findFirst()
                .map(ServiceConsumer::getDescription).orElse(null);
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
