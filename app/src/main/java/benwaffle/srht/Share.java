package benwaffle.srht;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Share extends Activity {
    private static final String LOG_TAG = Share.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEND)) { // share with sr.ht
            Bundle extras = intent.getExtras();
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
                Log.d(LOG_TAG, "ACTION_SEND: " + uri);

                if (uri == null) {
                    Toast.makeText(this, "Error: file missing", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                if (Config.getApiKey(this).isEmpty()) {
                    Toast.makeText(this, "Please get your API key from sr.ht", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }

                Toast.makeText(this, "Uploading file...", Toast.LENGTH_SHORT).show();
                upload(parseUriToFilename(uri));
            }
        }

        finish();
    }

    private String parseUriToFilename(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Files.FileColumns.DATA}, null, null, null);
        int index = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        cursor.moveToFirst();
        if (index >= 0) {
            String result = cursor.getString(index);
            cursor.close();
            return result;
        } else {
            cursor.close();
            return "";
        }
    }

    private void upload(String filepath) {
        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("key", Config.getApiKey(this))
                .addFormDataPart("file", filepath,
                        RequestBody.create(
                                MediaType.parse("application/octet-stream"),
                                new File(filepath)
                        )
                ).build();
        String path = Config.getUrl(this);
        if (!path.startsWith("http://") && !path.startsWith("https://"))
            path = "https://" + path;
        Request req = new Request.Builder()
                .url(path + "/api/upload")
                .post(body)
                .build();

        new OkHttpClient().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                toast("Upload failed");
                Log.e(LOG_TAG, "upload failed", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final JSONObject body = new JSONObject(response.body().string());
                        if (!body.optString("error").isEmpty() || body.getBoolean("success") == false) {
                            toast("Upload failed: " + body.optString("error"));
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("sr.ht URL", body.optString("url"));
                                    clipboard.setPrimaryClip(clip);
                                    toast("URL copied to clipboard");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON error", e);
                        toast("Upload failed");
                    }
                } else {
                    try {
                        JSONObject body = new JSONObject(response.body().string());
                        toast("Upload failed: " + body.optString("error", "unknown reason"));
                        Log.e(LOG_TAG, response.body().string());
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "JSON error", e);
                        Log.d(LOG_TAG, response.body().string());
                    }
                }
            }

            private void toast(final String text) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Share.this, text, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}