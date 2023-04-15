/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ai.metaheuristic.mhbp.utils.dispatcher_schedule;

import ai.metaheuristic.mhbp.utils.S;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ToString(of = "asString")
public class DispatcherSchedule {

    private final List<LocalDate> holidays = new ArrayList<>();
    private final List<LocalDate> exceptionWorkingDays = new ArrayList<>();
    private TimePeriods monday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods tuesday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods wednesday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods thursday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods friday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods saturday = TimePeriods.ALWAYS_ACTIVE;
    private TimePeriods sunday = TimePeriods.ALWAYS_ACTIVE;
    public final ExtendedTimePeriod.SchedulePolicy policy;

/*
    Monday 	    Mon. 	Mo.
    Tuesday 	Tue. 	Tu.
    Wednesday 	Wed. 	We.
    Thursday 	Thu. 	Th.
    Friday 	    Fri. 	Fr.
    Saturday 	Sat. 	Sa.
    Sunday 	    Sun. 	Su
*/

    public final String asString;

    private static final ConcurrentHashMap<String, DispatcherSchedule> cache = new ConcurrentHashMap<>();

    public static DispatcherSchedule createDispatcherSchedule(@Nullable String cfg) {
        String key = S.b(cfg) ? "" : cfg;
        return cache.computeIfAbsent(key, DispatcherSchedule::new);
    }

    private DispatcherSchedule(@Nullable String cfg) {
        if (S.b(cfg)) {
            this.policy = ExtendedTimePeriod.SchedulePolicy.normal;
            this.asString = "";
            return;
        }
        this.asString = cfg;
        final ExtendedTimePeriod config = ExtendedTimePeriodUtils.to(cfg);
        this.policy = config.policy==null ? ExtendedTimePeriod.SchedulePolicy.normal : config.policy;

        try {
            if (!S.b(config.workingDay)) {
                TimePeriods workingDay = TimePeriods.from(config.workingDay);
                monday = tuesday = wednesday = thursday = friday = workingDay;
            }
            if (!S.b(config.weekend)) {
                TimePeriods weekend = TimePeriods.from(config.weekend);
                saturday = sunday = weekend;
            }
            if (config.week!=null) {
                if (!S.b(config.week.mon)) {
                    monday = TimePeriods.from(config.week.mon);
                }
                if (!S.b(config.week.tue)) {
                    tuesday = TimePeriods.from(config.week.tue);
                }
                if (!S.b(config.week.wed)) {
                    wednesday = TimePeriods.from(config.week.wed);
                }
                if (!S.b(config.week.thu)) {
                    thursday = TimePeriods.from(config.week.thu);
                }
                if (!S.b(config.week.fri)) {
                    friday = TimePeriods.from(config.week.fri);
                }
                if (!S.b(config.week.sat)) {
                    saturday = TimePeriods.from(config.week.sat);
                }
                if (!S.b(config.week.sun)) {
                    sunday = TimePeriods.from(config.week.sun);
                }
            }

            if (config.dayMask!=null) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern(config.dayMask);

                toLocalDate(fmt, config.holiday, holidays);
                toLocalDate(fmt, config.exceptionWorkingDay, exceptionWorkingDays);
            }
        } catch (ParseException e) {
            log.error("Error", e);
            throw new IllegalStateException("Error", e);
        }
    }

    private static void toLocalDate(DateTimeFormatter fmt, String datesAsStr, List<LocalDate> dates) throws ParseException {
        if (StringUtils.isBlank(datesAsStr)) {
            return;
        }
        for (StringTokenizer st = new StringTokenizer(datesAsStr, ","); st.hasMoreTokens(); ) {
            String token = st.nextToken().trim();
            if (StringUtils.isBlank(token)) {
                continue;
            }
            dates.add(LocalDate.parse( token, fmt));
        }
    }

    private static void toCalendars(String dayMask, String dates, List<Calendar> calendars) throws ParseException {
        for (StringTokenizer st = new StringTokenizer(dates, ","); st.hasMoreTokens(); ) {
            String token = st.nextToken().trim();
            if (StringUtils.isBlank(token)) {
                continue;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(dayMask);
            Date date = sdf.parse(token);

            Calendar c = Calendar.getInstance();
            c.setTime(date);

            calendars.add(c);
        }
    }

    public boolean isActive(final LocalDateTime time) {
        final LocalDateTime curr = time.withSecond(0).withNano(0);

        if (holidays.contains(curr.toLocalDate())) {
            return weekendIsActive(curr.toLocalTime());
        }
        if (exceptionWorkingDays.contains(curr.toLocalDate())) {
            return workingDaysActive(curr.toLocalTime());
        }
        TimePeriods periods;
        int i = curr.get(ChronoField.DAY_OF_WEEK);
        periods = switch (i) {
            case 1 -> monday;
            case 2 -> tuesday;
            case 3 -> wednesday;
            case 4 -> thursday;
            case 5 -> friday;
            case 6 -> saturday;
            case 7 -> sunday;
            default -> throw new IllegalStateException("Wrong number of day of week " + i);
        };
        return periods.isActive(curr.toLocalTime());
    }

    private boolean workingDaysActive(LocalTime time) {
        return monday.isActive(time) || tuesday.isActive(time) || wednesday.isActive(time) ||
                thursday.isActive(time) || friday.isActive(time);
    }

    private boolean weekendIsActive(LocalTime time) {
        return saturday.isActive(time) || sunday.isActive(time);
    }

    public boolean isCurrentTimeActive() {
        return !isCurrentTimeInactive();
    }

    public boolean isCurrentTimeInactive() {
        final LocalDateTime now = LocalDateTime.now();

        //noinspection RedundantIfStatement
        if (isActive(now)) {
            return false;
        } else {
            return true;
        }
    }

}
