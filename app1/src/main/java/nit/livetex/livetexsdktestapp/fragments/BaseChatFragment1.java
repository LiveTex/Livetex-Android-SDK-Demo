package nit.livetex.livetexsdktestapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.adapters.ChatArrayAdapter;
import nit.livetex.livetexsdktestapp.fragments.dialogs.FileManagerDialog;
import nit.livetex.livetexsdktestapp.utils.FileUtils;
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

    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void onClick(View view) {
        if (R.id.ivSendMsg == view.getId()) {
            if (!LivetexUtils.isEmpty(etInputMsg)) {
                sendMessage(etInputMsg.getText().toString());
                etInputMsg.setText("");
            } else {
                LivetexUtils.showToast(getContext(), "Введите сообщение");
            }
        } else if (R.id.ivVoteUp == view.getId()) {

        } else if (R.id.ivVoteDown == view.getId()) {

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
        mTakenPhotoUri = FileUtils.getOutputMediaFile(getFragmentEnvironment());
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

    protected abstract void sendFile(String path);

    @Override
    public void onDestroyView() {
        getFragmentEnvironment().getSupportActionBar().setDisplayShowCustomEnabled(false);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void sendTyping(String text) {
        MainApplication.sendTextMessageTyping(text);
    }

    private final long DELAY = 500; // milliseconds
    private PublishSubject<String> textSubject = PublishSubject.create();

    private void initTextWatcher() {
        Disposable disposable = textSubject
                .throttleLast(DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::sendTyping);
        disposables.add(disposable);

        etInputMsg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString().trim();
                // Send typing event, not faster than DELAY
                if (!TextUtils.isEmpty(text)) {
                    textSubject.onNext(text);
                }
            }
        });
    }

    protected void setSelectionOnLastEntry() {
        lvChat.setSelection(adapter.getCount());
    }
}
