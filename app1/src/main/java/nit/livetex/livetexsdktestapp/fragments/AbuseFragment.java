package nit.livetex.livetexsdktestapp.fragments;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.view.CustomEditText;

/**
 * Created by user on 16.08.15.
 */
public class AbuseFragment extends BaseFragment implements View.OnClickListener {

    EditText etAbusePhone;
    EditText etAbuseMessage;
    EditText etEmail;
    Button btnSendAbuse;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_abuse;
    }

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View v = inflater.inflate(R.layout.header_abuse, null);
        ImageView ivBackToDialog = (ImageView) v.findViewById(R.id.ivBackToDialog);
        ivBackToDialog.setColorFilter(Color.WHITE);
        ivBackToDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentEnvironment().getSupportFragmentManager().popBackStack();
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        if(R.id.btnSendAbuse == view.getId()) {

        }
    }

    private void init(View v) {
        etAbuseMessage = (CustomEditText) v.findViewById(R.id.etAbuseMessage);
        etAbusePhone = (CustomEditText) v.findViewById(R.id.etAbusePhone);
        etEmail = (CustomEditText) v.findViewById(R.id.etEmail);
        btnSendAbuse = (Button) v.findViewById(R.id.btnSendAbuse);
        btnSendAbuse.setOnClickListener(this);
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        return super.onCreateView(v);
    }

}









