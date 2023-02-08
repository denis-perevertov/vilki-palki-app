package training;

public class TrainingBox extends Training{

    public TrainingBox(int avg_pulse, int training_length, int athlete_weight) {
        super(avg_pulse, training_length, athlete_weight);
    }

    @Override
    public String showInfo() {
        return "Тренировка по кикбоксингу! Длительность: " + this.training_length
                + ", килокалорий: " + this.calculateCalories(this.avg_pulse, this.training_length, this.avg_pulse);
    }
}
