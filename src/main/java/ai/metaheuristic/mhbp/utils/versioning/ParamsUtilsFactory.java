package ai.metaheuristic.mhbp.utils.versioning;

import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:33 PM
 */
@Data
public class ParamsUtilsFactory {

    public @NonNull Map<Integer, AbstractParamsUtils> map;
    public @NonNull AbstractParamsUtils defYamlUtils;

    public void ParamsYamlUtilsFactory(@NonNull Map<Integer, AbstractParamsUtils> map, @NonNull AbstractParamsUtils defYamlUtils) {
        this.map = map;
        this.defYamlUtils = defYamlUtils;
    }

    public @NonNull AbstractParamsUtils getForVersion(int version) {
        AbstractParamsUtils yamlUtils = map.get(version);
        if (yamlUtils==null) {
            throw new IllegalArgumentException("Not supported version: " + version);
        }
        return yamlUtils;
    }


    public @NonNull AbstractParamsUtils getDefault() {
        return defYamlUtils;
    }
}
