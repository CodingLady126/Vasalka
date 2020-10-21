package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {

    JSONObject json;
    Person person;

    @Before
    public void setup() {
        json = new JSONObject();
        JSONObject cityJson = new JSONObject();

        try {
            cityJson.put(Fields.CITY_JSON_ID, new Long(1));
            cityJson.put(Fields.CITY_JSON_NAME, "Vác");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(new Long(1));
            jsonArray.put(new Long(2));
            jsonArray.put(new Long(3));
            jsonArray.put(new Long(4));

            json.put(Fields.PERSON_JSON_ID, new Long(1));
            json.put(Fields.PERSON_JSON_EMAIL, "a@a.hu");
            json.put(Fields.PERSON_JSON_NAME, "Kresz Géza");
            json.put(Fields.PERSON_JSON_TEL, "+36205550555");
            json.put(Fields.PERSON_JSON_HAS_SERVICE, true);
            json.put(Fields.PERSON_JSON_FCM_TOKEN, "This is a FCM token");
            json.put(Fields.PERSON_JSON_SERVICES, jsonArray);
            json.put(Fields.PERSON_JSON_LOGIN_TYPE, 1);
            json.put(Fields.PERSON_JSON_SERVICE_AREA, 100);

        }catch (JSONException e) {
            e.printStackTrace();
        }
        person = new Person();
        person.update(json);
    }


    @Test
    public void constructorTest() {
        assertNotNull(person);
        assertEquals(new Long(1), person.getId());
        //assertEquals("Kresz Géza", person.getName());
        assertEquals("a@a.hu", person.getEmail());
        assertEquals("+36205550555", person.getTel());
        assertEquals("This is a FCM token", person.getToken());
        assertTrue(person.isHasService());
    }

    @Test
    public void serviceListIsNotNull() {
        assertNotNull(person.getServiceList());
    }

    @Test
    public void serviceListNotEmpty() {
        assertFalse(person.getServiceList().isEmpty());
    }

    @Test
    public void serviceListHasFourMembers() {
        assertEquals(4, person.getServiceList().size());
    }

    @Test
    public void serviceListHasServices() {
        assertTrue(person.getServiceList().get(0) instanceof Long);
        assertTrue(person.getServiceList().get(1) instanceof Long);
        assertTrue(person.getServiceList().get(2) instanceof Long);
        assertTrue(person.getServiceList().get(3) instanceof Long);

        assertNotSame(person.getServiceList().get(0), person.getServiceList().get(1));
        assertNotSame(person.getServiceList().get(1), person.getServiceList().get(2));
        assertNotSame(person.getServiceList().get(2), person.getServiceList().get(3));
    }

    @Test
    public void serviceAddTest() {
        person.addServiceToList(new Long(5));
        assertEquals(5, person.getServiceList().size());
        assertTrue(person.getServiceList().contains(new Long(5)));
    }

    @Test
    public void serviceRemoveTest() {
        person.removeServiceFromList(new Long(5));
        assertEquals(4, person.getServiceList().size());
        assertFalse(person.getServiceList().contains(new Long(5)));
    }


}
