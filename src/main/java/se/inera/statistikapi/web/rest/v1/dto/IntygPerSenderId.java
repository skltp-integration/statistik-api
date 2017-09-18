package se.inera.statistikapi.web.rest.v1.dto;

public class IntygPerSenderId {
        public String senderId;
        public long anrop;
        public String namn;

        public IntygPerSenderId(String senderId, long docCount, String description) {
            this.senderId = senderId;
            this.anrop = docCount;
            this.namn = description;
        }

}