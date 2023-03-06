package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.beans.Account;
import ai.metaheuristic.mhbp.beans.Company;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:00 PM
 */
@Data
@RequiredArgsConstructor
public class RequestContext {
    public final String contextId = UUID.randomUUID().toString();

    public final Account account;

    private final Company company;

    public String getUsername() {
        return account.username;
    }
    public Long getAccountId() {
        return account.id;
    }
    public Long getCompanyId() {
        return company.uniqueId;
    }
}
