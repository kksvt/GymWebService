package uj.wmii.jwzp.config.timezone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZoneOffset;

//todo: consider having the database always use the UTC zoneId
@Configuration
public class UTCTimeZoneService implements TimeZoneService {
    @Override
    @Bean
    public ZoneId getZoneId() {
        return ZoneOffset.UTC;
    }
}
