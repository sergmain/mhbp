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

package ai.metaheuristic.mhbp.nlu;

import ai.metaheuristic.mhbp.data.NluData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:49 PM
 */
public class NluTextProcessingUtils {

    public static final Pattern QUERY_TYPE_PATTERN = Pattern.compile("^((\\s+)?.*)?(?<type>(what|when|who|where|how|why|which)).*$", Pattern.UNICODE_CASE);

    public static final Pattern WEATHER_PATTERN = Pattern.compile("^(what\\s+)?(is\\s+)?(the\\s+)?weather in (?<city>.*)$", Pattern.UNICODE_CASE);

    @Nullable
    public static NluData.QueriedInfo parse(String rawText) {
        String text = rawText.strip();
        final String type = queryType(text);
        if (type==null) {
            // default 'what is'
            return parseWhatIs(text);
        }
        return switch (type) {
            case "what" -> parseWhatIs(text);
            case "when" -> parseWhen(text);
            case "who" -> parseWho(text);
            case "where" -> parseWhere(text);
            case "how" -> parseHow(text);
            case "why" -> parseWhy(text);
            case "which" -> parseWhich(text);
            default -> null;
        };
    }

    @Nullable
    public static String queryType(String text) {
        final Matcher matcher = QUERY_TYPE_PATTERN.matcher(text);
        return matcher.find() ? matcher.group("type") : null;
    }

    // text must be in lower-case
    public static String getQueryType(String text) {
        final Matcher matcher = QUERY_TYPE_PATTERN.matcher(text);
        String type = "what";
        if (matcher.find()) {
            type = matcher.group("type");
        }
        return type;
    }

    @Nullable
    private static NluData.QueriedInfo parseWhere(String text) {
        return null;
    }

    @Nullable
    private static NluData.QueriedInfo parseWho(String text) {
        return null;
    }

    @Nullable
    private static NluData.QueriedInfo parseHow(String text) {
        return null;
    }

    @Nullable
    private static NluData.QueriedInfo parseWhy(String text) {
        return null;
    }

    @Nullable
    private static NluData.QueriedInfo parseWhich(String text) {
        return null;
    }

    @Nullable
    private static NluData.QueriedInfo parseWhen(String text) {
        return null;
    }

    @Nullable
    public static NluData.QueriedInfo parseWhatIs(String text) {
        final Matcher matcher = WEATHER_PATTERN.matcher(text);
        if (matcher.find()) {
            String c = matcher.group("city").toLowerCase();
            String city;
            int idx = c.indexOf(',');
            List<NluData.Property> props = new ArrayList<>();
            if (idx!=-1) {
                city = c.substring(0, idx);
                String s = c.substring(idx+1).strip();
                props.addAll(initTemperatureProp(s));
            }
            else {
                idx = c.lastIndexOf(' ');
                if (idx!=-1) {
                    String s = c.substring(idx+1).strip();
                    List<NluData.Property> pps = initTemperatureProp(s);
                    if (!pps.isEmpty()) {
                        city = c.substring(0, idx);
                        props.addAll(initTemperatureProp(s));
                    }
                    else {
                        city = c;
                    }
                }
                else {
                    city = c;
                }
            }
            props.add(new NluData.Property("city", city));
            return new NluData.QueriedInfo("weather", props);
        }
        return null;
    }

    private static List<NluData.Property> initTemperatureProp(String s) {
        List<NluData.Property> props = new ArrayList<>();
        if (StringUtils.equalsAny(s, "c", "celsius")) {
            props.add(new NluData.Property("temperature in celsius", "C"));
        }
        else if (StringUtils.equalsAny(s, "f", "fahrenheit")) {
            props.add(new NluData.Property("temperature in fahrenheit", "F"));
        }
        return props;
    }
}
