package ai.metaheuristic.mhbp.commons_info;

import ai.metaheuristic.api.data.experiment.ExperimentApiData;
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

    private static final String REDIRECT_DISPATCHER_EXPERIMENTS = "redirect:/mhbp/commons/info";
    private static final CommonsInfoService commonsInfoService;

    @GetMapping("/info")
    public String getExperiments(Model model, @PageableDefault(size = 5) Pageable pageable,
                                 @ModelAttribute("infoMessages") final ArrayList<String> infoMessages,
                                 @ModelAttribute("errorMessage") final ArrayList<String> errorMessage) {
        CommonInfoData.Info info = commonsInfoService.getExperiments(pageable);
        ControllerUtils.addMessagesToModel(model, experiments);
        model.addAttribute("result", experiments);
        return "mhbp/commons/info";
    }

    // for AJAX
    @PostMapping("/experiments-part")
    public String getExperimentsAjax(Model model, @PageableDefault(size = 5) Pageable pageable) {
        ExperimentApiData.ExperimentsResult experiments = experimentTopLevelService.getExperiments(pageable);
        model.addAttribute("result", experiments);
        return "dispatcher/ai/experiment/experiments :: table";
    }

}
