package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "fkFragorOchSvarTillVarden")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FkFragorOchSvarTillVarden {

    private IntygGrupperatPaReceiverIds fragorOchSvarFranFKTillVarden;

    private long antalFel;
    private long antalBehorighetsFel;
    private long antalRoutingFel;

    public IntygGrupperatPaReceiverIds getFragorOchSvarFranFKTillVarden() {
        return fragorOchSvarFranFKTillVarden;
    }

    public void setFragorOchSvarFranFKTillVarden(IntygGrupperatPaReceiverIds fragorOchSvarFranFKTillVarden) {
        this.fragorOchSvarFranFKTillVarden = fragorOchSvarFranFKTillVarden;
    }

    public long getAntalFel() {
        return antalFel;
    }

    public void setAntalFel(long antalFel) {
        this.antalFel = antalFel;
    }

    public long getAntalBehorighetsFel() {
        return antalBehorighetsFel;
    }

    public void setAntalBehorighetsFel(long antalBehorighetsFel) {
        this.antalBehorighetsFel = antalBehorighetsFel;
    }

    public long getAntalRoutingFel() {
        return antalRoutingFel;
    }

    public void setAntalRoutingFel(long antalRoutingFel) {
        this.antalRoutingFel = antalRoutingFel;
    }
}
