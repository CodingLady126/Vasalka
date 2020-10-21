package co.gergely.vasalka.model;

import android.util.Log;
import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tender implements Serializable {
    private static final String TAG = "Tender";

    @SerializedName(Fields.TENDER_JSON_ID)
    private Long id;
    @SerializedName(Fields.TENDER_JSON_CREATED_TIME)
    private String createdAt;
    @SerializedName(Fields.TENDER_JSON_UPDATED_TIME)
    private String updatedAt;
    @SerializedName(Fields.TENDER_JSON_DESCRIPTION)
    private String description;
    @SerializedName(Fields.TENDER_JSON_SERVICE_AREA)
    private int serviceArea;
    @SerializedName(Fields.TENDER_JSON_ALIVE)
    private Boolean alive;
    @SerializedName(Fields.TENDER_JSON_SERVICE_LIST)
    private List<Service> serviceList;
    @SerializedName(Fields.TENDER_JSON_PERSON)
    private Person person;
    @SerializedName(Fields.TENDER_JSON_IMAGE_1)
    private String image1;
    @SerializedName(Fields.TENDER_JSON_IMAGE_2)
    private String image2;
    @SerializedName(Fields.TENDER_JSON_IMAGE_3)
    private String image3;
    @SerializedName(Fields.TENDER_JSON_CITY)
    private City city;

    public Tender() {
    }

    public Tender(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.TENDER_JSON_ID, json);

        this.createdAt = (String) Fields.get(Fields.TENDER_JSON_CREATED_TIME, json);
        this.updatedAt = (String) Fields.get(Fields.TENDER_JSON_UPDATED_TIME, json);
        this.description = (String) Fields.get(Fields.TENDER_JSON_DESCRIPTION, json);
        this.serviceArea = (int) Fields.get(Fields.TENDER_JSON_SERVICE_AREA, json);
        this.alive = (boolean) Fields.get(Fields.TENDER_JSON_ALIVE, json);

        try {
            this.person = new Person();
            this.person.update(json.getJSONObject(Fields.TENDER_JSON_PERSON));
        }catch (JSONException e) {
            Log.d(TAG, "update onPerson: " + e.getLocalizedMessage());
        }

        try {
            JSONArray jsonArray = json.getJSONArray(Fields.TENDER_JSON_SERVICE_LIST);
            for(int i=0; i<jsonArray.length(); i++) {
                Service service = new Service(jsonArray.getJSONObject(i));
                if(this.serviceList == null) {
                    this.serviceList = new ArrayList<>();
                }
                this.serviceList.add(service);
            }
        } catch (JSONException e) {
            Log.d(TAG, "update onService: " + e.getLocalizedMessage());
        }

        try {
            this.city = new City(json.getJSONObject(Fields.TENDER_JSON_CITY));
        } catch (JSONException e) {
            Log.d(TAG, "update onCity: " + e.getLocalizedMessage());
        }
        this.image1 = (String) Fields.get(Fields.TENDER_JSON_IMAGE_1, json);
        this.image2 = (String) Fields.get(Fields.TENDER_JSON_IMAGE_2, json);
        this.image3 = (String) Fields.get(Fields.TENDER_JSON_IMAGE_3, json);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(int serviceArea) {
        this.serviceArea = serviceArea;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public void addService(Service service) {
        if (this.serviceList == null) {
            this.serviceList = new ArrayList<>();
        }
        if (!containsService(service)) {
            this.serviceList.add(service);
        }
    }

    private boolean containsService(Service service) {
        if (this.serviceList == null || this.serviceList.isEmpty()) {
            return false;
        }

        for (Service src : this.serviceList) {
            if (src.getId() == service.getId()) {
                return true;
            }
        }
        return false;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", description='" + description + '\'' +
                ", serviceArea=" + serviceArea +
                ", alive=" + alive +
                ", serviceList=" + serviceList +
                ", person=" + person +
                ", image1='" + image1 + '\'' +
                ", image2='" + image2 + '\'' +
                ", image3='" + image3 + '\'' +
                ", city=" + city +
                '}';
    }
}
