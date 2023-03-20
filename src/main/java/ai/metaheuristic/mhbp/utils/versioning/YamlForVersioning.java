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

package ai.metaheuristic.mhbp.utils.versioning;

import ai.metaheuristic.mhbp.Consts;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:37 PM
 */
public class YamlForVersioning {

    public static ParamsVersion getParamsVersion(String s) {
        ParamsVersion yamlVersion = getYamlForVersion().load(s);
        return yamlVersion==null ? Consts.PARAMS_VERSION_1 : yamlVersion;
    }

    private static Yaml getYamlForVersion() {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        representer.addClassTag(ParamsVersion.class, Tag.MAP);

        Constructor constructor = new Constructor(ParamsVersion.class);

        Yaml yaml = new Yaml(constructor, representer);
        return yaml;
    }
}
