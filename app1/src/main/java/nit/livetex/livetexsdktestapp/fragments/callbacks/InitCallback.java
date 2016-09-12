package nit.livetex.livetexsdktestapp.fragments.callbacks;

/**
 * Created by user on 28.07.15.
 */
public interface InitCallback extends BaseCallback {

    public void onInitComplete(String token);

    public void onClear();

}
