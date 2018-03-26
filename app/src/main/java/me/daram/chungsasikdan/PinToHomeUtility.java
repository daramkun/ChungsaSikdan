package me.daram.chungsasikdan;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by daram on 2018-03-26.
 */

public final class PinToHomeUtility {
    public static boolean pinToHome (Context context, String id, Intent intent,
                                     CharSequence name, CharSequence shortName, int icon) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return pinToHomeRegacy (context, id, intent, name, shortName, icon);
        } else {
            return pinToHomeOreo ( context, id, intent, name, shortName, icon);
        }
    }

    private static boolean pinToHomeRegacy (Context context, String id, Intent intent,
                                            CharSequence name, CharSequence shortName, int icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!checkShortcutPermission(context))
                return false;

        Intent shortcutAddIntent = new Intent();
        shortcutAddIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutAddIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortName);
        shortcutAddIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, icon));
        shortcutAddIntent.putExtra("duplicate", false);
        shortcutAddIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        context.sendBroadcast(shortcutAddIntent);

        goToHomeScreen(context);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean pinToHomeOreo (Context context, String id, Intent intent,
                                          CharSequence name, CharSequence shortName, int icon) {
        if (!checkShortcutPermission(context))
            return false;

        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        if(!shortcutManager.isRequestPinShortcutSupported())
            return false;

        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, id)
                .setIntent(intent)
                .setShortLabel(shortName)
                .setLongLabel(name)
                .setIcon(Icon.createWithResource(context, icon))
                .build();
        Intent pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(shortcutInfo);

        PendingIntent successCallback = PendingIntent.getBroadcast(context, 0,
                pinnedShortcutCallbackIntent, 0);

        return shortcutManager.requestPinShortcut(shortcutInfo, successCallback.getIntentSender());
    }

    public static void goToHomeScreen (Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkShortcutPermission (Context context) {
        return context.checkSelfPermission(Manifest.permission.INSTALL_SHORTCUT) == PackageManager.PERMISSION_GRANTED;
    }
}
