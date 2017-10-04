package se.inera.statistikapi.web.rest.v1.dto;


public class IntygPerReceiverId {
    private String receiverId;
    private long anrop;
    private String namn;

    public IntygPerReceiverId(String receiverId, long docCount, String description) {
        this.receiverId = receiverId;
        this.anrop = docCount;
        this.namn = description;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public long getAnrop() {
        return anrop;
    }

    public String getNamn() {
        return namn;
    }
}
