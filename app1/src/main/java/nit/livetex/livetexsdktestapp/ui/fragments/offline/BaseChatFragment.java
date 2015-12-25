package nit.livetex.livetexsdktestapp.ui.fragments.offline;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.adapters.ChatAdapter;
import nit.livetex.livetexsdktestapp.providers.ConversationsProvider;
import nit.livetex.livetexsdktestapp.providers.MessagesProvider;
import nit.livetex.livetexsdktestapp.ui.dialogs.AttachChooseDialog;
import nit.livetex.livetexsdktestapp.ui.dialogs.FileManagerDialog;
import nit.livetex.livetexsdktestapp.ui.fragments.BaseFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;

/**
 * Created by user on 28.07.15.
 */
public abstract class BaseChatFragment extends BaseFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    protected EditText etInputMsg;
    protected ImageView ivVoteUp;
    protected ImageView ivVoteDown;
    protected ImageButton ivAddFile;
    protected ImageView ivSendMsg;
    private ListView lvChat;

    private ChatAdapter adapter;

    protected String conversationId;

    public static final int CAPTURE_IMAGE_REQUEST = 0;
    public static final int SELECT_PICTURE_REQUEST = 1;


    public static final int CHOOSER_DIALOG_REQUEST = 191;
    public static final int CHOOSER_FILE_REQUEST = 192;

    private Uri mTakenPhotoUri;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public void onClick(View view) {
        if(R.id.ivSendMsg == view.getId()) {
            if(!CommonUtils.isEmpty(etInputMsg)) {
                sendMessage(etInputMsg.getText().toString());
                etInputMsg.setText("");
            } else {
                CommonUtils.showToast(getContext(), "Введите сообщение");
            }
        }
    }

    private void init(View v) {
        lvChat = (ListView) v.findViewById(android.R.id.list);
        ivSendMsg = (ImageView) v.findViewById(R.id.ivSendMsg);
        ivSendMsg.setColorFilter(getResources().getColor(R.color.new_blue));
        etInputMsg = (EditText) v.findViewById(R.id.etInputMsg);
        ivVoteUp = (ImageView) v.findViewById(R.id.ivVoteUp);
        ivVoteDown = (ImageView) v.findViewById(R.id.ivVoteDown);
       // ivAddFile = (ImageButton) v.findViewById(R.id.ivAddFile);
        ivSendMsg.setOnClickListener(this);
        ivVoteDown.setOnClickListener(this);
        ivVoteUp.setOnClickListener(this);
       // ivAddFile.setOnClickListener(this);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onExtrasParsed(Bundle extra) {
        conversationId = extra.getString(ConversationsProvider.CONVERSATION_ID);
    }




    @Override
    protected View onCreateView(View v) {
        init(v);
        ivVoteDown.setColorFilter(getResources().getColor(R.color.new_red));
        ivVoteUp.setColorFilter(getResources().getColor(R.color.new_green));
        RelativeLayout rlVote = (RelativeLayout) v.findViewById(R.id.rlVote);

        initTextWatcher();

        if(onVoteLayoutVisible()) {
            rlVote.setVisibility(View.VISIBLE);
        }
        adapter = new ChatAdapter(getContext());
        //getFragmentEnvironment().getSupportLoaderManager().initLoader(getLoaderId(), null, this);
        getFragmentEnvironment().getSupportLoaderManager().restartLoader(getLoaderId(), null, this);
        lvChat.setAdapter(adapter);

        return super.onCreateView(v);
    }

    protected abstract int getLoaderId();

    public boolean onVoteLayoutVisible() {
        return true;
    }

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

/*
    public void sendFile() {

    }*/




    public void takePictureByCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTakenPhotoUri = CommonUtils.getOutputMediaFile(getFragmentEnvironment());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakenPhotoUri);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    public void takeGalleryPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE_REQUEST);
    }

    public void takeFile() {
        FileManagerDialog dialog = new FileManagerDialog();
        dialog.setTargetFragment(this, CHOOSER_FILE_REQUEST);
        dialog.show(getFragmentEnvironment().getSupportFragmentManager(), "FileManagerDialog");
    }

    protected abstract void sendFileFromUri(String path);


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.CODE.FILE_SELECT:
                if(data != null) {
                    Uri uri = data.getData();
                    sendFileFromUri(CommonUtils.getPath(getContext(), uri));
                }
                break;
            case CHOOSER_DIALOG_REQUEST:
                if(resultCode == AttachChooseDialog.TAKE_FILE) {
                    takeFile();
                } else if(resultCode == AttachChooseDialog.TAKE_PICTURE_BY_CAM) {
                    takePictureByCam();
                } else if(resultCode == AttachChooseDialog.TAKE_GALLERY_PICTURE) {
                    takeGalleryPicture();
                }
                break;
            case SELECT_PICTURE_REQUEST:
                if(data != null) {
                    Uri uri = data.getData();
                    Uri pathUri = CommonUtils.getRealPathFromURI(getContext(), uri);
                    if(pathUri != null) {
                        sendFileFromUri(pathUri.toString());
                    } else {
                        CommonUtils.showToast(getContext(), "Пожалуйста,выберите другой файл");
                    }

                }
                break;
            case CHOOSER_FILE_REQUEST:
                if(resultCode == FileManagerDialog.TAKE_FILE_URI) {
                    if(data != null) {
                        Uri uri = data.getData();
                        sendFileFromUri(CommonUtils.getPath(getContext(), uri));
                    }
                }
                break;
            case CAPTURE_IMAGE_REQUEST:
         //       CommonUtils.galleryAddPic(getContext(), mTakenPhotoUri);
                sendFileFromUri(CommonUtils.getPath(getContext(), mTakenPhotoUri));
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        getFragmentEnvironment().getSupportActionBar().setDisplayShowCustomEnabled(false);
        super.onDestroyView();

    }

    public void sendTyping(String text) {

    }

    public void initTextWatcher() {
        etInputMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d("tag", charSequence.toString() + " " + " " + start + " " + before + " " + count);
                if(charSequence.length() != 0) {
                    sendTyping(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public abstract void sendMessage(String message);

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri uri = Uri.withAppendedPath(MessagesProvider.URI_DATA, MessagesProvider.CONVERSATION_ID + "/" + getConversationId());
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            adapter.swapCursor(cursor);
            setSelectionOnLastEntry();

        // getLvChat().setSelection(getChatAdapter().getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    protected void setSelectionOnLastEntry() {
        lvChat.setSelection(adapter.getCount());
    }

}

















