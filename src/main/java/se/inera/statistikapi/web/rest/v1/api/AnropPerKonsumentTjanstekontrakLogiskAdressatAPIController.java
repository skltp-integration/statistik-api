/**
 * 
 */
package se.inera.statistikapi.web.rest.v1.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
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

import se.inera.statistikapi.criteria.AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria;
import se.inera.statistikapi.service.AnropPerKonsumentTjanstekontrakLogiskAdressatService;
import se.inera.statistikapi.web.rest.TimeValidator;
import se.inera.statistikapi.web.rest.exception.TimeFormatException;
import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;
import se.inera.statistikapi.web.rest.v1.dto.list.AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO;


@RestController()
@RequestMapping(value = { "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat"})
public class AnropPerKonsumentTjanstekontrakLogiskAdressatAPIController {

    private static final String CSV_DELIMITER = ";";


	public static final String PARAM_TID_DESCRIPTION = "Hur långt tillbaka i tiden statistik skall tas. Ex. 1h, 2d, 1w. Om inget anges används 1w d.v.s. 1 vecka bakåt. Max 2 veckor är tillåtet.";
	public static final String PARAM_KONTRAKT_DESCRIPTION = "Vilket tjänstekontrakt som man vill ha information om. Om inget anges visas alla.";
    public static final String OPERATION_NOTES = "Hämtar anropPerKonsumentTjanstekontraktLogiskAdressat. Du kan begränsa genom att ange tid och/eller vilket tjänstekontrakt med parameterar nedan.";

    private final Logger log = LoggerFactory.getLogger(AnropPerKonsumentTjanstekontrakLogiskAdressatAPIController.class);


	@Autowired
	private AnropPerKonsumentTjanstekontrakLogiskAdressatService anropPerKonsumentTjanstekontrakLogiskAdressatService;
	
	private List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> getAll(String tjanstekontrakt, String time) {

		AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria = new AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria(tjanstekontrakt, time);
		List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> posts = anropPerKonsumentTjanstekontrakLogiskAdressatService.findAll(criteria);

		return posts;
	}
	
	/**
	 * GET /connectionPoints -> get all the connectionPoints as csv
	 * @throws IOException 
	 */
	@ApiOperation(value = "anropPerKonsumentTjanstekontraktLogiskAdressat i text/csv format",
			notes = OPERATION_NOTES )
	@RequestMapping(value={"txt"}, method = RequestMethod.GET)
	public void getAllAsCsv(HttpServletResponse response,
			@RequestParam(required = false) @ApiParam(value = PARAM_KONTRAKT_DESCRIPTION) String tjanstekontrakt,
			@RequestParam(required = false) @ApiParam(value = PARAM_TID_DESCRIPTION) String time) throws IOException {
		log.debug("REST request to get all AnropPerKonsumentTjanstekontrakLogiskAdressatDTO as csv");
		response.setContentType("text/plain; charset=utf-8");
		
		TimeValidator.validateTime(time);
		AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO dto = new AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO(getAll(tjanstekontrakt, time));
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("konsumentHsaId" + CSV_DELIMITER);
		strBuilder.append("konsumentBeskrivning" + CSV_DELIMITER);
		strBuilder.append("antalAnrop" + CSV_DELIMITER);
		strBuilder.append("tjanstekontrakt" + CSV_DELIMITER);
		strBuilder.append("logiskAdressatHsaId" + CSV_DELIMITER);
		strBuilder.append("logiskAdressatBeskrivning" + CSV_DELIMITER);
		strBuilder.append("producentHsaId" + CSV_DELIMITER);
		strBuilder.append("producentBeskrivning" + CSV_DELIMITER);
		strBuilder.append("snittsvarstid" + CSV_DELIMITER);
		
		for (AnropPerKonsumentTjanstekontrakLogiskAdressatDTO item : dto.getAnrops()) {
			strBuilder.append(System.lineSeparator());
			strBuilder.append(item.getKonsumentHsaId() + CSV_DELIMITER);
			strBuilder.append(item.getKonsumentBeskrivning() + CSV_DELIMITER);
			strBuilder.append(item.getAntalAnrop() + CSV_DELIMITER);
			strBuilder.append(item.getTjanstekontrakt() + CSV_DELIMITER);
			strBuilder.append(item.getLogiskAdressatHsaId() + CSV_DELIMITER);
			strBuilder.append(item.getLogiskAdressatBeskrivning() + CSV_DELIMITER);
			strBuilder.append(item.getProducentHsaId() + CSV_DELIMITER);
			strBuilder.append(item.getProducentBeskrivning() + CSV_DELIMITER);
			strBuilder.append(item.getSnittsvarstid());
		}
		
		response.getWriter().print(strBuilder.toString());
	}
	
	/**
	 * GET /connectionPoints -> get all the connectionPoints as json
	 */
	@ApiOperation(value = "anropPerKonsumentTjanstekontraktLogiskAdressat i json/xml format",
			notes = OPERATION_NOTES, response = AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO.class)
    @RequestMapping(value={""}, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
	public List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> getAllAsJson(
			@RequestParam(required = false) @ApiParam(value = PARAM_KONTRAKT_DESCRIPTION) String tjanstekontrakt,
			@RequestParam(required = false) @ApiParam(value = PARAM_TID_DESCRIPTION) String time) {
		log.debug("REST request to get all AnropPerKonsumentTjanstekontrakLogiskAdressatDTO as json");

        TimeValidator.validateTime(time);

		return getAll(tjanstekontrakt, time);
	}

}
