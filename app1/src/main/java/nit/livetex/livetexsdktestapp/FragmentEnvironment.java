package nit.livetex.livetexsdktestapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import nit.livetex.livetexsdktestapp.fragments.ClientFormFragment;
import nit.livetex.livetexsdktestapp.fragments.InitFragment;
import nit.livetex.livetexsdktestapp.fragments.OnlineChatFragment1;
import nit.livetex.livetexsdktestapp.utils.BusProvider;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

import sdk.handler.AHandler;

public class FragmentEnvironment extends AppCompatActivity {

    public static AppCompatActivity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        setContentView(R.layout.activity_main1);
        init();

    }

    private boolean isAwakenByPush() {
        if (Const.PUSH_ONLINE_ACTION.equals(getIntent().getAction())) {
            String appID = DataKeeper.restoreAppId(this);
            String regID = DataKeeper.restoreRegId(this);
            String mes = getIntent().getStringExtra("mes");

            OnlineChatFragment1 onlineChatFragment = new OnlineChatFragment1();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, onlineChatFragment).commitAllowingStateLoss();

            MainApplication.initLivetex(appID, regID, new AHandler<Boolean>() {
                @Override
                public void onError(String errMsg) {

                }

                @Override
                public void onResultRecieved(Boolean result) {
                    OnlineChatFragment1 onlineChatFragment = new OnlineChatFragment1();
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, onlineChatFragment).commitAllowingStateLoss();
                }
            });
            return true;
        }
        return false;
    }

    public void init() {
        if (!isAwakenByPush()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new InitFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.IS_ACTIVE = true;
        if (MainApplication.getsLiveTex() != null && !TextUtils.isEmpty(sdk.data.DataKeeper.restoreToken(this))) {
            MainApplication.getsLiveTex().bindService();
        }
    }

    @Override
    public void onBackPressed() {
        MainApplication.IS_ACTIVE = false;
        if (MainApplication.getsLiveTex() != null) {
            MainApplication.getsLiveTex().destroy();
        }
        BusProvider.unregister(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            String currentFragment = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            if (currentFragment != null && (currentFragment.equals(OnlineChatFragment1.class.getName()) || currentFragment.equals(ClientFormFragment.class.getName()))) {
                finish();
                return;
            }
        }

        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        MainApplication.IS_ACTIVE = false;
        if (MainApplication.getsLiveTex() != null) {
            MainApplication.getsLiveTex().destroy();
        }
        super.onStop();
    }


}
