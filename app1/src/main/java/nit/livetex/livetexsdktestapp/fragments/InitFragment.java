package nit.livetex.livetexsdktestapp.fragments;

import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.fragments.callbacks.InitCallback;
import nit.livetex.livetexsdktestapp.fragments.presenters.InitPresenter;
import nit.livetex.livetexsdktestapp.models.BaseMessage;
import nit.livetex.livetexsdktestapp.models.ErrorMessage1;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;

/**
 * Created by user on 28.07.15.
 */
public class InitFragment extends BaseFragment implements InitCallback {

    private InitPresenter presenter;

    @Override
    protected View onCreateView(View v) {
        showProgress();
        MainApplication.clearGlobal(getActivity());
        presenter = new InitPresenter(this);
        presenter.init(Const.APP_ID);
        return super.onCreateView(v);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_init;
    }

    @Subscribe
    public void onEventMessage(EventMessage eventMessage) {
        if (eventMessage.getMessageType() == BaseMessage.TYPE.INIT) {
            onInitComplete(eventMessage.getStringExtra());
        }
    }

    public void onErrorMessage(ErrorMessage1 errorMessage1) {
        if (errorMessage1.getMessageType() == BaseMessage.TYPE.INIT) {
            onInitError(errorMessage1.getStringExtra());
        }
    }

    private void onInitError(String msg) {
        dismissProgress();
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitComplete(String token) {
        dismissProgress();
        showFragment(new ChooseModeFragment());
    }

    @Override
    public void onClear() {
        CommonUtils.showToast(getContext(), "Cache is clear");
    }
}