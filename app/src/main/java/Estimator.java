import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Nishanth on 27-Jan-18.
 */

public class Estimator extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);
    }
}
