package bo.com.bpdarlyn.notificationmessages;

import com.orm.SugarRecord;

public class Message extends SugarRecord {
    public String title;
    public String body;

    public Message(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
