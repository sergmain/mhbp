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
import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 10:07 PM
 */
public class ProviderQueryUtils {

    public static String processAnswerFromApi(String json, ApiScheme.Response response) {
        if (response.type==Enums.PromptResponseType.text) {
            return json;
        }
        if (response.type==Enums.PromptResponseType.json) {
            DocumentContext jsonContext = JsonPath.parse(json);
            String content = jsonContext.read(response.path);
            return content;
        }
        throw new IllegalStateException();
    }

}
