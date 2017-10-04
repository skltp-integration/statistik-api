package se.inera.statistikapi.web.rest.v1.api;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import se.inera.statistikapi.web.rest.v1.dto.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping(value = { "/v1/fkStatistik"})
public class FkStatistikAPIController {

    private static final Logger log = LoggerFactory.getLogger(FkStatistikAPIController.class);

    public static final String PARAM_TID_DESCRIPTION = "Hur långt tillbaka i tiden statistik skall tas. Ex. 1h, 2d, 1w. Om inget anges används 1w d.v.s. 1 vecka bakåt. Max 2 veckor är tillåtet.";
    public static final String OPERATION_NOTES = "Hämtar FkStatistik. Du kan begränsa genom att ange tid.";


    @Autowired
    FkStatistikServiceImpl fkStatistikService;

    /**
     * GET /connectionPoints -> get all the connectionPoints as csv
     * @throws IOException
     */
    @ApiOperation(value = "FkStatistik i text format",
            notes = OPERATION_NOTES )
    @RequestMapping(value={"txt"}, method = RequestMethod.GET)
    public void getFkStatistikAsTxt(HttpServletResponse response,
                            @RequestParam(required = false) @ApiParam(value = PARAM_TID_DESCRIPTION) String time) throws IOException {
        log.debug("REST request to get FkStatistik as txt");
        response.setContentType("text/plain; charset=utf-8");

        TimeValidator.validateTime(time);
        FkStatistik fkStatistik =  fkStatistikService.getFkStatistik(time);
        StringBuilder strBuilder = new StringBuilder();

        FkAntalIntyg fkAntalIntyg = fkStatistik.getFkAntalIntyg();
        strBuilder.append("Läkarintyg till FK\n");
        strBuilder.append("--------------------\n");
        strBuilder.append("Antal RegisterMedicalCertificate totalt till FK (bildref: C + F): "+ fkAntalIntyg.getAntalRegisterMedicalCertificateToFKTot()+"\n");
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Antal RegisterMedicalCertificate till FK direkt (bildref: F):     "+ fkAntalIntyg.getRegisterMedicalCertificateToFKDirekt().getAntal()+"\n");
        addIntygPerSenderList(strBuilder, fkAntalIntyg.getRegisterMedicalCertificateToFKDirekt().getIntygPerSenderIdList());
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Antal SendMedicalCertificate in till Intygstj (bildref: A):       "+ fkAntalIntyg.getSendMedicalCertificateToIntygstjansten().getAntal()+"\n");
        addIntygPerSenderList(strBuilder, fkAntalIntyg.getSendMedicalCertificateToIntygstjansten().getIntygPerSenderIdList());
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Antal fel (RegisterMedicalCertificate) "+ fkAntalIntyg.getFelRegisterMedicalCertificate().getAntalFelTotal()+"\n");
        strBuilder.append("   varav under: Servicefönster         "+ fkAntalIntyg.getFelRegisterMedicalCertificate().getAntalFelOnSundays()+"\n");
        strBuilder.append("   varav under: Annan tid              "+ fkAntalIntyg.getFelRegisterMedicalCertificate().getAntalFelNotSundays()+"\n");
        strBuilder.append("Antal fel av totalt till FK            "+ fkAntalIntyg.getFelRegisterMedicalCertificate().getAntalFelAvTotal()+"\n");

        FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden = fkStatistik.getFkFragorOchSvarFranVarden();
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Frågor och svar från vården till FK\n");
        strBuilder.append("------------------------------------\n");
        strBuilder.append("Antal totalt " + fkFragorOchSvarFranVarden.getFragorOchSvarFranVardenTillFK().getAntal()+"\n");
        addIntygPerSenderList(strBuilder, fkFragorOchSvarFranVarden.getFragorOchSvarFranVardenTillFK().getIntygPerSenderIdList());
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Antal fel                      "+ fkFragorOchSvarFranVarden.getFragorOchSvarFel().getAntalFelTotal()+"\n");
        strBuilder.append("   varav under: Servicefönster "+ fkFragorOchSvarFranVarden.getFragorOchSvarFel().getAntalFelOnSundays()+"\n");
        strBuilder.append("   varav under: Annan tid      "+ fkFragorOchSvarFranVarden.getFragorOchSvarFel().getAntalFelNotSundays()+"\n");

        FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden = fkStatistik.getFkFragorOchSvarTillVarden();
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Frågor och svar från FK till vården\n");
        strBuilder.append("-----------------------------------\n");
        strBuilder.append("Antal totalt              " + fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().getAntal()+"\n");
        strBuilder.append("  varav typ komplettering " + fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().getVaravKomplettering()+"\n");
        strBuilder.append("  varav typ påminnelse    " + fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().getVaravPaminnelse()+"\n");
        addIntygPerReceiverList(strBuilder, fkFragorOchSvarTillVarden.getFragorOchSvarFranFKTillVarden().getIntygPerReceiverIdList());
        strBuilder.append(System.lineSeparator());
        strBuilder.append("Antal fel               "+ fkFragorOchSvarTillVarden.getAntalFel()+"\n");
        strBuilder.append("   varav routingfel     "+ fkFragorOchSvarTillVarden.getAntalRoutingFel()+"\n");
        strBuilder.append("   varav behörighetsfel "+ fkFragorOchSvarTillVarden.getAntalBehorighetsFel()+"\n");

        response.getWriter().print(strBuilder.toString());
    }

    private void addIntygPerReceiverList(StringBuilder strBuilder, List<IntygPerReceiverId> intygPerReceiverIdList) {
        if(intygPerReceiverIdList!=null && !intygPerReceiverIdList.isEmpty()) {
            strBuilder.append("receiverId                    Anrop  Namn\n");
            for (IntygPerReceiverId intygPerReceiverId : intygPerReceiverIdList) {
                strBuilder.append(String.format("%-30s%-7d%s\n", intygPerReceiverId.getReceiverId(), intygPerReceiverId.getAnrop(), intygPerReceiverId.getNamn()));
            }
        }
    }

    private void addIntygPerSenderList(StringBuilder strBuilder, List<IntygPerSenderId> intygPerSenderIdList) {
        if(intygPerSenderIdList!=null && !intygPerSenderIdList.isEmpty()) {
            strBuilder.append("senderId                      Anrop  Namn\n");
            for (IntygPerSenderId intygPerSenderId : intygPerSenderIdList) {
                strBuilder.append(String.format("%-30s%-7d%s\n", intygPerSenderId.getSenderId(), intygPerSenderId.getAnrop(), intygPerSenderId.getNamn()));
            }
        }
    }

    /**
     * GET /connectionPoints -> get all the connectionPoints as json
     */
    @ApiOperation(value = "FkStatistik i json/xml format",
            notes = OPERATION_NOTES )
    @RequestMapping( value={""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public FkStatistik getAllAsJson(
             @RequestParam(required = false)  @ApiParam(value = PARAM_TID_DESCRIPTION) String time) {
        log.debug("REST request to get all FkStatistik as json/xml");

        TimeValidator.validateTime(time);
        return fkStatistikService.getFkStatistik(time);
    }

}
