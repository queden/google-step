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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> eventTimes = new ArrayList<TimeRange>();
    // First find all events with attendees, add their time range to a list
    for (Event event : events) {
      if (!Collections.disjoint(event.getAttendees(), request.getAttendees())) {
        eventTimes.add(event.getWhen());
      }
    }

    // Sort time range list by start time
    Collections.sort(eventTimes, TimeRange.ORDER_BY_START);

    // Starting from beginning of day, look for time ranges between events with
    // at least the amount of time needed
    int lastTime = TimeRange.START_OF_DAY;
    Collection<TimeRange> possibleTimes = new ArrayList<TimeRange>();
    for (TimeRange time : eventTimes) {
      if (time.start() - lastTime >= request.getDuration()) {
        possibleTimes.add(TimeRange.fromStartEnd(lastTime, time.start(), false));
      }

      if (time.end() >= lastTime) {
        lastTime = time.end();
      }
    }

    if (lastTime < TimeRange.END_OF_DAY
        && (TimeRange.END_OF_DAY - lastTime) >= request.getDuration()) {
      possibleTimes.add(TimeRange.fromStartEnd(lastTime, TimeRange.END_OF_DAY, true));
    }

    return possibleTimes;
  }
}