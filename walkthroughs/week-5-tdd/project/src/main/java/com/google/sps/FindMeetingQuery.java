 
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
import java.util.HashSet;
import java.util.Arrays;

public final class FindMeetingQuery {
  /**
  * Given a list of events and a meeting request, finds all possible time slots for the 
  * meeting that would satisfy each attendees schedule. If at least one time slot will allow 
  * optional attendees to attend, they are returned. Otherwise, only mandatory attendees are 
  * considered.
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
      // Get a set of all (mandatory and optional) attendees
      Collection<String> allAttendees = new HashSet<>();
      Collection<String> mandatoryAtts = request.getAttendees();
      Collection<String> optAtts = request.getOptionalAttendees();
      
      for (String attendee : mandatoryAtts) {
        allAttendees.add(attendee);
      }

      for (String optAttendee : optAtts) {
        allAttendees.add(optAttendee);
      }

      // Get what time slots satsifies all attendees schedules
      Collection<TimeRange> allAttSlots = query(events, request, allAttendees);

      // If timeslots are available that satisfy all attendees,
      // those are returned. Otherwise, only slots that satisfy
      // mandatory attendees are returned
      if (allAttSlots.size() > 0) {
        return allAttSlots;
      }
      else if (request.getAttendees().size() > 0) {
        return query(events, request, request.getAttendees());
      }
      else {
        // No timeslots that satisfy everyone nor just mandatory attendees
        return Arrays.asList();
      }
  }

  private Collection<TimeRange> query(Collection<Event> events, MeetingRequest request, Collection<String> attendees) {
    List<TimeRange> eventTimes = new ArrayList<TimeRange>();
    // First find all events with attendees, add their time range to a list
    for (Event event : events) {
      if (!Collections.disjoint(event.getAttendees(), attendees)) {
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