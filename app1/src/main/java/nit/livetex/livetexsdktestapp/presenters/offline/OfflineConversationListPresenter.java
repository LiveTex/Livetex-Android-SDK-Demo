package nit.livetex.livetexsdktestapp.presenters.offline;

import android.util.Log;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.presenters.BasePresenter;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.ui.callbacks.OfflineConversationListCallback;

import java.util.ArrayList;
import java.util.List;

import Clients.Enums.OfflineConversation;
import Clients.Enums.OfflineMessage;
import livetex.employee.Employee;
import sdk.handler.AHandler;
import sdk.models.LTEmployee;

/**
 * Created by user on 28.07.15.
 */
public class OfflineConversationListPresenter extends BasePresenter<OfflineConversationListCallback> {

    public OfflineConversationListPresenter(OfflineConversationListCallback callback) {
        super(callback);
    }

    public void fetchConversationData() {
        MainApplication.getOfflineConversations(new AHandler<ArrayList<OfflineConversation>>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(ArrayList<OfflineConversation> result) {
                if(result != null && result.size() != 0) {
                    for(final OfflineConversation conversation : result) {
                        MainApplication.getOperatorById(String.valueOf(conversation.route.getMember_id()), new AHandler<Employee>() {
                            @Override
                            public void onError(String errMsg) {

                            }

                            @Override
                            public void onResultRecieved(Employee result) {
                                LTEmployee operator = new LTEmployee();
                                operator.setAvatar(result.getAvatar() != null ? result.getAvatar() : "");
                                operator.setFirstname(result.getFirstname() != null ? result.getFirstname() : "Неизвестный оператор");
                                final String conversationId = conversation.getId();
                                Log.d("result", "conversationId " + conversation.getId());
                                if(!Dao.getInstance(getContext()).hasConversation(conversationId)) {
                                    Dao.getInstance(getContext()).saveConversation(conversation, operator);

                                    MainApplication.getOfflineMessagesList(Integer.parseInt(conversation.getId()), new AHandler<ArrayList<OfflineMessage>>() {
                                        @Override
                                        public void onError(String errMsg) {

                                        }

                                        @Override
                                        public void onResultRecieved(ArrayList<OfflineMessage> result) {
                                            Dao.getInstance(getContext()).saveMessages(result, conversationId);
                                            Log.d("result messages ", " " + result);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void onOperatorsListReceived(final ArrayList<LTEmployee> employees) {
        MainApplication.getOfflineConversations(new AHandler<ArrayList<OfflineConversation>>() {
            @Override
            public void onError(String errMsg) {

            }

            @Override
            public void onResultRecieved(ArrayList<OfflineConversation> result) {
                if(result != null && result.size() != 0) {
                    for (OfflineConversation conversation : result/*int i=0; i<result.size(); i++*/) {
                        final LTEmployee operator = getEmployeeById(employees, String.valueOf(conversation.route.getMember_id()));

                        final String conversationId = conversation.getId();
                        Log.d("result", "conversationId " + conversation.getId());
                        if(!Dao.getInstance(getContext()).hasConversation(conversationId)) {
                            Dao.getInstance(getContext()).saveConversation(conversation, operator);

                            MainApplication.getOfflineMessagesList(Integer.parseInt(conversation.getId()), new AHandler<ArrayList<OfflineMessage>>() {
                                @Override
                                public void onError(String errMsg) {

                                }

                                @Override
                                public void onResultRecieved(ArrayList<OfflineMessage> result) {
                                    Dao.getInstance(getContext()).saveMessages(result, conversationId);
                                    Log.d("result messages ", " " + result);
                                }
                            });
                        }

                    }
                }
            }
        });
    }

    public static LTEmployee getEmployeeById(List<LTEmployee> employees, String operatorId) {
        if(employees != null) {
            for(LTEmployee employee : employees) {
                if(employee.employeeId.equals(operatorId)) {
                    return employee;
                }
            }
        }

        return null;
    }
}
