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

package ai.metaheuristic.mhbp.utils;

import ai.metaheuristic.mhbp.data.ApiData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.client.fluent.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:19 PM
 */
public class RestUtils {

    public static void putNoCacheHeaders(Map<String, String> map) {
        map.put("cache-control", "no-cache");
        map.put("expires", "Tue, 01 Jan 1980 1:00:00 GMT");
        map.put("pragma", "no-cache");
    }

    public static void addHeaders(Request request) {
        Map<String, String> map = new HashMap<>();
        putNoCacheHeaders(map);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

    public static HttpHeaders getHeader(long length) {
        return getHeader(null, length);
    }

    public static HttpHeaders getHeader(@Nullable HttpHeaders httpHeaders, long length) {
        HttpHeaders header = httpHeaders != null ? httpHeaders : new HttpHeaders();
        header.setContentLength(length);
        header.setCacheControl("max-age=0");
        header.setExpires(0);
        header.setPragma("no-cache");

        return header;
    }

    public static ResponseEntity<String> returnError(HttpStatus status, String err) throws JsonProcessingException {
        final ApiData.SimpleError error = new ApiData.SimpleError(err);
        String json = JsonUtils.getMapper().writeValueAsString(error);
        return new ResponseEntity<>(json, status);
    }
}