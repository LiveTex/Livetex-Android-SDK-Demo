package nit.livetex.livetexsdktestapp.ui.fragments.online;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.adapters.DepartmentsAdapter;
import nit.livetex.livetexsdktestapp.presenters.online.ClientFormPresenter;
import nit.livetex.livetexsdktestapp.ui.callbacks.ClientFormCallback;
import nit.livetex.livetexsdktestapp.ui.fragments.BaseFragment;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import nit.livetex.livetexsdktestapp.view.CustomEditText;
import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 29.07.15.
 */
public class ClientFormFragment extends BaseFragment implements ClientFormCallback, View.OnClickListener {

    EditText etWelcomeName;
    Spinner spDepartments;
   // EditText etLivetexId;
    EditText etMessage;
    Button btnCreateOnlineDialog;

    private DepartmentsAdapter spAdapter;
    private ClientFormPresenter presenter;

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View v = inflater.inflate(R.layout.header_abuse, null);
        ImageView ivBackToDialog = (ImageView) v.findViewById(R.id.ivBackToDialog);
        ivBackToDialog.setVisibility(View.INVISIBLE);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("Начать чат");
        return v;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_client_form;
    }

    private void init(View v) {
        etWelcomeName = (CustomEditText) v.findViewById(R.id.etWelcomeName);
    //    etLivetexId = (CustomEditText) v.findViewById(R.id.etLivetexId);
        etMessage = (CustomEditText) v.findViewById(R.id.etMessage);
        btnCreateOnlineDialog = (Button) v.findViewById(R.id.btnCreateOnlineDialog);
        btnCreateOnlineDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnCreateOnlineDialog) {
            String livetexId = "";
            String welcomeName = "";
               /* if(!CommonUtils.isEmpty(etLivetexId)) {
                    livetexId = etLivetexId.getText().toString();
                }*/
            if(!CommonUtils.isEmpty(etWelcomeName)) {
                welcomeName = etWelcomeName.getText().toString();
            }
            if(CommonUtils.isEmpty(etMessage)) {
                CommonUtils.showToast(getContext(), "Введите сообщение");
                return;
            }
            if(spDepartments.getSelectedItem() == null) {
                CommonUtils.showToast(getContext(), "Нет доступных операторов");
                return;
            }
            showProgress();
            LTDepartment department = (LTDepartment) spDepartments.getSelectedItem();
            presenter.sendToDepartmentOperator(department, welcomeName, livetexId, etMessage.getText().toString());
        }

    }

    @Override
    protected View onCreateView(View v) {
        init(v);
        spDepartments = (Spinner) v.findViewById(R.id.spDepartments);
        spAdapter = new DepartmentsAdapter(getContext());
        spDepartments.setAdapter(spAdapter);
        presenter = new ClientFormPresenter(this);
        presenter.process();
        return super.onCreateView(v);
    }

    @Override
    protected void setSoftInputMode() {
        getFragmentEnvironment().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onEmployeesReceived(List<LTEmployee> operators) {
        //spAdapter.setData(operators);
    }

    @Override
    public void onDepartmentsReceived(List<LTDepartment> departments) {
        ArrayList<LTDepartment> newDepartments = new ArrayList<LTDepartment>();
        for(LTDepartment department : departments) {
            if(!"117490".equals(department.getDepartmentId())) {
                newDepartments.add(department);
            }
        }
        spAdapter.setData(newDepartments);
    }

    @Override
    public void createChat(String conversationId, String avatar, String firstName) {
        dismissProgress();
        Bundle bundle = new Bundle();
        bundle.putString(OnlineChatFragment.CONVERSATION_ID, conversationId);
        bundle.putString(OnlineChatFragment.AVATAR, avatar);
        bundle.putString(OnlineChatFragment.FIRST_NAME, firstName);
        showFragment(new OnlineChatFragment(), bundle);
        CommonUtils.clear(etMessage, etWelcomeName);;
    }

    @Override
    public void onEmployeesEmpty() {
        dismissProgress();
        CommonUtils.showToast(getContext(), "Нет доступных операторов");
    }

    @Override
    public void onDepartmentsEmpty() {
        dismissProgress();
        CommonUtils.showToast(getContext(), "Нет доступных департаментов");
    }


}























