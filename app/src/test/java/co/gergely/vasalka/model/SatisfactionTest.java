package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class SatisfactionTest {

    Satisfaction satisfaction;

    @Before
    public void setup() {
        JSONObject jsonObject = new JSONObject();
        JSONObject personJson1 = new JSONObject();
        JSONObject personJson2 = new JSONObject();

        JSONObject serviceJson1 = new JSONObject();
        JSONObject serviceJson2 = new JSONObject();
        JSONObject serviceJson3 = new JSONObject();
        JSONObject serviceJson4 = new JSONObject();

        JSONObject addressJson = new JSONObject();
        JSONObject countryJson = new JSONObject();
        JSONObject stateJson = new JSONObject();
        JSONObject cityJson = new JSONObject();


        try {


            stateJson.put(Fields.STATE_JSON_ID, new Long(1));
            stateJson.put(Fields.STATE_JSON_NAME, "Pest");
            stateJson.put(Fields.STATE_JSON_COUNTRY, countryJson);

            cityJson.put(Fields.CITY_JSON_ID, new Long(1));
            cityJson.put(Fields.CITY_JSON_NAME, "Vác");



            serviceJson1.put(Fields.SERVICE_JSON_ID, new Long(1));
            serviceJson1.put(Fields.SERVICE_JSON_NAME, "Vasalás");

            serviceJson2.put(Fields.SERVICE_JSON_ID, new Long(2));
            serviceJson2.put(Fields.SERVICE_JSON_NAME, "Mosás");

            serviceJson3.put(Fields.SERVICE_JSON_ID, new Long(3));
            serviceJson3.put(Fields.SERVICE_JSON_NAME, "Takarítás");

            serviceJson4.put(Fields.SERVICE_JSON_ID, new Long(4));
            serviceJson4.put(Fields.SERVICE_JSON_NAME, "Kertrendezés");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(serviceJson1);
            jsonArray.put(serviceJson2);
            jsonArray.put(serviceJson3);
            jsonArray.put(serviceJson4);

            personJson1.put(Fields.PERSON_JSON_ID, new Long(1));
            personJson1.put(Fields.PERSON_JSON_NAME, "Kresz Géza");
            personJson1.put(Fields.PERSON_JSON_EMAIL, "a@a.hu");
            personJson1.put(Fields.PERSON_JSON_TEL, "+36205550555");
            personJson1.put(Fields.PERSON_JSON_HAS_SERVICE, true);
            personJson1.put(Fields.PERSON_JSON_FCM_TOKEN, "This is a FCM token");
            personJson1.put(Fields.PERSON_JSON_SERVICES, jsonArray);

            personJson2.put(Fields.PERSON_JSON_ID, new Long(2));
            personJson2.put(Fields.PERSON_JSON_NAME, "Alig Átor");
            personJson2.put(Fields.PERSON_JSON_EMAIL, "b@b.hu");
            personJson2.put(Fields.PERSON_JSON_TEL, "+36705552555");
            personJson2.put(Fields.PERSON_JSON_HAS_SERVICE, false);
            personJson2.put(Fields.PERSON_JSON_FCM_TOKEN, "This is a FCM token2");


            jsonObject.put(Fields.SATISFACTION_JSON_ID, new Long(1));
            jsonObject.put(Fields.SATISFACTION_JSON_DATE_TIME, new Date());
            jsonObject.put(Fields.SATISFACTION_JSON_DESC, "Description");
            jsonObject.put(Fields.SATISFACTION_JSON_RATE, 10);
            jsonObject.put(Fields.SATISFACTION_JSON_DST_PERSON, personJson1);
            jsonObject.put(Fields.SATISFACTION_JSON_SRC_PERSON, personJson2);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        satisfaction = new Satisfaction(jsonObject);
    }

    @Test
    public void constructorTest() {
        assertEquals(new Long(1), satisfaction.getId());
        assertEquals("Description", satisfaction.getDescription());
        assertEquals(10, satisfaction.getRate());
    }
}
