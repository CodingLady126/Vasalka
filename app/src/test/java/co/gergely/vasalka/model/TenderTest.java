package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class TenderTest {
    Tender tender;

    @Before
    public void setup() {
        JSONObject jsonObject = new JSONObject();
        JSONObject personJson = new JSONObject();
        JSONObject addressJson = new JSONObject();
        JSONArray serviceJsonArray = new JSONArray();
        JSONArray imageJsonArray = new JSONArray();
        JSONObject serviceJson1 = new JSONObject();
        JSONObject serviceJson2 = new JSONObject();
        JSONObject imageJson1 = new JSONObject();
        JSONObject imageJson2 = new JSONObject();

        try{
            serviceJson1.put(Fields.SERVICE_JSON_ID, new Long(1));
            serviceJson1.put(Fields.SERVICE_JSON_NAME, "Vasalás");

            serviceJson2.put(Fields.SERVICE_JSON_ID, new Long(2));
            serviceJson2.put(Fields.SERVICE_JSON_NAME, "Mosás");

            serviceJsonArray.put(serviceJson1);
            serviceJsonArray.put(serviceJson2);

            personJson.put(Fields.PERSON_JSON_ID, new Long(1));
            personJson.put(Fields.PERSON_JSON_NAME, "Kresz Géza");
            personJson.put(Fields.PERSON_JSON_EMAIL, "a@a.hu");
            personJson.put(Fields.PERSON_JSON_TEL, "+36205550555");
            personJson.put(Fields.PERSON_JSON_HAS_SERVICE, true);
            personJson.put(Fields.PERSON_JSON_FCM_TOKEN, "This is a FCM token");
            personJson.put(Fields.PERSON_JSON_SERVICES, serviceJsonArray);

            imageJson1.put(Fields.IMAGE_JSON_ID, new Long(1));
            imageJson1.put(Fields.IMAGE_JSON_URL, "IMAGE 1 URL");

            imageJson2.put(Fields.IMAGE_JSON_ID, new Long(2));
            imageJson2.put(Fields.IMAGE_JSON_URL, "IMAGE 2 URL");

            imageJsonArray.put(imageJson1);
            imageJsonArray.put(imageJson2);

            jsonObject.put(Fields.TENDER_JSON_ID, new Long(1));
            jsonObject.put(Fields.TENDER_JSON_DATE_TIME, new Date());
            jsonObject.put(Fields.TENDER_JSON_DESCRIPTION, "Description");
            jsonObject.put(Fields.TENDER_JSON_SERVICE_AREA, 10);
            jsonObject.put(Fields.TENDER_JSON_ALIVE, true);
            jsonObject.put(Fields.TENDER_JSON_PERSON, personJson);
            jsonObject.put(Fields.TENDER_JSON_SERVICE, serviceJsonArray);
            jsonObject.put(Fields.TENDER_JSON_IMAGES, imageJsonArray);

        }catch (JSONException e) {e.printStackTrace();}
        tender = new Tender(jsonObject);
    }

    @Test
    public void constructorTest() {
        assertEquals(new Long(1), tender.getId());
        assertEquals("Description", tender.getDescription());
    }

    @Test
    public void serviceListIsNotNull() {
        assertNotNull(tender.getPerson().getServiceList());
    }

    @Test
    public void serviceListNotEmpty() {
        assertFalse(tender.getPerson().getServiceList().isEmpty());
    }

    @Test
    public void serviceListHasTwoMembers() {
        assertEquals(2, tender.getPerson().getServiceList().size());
    }

    @Test
    public void serviceListHasServices() {
        assertTrue(tender.getPerson().getServiceList().get(0) instanceof Long);
        assertTrue(tender.getPerson().getServiceList().get(1) instanceof Long);

        assertNotSame(tender.getPerson().getServiceList().get(0), tender.getPerson().getServiceList().get(1));
    }

    @Test
    public void imageListIsNotNull() {
        assertNotNull(tender.getImageList());
    }

    @Test
    public void imageListNotEmpty() {
        assertFalse(tender.getImageList().isEmpty());
    }

    @Test
    public void imageListHasTwoMembers() {
        assertEquals(2, tender.getImageList().size());
    }

    @Test
    public void imageListHasImages() {
        assertTrue(tender.getImageList().get(0) instanceof Image);
        assertTrue(tender.getImageList().get(1) instanceof Image);

        assertNotSame(tender.getImageList().get(0), tender.getImageList().get(1));
    }

    @Test
    public void testFirstImage() {
        assertEquals(new Long(1), tender.getImageList().get(0).getId());
        assertEquals("IMAGE 1 URL", tender.getImageList().get(0).getUrl());
    }

    @Test
    public void testSecondImage() {
        assertEquals(new Long(2), tender.getImageList().get(1).getId());
        assertEquals("IMAGE 2 URL", tender.getImageList().get(1).getUrl());
    }

}
