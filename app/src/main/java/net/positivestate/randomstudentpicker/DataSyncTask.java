package net.positivestate.randomstudentpicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.ListStudentsResponse;
import com.google.api.services.classroom.model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class DataSyncTask extends AsyncTask<Void, Void, String> {

    private MainActivity mActivity;
    private Context mContext;
    private Classroom mService;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { ClassroomScopes.CLASSROOM_COURSES,
            ClassroomScopes.CLASSROOM_PROFILE_PHOTOS,
            ClassroomScopes.CLASSROOM_PROFILE_EMAILS,
            ClassroomScopes.CLASSROOM_ROSTERS };
     GoogleAccountCredential credential;
        /**
         * Constructor.
         * @param activity MainActivity that spawned this task.
         */
    DataSyncTask(MainActivity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        SharedPreferences settings =  this.mContext.getSharedPreferences(activity.getPackageName(), this.mContext.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                this.mContext, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                //.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, "victor.rajewski@jmss.vic.edu.au"));

        mService = new com.google.api.services.classroom.Classroom.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("")
                .build();
    }

    @Override
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(this.mContext, result, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            //mActivity.clearResultsText();
            //mActivity.updateResultsText(getCourseNames());
            //mActivity.updateResultsText(getStudentNames("101645228"));
            getCourseNames();

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            return "Error:\n" + e.getMessage();
            //Toast toast = Toastd.makeText(this.mContext, "Error:\n" + e.getMessage(), Toast.LENGTH_LONG);
            //toast.show();
        }
        return null;
    }

    /**
     * Fetch a list of the names of the first 10 courses the user has access to.
     * @return List course names, or a simple error message if no courses are
     *         found.
     * @throws IOException
     */
    private List<String> getCourseNames() throws IOException {
        ListCoursesResponse response = this.mService.courses().list()
                .setPageSize(10)
                .execute();
        List<Course> courses = response.getCourses();
        List<String> names = new ArrayList<String>();
        if (courses != null) {
            for (Course course : courses) {
                names.add(course.getName());
            }
        }
        return names;
    }

    /**
     * Fetch a list of the names of the first 10 courses the user has access to.
     * @return List course names, or a simple error message if no courses are
     *         found.
     * @throws IOException
     */
    private List<String> getStudentNames(String courseId) throws IOException {
        String pageToken = null;
        List<String> names = new ArrayList<String>();
        do {
            ListStudentsResponse response = this.mService.courses().students().list(courseId)
                    .setPageToken(pageToken)
                    .setPageSize(100)
                    .execute();
            pageToken = response.getNextPageToken();
            List<Student> students = response.getStudents();
            if (students != null) {
                for (Student student : students) {
                    names.add(student.getProfile().getName().getFullName());
                }
            }
        } while (pageToken != null);
        return names;
    }
}
