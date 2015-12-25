package nit.livetex.livetexsdktestapp.ui.fragments.offline;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.models.OfflineConversationPersistent;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.ui.dialogs.AttachChooseDialog;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;

import sdk.handler.AHandler;

/**
 * Created by user on 29.07.15.
 */
public class OfflineChatFragment extends BaseChatFragment   {

    OfflineConversationPersistent offlineConversationPersistent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getFragmentEnvironment().getSupportLoaderManager().initLoader(0, null, this);
        offlineConversationPersistent = Dao.getInstance(getContext()).getConversationBy(getConversationId());
    }



    @Subscribe
    public void onMessageReceive(EventMessage eventMessage) {
        switch (eventMessage.getMessageType()) {
            case OFFLINE_MSG_RECEIVED:
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                break;
        }
    }

    @Override
    protected int getLoaderId() {
        return 2;
    }

    @Override
    public boolean onVoteLayoutVisible() {
        return false;
    }

    @Override
    protected void sendFileFromUri(final String path) {
        if (path == null) {
            CommonUtils.showToast(getContext(), "Файл не доступен для загрузки");
        }
        final File file = new File(path);
        showProgress();

        MainApplication.sendOfflineFile(file, getConversationId(), new AHandler<Boolean>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(Boolean result) {
                String name = path.substring(path.lastIndexOf("/") + 1);
                Dao.getInstance(getContext()).saveMessage(name, String.valueOf(System.currentTimeMillis()), Integer.parseInt(getConversationId()), true);
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                dismissProgress();
            }
        });
    }

    @Override
    public void sendMessage(String message) {
        Dao.getInstance(getContext()).saveMessage(message,
                String.valueOf(System.currentTimeMillis()),
                Integer.parseInt(getConversationId()), true);
        getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
        MainApplication.sendOfflineMessage(etInputMsg.getText().toString(), Integer.parseInt(getConversationId()), new AHandler<Boolean>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(Boolean result) {
                Log.d("offline_result", "Result " + result);
            }
        });
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View header = inflater.inflate(R.layout.header_chat1, null);
        ImageView ivAvatarHeader = (ImageView) header.findViewById(R.id.ivAvatarHeader);
       // ImageView ivScreenshot = (ImageView) header.findViewById(R.id.ivScreenshot);
//        ivScreenshot.setColorFilter(getResources().getColor(android.R.color.darker_gray));
//        ivScreenshot.setVisibility(View.VISIBLE);
//
//        ivScreenshot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showProgress();
//                MainApplication.sendScreenshotOffline(getActivity(), getConversationId(), new AHandler<Boolean>() {
//                    @Override
//                    public void onError(String errMsg) {
//
//                    }
//
//                    @Override
//                    public void onResultRecieved(Boolean result) {
//                        dismissProgress();
//                        Dao.getInstance(getContext()).saveMessage("Скриншот отправлен", String.valueOf(System.currentTimeMillis()), Integer.parseInt(getConversationId()), true);
//                        getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
//                    }
//                });
//            }
//        });

        if(actionBarHeight != -1) {
            ivAvatarHeader.getLayoutParams().height = actionBarHeight-26;
            ivAvatarHeader.getLayoutParams().width = actionBarHeight-26;
        }
        if(offlineConversationPersistent != null) {
            Picasso.with(getContext()).load(offlineConversationPersistent.getAvatar())./*resize(40, 40).centerCrop().*/into(ivAvatarHeader);
        }
        TextView tvHeaderTitle = (TextView) header.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(offlineConversationPersistent.getFirstName());
        TextView tvHeaderTyping = (TextView) header.findViewById(R.id.tvHeaderTyping);
        tvHeaderTyping.setVisibility(View.GONE);
        ImageView ivSendFile = (ImageView) header.findViewById(R.id.ivSendFile);
        ivSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AttachChooseDialog dialog = new AttachChooseDialog();
                dialog.setTargetFragment(OfflineChatFragment.this, CHOOSER_DIALOG_REQUEST);
                dialog.show(getFragmentEnvironment().getSupportFragmentManager(), "AttachChooseDialog");
            }
        });
        return header;
    }

    /*@Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file*//*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Выберите файл для загрузки"), Const.CODE.FILE_SELECT);
        } catch(ActivityNotFoundException ex) {
            CommonUtils.showToast(getContext(), "Пожалуйста, установите файловый менеджер");
        }
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.CODE.FILE_SELECT:
                if(data != null) {
                    Uri uri = data.getData();
                    final String path = CommonUtils.getPath(getContext(), uri);
                    if (path == null) {
                        CommonUtils.showToast(getContext(), "Файл не доступен для загрузки");
                    }
                    final File file = new File(path);
                    showProgress();

                    MainApplication.sendOfflineFile(file, getConversationId(), new AHandler<Boolean>() {
                        @Override
                        public void onError(String errMsg) {

                        }

                        @Override
                        public void onResultRecieved(Boolean result) {
                            String name = path.substring(path.lastIndexOf("/") + 1);
                            Dao.getInstance(getContext()).saveMessage(name, String.valueOf(System.currentTimeMillis()), Integer.parseInt(getConversationId()), true);
                            getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                            dismissProgress();
                        }
                    });

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        Loader loader = getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId());
        if(loader != null) {
            loader.forceLoad();
        }
    }
}
