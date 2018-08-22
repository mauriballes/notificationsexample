package bo.com.bpdarlyn.notificationmessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver handleMessages = null;
    ArrayList<String> messagesData;
    ArrayAdapter<String> messageAdapter;

    private static final int REQUEST_EXTERNAL_STORAGE = 100;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        // Config Broadcast Messages
        handleMessages = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().containsKey(BroadcastUtil.MESSAGE)) {
                    Bundle newMessage = intent.getExtras().getBundle(BroadcastUtil.MESSAGE);
                    String title = newMessage.getString(BroadcastUtil._TITLE);
                    String body = newMessage.getString(BroadcastUtil._BODY);

                    messagesData.add("Title: " + title + "\nBody: " + body);
                    messageAdapter.notifyDataSetChanged();

                    Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver(handleMessages, new IntentFilter(BroadcastUtil.MESSAGE));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Build List from DB
        List<Message> messages = Message.listAll(Message.class);
        messagesData = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            messagesData.add("Title: " + messages.get(i).title + "\nBody: " + messages.get(i).body);
        }

        messageAdapter = new ArrayAdapter<String>(this, R.layout.message_adapter, R.id.txtitem, messagesData);
        ListView listview = findViewById(R.id.messages_view);
        listview.setAdapter(messageAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handleMessages != null) {
            unregisterReceiver(handleMessages);
            handleMessages = null;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }
}
