// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Comparator;

/**
 * A Pair holds a time range and the number of optional attendees that are available in that Time
 * Range
 */
public final class Pair {
  private TimeRange timeRange;
  private int attendees;

  /** Constructs a Pair from a time range and number of attendees available */
  private Pair(TimeRange timeRange, int attendees) {
    this.timeRange = timeRange;
    this.attendees = attendees;
  }

  /** Returns the Pair's time range */
  public TimeRange getTimeRange() {
    return timeRange;
  }

  /** Returns the Pair's available attendees */
  public int getAttendees() {
    return attendees;
  }

  /** Creates an instance of a Pair from a TimeRange's duration, end, and available attendees */
  public static Pair fromTimeAttendees(int duration, int endTime, int attendees) {
    TimeRange curTimeRange = TimeRange.fromStartEnd(endTime - duration, endTime, true);
    return new Pair(curTimeRange, attendees);
  }

  /** A comparator for sorting Pairs based on maximum amount of attendees available */
  public static final Comparator<Pair> ORDER_BY_ATTENDEES =
      new Comparator<Pair>() {
        @Override
        public int compare(Pair a, Pair b) {
          return Integer.compare(b.getAttendees(), a.getAttendees());
        }
      };
}