package ai.metaheuristic.mhbp.utils.versioning;

import ai.metaheuristic.mhbp.data.BaseParams;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:25 PM
 */
abstract public class AbstractParamsUtils
        <CurrT extends BaseParams, NextT extends BaseParams, NextU, PrevT, PrevU, CurrForDownT> {

    public abstract @NonNull Yaml getYaml();

    public abstract @NonNull NextT upgradeTo(@NonNull CurrT yaml);

    public abstract @NonNull PrevT downgradeTo(@NonNull CurrForDownT yaml);

    public abstract @Nullable NextU nextUtil();

    public abstract @Nullable PrevU prevUtil();

    public abstract String toString(CurrT yaml);

    public abstract @NonNull CurrT to(String s);

    public abstract int getVersion();
}
