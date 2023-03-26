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

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:20 AM
 */
public class Enums {

    public enum RequestCategory {math, social}

    public enum ResultStatus { usual, fail, problem }

    public enum RequestType {text, video, audio }
    public enum ResponseType {text, bool, digit }

    public enum OperationStatus {OK, ERROR}
    public enum AuthType { basic, token }

    public enum QueryResultErrorType { cant_understand, common, server_error, query_too_long }

    public enum SessionStatus { created(0), finished(1), finished_with_error(2);
        public final int code;

        SessionStatus(int code) {
            this.code = code;
        }
        public static SessionStatus to(int code) {
            return switch (code) {
                case 0 -> created;
                case 1 -> finished;
                case 2 -> finished_with_error;
                default -> throw new IllegalStateException("Unexpected value: " + code);
            };
        }
    }

    public enum AnswerStatus { normal(0), fail(1), error(2);
        public final int code;
        AnswerStatus(int code) {
            this.code = code;
        }
        public static AnswerStatus to(int status) {
            return switch (status) {
                case 0 -> normal;
                case 1 -> fail;
                case 2 -> error;
                default -> throw new IllegalStateException("Unexpected value: " + status);
            };
        }
    }
}
