package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONObject;

import java.io.Serializable;

public class TenderAlertSum implements Serializable {

    private Long tenderId;
    private int alerted;
    private int saw;
    private int applied;


    public TenderAlertSum() {
    }

    public TenderAlertSum(JSONObject json) {
        update(json);
    }

    private void update(JSONObject json) {
        this.tenderId = (Long) Fields.get(Fields.TENDER_ALERT_JSON_ID, json);
        this.alerted = (int) Fields.get(Fields.TENDER_ALERT_JSON_ALERTED, json);
        this.saw = (int) Fields.get(Fields.TENDER_ALERT_JSON_SAW, json);
        this.applied = (int) Fields.get(Fields.TENDER_ALERT_JSON_APPLIED, json);
    }

    public Long getTenderId() {
        return tenderId;
    }

    public void setTenderId(Long tenderId) {
        this.tenderId = tenderId;
    }

    public int getAlerted() {
        return alerted;
    }

    public void setAlerted(int alerted) {
        this.alerted = alerted;
    }

    public int getSaw() {
        return saw;
    }

    public void setSaw(int saw) {
        this.saw = saw;
    }

    public int getApplied() {
        return applied;
    }

    public void setApplied(int applied) {
        this.applied = applied;
    }

    @Override
    public String toString() {
        return "{" +
                "tenderId=" + tenderId +
                ", alerted=" + alerted +
                ", saw=" + saw +
                ", applied=" + applied +
                '}';
    }
}
