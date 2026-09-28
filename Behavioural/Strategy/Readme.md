
# Strategy Pattern

The **Strategy Pattern** is a behavioral design pattern used when we have a behavior or algorithm that can have multiple implementations.

Instead of putting all implementations inside one class, we:

1. Identify the behavior that changes.
2. Extract that behavior behind an interface.
3. Put each implementation into its own class.
4. Let the main class hold a reference to that interface.
5. Delegate the behavior to the selected implementation.

The core idea is:

> **Encapsulate what varies.**

A very simple mental model is:

```text
Something needs to perform an operation.

        ↓

There are multiple ways to perform it.

        ↓

Separate those ways into different classes.

        ↓

Let the main class use one through an interface.
```

We will understand this through **two completely different problems**.

---

# Part 1 — Strategy for an `if/else` Problem

## 1. The Problem

Imagine we are building a payment system.

A customer can pay using different payment methods:

```text
Payment
│
├── Credit Card
├── PayPal
└── Bank Transfer
```

At first, we might naturally write the code like this:

```java
class PaymentService {

    void pay(String paymentType, double amount) {

        if (paymentType.equals("CREDIT_CARD")) {

            System.out.println("Validating credit card");
            System.out.println("Charging credit card: " + amount);
            System.out.println("Saving transaction");

        } else if (paymentType.equals("PAYPAL")) {

            System.out.println("Connecting to PayPal");
            System.out.println("Charging PayPal: " + amount);
            System.out.println("Saving transaction");

        } else if (paymentType.equals("BANK_TRANSFER")) {

            System.out.println("Creating bank transfer");
            System.out.println("Processing bank transfer: " + amount);
            System.out.println("Saving transaction");
        }
    }
}
```

And we might use it like this:

```java
public class Main {

    public static void main(String[] args) {

        PaymentService paymentService = new PaymentService();

        paymentService.pay("CREDIT_CARD", 500);
    }
}
```

The output would be:

```text
Validating credit card
Charging credit card: 500.0
Saving transaction
```

At this point, there is nothing obviously wrong.

The code works.

So why would we change it?

---

# 2. The Code Works — But Look at What the Class Knows

Look carefully at `PaymentService`.

It knows how to process:

```text
Credit Card
PayPal
Bank Transfer
```

That means the class contains **three different payment algorithms**.

We can visualize it like this:

```text
                 PaymentService
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
     Credit Card     PayPal    Bank Transfer
      algorithm     algorithm    algorithm
```

The service is becoming responsible for every possible payment implementation.

Now imagine the business asks for:

```text
Apple Pay
Google Pay
Stripe
Wallet
Crypto
```

We would keep modifying this same method:

```java
if (...) {

}
else if (...) {

}
else if (...) {

}
else if (...) {

}
else if (...) {

}
else if (...) {

}
```

The class keeps growing.

---

# 3. The Smell

The problem is not simply:

> "There is an `if/else`."

An `if/else` by itself is not bad.

The real problem is that the `if/else` is choosing between **different algorithms for performing the same operation**.

We have:

```text
Same goal:

    Make a payment

Different algorithms:

    Credit Card
    PayPal
    Bank Transfer
```

So the `PaymentService` is carrying around multiple versions of the payment algorithm.

That gives us the first important Strategy smell:

> **A class contains multiple implementations of the same kind of behavior and selects between them using conditionals.**

This is one of the classic situations where Strategy can be useful.

---

# 4. Ask the Most Important Question

Instead of immediately creating interfaces, stop and ask:

> **What varies?**

Look at the code.

This part stays conceptually the same:

```text
Make a payment.
```

But this part changes:

```text
HOW do we make the payment?
```

For example:

```text
Credit Card
    → validate card
    → charge card
    → save transaction

PayPal
    → connect to PayPal
    → charge PayPal
    → save transaction

Bank Transfer
    → create transfer
    → process transfer
    → save transaction
```

So:

```text
WHAT stays stable?
        ↓
    Pay amount

WHAT varies?
        ↓
    Payment algorithm
```

That is the thing we want to extract.

---

# 5. Extract the Varying Behavior

We create an interface representing the common payment behavior.

```java
interface PaymentStrategy {

    void pay(double amount);
}
```

This interface means:

> "Any payment strategy must know how to process a payment."

Notice that the interface does **not** know how the payment happens.

It only defines the operation:

```java
pay(amount)
```

Now our different algorithms can implement that contract.

---

# 6. Create the Credit Card Strategy

```java
class CreditCardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Validating credit card");

        System.out.println(
            "Charging credit card: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

Now the credit card algorithm has its own home.

It is no longer inside `PaymentService`.

---

# 7. Create the PayPal Strategy

```java
class PayPalPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Connecting to PayPal");

        System.out.println(
            "Charging PayPal: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

Again, PayPal has its own implementation.

---

# 8. Create the Bank Transfer Strategy

```java
class BankTransferPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Creating bank transfer");

        System.out.println(
            "Processing bank transfer: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

Now we have:

```text
                 PaymentStrategy
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
   CreditCard      PayPal      BankTransfer
    Payment        Payment        Payment
```

Each class represents one payment algorithm.

---

# 9. Now Change the PaymentService

Previously, `PaymentService` contained all the algorithms.

Now it doesn't.

It simply holds a `PaymentStrategy`.

```java
class PaymentService {

    private PaymentStrategy paymentStrategy;

    PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    void pay(double amount) {

        paymentStrategy.pay(amount);
    }
}
```

This is the most important part.

Look at this:

```java
private PaymentStrategy paymentStrategy;
```

It means:

```text
PaymentService HAS-A PaymentStrategy
```

That is **composition**.

The service does not inherit the payment algorithm.

It **contains an object that provides the payment algorithm**.

---

# 10. How Does the Service Know Which Payment to Use?

It doesn't.

The caller gives it the strategy.

For example:

```java
PaymentStrategy strategy =
    new CreditCardPayment();
```

Then:

```java
PaymentService paymentService =
    new PaymentService(strategy);
```

Now the relationship is:

```text
PaymentService
      │
      │ has
      ▼
PaymentStrategy
      ▲
      │
      │ actual object
      │
CreditCardPayment
```

Then:

```java
paymentService.pay(500);
```

internally becomes:

```java
paymentStrategy.pay(500);
```

Since the actual object is `CreditCardPayment`, Java executes:

```java
CreditCardPayment.pay(500);
```

---

# 11. Complete Payment Example

Now let's put everything together.

## Strategy interface

```java
interface PaymentStrategy {

    void pay(double amount);
}
```

## Credit Card Strategy

```java
class CreditCardPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Validating credit card");

        System.out.println(
            "Charging credit card: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

## PayPal Strategy

```java
class PayPalPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Connecting to PayPal");

        System.out.println(
            "Charging PayPal: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

## Bank Transfer Strategy

```java
class BankTransferPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Creating bank transfer");

        System.out.println(
            "Processing bank transfer: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

## Context

```java
class PaymentService {

    private PaymentStrategy paymentStrategy;

    PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    void pay(double amount) {

        paymentStrategy.pay(amount);
    }
}
```

## Client

```java
public class Main {

    public static void main(String[] args) {

        PaymentStrategy strategy =
            new CreditCardPayment();

        PaymentService paymentService =
            new PaymentService(strategy);

        paymentService.pay(500);
    }
}
```

Output:

```text
Validating credit card
Charging credit card: 500.0
Saving transaction
```

---

# 12. What Changed?

Before:

```text
PaymentService
│
├── if CREDIT_CARD
│      └── credit card algorithm
│
├── else if PAYPAL
│      └── PayPal algorithm
│
└── else if BANK_TRANSFER
       └── bank transfer algorithm
```

After:

```text
                    PaymentService
                          │
                          │ has
                          ▼
                  PaymentStrategy
                          ▲
                          │
             ┌────────────┼────────────┐
             │            │            │
             ▼            ▼            ▼
      CreditCard       PayPal      BankTransfer
       Payment         Payment        Payment
```

The payment algorithms have been **removed from the service**.

The service only knows:

```java
paymentStrategy.pay(amount);
```

It doesn't care how the payment happens.

---

# 13. Adding Another Payment Method

Suppose we now want Apple Pay.

Before Strategy, we would modify `PaymentService`:

```java
else if (paymentType.equals("APPLE_PAY")) {

    // Apple Pay algorithm
}
```

With Strategy, we create another strategy:

```java
class ApplePayPayment implements PaymentStrategy {

    @Override
    public void pay(double amount) {

        System.out.println("Authenticating Apple Pay");

        System.out.println(
            "Charging Apple Pay: " + amount
        );

        System.out.println("Saving transaction");
    }
}
```

Now:

```java
PaymentService paymentService =
    new PaymentService(
        new ApplePayPayment()
    );

paymentService.pay(500);
```

We didn't modify `PaymentService`.

That's the important benefit.

The **new algorithm is added as a new strategy**, rather than being inserted into the existing context.

---

# 14. What Exactly Did Strategy Solve?

Before:

```text
PaymentService
    ↓
knows every payment algorithm
```

After:

```text
PaymentService
    ↓
knows only PaymentStrategy
```

And:

```text
PaymentStrategy
    ↓
CreditCardPayment
PayPalPayment
BankTransferPayment
ApplePayPayment
```

So the responsibility is separated:

```text
PaymentService
    → coordinates payment

CreditCardPayment
    → knows how to process credit card

PayPalPayment
    → knows how to process PayPal

BankTransferPayment
    → knows how to process bank transfer
```

Each algorithm now has its own class.

---

# Part 2 — Strategy for an Inheritance Problem

Now let's look at a completely different problem.

This time, we don't have a huge `if/else`.

Instead, we have an **inheritance problem**.

---

# 15. The Original Inheritance Design

Suppose we have birds.

We create a parent class:

```java
class Bird {

    void fly() {
        System.out.println("Flying");
    }
}
```

Then:

```java
class Sparrow extends Bird {
}
```

and:

```java
class Eagle extends Bird {
}
```

This looks reasonable.

Both can fly.

Our hierarchy is:

```text
Bird
 │
 ├── Sparrow
 │
 └── Eagle
```

Because `Sparrow` extends `Bird`, it automatically receives:

```java
fly()
```

Same for Eagle.

So:

```java
Sparrow sparrow = new Sparrow();

sparrow.fly();
```

prints:

```text
Flying
```

Everything seems fine.

---

# 16. Now Add Penguin

We add:

```java
class Penguin extends Bird {
}
```

Now:

```text
Bird
 │
 ├── Sparrow
 │      └── fly()
 │
 ├── Eagle
 │      └── fly()
 │
 └── Penguin
        └── fly()  ← Problem
```

Because `Penguin` extends `Bird`, it automatically inherits:

```java
fly()
```

But penguins cannot fly.

So our model is forcing a behavior onto the child.

---

# 17. The Ugly Fix

We might say:

> "Fine, Penguin will override it."

```java
class Penguin extends Bird {

    @Override
    void fly() {

        System.out.println(
            "I cannot fly"
        );
    }
}
```

Now:

```java
Penguin penguin = new Penguin();

penguin.fly();
```

prints:

```text
I cannot fly
```

Technically, the program works.

But the design is telling us something.

---

# 18. What Is Wrong Here?

Look at what happened.

The parent says:

```text
Bird
    → fly()
```

The child says:

```text
Penguin
    → I don't want that behavior.
```

So we have:

```text
Parent gives behavior
        ↓
Child doesn't support it
        ↓
Child overrides the behavior
        ↓
Child says "I cannot do this"
```

That is a smell.

The problem is not that inheritance itself is bad.

This relationship is still true:

```text
Penguin IS-A Bird
```

The problem is that we put **flying behavior into the common parent** even though that behavior does not apply to every bird.

A subclass needing to override inherited behavior because it cannot support the parent's behavior is a useful signal to reconsider where that behavior belongs.

---

# 19. Ask the Same Question Again

Now use the exact same Strategy thinking from the payment example.

Ask:

> **What varies?**

Here:

```text
Bird
```

is the object.

But:

```text
Flying
```

is a behavior.

And flying behavior can vary.

For example:

```text
Flying behavior
│
├── Fly with wings
└── Cannot fly
```

So instead of putting:

```java
fly()
```

directly inside `Bird`, we can extract flying into its own abstraction.

---

# 20. Create the Strategy Interface

```java
interface FlyBehavior {

    void fly();
}
```

This says:

> Any flying behavior must provide `fly()`.

---

# 21. Create the Flying Strategy

For birds that can fly:

```java
class FlyWithWings implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println(
            "Flying with wings"
        );
    }
}
```

And for birds that cannot fly:

```java
class FlyNoWay implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println(
            "I cannot fly"
        );
    }
}
```

Now:

```text
                FlyBehavior
                     ▲
                     │
          ┌──────────┴──────────┐
          │                     │
          ▼                     ▼
   FlyWithWings             FlyNoWay
```

Flying is no longer tied directly to the `Bird` class.

---

# 22. Put the Strategy Inside Bird

Now `Bird` gets a `FlyBehavior`.

```java
class Bird {

    private FlyBehavior flyBehavior;

    Bird(FlyBehavior flyBehavior) {

        this.flyBehavior = flyBehavior;
    }

    void fly() {

        flyBehavior.fly();
    }
}
```

Again:

```java
private FlyBehavior flyBehavior;
```

means:

```text
Bird HAS-A FlyBehavior
```

This is composition.

The bird doesn't inherit the flying algorithm.

It contains an object that provides the flying behavior.

---

# 23. Give Different Birds Different Behaviors

For a flying bird:

```java
Bird sparrow =
    new Bird(
        new FlyWithWings()
    );
```

For a penguin:

```java
Bird penguin =
    new Bird(
        new FlyNoWay()
    );
```

Now:

```java
sparrow.fly();
```

calls:

```text
FlyWithWings.fly()
```

and:

```java
penguin.fly();
```

calls:

```text
FlyNoWay.fly()
```

The behavior is selected through composition.

---

# 24. Complete Bird Example

## Strategy interface

```java
interface FlyBehavior {

    void fly();
}
```

## Concrete Strategy 1

```java
class FlyWithWings implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println(
            "Flying with wings"
        );
    }
}
```

## Concrete Strategy 2

```java
class FlyNoWay implements FlyBehavior {

    @Override
    public void fly() {

        System.out.println(
            "I cannot fly"
        );
    }
}
```

## Context

```java
class Bird {

    private FlyBehavior flyBehavior;

    Bird(FlyBehavior flyBehavior) {

        this.flyBehavior = flyBehavior;
    }

    void fly() {

        flyBehavior.fly();
    }
}
```

## Client

```java
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
```

Output:

```text
Flying with wings
I cannot fly
```

---

# 25. What Happened to Inheritance?

This is important.

We didn't necessarily discover that:

> "Bird inheritance is bad."

The real problem was:

```text
Bird
    └── owns fly()
```

because not every bird can fly.

We separated the behavior:

```text
Bird
   │
   │ has
   ▼
FlyBehavior
   ▲
   │
   ├── FlyWithWings
   └── FlyNoWay
```

So now:

```text
Bird
    → owns bird-related state/behavior

FlyBehavior
    → owns flying behavior
```

That is much cleaner.

---

# 26. The Two Problems Are Different

This is the most important part of the lesson.

The **payment problem** and the **bird problem** did not start the same way.

### Payment

We started with:

```java
if (...)
else if (...)
else if (...)
```

The smell was:

```text
Multiple algorithms inside one class.
```

We extracted:

```text
PaymentStrategy
```

---

### Bird

We started with:

```java
class Penguin extends Bird
```

The smell was:

```text
A child inherits behavior that it does not support.
```

We extracted:

```text
FlyBehavior
```

---

# 27. But Both Lead to the Same Design

This is the beautiful part.

Both problems eventually become:

```text
             Context
                │
                │ has
                ▼
             Strategy
                ▲
                │
        ┌───────┼───────┐
        │       │       │
        ▼       ▼       ▼
       A       B       C
```

For payment:

```text
PaymentService
      │
      ▼
PaymentStrategy
      ▲
      │
 ┌────┼──────────────┐
 │    │              │
 ▼    ▼              ▼
Card PayPal      BankTransfer
```

For birds:

```text
Bird
 │
 ▼
FlyBehavior
 ▲
 │
 ├── FlyWithWings
 └── FlyNoWay
```

Different problem.

Same design idea.

---

# 28. The Real Strategy Pattern

Now we can define it properly.

The Strategy Pattern consists of three important parts.

### 1. Strategy

The common interface.

```java
interface PaymentStrategy {

    void pay(double amount);
}
```

or:

```java
interface FlyBehavior {

    void fly();
}
```

---

### 2. Concrete Strategies

Different implementations.

```text
CreditCardPayment
PayPalPayment
BankTransferPayment
```

or:

```text
FlyWithWings
FlyNoWay
```

---

### 3. Context

The class that **uses** the strategy.

```java
class PaymentService {

    private PaymentStrategy paymentStrategy;
}
```

or:

```java
class Bird {

    private FlyBehavior flyBehavior;
}
```

The Context delegates the varying behavior to the strategy it contains. This Context–Strategy–ConcreteStrategy structure is the standard Strategy pattern structure.

---

# 29. The Complete Mental Transformation

Whenever you recognize a Strategy problem, think through these steps.

## Step 1 — Find the behavior

Ask:

```text
What behavior is changing?
```

Payment:

```text
Payment algorithm
```

Bird:

```text
Flying behavior
```

---

## Step 2 — Separate it

Create an interface:

```java
interface Strategy {

    void execute();
}
```

---

## Step 3 — Move each variation into its own class

```text
Strategy
   ▲
   │
   ├── Implementation A
   ├── Implementation B
   └── Implementation C
```

---

## Step 4 — Compose it

The main class gets:

```java
private Strategy strategy;
```

---

## Step 5 — Delegate

Instead of implementing the algorithm itself:

```java
strategy.execute();
```

---

# 30. The Simplest Way to Remember Strategy

Think:

```text
BEFORE

Class
  │
  ├── behavior A
  ├── behavior B
  └── behavior C
```

Strategy changes it to:

```text
AFTER

Class
  │
  │ has
  ▼
Strategy
  │
  ├── behavior A
  ├── behavior B
  └── behavior C
```

The behaviors are now **separate objects**.

That is why Strategy is strongly associated with composition and delegation.

---

# 31. Final Rule

When you see:

```java
if (type == A) {
    // algorithm A
}
else if (type == B) {
    // algorithm B
}
else if (type == C) {
    // algorithm C
}
```

ask:

> **Are A, B, and C different ways of performing the same operation?**

If yes, Strategy may be appropriate.

And when you see:

```text
Parent
   ↓
Child
   ↓
Child doesn't actually want inherited behavior
```

ask:

> **Is that behavior itself something that can vary?**

If yes, consider extracting that behavior into a Strategy and using composition.

---

# 32. One-Line Definition

Finally:

> **Strategy Pattern encapsulates a family of interchangeable behaviors behind an interface and uses composition to allow the object that needs the behavior to delegate to whichever implementation is selected.**

Or, in the simplest possible form:

```text
WHAT stays the same
        +
HOW it is done can change
        ↓
Extract the HOW
        ↓
Strategy interface
        ↓
Different implementations
        ↓
Composition
        ↓
Delegate
```

That is the Strategy Pattern.