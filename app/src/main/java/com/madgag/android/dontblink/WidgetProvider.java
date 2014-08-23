package com.madgag.android.dontblink;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import static android.app.AlarmManager.ELAPSED_REALTIME;

public class WidgetProvider extends AppWidgetProvider {

    private PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(context, MyService.class);

        if (service == null) {
            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        m.setInexactRepeating(ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000, service);
        Log.i("BLINK", "Alarm set...");
    }

    @Override
    public void onDisabled(Context context) {
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        m.cancel(service);
    }
}