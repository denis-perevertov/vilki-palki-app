package training;

public class TrainingRun extends Training{

    int range;

    public TrainingRun(int avg_pulse, int training_length, int athlete_weight, int range) {
        super(avg_pulse, training_length, athlete_weight);
        this.range = range;
    }

    @Override
    public String showInfo() {
        return "Беговая тренировка! Длительность: " + this.training_length
                + ", дистанция: " + this.range
                + ", килокалорий: " + this.calculateCalories(this.avg_pulse, this.training_length, this.avg_pulse);
    }
}
