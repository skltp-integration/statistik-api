package se.inera.statistikapi.web.rest.v1.dto;

public class IntygPerSenderId {
    private String senderId;
    private long anrop;
    private String namn;

    public IntygPerSenderId(String senderId, long docCount, String description) {
        this.senderId = senderId;
        this.anrop = docCount;
        this.namn = description;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getAnrop() {
        return anrop;
    }

    public String getNamn() {
        return namn;
    }
}