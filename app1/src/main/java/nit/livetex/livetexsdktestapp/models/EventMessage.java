package nit.livetex.livetexsdktestapp.models;

/**
 * Created by user on 12/5/2015.
 */
public class EventMessage extends BaseMessage {


    public EventMessage(TYPE messageType) {
        super(messageType);
    }

    public EventMessage(TYPE messageType, String stringExtra) {
        super(messageType, stringExtra);
    }
}
