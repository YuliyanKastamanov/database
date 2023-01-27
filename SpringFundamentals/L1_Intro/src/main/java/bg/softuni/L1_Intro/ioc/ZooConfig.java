package bg.softuni.L1_Intro.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ZooConfig {

    @Bean("normalDog")
    public Animal dog(){
        return new Dog();
    }

    @Bean("superDog")
    public Animal superDog(){
        return new Dog(true);
    }

    @Bean("cat")
    public Animal cat(){
        return new Cat();
    }
}
