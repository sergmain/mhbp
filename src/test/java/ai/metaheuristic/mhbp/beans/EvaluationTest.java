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

package ai.metaheuristic.mhbp.beans;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Sergio Lissner
 * Date: 4/16/2023
 * Time: 11:48 PM
 */
public class EvaluationTest {

    @Test
    public void test_KbIdsConverter() {
        Evaluation.KbIdsConverter converter = new Evaluation.KbIdsConverter();

        List<Long> list = converter.convertToEntityAttribute("11");
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(11L, list.get(0));


        list = converter.convertToEntityAttribute("11,42");
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(11L, list.get(0));
        assertEquals(42L, list.get(1));
    }
}
