package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person extends Observable implements Serializable {

    private static final String TAG = "Person";
    @SerializedName(Fields.PERSON_JSON_ID)
    private Long id;
    @SerializedName(Fields.PERSON_JSON_NAME)
    private String name;
    @SerializedName(Fields.PERSON_JSON_EMAIL)
    private String email;
    @SerializedName(Fields.PERSON_JSON_TEL)
    private String tel;
    @SerializedName(Fields.PERSON_JSON_CITY)
    private City city;
    @SerializedName(Fields.PERSON_JSON_HAS_SERVICE)
    private boolean hasService;
    @SerializedName(Fields.PERSON_JSON_SERVICE_AREA)
    private int serviceArea;
    @SerializedName(Fields.PERSON_JSON_SERVICES)
    private List<Service> serviceList = new ArrayList<>();
    @SerializedName(Fields.PERSON_JSON_FCM_TOKEN)
    private String token;
    @SerializedName(Fields.PERSON_JSON_BIO)
    private String bio;
    @SerializedName(Fields.PERSON_JSON_USER)
    private String user;
    @SerializedName(Fields.PERSON_JSON_PHOTO)
    private String photo;
    @SerializedName(Fields.PERSON_JSON_LOGIN_TYPE)
    private int loginType;
    @SerializedName(Fields.PERSON_JSON_SATISFACTION_SUM)
    private SatisfactionSummary satisfactionSummary;
    @SerializedName(Fields.PERSON_JSON_ALERT_ON_TENDER)
    private boolean alertOnTender = true;
    @SerializedName(Fields.PERSON_JSON_ALERT_ON_CHAT)
    private boolean alertOnChat = true;
    @SerializedName(Fields.PERSON_JSON_DEVICE_ID)
    private String deviceId;
    @SerializedName(Fields.PERSON_JSON_DEVICE_NAME)
    private String deviceName;
    @SerializedName(Fields.PERSON_JSON_DEVICE_TYPE)
    private Character deviceType = 'A';


    public Person() {
    }

    public void update(JSONObject json) throws NullPointerException {

        this.id = (Long) Fields.get(Fields.PERSON_JSON_ID, json);
        this.name = (String) Fields.get(Fields.PERSON_JSON_NAME, json);
        this.email = (String) Fields.get(Fields.PERSON_JSON_EMAIL, json);
        this.tel = (String) Fields.get(Fields.PERSON_JSON_TEL, json);

        try {
            this.city = new City(json.getJSONObject(Fields.PERSON_JSON_CITY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.user = (String) Fields.get(Fields.PERSON_JSON_USER, json);
        this.hasService = (boolean) Fields.get(Fields.PERSON_JSON_HAS_SERVICE, json);
        this.token = (String) Fields.get(Fields.PERSON_JSON_FCM_TOKEN, json);
        this.photo = (String) Fields.get(Fields.PERSON_JSON_PHOTO, json);
        this.loginType = (int) Fields.get(Fields.PERSON_JSON_LOGIN_TYPE, json);
        this.serviceArea = (int) Fields.get(Fields.PERSON_JSON_SERVICE_AREA, json);
        this.alertOnTender = (boolean) Fields.get(Fields.PERSON_JSON_ALERT_ON_TENDER, json);
        this.alertOnChat = (boolean) Fields.get(Fields.PERSON_JSON_ALERT_ON_CHAT, json);
        this.deviceId = (String) Fields.get(Fields.PERSON_JSON_DEVICE_ID, json);
        this.deviceName = (String) Fields.get(Fields.PERSON_JSON_DEVICE_NAME, json);
        this.deviceType = (Character) Fields.get(Fields.PERSON_JSON_DEVICE_TYPE, json);

        try {
            JSONArray jsonArray = json.getJSONArray(Fields.PERSON_JSON_SERVICES);
            for (int i = 0; i < jsonArray.length(); i++) {
                Service service = new Service((JSONObject) jsonArray.get(i));
                if (this.serviceList == null) {
                    this.serviceList = new ArrayList<Service>();
                }
                this.serviceList.add(service);
                this.hasService = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (this.serviceList == null) {
                this.serviceList = new ArrayList<Service>();
            }
        }

        this.bio = (String) Fields.get(Fields.PERSON_JSON_BIO, json);
        try {
            SatisfactionSummary satisfactionSummary = new SatisfactionSummary();
            satisfactionSummary.update(json.getJSONObject(Fields.PERSON_JSON_SATISFACTION_SUM));
            this.satisfactionSummary = satisfactionSummary;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isAlertOnTender() {
        return alertOnTender;
    }

    public void setAlertOnTender(boolean alertOnTender) {
        this.alertOnTender = alertOnTender;
    }

    public boolean isAlertOnChat() {
        return alertOnChat;
    }

    public void setAlertOnChat(boolean alertOnChat) {
        this.alertOnChat = alertOnChat;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean isHasService() {
        return hasService;
    }

    public void setHasService(boolean hasService) {
        this.hasService = hasService;
        if(!hasService) {
            this.serviceList = null;
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Character getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Character deviceType) {
        this.deviceType = deviceType;
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
        if(serviceList != null) {
            this.hasService = true;
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }


    public void removeServiceFromList(Service service) {
        if (this.serviceList == null) {
            return;
        }
        if (this.serviceList.contains(service)) {
            this.serviceList.remove(service);
        }
    }

    public void addServiceToList(Service service) {
        if (this.serviceList == null) {
            this.serviceList = new ArrayList<>();
        }
        if (!this.serviceList.contains(service)) {
            this.serviceList.add(service);
        }
    }

    public SatisfactionSummary getSatisfactionSummary() {
        return satisfactionSummary;
    }

    public void setSatisfactionSummary(SatisfactionSummary satisfactionSummary) {
        this.satisfactionSummary = satisfactionSummary;
    }

    public void save() {
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", city=" + city +
                ", hasService=" + hasService +
                ", serviceArea=" + serviceArea +
                ", serviceList=" + serviceList +
                ", token='" + token + '\'' +
                ", bio='" + bio + '\'' +
                ", user='" + user + '\'' +
                ", photo='" + photo + '\'' +
                ", loginType=" + loginType +
                ", satisfactionSummary=" + satisfactionSummary +
                ", alertOnTender=" + alertOnTender +
                ", alertOnChat=" + alertOnChat +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }
}
