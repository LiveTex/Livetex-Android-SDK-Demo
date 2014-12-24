package nit.livetex.livetexsdktestapp.adapter;

import android.util.Log;

import livetex.sdk.models.FileMessage;
import livetex.sdk.models.HoldMessage;
import livetex.sdk.models.TextMessage;

/**
 * Created by sergey.so on 08.12.2014.
 *
 */
public class MessageModel {

    String id;
    String text;
    public String timestamp;
    boolean isOutgoing;
    String holdMessage;
    boolean isChecked = false;

    public MessageModel(){}

    public MessageModel(TextMessage textMessage){
         this(textMessage, false);
    }

    public MessageModel(TextMessage textMessage, boolean isChecked){
        text = textMessage.text;
        timestamp = textMessage.timestamp;
        isOutgoing = textMessage.getSender() == null;
        id = textMessage.id;
        this.isChecked = isChecked;
    }

    public MessageModel(HoldMessage holdMessage){
        this.holdMessage = holdMessage.text;
        this.timestamp = holdMessage.timestamp;
        id = null;
    }

    public MessageModel(FileMessage fileMessage){
        this.text = fileMessage.url;
        this.timestamp = fileMessage.timestamp;
        this.id = fileMessage.id;
    }



}
