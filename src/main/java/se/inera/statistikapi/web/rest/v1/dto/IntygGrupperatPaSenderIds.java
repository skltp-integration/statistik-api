package se.inera.statistikapi.web.rest.v1.dto;

import java.util.List;

public class IntygGrupperatPaSenderIds {
    private long antal;
    private List<IntygPerSenderId> intygPerSenderIdList;

    public long getAntal() {
        return antal;
    }

    public void setAntal(long antal) {
        this.antal = antal;
    }

    public List<IntygPerSenderId> getIntygPerSenderIdList() {
        return intygPerSenderIdList;
    }

    public void setIntygPerSenderIdList(List<IntygPerSenderId> intygPerSenderIdList) {
        this.intygPerSenderIdList = intygPerSenderIdList;
    }


}
