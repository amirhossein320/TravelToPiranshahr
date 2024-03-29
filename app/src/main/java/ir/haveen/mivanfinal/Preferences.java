package ir.haveen.mivanfinal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

import ir.haveen.mivanfinal.database.Database;
import ir.haveen.mivanfinal.net.Api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {

    private static final String LANG = "Lang";
    private Context context;
    private SharedPreferences preferences;
    private String FIRST_RUN = "FirstRun";
    private Database database;

    public Preferences(Context context) {
        this.context = context;
        setPreferences();// init preference
    }

    //get shared preference
    public SharedPreferences getPreferences() {
        return preferences;
    }

    //int sharedPreference
    public void setPreferences() {
        preferences = preferences = context.getSharedPreferences("settings", MODE_PRIVATE);
    }

    // get for shared preference editor
    public SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    // check first run of app
    public boolean IsFirstRun() {
        return preferences.getBoolean(FIRST_RUN, true);
    }

    //first run off
    public void setFirstRunOff() {
        getEditor().putBoolean(FIRST_RUN, false).apply();
    }

    //set local app language
    public void setLocaleLang(String language) {
        getEditor().putString(LANG, language).apply();
    }

    //change local of app
    public void setLocalToApp(AppCompatActivity activity) {

        Locale locale = new Locale(getLang());
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        if (getLang() != "en") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLayoutDirection(new Locale("fa"));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            config.setLocale(locale);
            activity.getBaseContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            activity.getBaseContext().getResources().
                    updateConfiguration(config,
                            activity.getBaseContext().getResources().getDisplayMetrics());
        }

    }

    //get local app language
    public String getLang() {
        return getPreferences().getString(LANG, "fa");
    }


    //init database
    private void setDatabase() {
        this.database = Room.databaseBuilder(context, Database.class, "data").allowMainThreadQueries().build();
    }

    //get database
    public Database getDatabase() {
        setDatabase();
        return database;
    }

    //return api
    public Api api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    //restart app method
    public void restartApp(Activity context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = context.getIntent();
            context.finish();
            context.startActivity(intent);
        } else {
            Intent mStartActivity = new Intent(context, context.getClass());
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
            context.finish();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
        }

    }
}
