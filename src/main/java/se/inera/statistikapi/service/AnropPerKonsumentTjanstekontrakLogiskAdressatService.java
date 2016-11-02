package se.inera.statistikapi.service;

import java.util.List;

import se.inera.statistikapi.criteria.AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria;
import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;

public interface AnropPerKonsumentTjanstekontrakLogiskAdressatService {

	List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> findAll(
			AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria criteria);

}
