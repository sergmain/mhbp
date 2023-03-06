package ai.metaheuristic.mhbp.data.company;

import ai.metaheuristic.mhbp.utils.versioning.BaseUtils;

import java.util.Map;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:23 PM
 */
public class CompanyParamsUtils {

    private static final CompanyParamsUtilsV1 YAML_UTILS_V_1 = new CompanyParamsUtilsV1();
    private static final CompanyParamsUtilsV1 DEFAULT_UTILS = YAML_UTILS_V_1;

    public static final BaseUtils<CompanyParams> UTILS = new BaseUtils<>(
            Map.of(
                    1, YAML_UTILS_V_1
            ),
            DEFAULT_UTILS
    );


}
