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

package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:14 PM
 */
public class NluData {

    @Data
    @AllArgsConstructor
    public static class Property {
        public String name;
        public String value;
    }

    @Data
    @AllArgsConstructor
    public static class QueriedInfo {
        public String object;
        public List<Property> properties;

        @Nullable
        public Property findByName(String name) {
            for (Property property : properties) {
                if (property.name.equals(name)) {
                    return property;
                }
            }
            return null;
        }
    }

    @Data
    @EqualsAndHashCode
    public static class ProcessedQuery {
        public QueriedInfo info;
        public String text;
    }
}
