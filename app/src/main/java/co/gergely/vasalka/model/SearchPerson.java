package co.gergely.vasalka.model;

import android.support.annotation.NonNull;
import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class SearchPerson extends Person implements Serializable, Comparable<SearchPerson> {

    @SerializedName(Fields.SEARCH_JSON_PERSON_DISTANCE)
    private double distance;

    public SearchPerson() {
    }

    public SearchPerson(JSONObject json) {
        update(json);
    }

    @Override
    public void update(JSONObject json) {
        super.update(json);
        this.distance = (double) Fields.get(Fields.SEARCH_JSON_PERSON_DISTANCE, json);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    @Override
    public int compareTo(@NonNull SearchPerson o) {
        if (this.distance > o.distance) {
            return (int) Math.round(this.getDistance() - o.getDistance());
        } else if (o.distance > this.distance) {
            return (int) Math.round(o.getDistance() - this.getDistance());
        } else {
            return 0;
        }
    }
}
