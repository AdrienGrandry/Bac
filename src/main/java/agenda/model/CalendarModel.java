package agenda.model;

import java.time.LocalDate;
import java.util.*;

public class CalendarModel {

    private final Map<LocalDate, List<EventModel>> eventsByDate = new HashMap<>();

    public void setEvents(List<EventModel> events) {
        eventsByDate.clear();
        for (EventModel event : events) {
            eventsByDate.computeIfAbsent(event.getDate(), k -> new ArrayList<>()).add(event);
        }
    }

    public List<EventModel> getEventsForDate(LocalDate date) {
        return eventsByDate.getOrDefault(date, Collections.emptyList());
    }
}