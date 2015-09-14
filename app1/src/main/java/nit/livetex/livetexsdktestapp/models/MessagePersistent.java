package nit.livetex.livetexsdktestapp.models;

import android.content.ContentValues;
import android.database.Cursor;

import nit.livetex.livetexsdktestapp.providers.MessagesProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Clients.Enums.OfflineMessage;

/**
 * Created by user on 15.07.15.
 */
public class MessagePersistent {

    private boolean isVisitorMessage;
    private String text;
    private String date;
    private String conversationId;

    public MessagePersistent(boolean isVisitorMessage, String text, String date, String conversationId) {
        this.isVisitorMessage = isVisitorMessage;
        this.text = text;
        this.date = date;
        this.conversationId = conversationId;
    }

    public MessagePersistent(OfflineMessage offlineMessage, String conversationId) {
        this.text = offlineMessage.getMessage();
        this.conversationId = conversationId;
        this.date = toMillis(offlineMessage.getCreated_at());
        this.isVisitorMessage = "0".equals(offlineMessage.getSender()) ? true : false;
    }

    private String toMillis(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        try {
            Date date = sdf.parse(stringDate);
            long dateLong = date.getTime();
            return String.valueOf(dateLong);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public MessagePersistent(Cursor cursor) {
            this.isVisitorMessage = cursor.getInt(cursor.getColumnIndex(MessagesProvider.IS_VISITOR_MESSAGE)) == 1 ? true : false;
            this.text = cursor.getString(cursor.getColumnIndex(MessagesProvider.TEXT));
            this.date = cursor.getString(cursor.getColumnIndex(MessagesProvider.DATE));
            this.conversationId = cursor.getString(cursor.getColumnIndex(MessagesProvider.CONVERSATION_ID));
    }

    public boolean isVisitorMessage() {
        return isVisitorMessage;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getConversationId() {
        return conversationId;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(MessagesProvider.IS_VISITOR_MESSAGE, isVisitorMessage ? 1 : 0);
        cv.put(MessagesProvider.TEXT, text);
        cv.put(MessagesProvider.DATE, date);
        cv.put(MessagesProvider.CONVERSATION_ID, conversationId);
        return cv;
    }

}
