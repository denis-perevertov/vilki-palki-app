package training;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Training[] trainings = {
                new TrainingRun(80, 30, 90, 10),
                new TrainingRun(95, 45, 70, 15),
                new TrainingCrossfit(100, 50, 80, 90),
                new TrainingBox(90, 120, 100)
        };

        for(Training training : trainings) {
            System.out.println(training.showInfo());
        }
    }
}
