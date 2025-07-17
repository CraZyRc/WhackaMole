package whackamole.whackamole.RS.Reward;


import java.util.ArrayList;
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

    public ValidationException addCause(ValidationException ex) {
        if (this.causes == null) {
            this.causes = new ArrayList<>();
        }
        this.causes.add(ex);
        return this;
    }

    private String format(int ident) {
        var output = " ".repeat(ident) + super.getMessage();
        if (this.causes == null) {
            return output;
        }
        
        for (var ex : causes) {
            output += "-" + " ".repeat(ident) + ex.format(ident + 1);
        }

        return output;
    }

    @Override
    public String getMessage() {
        return this.format(0);
    }
}
