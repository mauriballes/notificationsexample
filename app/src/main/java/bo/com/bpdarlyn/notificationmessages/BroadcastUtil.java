package bo.com.bpdarlyn.notificationmessages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BroadcastUtil {

    public static final String MESSAGE = "MESSAGE";
    public static final String _TITLE = "_TITLE";
    public static final String _BODY = "_BODY";

    public static void sendMessageBroadcast(Context context, String title, String body) {
        Intent intent = new Intent(MESSAGE);

        Bundle bundleObject = new Bundle();
        bundleObject.putString(_TITLE, title);
        bundleObject.putString(_BODY, body);

        intent.putExtra(MESSAGE, bundleObject);
        context.sendBroadcast(intent);
    }
}
