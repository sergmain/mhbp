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
    public static final String RESOURCES_TO_CLEAN = "mh-to-clean";

    public static final List<String> POSSIBLE_ROLES = List.of(ROLE_ADMIN,"ROLE_MANAGER","ROLE_OPERATOR", "ROLE_DATA");
    public static final List<String> COMPANY_1_POSSIBLE_ROLES =
            List.of(ROLE_SERVER_REST_ACCESS, ROLE_MAIN_OPERATOR, ROLE_MAIN_SUPPORT, "ROLE_MAIN_ASSET_MANAGER");

    public static final ParamsVersion PARAMS_VERSION_1 = new ParamsVersion();

    public static final String METAHEURISTIC_TEMP = "metaheuristic-temp";

    public static final String ARTIFACTS_DIR = "artifacts";

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    public static final String GIT_PATH = "git";
}
