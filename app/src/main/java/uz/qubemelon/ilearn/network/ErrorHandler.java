package uz.qubemelon.ilearn.network;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import uz.qubemelon.ilearn.database.Storage;
import uz.qubemelon.ilearn.ui.activities.AuthActivity;
import uz.qubemelon.ilearn.utilities.Utility;

public class ErrorHandler extends Application {

    static ErrorHandler instance;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /* make the error handler class singleton */
    public static synchronized ErrorHandler get_instance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    /* handle error globally by checking the http code */
    public void handle_error(int code, Activity activity, ProgressDialog dialog) {
        switch (code) {
            case 500:
                if(activity != null)
                    Toast.makeText(activity, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                Utility.dismiss_dialog(dialog);
                break;

            case 400:
                if(activity != null)
                    Toast.makeText(activity, "Invalid Request!", Toast.LENGTH_SHORT).show();
                Utility.dismiss_dialog(dialog);
                break;

            case 429:
                if(activity != null)
                    Toast.makeText(activity, "Too Many Request, Please Try Again Later!", Toast.LENGTH_SHORT).show();
                Utility.dismiss_dialog(dialog);
                break;

            case 401:
                Toast.makeText(activity, "Session Expired!", Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                    Utility.dismiss_dialog(dialog);
                }
                go_to_sign_in_activity(activity);
                break;
        }
    }

    /* take user to Auth Activity */
    private void go_to_sign_in_activity(Activity activity) {
        /* make the current user logged out */
        Storage storage = new Storage(activity);
        storage.save_sign_in_sate(false);
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
