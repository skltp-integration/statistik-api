package se.inera.statistikapi.service.impl;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import se.inera.statistikapi.service.helper.ElasticSearchHelper;
import se.inera.statistikapi.web.rest.v1.dto.FkStatistik;

@Service("fkStatistikService")
public class FkStatistikServiceImpl {

    @Autowired
    Environment env;

    @Autowired
    FkAntalAnropServiceImpl fkAntalAnropService;

    @Autowired
    FkFragorOchSvarFranVardenServiceImpl fkFragorOchSvarService;

    private static final String DEFAULT_TIME = "1w";

    public FkStatistik getFkStatistik(String time) {

        TransportClient client = ElasticSearchHelper.getTransportClient(env);

        time = time == null ? DEFAULT_TIME : time;

        FkStatistik fkStatistik = new FkStatistik();
        fkStatistik.setFkAntalIntyg(fkAntalAnropService.getFkAntalAnrop(time, client));
        fkStatistik.setFkFragorOchSvarFranVarden(fkFragorOchSvarService.getFkFragorOchSvar(time, client));

        client.close();
        return fkStatistik;
    }
}
