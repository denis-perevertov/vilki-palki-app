package training;

public class TrainingCrossfit extends Training {

    int max_gear_weight;

    public TrainingCrossfit(int avg_pulse, int training_length, int athlete_weight, int max_gear_weight) {
        super(avg_pulse, training_length, athlete_weight);
        this.max_gear_weight = max_gear_weight;
    }

    @Override
    public String showInfo() {
        return "Кроссфит тренировка! Длительность: " + this.training_length
                + ", средний вес штанги: " + this.max_gear_weight
                + ", килокалорий: " + this.calculateCalories(this.avg_pulse, this.training_length, this.avg_pulse);
    }
}
