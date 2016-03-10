package com.example.chen41283922.wordmemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.Toast;

/**
 * Created by chen41283922 on 2016/1/10.
 */
public class PlayReceiver extends BroadcastReceiver
{
    private MyDB db ;
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new MyDB(context.getApplicationContext());
        db.open();

    try {
        Cursor c = db.getRandom();
        if (c != null && c.moveToFirst()) {
            NotificationManager noMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            long[] vibrate_effect =
                    {1700, 500, 150, 50, 150, 50};

            Notification notification = new Notification.Builder(context.getApplicationContext())
                    .setSmallIcon(R.drawable.ic_action_alarms)
                    .setContentTitle(c.getString(1))
                    .setContentText(c.getString(3) + c.getString(2))
                    .setVibrate(vibrate_effect)
                    .build(); // 建立通知

            notification.defaults = Notification.DEFAULT_LIGHTS;
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            noMgr.notify((int) (Math.random() * 99 + 1), notification); // 發送通知

            c.close();
            db.close();
        }
    }catch (NullPointerException err){
        Toast.makeText(context,""+ err, Toast.LENGTH_SHORT).show();
    }
    }

}

