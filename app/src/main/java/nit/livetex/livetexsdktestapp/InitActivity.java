package nit.livetex.livetexsdktestapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class InitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        findViewById(R.id.btn)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (TextUtils.isEmpty(((EditText) findViewById(R.id.input_id)).getText().toString())) {
                                                Toast.makeText(MainApplication.getInstance(), "Введите id", Toast.LENGTH_LONG).show();
                                                return;
                                            }
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
        WelcomeActivity.show(InitActivity.this);
    }
}
