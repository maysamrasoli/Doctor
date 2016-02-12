package ir.medxhub.doctor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import ir.medxhub.doctor.database.DBHelper;
import ir.medxhub.doctor.message.Message;
import ir.medxhub.doctor.message.MessageItem;
import ir.medxhub.doctor.message.ServerUtilities;
import ir.medxhub.doctor.util.Tools;
import org.json.JSONException;
import org.json.JSONObject;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Globals.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        DBHelper db = new DBHelper(context);
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("message"));
            MessageItem message = new MessageItem();
            message.setTitle(data.getString("title"));
            message.setMessage(data.getString("content"));
            message.setDate(data.getString("date"));
            message.setMessageType(data.getInt("type"));
            message.setRecivedAt(Tools.getDate(System.currentTimeMillis()));

            if (message.getMessageType() == 0) {
                // save system message in database and create notification
                long result = db.saveMessage(message);
                if (result > 0) {
                    message.setId((int) result);
                    createNotification(context, message);
                }
            } else {
                message.setId(0);
                createNotification(context, message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    private void createNotification(Context context, MessageItem message) {
        Intent notificationIntent;
        if (message.getMessageType() == 0) {
            notificationIntent = new Intent(this, Message.class);
            notificationIntent.putExtra("new_message", 1);
        } else {
            notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra("tab", message.getMessageType());
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationIntent.putExtra("message_id", message.getId());
        PendingIntent contentIntent = PendingIntent.getActivity(this, Globals.Notification_ID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker(getString(R.string.new_message))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSound(Uri.parse("android.resource://ir.medxhub.doctor/raw/notification"))
                .setContentTitle(message.getTitle())
                .setContentText(Tools.limitString(message.getMessage(), 35));
        Notification notification = builder.build();

        notificationManager.notify(Globals.Notification_ID, notification);
    }
}
