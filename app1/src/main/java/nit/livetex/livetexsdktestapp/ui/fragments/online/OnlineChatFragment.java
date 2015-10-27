package nit.livetex.livetexsdktestapp.ui.fragments.online;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import livetex.message.TextMessage;
import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.models.OnlineOperator;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.services.DownloadService;
import nit.livetex.livetexsdktestapp.ui.dialogs.AttachChooseDialog;
import nit.livetex.livetexsdktestapp.ui.dialogs.FileManagerDialog;
import nit.livetex.livetexsdktestapp.ui.fragments.AbuseFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.ChooseModeFragment;
import nit.livetex.livetexsdktestapp.ui.fragments.offline.BaseChatFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import sdk.handler.AHandler;
import sdk.models.LTDialogState;
import sdk.models.LTEmployee;
import sdk.models.LTFileMessage;
import sdk.models.LTTextMessage;

/**
 * Created by user on 31.07.15.
 */
public class OnlineChatFragment extends BaseChatFragment {

    private TextView tvHeaderTyping;

    public final static String CONVERSATION_ID = "conversation_id";
    public final static String AVATAR = "avatar";
    public final static String FIRST_NAME = "first_name";



    private String avatar;
    private String firstName;

    private ImageView ivAvatarHeader;
    private TextView tvHeaderTitle;

    private Handler handler;
    private TypingTask typingTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        typingTask = new TypingTask();

    }

    public class TypingTask implements Runnable {

        @Override
        public void run() {
           // tvHeaderTyping.setVisibility(View.GONE);
            tvHeaderTyping.setText("оператор онлайн");
        }
    }

    private void sendingMessagesEnabled(boolean enabled) {
        etInputMsg.setEnabled(enabled);
        ivSendMsg.setEnabled(enabled);
    }

    @Override
    protected View onCreateView(View v) {
        tvHeaderTyping.setText("оператор онлайн");
        return super.onCreateView(v);
    }

    @Subscribe
    public void onMessageReceive(EventMessage eventMessage) {
        switch (eventMessage.getMessageType()) {
            case UPDATE_STATE:
                LTEmployee employee = (LTEmployee) eventMessage.getSerializable();
                setHeaderData(employee.getAvatar(), employee.getFirstname());
                setConversationId(employee.getEmployeeId());
                sendingMessagesEnabled(true);
                tvHeaderTyping.setText("оператор онлайн");
                Dao.getInstance(getContext()).saveMessage("OPEN_DIALOG",
                        String.valueOf(System.currentTimeMillis()), Integer.parseInt(getConversationId()), false);
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                break;
            case CLOSE:
                Log.d("close", "onMessageReceive");
                //sendingMessagesEnabled(false);
                tvHeaderTyping.setText("оператор оффлайн");
                Dao.getInstance(getContext()).saveMessage("CLOSE_DIALOG",
                        String.valueOf(System.currentTimeMillis()), Integer.parseInt(getConversationId()), false);
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                break;
            case TYPING_MESSAGE:
                tvHeaderTyping.setVisibility(View.VISIBLE);
                tvHeaderTyping.setText("печатает...");
                handler.postDelayed(typingTask, 1500);
                break;
            case RECEIVE_MSG:
                LTTextMessage message = (LTTextMessage) eventMessage.getSerializable();
                OnlineOperator onlineOperator = new OnlineOperator(getContext());
                MainApplication.confirmTxtMsg(message.getId());
                Dao.getInstance(getContext()).saveMessage(message.getText(),
                        String.valueOf(System.currentTimeMillis()), Integer.parseInt(onlineOperator.getId()), false);
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                break;
            case RECEIVE_FILE:
                LTFileMessage fileMessage = (LTFileMessage) eventMessage.getSerializable();
                showProgress();
                File path = new File(Environment.getExternalStorageDirectory(), "Downloadzz");
                if(!path.exists()) {
                    path.mkdirs();
                }
                Log.d("downloadzz", path.getAbsolutePath());
                String[] parts = fileMessage.getText().split("/");
                String fileName = null;
                try {
                    fileName = URLDecoder.decode(parts[parts.length - 1], "UTF-8");
                    File outFile = new File(path, fileName);

                    Dao.getInstance(getContext()).saveMessage(outFile.getAbsolutePath(), String.valueOf(System.currentTimeMillis()),
                            Integer.parseInt(new OnlineOperator(getContext()).getId()), false);

                    Intent intent = new Intent(getContext(), DownloadService.class);
                    intent.putExtra("url", fileMessage.getText());
                    intent.putExtra("receiver", new DownloadReceiver(new Handler()));
                    intent.putExtra("outFile", outFile);
                    getContext().startService(intent);
                    getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    protected void onExtrasParsed(Bundle extra) {
        conversationId = extra.getString(CONVERSATION_ID);
        avatar = extra.getString(AVATAR);
        firstName = extra.getString(FIRST_NAME);
    }

    @Override
    protected int getLoaderId() {
        return 1;
    }

    private void setHeaderData(String avatar, String firstName) {
        Picasso.with(getContext()).load(avatar).resize(50, 50).centerCrop().into(ivAvatarHeader);
        tvHeaderTitle.setText(firstName);
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View header = inflater.inflate(R.layout.header_chat1, null);
        ivAvatarHeader = (ImageView) header.findViewById(R.id.ivAvatarHeader);
        ImageView ivAbuseSend = (ImageView) header.findViewById(R.id.ivAbuseSend);
        ivAbuseSend.setVisibility(View.VISIBLE);

        ivAbuseSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new AbuseFragment());
            }
        });
        tvHeaderTyping = (TextView) header.findViewById(R.id.tvHeaderTyping);
        if(actionBarHeight != -1) {
            ivAvatarHeader.getLayoutParams().height = actionBarHeight-26;
            ivAvatarHeader.getLayoutParams().width = actionBarHeight-26;
        }

        Picasso.with(getContext()).load(avatar)./*resize(50, 50).centerCrop().*/into(ivAvatarHeader);
        tvHeaderTitle = (TextView) header.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(firstName);

        ImageView ivSendFile = (ImageView) header.findViewById(R.id.ivSendFile);
        ivSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AttachChooseDialog dialog = new AttachChooseDialog();
                dialog.setTargetFragment(OnlineChatFragment.this, CHOOSER_DIALOG_REQUEST);
                dialog.show(getFragmentEnvironment().getSupportFragmentManager(), "AttachChooseDialog");
            }
        });


        return header;
    }


    @Override
    public void onResume() {
        super.onResume();

        MainApplication.getMsgHistory(10, 0, new AHandler<List<TextMessage>>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(List<TextMessage> result) {
                for (TextMessage message : result) {
                    MainApplication.confirmTxtMsg(message.getId());
                }

            }
        });

        Loader loader = getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId());
        if(loader != null) {
            loader.forceLoad();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if(view.getId() == R.id.ivVoteUp) {
            MainApplication.vote(true, new AHandler() {
                @Override
                public void onError(String errMsg) {

                }

                @Override
                public void onResultRecieved(Object result) {
                    CommonUtils.showToast(getContext(), "Спасибо за оценку");
                }
            });
        } else if(view.getId() == R.id.ivVoteDown) {
            MainApplication.vote(false, new AHandler() {
                @Override
                public void onError(String errMsg) {

                }

                @Override
                public void onResultRecieved(Object result) {
                    CommonUtils.showToast(getContext(), "Спасибо за оценку");
                }
            });
        }
    }

    @Override
    public void sendTyping(String text) {
        super.sendTyping(text);
        MainApplication.typing(text);
    }

    @Override
    public void sendMessage(String message) {

        Dao.getInstance(getContext()).saveMessage(message, String.valueOf(System.currentTimeMillis()), Integer.parseInt(new OnlineOperator(getContext()).getId()), true);
        getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();

        MainApplication.sendMsg(message, new AHandler<LTTextMessage>() {
            @Override
            public void onError(String errMsg) {
                Log.d("online", "message not sent ");
            }

            @Override
            public void onResultRecieved(LTTextMessage result) {
                Log.d("online", "message sent ");
            }
        });
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

    public void sendFileFromUri(final String path) {
       // final Uri uriPath = CommonUtils.getRealPathFromURI(getContext(), uri);
       // final String path = uri.toString();

        if(path == null) {
            CommonUtils.showToast(getContext(), "Файл не доступен для загрузки");
            return;
        }
       // String decodedPath = URLDecoder.decode(path, "UTF-8");
        final File file = new File(path);
        showProgress();
        MainApplication.sendFile(file, conversationId, new AHandler<Boolean>() {
            @Override
            public void onError(String errMsg) {
                Log.d("tag", errMsg);
            }

            @Override
            public void onResultRecieved(Boolean result) {
                dismissProgress();
                String name = path.substring(path.lastIndexOf("/") + 1);
                Dao.getInstance(getContext()).saveMessage(name, String.valueOf(System.currentTimeMillis()), Integer.parseInt(new OnlineOperator(getContext()).getId()), true);
                getFragmentEnvironment().getSupportLoaderManager().getLoader(getLoaderId()).forceLoad();
            }
        });

    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt("progress");
                if (progress == 100) {
                    dismissProgress();
                }
            }
        }
    }


}





















