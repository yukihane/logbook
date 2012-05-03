package yukihane.logbook;

import static yukihane.logbook.LogbookActivity.TAG;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.ItemAdapter.ReachLastItemListener;
import yukihane.logbook.R.layout;
import yukihane.logbook.entity.CommentsPage;
import yukihane.logbook.entity.Page;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import com.facebook.android.R.id;
import com.facebook.android.Util;

public class CommentActivity extends Activity {
    private final ItemAdapter adapter = new ItemAdapter(this, new RequestNextPage());
    private final MeRequestListener pageLiquestListener = new MeRequestListener();
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.main);

        threadID = getIntent().getStringExtra("id");

        final ListView list = (ListView) findViewById(id.list);
        final TextView footer = new TextView(list.getContext());
        footer.setText("here is footer");
        list.addFooterView(footer);
        list.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        LogbookApplication.mFacebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        Log.v(TAG, "onActivityResult");
        //
        //        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    private class MeRequestListener implements RequestListener {

        @Override
        public void onComplete(String response, Object state) {
            // TODO Auto-generated method stub
            Log.v(TAG, "MeRequestListener#onComplete");
            try {
                final JSONObject res = Util.parseJson(response);
                CommentActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final Page feed = CommentsPage.fromJSONObject(res);
                            adapter.addPage(feed);
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

        @Override
        public void onIOException(IOException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onIOException", e);
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }
    }

    private class RequestNextPage implements ReachLastItemListener {

        @Override
        public void fire(Bundle nextParam) {
            if (nextParam != null) {
                LogbookApplication.mAsyncRunner.request(threadID, nextParam, pageLiquestListener);
            }
        }
    }
}
