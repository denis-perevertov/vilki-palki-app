package training;

public class Training {

    protected int avg_pulse;
    protected int training_length;
    protected int athlete_weight;

    protected Training(int avg_pulse, int training_length, int athlete_weight) {
        this.avg_pulse = avg_pulse;
        this.training_length = training_length;
        this.athlete_weight = athlete_weight;
    }

    protected double calculateCalories(int avg_pulse, int training_length, int athlete_weight) {
        return 0.014 * athlete_weight * training_length * (0.12*avg_pulse - 7);
    }

    public String showInfo() {
        return "DEFAULT TO-STRING Training{" +
                "avg_pulse=" + avg_pulse +
                ", training_length=" + training_length +
                ", athlete_weight=" + athlete_weight +
                '}';
    }
}
