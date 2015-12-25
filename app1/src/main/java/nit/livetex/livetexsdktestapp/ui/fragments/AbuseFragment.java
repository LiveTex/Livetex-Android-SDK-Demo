package nit.livetex.livetexsdktestapp.ui.fragments;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.ui.fragments.offline.SendOfflineMessageFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
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
                //showFragment(new SendOfflineMessageFragment(), true);
                getFragmentEnvironment().getSupportFragmentManager().popBackStack();
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSendAbuse) {
            if(!CommonUtils.isEmpty(etAbuseMessage, etAbusePhone, etEmail)) {
                if(!CommonUtils.isEmailValid(etEmail.getText().toString())) {
                    CommonUtils.showToast(getContext(), "Введите, пожалуйста, корректный email");
                    return;
                }
                MainApplication.abuse(etAbusePhone.getText().toString(), etAbuseMessage.getText().toString());
                CommonUtils.showToast(getContext(), "Ваша жалоба отправлена");
                getFragmentEnvironment().getSupportFragmentManager().popBackStack();
            } else {
                CommonUtils.showToast(getContext(), "Пожалуйста, заполните все поля");
            }
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









