package cc.wanko.karin.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import cc.wanko.karin.app.R;
import cc.wanko.karin.app.activities.UserListActivity;
import cc.wanko.karin.app.adapters.UserListListAdapter;
import cc.wanko.karin.app.client.TwitterProvider;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.UserList;

/**
 * Created by eagletmt on 14/05/05.
 */
public class UserListListFragment extends RoboFragment {
    @InjectView(R.id.user_list_list)
    private ListView userListList;

    private UserListListAdapter adapter;

    private Twitter twitter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_list_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new UserListListAdapter(getActivity());
        userListList.setAdapter(adapter);
        userListList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        UserList list = adapter.getItem(position);
                        Intent intent = UserListActivity.createIntent(getActivity(), list);
                        startActivity(intent);
                    }
                }
        );

        twitter = TwitterProvider.get(getActivity());
        retrieveUserLists();
    }

    private void retrieveUserLists() {
        new RoboAsyncTask<ResponseList<UserList>>(getActivity()) {
            @Override
            public ResponseList<UserList> call() throws Exception {
                return twitter.getUserLists("");
            }

            @Override
            protected void onSuccess(ResponseList<UserList> userLists) throws Exception {
                adapter.clear();
                for (UserList list : userLists) {
                    adapter.add(list);
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                reportException("Cannot get user lists", e);
            }
        }.execute();
    }

    private void reportException(String message, Exception e) {
        Toast.makeText(getActivity(), message + ": " + e.getClass() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
