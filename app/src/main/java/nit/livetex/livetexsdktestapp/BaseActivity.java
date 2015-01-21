package nit.livetex.livetexsdktestapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import android.text.TextUtils;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import livetex.sdk.models.Department;
import livetex.sdk.models.DialogState;
import livetex.sdk.models.Employee;
import livetex.sdk.models.FileMessage;
import livetex.sdk.models.HoldMessage;
import livetex.sdk.models.TextMessage;
import livetex.sdk.models.TypingMessage;

/**
 * Created by sergey.so on 14.11.2014.
 *
 */
public class BaseActivity extends ActionBarActivity {

    protected Reciever mReciever;
    private ProgressDialog mProgressDialog;
    private static HashMap<String, String> errors;
    private Handler mHandler;

    static {
        errors = new HashMap<>();
        errors.put(MainApplication.REQUEST_INIT, "Ошибка инициализации");
        errors.put(MainApplication.REQUEST_DEPARTMENTS, "Ошибка получения списка отделов");
        errors.put(MainApplication.REQUEST_SEND_MSG, "Ошибка отправки сообщения");
        errors.put(MainApplication.REQUEST_MSG_HISTORY, "Ошибка получения истории");
        errors.put(MainApplication.REQUEST_DIALOG, "Ошибка получения диалога");
        errors.put(MainApplication.REQUEST_VOTE, "Ошибка отправки оценки");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mHandler == null) mHandler = new Handler();
        if (mReciever == null) mReciever = new Reciever();
        registerReceiver(mReciever, new IntentFilter(MainApplication.ACTION_RECIEVER));
        initProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgressDialog.dismiss();
    }

    private void registerNewReciever(){
        if (mReciever == null) mReciever = new Reciever();
        registerReceiver(mReciever, new IntentFilter(MainApplication.ACTION_RECIEVER));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReciever);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
    }

    protected final void showProgressDialog(String msg) {
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void showToast(String txt) {
        if (TextUtils.isEmpty(txt)) return;
        Toast.makeText(MainApplication.getInstance(), txt, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(String msg){
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    protected void initComplete() {

    }

    protected void departmentsRecieved(List<Department> result) {

    }

    protected void employeesRecieved(List<Employee> result) {

    }

    protected void dialogRecieved(DialogState state) {

    }

    protected void onUpdateDialogState(DialogState state) {

    }

    protected void onMsgSended(TextMessage msg) {

    }

    protected void onMsgHistoryGetted(List<TextMessage> msg) {

    }

    protected void onMsgRecieved(TextMessage msg) {

    }

    protected void onVoted() {

    }

    protected void onTyping(TypingMessage typingMessage) {

    }

    protected void onNameSetted() {

    }

    protected void onDialogStateGetted(DialogState state) {

    }

    protected void onOperatorMsgConfirmed() {

    }

    protected void onSendMsgConfirmed(String msgId) {
    }

    protected void onError(String request, String msg){

    }

    protected void onDialogClose(DialogState state){

    }

    protected void onFileRecieved(FileMessage fileMessage){
    }

    protected void onHoldMsgRecieved(HoldMessage holdMessage){
    }

    private class Reciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            }, 500);
            if (intent.getIntExtra(MainApplication.KEY_RESULT_CODE, 0) == -1) {
                showToast(errors.get(intent.getStringExtra(MainApplication.KEY_REQUEST_NAME)));
                onError(intent.getStringExtra(MainApplication.KEY_REQUEST_NAME),
                        intent.getStringExtra(MainApplication.KEY_RESULT_OBJECT));
                return;
            }
            Serializable result = intent.getSerializableExtra(MainApplication.KEY_RESULT_OBJECT);
            switch (intent.getStringExtra(MainApplication.KEY_REQUEST_NAME)) {
                case MainApplication.REQUEST_INIT:
                    initComplete();
                    break;
                case MainApplication.REQUEST_DEPARTMENTS:
                    departmentsRecieved((List<Department>) result);
                    break;
                case MainApplication.REQUEST_OPERATORS:
                    employeesRecieved((List<Employee>) result);
                    break;
                case MainApplication.REQUEST_DIALOG:
                    dialogRecieved((DialogState) result);
                    break;
                case MainApplication.REQUEST_SEND_MSG:
                    onMsgSended((TextMessage) result);
                    break;
                case MainApplication.REQUEST_MSG_HISTORY:
                    onMsgHistoryGetted((ArrayList<TextMessage>) result);
                    break;
                case MainApplication.REQUEST_RECIEVE_MSG:
                    onMsgRecieved((TextMessage) result);
                    break;
                case MainApplication.REQUEST_HOLD_MSG:
                    onHoldMsgRecieved((HoldMessage) result);
                    break;
                case MainApplication.REQUEST_RECIEVE_FILE:
                    onFileRecieved((FileMessage) result);
                    break;
                case MainApplication.REQUEST_VOTE:
                    onVoted();
                    break;
                case MainApplication.REQUEST_UPDATE_STATE:
                    onUpdateDialogState((DialogState) result);
                    break;
                case MainApplication.REQUEST_OPERATOR_TYPING:
                    onTyping((TypingMessage) result);
                    break;
                case MainApplication.REQUEST_SET_NAME:
                    onNameSetted();
                    break;
                case MainApplication.REQUEST_GET_STATE:
                    onDialogStateGetted((DialogState) result);
                    break;
                case MainApplication.REQUEST_CONFIRM_OPERATOR_MSG:
                    onOperatorMsgConfirmed();
                    break;
                case MainApplication.REQUEST_CONFIRM_SEND_MSG:
                    onSendMsgConfirmed((String) result);
                    break;
                case MainApplication.REQUEST_CLOSE_CHAT:
                    onDialogClose((DialogState) result);
                    break;
            }
        }
    }
}
