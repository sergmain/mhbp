package ai.metaheuristic.mhbp.data.company;

import ai.metaheuristic.mhbp.data.BaseParams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:22 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyParams implements BaseParams {

    public final int version=1;

    @Override
    public boolean checkIntegrity() {
        return true;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessControl {
        public String groups;
    }

    public long createdOn;
    public long updatedOn;

    public AccessControl ac;

}