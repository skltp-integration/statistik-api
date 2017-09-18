package se.inera.statistikapi.web.rest.v1.dto;


public class IntygErrors {
    private Long antalFelTotal;
    private Long antalFelOnSundays;
    private Long antalFelNotSundays;
    private String antalFelAvTotal;

    public Long getAntalFelTotal() {
        return antalFelTotal;
    }

    public void setAntalFelTotal(Long antalFelTotal) {
        this.antalFelTotal = antalFelTotal;
    }

    public Long getAntalFelOnSundays() {
        return antalFelOnSundays;
    }

    public void setAntalFelOnSundays(Long antalFelOnSundays) {
        this.antalFelOnSundays = antalFelOnSundays;
    }

    public Long getAntalFelNotSundays() {
        return antalFelNotSundays;
    }

    public void setAntalFelNotSundays(Long antalFelNotSundays) {
        this.antalFelNotSundays = antalFelNotSundays;
    }

    public String getAntalFelAvTotal() {
        return antalFelAvTotal;
    }

    public void setAntalFelAvTotal(String antalFelAvTotal) {
        this.antalFelAvTotal = antalFelAvTotal;
    }



}
