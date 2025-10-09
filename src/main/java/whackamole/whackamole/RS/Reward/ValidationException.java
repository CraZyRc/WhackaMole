package whackamole.whackamole.RS.Reward;


import java.util.ArrayList;
import java.util.Arrays;
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

    public List<ValidationException> getCauses() {
        return this.causes;
    }

    private String Render() {return this.Render(0); }
    private String Render(int indent) {
        var sb = new StringBuilder();

        var message = super.getMessage();

        if (message.length() > 0) {
            if (indent != 0) {
                var spaces = new char[indent*2];
                Arrays.fill(spaces, ' ');
                sb.append(spaces);
                sb.append("- ");
            }
    
            sb.append(super.getMessage());
            sb.append('\n');
            indent ++;
        }

        if (this.causes != null && this.causes.size() > 0) {
            for (var e : causes) {
                sb.append(e.Render(indent));
            }
        }

        return sb.toString();
    }

    @Override
    public String getMessage() {
        return this.Render();
    }
}
