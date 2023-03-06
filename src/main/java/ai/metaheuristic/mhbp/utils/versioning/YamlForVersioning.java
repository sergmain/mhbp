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
