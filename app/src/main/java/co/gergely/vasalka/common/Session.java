package co.gergely.vasalka.common;

import android.util.Log;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.VasalkaApp;
import co.gergely.vasalka.model.*;
import co.gergely.vasalka.tools.MySecurePreferences;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Session implements Observer {

    private MySecurePreferences securePreferences;
    private VasalkaApp app;
    private List<Service> serviceList;
    private List<City> cityList;
    private List<ChatRoom> chatRoomList;
    private List<Bookmark> bookmarkList;
    private List<News> newsList;
    private List<SearchPerson> providerList;
    private List<Satisfaction> satisfactionList;
    private List<Tender> tenderList;
    private Person myPerson;
    private boolean debug = true;
    private String jwtToken = null;

    public Session(VasalkaApp application) {
        this.app = application;
        securePreferences = new MySecurePreferences(application);
        Person person = null;
        String lCityList = null;
        String lServiceList = null;
        String lChatRoomList = null;
        String lBookmarkList = null;
        String lNewsList = null;
        String lProviderList = null;
        String lSatisfactionList = null;
        String lTenderList = null;


        try {
            person = securePreferences.get(Constants.MY_PERSON_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (person == null) {
            this.myPerson = new Person();
        } else {
            this.myPerson = person;
        }
        myPerson.addObserver(this);


        Gson gson = new Gson();
        lCityList = securePreferences.getString(Constants.CITY_LIST_KEY, "");
        if (!lCityList.isEmpty()) {
            City[] text = gson.fromJson(lCityList, City[].class);
            this.cityList = Arrays.asList(text);
        } else {
            this.cityList = new ArrayList<>();
        }

        gson = new Gson();
        lServiceList = securePreferences.getString(Constants.SERVICE_LIST_KEY, "");
        if (!lServiceList.isEmpty()) {
            Service[] text = gson.fromJson(lServiceList, Service[].class);
            this.serviceList = Arrays.asList(text);
        } else {
            this.serviceList = new ArrayList<>();
        }

        gson = new Gson();
        lChatRoomList = securePreferences.getString(Constants.CHAT_ROOM_LIST_KEY, "");
        if (!lChatRoomList.isEmpty()) {
            ChatRoom[] text = gson.fromJson(lChatRoomList, ChatRoom[].class);
            this.chatRoomList = Arrays.asList(text);
        } else {
            this.chatRoomList = new ArrayList<>();
        }

        gson = new Gson();
        lBookmarkList = securePreferences.getString(Constants.BOOKMARK_LIST_KEY, "");
        if (!lBookmarkList.isEmpty()) {
            Bookmark[] text = gson.fromJson(lBookmarkList, Bookmark[].class);
            this.bookmarkList = Arrays.asList(text);
        } else {
            this.bookmarkList = new ArrayList<>();
        }

        gson = new Gson();
        lNewsList = securePreferences.getString(Constants.NEWS_LIST_KEY, "");
        if (!lNewsList.isEmpty()) {
            News[] text = gson.fromJson(lNewsList, News[].class);
            this.newsList = Arrays.asList(text);
        } else {
            this.newsList = new ArrayList<>();
        }

        gson = new Gson();
        lProviderList = securePreferences.getString(Constants.PROVIDER_LIST_KEY, "");
        if (!lProviderList.isEmpty()) {
            SearchPerson[] text = gson.fromJson(lProviderList, SearchPerson[].class);
            this.providerList = Arrays.asList(text);
        } else {
            this.providerList = new ArrayList<>();
        }

        gson = new Gson();
        lSatisfactionList = securePreferences.getString(Constants.SATISFACTION_LIST_KEY, "");
        if (!lSatisfactionList.isEmpty()) {
            Satisfaction[] text = gson.fromJson(lSatisfactionList, Satisfaction[].class);
            this.satisfactionList = Arrays.asList(text);
        } else {
            this.satisfactionList = new ArrayList<>();
        }

        gson = new Gson();
        lTenderList = securePreferences.getString(Constants.TENDER_LIST_KEY, "");
        if (!lTenderList.isEmpty()) {
            Tender[] text = gson.fromJson(lTenderList, Tender[].class);
            this.tenderList = Arrays.asList(text);
        } else {
            this.tenderList = new ArrayList<>();
        }

    }

    public String getJwtToken() {
        return this.jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        securePreferences.putString(Constants.JWT_TOKEN_KEY, jwtToken);
    }

    public Person getMyPerson() {
        return this.myPerson;
    }

    public List<City> getCityList() {
        return this.cityList;
    }

    public List<Service> getServiceList() {
        return this.serviceList;
    }

    public List<ChatRoom> getChatRoomList() {
        return this.chatRoomList;
    }

    public List<Bookmark> getBookmarkList() {
        return this.bookmarkList;
    }

    public List<News> getNewsList() {
        return this.newsList;
    }

    public List<SearchPerson> getProviderList() {
        return this.providerList;
    }

    public List<Tender> getTenderList() {
        return this.tenderList;
    }

    public void setProviderList(List<SearchPerson> list) {
        this.providerList = list;
        Gson gson = new Gson();
        String jsonText = gson.toJson(list);
        securePreferences.putString(Constants.PROVIDER_LIST_KEY, jsonText);
    }

    public void setMyPerson(Person myPerson) {
        this.myPerson = myPerson;
        update(myPerson, null);
    }

    public List<Satisfaction> getSatisfactionList() {
        return this.satisfactionList;
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
        Gson gson = new Gson();
        String jsonText = gson.toJson(this.bookmarkList);
        securePreferences.putString(Constants.BOOKMARK_LIST_KEY, jsonText);
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        Gson gson = new Gson();
        String jsonText = gson.toJson(this.newsList);
        securePreferences.putString(Constants.NEWS_LIST_KEY, jsonText);
    }

    public void setTenderList(List<Tender> list) {
        this.tenderList = list;
        Gson gson = new Gson();
        String jsonText = gson.toJson(this.tenderList);
        securePreferences.putString(Constants.TENDER_LIST_KEY, jsonText);
    }

    public void setChatRoomList(List<ChatRoom> chatRoomList) {
        this.chatRoomList = chatRoomList;
        Gson gson = new Gson();
        String jsonText = gson.toJson(chatRoomList);
        securePreferences.putString(Constants.CHAT_ROOM_LIST_KEY, jsonText);
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
        Gson gson = new Gson();
        String jsonText = gson.toJson(serviceList);
        securePreferences.putString(Constants.SERVICE_LIST_KEY, jsonText);
    }

    public void setCityListList(List<City> cityListList) {
        this.cityList = cityListList;
        Gson gson = new Gson();
        String jsonText = gson.toJson(cityListList);
        securePreferences.putString(Constants.CITY_LIST_KEY, jsonText);

    }

    public void setSatisfactionList(List<Satisfaction> lista) {
        this.satisfactionList = lista;
        Gson gson = new Gson();
        String jsonText = gson.toJson(this.satisfactionList);
        securePreferences.putString(Constants.SATISFACTION_LIST_KEY, jsonText);
    }

    public void clear() {
        app.clearSession();
        securePreferences.clear();
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            securePreferences.put(Constants.MY_PERSON_KEY, (Serializable) o);
            if (BuildConfig.DEBUG && debug) {
                Log.d("SESSION", "update: " + (Serializable) o);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG && debug) {
                Log.e("SESSION", "update: " + e.getLocalizedMessage());
            }
        }
    }
}
