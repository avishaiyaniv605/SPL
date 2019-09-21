package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast is a message represents the time ticks in the program
 */
public class TickBroadcast implements Broadcast {

    private final int _tick;

    public TickBroadcast(int currentTick) {
        _tick = currentTick;
    }

    /**
     * Retrieves the tick number
     * @return int which is the current tick
     */
    public int getCurrentTick() {
        return _tick;
    }
}
