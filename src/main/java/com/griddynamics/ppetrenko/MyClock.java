package com.griddynamics.ppetrenko;

import java.time.Clock;

public class MyClock {
    private static Clock clock = Clock.systemDefaultZone();

    public static void setClock(Clock clockParam) {
        clock = clockParam;
    }

    public static Clock getClock() {
        return clock;
    }
}
