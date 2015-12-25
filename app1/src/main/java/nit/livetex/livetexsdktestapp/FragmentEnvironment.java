package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;


import livetex.message.TextMessage;
import nit.livetex.livetexsdktestapp.models.OnlineOperator;
import nit.livetex.livetexsdktestapp.ui.fragments.ChooseModeFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.InitFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.offline.OfflineChatFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.online.OnlineChatFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

import java.util.List;

import sdk.handler.AHandler;
import sdk.models.LTDialogState;
import sdk.models.LTTextMessage;


public class FragmentEnvironment extends ActionBarActivity {

    private static int tapToGetOut = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.material_blue_900));
        }
        setContentView(R.layout.activity_main);
        init();

    }

    private boolean isAwakenByPush() {
        if(Const.PUSH_ONLINE_ACTION.equals(getIntent().getAction())) {
            final OnlineOperator onlineOperator = new OnlineOperator(this);
            String appID = DataKeeper.restoreAppId(this);
            String regID = DataKeeper.restoreRegId(this);
            String mes = getIntent().getStringExtra("mes");

            MainApplication.initLivetex(appID, regID, new AHandler<Boolean>() {
                @Override
                public void onError(String errMsg) {

                }

                @Override
                public void onResultRecieved(Boolean result) {
                    MainApplication.requestDialogByEmployee(onlineOperator.getId(), "", new AHandler<LTDialogState>() {
                        @Override
                        public void onError(String errMsg) {

                        }

                        @Override
                        public void onResultRecieved(LTDialogState result) {
                            MainApplication.getMsgHistory(10, 0, new AHandler<List<TextMessage>>() {
                                @Override
                                public void onError(String errMsg) {

                                }

                                @Override
                                public void onResultRecieved(List<TextMessage> result) {
                                    for(TextMessage message : result) {
                                        MainApplication.confirmTxtMsg(message.getId());
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString(OnlineChatFragment.CONVERSATION_ID, onlineOperator.getId());
                                    bundle.putString(OnlineChatFragment.AVATAR, onlineOperator.getAvatar());
                                    bundle.putString(OnlineChatFragment.FIRST_NAME, onlineOperator.getName());
                                    OnlineChatFragment onlineChatFragment = new OnlineChatFragment();
                                    onlineChatFragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, onlineChatFragment).commit();
                                }
                            });

                        }
                    });
                }
            });
            return true;
        } else if(Const.PUSH_OFFLINE_ACTION.equals(getIntent().getAction())) {

            String appID = DataKeeper.restoreAppId(this);
            String regID = DataKeeper.restoreRegId(this);
            MainApplication.initLivetex(appID, regID, new AHandler<Boolean>() {
                @Override
                public void onError(String errMsg) {
                }

                @Override
                public void onResultRecieved(Boolean result) {
                    Bundle bundle = getIntent().getExtras();
                    OfflineChatFragment offlineChatFragment = new OfflineChatFragment();
                    offlineChatFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, offlineChatFragment).commit();
                }
            });
            return true;
        }
        return false;
    }

    public void init() {
        if(!isAwakenByPush()) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new InitFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.IS_ACTIVE = true;
        if(MainApplication.getsLiveTex() != null) {
            MainApplication.getsLiveTex().bindService();
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0) {
            String name1 = ChooseModeFragment.class.getName();
            String nameOnlineChat = OnlineChatFragment.class.getName();
            String name2 = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName();
            if(name1 != null && name2 != null && name1.equals(name2)) {
                if(tapToGetOut == 0) {
                    CommonUtils.showToast(this, "Для выхода из приложения нажмите еще раз кнопку назад");
                    tapToGetOut++;
                } else {
                    tapToGetOut = 0;
                    finish();
                }
                return;
            } else if(nameOnlineChat != null && name2 != null && nameOnlineChat.equals(name2)) {
                MainApplication.closeDialog(new AHandler<LTDialogState>() {
                    @Override
                    public void onError(String errMsg) {
                    }

                    @Override
                    public void onResultRecieved(LTDialogState result) {
                        CommonUtils.showToast(FragmentEnvironment.this, "Диалог закрыт");
                    }
                });
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
        if(MainApplication.getsLiveTex() != null) {
            MainApplication.getsLiveTex().destroy();
        }
        super.onStop();
    }

}
