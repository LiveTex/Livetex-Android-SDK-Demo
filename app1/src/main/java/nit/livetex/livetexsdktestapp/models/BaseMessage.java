package nit.livetex.livetexsdktestapp.models;

import java.io.Serializable;

/**
 * Created by user on 28.07.15.
 */
public abstract class BaseMessage {

    public enum TYPE {
        INIT, UPDATE_STATE, RECEIVE_FILE, RECEIVE_MSG, TYPING_MESSAGE,
        CONFIRM_SEND_MSG, HOLD_MSG, OPERATOR_TYPING, OFFLINE_MSG_RECEIVED, CLOSE
    }

    protected TYPE messageType;
    protected String stringExtra;
    protected  Serializable object;

    public BaseMessage(TYPE messageType) {
        this.messageType = messageType;
    }

    public BaseMessage(TYPE messageType, String stringExtra) {
        this.messageType = messageType;
        this.stringExtra = stringExtra;
    }

    public void putStringExtra(String extra) {
        this.stringExtra = extra;
    }

    public String getStringExtra() {
        return stringExtra;
    }

    public TYPE getMessageType() {
        return messageType;
    }

    public void putSerializable(Serializable object) {
        this.object = object;
    }

    public Serializable getSerializable() {
        return object;
    }

}
