package nit.livetex.livetexsdktestapp.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.fragments.callbacks.ClientFormCallback;
import nit.livetex.livetexsdktestapp.fragments.presenters.ClientFormPresenter;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import nit.livetex.livetexsdktestapp.view.CustomEditText;

import java.util.List;

import livetex.queue_service.Destination;
import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 29.07.15.
 */
public class ClientFormFragment extends BaseFragment implements ClientFormCallback, View.OnClickListener {

    EditText etWelcomeName;
    Button btnCreateOnlineDialog;
    Destination destination;

    private ClientFormPresenter presenter;

    @Override
    protected boolean onActionBarVisible() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_client_form;
    }

    private void init(View v) {
        etWelcomeName = (CustomEditText) v.findViewById(R.id.etWelcomeName);
        btnCreateOnlineDialog = (Button) v.findViewById(R.id.btnCreateOnlineDialog);
        btnCreateOnlineDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateOnlineDialog:
                String welcomeName = "";
                if(!CommonUtils.isEmpty(etWelcomeName)) {
                    welcomeName = etWelcomeName.getText().toString();
                }
                showProgress();
                presenter.sendToDestination(getContext(), destination, welcomeName, this);
                break;
        }
    }

    @Override
    protected View onCreateView(View v) {
        init(v);
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
    }

    @Override
    public void onDepartmentsReceived(List<LTDepartment> departments) {
    }

    @Override
    public void onDestinationsReceived(List<Destination> destinations) {
        if(destinations != null && !destinations.isEmpty()) {
            destination = destinations.get(0);
        }
    }

    @Override
    public void createChat(String conversationId, String avatar, String firstName) {
        dismissProgress();
        Bundle bundle = new Bundle();
        showFragment(new OnlineChatFragment1(), bundle);
        CommonUtils.clear(etWelcomeName);
    }

    @Override
    public void createChat() {
        dismissProgress();
        showFragment(new OnlineChatFragment1());
        CommonUtils.clear(etWelcomeName);
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























