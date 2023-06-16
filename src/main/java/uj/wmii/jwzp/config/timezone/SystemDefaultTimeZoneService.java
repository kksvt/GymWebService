package uj.wmii.jwzp.config.timezone;

import java.time.ZoneId;

public class SystemDefaultTimeZoneService implements TimeZoneService {

    @Override
    public ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }
}
