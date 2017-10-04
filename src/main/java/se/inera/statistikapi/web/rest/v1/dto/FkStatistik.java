package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "fkStatistik")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FkStatistik {

    private FkAntalIntyg fkAntalIntyg;
    private FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden;
    private FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden;

    public FkAntalIntyg getFkAntalIntyg() {
        return fkAntalIntyg;
    }

    public void setFkAntalIntyg(FkAntalIntyg fkAntalIntyg) {
        this.fkAntalIntyg = fkAntalIntyg;
    }

    public FkFragorOchSvarFranVarden getFkFragorOchSvarFranVarden() {
        return fkFragorOchSvarFranVarden;
    }

    public void setFkFragorOchSvarFranVarden(FkFragorOchSvarFranVarden fkFragorOchSvarFranVarden) {
        this.fkFragorOchSvarFranVarden = fkFragorOchSvarFranVarden;
    }

    public FkFragorOchSvarTillVarden getFkFragorOchSvarTillVarden() {
        return fkFragorOchSvarTillVarden;
    }

    public void setFkFragorOchSvarTillVarden(FkFragorOchSvarTillVarden fkFragorOchSvarTillVarden) {
        this.fkFragorOchSvarTillVarden = fkFragorOchSvarTillVarden;
    }
}
