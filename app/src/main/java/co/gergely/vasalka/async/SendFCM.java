package co.gergely.vasalka.async;

import android.os.AsyncTask;
import android.util.Log;
import co.gergely.vasalka.BuildConfig;
import co.gergely.vasalka.common.Constants;
import okhttp3.*;
import org.json.JSONObject;


public class SendFCM extends AsyncTask<String, Void, Void> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final String TAG = "SendFCM";
    private final boolean debug = true;


    @Override
    protected Void doInBackground(String... strings) {

        if (strings.length != 5) {
            return null;
        }

        String token = strings[0];
        String title = strings[1];
        String alertBody = strings[2];
        int messageType = Integer.parseInt(strings[3]);
        int messageId = Integer.parseInt(strings[4]);

        try {

            OkHttpClient client = new OkHttpClient();

            JSONObject json = new JSONObject();
            JSONObject notifJson = new JSONObject();
            JSONObject dataJson = new JSONObject();

            notifJson.put("body", alertBody);
            notifJson.put("title", title);
            notifJson.put("sound", "alert");

            dataJson.put(Constants.PUSH_MESSAGE_TYPE_KEY, messageType);
            dataJson.put(Constants.PUSH_MESSAGE_ID_KEY, messageId);

            json.put("notification", notifJson);
            json.put("to", token);
            json.put("data", dataJson);
            json.put("priority", "high");

            RequestBody body = RequestBody.create(JSON, json.toString());

            Request request = new Request.Builder()
                    .header("Authorization", "key=" + Constants.LEGACY_SERVER_KEY)
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();

            if (BuildConfig.DEBUG && debug) {
                String finalResponse = response.body().string();
                Log.d(TAG, "RESPONSE: " + finalResponse);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG && debug) {
                Log.d(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return null;


    }
}
