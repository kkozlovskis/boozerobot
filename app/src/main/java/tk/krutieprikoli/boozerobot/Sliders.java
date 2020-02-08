package tk.krutieprikoli.boozerobot;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Sliders extends AppCompatActivity {

    TextView alcoholBarView;
    TextView sodaBarView;
    private static final String TAG = "TEST ";
    private static final int maxPercentage = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliders);

        // set a change listener on the SeekBar
        SeekBar alcoholBar = findViewById(R.id.alcoholBar);
        alcoholBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int alcoholBarPercentage = alcoholBar.getProgress();
        alcoholBarView = findViewById(R.id.alcoholTextView);
        alcoholBarView.setText("Alcohol percentage: " + alcoholBarPercentage);


        SeekBar sodaBar = findViewById(R.id.sodaBar);
        sodaBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int sodaBarPercentage = sodaBar.getProgress();
        sodaBarView = findViewById(R.id.sodaTextView);
        sodaBarView.setText("Soda percentage: " + sodaBarPercentage);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int percentages, boolean fromUser) {

            switch (seekBar.getId()) {
                case R.id.alcoholBar:
                    alcoholBarView.setText("Alcohol percentage: " + percentages);

                    SeekBar sodaBar = findViewById(R.id.sodaBar);
                    sodaBar.setProgress(getPercentages(percentages));
                    break;

                case R.id.sodaBar:
                    sodaBarView.setText("Soda percentage: " + percentages);

                    SeekBar alcoholBar = findViewById(R.id.alcoholBar);
                    alcoholBar.setProgress(getPercentages(percentages));
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    public void submitClicked(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SeekBar alcoholBar = findViewById(R.id.alcoholBar);
                SeekBar sodaBar = findViewById(R.id.sodaBar);
                EditText clientName = findViewById(R.id.clientsName);

                String urlAdress = "https://krutieprikoli.ml/api/booze/request";
                try {
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();

                    jsonParam.put("clientsName", clientName.getText().toString());
                    jsonParam.put("alcoholAmount", alcoholBar.getProgress());
                    jsonParam.put("nonAlcoholAmount", sodaBar.getProgress());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//            os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        Intent intent = new Intent(this, DrinkOrderedActivity.class);
        startActivity(intent);
    }

    private static int getPercentages(int percentage) {
        return maxPercentage - percentage;
    }
}
