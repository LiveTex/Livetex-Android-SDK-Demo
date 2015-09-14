package nit.livetex.livetexsdktestapp.models;

/**
 * Created by user on 28.07.15.
 */
public class ErrorMessage1 extends BaseMessage {

    public ErrorMessage1(TYPE messageType) {
        super(messageType);
    }

    public ErrorMessage1(TYPE messageType, String stringExtra) {
        super(messageType, stringExtra);
    }
}
