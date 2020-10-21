package co.gergely.vasalka.model;

import co.gergely.vasalka.common.Fields;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CityTest {

    JSONObject json;

    @Before
    public void setup() {
        json = new JSONObject();
        JSONObject countryJson = new JSONObject();
        JSONObject stateJson = new JSONObject();

        try {


            stateJson.put(Fields.STATE_JSON_ID, new Long(1));
            stateJson.put(Fields.STATE_JSON_NAME, "Pest");
            stateJson.put(Fields.STATE_JSON_COUNTRY, countryJson);

            json.put(Fields.CITY_JSON_ID, new Long(1));
            json.put(Fields.CITY_JSON_NAME, "Vác");


        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void ConstructorTest() {
        City city = new City(json);

        assertEquals("Vác", city.getName());
        assertEquals(new Long(1), city.getId());


    }

}
