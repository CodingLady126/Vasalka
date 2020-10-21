package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONObject;

import java.io.Serializable;

public class Image implements Serializable {

    private static final String TAG = "Image";

    private String url;

    public Image() {
    }


    public Image(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {
        this.url = (String) Fields.get(Fields.IMAGE_JSON_URL, json);
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{" +
                ", url='" + url + '\'' +
                '}';
    }
}
