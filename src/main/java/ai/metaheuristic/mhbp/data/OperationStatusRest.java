package ai.metaheuristic.mhbp.data;

import ai.metaheuristic.mhbp.Enums;
import ai.metaheuristic.mhbp.utils.S;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:37 PM
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class OperationStatusRest extends BaseDataClass {

    public static final OperationStatusRest OPERATION_STATUS_OK = new OperationStatusRest(Enums.OperationStatus.OK);
    public Enums.OperationStatus status;

    public OperationStatusRest(Enums.OperationStatus status) {
        this.status = status;
    }

    public OperationStatusRest(Enums.OperationStatus status, List<String> errorMessages) {
        this.status = status;
        this.errorMessages = errorMessages;
    }

    @JsonCreator
    public OperationStatusRest(@JsonProperty("status") Enums.OperationStatus status,
                               @JsonProperty("errorMessages") @Nullable List<String> errorMessages,
                               @JsonProperty("infoMessages") @Nullable List<String> infoMessages) {
        this.status = status;
        this.errorMessages = errorMessages;
        this.infoMessages = infoMessages;
    }

    public OperationStatusRest(Enums.OperationStatus status, String errorMessage) {
        this.status = status;
        this.errorMessages = Collections.singletonList(errorMessage);
    }

    public OperationStatusRest(Enums.OperationStatus status, @Nullable String infoMessage, @Nullable String errorMessage) {
        this.status = status;
        if (!S.b(infoMessage)) {
            this.infoMessages = List.of(infoMessage);
        }
        if (!S.b(errorMessage)) {
            this.errorMessages = List.of(errorMessage);
        }
    }

}
