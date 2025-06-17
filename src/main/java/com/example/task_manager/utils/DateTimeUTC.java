package com.example.task_manager.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeUTC {
    public static Date now() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        return Date.from(zonedDateTime.toInstant());
    }
}
