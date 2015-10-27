package nit.livetex.livetexsdktestapp.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import nit.livetex.livetexsdktestapp.FragmentEnvironment;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.utils.BusProvider;

/**
 * Created by user on 28.07.15.
 */
public abstract class BaseFragment extends Fragment {

    protected ProgressDialog progressDialog;
    protected Handler handler;
    protected Handler threadHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Пожалуйста, подождите");
        handler = new Handler();
        HandlerThread thread = new HandlerThread("");
        thread.start();
        threadHandler = new Handler(thread.getLooper());
        if(getArguments() != null) {
            onExtrasParsed(getArguments());
        }

    }

    protected void setSoftInputMode() {
        getFragmentEnvironment().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected boolean onActionBarVisible() {
        return false;
    }

    protected void onExtrasParsed(Bundle extra) {

    }

    protected void showProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }

    protected void dismissProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    protected void showFragment(BaseFragment fragment) {
        getFragmentEnvironment().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).commit();
    }

    protected void showFragment(BaseFragment fragment, boolean addToBackstack) {
        if(addToBackstack) {
            getFragmentEnvironment().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).commit();
        } else {
            showFragment(fragment);
        }

    }

    protected void showFragment(BaseFragment fragment, Bundle extra, boolean addToBackstack) {
        fragment.setArguments(extra);
        showFragment(fragment, addToBackstack);
    }

    protected void showFragment(BaseFragment fragment, Bundle extra) {
        fragment.setArguments(extra);
        showFragment(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setSoftInputMode();
        View view = inflater.inflate(getLayoutId(), container, false);

        if(!onActionBarVisible()) {
            getFragmentEnvironment().getSupportActionBar().hide();
        } else {
            setHasOptionsMenu(true);
            getFragmentEnvironment().getSupportActionBar().show();
            addActionBar();
        }
        return onCreateView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public Handler getHandler() {
        return handler;
    }

    public Handler getThreadHandler() {
        return threadHandler;
    }

    protected View onCreateView(View v) {
        return v;
    }

    protected abstract int getLayoutId();

    public Context getContext() {
        return getActivity();
    }

    public FragmentEnvironment getFragmentEnvironment() {
        return (FragmentEnvironment) getActivity();
    }

    public void addActionBar() {
        TypedValue tv = new TypedValue();
        int actionBarHeight = -1;
        if (getFragmentEnvironment().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        getFragmentEnvironment().getSupportActionBar().setDisplayShowCustomEnabled(true);
        getFragmentEnvironment().getSupportActionBar().setTitle("");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        getFragmentEnvironment().getSupportActionBar().setCustomView(getCustomActionBarView(inflater, actionBarHeight));
    }

    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight){
        return null;
    }
}

