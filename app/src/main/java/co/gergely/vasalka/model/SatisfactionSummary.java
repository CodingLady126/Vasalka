package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class SatisfactionSummary implements Serializable {

    @SerializedName(Fields.SATISFACTION_SUM_JSON_AVG)
    private Double avg;
    @SerializedName(Fields.SATISFACTION_SUM_JSON_COUNT)
    private int count;

    public SatisfactionSummary() {
    }

    public void update(JSONObject json) {
        this.avg = (Double) Fields.get(Fields.SATISFACTION_SUM_JSON_AVG, json);
        this.count = (int) Fields.get(Fields.SATISFACTION_SUM_JSON_COUNT, json);
    }


    public Double getAvg() {
        return avg;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "{" +
                "avg=" + avg +
                ", count=" + count +
                '}';
    }
}
