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

import lombok.Data;

import java.io.File;

@Data
public class AssetFile {
    public File file;
    public long fileLength;
    public boolean isError;
    public boolean isContent;
    public boolean isExist;
    public boolean provided = false;
    public String error;

    @Override
    public String toString() {
        return "AssetFile{" +
                "file=" + (file!=null ? file.getPath()  : "null") +
                ", fileLength=" + fileLength +
                ", isError=" + isError +
                ", isContent=" + isContent +
                ", isExist=" + isExist +
                ", isProvided=" + provided +
                ", error=" + error +
                '}';
    }
}
