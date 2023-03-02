package ai.metaheuristic.mhbp.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
    private static final long serialVersionUID = -955288291602026786L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    public Integer version;

    /**
     * as UUID
     */
    private String username;

    /**
     * as UUID with BCrypt
     */
    private String password;

    private String roles;

    @Column(name="is_acc_not_expired")
    private boolean accountNonExpired;

    @Column(name="is_not_locked")
    private boolean accountNonLocked;

    @Column(name="is_cred_not_expired")
    private boolean credentialsNonExpired;

    @Column(name="is_enabled")
    private boolean enabled;

    //    @Column(name="IS_PHONE_NUMBER_VERIFIED")
    private boolean isPhoneNumberVerified;

    @Column(name="mail_address")
    private String mailAddress;
    private long phone;

    //TODO add checks on max length
    @Column(name="phone_as_str")
    private String phoneAsStr;

    @Column(name="PUBLIC_NAME")
    private String publicName;

    @Column(name="created_on")
    private long createdOn;

    public List<? extends GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authList = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(roles, ",");
        while (st.hasMoreTokens()) {
            authList.add(new SimpleGrantedAuthority(st.nextToken().trim()));
        }
        return authList;
    }

}
