package net.positivestate.randomstudentpicker;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextSwitcher;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Random;

public class MainActivity extends AppCompatActivity {



    private static final String[] students = {
            "Joe Blogs",
            "Sue Blugs",
            "Simon Says",
            "Dorothy Does" };

    private TextSwitcher mStatusText;

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    protected String getRandomStudent(){
        return students[new Random().nextInt(students.length)];
    }

    protected void showRandomStudent(){
        mStatusText.setText(getRandomStudent());
    }


    /**
     * the random button (dice) has been pressed
     * @param view  A handle to the random button (cast to FloatingActionButton)
     */
    public void randomButtonPressed(View view) {

        // pretend lag (networking). Should really inf animate and callback disable
        final Handler lagger = new Handler();
        lagger.postDelayed(new Runnable() {
            @Override
            public void run() {
                showRandomStudent();
            }
        }, getResources().getInteger(R.integer.dice_roll_length));

        // spin animation
        if (android.os.Build.VERSION.SDK_INT >= 11) {

            ObjectAnimator
                    .ofFloat(findViewById(R.id.randomButton), "rotation", 0f, 359f)
                    .setDuration(getResources().getInteger(R.integer.dice_roll_length))
                    .start();
        }
        else {
            // substitute animation
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // install the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // get handle to the student name view
        mStatusText = (TextSwitcher) findViewById(R.id.mTextView);




        DataSyncTask d = new DataSyncTask(this);
        d.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.menu_history) {

            System.out.println("history!");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

}
