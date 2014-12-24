package nit.livetex.livetexsdktestapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nit.livetex.livetexsdktestapp.R;

/**
 * Created by sergey.so on 12.11.2014.
 */
public class ChatAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<MessageModel> messages;
    private final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public ChatAdapter(Context context) {
        mContext = context;
        messages = new ArrayList<>();
    }

//    public ChatAdapter(Context context, List<TextMessage> messages){
//        this.messages = messages;
//        mContext = context;
//    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    public void addMsg(MessageModel msg) {
        if (msg == null) return;
        for (MessageModel model : messages) {
            if (model.id != null && msg.id != null && model.id.equals(msg.id)) {
                return;
            }
        }
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void addAllMsgs(List<MessageModel> msgs) {
        if (msgs == null) return;
        messages.addAll(msgs);
        notifyDataSetChanged();
    }

    public void removeAll() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setMsgChecked(String id) {
        if (id == null) return;
        for (MessageModel model : messages) {
            if (id.equals(model.id)){
                model.isChecked = true;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_msgs, parent, false);
        MessageModel msg = (MessageModel) getItem(position);
        if (msg == null) return view;
        final TextView msgText = (TextView) view.findViewById(R.id.message_text);
        if (msg.holdMessage == null) {
            msgText.setText(msg.text);
            alignMsg(view, msg.isOutgoing, msg.isChecked);
            view.findViewById(R.id.main_msg).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.main_msg).setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(msg.timestamp) || msg.holdMessage != null)
            bindTime(view, msg, position);
        return view;
    }

    private void bindTime(View view, MessageModel model, int pos) {
        Date date = new Date((long) (Double.parseDouble(model.timestamp) * 1000));
        final String curHeaderDate = sdf_date.format(date);
        ((TextView) view.findViewById(R.id.message_time)).setText(sdf_time.format(date));
        TextView header = (TextView) view.findViewById(R.id.message_date);
        boolean dateVisibility;
        if (pos > 0) {
            MessageModel oldMsg = messages.get(pos - 1);
            dateVisibility = !TextUtils.isEmpty(oldMsg.timestamp)
                    && !(curHeaderDate.equals(formatTimestamp(oldMsg.timestamp))
                    || TextUtils.isEmpty(curHeaderDate.trim()));
        } else {
            dateVisibility = true;
        }
        StringBuilder headerText = new StringBuilder();
        if (dateVisibility) {
            headerText.append(curHeaderDate);
        }
        if (!TextUtils.isEmpty(model.holdMessage)) {
            headerText.append("\n").append(model.holdMessage);
        }
        if (headerText.length() > 0) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        } else {
            header.setVisibility(View.GONE);
        }
    }

    private String formatTimestamp(String timestamp) {
        Date date = new Date((long) (Double.parseDouble(timestamp) * 1000));
        return sdf_date.format(date);
    }

    private void alignMsg(View view, boolean isOutgoing, boolean isChecked) {
        View mainLayout = view.findViewById(R.id.message_type);
        View time = view.findViewById(R.id.message_time);
        TextView text = (TextView) view.findViewById(R.id.message_text);
        RelativeLayout.LayoutParams mainParams = (RelativeLayout.LayoutParams) mainLayout.getLayoutParams();
        RelativeLayout.LayoutParams timeParams = (RelativeLayout.LayoutParams) time.getLayoutParams();
        LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) text.getLayoutParams();

        int ten = (int) mContext.getResources().getDimension(R.dimen.margin);
        int thirteen = (int) mContext.getResources().getDimension(R.dimen.thirteen);
        if (isOutgoing) {
            mainLayout.setBackgroundResource(R.drawable.message_left);
            mainLayout.setPadding(ten, ten, ten, ten);
            mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            mainParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            timeParams.addRule(RelativeLayout.RIGHT_OF, 0);
            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.message_type);
            mainLayout.setLayoutParams(mainParams);
            time.setLayoutParams(timeParams);
            textParams.leftMargin = 0;
            textParams.rightMargin = (int) mContext.getResources().getDimension(R.dimen.margin);
            text.setTextColor(Color.WHITE);
            text.setLinkTextColor(mContext.getResources().getColor(R.color.light_silver));
            if (isChecked){
                view.findViewById(R.id.message_mark).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.message_mark).setVisibility(View.GONE);
            }
        } else {
            mainLayout.setBackgroundResource(R.drawable.message_right);
            mainLayout.setPadding(ten, thirteen, ten, thirteen);
            mainParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            timeParams.addRule(RelativeLayout.LEFT_OF, 0);
            timeParams.addRule(RelativeLayout.RIGHT_OF, R.id.message_type);
            mainLayout.setLayoutParams(mainParams);
            time.setLayoutParams(timeParams);
            textParams.rightMargin = 0;
            textParams.leftMargin = (int) mContext.getResources().getDimension(R.dimen.margin);
            text.setTextColor(Color.BLACK);
            text.setLinkTextColor(mContext.getResources().getColor(R.color.link_registartion));
        }
    }
}
