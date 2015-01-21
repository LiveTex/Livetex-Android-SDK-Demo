package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import livetex.sdk.models.DialogState;
import livetex.sdk.models.FileMessage;
import livetex.sdk.models.HoldMessage;
import livetex.sdk.models.TextMessage;
import livetex.sdk.models.TypingMessage;
import nit.livetex.livetexsdktestapp.adapter.ChatAdapter;
import nit.livetex.livetexsdktestapp.adapter.MessageModel;


public class ChatActivity extends ReinitActivity {

    private static HashMap<CurrentDialogState, String> mStateMsgs = new HashMap<>();
    private static HashMap<CurrentDialogState, String> mStateInfoMsgs = new HashMap<>();

    static {
        mStateMsgs.put(CurrentDialogState.ACTIVE, "Оператор онлайн");
        mStateMsgs.put(CurrentDialogState.QUEUED, "Оператор оффлайн, диалог в очереди");
        mStateMsgs.put(CurrentDialogState.CLOSED, "Диалог закрыт");
        mStateInfoMsgs.put(CurrentDialogState.ACTIVE, "ОЦЕНИТЕ КОНСУЛЬТАЦИЮ");
        mStateInfoMsgs.put(CurrentDialogState.QUEUED, "ОЖИДАЕМ ОПЕРАТОРА");
        mStateInfoMsgs.put(CurrentDialogState.CLOSED, "ДИАЛОГ ЗАКРЫТ");
    }


    public static void show(Activity activity) {
        Intent intent = new Intent(activity, ChatActivity.class);
        activity.startActivity(intent);
    }


    private enum CurrentDialogState{
        ACTIVE,
        QUEUED,
        CLOSED
    }

    private ListView mListView;
    private TextView mOperatorNameTV;
    private ImageView mOperatorAvaIV;

    private ChatAdapter mAdapter;
    private String mOperatorName;
    private String mOperatorAva;
    private boolean isTyping = false;
    private String employeeId = null;
    private CurrentDialogState mDialogState;
    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mOperatorNameTV != null) {
                isTyping = false;
                if (mOperatorName != null)
                    mOperatorNameTV.setText(mOperatorName);
                else
                    mOperatorNameTV.setText("");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initActionBar();
        initViews();
        getDialogState();
    }

    @Override
    protected void onStop() {
        MainApplication.setLastEmployee(employeeId);
        super.onStop();
    }

    private boolean checkRequestAvailable(){
        if (!isInetActive()){
            showToast("Отсутствует интернет соединение. Повторите попытку позже.");
            return false;
        }
        return true;
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        View v = LayoutInflater.from(this).inflate(R.layout.chat_ab, null);
        Resources res = getResources();
        Bitmap src = BitmapFactory.decodeResource(res, R.drawable.avatarka);
        int bmpSize = getResources().getDimensionPixelSize(R.dimen.ava_size);
        src = Bitmap.createScaledBitmap(src, bmpSize, bmpSize, true);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, src);
        dr.setCornerRadius(bmpSize / 2.0f);
        mOperatorNameTV = (TextView) v.findViewById(R.id.operator_name);
        mOperatorAvaIV = (ImageView) v.findViewById(R.id.ava);

        if (!TextUtils.isEmpty(mOperatorAva)) {
            AQuery aQuery = new AQuery(this);
            aQuery.id(mOperatorAvaIV).image(mOperatorAva);
        }

//        mOperatorAvaIV.setImageDrawable(dr);
        if (mOperatorName != null) {
            mOperatorNameTV.setText(mOperatorName);
        }
        int mOperatorAvaVisibility = View.INVISIBLE;
        mOperatorAvaIV.setVisibility(mOperatorAvaVisibility);
        actionBar.setCustomView(v);
    }

    public void setActionBarText(String name) {
        if (mOperatorNameTV == null) return;
        mHandler.removeCallbacks(runnable);
        SpannableString s = new SpannableString(name + "\n" + "печатает...");
        s.setSpan(new RelativeSizeSpan(0.7f), name.length() + 1, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        isTyping = true;
        mOperatorNameTV.setText(s);
        mHandler.postDelayed(runnable, 5000);
    }

    private void initViews() {
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new ChatAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setSelector(android.R.color.transparent);
        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequestAvailable()) return;
                sendMsg();
            }
        });
        ((ImageView) findViewById(R.id.vote_down)).setColorFilter(getResources().getColor(R.color.material_red_500));
        ((ImageView) findViewById(R.id.vote_up)).setColorFilter(getResources().getColor(R.color.material_green_500));
        findViewById(R.id.vote_down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequestAvailable()) return;
                vote(false);
            }
        });
        findViewById(R.id.vote_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequestAvailable()) return;
                vote(true);
            }
        });
        ((EditText) findViewById(R.id.input_msg)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    MainApplication.typing(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void vote(boolean isLike) {
        showProgressDialog("Оценка");
        MainApplication.vote(isLike);
    }

    private void sendMsg() {
        String text = ((EditText) findViewById(R.id.input_msg)).getText().toString();
        if (TextUtils.isEmpty(text)) return;
        MainApplication.sendMsg(text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_claim:
                if (!checkRequestAvailable()) return true;
                if (mDialogState.equals(CurrentDialogState.CLOSED) ||
                        mDialogState.equals(CurrentDialogState.QUEUED))
                    return true;
                livetexLockStop();
                ClaimActivity.show(this);
                return true;
            case R.id.action_close:
                if (!checkRequestAvailable()) return true;
                if (mDialogState.equals(CurrentDialogState.CLOSED)) {
                    showInitActivity();
                } else {
                    MainApplication.closeDialog();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSendMsgConfirmed(String msgId) {
        if (mAdapter != null)
            mAdapter.setMsgChecked(msgId);
    }

    @Override
    protected void onDialogClose(DialogState state) {
        super.onDialogClose(state);
        employeeId = null;
        showInitActivity();
    }

    @Override
    protected void onDialogStateGetted(DialogState state) {
        super.onDialogStateGetted(state);
        onUpdateDialogState(state, false);
        getMsgHistory();
    }

    private void getMsgHistory() {
//        showProgressDialog("Получение истории сообщений");
        MainApplication.getMsgHistory(20, 0);
    }

    @Override
    protected void onMsgHistoryGetted(List<TextMessage> msg) {
        mAdapter.removeAll();
        List<MessageModel> models = new ArrayList<>();
        for (TextMessage textMessage : msg) {
            models.add(new MessageModel(textMessage, true));
        }
        sortMsgList(models);
        mAdapter.addAllMsgs(models);
        addHoldMsgByState();
    }

    private void sortMsgList(List<MessageModel> msg) {
        Collections.sort(msg, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel lhs, MessageModel rhs) {
                if (TextUtils.isEmpty(lhs.timestamp))
                    return -1;
                else if (TextUtils.isEmpty(rhs.timestamp))
                    return 1;
                if (Long.parseLong(lhs.timestamp) < Long.parseLong(rhs.timestamp))
                    return -1;
                else if (Long.parseLong(lhs.timestamp) == Long.parseLong(rhs.timestamp))
                    return 0;
                else if (Long.parseLong(lhs.timestamp) == Long.parseLong(rhs.timestamp))
                    return 1;
                return 0;
            }
        });
    }

    private void addHoldMsgByState(){
        HoldMessage holdMessage = new HoldMessage();
        holdMessage.timestamp = "" + (System.currentTimeMillis() / 1000);
        holdMessage.text = mStateMsgs.get(mDialogState);
        onHoldMsgRecieved(holdMessage);
    }

    @Override
    protected void onMsgSended(TextMessage msg) {
        if (mAdapter != null && msg != null) {
            ((EditText) findViewById(R.id.input_msg)).setText("");
            MessageModel model = new MessageModel(msg);
            mAdapter.addMsg(model);
            mListView.setSelection(mAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onMsgRecieved(TextMessage msg) {
        MainApplication.confirmMsg(msg.id);
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
        if (mAdapter != null) {
            mAdapter.addMsg(new MessageModel(msg));
            mListView.setSelection(mAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onHoldMsgRecieved(HoldMessage holdMessage) {
        if (mAdapter != null) {
            mAdapter.addMsg(new MessageModel(holdMessage));
            mListView.setSelection(mAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onVoted() {
        showToast("Оценка отправлена");
    }

    @Override
    protected void onTyping(TypingMessage typingMessage) {
        if (mOperatorName != null)
            setActionBarText(mOperatorName);
        else
            setActionBarText("Оператор");
    }

    @Override
    protected void onFileRecieved(FileMessage fileMessage) {
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
        if (mAdapter != null) {
            mAdapter.addMsg(new MessageModel(fileMessage));
            mListView.setSelection(mAdapter.getCount() - 1);
        }
    }

    @Override
    protected void onUpdateDialogState(DialogState state) {
        onUpdateDialogState(state, true);
    }

    protected void onUpdateDialogState(DialogState state, boolean withMsg) {
        Log.d("livetex_sdk", "onUpdateDialogState - " + withMsg + " " + state);
        if (state == null) return;
        String operatorName = "";
        int avaVisibility = View.VISIBLE;
        if (state.conversation != null && state.employee != null) {  //Conversation
            operatorName = state.employee.firstname + " " + state.employee.lastname;
            mOperatorAva = state.employee.avatar;
            avaVisibility = View.VISIBLE;
            setMsgInputVisible();

            employeeId = state.employee.employeeId;

            mDialogState = CurrentDialogState.ACTIVE;
        } else if (state.conversation != null) {            //Queued
            avaVisibility = View.GONE;
        //    setMsgInputVisible();
            employeeId = null;

            mDialogState = CurrentDialogState.QUEUED;
        } else if (state.employee == null) {     //No conversation
            operatorName = "Диалог закрыт";
            avaVisibility = View.GONE;
            setMsgInputHide();
            employeeId = null;

            mDialogState = CurrentDialogState.CLOSED;
        }
        if (withMsg) {
            addHoldMsgByState();
        }
        setInfoMsg();
        mOperatorName = operatorName;
        if (mOperatorNameTV != null) {
            if (isTyping)
                setActionBarText(operatorName);
            else
                mOperatorNameTV.setText(operatorName);
        }

        if (mOperatorAvaIV != null) {
            if (!TextUtils.isEmpty(mOperatorAva)) {
                AQuery aQuery = new AQuery(this);
                aQuery.id(mOperatorAvaIV).image(mOperatorAva);
            }
            mOperatorAvaIV.setVisibility(avaVisibility);
        }
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(android.R.id.list).getLayoutParams();
//        if (avaVisibility == View.GONE) {
//            params.addRule(RelativeLayout.ABOVE, 0);
//        }
    }

    private void setMsgInputVisible(){
        findViewById(R.id.msg_ll).setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.divider).getLayoutParams();
        params.addRule(RelativeLayout.ABOVE, R.id.msg_ll);
    }

    private void setMsgInputHide(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.divider).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        findViewById(R.id.msg_ll).setVisibility(View.GONE);
    }

    private void setInfoMsg(){
        int visibility;
        ((TextView)findViewById(R.id.status_tv)).setText(mStateInfoMsgs.get(mDialogState));
        if (mDialogState.equals(CurrentDialogState.ACTIVE)){
            findViewById(R.id.vote_down).setEnabled(true);
            findViewById(R.id.vote_up).setEnabled(true);
            visibility = View.VISIBLE;
        } else {
            findViewById(R.id.vote_down).setEnabled(false);
            findViewById(R.id.vote_up).setEnabled(false);
            if (mDialogState.equals(CurrentDialogState.QUEUED)){
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
        }
        findViewById(R.id.vote_ll).setVisibility(visibility);
    }
}
