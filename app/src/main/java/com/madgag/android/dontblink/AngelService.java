/*
 * Copyright (c) 2013-2014 Roberto Tyley
 *
 * This file is part of 'Don't Blink' - a Weeping Angel widget
 * for Android devices.
 *
 * Don't Blink is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Don't Blink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.android.dontblink;

import android.app.ActivityManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AngelService extends Service {

    private Set<String> homeActivityPackageNames;

    private Set<String> getHomeActivityPackageNames() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        Set<String> homeList = new HashSet<String>();
        for (ResolveInfo info : getPackageManager().queryIntentActivities(intent, 0)) {
            homeList.add(info.activityInfo.packageName);
        }
        Log.i("BLINK", "homeList=" + homeList);
        return homeList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        homeActivityPackageNames = getHomeActivityPackageNames();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        buildUpdate();

        return START_NOT_STICKY;
    }

    private int[] angelImages = new int[] {
        R.drawable.angel1,
        R.drawable.angel2,
        R.drawable.angel3,
        R.drawable.angel4,
        R.drawable.angel5,
        R.drawable.angel6
    };

    private int angelState = 0;

    private boolean homeWasVisibleOnPreviousCheck = true;

    private void buildUpdate() {
        boolean homeShowing = isHomeShowing();
        Log.d("BLINK", "homeShowing=" + homeShowing);
        if (!homeShowing && homeWasVisibleOnPreviousCheck) {
            angelState = (angelState + 1) % angelImages.length;
            Log.i("BLINK", "Advancing to angelState=" + angelState);

            RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);
            view.setImageViewResource(R.id.angelImageView, angelImages[angelState]);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
            AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, view);
        }
        homeWasVisibleOnPreviousCheck = homeShowing;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isHomeShowing() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningTaskInfo t : am.getRunningTasks(1)) {
            if (t != null && t.numRunning > 0) {
                ComponentName cn = t.baseActivity;
                if (cn == null)
                    continue;
                else if (homeActivityPackageNames.contains(cn.getPackageName())) return true;
            }
        }
        return false;
    }

}
