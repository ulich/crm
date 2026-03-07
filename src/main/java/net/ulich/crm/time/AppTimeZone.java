package net.ulich.crm.time;

import java.time.ZoneId;

public final class AppTimeZone {
    public static final Companion Companion = new Companion();
    public static final ZoneId BERLIN = Companion.BERLIN;

    private AppTimeZone() {
    }

    public static final class Companion {
        public static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

        private Companion() {
        }
    }
}
