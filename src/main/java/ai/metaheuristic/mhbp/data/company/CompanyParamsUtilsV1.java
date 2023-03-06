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
