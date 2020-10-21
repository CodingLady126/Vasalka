package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class NewsSection implements Serializable, Comparable<NewsSection> {

    @SerializedName(Fields.NEWS_SECTION_JSON_TITLE)
    private String title;
    @SerializedName(Fields.NEWS_SECTION_JSON_CONTENT)
    private String content;
    @SerializedName(Fields.NEWS_SECTION_JSON_ORDER)
    private int order;

    public NewsSection() {
    }

    public NewsSection(String title, String content, int order) {
        this.title = title;
        this.content = content;
        this.order = order;
    }

    public NewsSection(JSONObject json) {
        update(json);
    }

    public void update(JSONObject json) {
        this.title = (String) Fields.get(Fields.NEWS_SECTION_JSON_TITLE, json);
        this.content = (String) Fields.get(Fields.NEWS_SECTION_JSON_CONTENT, json);
        this.order = (int) Fields.get(Fields.NEWS_SECTION_JSON_ORDER, json);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int compareTo(NewsSection o) {
        return this.order - o.order;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", order=" + order +
                '}';
    }


}
