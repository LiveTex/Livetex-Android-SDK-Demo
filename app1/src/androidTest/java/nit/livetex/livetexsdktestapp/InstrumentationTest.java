package nit.livetex.livetexsdktestapp;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import nit.livetex.livetexsdktestapp.R;
import com.robotium.solo.Solo;

import nit.livetex.livetexsdktestapp.FragmentEnvironment;

/**
 * Created by user on 20.07.15.
 */
public class InstrumentationTest extends ActivityInstrumentationTestCase2<FragmentEnvironment> {

    private Solo solo;

    public InstrumentationTest() {
        super(FragmentEnvironment.class);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Вход в приложение offline
     */
    private void enterConversationListOffline() {
        solo.assertCurrentActivity("wrong activity", FragmentEnvironment.class);
        solo.clickOnButton(solo.getString(R.string.next));
        solo.clickOnButton(solo.getString(R.string.offline_mode));
    }

    /**
     * Вход в приложение online
     */
    private void enterConversationListOnline() {
        solo.assertCurrentActivity("wrong activity", FragmentEnvironment.class);
        solo.clickOnButton(solo.getString(R.string.next));
        solo.clickOnButton(solo.getString(R.string.online_mode));
    }

    /**
     * Создание разговора, ввод информации о клиенте
     */
    public void testCreateConversation() {
        enterConversationListOffline();
        solo.sendKey(Solo.MENU);
       // solo.clickOnView(solo.getView(R.id.action_offline_msg));
        solo.clickOnView(solo.getView(R.id.ivConversationAdd));
        EditText etName = (EditText) solo.getView(R.id.etName);
        EditText etPhone = (EditText) solo.getView(R.id.etPhone);
        EditText etEmail = (EditText) solo.getView(R.id.etEmail);
        EditText etMessage = (EditText) solo.getView(R.id.etMessage);
        solo.enterText(etName, "Алексей Алексеевич");
        solo.enterText(etPhone, "89650089191");
        solo.enterText(etEmail, "yes.android.cool@yandex.ru");
        solo.enterText(etMessage, "Дай пять");

        solo.clickOnButton(solo.getString(R.string.send_offline_msg));
        solo.sleep(1000);
    }

    /**
     * Тестирование оффлайн сообщений из чата
     */
    public void testSendOfflineMessage() {
        enterConversationListOffline();
        solo.clickInList(1);
        EditText etInputMessage = (EditText) solo.getView(R.id.etInputMsg);
        ImageView ivSendMessage = (ImageView) solo.getView(R.id.ivSendMsg);
        solo.enterText(etInputMessage, "Hello");
        solo.clickOnView(ivSendMessage);
    }

    /**
     * Создание онлайн чата
     */
    public void testCreateConversationOnline() {
        enterConversationListOnline();
        EditText etWelcomeName = (EditText) solo.getView(R.id.etWelcomeName);
        EditText etMessage = (EditText) solo.getView(R.id.etMessage);
        Spinner spDepartments = (Spinner) solo.getView(R.id.spDepartments);
        solo.clickOnView(spDepartments);
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 0));
        solo.enterText(etWelcomeName, "Мишаня");
        solo.enterText(etMessage, "На помощь, помогите кто-нибудь!");
        solo.clickOnButton(solo.getString(R.string.create_online_dialog));
        solo.sleep(1000);
        EditText etInputMessage = (EditText) solo.getView(R.id.etInputMsg);
        ImageView ivSendMessage = (ImageView) solo.getView(R.id.ivSendMsg);
        solo.enterText(etInputMessage, "Help! Help!");
        solo.clickOnView(ivSendMessage);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}