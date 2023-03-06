package ai.metaheuristic.mhbp.beans;

import ai.metaheuristic.commons.account.AccountRoles;
import ai.metaheuristic.mhbp.data.AccountData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergio Lissner
 * Date: 3/1/2023
 * Time: 11:15 PM
 */
@Entity
@Table(name = "MHBP_ACCOUNT")
@Data
@EqualsAndHashCode(of = {"username", "password"})
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Account implements UserDetails, Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 708692073045562337L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Version
    public Integer version;

    // This field contains a value from MH_COMPANY.UNIQUE_ID, !NOT! from ID field
    @Column(name = "COMPANY_ID")
    public Long companyId;

    @Column(name = "USERNAME")
    public String username;

    @Column(name = "PASSWORD")
    public String password;

    @Column(name="IS_ACC_NOT_EXPIRED")
    public boolean accountNonExpired;

    @Column(name="IS_NOT_LOCKED")
    public boolean accountNonLocked;

    @Column(name="IS_CRED_NOT_EXPIRED")
    public boolean credentialsNonExpired;

    @Column(name="IS_ENABLED")
    public boolean enabled;

    @Column(name="PUBLIC_NAME")
    public String publicName;

    @Column(name="CREATED_ON")
    public long createdOn;

    @Column(name="UPDATED_ON")
    public long updatedOn;

    @Nullable
    public String roles;

    @Transient
    @JsonIgnore
    public final AccountRoles accountRoles = new AccountRoles(()-> roles, (o)->roles = o);

    public List<AccountData.SerializableGrantedAuthority> getAuthorities() {
        return accountRoles.getAuthorities().stream()
                .map(o->new AccountData.SerializableGrantedAuthority(o.authority))
                .collect(Collectors.toList());
    }

}
