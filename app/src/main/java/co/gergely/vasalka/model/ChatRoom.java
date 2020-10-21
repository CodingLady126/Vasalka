package co.gergely.vasalka.model;

import android.util.Log;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ChatRoom implements Serializable {

    @SerializedName(Fields.CHATROOM_JSON_ID)
    private Long id;
    @SerializedName(Fields.CHATROOM_JSON_FB_ID)
    private String fbId;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_ONE)
    private Person personOne;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_TWO)
    private Person personTwo;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_ONE_ALIVE)
    private boolean personOneAlive;
    @SerializedName(Fields.CHATROOM_JSON_PERSON_TWO_ALIVE)
    private boolean personTwoAlive;


    public ChatRoom() {
    }

    public ChatRoom(Long id, String fbId, Person personOne, Person personTwo) {
        this.id = id;
        this.fbId = fbId;
        this.personOne = personOne;
        this.personTwo = personTwo;
    }

    public ChatRoom(JSONObject jsonObject) {
        update(jsonObject);
    }

    public void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.CHATROOM_JSON_ID, json);
        this.fbId = (String) Fields.get(Fields.CHATROOM_JSON_FB_ID, json);
        this.personOneAlive = (boolean) Fields.get(Fields.CHATROOM_JSON_PERSON_ONE_ALIVE, json);
        this.personTwoAlive = (boolean) Fields.get(Fields.CHATROOM_JSON_PERSON_TWO_ALIVE, json);

        try {
            this.personOne = new Person();
            this.personOne.update(json.getJSONObject(Fields.CHATROOM_JSON_PERSON_ONE));
        }catch (JSONException e) {
            if(BuildConfig.DEBUG) {
                Log.d("ChatRoom", "update: " + e.getLocalizedMessage());
            }
        }
        try {
            this.personTwo = new Person();
            this.personTwo.update(json.getJSONObject(Fields.CHATROOM_JSON_PERSON_TWO));
        }catch (JSONException e) {
            if(BuildConfig.DEBUG) {
                Log.d("ChatRoom", "update: " + e.getLocalizedMessage());
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public Person getPersonOne() {
        return personOne;
    }

    public void setPersonOne(Person personOne) {
        this.personOne = personOne;
    }

    public Person getPersonTwo() {
        return personTwo;
    }

    public void setPersonTwo(Person personTwo) {
        this.personTwo = personTwo;
    }

    public boolean isPersonOneAlive() {
        return personOneAlive;
    }

    public void setPersonOneAlive(boolean personOneAlive) {
        this.personOneAlive = personOneAlive;
    }

    public boolean isPersonTwoAlive() {
        return personTwoAlive;
    }

    public void setPersonTwoAlive(boolean personTwoAlive) {
        this.personTwoAlive = personTwoAlive;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", fbId='" + fbId + '\'' +
                ", personOne=" + personOne +
                ", personTwo=" + personTwo +
                ", personOneAlive=" + personOneAlive +
                ", personTwoAlive=" + personTwoAlive +
                '}';
    }
}
