package ai.metaheuristic.mhbp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:26 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleAccount {
    public Long id;
    public Long companyId;
    public String username;
    public String publicName;
    public boolean enabled;
    public long createdOn;
    public long updatedOn;
    public String roles;

    @Transient
    @JsonIgnore
    public final AccountRoles accountRoles = new AccountRoles(()-> roles, (o)->roles = o);

    public List<AccountData.SerializableGrantedAuthority> getAuthorities() {
        return accountRoles.getAuthorities();
    }
}