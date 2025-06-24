package whackamole.whackamole.RS_new.Reward.Steps;

public enum StepType {
    Invalid(""),
    Message("Message"),
    Teleport("Teleport"),
    Sound("Sound"),
    Effect("Effect"),
    Currency("Currency");

    private String Value;

    StepType(String value) {
        this.Value = value;
    }

    static public StepType Parse(String type) {
        for (var step : values()) {
            if (step.Value.toLowerCase().equals(type.toLowerCase())) {
                return step;
            }
        }
        return Invalid;
    }
}
