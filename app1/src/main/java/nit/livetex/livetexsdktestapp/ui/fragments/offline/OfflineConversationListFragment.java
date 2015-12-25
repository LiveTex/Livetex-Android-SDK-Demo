package nit.livetex.livetexsdktestapp.ui.fragments.offline;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.adapters.ConversationAdapter;
import nit.livetex.livetexsdktestapp.presenters.offline.OfflineConversationListPresenter;
import nit.livetex.livetexsdktestapp.providers.ConversationsProvider;
import nit.livetex.livetexsdktestapp.ui.callbacks.OfflineConversationListCallback;
import nit.livetex.livetexsdktestapp.ui.fragments.BaseFragment;

/**
 * Created by user on 28.07.15.
 */
public class OfflineConversationListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, OfflineConversationListCallback, AdapterView.OnItemClickListener {

    private ListView lvConversations;
    private ConversationAdapter adapter;

    private OfflineConversationListPresenter presenter;

    @Override
    protected boolean onActionBarVisible() {
        return true;
    }

    @Override
    public View getCustomActionBarView(LayoutInflater inflater, int actionBarHeight) {
        View v = inflater.inflate(R.layout.header_conversation_list, null);
        ImageView ivAddConversation = (ImageView) v.findViewById(R.id.ivConversationAdd);
        ivAddConversation.setColorFilter(Color.WHITE);
        ivAddConversation.setVisibility(View.INVISIBLE);
        ivAddConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  showFragment(new SendOfflineMessageFragment(), true);
            }
        });
        return v;
    }

    @Override
    protected View onCreateView(View v) {
        presenter = new OfflineConversationListPresenter(this);

        presenter.fetchConversationData();

        lvConversations = (ListView) v.findViewById(R.id.lvConversations);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(new SendOfflineMessageFragment(), true);
            }
        });
        adapter = new ConversationAdapter(getContext());
        getFragmentEnvironment().getSupportLoaderManager().initLoader(0, null, this);

        lvConversations.setAdapter(adapter);

        lvConversations.setOnItemClickListener(this);

        return super.onCreateView(v);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_offline;
    }




    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(), ConversationsProvider.URI_DATA, null, null, null, ConversationsProvider.CREATED_AT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String conversationId = (String) view.getTag();
        if(conversationId != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ConversationsProvider.CONVERSATION_ID, conversationId);
            showFragment(new OfflineChatFragment(), bundle, true);
        }


    }
}
