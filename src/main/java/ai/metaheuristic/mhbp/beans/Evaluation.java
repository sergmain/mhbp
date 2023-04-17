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

import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 4/15/2023
 * Time: 7:32 PM
 */
@Entity
@Table(name = "MHBP_EVALUATION")
@Data
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Evaluation implements Serializable {

    public static class KbIdsConverter implements AttributeConverter<List<Long>, String> {

        @Override
        public String convertToDatabaseColumn(@Nullable List<Long> extraFields) {
            if (extraFields==null) {
                throw new IllegalStateException("(extraFields==null)");
            }
            String s = extraFields.stream().map(Object::toString).collect(Collectors.joining(","));
            return s;
        }

        @Override
        public List<Long> convertToEntityAttribute(String data) {
            if (S.b(data)) {
                return new ArrayList<>();
            }
            List<Long> list = Arrays.stream(StringUtils.split(data, ',')).map(Long::parseLong).toList();
            return list;
        }
    }

    @Serial
    private static final long serialVersionUID = -5515608565018985069L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Version
    public Integer version;

    @Column(name = "COMPANY_ID")
    public long companyId;

    @Column(name = "ACCOUNT_ID")
    public long accountId;

    @Column(name = "API_ID")
    public long apiId;

    @Column(name = "KB_IDS")
    @Convert(converter = KbIdsConverter.class)
    public List<Long> kbIds;

    public long createdOn;

    public String name;
}
