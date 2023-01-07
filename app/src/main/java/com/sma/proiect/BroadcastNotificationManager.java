package com.sma.proiect;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class BroadcastNotificationManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotification(context, intent);
    }

    public void createNotification(Context context,Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"bookRequestReminder");
        builder.setSmallIcon(R.drawable.bell);
        builder.setContentTitle("Don't forget to return your books to the librarian! :)");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}
