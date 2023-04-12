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

import ai.metaheuristic.mhbp.api.scheme.ApiScheme;
import ai.metaheuristic.mhbp.api.scheme.ApiSchemeUtils;
import ai.metaheuristic.mhbp.api.params.ApiParams;
import ai.metaheuristic.mhbp.api.params.ApiParamsUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Sergio Lissner
 * Date: 3/19/2023
 * Time: 9:05 PM
 */
@Entity
@Table(name = "MHBP_API")
@Data
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Api implements Serializable {
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

    @Column(name = "NAME")
    public String name;

    @Column(name = "CODE")
    public String code;

    @Column(name="CREATED_ON")
    public long createdOn;

    @Column(name="DISABLED")
    public boolean disabled;

    @Column(name = "PARAMS")
    private String params;

    public void setParams(String params) {
        synchronized (this) {
            this.params = params;
            this.apiParams = null;
        }
    }

    public String getParams() {
        return params;
    }

    @Column(name = "SCHEME")
    private String scheme;

    public void setScheme(String scheme) {
        synchronized (this) {
            this.scheme = scheme;
            this.apiScheme = null;
        }
    }

    public String getScheme() {
        return scheme;
    }

    @Transient
    @JsonIgnore
    @Nullable
    private ApiParams apiParams = null;

    @Transient
    @JsonIgnore
    private final Object syncParamsObj = new Object();

    @JsonIgnore
    public ApiParams getApiParams() {
        if (apiParams ==null) {
            synchronized (syncParamsObj) {
                if (apiParams ==null) {
                    //noinspection UnnecessaryLocalVariable
                    ApiParams temp = ApiParamsUtils.BASE_UTILS.to(params);
                    apiParams = temp;
                }
            }
        }
        return apiParams;
    }

    @JsonIgnore
    public void updateParams(ApiParams apy) {
        setParams(ApiParamsUtils.BASE_UTILS.toString(apy));
    }

    @Transient
    @JsonIgnore
    @Nullable
    private ApiScheme apiScheme = null;

    @Transient
    @JsonIgnore
    private final Object syncSchemeObj = new Object();

    @JsonIgnore
    public ApiScheme getApiScheme() {
        if (apiScheme==null) {
            synchronized (syncSchemeObj) {
                if (apiScheme==null) {
                    //noinspection UnnecessaryLocalVariable
                    ApiScheme temp = ApiSchemeUtils.BASE_UTILS.to(scheme);
                    apiScheme = temp;
                }
            }
        }
        return apiScheme;
    }

    @JsonIgnore
    public void updateScheme(ApiScheme apiScheme) {
        setScheme(ApiSchemeUtils.BASE_UTILS.toString(apiScheme));
    }

}
