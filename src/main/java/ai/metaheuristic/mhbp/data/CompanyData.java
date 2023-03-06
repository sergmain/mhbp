package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:09 PM
 */
public class CompanyData {


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class SimpleCompaniesResult extends BaseDataClass {
        public Page<SimpleCompany> companies;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyAccessControl {
        public String groups;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public static class SimpleCompanyResult extends BaseDataClass {
        public SimpleCompany company;
        public final CompanyAccessControl companyAccessControl = new CompanyAccessControl();

        public SimpleCompanyResult(String errorMessage) {
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public SimpleCompanyResult(SimpleCompany company, String errorMessage) {
            this.company = company;
            this.errorMessages = Collections.singletonList(errorMessage);
        }

        public SimpleCompanyResult(SimpleCompany company) {
            this.company = company;
        }
    }

}
