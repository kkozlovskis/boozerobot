package tk.krutieprikoli.boozerobot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DrinkOrderedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_ordered);
    }

    public void orderNewDrink(View view) {
        Intent intent = new Intent(this, Sliders.class);
        startActivity(intent);
    }
}
