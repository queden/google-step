package com.google.sps;

import java.util.Comparator;

public final class Pair {
    private TimeRange timeRange;
    private int availableAttendees;

    private Pair(TimeRange timeRange, int availableAttendees) {
      this.timeRange = timeRange;
      this.availableAttendees = availableAttendees;
    }

    public TimeRange getTimeRange() {
      return timeRange;
    }

    public int getAvailableAttendees() {
     return availableAttendees;
    }

    public static Pair fromTimeAttendees(int duration, int endTime, int attendees) {
      TimeRange curTimeRange = TimeRange.fromStartEnd(endTime - duration, endTime, true);
      return new Pair(curTimeRange, attendees);
    }

    public static final Comparator<Pair> ORDER_BY_ATTENDEES = new Comparator<Pair>() {
      @Override
      public int compare(Pair a, Pair b) {
        return -1 * Integer.compare(a.getAvailableAttendees(), b.getAvailableAttendees());
      }
    };

    @Override
    public String toString() {
      return timeRange.toString() + " Optional Attendees Available: " + availableAttendees;
    }
}