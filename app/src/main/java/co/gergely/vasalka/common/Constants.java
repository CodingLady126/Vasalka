package co.gergely.vasalka.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String BASE_URL = "https://api.vasalka.personal.hu";
    public static final String LEGACY_SERVER_KEY = "AIzaSyBrqvLN8qf3-x9sK-Wk4YlrtP-Px6Xlngg";

    public static final int GOOGLE_SIGN_IN = 10001;
    public static final int PHOTO_FOR_PERSON_GALERY = 10002;
    public static final int PHOTO_FOR_PERSON_CAMERA = 10003;
    public static final int PHOTO_FOR_TENDER_GALERY = 10004;
    public static final int PHOTO_FOR_TENDER_CAMERA = 10005;
    public static final int PHOTO_FOR_CHAT_GALERY = 10006;
    public static final int PHOTO_FOR_CHAT_CAMERA = 10007;

    public static final int PHOTO_FOR_PERSON = 100;
    public static final int PHOTO_FOR_TENDER = 101;
    public static final int PHOTO_FOR_CHAT = 102;

    public static final int REQUEST_CAMERA_STATE = 20001;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_STATE = 20002;

    public static final int REQUEST_SEARCH_PERSON = 30001;

    public static final String PUSH_MESSAGE_TYPE_KEY = "MESSAGE_KEY";
    public static final String PUSH_MESSAGE_ID_KEY = "ID";

    public static final int PUSH_MESSAGE_TYPE_CHAT = 1;
    public static final int PUSH_MESSAGE_TYPE_TENDER = 2;
    public static final int PUSH_MESSAGE_TYPE_NEWS = 3;
    public static final int PUSH_MESSAGE_TYPE_TENDER_APPLY = 4;

    public static final String SEARCH_RESULT_PERSON_KEY = "search_result_person_key";
    public static final String NEWS_ID_KEY = "news_id_key";
    public static final String TENDER_ID_KEY = "tender_id_key";
    public static final String PERSON_ID_KEY = "person_id_key";
    public static final String MY_PERSON_KEY = "my_person";
    public static final String PASSWORD_KEY = "my_password";
    public static final String EMAIL_KEY = "my_email";
    public static final String JWT_TOKEN_KEY = "my_jwt_token";
    public static final String CITY_LIST_KEY = "city_list";
    public static final String SERVICE_LIST_KEY = "service_list";
    public static final String CHAT_ROOM_LIST_KEY = "chat_room_list";
    public static final String BOOKMARK_LIST_KEY = "bookmark_list";
    public static final String SIGN_IN_TYPE_KEY = "login_type";
    public static final String NEWS_LIST_KEY = "news_list";
    public static final String PROVIDER_LIST_KEY = "provider_list";
    public static final String SATISFACTION_LIST_KEY = "satisfaction_list";
    public static final String TENDER_LIST_KEY = "tender_list";

    public static final int SIGN_TYPE_GOOGLE = 1;
    public static final int SIGN_TYPE_FACEBOOK = 2;
    public static final int SIGN_TYPE_EMAIL = 3;
    public static final int SIGN_TYPE_SMS = 4;

    public static final String SIGN_TYPE_GOOGLE_STRING = "Google";
    public static final String SIGN_TYPE_FACEBOOK_STRING = "Facebook";
    public static final String SIGN_TYPE_EMAIL_STRING = "Email";
    public static final String SIGN_TYPE_SMS_STRING = "SMS";



    public static final String SIGN_TYPE_KEY = "sign_type";
    public static final String SMS_LOGIN_IN_PROGRESS_KEY = "sms_in_progress";
    public static final String SMS_VERIFICATION_ID_KEY = "sms_verification_id";
    public static final String SMS_VERIFICATION_TIME_KEY = "sms_verification_time";
    public static final String CHAT_ROOM_SOUND_EFFECT_KEY = "chat_room_sound_effect";
    public static final String CHAT_ROOM_EXTRAS_KEY = "chat_room";

    public static final List<Integer> AREA_ARRAY = new ArrayList<Integer>(Arrays.asList(new Integer[]{10, 25, 50, 100, 200, 300, 500}));
    public static final Long SERVICE_IRONING_ID = new Long(1);
    public static final Long SERVICE_WASHING_ID = new Long(2);
    public static final Long SERVICE_CLEANING_ID = new Long(3);
    public static final Long SERVICE_GARDENING_ID = new Long(4);

    public static final String FB_DB_CHILD_CHAT = "chat";
    public static final String FB_DB_CHILD_MESSAGES = "messages";


    public static final int BOOKMARK_OBJECT_TYPE_PERSON_ID = 6;
    public static final int BOOKMARK_OBJECT_TYPE_CHAT_ID = 12;
    public static final int BOOKMARK_OBJECT_TYPE_TENDER_ID = 9;
    public static final String BOOKMARK_OBJECT_TYPE_PERSON_NAME = "PERSON";
    public static final String BOOKMARK_OBJECT_TYPE_CHAT_NAME = "CHAT";
    public static final String BOOKMARK_OBJECT_TYPE_TENDER_NAME = "TENDER";




}
