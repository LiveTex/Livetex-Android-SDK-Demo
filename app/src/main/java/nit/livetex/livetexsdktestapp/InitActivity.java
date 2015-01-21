package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import livetex.sdk.models.DialogState;


public class InitActivity extends BaseActivity {

    public static String AUTH_URL = "";
    public static String API_KEY = "";

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, InitActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
//        ((EditText) findViewById(R.id.input_id)).setText("10008248");
//        ((EditText) findViewById(R.id.input_id)).setText("10006460");
        ((EditText) findViewById(R.id.input_id)).setText("91605");
        ((EditText) findViewById(R.id.authUrl)).setText("http://authentication-service-sdk-prerelease.livetex.ru/");
        ((EditText) findViewById(R.id.apiKey)).setText(MainApplication.API_KEY);
        findViewById(R.id.btn)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AUTH_URL = ((EditText) findViewById(R.id.authUrl)).getText().toString();
                                            API_KEY = ((EditText) findViewById(R.id.apiKey)).getText().toString();
                                            initClick(v.getContext());
                                        }
                                    }
                );
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropData(v.getContext());
            }
        });
    }

    private void initClick(final Context context){
        GcmUtils.startGCM(this, new GcmUtils.Callback() {
            @Override
            public void onResult(boolean status, String msg) {
                if (!status) {
                    showToast("GCM error:"+msg);
                    return;
                }
                if (TextUtils.isEmpty(((EditText) findViewById(R.id.input_id)).getText().toString())) {
                    showToast("Введите id");
                    return;
                }
                DataKeeper.saveAppId(context, ((EditText) findViewById(R.id.input_id)).getText().toString());
                showProgressDialog("инициализация");
                init(msg);
            }
        });
    }


    private void dropData(Context context) {
        context.getSharedPreferences("com.livetex.sdk.thrift.PREFS", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
        showToast("Кэш очищен");
    }

    private void init(String regId) {
        EditText idEt = (EditText) findViewById(R.id.input_id);
        MainApplication.initLivetex(idEt.getText().toString(), regId);
    }

    @Override
    protected void initComplete() {
        getDialogState();
        showProgressDialog("инициализация");
    }

    private void getDialogState() {
        MainApplication.getDialogState();
    }

    @Override
    protected void onDialogStateGetted(DialogState state) {
        if (state == null) return;
        try {
            unregisterReceiver(mReciever);
        } catch (IllegalArgumentException ignored) {
        }
        if (state.conversation != null) {
            ChatActivity.show(InitActivity.this);
        } else {
            WelcomeActivity.show(InitActivity.this);
        }
    }
}
