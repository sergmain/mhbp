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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.PathUtils;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/8/2023
 * Time: 10:06 AM
 */
@Slf4j
public class DirUtils {

    public static void deletePathAsync(@Nullable final Path fileOrDir) {
        if (fileOrDir != null) {
            Thread t = new Thread(() -> {
                try {
                    if (Files.isDirectory(fileOrDir)) {
                        PathUtils.deleteDirectory(fileOrDir);
                    }
                    else {
                        Files.deleteIfExists(fileOrDir);
                    }
                } catch(IOException e){
                    // it's cleaning so don't report any error
                }
            });
            t.start();
        }
    }

    public static void deletePaths(@Nullable List<Path> toClean) {
        if (toClean!=null) {
            for (Path file : toClean) {
                try {
                    if (Files.notExists(file)) {
                        continue;
                    }
                    deletePathAsync(file);
                } catch (Throwable th) {
                    log.error("Error while cleaning resources", th);
                }
            }
        }
    }
}
