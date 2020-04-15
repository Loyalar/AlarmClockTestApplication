package Logic;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lj on 09-05-2016.
 */
public class LOG {

    private static final boolean DEBUG = true; // Set to true to enable logging

    private static String TAG;

    public static void logSimple(String text) {
        if (TAG == null)
            TAG = "LJ-DEBUG";

        if (DEBUG)
            Log.i(TAG, text);
    }

    public static void log(String text) {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("log") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = fullClassName.substring(fullClassName
                .lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());

        if (TAG == null)
            TAG = "LJ-DEBUG";

        if (DEBUG) {
            Log.i(TAG + " position", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")");
            Log.i(TAG, text);
        }
    }

    public static void logError(Activity activity, String error) {
        logError(activity, error, null);
    }

    public static void logError(Activity activity, String error, Throwable throwable) {
        logError(activity, error, throwable, true);
    }

    public static void logError(Activity activity, String error, Throwable throwable, boolean logToService) {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("logError") == 0) {
                currentIndex = i + 1;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String.valueOf(stackTraceElement[currentIndex].getLineNumber());

        if (TAG == null)
            TAG = "LJ-DEBUG";

        if (logToService)
            logErrorToService(activity, error, fullClassName, lineNumber, methodName, throwable);

        if (DEBUG)
            Log.e(TAG, error + (throwable == null ? "" : '\n' + Log.getStackTraceString(throwable)));
    }

    public static void logErrorToService(Activity activity, String error, String className, String lineNumber, String methodName, Throwable ex) {
        if (TextUtils.isEmpty(error))
            error = ex.getMessage();

//        try {
//            LogErrorIO io = new LogErrorIO(activity.getString(R.string.APP_GUID), error, Log.getStackTraceString(ex), className, methodName + ":" + lineNumber, "Android");
//            RestCallInit logErrorInit = new RestCallInit<>("Common/LogError", io, GeneralRO.class);
//            new RestCall<LogErrorIO, GeneralRO>(null,
//                    new RestResponse<GeneralRO>() {
//                        @Override
//                        public void restCallDone(String command, GeneralRO rto) {
//
//                        }
//
//                        @Override
//                        public void restCallError(String command, String error, GeneralRO rto) {
//
//                        }
//                    }, activity, true, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, logErrorInit);
//        } catch (Exception e) {
//            //too bad
//        }
    }
}
