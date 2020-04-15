package Logic;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by phk_000 on 11-02-2016.
 */
public class DataStore<TObject> {
    private Context mContext;
    private Activity mActivity;

    public DataStore(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity;
    }

    public DataStore(Context context) {
        this.mContext = context;
    }

    public TObject getObject(String name) {
        try {
            String fileName = "FreezerTracker_" + name + ".data";
            File dirFile = new File(mContext.getFilesDir(), "");
            if (!dirFile.exists())
                dirFile.mkdirs();

            String filePath = mContext.getFilesDir().getPath() + File.separator + fileName;
            File dataStoreFile = new File(filePath);

            if (!dataStoreFile.exists())
                return null;

            FileInputStream fileInputStream = new FileInputStream(dataStoreFile);

            if (fileInputStream == null)
                return null;

            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            if (objectInputStream == null)
                return null;

            TObject tObject = (TObject) objectInputStream.readObject();
            objectInputStream.close();

            return tObject;
        } catch (Exception exception) {
            if (mActivity != null)
                LOG.logError(mActivity, "DataStore getObject failed: " + exception.getMessage(), exception, false);
            return null;
        }
    }

    public void setObject(String name, TObject tObject) {
        String fileName = "FreezerTracker_" + name + ".data";
        try {
            String filePath = mContext.getFilesDir().getPath() + File.separator + fileName;
            File dataStoreFile = new File(filePath);
            FileOutputStream fos = new FileOutputStream(dataStoreFile);

            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(tObject);

            out.close();
            fos.close();
        } catch (IOException e) {
            if (mActivity != null)
                LOG.logError(mActivity, "DataStore setObject failed: " + e.getMessage(), e, false);
        }
    }
}
