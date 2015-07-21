package benwaffle.srht;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
        }

        if (getIntent().getAction().equals("android.intent.action.VIEW")) {
            // TODO
            Log.d(LOG_TAG, "Opened via link");
            /*
            String data = getIntent().getDataString();
            Log.d(LOG_TAG, data);
            String[] dataSplit = data.split(":");
            Log.d(LOG_TAG, "Length: " + dataSplit.length);
            if (dataSplit.length != 3) {
                Toast.makeText(getApplicationContext(), "Incompatible URL", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Your settings have been saved", Toast.LENGTH_SHORT).show();
            }
            */
        }

        int images[] = {
                R.drawable.one,
                R.drawable.two,
                R.drawable.three,
                R.drawable.four,
                R.drawable.five,
                R.drawable.six,
                R.drawable.seven,
                R.drawable.eight,
                R.drawable.nine
        };

        ImageView bgimage = (ImageView) findViewById(R.id.bgimage);
        bgimage.setImageResource(images[new Random().nextInt(images.length)]);
    }
}
