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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:39 PM
 */
public class S {
    public static @NonNull String f(@NonNull String format, @Nullable Object... args) {
        return Objects.requireNonNull(String.format(format, args));
    }

    public static @NonNull String f(@NonNull Locale l, @NonNull String format, Object... args) {
        return String.format(l, format, args);
    }

    public static boolean b(@Nullable String s) {
        return s==null || s.isBlank();
    }
}
