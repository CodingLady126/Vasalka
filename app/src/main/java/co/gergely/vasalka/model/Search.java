package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Search implements Serializable {
    private static final String TAG = "Search";

    @SerializedName(Fields.SEARCH_JSON_ID)
    private Long id;
    @SerializedName(Fields.SEARCH_JSON_DATE_TIME)
    private Date dateTime;
    @SerializedName(Fields.SEARCH_JSON_CITY)
    private City city;
    @SerializedName(Fields.SEARCH_JSON_SERVICE_AREA)
    private int serviceArea;
    @SerializedName(Fields.SEARCH_JSON_SERVICE)
    private List<Service> serviceList;
    @SerializedName(Fields.SEARCH_JSON_PERSON_ID)
    private Long personId;

    public Search() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public int getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(int serviceArea) {
        this.serviceArea = serviceArea;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", serviceArea=" + serviceArea +
                ", city=" + city +
                ", serviceList=" + serviceList +
                ", person_id=" + personId +
                '}';
    }
}
