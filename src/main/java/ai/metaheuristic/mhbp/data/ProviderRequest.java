package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.Enums;

import java.time.LocalDateTime;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:31 AM
 */
public class ProviderRequest {
    public long requestId;
    public String providerCode;
    public LocalDateTime dateTime;
    public Enums.RequestType type;

}
