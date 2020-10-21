package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class News implements Serializable {

    @SerializedName(Fields.NEWS_JSON_ID)
    private Long id;
    @SerializedName(Fields.NEWS_JSON_TITLE)
    private String title;
    @SerializedName(Fields.NEWS_JSON_INTRO)
    private String intro;
    @SerializedName(Fields.NEWS_JSON_PERSON)
    private Person person;
    @SerializedName(Fields.NEWS_JSON_CREATED_AT)
    private String createdAt;
    @SerializedName(Fields.NEWS_JSON_SECTIONS)
    private List<NewsSection> sections;
    @SerializedName(Fields.NEWS_JSON_LANGUAGE)
    private String lang;


    public News() {
    }


    public News(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {
        this.id = (Long) Fields.get(Fields.NEWS_JSON_ID, json);
        this.title = (String) Fields.get(Fields.NEWS_JSON_TITLE, json);
        this.intro = (String) Fields.get(Fields.NEWS_JSON_INTRO, json);
        this.createdAt = (String) Fields.get(Fields.NEWS_JSON_CREATED_AT, json);
        this.lang = (String) Fields.get(Fields.NEWS_JSON_LANGUAGE, json);

        try {
            this.person = new Person();
            person.update(json.getJSONObject(Fields.NEWS_JSON_PERSON));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.sections = new ArrayList<>();
            JSONArray array = json.getJSONArray(Fields.NEWS_JSON_SECTIONS);
            for (int x = 0; x < array.length(); x++) {
                NewsSection section = new NewsSection();
                section.update(array.getJSONObject(x));
                this.sections.add(section);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<NewsSection> getSections() {
        return sections;
    }

    public void setSections(List<NewsSection> sections) {
        this.sections = sections;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language='" + lang + '\'' +
                ", intro='" + intro + '\'' +
                ", person=" + person +
                ", createdAt='" + createdAt + '\'' +
                ", sections=" + sections +
                '}';
    }
}
