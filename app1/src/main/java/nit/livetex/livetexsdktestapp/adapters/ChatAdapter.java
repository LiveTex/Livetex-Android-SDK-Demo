package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.MessagePersistent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 16.07.15.
 */
public class ChatAdapter extends CursorAdapter {
    private final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat sdf_date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private Context context;

    private enum BubbleState {
        OPERATOR, VISITOR, CLOSE, OPEN
    }

    public ChatAdapter(Context context) {
        super(context, null, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_chat, viewGroup, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MessagePersistent offlineMessagePersistent = new MessagePersistent(cursor);
        if(offlineMessagePersistent.isVisitorMessage()) {
            setBubbleState(view,offlineMessagePersistent, BubbleState.VISITOR);
        } else {
            setBubbleState(view, offlineMessagePersistent, BubbleState.OPERATOR);
        }
    }

    private void setBubbleState(View view,  final MessagePersistent offlineMessagePersistent, BubbleState state) {
        if("CLOSE_DIALOG".equals(offlineMessagePersistent.getText())) {
            state = BubbleState.CLOSE;
        }
        if("OPEN_DIALOG".equals(offlineMessagePersistent.getText())) {
            state = BubbleState.OPEN;
        }
        if(offlineMessagePersistent.getText() != null &&offlineMessagePersistent.getText().contains("/")) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String filePath = offlineMessagePersistent.getText();
                    File file = new File(filePath);
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String ext = filePath.split("\\.").length > 0 ? filePath.split("\\.")[1] : "";
                    String type = map.getMimeTypeFromExtension(ext);
                    if(type == null) {
                        if(URLUtil.isValidUrl(filePath)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
                            context.startActivity(browserIntent);
                            return;
                        } else {
                            type = "*/*";
                        }
                    }
                    Log.d("tag", "click");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(file);
                    intent.setDataAndType(data, type);
                    context.startActivity(intent);
                }
            });
        }
        LinearLayout llLeft = (LinearLayout) view.findViewById(R.id.llLeft);
        LinearLayout llRight = (LinearLayout) view.findViewById(R.id.llRight);
        LinearLayout llRightBaloon = (LinearLayout) view.findViewById(R.id.llRightBaloon);
        llRightBaloon.getBackground().setColorFilter(context.getResources().getColor(R.color.new_blue), PorterDuff.Mode.SRC_ATOP);
        TextView tvDialogState = (TextView) view.findViewById(R.id.tvDialogState);
        TextView tvMessageLeft = (TextView) view.findViewById(R.id.tvLeft);
        tvMessageLeft.getBackground().setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        tvMessageLeft.setTextColor(Color.BLACK);
        TextView tvMessageRight = (TextView) view.findViewById(R.id.tvRight);
        tvMessageRight.setTextColor(Color.WHITE);
        TextView tvTimerLeft = (TextView) view.findViewById(R.id.tvTimerLeft);
        TextView tvTimerRight = (TextView) view.findViewById(R.id.tvTimerRight);
        ImageView ivScreenshot = (ImageView) view.findViewById(R.id.ivScreenshot);
        switch (state) {
            case CLOSE:
                tvDialogState.setVisibility(View.VISIBLE);
                tvDialogState.setText("Оператор вышел из диалога");
                llLeft.setVisibility(View.GONE);
                llRight.setVisibility(View.GONE);
                break;
            case OPEN:
                tvDialogState.setVisibility(View.VISIBLE);
                tvDialogState.setText("Оператор зашел в диалог");
                llLeft.setVisibility(View.GONE);
                llRight.setVisibility(View.GONE);
                break;
            case VISITOR:
                if("Скриншот отправлен".equals(offlineMessagePersistent.getText())) {
                    ivScreenshot.setVisibility(View.VISIBLE);
                }
                llLeft.setVisibility(View.GONE);
                llRight.setVisibility(View.VISIBLE);
                tvMessageRight.setText(offlineMessagePersistent.getText());
                tvTimerRight.setText(formatTimestamp(offlineMessagePersistent.getDate()));
                break;
            case OPERATOR:
                llRight.setVisibility(View.GONE);
                llLeft.setVisibility(View.VISIBLE);
                tvMessageLeft.setText(offlineMessagePersistent.getText());
                tvTimerLeft.setText(formatTimestamp(offlineMessagePersistent.getDate()));
                break;
        }
    }

    private String formatTimestamp(String timestamp) {
        if(timestamp != null && !"".equals(timestamp)) {
            Date date = new Date((long) (Double.parseDouble(timestamp)));
            return sdf_time.format(date);
        } else {
            return "";
        }
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }
}














