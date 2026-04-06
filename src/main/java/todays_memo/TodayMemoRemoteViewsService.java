package todays_memo;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

public class TodayMemoRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TodayMemoRemoteViewsFactory(this.getApplicationContext(),intent);
    }

}
