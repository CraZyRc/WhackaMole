package whackamole.whackamole.RS.Reward.Steps;

public enum StepType {
    Invalid(""),
    Message("Message"),
    Item("Item"),
    Teleport("Teleport"),
    Sound("Sound"),
    Effect("Effect"),
    Currency("Currency"),
    Animation("Animation");

    private String value;

    StepType(String value) {
        this.value = value;
    }

    static public StepType Parse(String type) {
        for (var step : values()) {
            if (step.value.toLowerCase().equals(type.toLowerCase())) {
                return step;
            }
        }
        return Invalid;
    }

    public String toString() {
        return this.value;
    }
}
