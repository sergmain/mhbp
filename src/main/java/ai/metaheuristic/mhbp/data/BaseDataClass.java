/*
 *    Copyright 2023, Sergio Lissner, Innovation platforms, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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

    @Nullable
    @JsonInclude(value= JsonInclude.Include.NON_NULL, content= JsonInclude.Include.NON_EMPTY)
    public List<String> errorMessages;

    @Nullable
    @JsonInclude(value= JsonInclude.Include.NON_NULL, content= JsonInclude.Include.NON_EMPTY)
    public List<String> infoMessages;

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
