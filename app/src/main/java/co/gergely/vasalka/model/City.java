package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONObject;

import java.io.Serializable;

public class City implements Serializable {

    private static final String TAG = "City";
    private Long id;
    private String name;
    private Double lat;
    private Double lng;

    public City() {
    }

    public City(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public City(Long id, String name, Double lat, Double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public City(JSONObject json) {
        update(json);
    }

    private void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.CITY_JSON_ID, json);
        this.name = (String) Fields.get(Fields.CITY_JSON_NAME, json);
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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }


    @Override
    public boolean equals(Object obj) {
        if (this.id == ((City) obj).id && this.name.equals(((City) obj).name)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }


}
