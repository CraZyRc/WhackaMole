package whackamole.whackamole.RS_new.Reward;


import java.util.List;

import whackamole.whackamole.Utils.Translator;

public class ValidationException extends Exception {
    
    private List<ValidationException> causes;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Translator message) {
        super(message.Format());
    }

    public ValidationException(List<ValidationException> ex) {
        super("");
        this.causes = ex;
    } 

    public List<ValidationException> getCauses() {
        return this.causes;
    }
}
