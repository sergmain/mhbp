package ai.metaheuristic.mhbp.data;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 6:20 PM
 */
public interface BaseParams {
    int getVersion();

    default boolean checkIntegrity() {
        return true;
    }
}
