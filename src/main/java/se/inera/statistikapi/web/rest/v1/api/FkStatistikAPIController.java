package se.inera.statistikapi.web.rest.v1.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.inera.statistikapi.service.impl.FkStatistikServiceImpl;
import se.inera.statistikapi.web.rest.TimeValidator;
import se.inera.statistikapi.web.rest.v1.dto.FkAntalIntyg;
import se.inera.statistikapi.web.rest.v1.dto.FkStatistik;

import java.io.IOException;

@RestController()
@RequestMapping(value = { "/v1/fkStatistik"})
public class FkStatistikAPIController {

//    private static final String CSV_DELIMITER = ";";
    private static final Logger log = LoggerFactory.getLogger(FkStatistikAPIController.class);


    @Autowired
    FkStatistikServiceImpl fkStatistikService;

    /**
     * GET /connectionPoints -> get all the connectionPoints as csv
     * @throws IOException
     */
//    @RequestMapping(value = {"/v1/fkStatistik.csv", "/v1/fkStatistik"}, method = RequestMethod.GET)
//    public void getAllAsCsv(HttpServletResponse response,
//                            @RequestParam(required = false) String tjanstekontrakt,
//                            @RequestParam(required = false) String time) throws IOException {
//        log.debug("REST request to get all FkAntalIntyg as csv");
//        response.setContentType("text/plain; charset=utf-8");
//
//        FkAntalIntyg dto = new FkAntalIntyg(getAll(tjanstekontrakt, time));
//
//        StringBuilder strBuilder = new StringBuilder();
//        strBuilder.append("konsumentHsaId" + CSV_DELIMITER);
//        strBuilder.append("konsumentBeskrivning" + CSV_DELIMITER);
//        strBuilder.append("antalAnrop" + CSV_DELIMITER);
//        strBuilder.append("tjanstekontrakt" + CSV_DELIMITER);
//        strBuilder.append("logiskAdressatHsaId" + CSV_DELIMITER);
//        strBuilder.append("logiskAdressatBeskrivning" + CSV_DELIMITER);
//        strBuilder.append("producentHsaId" + CSV_DELIMITER);
//        strBuilder.append("producentBeskrivning" + CSV_DELIMITER);
//        strBuilder.append("snittsvarstid" + CSV_DELIMITER);
//
//        for (AnropPerKonsumentTjanstekontrakLogiskAdressatDTO item : dto.getAnrops()) {
//            strBuilder.append(System.lineSeparator());
//            strBuilder.append(item.getKonsumentHsaId() + CSV_DELIMITER);
//            strBuilder.append(item.getKonsumentBeskrivning() + CSV_DELIMITER);
//            strBuilder.append(item.getAntalAnrop() + CSV_DELIMITER);
//            strBuilder.append(item.getTjanstekontrakt() + CSV_DELIMITER);
//            strBuilder.append(item.getLogiskAdressatHsaId() + CSV_DELIMITER);
//            strBuilder.append(item.getLogiskAdressatBeskrivning() + CSV_DELIMITER);
//            strBuilder.append(item.getProducentHsaId() + CSV_DELIMITER);
//            strBuilder.append(item.getProducentBeskrivning() + CSV_DELIMITER);
//            strBuilder.append(item.getSnittsvarstid());
//        }
//
//        response.getWriter().print(strBuilder.toString());
//    }

    /**
     * GET /connectionPoints -> get all the connectionPoints as json
     */
    @RequestMapping( value={"","json"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FkStatistik getAllAsJson(
             @RequestParam(required = false) String time) {
        log.debug("REST request to get all FkStatistik as json");

        TimeValidator.validateTime(time);
        return fkStatistikService.getFkStatistik(time);
    }

//    /**
//     * GET /connectionPoints -> get all the connectionPoints as xml
//     */
//    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
//    public FkAntalIntyg getAllAsXml(
//            @RequestParam(required = false) String tjanstekontrakt,
//            @RequestParam(required = false) String time) {
//        log.debug("REST request to get all FkAntalIntyg as xml");
//
//
//        return new AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO(getAll(tjanstekontrakt, time));
//    }


}
