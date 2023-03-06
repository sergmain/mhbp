package ai.metaheuristic.mhbp;

/**
 * @author Sergio Lissner
 * Date: 3/5/2023
 * Time: 1:20 AM
 */
public class Enums {

    public enum RequestCategory {
        math, social
    }

    public enum ResultStatus {
        usual, fail, problem
    }

    public enum RequestType {text, video, audio }
    public enum ResponseType {text, bool, digit }

    public enum OperationStatus {OK, ERROR}
}
