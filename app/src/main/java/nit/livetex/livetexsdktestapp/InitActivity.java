package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import livetex.sdk.models.DialogState;


public class InitActivity extends BaseActivity {

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, InitActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        ((EditText) findViewById(R.id.input_id)).setText("10008248");
        findViewById(R.id.btn)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (TextUtils.isEmpty(((EditText) findViewById(R.id.input_id)).getText().toString())) {
                                                Toast.makeText(MainApplication.getInstance(), "Введите id", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            DataKeeper.saveAppId(v.getContext(), ((EditText) findViewById(R.id.input_id)).getText().toString());
                                            showProgressDialog("инициализация");
                                            init();
                                        }
                                    }
                );
    }

    private void init() {
        EditText idEt = (EditText) findViewById(R.id.input_id);
        MainApplication.initLivetex(idEt.getText().toString());
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
