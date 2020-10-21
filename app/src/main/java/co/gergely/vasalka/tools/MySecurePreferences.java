package co.gergely.vasalka.tools;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import co.gergely.vasalka.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securepreferences.SecurePreferences;

import java.io.IOException;

/**
 * Created by gergelya on 2019. 01. 01..
 */

public class MySecurePreferences {

    private static final String TAG = "SecPref";
    private boolean debug = true;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ObjectMapper mapper;

    public MySecurePreferences(Application context) {

        this.context = context.getApplicationContext();
        sharedPreferences = new SecurePreferences(this.context);
        mapper = new ObjectMapper();

    }

    public MySecurePreferences(Application context, String password) {

        this.context = context.getApplicationContext();
        sharedPreferences = new SecurePreferences(this.context, password, "my_user_prefs.xml");
        mapper = new ObjectMapper();

/*
        String sekrt = getKey(context);

        sharedPreferences = new ObscuredPreferencesBuilder()
                .setApplication(context)
                .obfuscateValue(true)
                .obfuscateKey(true)
                .setSharePrefFileName(Constants.PREFS_NAME)
                .setSecret(sekrt)
                .createSharedPrefs();  */

    }

    /*
        private String getKey(Application myContext) {
            String sekrt = null;
            try {
                sekrt = KeyStoreKeyGenerator.get(myContext, myContext.getPackageName() + ".key").loadOrGenerateKeys();
            } catch (IllegalStateException e) {
                throw new RuntimeException("can't create key " + e.getLocalizedMessage());
            } catch (GeneralSecurityException e) {
                throw new RuntimeException("can't create  key");
            } catch (IOException e) {
                throw new RuntimeException("can't create key");
            }
            return sekrt;
        }
    */
    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public long getLong(String key, long value) {
        return sharedPreferences.getLong(key, value);
    }

    public String getString(String key, String value) {
        return sharedPreferences.getString(key, value);
    }

    public Person get(String key) throws IOException {
        String myValue = sharedPreferences.getString(key, "");
        if (myValue.isEmpty()) {
            return null;
        } else {
            return mapper.readValue(myValue, Person.class);
        }
    }

    public void put(String key, Object obj) throws JsonProcessingException {
        String value = mapper.writeValueAsString(obj);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear(String key) {
        sharedPreferences.edit().clear().apply();
    }
}
