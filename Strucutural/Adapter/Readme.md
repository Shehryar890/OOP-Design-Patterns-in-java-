Adapter Pattern in Java
1. What Problem Does Adapter Solve?
Suppose our application has this interface:
public interface PaymentService {
    void pay(double amount);
}

Our application expects every payment implementation to provide:
pay(double amount)

Now suppose we already have an existing payment library:
public class OldPaymentSystem {

    public void makePayment(double amount) {
        System.out.println("Processing payment: " + amount);
    }
}

At first glance, they look almost the same.
Our application expects:
PaymentService
      ↓
pay(amount)

But the existing system provides:
OldPaymentSystem
      ↓
makePayment(amount)

The method names are different, so we cannot directly use OldPaymentSystem where PaymentService is expected.
2. We Already Have the Logic
This is the important part.
OldPaymentSystem already contains the payment logic:
public void makePayment(double amount) {
    System.out.println("Processing payment: " + amount);
}

We don't want to rewrite that logic:
public void pay(double amount) {
    // rewrite the same payment logic again ❌
}

Why duplicate it?
Instead, we can simply connect our interface to the existing class.
That's what the Adapter does.
3. Create the Adapter
public class PaymentAdapter implements PaymentService {

    private final OldPaymentSystem oldPaymentSystem;

    public PaymentAdapter(OldPaymentSystem oldPaymentSystem) {
        this.oldPaymentSystem = oldPaymentSystem;
    }

    @Override
    public void pay(double amount) {
        oldPaymentSystem.makePayment(amount);
    }
}

Look at what's happening:
Our application calls:
pay(amount)

The adapter receives that call and calls:
makePayment(amount)

So the adapter is basically a translator between two interfaces.
Our Application
      │
      │ pay(500)
      ▼
PaymentAdapter
      │
      │ makePayment(500)
      ▼
OldPaymentSystem
      │
      ▼
Existing payment logic

We didn't rewrite the payment logic.
We simply adapted the existing class to our interface.
4. Complete Example
Our interface
public interface PaymentService {

    void pay(double amount);
}

Existing class
public class OldPaymentSystem {

    public void makePayment(double amount) {

        System.out.println(
                "Processing payment: " + amount
        );
    }
}

Adapter
public class PaymentAdapter
        implements PaymentService {

    private final OldPaymentSystem oldPaymentSystem;

    public PaymentAdapter(
            OldPaymentSystem oldPaymentSystem
    ) {
        this.oldPaymentSystem = oldPaymentSystem;
    }

    @Override
    public void pay(double amount) {

        oldPaymentSystem.makePayment(amount);
    }
}

Client
public class Main {

    public static void main(String[] args) {

        OldPaymentSystem oldPaymentSystem =
                new OldPaymentSystem();

        PaymentService paymentService =
                new PaymentAdapter(oldPaymentSystem);

        paymentService.pay(500);
    }
}

Output:
Processing payment: 500.0

5. What Exactly Did We Adapt?
This is the whole pattern:
Application expects
        ↓
PaymentService
        ↓
pay(amount)


Existing class provides
        ↓
OldPaymentSystem
        ↓
makePayment(amount)

They represent the same general operation, but their interfaces don't match.
So:
PaymentService
      ↑
      │ implements
      │
PaymentAdapter
      │
      │ calls
      ↓
OldPaymentSystem

The adapter makes the existing class usable through the interface our application expects.
6. Backend Example
Now imagine a real backend.
Your application wants an email service:
public interface EmailSender {

    void send(
            String to,
            String subject,
            String body
    );
}

Your business code is written against this:
public class NotificationService {

    private final EmailSender emailSender;

    public NotificationService(
            EmailSender emailSender
    ) {
        this.emailSender = emailSender;
    }

    public void sendWelcomeEmail(String email) {

        emailSender.send(
                email,
                "Welcome",
                "Welcome to our platform"
        );
    }
}

Now you integrate an existing email library.
Its API looks like this:
public class ExternalEmailClient {

    public void sendMessage(
            String recipient,
            String title,
            String content
    ) {

        System.out.println(
                "Sending email using external service"
        );
    }
}

Again, the interfaces don't match.
Your application expects:
send(to, subject, body)

The library provides:
sendMessage(recipient, title, content)

We already have the library's email logic.
So instead of rewriting it, create an adapter:
public class EmailAdapter
        implements EmailSender {

    private final ExternalEmailClient emailClient;

    public EmailAdapter(
            ExternalEmailClient emailClient
    ) {
        this.emailClient = emailClient;
    }

    @Override
    public void send(
            String to,
            String subject,
            String body
    ) {

        emailClient.sendMessage(
                to,
                subject,
                body
        );
    }
}

Now:
NotificationService
        │
        │ send(to, subject, body)
        ▼
    EmailSender
        ▲
        │
   EmailAdapter
        │
        │ sendMessage(...)
        ▼
ExternalEmailClient

The important thing is that NotificationService doesn't need to know the external library's API.
7. Why Not Just Use the Library Directly?
We could do:
emailClient.sendMessage(...);

everywhere.
But then our application becomes dependent on that external API:
OrderService ──────► ExternalEmailClient
UserService ───────► ExternalEmailClient
NotificationService ► ExternalEmailClient

With an adapter:
OrderService ───────┐
UserService ────────┤
NotificationService ┤
                     ▼
                 EmailSender
                     ▲
                     │
                EmailAdapter
                     │
                     ▼
             ExternalEmailClient

Our application uses our own interface, while the adapter deals with the external system.