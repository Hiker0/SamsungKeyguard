package com.allen.hq.keygurad;

import android.os.BatteryManager;

public class BatteryStatus {
	 static final int LOW_BATTERY_THRESHOLD = 16;
	 static final int BATTERY_STATUS_FULL = 5;
	 static final int BATTERY_PLUGGED_WIRELESS = 4;
	 
    public final int status;
    public final int level;
    public final int plugged;
    public final int health;
    public BatteryStatus(int status, int level, int plugged, int health) {
        this.status = status;
        this.level = level;
        this.plugged = plugged;
        this.health = health;
    }

    /**
     * Determine whether the device is plugged in (USB, power, or wireless).
     * @return true if the device is plugged in.
     */
    boolean isPluggedIn() {
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
    }

    /**
     * Whether or not the device is charged. Note that some devices never return 100% for
     * battery level, so this allows either battery level or status to determine if the
     * battery is charged.
     * @return true if the device is charged
     */
    public boolean isCharged() {
        return status == BATTERY_STATUS_FULL || level >= 100;
    }

    /**
     * Whether battery is low and needs to be charged.
     * @return true if battery is low
     */
    public boolean isBatteryLow() {
        return level < LOW_BATTERY_THRESHOLD;
    }

}
