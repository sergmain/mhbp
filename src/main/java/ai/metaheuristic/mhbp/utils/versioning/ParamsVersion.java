package ai.metaheuristic.mhbp.utils.versioning;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:36 PM
 */
@Data
@NoArgsConstructor
public class ParamsVersion {
    public Integer version;

    public ParamsVersion(Integer version) {
        this.version = version;
    }

    public int getActualVersion() {
        return version!=null ? version : 1;
    }
}
