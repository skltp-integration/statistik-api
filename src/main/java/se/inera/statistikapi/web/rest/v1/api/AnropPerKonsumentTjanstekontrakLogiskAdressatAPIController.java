/**
 * 
 */
package se.inera.statistikapi.web.rest.v1.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import se.inera.statistikapi.web.rest.exception.TimeFormatException;
import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;
import se.inera.statistikapi.web.rest.v1.dto.list.AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO;

@RestController()
@RequestMapping(value = { "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat", "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat.xml", "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat.json", ""})
public class AnropPerKonsumentTjanstekontrakLogiskAdressatAPIController {

	private static final String CSV_DELIMITER = ";";
	private static final String TIME_ALLOWED_REGEX = "([0-9]+[s|m|h|d|w])";
	private static final int MAX_WEEKS = 2;
	private static final int MAX_DAYS = MAX_WEEKS*14;
	private static final int MAX_HOURS = MAX_DAYS*24;
	private static final int MAX_MINUTES = MAX_HOURS*60;
	private static final int MAX_SECONDS = MAX_MINUTES*60;

	private static final String TIME_WRONG_FORMAT_ERROR_MESSAGE = "Time har ett falaktigt format.";
	private static final String TIME_EXCEEDS_MAX_ERROR_MESSAGE = "Time får max vara " + MAX_WEEKS + " veckor bakåt i tiden.";

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
	@RequestMapping(value = {"/v1/anropPerKonsumentTjanstekontrakLogiskAdressat.csv", "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat"}, method = RequestMethod.GET)
	public void getAllAsCsv(HttpServletResponse response,
			@RequestParam(required = false) String tjanstekontrakt, 
			@RequestParam(required = false) String time) throws IOException {
		log.debug("REST request to get all AnropPerKonsumentTjanstekontrakLogiskAdressatDTO as csv");
		response.setContentType("text/plain; charset=utf-8");
		
		validateTime(time);
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
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> getAllAsJson(
			@RequestParam(required = false) String tjanstekontrakt, 
			@RequestParam(required = false) String time) {
		log.debug("REST request to get all AnropPerKonsumentTjanstekontrakLogiskAdressatDTO as json");

		validateTime(time);
		
		return getAll(tjanstekontrakt, time);
	}

	/**
	 * GET /connectionPoints -> get all the connectionPoints as xml
	 */
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO getAllAsXml(
			@RequestParam(required = false) String tjanstekontrakt, 
			@RequestParam(required = false) String time) {
		log.debug("REST request to get all AnropPerKonsumentTjanstekontrakLogiskAdressatDTO as xml");

		validateTime(time);
		
		return new AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO(getAll(tjanstekontrakt, time));
	}
	
	private void validateTime(String time) {
		if(time == null) {
			return;
		}
		time = time.toLowerCase();
		
		if(!time.matches(TIME_ALLOWED_REGEX)) {
			throw new TimeFormatException(TIME_WRONG_FORMAT_ERROR_MESSAGE);
		}
		
		if(time.endsWith("s") && getTimeNumberPart(time) > MAX_SECONDS) {
			throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
		}
		
		if(time.endsWith("m") && getTimeNumberPart(time) > MAX_MINUTES) {
			throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
		}
		
		if(time.endsWith("h") && getTimeNumberPart(time) > MAX_HOURS) {
			throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
		}
		
		if(time.endsWith("d") && getTimeNumberPart(time) > MAX_DAYS) {
			throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
		}
		
		if(time.endsWith("w") && getTimeNumberPart(time) > MAX_WEEKS) {
			throw new TimeFormatException(TIME_EXCEEDS_MAX_ERROR_MESSAGE);
		}
	}

	private int getTimeNumberPart(String time) {
		return Integer.parseInt(time.substring(0, time.length()-1));
	}
}
