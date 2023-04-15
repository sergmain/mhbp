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
package ai.metaheuristic.mhbp.utils.asset;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.utils.S;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.io.File;

@Slf4j
public class AssetUtils {

    /**
     *
     * @param rootDir File
     * @param dataId -  this is the code of resource
     * @param variableFilename String
     * @return AssetFile
     */
    public static AssetFile prepareFileForVariable(File rootDir, String dataId, @Nullable String variableFilename, Enums.DataType binaryType) {
        return prepareAssetFile(rootDir, dataId, variableFilename, binaryType.toString());
    }

    public static AssetFile prepareOutputAssetFile(File rootDir, String dataId) {
        return prepareAssetFile(rootDir, dataId, null, Consts.ARTIFACTS_DIR);
    }

    public static AssetFile fromFile(File file) {
        final AssetFile assetFile = new AssetFile();
        assetFile.file = file;
        if (file.isDirectory()) {
            String es = S.f("#025.020 path {} is dir", file.getAbsolutePath());
            log.error(es);
            assetFile.error = es;
            assetFile.isError = true;
        }
        assetFile.isExist = assetFile.file.exists();

        if (assetFile.isExist) {
            assetFile.fileLength = assetFile.file.length();
            if (assetFile.fileLength == 0) {
                assetFile.file.delete();
                assetFile.isExist = false;
            }
            else {
                assetFile.isContent = true;
            }
        }
        return assetFile;
    }

    // dataId must be String because for Function it's a code of function
    private static AssetFile prepareAssetFile(File rootDir, String dataId, @Nullable String filename, String assetDirname ) {
        final File assetDir = new File(rootDir, assetDirname);
        return prepareAssetFile(assetDir, dataId, filename);
    }

    public static AssetFile prepareAssetFile(File assetDir, @Nullable String dataId, @Nullable String filename) {
        final AssetFile assetFile = new AssetFile();
        assetDir.mkdirs();
        if (!assetDir.exists()) {
            assetFile.isError = true;
            log.error("#025.040 Can't create a variable dir for task: {}", assetDir.getAbsolutePath());
            return assetFile;
        }
        if (StringUtils.isNotBlank(filename)) {
            assetFile.file = new File(assetDir, filename);
        }
        else if (!S.b(dataId)) {
            final String resId = dataId.replace(':', '_');
            assetFile.file = new File(assetDir, "" + resId);
        }
        else {
            throw new IllegalArgumentException("#025.050 filename==null && S.b(dataId)");
        }
        assetFile.isExist = assetFile.file.exists();

        if (assetFile.isExist) {
            assetFile.fileLength = assetFile.file.length();
            if (assetFile.fileLength == 0) {
                assetFile.file.delete();
                assetFile.isExist = false;
            }
            else {
                assetFile.isContent = true;
            }
        }
        return assetFile;
    }

    public static AssetFile prepareFunctionFile(File baseDir, String functionCode, @Nullable String resourceFilename) {

        File baseFunctionDir = new File(baseDir, Enums.DataType.function.toString());

        final String resId = functionCode.replace(':', '_');
        final File resDir = new File(baseFunctionDir, resId);
        resDir.mkdirs();

        final AssetFile assetFile = new AssetFile();
        if (!resDir.exists()) {
            assetFile.isError = true;
            log.error("#025.080 Can't create a concrete function dir: {}", resDir.getAbsolutePath());
            return assetFile;
        }
        assetFile.file = !S.b(resourceFilename) ? new File(resDir, resourceFilename) : new File(resDir, resId);

        assetFile.isExist = assetFile.file.exists();

        if (assetFile.isExist) {
            if (assetFile.file.length() == 0) {
                assetFile.file.delete();
                assetFile.isExist = false;
            }
            else {
                assetFile.isContent = true;
            }
        }
        return assetFile;
    }
}
