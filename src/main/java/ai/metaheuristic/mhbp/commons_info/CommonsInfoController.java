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

package ai.metaheuristic.mhbp.commons_info;

import ai.metaheuristic.mhbp.data.CommonInfoData;
import ai.metaheuristic.mhbp.utils.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 1:47 PM
 */
@Controller
@RequestMapping("/mhbp/commons")
@Slf4j
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('ADMIN', 'DATA')")
public class CommonsInfoController {

    private static final String REDIRECT_MHBP_COMMONS_INFO = "redirect:/mhbp/commons/info";

    private final CommonsInfoService commonsInfoService;

    @GetMapping("/info")
    public String getExperiments(Model model, @PageableDefault(size = 5) Pageable pageable,
                                 @ModelAttribute("infoMessages") final ArrayList<String> infoMessages,
                                 @ModelAttribute("errorMessage") final ArrayList<String> errorMessage) {
        CommonInfoData.Info info = commonsInfoService.getInfo(pageable);
        ControllerUtils.addMessagesToModel(model, info);
        model.addAttribute("result", info);
        return "mhbp/commons/info";
    }

    // for AJAX
    @PostMapping("/info-part")
    public String getExperimentsAjax(Model model, @PageableDefault(size = 5) Pageable pageable) {
        CommonInfoData.Info info = null;
        model.addAttribute("result", info);
        return "mhbp/commons/info :: table";
    }

}
