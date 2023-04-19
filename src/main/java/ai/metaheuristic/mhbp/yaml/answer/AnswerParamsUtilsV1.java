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

package ai.metaheuristic.mhbp.yaml.answer;

import ai.metaheuristic.mhbp.utils.YamlUtils;
import ai.metaheuristic.mhbp.utils.versioning.AbstractParamsUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.Yaml;

public class AnswerParamsUtilsV1 extends
        AbstractParamsUtils<AnswerParamsV1, AnswerParams, Void, Void, Void, Void> {

    @Override
    public int getVersion() {
        return 1;
    }

    @NonNull
    @Override
    public Yaml getYaml() {
        return YamlUtils.init(AnswerParamsV1.class);
    }

    @NonNull
    @Override
    public AnswerParams upgradeTo(@NonNull AnswerParamsV1 v1) {
        v1.checkIntegrity();

        AnswerParams t = new AnswerParams();
        t.raw = v1.raw;
        t.expected = toExpected(v1.expected);

        t.checkIntegrity();
        return t;
    }

    @Nullable
    private static AnswerParams.Expected toExpected(@Nullable AnswerParamsV1.ExpectedV1 v1) {
        if (v1==null) {
            return null;
        }
        AnswerParams.Expected f = new AnswerParams.Expected(v1.prompt, v1.answer);
        return f;
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
    public String toString(@NonNull AnswerParamsV1 yaml) {
        yaml.checkIntegrity();

        return getYaml().dump(yaml);
    }

    @NonNull
    @Override
    public AnswerParamsV1 to(@NonNull String s) {
        final AnswerParamsV1 p = getYaml().load(s);
        return p;
    }

}
