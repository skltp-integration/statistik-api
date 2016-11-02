/**
 * 
 */
package se.inera.statistikapi.web.rest.v1.api;

import java.util.List;

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
@RequestMapping(value = { "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat", "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat.xml", "/v1/anropPerKonsumentTjanstekontrakLogiskAdressat.json" })
public class AnropPerKonsumentTjanstekontrakLogiskAdressatAPIController {

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
