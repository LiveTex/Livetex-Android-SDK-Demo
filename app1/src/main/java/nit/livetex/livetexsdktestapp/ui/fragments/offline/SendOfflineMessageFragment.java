package nit.livetex.livetexsdktestapp.ui.fragments.offline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.presenters.offline.SendOfflineMessagePresenter;
import nit.livetex.livetexsdktestapp.providers.ConversationsProvider;
import nit.livetex.livetexsdktestapp.ui.callbacks.SendOfflineMessageCallback;
import nit.livetex.livetexsdktestapp.ui.fragments.BaseFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import nit.livetex.livetexsdktestapp.view.CustomEditText;

/**
 * Created by user on 28.07.15.
 */
public class SendOfflineMessageFragment extends BaseFragment implements SendOfflineMessageCallback, View.OnClickListener {

    EditText etName;
    EditText etEmail;
    EditText etMessage;
    //EditText etPhone;
    Button btnSendOfflineMsg;

    private SendOfflineMessagePresenter presenter;

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View v = inflater.inflate(R.layout.header_abuse, null);
        ImageView ivBackToDialog = (ImageView) v.findViewById(R.id.ivBackToDialog);
        ivBackToDialog.setVisibility(View.INVISIBLE);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("Отправить обращение");
        return v;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_send_offline_msg;
    }

    private void init(View v) {
     //   etPhone = (CustomEditText) v.findViewById(R.id.etPhone);
        etName = (CustomEditText) v.findViewById(R.id.etName);
        etEmail = (CustomEditText) v.findViewById(R.id.etEmail);
        etMessage = (CustomEditText) v.findViewById(R.id.etMessage);
        btnSendOfflineMsg = (Button) v.findViewById(R.id.btnSendOfflineMsg);
        btnSendOfflineMsg.setOnClickListener(this);
    }

    @Override
    protected void setSoftInputMode() {
        getFragmentEnvironment().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        presenter = new SendOfflineMessagePresenter(this);
        return super.onCreateView(v);
    }

    @Override
    public void onClick(View view) {

            String phone = "";
            String name = "";
                /*if(!CommonUtils.isEmpty(etPhone)) {
                    phone = etPhone.getText().toString();
                }*/
            if(!CommonUtils.isEmpty(etName)) {
                name = etName.getText().toString();
            }
            if(!CommonUtils.isEmpty(etName, etEmail, etMessage)) {
                if(!CommonUtils.isEmailValid(etEmail.getText().toString())) {
                    CommonUtils.showToast(getContext(), "Неверно введен email");
                    return;
                }
                showProgress();
                presenter.createConversation(name, etEmail.getText().toString(), phone, etMessage.getText().toString());
            } else {
                CommonUtils.showToast(getActivity(), "Заполните обязательные поля");
            }

    }

    @Override
    public void onMessageSended(String conversationId) {
        dismissProgress();
        getFragmentManager().popBackStack();
        if(conversationId != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ConversationsProvider.CONVERSATION_ID, conversationId);
            showFragment(new OfflineChatFragment(), bundle, false);
        }

    }
}





















