package bg.softuni.L1_Intro.ioc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZooService {


    private final Animal animal;
    private final Animal animal1;
    private Animal animal2;

    public ZooService (@Qualifier("normalDog") Animal animal, @Qualifier("superDog") Animal animal1, @Qualifier("cat") Animal animal2) {

        this.animal = animal;
        this.animal1 = animal1;
        this.animal2 = animal2;
    }

    public void doWork(){
        animal.makeNoise();
        animal1.makeNoise();
        animal2.makeNoise();
    }

   /* private List<Animal> animals;

    public ZooService(List<Animal> animals){

        this.animals = animals;
    }

    public void doWork(){
        animals.stream().forEach(Animal::makeNoise);
    }*/
}
