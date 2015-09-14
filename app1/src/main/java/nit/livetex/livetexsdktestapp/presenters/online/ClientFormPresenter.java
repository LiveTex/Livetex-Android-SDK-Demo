package nit.livetex.livetexsdktestapp.presenters.online;

import android.util.Log;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.models.OnlineOperator;
import nit.livetex.livetexsdktestapp.presenters.BasePresenter;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.ui.callbacks.ClientFormCallback;

import java.util.ArrayList;
import java.util.HashMap;

import livetex.dialog.DialogAttributes;
import livetex.dialog_state.DialogState;

import sdk.handler.AHandler;
import sdk.models.LTDepartment;
import sdk.models.LTTextMessage;

/**
 * Created by user on 29.07.15.
 */
public class ClientFormPresenter extends BasePresenter<ClientFormCallback> {

    public ClientFormPresenter(ClientFormCallback callback) {
        super(callback);

    }

    public void process() {

        MainApplication.getDepartments("online", new AHandler<ArrayList<LTDepartment>>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(ArrayList<LTDepartment> departments) {
                if (departments != null && !departments.isEmpty()) {
                    getCallback().onDepartmentsReceived(departments);

                } else {
                    getCallback().onDepartmentsEmpty();
                }
            }
        });

    }

    public void sendToDepartmentOperator(LTDepartment department, final String name, final String livetexId, final String text) {

        DialogAttributes dialogAttributes = new DialogAttributes();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Livetex ID", livetexId);
        dialogAttributes.setVisible(hashMap);

        MainApplication.setName(name);

        MainApplication.getDepartment1(department.getDepartmentId(), dialogAttributes, new AHandler<DialogState>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(DialogState result) {
                Log.d("tag", "");
                MainApplication.getState(new AHandler<DialogState>() {
                    @Override
                    public void onError(String errMsg) {

                    }

                    @Override
                    public void onResultRecieved(DialogState result) {
                        if(result == null || result.getEmployee() == null) {
                            getCallback().onEmployeesEmpty();
                            return;
                        }

                        final String employeeId = result.getEmployee().getEmployeeId();
                        final String operatorFirstName = result.getEmployee().getFirstname();
                        final String avatar = result.getEmployee().getAvatar();


                        MainApplication.sendMsg(text, new AHandler<LTTextMessage>() {
                            @Override
                            public void onError(String errMsg) {
                                Log.d("online", "message not sent ");
                            }

                            @Override
                            public void onResultRecieved(LTTextMessage result) {

                                // if previous dialog was closed, clear cache
                                if(false/*Dao.getInstance(getContext()).hasMessage(employeeId, "CLOSE_DIALOG")*/) {
                                    Dao.getInstance(getContext()).clearConversation(employeeId);
                                }
                                Dao.getInstance(getContext()).saveMessage(text, String.valueOf(System.currentTimeMillis()), Integer.parseInt(employeeId), true);
                                OnlineOperator onlineOperator = new OnlineOperator(employeeId, operatorFirstName, avatar);
                                onlineOperator.toSharedPreferences(getContext());
                                getCallback().createChat(employeeId, avatar, operatorFirstName);
                                Log.d("online", "message sent ");
                            }
                        });
                        Log.d("tag", "");
                    }
                });
            }
        });

    }

}
