package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;


import yukihane.logbook.ItemAdapter.ReachLastItemListener;
import yukihane.logbook.entity.Listable;
import yukihane.logbook.structure.FeedPage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public abstract class FacebookListActivity<T extends Listable<T>> extends Activity {
    protected final ItemAdapter adapter = new ItemAdapter(this, new RequestNextPage());
    private final MeRequestListener pageLiquestListener = new MeRequestListener();
    private static final int RESULT_CODE_AUTHORIZE_ACTIVITY = 0;
    private static final int RESULT_CODE_POST_ACTIVITY = 1;

    private static final int MENU_GROUP_LOGIN_LOGOUT = 1;

    private static final int MENU_POST = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ListView list = (ListView) findViewById(R.id.list);
        final TextView footer = new TextView(list.getContext());
        footer.setText("here is footer");
        list.addFooterView(footer);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView lv = (ListView) parent;
                final T we = (T) lv.getItemAtPosition(position);
                onListItemClicked(we);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (LogbookApplication.mFacebook.isSessionValid()) {
            onLoginValidated();
        }
    }

    protected abstract void onListItemClicked(T item);

    protected abstract void onLoginValidated();

    protected abstract FeedPage createPage(JSONObject obj) throws JSONException, ParseException;

    protected abstract String getGraphPath();

    protected abstract String getPostGraphPath();

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        LogbookApplication.mFacebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: " + resultCode + ", " + resultCode);

        LogbookApplication.mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    private final class MeRequestListener extends RequestListenerAdapter {

        @Override
        public void onComplete(String response, Object state) {
            // TODO Auto-generated method stub
            Log.v(TAG, "MeRequestListener#onComplete");
            try {
                final JSONObject res = Util.parseJson(response);
                FacebookListActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            final FeedPage page = createPage(res);
                            adapter.addPage(page);
                        } catch (JSONException e) {
                            Log.e(TAG, "", e);
                        } catch (ParseException e) {
                            Log.e(TAG, "", e);
                        }
                    }
                });
            } catch (FacebookError e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MeRequestListener#onComplete", e);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MeRequestListener#onComplete", e);
            }
        }
    }

    private final class RequestNextPage implements ReachLastItemListener {

        @Override
        public void fire(Bundle nextParam) {
            if (nextParam != null) {
                requestPage(nextParam);
            }
        }
    }

    protected final void requestPage() {
        Bundle b = new Bundle();
        requestPage(b);
    }

    protected final void requestPage(final Bundle params) {
        LogbookApplication.mAsyncRunner.request(getGraphPath(), params, pageLiquestListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "onPrepareOptionsMenu " + getClass().getSimpleName());
        menu.clear();

        if (LogbookApplication.mFacebook.isSessionValid()) {
            menu.add(MENU_GROUP_LOGIN_LOGOUT, 1, 1, "logout");
        } else {
            menu.add(MENU_GROUP_LOGIN_LOGOUT, 2, 1, "login");
        }
        menu.add(Menu.NONE, MENU_POST, 2, "post");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected " + getClass().getSimpleName());
        if (item.getGroupId() == MENU_GROUP_LOGIN_LOGOUT) {
            LogbookApplication.changeLoginStatus(FacebookListActivity.this, RESULT_CODE_AUTHORIZE_ACTIVITY);
            return true;
        } else if (item.getItemId() == MENU_POST) {
            final Intent intent = new Intent(getBaseContext(), PostActivity.class);
            intent.putExtra("graphPath", getPostGraphPath());
            startActivity(intent);
            return true;
        }
        return false;
    }
}
