package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Bookmark implements Serializable {

    @SerializedName(Fields.BOOKMARK_JSON_ID)
    private Long id;
    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_PERSON)
    private Person person;
    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_TENDER)
    private Tender tender;
    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_CHAT)
    private ChatRoom chatRoom;
    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_TYPE)
    private BookmarkObjectType objectType;
    @SerializedName(Fields.BOOKMARK_JSON_CREATED_AT)
    private String createdAt;


    public Bookmark() {
    }


    public void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.BOOKMARK_JSON_ID, json);
        try {
            JSONObject object = json.getJSONObject(Fields.BOOKMARK_JSON_OBJECT_TYPE);
            if (object != null) {
                this.objectType = new BookmarkObjectType();
                this.objectType.update(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.person = new Person();
            this.person.update(json.getJSONObject(Fields.BOOKMARK_JSON_OBJECT_PERSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.tender = new Tender();
            this.tender.update(json.getJSONObject(Fields.BOOKMARK_JSON_OBJECT_TENDER));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.chatRoom = new ChatRoom();
            this.chatRoom.update(json.getJSONObject(Fields.BOOKMARK_JSON_OBJECT_CHAT));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        this.createdAt = (String) Fields.get(Fields.BOOKMARK_JSON_CREATED_AT, json);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Tender getTender() {
        return tender;
    }

    public void setTender(Tender tender) {
        this.tender = tender;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public BookmarkObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(BookmarkObjectType objectType) {
        this.objectType = objectType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "id=" + id +
                ", person=" + person +
                ", tender=" + tender +
                ", chatRoom=" + chatRoom +
                ", objectType=" + objectType +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
