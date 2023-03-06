package ai.metaheuristic.mhbp.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:02 PM
 */
@Entity
@Table(name = "MHBP_COMPANY")
@Data
@NoArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company implements Serializable {
    @Serial
    private static final long serialVersionUID = -159889135750827404L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Version
    public Integer version;

    @Column(name = "UNIQUE_ID")
    public Long uniqueId;

    @Nullable
    @Column(name = "PARAMS")
    public String params;

    public String name;

    public Company(String companyName) {
        this.name = companyName;
    }
}
