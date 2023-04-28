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

import javax.persistence.Column;

/**
 * @author Sergio Lissner
 * Date: 4/28/2023
 * Time: 2:49 PM
 */
@AllArgsConstructor
public class SimpleAnswerStats {
    public long id;
    public long sessionId;
    public long chapterId;
    public int total;
    public int failed;
    public int systemError;
}
