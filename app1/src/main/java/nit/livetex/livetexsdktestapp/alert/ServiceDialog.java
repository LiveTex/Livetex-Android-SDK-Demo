package nit.livetex.livetexsdktestapp.alert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.FragmentEnvironment;
import nit.livetex.livetexsdktestapp.MainApplication;

public class ServiceDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(FragmentEnvironment.fa != null) {
            FragmentEnvironment.fa.finish();
        }
        MainApplication.clearExternalActivitiesStack();
        Intent intent=getIntent();

        if(intent.hasExtra("text")) {
            final String text = intent.getStringExtra("text");
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Сообщение");
            alert.setIcon(android.R.drawable.ic_dialog_info);
            alert.setMessage(text);
            alert.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent initIntent = new Intent(ServiceDialog.this, FragmentEnvironment.class);
                            initIntent.setAction(Const.PUSH_ONLINE_ACTION);
                            initIntent.putExtra("mes", text);
                            initIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            ServiceDialog.this.startActivity(initIntent);
                            ServiceDialog.this.finish();
                        }
                    });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface arg0) {
                    ServiceDialog.this.finish();
                }
            });
            alert.show();
        }



    }
}
