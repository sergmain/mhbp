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

package ai.metaheuristic.mhbp.data.company;

import ai.metaheuristic.mhbp.utils.versioning.AbstractParamsUtils;
import ai.metaheuristic.mhbp.utils.YamlUtils;
import org.springframework.lang.NonNull;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:24 PM
 */
public class CompanyParamsUtilsV1
        extends AbstractParamsUtils<CompanyParamsV1, CompanyParams, Void, Void, Void, Void> {

    @Override
    public int getVersion() {
        return 1;
    }

    @NonNull
    @Override
    public Yaml getYaml() {
        return YamlUtils.init(CompanyParamsV1.class);
    }

    @NonNull
    @Override
    public CompanyParams upgradeTo(@NonNull CompanyParamsV1 src) {
        src.checkIntegrity();
        CompanyParams trg = new CompanyParams();
        if (src.ac!=null) {
            trg.ac = new CompanyParams.AccessControl(src.ac.groups);
        }
        trg.createdOn = src.createdOn;
        trg.updatedOn = src.updatedOn;
        trg.checkIntegrity();
        return trg;
    }

    @NonNull
    @Override
    public Void downgradeTo(@NonNull Void yaml) {
        return null;
    }

    @Override
    public Void nextUtil() {
        return null;
    }

    @Override
    public Void prevUtil() {
        return null;
    }

    @Override
    public String toString(@NonNull CompanyParamsV1 yaml) {
        return getYaml().dump(yaml);
    }

    @NonNull
    @Override
    public CompanyParamsV1 to(@NonNull String s) {
        final CompanyParamsV1 p = getYaml().load(s);
        return p;
    }

}
