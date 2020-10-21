package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Constants;
import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookmarkForSend implements Serializable {

    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_TYPE_ID)
    private int objectTypeId;
    @SerializedName(Fields.BOOKMARK_JSON_OBJECT_ID)
    private Long objectId;


    public BookmarkForSend() {
    }

    public BookmarkForSend(int objectTypeId, Long objectId) {
        this.objectTypeId = objectTypeId;
        this.objectId = objectId;
    }

    public BookmarkForSend(Bookmark bookmark) {
        this.objectTypeId = bookmark.getObjectType().getId();

        if (this.objectTypeId == Constants.BOOKMARK_OBJECT_TYPE_PERSON_ID) {
            this.objectId = bookmark.getPerson().getId();
        } else if (this.objectTypeId == Constants.BOOKMARK_OBJECT_TYPE_TENDER_ID) {
            this.objectId = bookmark.getTender().getId();
        } else if (this.objectTypeId == Constants.BOOKMARK_OBJECT_TYPE_CHAT_ID) {
            this.objectId = bookmark.getChatRoom().getId();
        }
    }

    public int getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(int objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "{" +
                "objectTypeId=" + objectTypeId +
                ", objectId=" + objectId +
                '}';
    }
}
