package bg.softuni.L1_Intro.ioc;

public class Dog implements Animal{

    private boolean superDog;

    public Dog() {
        this(false);
    }

    public Dog(boolean superDog) {
        this.superDog = superDog;
    }

    @Override
    public void makeNoise() {
        if(superDog) {
            System.out.println("SUPER BARK, SUPER BARK");
        } else {
            System.out.println("bark, bark");

        }
    }
}
