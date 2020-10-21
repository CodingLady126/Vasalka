package co.gergely.vasalka.common;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class Fields {

    public static Object get(String field, JSONObject json) {
        try{
            return json.get(field);
        }catch (JSONException e) {
            Log.d("FIELDS", "get: " + e.getLocalizedMessage());
        }
        return null;
    }

    public static final String STATE_JSON_NAME = "name";
    public static final String STATE_JSON_ID = "id";
    public static final String STATE_JSON_COUNTRY = "country";

    public static final String CITY_JSON_ID = "id";
    public static final String CITY_JSON_NAME = "name";
    public static final String CITY_JSON_LAT = "lat";
    public static final String CITY_JSON_LNG = "lng";



    public static final String SERVICE_JSON_ID = "id";
    public static final String SERVICE_JSON_NAME = "name";

    public static final String PERSON_JSON_ID = "id";
    public static final String PERSON_JSON_NAME = "name";
    public static final String PERSON_JSON_EMAIL = "email";
    public static final String PERSON_JSON_TEL = "tel";
    public static final String PERSON_JSON_CITY = "city";
    public static final String PERSON_JSON_USER = "fb_userid";
    public static final String PERSON_JSON_PHOTO = "photo";
    public static final String PERSON_JSON_HAS_SERVICE = "is_service";
    public static final String PERSON_JSON_SERVICE_AREA = "service_area";
    public static final String PERSON_JSON_SERVICES = "service_list";
    public static final String PERSON_JSON_FCM_TOKEN = "fcm_token";
    public static final String PERSON_JSON_BIO = "bio";
    public static final String PERSON_JSON_LOGIN_TYPE = "login_type";
    public static final String PERSON_JSON_SATISFACTION_SUM = "satisfaction_sum";
    public static final String PERSON_JSON_ALERT_ON_TENDER = "alert_on_tender";
    public static final String PERSON_JSON_ALERT_ON_CHAT = "alert_on_chat";
    public static final String PERSON_JSON_DEVICE_ID = "device_id";
    public static final String PERSON_JSON_DEVICE_NAME = "device_name";
    public static final String PERSON_JSON_DEVICE_TYPE = "device_type";


    public static final String CHATROOM_JSON_ID = "id";
    public static final String CHATROOM_JSON_FB_ID = "fb_id";
    public static final String CHATROOM_JSON_PERSON_ONE = "user1";
    public static final String CHATROOM_JSON_PERSON_ONE_ALIVE = "user1_alive";
    public static final String CHATROOM_JSON_PERSON_TWO = "user2";
    public static final String CHATROOM_JSON_PERSON_TWO_ALIVE = "user2_alive";
    public static final String CHATROOM_JSON_MESSAGES = "messages";


    public static final String IMAGE_JSON_ID = "id";
    public static final String IMAGE_JSON_URL = "url";

    public static final String TENDER_JSON_ID = "id";
    public static final String TENDER_JSON_CREATED_TIME = "created_at";
    public static final String TENDER_JSON_UPDATED_TIME = "updated_at";
    public static final String TENDER_JSON_DESCRIPTION = "description";
    public static final String TENDER_JSON_SERVICE_AREA = "service_area";
    public static final String TENDER_JSON_ALIVE = "alive";
    public static final String TENDER_JSON_SERVICE_LIST = "service_list";
    public static final String TENDER_JSON_PERSON = "person";
    public static final String TENDER_JSON_CITY = "city";
    public static final String TENDER_JSON_IMAGE_1 = "image1";
    public static final String TENDER_JSON_IMAGE_2 = "image2";
    public static final String TENDER_JSON_IMAGE_3 = "image3";


    public static final String TENDER_ALERT_JSON_ID = "tender_id";
    public static final String TENDER_ALERT_JSON_ALERTED = "alerted";
    public static final String TENDER_ALERT_JSON_SAW = "saw";
    public static final String TENDER_ALERT_JSON_APPLIED = "applied";


    public static final String SATISFACTION_JSON_ID = "id";
    public static final String SATISFACTION_JSON_DATE_TIME = "created_at";
    public static final String SATISFACTION_JSON_RATE = "rate";
    public static final String SATISFACTION_JSON_DESC = "desc";
    public static final String SATISFACTION_JSON_SRC_PERSON = "src_person";
    public static final String SATISFACTION_JSON_DST_PERSON = "dst_person";
    public static final String SATISFACTION_JSON_SERVICE = "service_id";

    public static final String SEARCH_JSON_ID = "id";
    public static final String SEARCH_JSON_DATE_TIME = "created_at";
    public static final String SEARCH_JSON_CITY = "city";
    public static final String SEARCH_JSON_SERVICE_AREA = "service_area";
    public static final String SEARCH_JSON_SERVICE = "service_list";
    public static final String SEARCH_JSON_PERSON_ID = "person_id";
    public static final String SEARCH_JSON_PERSON_DISTANCE = "distance";

    public static final String SATISFACTION_SUM_JSON_AVG = "avg";
    public static final String SATISFACTION_SUM_JSON_COUNT = "count";


    public static final String BOOKMARK_OBJECT_TYPE_JSON_ID = "id";
    public static final String BOOKMARK_OBJECT_TYPE_JSON_NAME = "name";


    public static final String BOOKMARK_JSON_ID = "id";
    public static final String BOOKMARK_JSON_OBJECT_TYPE = "object_type";
    public static final String BOOKMARK_JSON_OBJECT_TYPE_ID = "object_type_id";
    public static final String BOOKMARK_JSON_OBJECT_PERSON = "object_person";
    public static final String BOOKMARK_JSON_OBJECT_TENDER = "object_tender";
    public static final String BOOKMARK_JSON_OBJECT_CHAT = "object_chat";
    public static final String BOOKMARK_JSON_OBJECT_ID = "object_id";
    public static final String BOOKMARK_JSON_CREATED_AT = "created_at";


    public static final String NEWS_SECTION_JSON_TITLE = "title";
    public static final String NEWS_SECTION_JSON_CONTENT = "content";
    public static final String NEWS_SECTION_JSON_ORDER = "order";

    public static final String NEWS_JSON_ID = "id";
    public static final String NEWS_JSON_TITLE = "title";
    public static final String NEWS_JSON_INTRO = "intro";
    public static final String NEWS_JSON_PERSON = "person";
    public static final String NEWS_JSON_SECTIONS = "sections";
    public static final String NEWS_JSON_CREATED_AT = "created_at";
    public static final String NEWS_JSON_LANGUAGE = "language";







}