package ai.metaheuristic.mhbp.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:16 PM
 */
@Entity
@Table(name = "MHBP_IDS")
@Data
@TableGenerator(
        name="mhbp_ids",
        table="mhbp_gen_ids",
        pkColumnName = "sequence_name",
        valueColumnName = "sequence_next_value",
        pkColumnValue = "mhbp_ids",
        allocationSize = 1,
        initialValue = 1
)
@NoArgsConstructor
public class Ids implements Serializable {
    @Serial
    private static final long serialVersionUID = 8697932300220763332L;

    @Id
    @GeneratedValue(strategy= GenerationType.TABLE, generator = "mhbp_ids")
    public Long id;

    public Integer stub;
}