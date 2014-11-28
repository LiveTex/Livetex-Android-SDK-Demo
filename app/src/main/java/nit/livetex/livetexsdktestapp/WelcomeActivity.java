package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import livetex.sdk.models.Department;
import livetex.sdk.models.DialogState;
import livetex.sdk.models.Employee;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private EditText mMsgEt, mNameEt;
    private Spinner mSpinner;
    private Button mSendBtn;
    private RadioGroup mRadioGroup;
    private HintAdapter mAdapter;
    private List<Department> departmentList = new ArrayList<>();
    private List<Employee> employeeList = new ArrayList<>();

    public static void show(Activity activity) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        initViewProperties();
    }

    private void initViews() {
        mMsgEt = (EditText) findViewById(R.id.msg);
        mNameEt = (EditText) findViewById(R.id.name);
        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        mSpinner.setAdapter(buildAdapter(new ArrayList<>()));
        mSendBtn = (Button) findViewById(R.id.send_btn);
        mRadioGroup = (RadioGroup) findViewById(R.id.search_type);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_site:
                        mSpinner.setVisibility(View.GONE);
                        break;
                    case R.id.radio_dep:
                        loadDepartments();
                        mSpinner.setVisibility(View.VISIBLE);
                        mAdapter.setModels(departmentList);
                        break;
                    case R.id.radio_op:
                        loadEmployees();
                        mSpinner.setVisibility(View.VISIBLE);
                        mAdapter.setModels(employeeList);
                        break;
                }
            }
        });
    }

    private void initViewProperties() {
        mSendBtn.setOnClickListener(this);
    }

    private void loadDepartments() {
        showProgressDialog("Получение списка отделов");
        MainApplication.getDepartments();
    }

    private void loadEmployees() {
        showProgressDialog("Получение списка операторов");
        MainApplication.getOperators();
    }

    private BaseAdapter buildAdapter(List<Object> models) {
        mAdapter = new HintAdapter(this, models);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return mAdapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
//                setName();
                requestDialog();
                break;
        }
    }

    private void setName(){
        if (mNameEt.getText() == null || TextUtils.isEmpty(mNameEt.getText().toString())) {
            showToast("Введите имя");
            return;
        }
        showProgressDialog("Устанавливается имя");
        MainApplication.setName(mNameEt.getText().toString());
    }

    private void requestDialog() {
        showProgressDialog("Получение диалога");
        MainApplication.requestDialog(mSpinner.getSelectedItemPosition() == 0 ?
                null : (Department) mSpinner.getSelectedItem());
    }

    private void sendMsg(final String txt) {
        MainApplication.sendMsg(txt);
        try {
            unregisterReceiver(mReciever);
        } catch (IllegalArgumentException ignored){
        }
        ChatActivity.show(WelcomeActivity.this);
    }

    @Override
    protected void onNameSetted() {
        requestDialog();
    }

    @Override
    protected void departmentsRecieved(List<Department> departments) {
        departmentList = departments;
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_dep) {
            mAdapter.setModels(departmentList);
        }
    }

    @Override
    protected void employeesRecieved(List<Employee> result) {
        employeeList = result;
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_op) {
            mAdapter.setModels(employeeList);
        }
    }

    @Override
    protected void dialogRecieved(DialogState state) {
        if (mMsgEt != null && !TextUtils.isEmpty(mMsgEt.getText().toString())) {
            showProgressDialog("Отправка сообщения");
            sendMsg(mMsgEt.getText().toString());
        } else {
            try {
                unregisterReceiver(mReciever);
            } catch (IllegalArgumentException ignored){
            }
            ChatActivity.show(WelcomeActivity.this);
        }
    }

    private static class  HintAdapter <T extends Object> extends ArrayAdapter<T> {

        private List<T> models = new ArrayList<>();

        public HintAdapter(Context context, List<T> models) {
            super(context, android.R.layout.simple_spinner_dropdown_item);
            this.models = models;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = LayoutInflater.from(getContext()).inflate(R.layout.spinner_view, parent, false);
            ((TextView) v.findViewById(android.R.id.text1)).setText(getText(position));
            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            ((TextView) v.findViewById(android.R.id.text1)).setText(getText(position));

            return v;
        }

        public void setModels(List<T> models) {
            this.models = models;
            notifyDataSetChanged();
        }

        private String getText(int pos) {
            Object obj = models.get(pos);
            if (obj instanceof Employee) {
                return getEmployeName((Employee) obj);
            } else {
                return ((Department) obj).getName();
            }
        }

        private String getEmployeName(Employee e) {
            return e.getFirstname() + " " + e.getLastname();
        }

        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public T getItem(int position) {
            return models.get(position);
        }
    }
}
