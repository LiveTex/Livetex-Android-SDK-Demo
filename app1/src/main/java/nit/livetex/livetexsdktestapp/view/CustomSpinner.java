package nit.livetex.livetexsdktestapp.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by user on 13.09.15.
 */
public class CustomSpinner extends Spinner {
    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
    }
}
