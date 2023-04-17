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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

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

    @Column(name = "KB_ID")
     public long kbId;

    public long createdOn;

    public String name;
}
