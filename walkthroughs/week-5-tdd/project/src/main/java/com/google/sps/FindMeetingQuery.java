 
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
import java.util.Comparator;

public final class FindMeetingQuery {
  
  private static final int MINS_IN_DAY = 1440;

  /**
  * Given a list of events and a meeting request, finds all possible time slots for the 
  * meeting that would satisfy each attendees schedule. If at least one time slot will allow 
  * optional attendees to attend, they are returned. Otherwise, only mandatory attendees are 
  * considered.
  */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeWithAttendees> eventTimes = getConlfictingEvents(events, request);

    int[] dayArr = getDayArray(eventTimes);

    List<Pair> possibleTimes = getPossibleTimes(dayArr, request);

    List<TimeRange> optimalTimes = new ArrayList<TimeRange>();

    int i = 0;

    if (possibleTimes.size() == 1) {
      optimalTimes.add(possibleTimes.get(0).getTimeRange());
    }
    else if (possibleTimes.size() > 0) {
    
      int maxOptionalAttendees = possibleTimes.get(0).getAvailableAttendees(); 
    
      while (i < possibleTimes.size() && possibleTimes.get(i).getAvailableAttendees() == maxOptionalAttendees) {
        optimalTimes.add(possibleTimes.get(i).getTimeRange());
        i++;
      }
    }

    return optimalTimes;

   }


  private List<TimeWithAttendees> getConlfictingEvents(Collection<Event> events, MeetingRequest request) {
      List<TimeWithAttendees> eventTimes = new ArrayList<TimeWithAttendees>();
    
      for (Event event : events) {
        TimeWithAttendees timeWithAttendees = new TimeWithAttendees();
        timeWithAttendees.range = event.getWhen();

        timeWithAttendees.noMandatoryConlicts = Collections.disjoint(event.getAttendees(), request.getAttendees());

        Collection<String> optionalAttendees = request.getOptionalAttendees();

        for (String attendee : optionalAttendees) {
          if (event.getAttendees().contains(attendee)) {
            timeWithAttendees.numOfOptionalConflicts++;
          }
        }  
      
        if (!timeWithAttendees.noMandatoryConlicts || timeWithAttendees.numOfOptionalConflicts > 0) {
          eventTimes.add(timeWithAttendees);
        }
      }

      return eventTimes;
  }

  private int[] getDayArray(List<TimeWithAttendees> eventTimes) {
      int[] dayArr = new int[MINS_IN_DAY];

      for (TimeWithAttendees eventTime : eventTimes) {
        int numToAdd = 0;

        // if there are mandatory conflicts, set to -1
        if (!eventTime.noMandatoryConlicts) {
          numToAdd = -1;
        }
        else {
          // if no mandatory conflicts, add num of optional attendees that can attend
          numToAdd = eventTime.numOfOptionalConflicts; 
        }

        int rangeStart = eventTime.range.start();
        int rangeEnd = eventTime.range.end();
        for (int i = rangeStart; i < rangeEnd; i++) {
          if (dayArr[i] != -1) {
            dayArr[i] += numToAdd;
          } 
        }
      }

      return dayArr;
  }

  private List<Pair> getPossibleTimes(int[] dayArr, MeetingRequest request) {
    List<Pair> possibleTimes = new ArrayList<Pair>();

    int curTime = 0;

    int curDuration = 0;

    Boolean possibleIgnoreOptional = false;

    int lastDuration = 0;

    while (curTime < MINS_IN_DAY) {
        int optionalAttendeesAvailable = request.getOptionalAttendees().size() - dayArr[curTime];

        if (dayArr[curTime] == -1) {
          curTime++;
        }
        else if (curTime == MINS_IN_DAY - 1) {
          if ((dayArr[curTime] == dayArr[curTime - 1]) && (curDuration >= request.getDuration())) {
            Pair timeAndOptAttendees = Pair.fromTimeAttendees(curDuration, curTime, optionalAttendeesAvailable);
            possibleTimes.add(timeAndOptAttendees);
          }
          curTime++;
        }
        else if (dayArr[curTime] == dayArr[curTime + 1]) {
          curDuration++;
          curTime++;
        }
        else {
          if (curDuration + 1 >= request.getDuration()) {
            Pair timeAndOptAttendees = Pair.fromTimeAttendees(curDuration, curTime, optionalAttendeesAvailable);
            possibleTimes.add(timeAndOptAttendees);
            possibleIgnoreOptional = false;
          }
          else {
              if (possibleIgnoreOptional) {
                int totalDuration = curDuration + lastDuration + 1;
                
                Pair timeAndOptAttendees = Pair.fromTimeAttendees(totalDuration, curTime, optionalAttendeesAvailable);

                if (timeAndOptAttendees.getTimeRange().duration() >= request.getDuration()) {
                  possibleTimes.add(timeAndOptAttendees);
                }

                lastDuration = 0;
                possibleIgnoreOptional = false;
              }
              else if (optionalAttendeesAvailable == 0 || request.getOptionalAttendees().size() - dayArr[curTime + 1] == 0) {
                possibleIgnoreOptional = true;
                lastDuration = curDuration;
              }
          }
          curDuration = 0; 
          curTime++;
        }
    }

    Collections.sort(possibleTimes, Pair.ORDER_BY_ATTENDEES);

    return possibleTimes;
  }

  /** Holds an event's time range, if there are no mandatory conflicts, and how many optional conflicts there are */
  class TimeWithAttendees {
    public TimeRange range;
    public Boolean noMandatoryConlicts;
    public int numOfOptionalConflicts;
  }
}