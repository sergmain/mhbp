package ai.metaheuristic.mhbp.sec;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.beans.Company;
import ai.metaheuristic.mhbp.data.RequestContext;
import ai.metaheuristic.mhbp.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:04 PM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    private final CompanyRepository companyRepository;

    public RequestContext getContext(Authentication authentication) {
        Account account = (Account)authentication.getPrincipal();
        if (account==null) {
            throw new RuntimeException("principal is null");
        }
        return getContext(authentication, account.companyId);
    }

    public RequestContext getContext(Authentication authentication, Long companyUniqueId) {
        Account account = (Account)authentication.getPrincipal();
        if (account==null) {
            throw new RuntimeException("principal is null");
        }
        Company company = companyRepository.findByUniqueId(companyUniqueId);
        if (company==null) {
            throw new RuntimeException("company not found not found for user: " + account.username);
        }
        RequestContext context = new RequestContext(account, company);
        return context;
    }

}
