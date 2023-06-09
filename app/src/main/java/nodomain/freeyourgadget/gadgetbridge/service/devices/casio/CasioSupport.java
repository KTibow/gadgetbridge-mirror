package nodomain.freeyourgadget.gadgetbridge.service.devices.casio;

import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import nodomain.freeyourgadget.gadgetbridge.devices.casio.CasioConstants;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.model.Alarm;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CallSpec;
import nodomain.freeyourgadget.gadgetbridge.model.CannedMessagesSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicSpec;
import nodomain.freeyourgadget.gadgetbridge.model.MusicStateSpec;
import nodomain.freeyourgadget.gadgetbridge.model.NotificationSpec;
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec;
import nodomain.freeyourgadget.gadgetbridge.service.btle.AbstractBTLEDeviceSupport;
import nodomain.freeyourgadget.gadgetbridge.service.devices.huami.HuamiSupport;

public class CasioSupport extends AbstractBTLEDeviceSupport {

    protected boolean mFirstConnect = false;
    private static final Logger LOG = LoggerFactory.getLogger(HuamiSupport.class);

    public CasioSupport() {
        this(LOG);
    }

    public CasioSupport(Logger logger) {
        super(logger);
        addSupportedService(CasioConstants.WATCH_FEATURES_SERVICE_UUID);
    }

    @Override
    public boolean connectFirstTime() {
        mFirstConnect = true;
        return connect();
    }

    protected byte[] prepareCurrentTime() {
        byte[] arr = new byte[10];
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        arr[0] = (byte)((year >>> 0) & 0xff);
        arr[1] = (byte)((year >>> 8) & 0xff);
        arr[2] = (byte)(1 + cal.get(Calendar.MONTH));
        arr[3] = (byte)cal.get(Calendar.DAY_OF_MONTH);
        arr[4] = (byte)cal.get(Calendar.HOUR_OF_DAY);
        arr[5] = (byte)cal.get(Calendar.MINUTE);
        arr[6] = (byte)(1 + cal.get(Calendar.SECOND));
        byte dayOfWk = (byte)(cal.get(Calendar.DAY_OF_WEEK) - 1);
        if(dayOfWk == 0)
            dayOfWk = 7;
        arr[7] = dayOfWk;
        arr[8] = (byte)(int) TimeUnit.MILLISECONDS.toSeconds(256 * cal.get(Calendar.MILLISECOND));
        arr[9] = 1; // or 0?
        return arr;
    }

    public void setInitialized() {
        mFirstConnect = false;
        gbDevice.setState(GBDevice.State.INITIALIZED);
        gbDevice.sendDeviceUpdateIntent(getContext());
    }

    @Override
    public boolean useAutoConnect() {
        return true;
    }
}
