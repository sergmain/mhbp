package ai.metaheuristic.mhbp.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 1:55 PM
 */
@Data
public abstract class BaseDataClass {

    @JsonInclude(value= JsonInclude.Include.NON_NULL, content= JsonInclude.Include.NON_EMPTY)
    public @Nullable List<String> errorMessages;

    @JsonInclude(value= JsonInclude.Include.NON_NULL, content= JsonInclude.Include.NON_EMPTY)
    public @Nullable List<String> infoMessages;

    public void addErrorMessage(String errorMessage) {
        if (this.errorMessages==null) {
            this.errorMessages = new ArrayList<>();
        }
        this.errorMessages.add(errorMessage);
    }

    public void addErrorMessages(@NonNull List<String> errorMessages) {
        if (this.errorMessages==null) {
            this.errorMessages = new ArrayList<>();
        }
        this.errorMessages.addAll(errorMessages);
    }

    public void addInfoMessage(String infoMessage) {
        if (this.infoMessages==null) {
            this.infoMessages = new ArrayList<>();
        }
        this.infoMessages.add(infoMessage);
    }

    public void addInfoMessages(@NonNull List<String> infoMessages) {
        if (this.infoMessages==null) {
            this.infoMessages = new ArrayList<>();
        }
        this.infoMessages.addAll(infoMessages);
    }

    @JsonIgnore
    public String getInfoMessagesAsStr() {
        if (!isNotEmpty(infoMessages)) {
            return "";
        }
        if (infoMessages.size()==1) {
            return Objects.requireNonNull(infoMessages.get(0));
        }
        return Objects.requireNonNull(infoMessages.toString());
    }

    @JsonIgnore
    public String getErrorMessagesAsStr() {
        if (!isNotEmpty(errorMessages)) {
            return "";
        }
        if (errorMessages.size()==1) {
            return Objects.requireNonNull(errorMessages.get(0));
        }
        return Objects.requireNonNull(errorMessages.toString());
    }

    @JsonIgnore
    public List<String> getErrorMessagesAsList() {
        return isNotEmpty(errorMessages) ? errorMessages : List.of();
    }

    @JsonIgnore
    public List<String> getInfoMessagesAsList() {
        return isNotEmpty(infoMessages) ? infoMessages : List.of();
    }

    @JsonIgnore
    public boolean isErrorMessages() {
        return isNotEmpty(errorMessages);
    }

    @JsonIgnore
    public boolean isInfoMessages() {
        return isNotEmpty(infoMessages);
    }

    @JsonIgnore
    private static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return collection!=null && !collection.isEmpty();
    }
}
