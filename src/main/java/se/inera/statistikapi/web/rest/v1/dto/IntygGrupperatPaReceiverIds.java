package se.inera.statistikapi.web.rest.v1.dto;


import java.util.List;

public class IntygGrupperatPaReceiverIds {
    private long antal;
    private long varavKomplettering;
    private long varavPaminnelse;

    private List<IntygPerReceiverId> intygPerReceiverIdList;

    public long getAntal() {
        return antal;
    }

    public void setAntal(long antal) {
        this.antal = antal;
    }

    public List<IntygPerReceiverId> getIntygPerReceiverIdList() {
        return intygPerReceiverIdList;
    }

    public void setIntygPerReceiverIdList(List<IntygPerReceiverId> intygPerReceiverIdList) {
        this.intygPerReceiverIdList = intygPerReceiverIdList;
    }

    public long getVaravPaminnelse() {
        return varavPaminnelse;
    }

    public void setVaravPaminnelse(long varavPaminnelse) {
        this.varavPaminnelse = varavPaminnelse;
    }

    public long getVaravKomplettering() {
        return varavKomplettering;
    }

    public void setVaravKomplettering(long varavKomplettering) {
        this.varavKomplettering = varavKomplettering;
    }

}
