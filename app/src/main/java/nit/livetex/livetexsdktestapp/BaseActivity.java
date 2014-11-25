package nit.livetex.livetexsdktestapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import livetex.sdk.models.Department;
import livetex.sdk.models.DialogState;
import livetex.sdk.models.Employee;
import livetex.sdk.models.TextMessage;
import livetex.sdk.models.TypingMessage;

/**
 * Created by sergey.so on 14.11.2014.
 *
 */
public class BaseActivity extends ActionBarActivity {

    private Reciever mReciever;
    private ProgressDialog mProgressDialog;
    private static HashMap<String, String> errors;

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
        if (mReciever == null) mReciever = new Reciever();
        registerReceiver(mReciever, new IntentFilter(MainApplication.ACTION_RECIEVER));
        initProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciever);
    }

    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
    }

    protected final void showProgressDialog(String msg){
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    protected void showToast(String txt){
        Toast.makeText(MainApplication.getInstance(), txt, Toast.LENGTH_LONG).show();
    }

    protected void initComplete(){

    }

    protected void departmentsRecieved(List<Department> result){

    }

    protected void employeesRecieved(List<Employee> result){

    }

    protected void dialogRecieved(DialogState state){

    }

    protected void onUpdateDialogState(DialogState state){

    }

    protected void onMsgSended(TextMessage msg){

    }

    protected void onMsgHistoryGetted(List<TextMessage> msg){

    }

    protected void onMsgRecieved(TextMessage msg){

    }

    protected void onVoted(){

    }

    protected void onTyping(TypingMessage typingMessage){

    }

    protected void onNameSetted(){

    }

    private class Reciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressDialog.dismiss();
            if (intent.getIntExtra(MainApplication.KEY_RESULT_CODE, 0) == -1) {
                showToast(errors.get(intent.getStringExtra(MainApplication.KEY_REQUEST_NAME)));
                return;
            }
            Serializable result = intent.getSerializableExtra(MainApplication.KEY_RESULT_OBJECT);
            switch (intent.getStringExtra(MainApplication.KEY_REQUEST_NAME)){
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
                case MainApplication.REQUEST_VOTE:
                    onVoted();
                    break;
                case MainApplication.REQUEST_UPDATE_STATE:
                    onUpdateDialogState((DialogState) result);
                    break;
                case MainApplication.REQUEST_TYPING:
                    onTyping((TypingMessage) result);
                    break;
                case MainApplication.REQUEST_SET_NAME:
                    onNameSetted();
                    break;
            }
        }
    }

}
