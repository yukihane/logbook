package yukihane.logbook;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

        final Button btn = (Button) findViewById(R.id.post_button);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = (EditText) findViewById(R.id.post_text);
                final Editable text = et.getText();
                final Bundle b = new Bundle();
                if (text.length() > 0) {
                    b.putString("message", text.toString());
                }
                b.putString("link", "http://www.yahoo.co.jp");
                LogbookApplication.mAsyncRunner.request("me/feed", b, "POST", new RequestListenerAdapter() {
                    @Override
                    public void onComplete(String response, Object state) {
                        PostActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "post success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, null);
            }
        });
    }
}
