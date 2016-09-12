package nit.livetex.livetexsdktestapp.fragments.callbacks;

import java.util.List;

import livetex.queue_service.Destination;
import sdk.models.LTDepartment;
import sdk.models.LTEmployee;

/**
 * Created by user on 29.07.15.
 */
public interface ClientFormCallback extends BaseCallback {

    public void onEmployeesReceived(List<LTEmployee> operators);

    public void onDepartmentsReceived(List<LTDepartment> departments);

    public void onDestinationsReceived(List<Destination> destinations);

    public void createChat(String conversationId, String avatar, String firstName);

    public void createChat();

    public void onEmployeesEmpty();

    public void onDepartmentsEmpty();
}
