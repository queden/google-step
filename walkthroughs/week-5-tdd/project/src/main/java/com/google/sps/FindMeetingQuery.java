 
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
    List<TimeWithAttendees> eventTimes = new ArrayList<TimeRange>();
    // First find all events with attendees, add their time range and conflicts to a list
    for (Event event : events) {
      TimeWithAttendees timeWithAttendees = new TimeWithAttendees();
      timeWithAttendees.range = event.getWhen();

      if (!Collections.disjoint(event.getAttendees(), request.getAttendees())) {
        timeWithAttendees.noMandatoryConlicts = true;
      }
      else {
        timeWithAttendees.noMandatoryConlicts = false;
      }

      Collection<String> optionalAttendees = request.getOptionalAttendees();

      for (String attendee : optionalAttendees) {
        if (event.getAttendees().contains(attendee)) {
          timeWithAttendees.numOfOptionalConflicts++;
        }
      }
      
      if (!timeWithAttendees.noMandatoryConlicts && numOfOptionalConflicts > 0) {
        eventTimes.add(timeWithAttendees);
      }
    }

    int[] dayArr = new int[1440];

    for (TimeWithAttendees eventTime : eventTimes) {
      int numToAdd = 0;

      // if there are mandatory conflicts, set to -1
      if (!eventTime.noMandatoryConlicts) {
        numToAdd = -1;
      }
      else {
        // if no mandatory conflicts, add num of optional attendees that can attend
        numToAdd = request.getOptionalAttendees().size() - eventTime.numOfOptionalConflicts; 
      }

      int rangeStart = eventTime.range.start();
      int rangeEnd = eventTime.range.end();
      for (int i = rangeStart; i <= rangeEnd; i++) {
        if (dayArr[i] != -1) {
            dayArr[i] += numToAdd;
        } 
      }
    }

    for (int i = 0; i < dayArr.length; i++) {
      if (dayArr[i] == 0) {
        dayArr[i] = request.getOptionalAttendees().size()
      }
    }


    
   }
    
  /** Holds an event's time range, if there are no mandatory conflicts, and how many optional conflicts there are */
  class TimeWithAttendees {
    public TimeRange range;
    public Boolean noMandatoryConlicts;
    public int numOfOptionalConflicts;
  }
}



// // Sort time range list by start time
//     Collections.sort(eventTimes, TimeRange.ORDER_BY_START);

//     // Starting from beginning of day, look for time ranges between events with
//     // at least the amount of time needed
//     int lastTime = TimeRange.START_OF_DAY;
//     Collection<TimeRange> possibleTimes = new ArrayList<TimeRange>();
//     for (TimeRange time : eventTimes) {
//       if (time.start() - lastTime >= request.getDuration()) {
//         possibleTimes.add(TimeRange.fromStartEnd(lastTime, time.start(), false));
//       }

//       if (time.end() >= lastTime) {
//         lastTime = time.end();
//       }
//     }

//     if (lastTime < TimeRange.END_OF_DAY
//         && (TimeRange.END_OF_DAY - lastTime) >= request.getDuration()) {
//       possibleTimes.add(TimeRange.fromStartEnd(lastTime, TimeRange.END_OF_DAY, true));
//     }

//     return possibleTimes;