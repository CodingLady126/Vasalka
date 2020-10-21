package co.gergely.vasalka.model;

import android.util.Log;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Satisfaction implements Serializable {
    private static final String TAG = "Satisfaction";

    private Long id;
    @SerializedName(Fields.SATISFACTION_JSON_RATE)
    private int rate;
    @SerializedName(Fields.SATISFACTION_JSON_DATE_TIME)
    private String dateTime;
    @SerializedName(Fields.SATISFACTION_JSON_DESC)
    private String description;
    @SerializedName(Fields.SATISFACTION_JSON_SRC_PERSON)
    private Person srcPerson;
    @SerializedName(Fields.SATISFACTION_JSON_DST_PERSON)
    private Person dstPerson;
    @SerializedName(Fields.SATISFACTION_JSON_SERVICE)
    private Long serviceId;


    public Satisfaction() {
        SimpleDateFormat dt = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
        dateTime = dt.format(new Date());
    }

    public Satisfaction(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.SATISFACTION_JSON_ID, json);
        this.description = (String) Fields.get(Fields.SATISFACTION_JSON_DESC, json);
        this.rate = (int) Fields.get(Fields.SATISFACTION_JSON_RATE, json);
        this.dateTime = (String) Fields.get(Fields.SATISFACTION_JSON_DATE_TIME, json);
        this.serviceId = (Long) Fields.get(Fields.SATISFACTION_JSON_SERVICE, json);
        try {
            this.srcPerson = new Person();
            this.srcPerson.update(json.getJSONObject(Fields.SATISFACTION_JSON_SRC_PERSON));
        }catch (JSONException e) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "update: " + e.getLocalizedMessage());
            }
        }
        try {
            this.dstPerson = new Person();
            this.dstPerson.update(json.getJSONObject(Fields.SATISFACTION_JSON_DST_PERSON));
        }catch (JSONException e) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "update: " + e.getLocalizedMessage());
            }
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getSrcPerson() {
        return srcPerson;
    }

    public void setSrcPerson(Person srcPerson) {
        this.srcPerson = srcPerson;
    }

    public Person getDstPerson() {
        return dstPerson;
    }

    public void setDstPerson(Person dstPerson) {
        this.dstPerson = dstPerson;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", rate=" + rate +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", srcPerson=" + srcPerson +
                ", dstPerson=" + dstPerson +
                ", service_id=" + serviceId +
                '}';
    }
}
