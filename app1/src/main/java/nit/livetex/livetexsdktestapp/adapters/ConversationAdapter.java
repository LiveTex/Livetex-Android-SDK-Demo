package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.MessagePersistent;
import nit.livetex.livetexsdktestapp.models.OfflineConversationPersistent;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 16.07.15.
 */
public class ConversationAdapter extends CursorAdapter {

    private final SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public ConversationAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_offline_conversation, viewGroup, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        OfflineConversationPersistent offlineConversationPersistent = new OfflineConversationPersistent(cursor);
        view.setTag(offlineConversationPersistent.getConversationId());
        TextView tvOperatorName = (TextView) view.findViewById(R.id.tvOperatorName);
        ImageView ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);

        tvOperatorName.setText(offlineConversationPersistent.getFirstName() != null ? offlineConversationPersistent.getFirstName() : "");
        try {
            Picasso.with(context).load(offlineConversationPersistent.getAvatar())/*.resize(50, 50).centerCrop()*/.into(ivAvatar);

        } catch (Exception e) {
            e.printStackTrace();
        }
        MessagePersistent messagePersistent = Dao.getInstance(context).getLastMessageFromConversation(offlineConversationPersistent.getConversationId());
        if(messagePersistent != null) {
            tvMessage.setText(messagePersistent.getText());
        } else if(!TextUtils.isEmpty(DataKeeper.getLastMessage(context))) {
            tvMessage.setText(DataKeeper.getLastMessage(context));
        }
        String date = offlineConversationPersistent.getCreatedAt().split("\\.")[0];
        tvTime.setText(dateReverse(date.split(" ")[0]));

    }

    private String dateReverse(String date) {
        String[] parts = date.split("-");
        StringBuilder sb = new StringBuilder();
        for(int i=parts.length-1; i>=0; i--) {
            sb.append(i==0 ? parts[i].substring(2) : parts[i]).append(i==0 ? "" : ".");
        }
        return sb.toString();
    }

    private String formatTimestamp(String timestamp) {
        if(!TextUtils.isEmpty(timestamp)) {
            Date date = new Date((long) (Double.parseDouble(timestamp)));
            return sdf_date.format(date);
        }
        return "";
    }

    @Override
    public int getItemViewType(int position) {
        return IGNORE_ITEM_VIEW_TYPE;
    }

}
