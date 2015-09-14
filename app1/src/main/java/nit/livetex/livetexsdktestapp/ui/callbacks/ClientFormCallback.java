package nit.livetex.livetexsdktestapp.ui.callbacks;

import java.util.List;

import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 29.07.15.
 */
public interface ClientFormCallback extends BaseCallback {

    public void onEmployeesReceived(List<LTEmployee> operators);

    public void onDepartmentsReceived(List<LTDepartment> departments);

    public void createChat(String conversationId, String avatar, String firstName);

    public void onEmployeesEmpty();

    public void onDepartmentsEmpty();
}
