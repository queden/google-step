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

  private static final int MINS_IN_DAY = 1440;

  /**
   * Given a list of events and a meeting request, finds all possible time slots for the meeting
   * that would satisfy each attendees schedule. If there are optional attendees, displays the time
   * slots that allows all mandatory and the largest amount of optional to attend.
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Finds all times that work for all mandatory attendees, paired with how many
    // optional attendees can attend. The list is sorted by maximum amount of
    // optional attendees that can attend.
    List<Pair> possibleTimes = getPossibleTimes(events, request);

    List<TimeRange> optimalTimes = new ArrayList<TimeRange>();

    // Finds the events that allow for the most optional attendees to attend
    if (possibleTimes.size() == 1) {
      optimalTimes.add(possibleTimes.get(0).getTimeRange());
    } else if (possibleTimes.size() > 0) {
      int maxOptionalAttendees = possibleTimes.get(0).getAttendees();
      int i = 0;

      while (i < possibleTimes.size()
          && possibleTimes.get(i).getAttendees() == maxOptionalAttendees) {
        optimalTimes.add(possibleTimes.get(i).getTimeRange());
        i++;
      }
    }

    return optimalTimes;
  }

  /**
   * Returns a list of Pairs of possible times that work for all mandatory attendees and how many
   * optional attendees have conflicts at that time. The list is sorted by how many optional
   * attendees can attend.
   */
  private List<Pair> getPossibleTimes(Collection<Event> events, MeetingRequest request) {
    int[] dayArr = getDayArray(events, request);

    List<Pair> possibleTimes = new ArrayList<Pair>();

    int curTime = 0;
    int curDuration = 0;

    // Tracks if there is a timeslot that is too short to work for mandatory attendees, but may be
    // valid when
    // paired with a neighboring time range and ignoring its optional attendees
    Boolean possibleIgnoreOptional = false;
    // Duration of last time slot if we may need to ignore optional attendees to make it work
    int lastDuration = 0;

    // Iterates through the day array and creates TimeRanges for each time range that satisfies the
    // meeting request
    while (curTime < MINS_IN_DAY) {
      int optionalAttendeesAvailable = request.getOptionalAttendees().size() - dayArr[curTime];

      if (dayArr[curTime] == -1) { 
        // If there are conflicts for mandatory attendees, skips over the minute
        curTime++;
      } else if (curTime == MINS_IN_DAY - 1) {
        // If it is the last minute of the day and current time range has a valid duration, adds it
        // to
        // list of possible times
        if ((dayArr[curTime] == dayArr[curTime - 1]) && (curDuration >= request.getDuration())) {
          Pair timeAndOptAttendees =
              Pair.fromTimeAttendees(curDuration, curTime, optionalAttendeesAvailable);
          possibleTimes.add(timeAndOptAttendees);
        }
        curTime++;
      } else if (dayArr[curTime] == dayArr[curTime + 1]) {
        // If the next minute has the same amount of attendees available as the current minute (thus
        // being apart of the same time range), increments the duration of the current time range
        curDuration++;
        curTime++;
      } else if (curDuration + 1 >= request.getDuration()) {
        // If the next minute is not part of the same time range, and the current time range's
        // duration is valid, adds it to the list of possible times.
        Pair timeAndOptAttendees =
            Pair.fromTimeAttendees(curDuration, curTime, optionalAttendeesAvailable);
        possibleTimes.add(timeAndOptAttendees);
        possibleIgnoreOptional = false;
        curDuration = 0;
        curTime++;
      } else {
        // If the current duration is too short to be valid, but, when optional attendees are
        // ignored, could pair with a previous timeslot to be valid for mandatory attendees, the 
        // time range is added to the list
        if (possibleIgnoreOptional) {
          int totalDuration =
              curDuration + lastDuration + 1; // duration of current and previous time ranges

          Pair timeAndOptAttendees =
              Pair.fromTimeAttendees(totalDuration, curTime, optionalAttendeesAvailable);

          if (timeAndOptAttendees.getTimeRange().duration() >= request.getDuration()) {
            possibleTimes.add(timeAndOptAttendees);
          }

          lastDuration = 0;
          possibleIgnoreOptional = false;
        } else if (optionalAttendeesAvailable == 0
            || request.getOptionalAttendees().size() - dayArr[curTime + 1] == 0) {
          // If the current time slot works for no optional attendees, or the next time slot works
          // for no attendees, lastDuration is stored to see if, when paired with the next time 
          // ranges duration, it becomes a valid time range
          possibleIgnoreOptional = true;
          lastDuration = curDuration;
        }
        curDuration = 0;
        curTime++;
      }
    }

    // Sorts the possible times by amount of optional attendees that can attend, with
    // the time ranges that allow the most optional attendees to attend to go to the front
    Collections.sort(possibleTimes, Pair.ORDER_BY_ATTENDEES);

    return possibleTimes;
  }

  /**
   * Returns an array representation of a day, with each index representing a minute, and the value
   * at each meeting representing how many optional attendees have a conflict at that time. If a
   * minute does not work for all mandatory attendees, the value will be -1. If it is 0, it works
   * for all mandatory attendees and all optional attendees. Positive values reflect the number of
   * optional attendees that have conflict.
   */
  private int[] getDayArray(Collection<Event> events, MeetingRequest request) {
    List<TimeWithAttendees> eventTimes = getConflictingEvents(events, request);

    int[] dayArr = new int[MINS_IN_DAY];

    for (TimeWithAttendees eventTime : eventTimes) {
      int numToAdd = 0;

      // if there are mandatory conflicts, set to -1
      if (!eventTime.noMandatoryConlicts) {
        numToAdd = -1;
      } else {
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

  /**
   * Returns a list of TimeWithAttendees objects for all events that conflict with the meeting
   * request. TimeWithAttendees holds the event's TimeRange, if there are no conflicts for mandatory
   * attendees, and how many optional attendees have conflcits.
   */
  private List<TimeWithAttendees> getConflictingEvents(
      Collection<Event> events, MeetingRequest request) {
    List<TimeWithAttendees> eventTimes = new ArrayList<TimeWithAttendees>();

    for (Event event : events) {
      TimeWithAttendees timeWithAttendees = new TimeWithAttendees();
      timeWithAttendees.range = event.getWhen();

      // If the no mandatory attendees are at the current event, noMandatory conflicts is
      // set to true
      timeWithAttendees.noMandatoryConlicts =
          Collections.disjoint(event.getAttendees(), request.getAttendees());

      Collection<String> optionalAttendees = request.getOptionalAttendees();

      // numOfOptionalConflicts is set to the number of optional attendees 
      // attending this event
      for (String attendee : optionalAttendees) {
        if (event.getAttendees().contains(attendee)) {
          timeWithAttendees.numOfOptionalConflicts++;
        }
      }

      // Adds to the conflicting event list if there are conflicts for either mandatory of 
      // optional attendees
      if (!timeWithAttendees.noMandatoryConlicts || timeWithAttendees.numOfOptionalConflicts > 0) {
        eventTimes.add(timeWithAttendees);
      }
    }

    return eventTimes;
  }

  /**
   * Holds an event's time range, whether there are no mandatory conflicts, and how many optional
   * conflicts there are
   */
  class TimeWithAttendees {
    public TimeRange range;
    public Boolean noMandatoryConlicts;
    public int numOfOptionalConflicts;
  }
}