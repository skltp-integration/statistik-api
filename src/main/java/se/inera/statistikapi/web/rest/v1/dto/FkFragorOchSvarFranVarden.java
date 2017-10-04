package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "fkFragorOchSvarFranVarden")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FkFragorOchSvarFranVarden {

    private IntygGrupperatPaSenderIds fragorOchSvarFranVardenTillFK;
    private IntygErrors fragorOchSvarFel;

    public IntygGrupperatPaSenderIds getFragorOchSvarFranVardenTillFK() {
        return fragorOchSvarFranVardenTillFK;
    }

    public void setFragorOchSvarFranVardenTillFK(IntygGrupperatPaSenderIds fragorOchSvarFranVardenTillFK) {
        this.fragorOchSvarFranVardenTillFK = fragorOchSvarFranVardenTillFK;
    }

    public IntygErrors getFragorOchSvarFel() {
        return fragorOchSvarFel;
    }

    public void setFragorOchSvarFel(IntygErrors fragorOchSvarFel) {
        this.fragorOchSvarFel = fragorOchSvarFel;
    }
}
