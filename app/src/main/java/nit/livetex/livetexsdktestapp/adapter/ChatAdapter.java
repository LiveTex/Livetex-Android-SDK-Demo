package nit.livetex.livetexsdktestapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
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

import livetex.sdk.models.TextMessage;
import nit.livetex.livetexsdktestapp.R;

/**
 * Created by sergey.so on 12.11.2014.
 *
 */
public class ChatAdapter extends BaseAdapter{

    private final Context mContext;
    private final ArrayList<TextMessage> messages;
    private final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public ChatAdapter(Context context){
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

    public void addMsg(TextMessage msg){
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void addAllMsgs(List<TextMessage> msgs){
        if (msgs == null) return;
        messages.addAll(msgs);
        notifyDataSetChanged();
    }

    public void removeAll(){
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_msgs, parent, false);
        TextMessage msg = (TextMessage) getItem(position);
        if (msg == null) return view;
        final TextView msgText = (TextView) view.findViewById(R.id.message_text);
        msgText.setText(msg.getText());
        if (!TextUtils.isEmpty(msg.getTimestamp()))
            bindTime(view, (long) (Double.parseDouble(msg.getTimestamp()) * 1000), position);
        alignMsg(view, msg.getSender() == null);
        return view;
    }

    private void bindTime(View view, long time, int pos) {
        Date date = new Date(time);
        ((TextView) view.findViewById(R.id.message_time)).setText(sdf_time.format(date));
        TextView header = (TextView) view.findViewById(R.id.message_date);
        final String curHeader = sdf_date.format(date);
        header.setText(curHeader);
        header.setVisibility(View.VISIBLE);
        if (pos > 0) {
            TextMessage oldMsg = messages.get(pos-1);
            Date prevDate = new Date((long) (Double.parseDouble(oldMsg.getTimestamp()) * 1000));
            String prevHeader = sdf_date.format(prevDate);
            if (curHeader.equals(prevHeader) || TextUtils.isEmpty(curHeader.trim())) {
                header.setVisibility(View.GONE);
            } else {
                header.setVisibility(View.VISIBLE);
            }
        }
    }

    private void alignMsg(View view, boolean isOutgoing) {
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
