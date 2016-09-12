package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.MessageModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dev on 02.06.16.
 */
public class ChatArrayAdapter extends ArrayAdapter<MessageModel> {

    private final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());


    private enum BubbleState {
        OPERATOR, VISITOR, CLOSE, OPEN, FILE_OPERATOR, FILE_VISITOR
    }

    public ChatArrayAdapter(Context context) {
        super(context, R.layout.item_chat);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_chat, parent, false);
        MessageModel message = getItem(position);

        if(message.isVisitorMessage()) {
            setBubbleState(view,message, BubbleState.VISITOR);
        } else {
            setBubbleState(view, message, BubbleState.OPERATOR);
        }

        return view;
    }


    public void setData(MessageModel message) {
        add(message);
        notifyDataSetChanged();
    }

    public void setData(List<MessageModel> messages) {
        clear();
        addAll(messages);
        notifyDataSetChanged();
    }

    private boolean isImage(String filePath) {
        String[] parts = filePath.split("\\.");
        return (parts.length > 1 && ("png".equals(parts[parts.length-1]) || "jpeg".equals(parts[parts.length-1]) || "jpg".equals(parts[parts.length-1])));
    }

    private static class ViewHolder {
        LinearLayout llLeft;
        LinearLayout llRight;
        LinearLayout llRightBaloon;
        LinearLayout llLeftBaloon;
        TextView tvDialogState;
        TextView tvMessageLeft;
        TextView tvMessageRight;
        TextView tvTimerLeft;
        TextView tvTimerRight;
        ImageView ivScreenshot;
        ImageView ivScreenshot1;
    }

    private void setBubbleState(final View view,  final MessageModel offlineMessagePersistent, BubbleState state) {
        ViewHolder holder = null;
        if(view.getTag() == null) {
            holder = new ViewHolder();
            holder.llLeftBaloon = (LinearLayout) view.findViewById(R.id.llLeftBaloon);
            holder.llLeft = (LinearLayout) view.findViewById(R.id.llLeft);
            holder.llRight = (LinearLayout) view.findViewById(R.id.llRight);
            holder.llRightBaloon = (LinearLayout) view.findViewById(R.id.llRightBaloon);
            holder.tvDialogState = (TextView) view.findViewById(R.id.tvDialogState);
            holder.tvMessageLeft = (TextView) view.findViewById(R.id.tvLeft);
            holder.tvMessageRight = (TextView) view.findViewById(R.id.tvRight);
            holder.tvTimerLeft = (TextView) view.findViewById(R.id.tvTimerLeft);
            holder.tvTimerRight = (TextView) view.findViewById(R.id.tvTimerRight);
            holder.ivScreenshot = (ImageView) view.findViewById(R.id.ivScreenshot);
            holder.ivScreenshot1 = (ImageView) view.findViewById(R.id.ivScreenshot1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.llRightBaloon.getBackground().setColorFilter(getContext().getResources().getColor(R.color.new_blue), PorterDuff.Mode.SRC_ATOP);
        holder.llLeftBaloon.getBackground().setColorFilter(getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        holder.tvMessageLeft.setTextColor(Color.BLACK);
        holder.tvMessageRight.setTextColor(Color.WHITE);

        if("CLOSE_DIALOG".equals(offlineMessagePersistent.getText())) {
            state = BubbleState.CLOSE;
        }
        if("OPEN_DIALOG".equals(offlineMessagePersistent.getText())) {
            state = BubbleState.OPEN;
        }
        if(offlineMessagePersistent.getText() != null && offlineMessagePersistent.getText().contains("/") && state == BubbleState.OPERATOR) {
            final String filePath = offlineMessagePersistent.getText();
            String messageText = offlineMessagePersistent.getText();
            holder.tvMessageLeft.setText(messageText);
            if(messageText.endsWith("png") || messageText.endsWith("jpg")) {
                state = BubbleState.FILE_OPERATOR;
                ImageLoader.getInstance().displayImage(messageText, holder.ivScreenshot1);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(filePath);
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String[] parts = filePath.split("\\.");
                    String ext = "";
                    if(parts != null && parts.length > 0) {
                        ext = parts[parts.length-1];
                    }
                    String type = map.getMimeTypeFromExtension(ext);
                    if(type != null) {
                        if(URLUtil.isValidUrl(filePath)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
                            getContext().startActivity(browserIntent);
                            return;
                        } else {
                            type = "*/*";
                        }
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(file);
                    intent.setDataAndType(data, type);
                    getContext().startActivity(intent);
                }
            });
        } else if(offlineMessagePersistent.getText() != null && offlineMessagePersistent.getText().contains("/") && state == BubbleState.VISITOR) {

            String filePath1 = offlineMessagePersistent.getText();
            holder.tvMessageRight.setText(offlineMessagePersistent.getText());
            final String filePath = filePath1;
            String messageText = offlineMessagePersistent.getText();
            holder.tvMessageLeft.setText(messageText);
            if(messageText.endsWith("png") || messageText.endsWith("jpg")) {
                state = BubbleState.FILE_VISITOR;
                if(messageText.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(messageText, holder.ivScreenshot);
                } else {
                    Picasso.with(getContext()).load(messageText).into(holder.ivScreenshot);
                }

            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(filePath);
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String[] parts = filePath.split("\\.");
                    String ext = "";
                    if(parts != null && parts.length > 0) {
                        ext = parts[parts.length-1];
                    }
                    String type = map.getMimeTypeFromExtension(ext);
                    if(type != null) {
                        if(URLUtil.isValidUrl(filePath)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
                            getContext().startActivity(browserIntent);
                            return;
                        } else {
                            type = "*/*";
                        }
                    }
                    Log.d("tag", "click");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(file);
                    intent.setDataAndType(data, type);
                    getContext().startActivity(intent);
                }
            });
        } else {
            view.setOnClickListener(null);
        }


        switch (state) {
            case CLOSE:
                holder.tvDialogState.setVisibility(View.VISIBLE);
                holder.tvDialogState.setText("Оператор вышел из диалога");
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.GONE);
                break;
            case OPEN:
                holder.tvDialogState.setVisibility(View.VISIBLE);
                holder.tvDialogState.setText("Оператор зашел в диалог");
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.GONE);
                break;
            case VISITOR:
                if("Скриншот отправлен".equals(offlineMessagePersistent.getText())) {
                    holder.ivScreenshot.setVisibility(View.VISIBLE);
                }
                holder.tvDialogState.setVisibility(View.GONE);
                holder.ivScreenshot.setVisibility(View.GONE);
                holder.ivScreenshot1.setVisibility(View.GONE);
                holder.tvMessageRight.setVisibility(View.VISIBLE);
                holder.llLeft.setVisibility(View.GONE);
                holder.llRight.setVisibility(View.VISIBLE);
                holder.tvMessageRight.setText(offlineMessagePersistent.getText());
                holder.tvTimerRight.setText(formatTimestamp1(offlineMessagePersistent.getDate()));
                break;
            case OPERATOR:
                holder.tvDialogState.setVisibility(View.GONE);
                holder.ivScreenshot.setVisibility(View.GONE);
                holder.ivScreenshot1.setVisibility(View.GONE);
                holder.tvMessageLeft.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.GONE);
                holder.llLeft.setVisibility(View.VISIBLE);
                holder.tvMessageLeft.setText(offlineMessagePersistent.getText());
                holder.tvTimerLeft.setText(formatTimestamp1(offlineMessagePersistent.getDate()));
                break;
            case FILE_OPERATOR:
                holder.tvDialogState.setVisibility(View.GONE);
                holder.ivScreenshot1.setVisibility(View.VISIBLE);
                holder.llLeft.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.GONE);
                holder.tvMessageRight.setVisibility(View.GONE);
                holder.tvMessageLeft.setVisibility(View.GONE);
                holder.tvTimerLeft.setText(formatTimestamp1(offlineMessagePersistent.getDate()));
                break;
            case  FILE_VISITOR:
                holder.tvDialogState.setVisibility(View.GONE);
                holder.llLeft.setVisibility(View.GONE);
                holder.ivScreenshot.setVisibility(View.VISIBLE);
                holder.llRight.setVisibility(View.VISIBLE);
                holder.llLeft.setVisibility(View.GONE);
                holder.tvMessageLeft.setVisibility(View.GONE);
                holder.tvMessageRight.setVisibility(View.GONE);
                holder.tvTimerRight.setText(formatTimestamp1(offlineMessagePersistent.getDate()));
                break;
        }
    }

    private String formatTimestamp1(String timestamp) {
        if(timestamp != null && !"".equals(timestamp)) {
            long d = (long) (Double.parseDouble(timestamp));
            if(timestamp.length() <=11) {
                d = d * 1000;
            }
            Date date = new Date(d);
            String f = sdf_time.format(date);
            return f;
        } else {
            return "";
        }
    }

}
