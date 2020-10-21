package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONObject;

import java.io.Serializable;

public class Service implements Serializable {

    private static final String TAG = "Service";
    private Long id;
    private String name;


    public Service() {
    }

    public Service(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Service(JSONObject json) {
        update(json);
    }

    private void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.SERVICE_JSON_ID, json);
        this.name = (String) Fields.get(Fields.SERVICE_JSON_NAME, json);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return this.name;
    }
}
