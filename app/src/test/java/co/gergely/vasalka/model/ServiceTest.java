package co.gergely.vasalka.model;


import co.gergely.vasalka.common.Fields;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServiceTest {

    JSONObject json;


    @Before
    public void setup() {
        json = new JSONObject();

        try {
            json.put(Fields.SERVICE_JSON_ID, new Long(1));
            json.put(Fields.SERVICE_JSON_NAME, "Vasalás");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ConstructorTest() {
        Service service = new Service(json);
        assertEquals(new Long(1), service.getId());
        assertEquals("Vasalás", service.getName());
    }
}
