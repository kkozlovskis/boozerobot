package tk.krutieprikoli.boozerobot;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Sliders extends AppCompatActivity {

    TextView alcoholBarView;
    TextView sodaBarView;
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

        Button submitBtn = findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText clientName = findViewById(R.id.clientsName);
                if (clientName.getText().toString().length() < 1) {
                    // Display toast
                    Toast.makeText(getApplicationContext(), "Please enter nickname!", Toast.LENGTH_LONG).show();
                } else {
                    submitData(view);
                }
            }
        });
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
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public void submitData(View view) {
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
