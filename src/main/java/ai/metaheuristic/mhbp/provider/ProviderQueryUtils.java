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
import ai.metaheuristic.mhbp.api.model.ApiModel;
import ai.metaheuristic.mhbp.data.ApiData;
import ai.metaheuristic.mhbp.data.NluData;
import ai.metaheuristic.mhbp.nlu.NluTextProcessingUtils;
import ai.metaheuristic.mhbp.utils.JsonUtils;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 10:07 PM
 */
public class ProviderQueryUtils {
    static String processAnswerFromApi(String json, ApiModel.Meta meta) throws JsonProcessingException {
        Object obj = JsonUtils.getMapper().readValue(json, Object.class);
        String field = meta.param;

        return meta.object + " is " + searchForValue(obj, field);
    }

    @Nullable
    private static String searchForValue(Object obj, String field) {
        if (obj instanceof List list) {
            for (Object o : list) {
                final String value = searchForValue(o, field);
                if (value!=null) {
                    return value;
                }
            }
        }
        else if (obj instanceof Map map) {
            Set<Map.Entry<Object, Object>> set = map.entrySet();
            for (Map.Entry<Object, Object> entry : set) {
                if (entry.getKey().equals(field)) {
                    return entry.getValue().toString();
                }
                final String value = searchForValue(entry.getValue(), field);
                if (value!=null) {
                    return value;
                }
            }
        }
        return null;
    }

    @Nullable
    public static ApiModel.Meta getFieldForLookingFor(ApiModel model, NluData.QueriedInfo queriedInfo) {
        for (ApiModel.MetaWithResponse meta : model.model.metas) {
            if (queriedInfo.object.equals(meta.meta.object)) {
                if (meta.response.attrs==null || meta.response.attrs.isEmpty()) {
                    continue;
                }
                for (ApiModel.Meta attr : meta.response.attrs) {
                    for (NluData.Property property : queriedInfo.properties) {
                        if (attr.object.equals(property.name)) {
                            return attr;
                        }
                    }
                }
                return meta.response.attrs.get(0);
            }
        }
        return null;
    }

    public static ApiData.QueriedInfoWithError getQueriedInfo(ProviderData.QueriedData queriedData) {
        if (queriedData.queryText().length()>1024) {
            return ApiData.QueriedInfoWithError.asError("text is too long or not specified", Enums.QueryResultErrorType.common);
        }
        if (S.b(queriedData.queryText())) {
            return ApiData.QueriedInfoWithError.asError("text is blank", Enums.QueryResultErrorType.common);
        }

        NluData.QueriedInfo queriedInfo = NluTextProcessingUtils.parse(queriedData.queryText());
        if (queriedInfo==null) {
            return ApiData.QueriedInfoWithError.asError("can't understand a text of query", Enums.QueryResultErrorType.cant_understand);
        }

        return new ApiData.QueriedInfoWithError(queriedInfo, null);
    }
}
