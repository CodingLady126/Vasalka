package co.gergely.vasalka;

import android.support.multidex.MultiDexApplication;
import co.gergely.vasalka.common.Session;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import io.fabric.sdk.android.Fabric;

public class VasalkaApp extends MultiDexApplication {
    private static Session session;

    public static Session getSession() {
        return session;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (session == null) session = new Session(this);
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
    }

    public void clearSession() {
        session.clear();
        session = new Session(this);
    }
}
