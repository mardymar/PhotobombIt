package skript.com.photobombit.permissions;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marc on 1/14/2017.
 */

public class PreferencesUtil {
    private final static String path = "/data/data/skript.com.pokemontattoo/shared_prefs/prefs.xml";

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime){
        SharedPreferences sharedPreference = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }
    public static boolean isFirstTimeAskingPermission(Context context, String permission){
        return false;
        //return context.getSharedPreferences(context.getFilesDir().getPath()/prefs.xml, MODE_PRIVATE).getBoolean(permission, true);
    }


}
