package com.madgag.android.dontblink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AngelService.class);
        i.putExtra("gone_non_interactive", true);
        context.startService(i);
    }

}
