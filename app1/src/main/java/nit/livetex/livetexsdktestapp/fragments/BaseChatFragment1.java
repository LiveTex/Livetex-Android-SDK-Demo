package nit.livetex.livetexsdktestapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.adapters.ChatArrayAdapter;
import nit.livetex.livetexsdktestapp.fragments.dialogs.FileManagerDialog;
import nit.livetex.livetexsdktestapp.utils.LivetexUtils;

/**
 * Created by dev on 02.06.16.
 */
public abstract class BaseChatFragment1 extends BaseFragment implements View.OnClickListener {

    protected EditText etInputMsg;
    protected ImageView ivVoteUp;
    protected ImageView ivVoteDown;
    protected ImageView ivSendMsg;
    protected ListView lvChat;
    protected ProgressBar pbHistory;

    protected ChatArrayAdapter adapter;

    protected String conversationId;

    public static final int CAPTURE_IMAGE_REQUEST = 0;
    public static final int SELECT_PICTURE_REQUEST = 1;


    public static final int CHOOSER_DIALOG_REQUEST = 191;
    public static final int CHOOSER_FILE_REQUEST = 192;

    protected Uri mTakenPhotoUri;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onClick(View view) {
        if(R.id.ivSendMsg == view.getId()) {
            if(!LivetexUtils.isEmpty(etInputMsg)) {
                sendMessage(etInputMsg.getText().toString());
                etInputMsg.setText("");
            } else {
                LivetexUtils.showToast(getContext(), "Введите сообщение");
            }
        } else if(R.id.ivVoteUp == view.getId()) {

        } else if(R.id.ivVoteDown == view.getId()) {

        }
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        ivVoteDown.setColorFilter(getResources().getColor(R.color.new_red));
        ivVoteUp.setColorFilter(getResources().getColor(R.color.new_green));
        RelativeLayout rlVote = (RelativeLayout) v.findViewById(R.id.rlVote);
        initTextWatcher();
        adapter = new ChatArrayAdapter(getContext());
        lvChat.setAdapter(adapter);

        return super.onCreateView(v);
    }

    private void init(View v) {
        pbHistory = (ProgressBar) v.findViewById(R.id.pb_history_download);
        lvChat = (ListView) v.findViewById(android.R.id.list);
        ivSendMsg = (ImageView) v.findViewById(R.id.ivSendMsg);
        etInputMsg = (EditText) v.findViewById(R.id.etInputMsg);
        ivVoteUp = (ImageView) v.findViewById(R.id.ivVoteUp);
        ivVoteDown = (ImageView) v.findViewById(R.id.ivVoteDown);
        ivSendMsg.setOnClickListener(this);
        ivVoteDown.setOnClickListener(this);
        ivVoteUp.setOnClickListener(this);
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public abstract void sendMessage(String message);

    public boolean onVoteLayoutVisible() {
        return true;
    }

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

    public void takePictureByCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTakenPhotoUri = LivetexUtils.getOutputMediaFile(getFragmentEnvironment());
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
                if (charSequence.length() != 0) {
                    sendTyping(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void setSelectionOnLastEntry() {
        lvChat.setSelection(adapter.getCount());
    }
}
