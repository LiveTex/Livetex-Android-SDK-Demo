package nit.livetex.livetexsdktestapp.presenters.offline;

import android.util.Log;

import java.util.ArrayList;

import Clients.Enums.OfflineConversation;
import Clients.Enums.OfflineMessage;
import livetex.employee.Employee;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.presenters.BasePresenter;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.ui.callbacks.SendOfflineMessageCallback;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

import sdk.handler.AHandler;
import sdk.models.LTEmployee;

/**
 * Created by user on 28.07.15.
 */
public class SendOfflineMessagePresenter extends BasePresenter<SendOfflineMessageCallback> {


    public SendOfflineMessagePresenter(SendOfflineMessageCallback callback) {
        super(callback);
    }

    public void createConversation(String name, String email, String phone, final String message) {
        MainApplication.createOfflineConversation(name, email, phone, MainApplication.OFFLINE_DEPARTMENT_ID, new AHandler<Integer>() {
            @Override
            public void onError(String errMsg) {
            }

            @Override
            public void onResultRecieved(Integer result) {
                final int conversationId = result;

                MainApplication.sendOfflineMessage(message, conversationId, new AHandler<Boolean>() {
                    @Override
                    public void onError(String errMsg) {

                    }

                    @Override
                    public void onResultRecieved(Boolean result) {
                        DataKeeper.saveLastMessage(getContext(), message);

                        MainApplication.getOfflineConversations(new AHandler<ArrayList<OfflineConversation>>() {
                            @Override
                            public void onError(String errMsg) {

                            }

                            @Override
                            public void onResultRecieved(ArrayList<OfflineConversation> result) {
                                if (result != null && result.size() != 0) {
                                    for (final OfflineConversation conversation : result) {
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
                                                if (!Dao.getInstance(getContext()).hasConversation(conversationId)) {
                                                    Dao.getInstance(getContext()).saveConversation(conversation, operator);

                                                    MainApplication.getOfflineMessagesList(Integer.parseInt(conversation.getId()), new AHandler<ArrayList<OfflineMessage>>() {
                                                        @Override
                                                        public void onError(String errMsg) {

                                                        }

                                                        @Override
                                                        public void onResultRecieved(ArrayList<OfflineMessage> result) {
                                                            Dao.getInstance(getContext()).saveMessages(result, conversationId);

                                                            Log.d("offline_result", "Result " + result);
                                                            getCallback().onMessageSended(String.valueOf(conversationId));
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
                });
            }
        });
    }
}
