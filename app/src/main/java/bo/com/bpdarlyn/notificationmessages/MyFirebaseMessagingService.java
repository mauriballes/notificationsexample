package bo.com.bpdarlyn.notificationmessages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
            // Handle message within 10 seconds
            handleNow(remoteMessage.getData());
//            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void handleNow(Map<String, String> data) {
        /*
         * Format Data
         * user: username
         * text: message
         * */

        String titleNotification = "Mensaje de " + data.get("user");
        String bodyNotification = data.get("text");

        Intent intentForOpenMainActivity = new Intent(this, MainActivity.class);
        intentForOpenMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntentForOpenMainActivity = PendingIntent.getActivity(
                this,
                0,
                intentForOpenMainActivity,
                PendingIntent.FLAG_ONE_SHOT);

        Message message = new Message(titleNotification, bodyNotification);
        message.save();

        BroadcastUtil.sendMessageBroadcast(this, titleNotification, bodyNotification);
        showNotification(pendingIntentForOpenMainActivity, titleNotification, bodyNotification, this);
    }

    public void showNotification(PendingIntent intent, String titleNotification, String bodyNotification, Context context) {
        final String CHANNEL_ID = "NOTIFICATION_CHANNEL_DARLYN";
        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Show Notification
        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[]{1000, 1000})
                .setContentTitle(titleNotification)
                .setContentText(bodyNotification)
                .setContentIntent(intent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotifyMgr != null;
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }

        assert mNotifyMgr != null;
        mNotifyMgr.notify(100, mBuilder.build());
    }
}
