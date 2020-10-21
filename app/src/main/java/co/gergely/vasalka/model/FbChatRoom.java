package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FbChatRoom implements Serializable {

    private static final String TAG = "FbChatRoom";
    @SerializedName(Fields.CHATROOM_JSON_FB_ID)
    private String id;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_ONE)
    private Long personOne;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_TWO)
    private Long personTwo;
    @SerializedName(Fields.CHATROOM_JSON_MESSAGES)
    private List<ChatMessage> messageList;

    public FbChatRoom() {
    }

    public FbChatRoom(String id, Long personOne, Long personTwo, List<ChatMessage> messageList) {
        this.id = id;
        this.personOne = personOne;
        this.personTwo = personTwo;
        this.messageList = messageList;
    }

    public FbChatRoom(ChatRoom chatroom) {
        this.id = chatroom.getFbId();
        this.personOne = chatroom.getPersonOne().getId();
        this.personTwo = chatroom.getPersonTwo().getId();
        this.messageList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPersonOne() {
        return personOne;
    }

    public void setPersonOne(Long personOne) {
        this.personOne = personOne;
    }

    public Long getPersonTwo() {
        return personTwo;
    }

    public void setPersonTwo(Long personTwo) {
        this.personTwo = personTwo;
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", personOne=" + personOne +
                ", personTwo=" + personTwo +
                ", messageList=" + messageList +
                '}';
    }
}
