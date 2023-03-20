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

package ai.metaheuristic.mhbp.utils;

import ai.metaheuristic.mhbp.Consts;
import ai.metaheuristic.mhbp.data.BaseDataClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 1:56 PM
 */
public class ControllerUtils {

    public static Pageable fixPageSize(int limit, Pageable pageable) {
        if (pageable.getPageSize()!= limit) {
            pageable = PageRequest.of(pageable.getPageNumber(), limit);
        }
        return pageable;
    }

    public static void initRedirectAttributes(RedirectAttributes redirectAttributes, BaseDataClass r) {
        if (r.isErrorMessages()) {
            redirectAttributes.addFlashAttribute(Consts.MODEL_ATTR_ERROR_MESSAGE, r.getErrorMessagesAsList());
        }
        if (r.isInfoMessages()) {
            redirectAttributes.addFlashAttribute(Consts.MODEL_ATTR_INFO_MESSAGES, r.getInfoMessagesAsList());
        }
    }

    @SuppressWarnings("unchecked")
    public static void addMessagesToModel(Model model, BaseDataClass baseData) {
        Collection<?> collection1 = baseData.getErrorMessagesAsList();
        if (collection1 != null && !collection1.isEmpty()) {
            List errorMessages = ((List)model.asMap().get(Consts.MODEL_ATTR_ERROR_MESSAGE));
            if (errorMessages==null) {
                errorMessages = new ArrayList();
                model.addAttribute(Consts.MODEL_ATTR_ERROR_MESSAGE, errorMessages);
            }
            errorMessages.addAll(baseData.getErrorMessagesAsList());
        }
        Collection<?> collection = baseData.getInfoMessagesAsList();
        if (collection != null && !collection.isEmpty()) {
            List infoMessages = ((List)model.asMap().get(Consts.MODEL_ATTR_INFO_MESSAGES));
            if (infoMessages==null) {
                infoMessages = new ArrayList();
                model.addAttribute(Consts.MODEL_ATTR_INFO_MESSAGES, infoMessages);
            }
            infoMessages.addAll(baseData.getInfoMessagesAsList());
        }
    }
}
