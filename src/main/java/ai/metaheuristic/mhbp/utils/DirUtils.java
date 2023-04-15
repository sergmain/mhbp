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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/8/2023
 * Time: 10:06 AM
 */
@Slf4j
public class DirUtils {

    @Nullable
    public static Path createTempPath(Path trgDir, String prefix) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String prefixDate = format.format(date);
        for (int i = 0; i < 5; i++) {
            Path newTempDir = trgDir.resolve( prefix + prefixDate + "-" + System.nanoTime());
            if (Files.exists(newTempDir)) {
                continue;
            }
            try {
                Files.createDirectories(newTempDir);
                if (Files.exists(newTempDir)) {
                    return newTempDir;
                }
            } catch (IOException e) {
                log.error(S.f("#017.040 Can't create temporary dir %s, attempt #%d, error: %s", newTempDir.normalize(), i, e.getMessage()));
            }
        }
        return null;
    }

    @SneakyThrows
    @Nullable
    public static Path createMhTempPath(String prefix) {
        return createMhTempPath(SystemUtils.getJavaIoTmpDir().toPath(), prefix);
    }

    @SneakyThrows
    @Nullable
    public static Path createMhTempPath(Path base, String prefix) {
        Path trgDir = base.resolve(Consts.METAHEURISTIC_TEMP);
        if (Files.notExists(trgDir)) {
            Files.createDirectories(trgDir);
        }
        return createTempPath(trgDir, prefix);
    }

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
