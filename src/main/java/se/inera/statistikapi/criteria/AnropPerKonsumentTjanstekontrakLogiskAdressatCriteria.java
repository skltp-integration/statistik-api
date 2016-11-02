package se.inera.statistikapi.criteria;

public class AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria {

	private String tjanstekontrakt;
	private String time;
	
	public AnropPerKonsumentTjanstekontrakLogiskAdressatCriteria(
			String tjanstekontrakt, String time) {
		this.tjanstekontrakt = tjanstekontrakt;
		this.time = time;
	}

	public String getTjanstekontrakt() {
		return tjanstekontrakt;
	}

	public void setTjanstekontrakt(String tjanstekontrakt) {
		this.tjanstekontrakt = tjanstekontrakt;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
