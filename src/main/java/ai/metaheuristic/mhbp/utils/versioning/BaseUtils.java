package ai.metaheuristic.mhbp.utils.versioning;

import ai.metaheuristic.mhbp.data.BaseParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Map;
import java.util.Objects;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:32 PM
 */
@Slf4j
public class BaseUtils<T extends BaseParams> {

    @NonNull
    private final ParamsUtilsFactory FACTORY;

    public BaseUtils(@NonNull Map<Integer, AbstractParamsUtils> map, @NonNull AbstractParamsUtils defYamlUtils) {
        map.forEach((k,v)-> {
            if (k!=v.getVersion()) {
                final String es = "Versions are different, class: "+ v.getClass() + ", expected version: "+ k+", version of class: " + v.getVersion();
                log.error(es);
                throw new IllegalStateException(es);
            }
        });
        FACTORY = new ParamsUtilsFactory(map, defYamlUtils);;
    }

    public @Nullable AbstractParamsUtils getForVersion(int version) {
        return FACTORY.getForVersion(version);
    }

    public @NonNull AbstractParamsUtils getDefault() {
        return FACTORY.getDefault();
    }

    public @NonNull String toString(@NonNull BaseParams baseParams) {
        baseParams.checkIntegrity();
        return Objects.requireNonNull(getDefault().getYaml().dumpAsMap(baseParams));
    }

    public @NonNull String toStringAsVersion(@NonNull BaseParams baseParamsYaml, int version) {
        if (baseParamsYaml.getVersion()==version) {
            return toString(baseParamsYaml);
        }

        AbstractParamsUtils utils = getForVersion(version);
        if (utils==null) {
            throw new IllegalStateException("Unsupported version: " + version);
        }
        if (getDefault().getVersion()==version) {
            return toString(baseParamsYaml);
        }
        else {

            AbstractParamsUtils yamlUtils = getDefault();
            Object currBaseParamsYaml = baseParamsYaml;
            do {
                if (yamlUtils.getVersion()==version) {
                    break;
                }
                //noinspection unchecked
                currBaseParamsYaml = yamlUtils.downgradeTo(currBaseParamsYaml);
            } while ((yamlUtils=(AbstractParamsUtils)yamlUtils.prevUtil())!=null);

            //noinspection unchecked
            T p = (T)currBaseParamsYaml;

            return Objects.requireNonNull(utils.getYaml().dumpAsMap(p));
        }
    }

    public T to(@NonNull String s) {
        try {
            ParamsVersion v = YamlForVersioning.getParamsVersion(s);
            AbstractParamsUtils yamlUtils = getForVersion(v.getActualVersion());
            if (yamlUtils==null) {
                throw new IllegalStateException("Unsupported version: " + v.getActualVersion());
            }

            BaseParams currBaseParamsYaml = yamlUtils.to(s);
            do {
                //noinspection unchecked
                currBaseParamsYaml = yamlUtils.upgradeTo(currBaseParamsYaml);
            } while ((yamlUtils=(AbstractParamsUtils)yamlUtils.nextUtil())!=null);

            //noinspection unchecked
            T p = (T)currBaseParamsYaml;

            p.checkIntegrity();

            return p;
        } catch (YAMLException e) {
            throw new IllegalArgumentException("Error: " + e.getMessage(), e);
        }
    }


}
