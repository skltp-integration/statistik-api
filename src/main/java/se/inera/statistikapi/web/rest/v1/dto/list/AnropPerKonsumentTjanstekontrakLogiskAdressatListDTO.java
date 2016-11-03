package se.inera.statistikapi.web.rest.v1.dto.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import se.inera.statistikapi.web.rest.v1.dto.AnropPerKonsumentTjanstekontrakLogiskAdressatDTO;

@JacksonXmlRootElement(localName="anropPerKonsumentTjanstekontrakLogiskAdressatList")
public class AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO {

	@JacksonXmlProperty(localName = "anropPerKonsumentTjanstekontrakLogiskAdressat")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> anrops = new ArrayList<>();
	
	public AnropPerKonsumentTjanstekontrakLogiskAdressatListDTO(
			List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> all) {
		this.anrops = all;
	}

	@XmlElement(name = "anropPerKonsumentTjanstekontrakLogiskAdressat")
	public List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> getAnrops() {
		return anrops;
	}

	public void setAnrops(List<AnropPerKonsumentTjanstekontrakLogiskAdressatDTO> anrops) {
		this.anrops = anrops;
	}
}
