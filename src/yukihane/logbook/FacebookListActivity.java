package yukihane.logbook;

import static yukihane.logbook.Constants.CONTEXT_MENU_GROUP_COMMON_LINK;
import static yukihane.logbook.Constants.MENU_GROUP_LOGIN_LOGOUT;
import static yukihane.logbook.Constants.MENU_GROUP_POST;
import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.ItemAdapter.ReachLastItemListener;
import yukihane.logbook.db.DatabaseHelper;
import yukihane.logbook.entity.Listable;
import yukihane.logbook.structure.Page;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

public abstract class FacebookListActivity<E extends Listable<E>, P extends Page<E>> extends Activity {
    private static final int RESULT_CODE_AUTHORIZE_ACTIVITY = 0;
    private static final int RESULT_CODE_POST_ACTIVITY = 1;

    private final MeRequestListener pageLiquestListener = new MeRequestListener();
    private DatabaseHelper databaseHelper = null;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ListView list = (ListView) findViewById(R.id.list);
        final TextView footer = new TextView(list.getContext());
        footer.setText("here is footer");
        list.addFooterView(footer);

        final ItemAdapter<E, P> adapter = getItemAdapter();
        try {
            final List<E> items = getPersistedItems();
            Log.i(TAG, "DB load : " + items.size());
            adapter.addItems(items);
        } catch (SQLException e) {
            Log.e(TAG, "cannot load items", e);
        }

        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView lv = (ListView) parent;
                final Object we = lv.getItemAtPosition(position);
                onListItemClicked(we, position);
            }
        });

        registerForContextMenu(list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (LogbookApplication.mFacebook.isSessionValid()) {
            onLoginValidated();
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume " + getClass().getSimpleName());
        super.onResume();
        LogbookApplication.mFacebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause " + getClass().getSimpleName());
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop " + getClass().getSimpleName());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy " + getClass().getSimpleName());
        super.onDestroy();

        /*
         * You'll need this in your class to release the helper when done.
         */
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: " + resultCode + ", " + resultCode);

        LogbookApplication.mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    protected final DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    protected abstract ItemAdapter<E, P> getItemAdapter();

    protected abstract void onListItemClicked(Object item, int position);

    protected abstract void onLoginValidated();

    protected abstract P createPage(JSONObject obj) throws JSONException, ParseException;

    protected abstract String getGraphPath();

    protected abstract String getPostGraphPath();

    protected abstract Dao<E, String> getDao() throws SQLException;

    protected abstract List<E> getPersistedItems() throws SQLException;

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

                            final P page = createPage(res);
                            getItemAdapter().addPage(page);

                            final Dao<E, String> dao = getDao();
                            final Integer created = dao.callBatchTasks(new Callable<Integer>() {
                                @Override
                                public Integer call() throws Exception {
                                    int created = 0;
                                    for (E item : page.getItems()) {
                                        final CreateOrUpdateStatus status = dao.createOrUpdate(item);
                                        if (status.isCreated()) {
                                            created++;
                                        }
                                    }
                                    return Integer.valueOf(created);
                                }
                            });
                            Log.i(TAG, "item created: " + created);
                        } catch (JSONException e) {
                            Log.e(TAG, "", e);
                        } catch (ParseException e) {
                            Log.e(TAG, "", e);
                        } catch (SQLException e) {
                            Log.e(TAG, "", e);
                        } catch (Exception e) {
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

    protected final class RequestNextPage implements ReachLastItemListener {

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
            menu.add(MENU_GROUP_LOGIN_LOGOUT, Menu.NONE, Menu.NONE, "logout");
        } else {
            menu.add(MENU_GROUP_LOGIN_LOGOUT, Menu.NONE, Menu.NONE, "login");
        }
        menu.add(MENU_GROUP_POST, Menu.NONE, Menu.NONE, "post");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected " + getClass().getSimpleName());
        if (item.getGroupId() == MENU_GROUP_LOGIN_LOGOUT) {
            LogbookApplication.changeLoginStatus(FacebookListActivity.this, RESULT_CODE_AUTHORIZE_ACTIVITY);
            return true;
        } else if (item.getGroupId() == MENU_GROUP_POST) {
            final Intent intent = new Intent(getBaseContext(), PostActivity.class);
            intent.putExtra("graphPath", getPostGraphPath());
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == CONTEXT_MENU_GROUP_COMMON_LINK) {
            startActivity(item.getIntent());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected static final int addTextLinkToContextMenu(final ContextMenu menu, final String text, final int order) {
        int num = 0;
        final Pattern urlPattern = Pattern.compile("https?://[^\\s]+");
        final Matcher urlMatcher = urlPattern.matcher(text);
        while (urlMatcher.find()) {
            final String url = urlMatcher.group();
            Log.i(TAG, "add link to context menu: " + url);
            final MenuItem item = menu.add(CONTEXT_MENU_GROUP_COMMON_LINK, num, order + num, url);
            item.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            num++;
        }
        return num;
    }

}
