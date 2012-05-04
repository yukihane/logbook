package yukihane.logbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

public class PostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        final Button btn = (Button) findViewById(R.id.post_button);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, 1, "Post");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == Menu.NONE && item.getItemId() == 1) {
            post();
            return true;
        }
        return false;
    }

    private void post() {
        final Bundle b = new Bundle();

        final EditText et = (EditText) findViewById(R.id.post_text);
        final Editable text = et.getText();
        if (text.length() > 0) {
            b.putString("message", text.toString());
        }

        final EditText link = (EditText) findViewById(R.id.post_link);
        final Editable linkText = link.getText();
        if (linkText.length() > 0) {
            b.putString("link", linkText.toString());
        }

        LogbookApplication.mAsyncRunner.request("me/feed", b, "POST", new PostRequestListener(), null);
    }

    private class PostRequestListener implements RequestListener {
        @Override
        public void onComplete(String response, Object state) {
            PostActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "post success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        @Override
        public void onIOException(IOException e, Object state) {
            processError(e);
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            processError(e);
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            processError(e);
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            processError(e);
        }

        private void processError(Exception e) {
            final String addText = (e == null) ? "" : ": " + e.getMessage();
            Toast.makeText(getApplicationContext(), "failed post" + addText, Toast.LENGTH_LONG).show();
        }
    }
}
