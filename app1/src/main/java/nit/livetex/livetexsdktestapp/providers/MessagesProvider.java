package nit.livetex.livetexsdktestapp.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import nit.livetex.livetexsdktestapp.Const;

import java.sql.SQLException;

/**
 * Created by user on 15.07.15.
 */
public class MessagesProvider extends ContentProvider {

    private static final String DB_NAME = "offline_messages_db";
    private static final int DB_VERSION = Const.DB_VERSION;
    public static final String TABLE_DATA = "messages";

    public static final String ID = "_id";
    public static final String IS_VISITOR_MESSAGE = "is_visitor_message";
    public static final String TEXT = "message_text";
    public static final String DATE = "message_date";
    public static final String CONVERSATION_ID = "conversation_id";

    private final static String CREATE_TABLE = "create table " + TABLE_DATA + " ("
            + IS_VISITOR_MESSAGE + " integer default -1, "
            + ID + " integer primary key, "
            + TEXT + " text default '\'\'', "
            + DATE + " text default '\'\'', "
            + CONVERSATION_ID + " text default '\'\''"
            + ");";

    public final static String AUTHORITY = "nit.livetex.livetexsdktestapp.providers.MessagesProvider";

    public final static Uri URI_DATA = Uri.parse("content://" + AUTHORITY + "/" + TABLE_DATA);

    private final static String DROP_TABLE = "drop table if exists " + TABLE_DATA;

    private final static int MATCH_MESSAGE_ITEM = 0;
    private final static int MATCH_MESSAGE_DIR = 1;
    private final static int MATCH_CONVERSATION_ID = 2;

    private UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH) {{
        addURI(AUTHORITY, TABLE_DATA + "/#", MATCH_MESSAGE_ITEM);
        addURI(AUTHORITY, TABLE_DATA, MATCH_MESSAGE_DIR);
        addURI(AUTHORITY, TABLE_DATA + "/" + CONVERSATION_ID + "/#", MATCH_CONVERSATION_ID);
    }};

    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case MATCH_CONVERSATION_ID:
                sortOrder = DATE + " ASC";
                selection = CONVERSATION_ID + "=" + uri.getLastPathSegment();
                break;
            case MATCH_MESSAGE_DIR:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ID + " ASC";
                }
                break;
            case MATCH_MESSAGE_ITEM:
                selection = ID + "=" + uri.getLastPathSegment();
        }
        db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DATA, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case MATCH_MESSAGE_ITEM:
                throw new IllegalArgumentException("Wrong Uri: " + uri);
        }
        db = dbOpenHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_DATA, null, values);
        Uri insertUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(insertUri, null);
        return insertUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbOpenHelper.getWritableDatabase();
        int affectedRowCount = db.delete(TABLE_DATA, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return affectedRowCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case MATCH_MESSAGE_DIR:
                throw new IllegalArgumentException("Wrong Uri: " + uri);
            case MATCH_MESSAGE_ITEM:
                selection = ID + "=" + uri.getLastPathSegment();
                break;
        }
        db = dbOpenHelper.getWritableDatabase();
        int rowsAffected = db.update(TABLE_DATA, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String table;
        switch (URI_MATCHER.match(uri)) {
            case MATCH_MESSAGE_DIR:
                table = TABLE_DATA;
                break;
            default:
                throw new IllegalArgumentException("Wrong Uri: " + uri);
        }
        db = dbOpenHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (ContentValues cv : values) {
                long newId = db.insertOrThrow(table, null, cv);
                if (newId <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return values.length;
    }

    private class DbOpenHelper extends SQLiteOpenHelper {

        public DbOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }


}

