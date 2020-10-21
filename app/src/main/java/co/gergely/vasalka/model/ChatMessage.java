package co.gergely.vasalka.model;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String messageText;
    private long messageTime;
    private String messageImage;
    private Long personId;

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageImage() {
        return messageImage;
    }

    public void setMessageImage(String messageImage) {
        this.messageImage = messageImage;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPerson(Long personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return "{" +
                "messageText='" + messageText + '\'' +
                ", messageTime=" + messageTime +
                ", messageImage='" + messageImage + '\'' +
                ", personId=" + personId +
                '}';
    }
}
