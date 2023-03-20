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

package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.NameValuePair;

import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:17 PM
 */
public class CommunicationData {

    @Data
    public static class RegisterDone {
        boolean success;
        int code;
        String msg;

        public String username;

        public RegisterDone(boolean success, int code) {
            this.success = success;
            this.code = code;
        }

        public RegisterDone(boolean success, int code, String username) {
            this.success = success;
            this.code = code;
            this.username = username;
        }
    }

    @Data
    public static class RegisterResult {
        boolean success;
        int code;
        int extendedCode;
        String msg;

        public RegisterResult(boolean success, int code) {
            this.success = success;
            this.code = code;
        }

        public RegisterResult(boolean success, int code, int extendedCode, String msg) {
            this.success = success;
            this.code = code;
            this.extendedCode = extendedCode;
            this.msg = msg;
        }

        public RegisterResult(boolean success, int code, String msg) {
            this.success = success;
            this.code = code;
            this.msg = msg;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Query {
        public final String url;
        public final List<NameValuePair> nvps;
    }
}
