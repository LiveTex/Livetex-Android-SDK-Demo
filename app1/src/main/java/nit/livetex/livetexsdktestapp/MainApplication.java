
package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.multidex.MultiDex;
import android.util.Log;

import livetex.abuse.Abuse;
import livetex.message.TextMessage;
import nit.livetex.livetexsdktestapp.models.BaseMessage;
import nit.livetex.livetexsdktestapp.models.ErrorMessage1;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.utils.BusProvider;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

import org.apache.thrift.TException;

import java.io.File;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Clients.Enums.OfflineConversation;
import Clients.Enums.OfflineMessage;
import livetex.dialog.DialogAttributes;
import livetex.dialog_state.DialogState;
import livetex.employee.Employee;
import sdk.Livetex;
import sdk.handler.AHandler;
import sdk.handler.IInitHandler;
import sdk.handler.INotificationDialogHandler;
import sdk.models.LTAbuse;
import sdk.models.LTDepartment;
import sdk.models.LTDialogState;
import sdk.models.LTEmployee;
import sdk.models.LTFileMessage;
import sdk.models.LTHoldMessage;
import sdk.models.LTTextMessage;
import sdk.models.LTTypingMessage;
import sdk.models.LTVoteType;

/**
 * Created by user on 28.07.15.
 */
public class MainApplication extends Application {

    public static boolean IS_ACTIVE = false;

    public static final String OFFLINE_DEPARTMENT_ID_REAL = "42";

    private static final String AUTH_URL_REAL = "http://authentication-service-sdk-production-1.livetex.ru";

    private static final String API_KEY_PRE_REAL =  "demo";

    private static  String API_KEY = "";
    public static String OFFLINE_DEPARTMENT_ID = "";
    private static String AUTH_URL = "";

    public static void setProductionScope() {
        API_KEY = API_KEY_PRE_REAL;
        OFFLINE_DEPARTMENT_ID = OFFLINE_DEPARTMENT_ID_REAL;
        AUTH_URL = AUTH_URL_REAL;
    }

    private static Livetex sLiveTex;
    private static MainApplication instance;
    public static MainApplication getInstance() {
        return instance;
    }
    public static Livetex getsLiveTex() {
        return sLiveTex;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MainApplication.setProductionScope();
        HandlerThread handlerThread = new HandlerThread("");
        handlerThread.start();
        BusProvider.register(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void initLivetex(String id, String regId) {
        initLivetex(id, regId, null);
    }

    public static void initLivetex(String id, String regId, final AHandler<Boolean> handler) {
        Log.d("polling", "initLivetex");

        sLiveTex = new Livetex.Builder(getInstance(), API_KEY, id)
                .addAuthUrl(AUTH_URL)
                .addDeviceId(regId)
                .setLogEnabled(true)
                .build();
        sLiveTex.init(new IInitHandler() {
            @Override
            public void onSuccess(String token) {
                postMessage(new EventMessage(BaseMessage.TYPE.INIT, token));
                if(handler != null) {
                    handler.onResultRecieved(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                postMessage(new ErrorMessage1(BaseMessage.TYPE.INIT, errorMessage));
            }
        });
        sLiveTex.setNotificationDialogHandler(new INotificationDialogHandler() {
            @Override
            public void ban(String message) throws TException {

            }

            @Override
            public void updateDialogState(LTDialogState state) throws TException {
                if(state.getEmployee() == null) {
                    EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.CLOSE);
                    postMessage(eventMessage);
                } else {
                    EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.UPDATE_STATE);
                    eventMessage.putSerializable(state.getEmployee());
                    postMessage(eventMessage);
                }
            }

            @Override
            public void receiveFileMessage(LTFileMessage message) throws TException {
                EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.RECEIVE_FILE);
                eventMessage.putSerializable(message);
                postMessage(eventMessage);
            }

            @Override
            public void receiveTextMessage(LTTextMessage message) throws TException {
                Log.d("double", "MainApplication " + message.getText() + ", id " + message.getId() + ", time_stamp " + message.getTimestamp());
                EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.RECEIVE_MSG);
                eventMessage.putSerializable(message);

                postMessage(eventMessage);
                Log.d("polling", "Main Application " + message.getText() + " , " + message.getSender());
            }


            @Override
            public void confirmTextMessage(String message) throws TException {

            }

            @Override
            public void receiveHoldMessage(LTHoldMessage message) throws TException {

            }

            @Override
            public void receiveTypingMessage(LTTypingMessage message) throws TException {
                EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.TYPING_MESSAGE);
                postMessage(eventMessage);
            }

            @Override
            public void receiveOfflineMessage(LTTextMessage message) throws TException {
                EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.OFFLINE_MSG_RECEIVED);
                eventMessage.putSerializable(message);
                Log.d("polling", "MainApplication " + message.getSender() + ", " + message.getText());

                Dao.getInstance(sLiveTex.getmContext()).saveMessage(message.getText(), String.valueOf(System.currentTimeMillis()),
                        Integer.parseInt(message.getSender()), "0".equals(message.getSender()));
                postMessage(eventMessage);

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public static void typing(String text) {
        if (sLiveTex != null) {
            LTTypingMessage msg = new LTTypingMessage();
            msg.setText(text);
            sLiveTex.typing(msg);
        }
    }

    public static void vote(boolean isLike, AHandler handler) {
        if (sLiveTex != null) {
            LTVoteType vote = isLike ? LTVoteType.GOOD : LTVoteType.BAD;
            sLiveTex.vote(vote, handler);
        }
    }

    public static void getMsgHistory(int limit, int offset, AHandler<List<TextMessage>> handler) {
        if (sLiveTex != null)
            sLiveTex.messageHistory((short) limit, (short) offset, handler);
    }

    public static void abuse(String name, String msg) {
        if (sLiveTex != null)
            sLiveTex.abuse(new Abuse(name, msg));
    }

    public static void postMessage(BaseMessage message) {
        BusProvider.getInstance().post(message);

    }

    public static void sendMsg(String msg, AHandler<LTTextMessage> handler) {
        if (sLiveTex != null)
            sLiveTex.sendTextMessage(msg, handler);
    }

    public static void confirmTxtMsg(String msgId) {
        if(sLiveTex != null) {
            sLiveTex.confirmTextMessage(msgId, null);
        }
    }

    public static void requestDialogByEmployee(String id, String livetexId, AHandler<LTDialogState> handler){
        if (sLiveTex != null) {
            DialogAttributes dialogAttributes = new DialogAttributes();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Livetex ID", livetexId);
            dialogAttributes.setVisible(hashMap);
            LTEmployee operator = new LTEmployee(id, "online", "", "", "");
            sLiveTex.request(operator, dialogAttributes, handler);
        }
    }

    public static void sendScreenshotOffline(Activity activity, String conversationId, AHandler<Boolean> handler) {
        if(sLiveTex != null) {
            sLiveTex.sendOfflineScreenshot(activity, conversationId, handler);
        }
    }

    public static void sendScreenshotOnline(Activity activity, String conversationId, AHandler<Boolean> handler) {
        if(sLiveTex != null) {
            sLiveTex.sendOnlineScreenshot(activity, conversationId, handler);
        }
    }

    public static void closeDialog(AHandler<LTDialogState> handler) {
        if (sLiveTex != null) {
            sLiveTex.close(handler);
        }
    }

    public static void getDeparmentOperator(LTDepartment department, AHandler<ArrayList<LTEmployee>> handler) {
        if(sLiveTex != null) {
            sLiveTex.getDepartmentOperators(department, handler);
        }
    }

    public static void getDepartment(LTDepartment department, AHandler<LTDialogState> handler) {
        if(sLiveTex != null) {
            sLiveTex.request(department, handler);
        }
    }

    public static void getDepartment1(String departmentId, DialogAttributes dialogAttributes, AHandler<DialogState> handler) {
        if(sLiveTex != null) {
            sLiveTex.request1(departmentId, dialogAttributes, handler);
        }
    }

    public static void getOperators(String status, AHandler<ArrayList<LTEmployee>> handler) {
        if (sLiveTex != null)
            sLiveTex.getOperators(status, handler);
    }

    public static void getOperatorById(String operatorId, AHandler<Employee> handler) {
        if(sLiveTex != null) {
            sLiveTex.getOperatorById(operatorId, handler);
        }
    }

    public static void getDepartments(String status, AHandler<ArrayList<LTDepartment>> handler) {
        if(sLiveTex != null) {
            sLiveTex.getDepartments(status, handler);
        }
    }

    public static void getOfflineConversations(AHandler<ArrayList<OfflineConversation>> handler) {
        if(sLiveTex != null) {
            sLiveTex.getOfflineConversations(handler);
        }
    }

    public static void getState(AHandler<DialogState> handler) {
        if(sLiveTex != null) {
            sLiveTex.getState(handler);
        }
    }

    public static void getOfflineMessagesList(int conversationId, AHandler<ArrayList<OfflineMessage>> handler) {
        if(sLiveTex != null) {
            sLiveTex.getOfflineMessagesList(conversationId, handler);
        }
    }

    public static void createOfflineConversation(String name, String email, String phone, String groupId, AHandler<Integer> handler) {
        if(sLiveTex != null) {
            sLiveTex.createOfflineConversation(name, email, phone, DataKeeper.restoreAppId(getInstance()), groupId, handler);
        }
    }

    public static void sendOfflineMessage(String message, int conversationId, AHandler<Boolean> handler) {
        if(sLiveTex != null) {
            sLiveTex.sendOfflineTextMessage(message, conversationId, handler);
        }
    }

    public static void sendOfflineFile(final File file, final String conversationId, final AHandler<Boolean> handler) {
        if(sLiveTex != null) {

            sLiveTex.sendOfflineFileMessage(file, Integer.parseInt(conversationId), handler);

        }
    }

    public static void sendFile(final File file, final String conversationId, final AHandler<Boolean> handler) {
        if(sLiveTex != null) {

            sLiveTex.sendOnlineFile(file, conversationId, handler);

            /*workerThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    sLiveTex.getUploadUrl(new AHandler<String>() {
                        @Override
                        public void onError(final String errMsg) {
                            mainThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    handler.onError(errMsg);
                                }
                            });

                        }

                        @Override
                        public void onResultRecieved(final String uploadUrl) {

                            String url1 = uploadUrl.replace("https", "http");
                            try {
                                CommonUtils.multipart(url1 + "&recipient_id=" + conversationId + "&transfer_id=" + CommonUtils.getID(), file);
                                mainThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.onResultRecieved(true);
                                    }
                                });

                            } catch (final IOException e) {
                                e.printStackTrace();
                                mainThreadHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.onError(e.getMessage());
                                    }
                                });

                            }

                        }
                    });
                }
            });*/


        }
    }

    public static void setName(String name) {
        if (sLiveTex != null) {
            sLiveTex.setName(name);
        }
    }
}























