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

import lombok.Data;

@Data
public class ExtendedTimePeriod {

    public enum SchedulePolicy {
        normal, strict
    }

    @Data
    public static class WeekTimePeriod {
        public String mon;
        public String tue;
        public String wed;
        public String thu;
        public String fri;
        public String sat;
        public String sun;
    }

    public String workingDay;
    public String weekend;
    public String dayMask;
    public String holiday;
    public String exceptionWorkingDay;
    public WeekTimePeriod week;
    public SchedulePolicy policy = SchedulePolicy.normal;
}
