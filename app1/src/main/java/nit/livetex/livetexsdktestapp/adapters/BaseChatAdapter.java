package nit.livetex.livetexsdktestapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 29.07.15.
 */
public class BaseChatAdapter extends CursorAdapter {


    public BaseChatAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
