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

package ai.metaheuristic.mhbp.provider;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.data.RequestContext;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:45 AM
 */
public class ProviderData {

    public static class Provider {
        public String code;
        public String name;
        public String url;
        public String token;
    }

    public static class Providers {
        public static final List<Provider> providers = new ArrayList<>();
    }

    public record QueriedData(
            String queryText, @Nullable Enums.AgeGroup ageGroup, @Nullable RequestContext context){}

    public record QuestionAndAnswer(@Nullable String q, @Nullable String a, Enums.OperationStatus status, @Nullable String error) {
        public QuestionAndAnswer(Enums.OperationStatus status) {
            this(null, null, status, null);
        }

        public QuestionAndAnswer(Enums.OperationStatus status, String error) {
            this(null, null, status, error);
        }
    }
}
