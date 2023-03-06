package ai.metaheuristic.mhbp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:11 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCompany {
    public Long id;
    public Long uniqueId;
    public String name;
}

