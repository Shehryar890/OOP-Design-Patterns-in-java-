public interface PaymentStrategy {

    void pay(double amount);
}

public class CreditCardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Validating credit card");
        System.out.println("Charging credit card: " + amount);
        System.out.println("Saving transaction");
    }
}
public class PayPalPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Connecting to PayPal");
        System.out.println("Charging PayPal: " + amount);
        System.out.println("Saving transaction");
    }
}
public class PaymentService {

    private PaymentStrategy paymentStrategy;

    public PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void pay(double amount) {

        paymentStrategy.pay(amount);
    }
}
public class Main {

    public static void main(String[] args) {

        PaymentService creditCardPayment = new PaymentService(
                        new CreditCardPayment()
                );

        creditCardPayment.pay(500);


        PaymentService paypalPayment =
                new PaymentService(
                        new PayPalPayment()
                );

        paypalPayment.pay(1000);


        

    }
}

// //Example when inheritance causes messs inform of a behaviour in parent that one child can have but other doesnt so that child has 
// to ovveride and gives the  default implementations or the behaviour inside the parent itself has many methods to perform the method like the above 
// pay method has many implementations so there inheritance causes mess 
public interface FlyBehavior {

    void fly();
}
public class FlyWithWings implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println("Flying with wings");
    }
}
public class FlyNoWay implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println("I cannot fly");
    }
}
public class Bird {

    private FlyBehavior flyBehavior;

    public Bird(FlyBehavior flyBehavior) {
        this.flyBehavior = flyBehavior;
    }

    public void fly() {

        flyBehavior.fly();
    }
}
public class Main {

    public static void main(String[] args) {

        Bird sparrow =
                new Bird(
                        new FlyWithWings()
                );

        Bird penguin =
                new Bird(
                        new FlyNoWay()
                );


        sparrow.fly();

        penguin.fly();
    }
}