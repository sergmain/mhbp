package ai.metaheuristic.mhbp.utils;

import org.springframework.lang.Nullable;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 5:39 PM
 */
public class S {
    public static boolean b(@Nullable String s) {
        return s==null || s.isBlank();
    }
}
