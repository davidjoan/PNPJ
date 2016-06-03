package pe.cayro.pnpj.v2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SamBootReceiver extends BroadcastReceiver {
    public SamBootReceiver() {
    }
    SamAlarmReceiver alarm = new SamAlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.i(SamBootReceiver.class.getSimpleName(), "BroadcastReceiver setup Alarm!");
            alarm.setAlarm(context);
        }
    }
}
