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

package ai.metaheuristic.mhbp.company;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.Globals;
import ai.metaheuristic.mhbp.beans.Company;
import ai.metaheuristic.mhbp.beans.Ids;
import ai.metaheuristic.mhbp.data.CompanyData;
import ai.metaheuristic.mhbp.data.OperationStatusRest;
import ai.metaheuristic.mhbp.data.SimpleCompany;
import ai.metaheuristic.mhbp.data.company.CompanyParams;
import ai.metaheuristic.mhbp.data.company.CompanyParamsUtils;
import ai.metaheuristic.mhbp.repositories.CompanyRepository;
import ai.metaheuristic.mhbp.repositories.IdsRepository;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import ai.metaheuristic.mhbp.utils.S;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:04 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    public static final int ROWS_IN_TABLE = 50;

    private final Globals globals;
    private final CompanyRepository companyRepository;
    private final IdsRepository idsRepository;

    public CompanyData.SimpleCompaniesResult getCompanies(Pageable pageable) {
        pageable = ControllerUtils.fixPageSize(ROWS_IN_TABLE, pageable);
        CompanyData.SimpleCompaniesResult result = new CompanyData.SimpleCompaniesResult();
        result.companies = companyRepository.findAllAsSimple(pageable);
        return result;
    }

    @Transactional
    public OperationStatusRest addCompany(String companyName) {
        return addCompany(new Company(companyName));
    }

    @Transactional
    public OperationStatusRest addCompany(Company company) {
        if (S.b(company.name)) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,
                    "#237.020 Name of company name must not be null");
        }

        CompanyParams cpy = S.b(company.getParams()) ? null : CompanyParamsUtils.UTILS.to(company.getParams());
        if (cpy==null) {
            cpy = new CompanyParams();
        }
        cpy.createdOn = System.currentTimeMillis();
        cpy.updatedOn = cpy.createdOn;

        String paramsYaml;
        try {
            paramsYaml = CompanyParamsUtils.UTILS.toString(cpy);
        } catch (Throwable th) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"#237.030 company params is in wrong format, error: " + th.getMessage());
        }
        company.setParams(paramsYaml);

        if (company.uniqueId==null) {
            company.uniqueId = getUniqueId();
        }
        companyRepository.save(company);
        return OperationStatusRest.OPERATION_STATUS_OK;
    }

    public Long getUniqueId() {
        Long maxUniqueId = companyRepository.getMaxUniqueIdValue();
        if (maxUniqueId==null) {
            // 2L because 1 is reserved for 'main company'
            maxUniqueId = 2L;
        }
        int compare;
        Long newUniqueId;
        do {
            newUniqueId = idsRepository.save(new Ids()).id;
            idsRepository.deleteById(newUniqueId);
            compare = Long.compare(newUniqueId, maxUniqueId);
        } while(compare<1);
        return newUniqueId;
    }

    @Transactional(readOnly = true)
    public CompanyData.SimpleCompanyResult getSimpleCompany(Long companyUniqueId){
        Company company = companyRepository.findByUniqueId(companyUniqueId);
        if (company == null) {
            return new CompanyData.SimpleCompanyResult("#237.050 company wasn't found, companyUniqueId: " + companyUniqueId);
        }
        String groups = "";
        CompanyParams cpy = S.b(company.params) ? new CompanyParams() : CompanyParamsUtils.UTILS.to(company.params);

        if (cpy.ac!=null && !S.b(cpy.ac.groups)) {
            groups = cpy.ac.groups;
        }
        SimpleCompany simpleCompany = new SimpleCompany();
        simpleCompany.id = company.id;
        simpleCompany.uniqueId = company.uniqueId;
        simpleCompany.name = company.name;
        CompanyData.SimpleCompanyResult companyResult = new CompanyData.SimpleCompanyResult(simpleCompany);
        companyResult.companyAccessControl.groups = groups;
        return companyResult;
    }

    /**
     *
     * @param companyUniqueId contains a value from Company.uniqueId, !not! from Company.Id
     * @param name
     * @param groups
     * @return
     */
    @Transactional
    public OperationStatusRest editFormCommit(Long companyUniqueId, String name, String groups) {
        Company c = companyRepository.findByUniqueIdForUpdate(companyUniqueId);
        if (c == null) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"#237.060 company wasn't found, companyUniqueId: " + companyUniqueId);
        }

        String paramsYaml;
        try {
            String p;
            if (S.b(c.getParams())) {
                CompanyParams params = new CompanyParams();
                params.createdOn = System.currentTimeMillis();
                p = CompanyParamsUtils.UTILS.toString(params);
            }
            else {
                p = c.getParams();
            }
            CompanyParams cpy = CompanyParamsUtils.UTILS.to(p);
            cpy.updatedOn = System.currentTimeMillis();
            cpy.ac = new CompanyParams.AccessControl(groups);
            paramsYaml = CompanyParamsUtils.UTILS.toString(cpy);
        } catch (Throwable th) {
            return new OperationStatusRest(Enums.OperationStatus.ERROR,"#237.080 company params is in wrong format, error: " + th.getMessage());
        }
        c.setParams(paramsYaml);
        c.setName(name);
        companyRepository.save(c);
        return new OperationStatusRest(Enums.OperationStatus.OK,"The data of company was changed successfully", null);
    }

    @Nullable
    @Transactional(readOnly = true)
    public Company getCompanyByUniqueId(Long uniqueId) {
        return companyRepository.findByUniqueId(uniqueId);
    }

}
