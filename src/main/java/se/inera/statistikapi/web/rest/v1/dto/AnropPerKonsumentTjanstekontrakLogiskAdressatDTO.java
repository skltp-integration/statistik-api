package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "anropPerKonsumentTjanstekontrakLogiskAdressat")
@JsonInclude(Include.NON_EMPTY)
public class AnropPerKonsumentTjanstekontrakLogiskAdressatDTO {

	String konsumentHsaId;
	String konsumentBeskrivning;
	long antalAnrop;
    String tjanstekontrakt;
    String logiskAdressatHsaId;
    String logiskAdressatBeskrivning;
    String producentHsaId;
    String producentBeskrivning;
    double snittsvarstid;
    
	public String getKonsumentHsaId() {
		return konsumentHsaId;
	}
	public void setKonsumentHsaId(String konsumentHsaId) {
		this.konsumentHsaId = konsumentHsaId;
	}
	public String getKonsumentBeskrivning() {
		return konsumentBeskrivning;
	}
	public void setKonsumentBeskrivning(String konsumentBeskrivning) {
		this.konsumentBeskrivning = konsumentBeskrivning;
	}
	public long getAntalAnrop() {
		return antalAnrop;
	}
	public void setAntalAnrop(long antalAnrop) {
		this.antalAnrop = antalAnrop;
	}
	public String getTjanstekontrakt() {
		return tjanstekontrakt;
	}
	public void setTjanstekontrakt(String tjanstekontrakt) {
		this.tjanstekontrakt = tjanstekontrakt;
	}
	public String getLogiskAdressatHsaId() {
		return logiskAdressatHsaId;
	}
	public void setLogiskAdressatHsaId(String logiskAdressatHsaId) {
		this.logiskAdressatHsaId = logiskAdressatHsaId;
	}
	public String getLogiskAdressatBeskrivning() {
		return logiskAdressatBeskrivning;
	}
	public void setLogiskAdressatBeskrivning(String logiskAdressatBeskrivning) {
		this.logiskAdressatBeskrivning = logiskAdressatBeskrivning;
	}
	public String getProducentHsaId() {
		return producentHsaId;
	}
	public void setProducentHsaId(String producentHsaId) {
		this.producentHsaId = producentHsaId;
	}
	public String getProducentBeskrivning() {
		return producentBeskrivning;
	}
	public void setProducentBeskrivning(String producentBeskrivning) {
		this.producentBeskrivning = producentBeskrivning;
	}
	public double getSnittsvarstid() {
		return snittsvarstid;
	}
	public void setSnittsvarstid(double snittsvarstid) {
		this.snittsvarstid = snittsvarstid;
	}
	
    
}
