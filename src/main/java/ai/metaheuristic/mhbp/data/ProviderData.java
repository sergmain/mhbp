package ai.metaheuristic.mhbp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:45 AM
 */
public class ProviderData {

    public static class Provider {
        public String code;
        public String name;
        public String url;
        public String token;
    }

    public static class Providers {
        public static final List<Provider> providers = new ArrayList<>();
    }

}
