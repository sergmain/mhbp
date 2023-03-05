package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.Enums;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:24 AM
 */
public class ProviderResponse {
    public String providerCode;
    public long requestId;
    public long sessionId;
    public String text;
    public LocalDateTime dateTime;
    public boolean safeGuard;
    @Nullable
    public String modelId;

    public Enums.ResponseType type;
}
