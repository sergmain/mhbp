package ai.metaheuristic.mhbp;

import ai.metaheuristic.mhbp.utils.versioning.ParamsVersion;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 1:56 PM
 */
public class Consts {
    public static final String MODEL_ATTR_ERROR_MESSAGE = "errorMessage";
    public static final String MODEL_ATTR_INFO_MESSAGES = "infoMessages";

    public static final Long ID_1 = 1L;

    public static final String ROLE_SERVER_REST_ACCESS = "ROLE_SERVER_REST_ACCESS";

    public static final String ROLE_MAIN_ADMIN = "ROLE_MAIN_ADMIN";
    public static final String ROLE_MAIN_OPERATOR = "ROLE_MAIN_OPERATOR";
    public static final String ROLE_MAIN_SUPPORT = "ROLE_MAIN_SUPPORT";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final List<String> POSSIBLE_ROLES = List.of(ROLE_ADMIN,"ROLE_MANAGER","ROLE_OPERATOR", "ROLE_DATA");
    public static final List<String> COMPANY_1_POSSIBLE_ROLES =
            List.of(ROLE_SERVER_REST_ACCESS, ROLE_MAIN_OPERATOR, ROLE_MAIN_SUPPORT, "ROLE_MAIN_ASSET_MANAGER");

    public static final ParamsVersion PARAMS_VERSION_1 = new ParamsVersion();
}
