package net.positivestate.randomstudentpicker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Random;

public class MainActivity extends AppCompatActivity {



    private static final String[] students = {
            "Joe Blogs",
            "Sue Blugs",
            "Simon Says",
            "Dorothy Does" };

    private TextView mStatusText;

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;


    protected String getRandomStudent(String[] StudentList){
        return StudentList[new Random().nextInt(StudentList.length)];
    }

    protected void showRandomStudent(String[] StudentList){
        mStatusText.setText(getRandomStudent(StudentList));
    }



    /**
     * the random button (dice) has been pressed
     * @param view  A handle to the random button (cast to FloatingActionButton)
     */
    public void randomButtonPressed(View view) {
        showRandomStudent(students);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // install the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get handle to the student name view
        mStatusText = (TextView) findViewById(R.id.mTextView);

        showRandomStudent(students);




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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
