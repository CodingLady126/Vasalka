package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class BookmarkObjectType implements Serializable {

    @SerializedName(Fields.BOOKMARK_OBJECT_TYPE_JSON_ID)
    private int id;
    @SerializedName(Fields.BOOKMARK_OBJECT_TYPE_JSON_NAME)
    private String name;

    public BookmarkObjectType() {
    }


    public BookmarkObjectType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void update(JSONObject json) {
        this.id = (int) Fields.get(Fields.BOOKMARK_OBJECT_TYPE_JSON_ID, json);
        this.name = (String) Fields.get(Fields.BOOKMARK_OBJECT_TYPE_JSON_NAME, json);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
