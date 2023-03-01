package ai.metaheuristic.mhbp.commons_info;

import ai.metaheuristic.mhbp.data.CommonInfoData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Sergio Lissner
 * Date: 2/28/2023
 * Time: 4:00 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommonsInfoService {


    public CommonInfoData.Info getInfo(Pageable pageable) {

        return false;
    }
}
