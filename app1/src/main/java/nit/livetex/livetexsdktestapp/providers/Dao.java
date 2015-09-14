package nit.livetex.livetexsdktestapp.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import nit.livetex.livetexsdktestapp.models.MessagePersistent;
import nit.livetex.livetexsdktestapp.models.OfflineConversationPersistent;

import java.util.ArrayList;

import Clients.Enums.OfflineConversation;
import Clients.Enums.OfflineMessage;
import sdk.models.LTEmployee;

/**
 * Created by user on 15.07.15.
 */
public class Dao {

    private static Dao instance;

    private Context context;

    public static Dao getInstance(Context context) {
        if(instance == null) {
            instance = new Dao(context);
        }
        return instance;
    }

    public Dao(Context context) {
        this.context = context;
    }

    public void clearConversations() {
        context.getContentResolver().delete(ConversationsProvider.URI_DATA, null, null);
    }

    public void saveConversations(ArrayList<OfflineConversation> conversations) {
        ContentValues[] cvs = new ContentValues[conversations.size()];
        for(int i=0; i < cvs.length; i++) {
            //cvs[0] = new OfflineConversationPersistent(conversations.get(i)).toContentValues();
        }
        context.getContentResolver().bulkInsert(ConversationsProvider.URI_DATA, cvs);
    }

    public MessagePersistent getLastMessageFromConversation(String conversationId) {
        Cursor cursor = context.getContentResolver().query(MessagesProvider.URI_DATA, null, MessagesProvider.CONVERSATION_ID + "=" + conversationId, null, MessagesProvider.DATE + " DESC");
        if(cursor.moveToFirst()) {
            MessagePersistent messagePersistent = new MessagePersistent(cursor);
            cursor.close();
            return messagePersistent;
        }
        return null;
    }

    public boolean isConversationsEmpty() {
        Cursor cursor = context.getContentResolver().query(ConversationsProvider.URI_DATA, null, null, null, null);
        return !cursor.moveToFirst();
    }

    public void saveConversation(OfflineConversation offlineConversation, LTEmployee operator) {
        context.getContentResolver().insert(ConversationsProvider.URI_DATA, new OfflineConversationPersistent(offlineConversation, operator).toContentValues());
    }

    public void saveMessages(ArrayList<OfflineMessage> messages, String conversationId) {
        ContentValues[] cvs = new ContentValues[messages.size()];
        for(int i=0; i < cvs.length; i++) {
            MessagePersistent offlineMessagePersistent = new MessagePersistent(messages.get(i), conversationId);
            cvs[i] = offlineMessagePersistent.toContentValues();
        }
        context.getContentResolver().bulkInsert(MessagesProvider.URI_DATA, cvs);
    }

    public OfflineConversationPersistent getConversationBy(String conversationId) {
        Cursor cursor = context.getContentResolver().query(ConversationsProvider.URI_DATA, null, ConversationsProvider.CONVERSATION_ID + "=" + conversationId, null, null);
        if(cursor.moveToFirst()) {
            return new OfflineConversationPersistent(cursor);
        }
        return null;
    }

    public void saveMessage(String message, String date, int conversationId, boolean isVisitorMessage) {

        MessagePersistent offlineMessagePersistent = new MessagePersistent(isVisitorMessage, message, date, String.valueOf(conversationId));

        context.getContentResolver().insert(MessagesProvider.URI_DATA, offlineMessagePersistent.toContentValues());
    }

    public MessagePersistent getOfflineMessage(String conversationId) {
        Cursor cursor = context.getContentResolver().query(MessagesProvider.URI_DATA, null, MessagesProvider.CONVERSATION_ID + "=" + conversationId, null, null);
        if(cursor.moveToFirst()) {
            MessagePersistent offlineMessagePersistent = new MessagePersistent(cursor);
            return offlineMessagePersistent;
        }
        return null;
    }
    public void clearConversation(String conversationId) {
        Uri uri = Uri.withAppendedPath(MessagesProvider.URI_DATA, MessagesProvider.CONVERSATION_ID + "/" + conversationId);
        context.getContentResolver().delete(uri, null, null);
    }
    public void clearAll() {
        context.getContentResolver().delete(MessagesProvider.URI_DATA, null, null);
        context.getContentResolver().delete(ConversationsProvider.URI_DATA, null, null);
    }

    public void saveConversation(String title, int conversationId, String avatar) {
        if(conversationId > 0) {
            OfflineConversationPersistent offlineConversationPersistent = new OfflineConversationPersistent(title, String.valueOf(conversationId), true, avatar);
            context.getContentResolver().insert(ConversationsProvider.URI_DATA, offlineConversationPersistent.toContentValues());
        }
    }

    public Cursor getMessageListBy(int conversationId) {
        Uri uri = Uri.withAppendedPath(MessagesProvider.URI_DATA, MessagesProvider.CONVERSATION_ID + "/" + conversationId);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null);
        return cursor;
    }

    public boolean hasMessage(String conversationId, String message) {
        Uri uri = Uri.withAppendedPath(MessagesProvider.URI_DATA, MessagesProvider.CONVERSATION_ID + "/" + conversationId);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex(MessagesProvider.TEXT));
                if(message.equals(text)) {
                    return true;
                }
            } while (cursor.moveToNext());

        }
        return false;
    }

    public boolean hasConversation(String conversationId) {
        Cursor cursor = context.getContentResolver().query(ConversationsProvider.URI_DATA, null, ConversationsProvider.CONVERSATION_ID + "=" + conversationId, null, null);
        return  cursor.moveToFirst();
    }
}















