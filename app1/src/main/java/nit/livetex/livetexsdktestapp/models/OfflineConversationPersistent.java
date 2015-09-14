package nit.livetex.livetexsdktestapp.models;

import android.content.ContentValues;
import android.database.Cursor;

import nit.livetex.livetexsdktestapp.providers.ConversationsProvider;

import Clients.Enums.OfflineConversation;
import sdk.models.LTEmployee;

/**
 * Created by user on 15.07.15.
 */
public class OfflineConversationPersistent {

    private String title;
    private String conversationId;
    private boolean isRead;
    private String avatar;
    private String createdAt;
    private String firstName;

    public OfflineConversationPersistent(OfflineConversation offlineConversation, LTEmployee operator) {
        this.conversationId = offlineConversation.getId();
        this.title = offlineConversation.getTitle();
        this.avatar = operator.getAvatar();
        this.firstName = operator.getFirstname();
        this.createdAt = offlineConversation.getCreated_at();
    }

    public OfflineConversationPersistent(String title, String conversationId, boolean isRead, String avatar) {
        this.title = title;
        this.conversationId = conversationId;
        this.isRead = isRead;
        this.avatar = avatar;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public OfflineConversationPersistent(Cursor cursor) {
        this.title = cursor.getString(cursor.getColumnIndex(ConversationsProvider.TITLE));
        this.conversationId = cursor.getString(cursor.getColumnIndex(ConversationsProvider.CONVERSATION_ID));
        this.isRead = cursor.getInt(cursor.getColumnIndex(ConversationsProvider.IS_READ)) == 1 ? true : false;
        this.avatar = cursor.getString(cursor.getColumnIndex(ConversationsProvider.AVATAR));
        this.createdAt = cursor.getString(cursor.getColumnIndex(ConversationsProvider.CREATED_AT));
        this.firstName = cursor.getString(cursor.getColumnIndex(ConversationsProvider.FIRST_NAME));
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ConversationsProvider.CONVERSATION_ID, this.conversationId);
        cv.put(ConversationsProvider.TITLE, this.title);
        cv.put(ConversationsProvider.IS_READ, isRead ? 1 : 0);
        cv.put(ConversationsProvider.AVATAR, avatar);
        cv.put(ConversationsProvider.CREATED_AT, createdAt);
        cv.put(ConversationsProvider.FIRST_NAME, firstName);
        return cv;
    }

    public String getTitle() {
        return title;
    }

    public String getConversationId() {
        return conversationId;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getFirstName() {
        return firstName;
    }
}
