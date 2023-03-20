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

import ai.metaheuristic.mhbp.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/8/2023
 * Time: 10:05 AM
 */
@Slf4j
public class CleanerInterceptor implements AsyncHandlerInterceptor {

    @SuppressWarnings("unchecked")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        List<Path> toClean = (List<Path>) request.getAttribute(Consts.RESOURCES_TO_CLEAN);
        if (toClean!=null && !toClean.isEmpty()) {
            DirUtils.deletePaths(toClean);
        }
    }

}

