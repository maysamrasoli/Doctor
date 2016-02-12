package ir.medxhub.doctor.message;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.database.DBHelper;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;

import java.util.List;

/**
 * Created by Ali on 2/15/2015.
 */
public class Message extends ActionBarActivity {
    private DBHelper db;
    private MessageListAdapter adapter;
    private List<MessageItem> messages;
    private MySharedPreferences sp;
    private View notFoundMessage;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        db = new DBHelper(this);
        sp = new MySharedPreferences(this);

        // set notifications count to zero
        sp.sharedPreferences.edit().putInt("notification_count", 0).commit();

        // action bar
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });
        ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.messages));

        notFoundMessage = findViewById(R.id.not_found);

        // loading messages
        listView = (ListView) findViewById(android.R.id.list);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        loadMessages();

        // preview new received message if exists
        Intent intent = getIntent();
        if (intent.hasExtra("message_id")) {
            previewMessage(db.getMessage(intent.getIntExtra("message_id", 0)));
        }
    }

    private void loadMessages() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Globals.Notification_ID);
        messages = db.getMessages();
        if (messages.size() == 0) notFoundMessage.setVisibility(View.VISIBLE);
        adapter = new MessageListAdapter(this, messages);
        listView.setAdapter(adapter);
    }

    private void previewMessage(final MessageItem message) {
        final Dialog dialog = new Dialog(Message.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_message_preview);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dialog.findViewById(R.id.container).getLayoutParams();
        params.width = Integer.parseInt(sp.getFromPreferences("ScreenWidth")) - 40;

        ((TextView) dialog.findViewById(R.id.date)).setText(message.getDate());
        ((TextView) dialog.findViewById(R.id.title)).setText(message.getTitle());
        ((TextView) dialog.findViewById(R.id.content)).setText(message.getMessage());
        (dialog.findViewById(R.id.close_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.delete_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMessage(message);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteMessage(MessageItem message) {
        int result = db.deleteMessage(message.getId());
        if (result > 0) {
            for (MessageItem messageItem : messages) {
                if (messageItem.getId() == message.getId()) {
                    messages.remove(messageItem);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public class MessageListAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater;
        private List<MessageItem> listItems;

        public MessageListAdapter(Activity activity, List<MessageItem> listItems) {
            this.activity = activity;
            this.listItems = listItems;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int location) {
            return listItems.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.message_item, null);

            final MessageItem listItem = listItems.get(position);

            ((TextView) convertView.findViewById(R.id.title)).setText(Tools.limitString(listItem.getTitle(), 35));
            ((TextView) convertView.findViewById(R.id.date)).setText(listItem.getDate());

            convertView.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteMessage(listItem);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previewMessage(listItem);
                }
            });
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            if (getCount() > 0) {
                notFoundMessage.setVisibility(View.GONE);
            } else {
                notFoundMessage.setVisibility(View.VISIBLE);
            }
            super.notifyDataSetChanged();
        }
    }
}
