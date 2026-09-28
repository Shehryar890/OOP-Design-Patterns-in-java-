Command Pattern in Java

1. First Understand the Problem

Let's start with a simple example.

We have a User class.

class User {

    void createUser(User user) {
        System.out.println("I am creating a user");
    }

    void deleteUser(int id) {
        System.out.println("I am deleting the user");
    }
}

Our User class has two operations:

User
 │
 ├── createUser()
 │
 └── deleteUser()

So we can say:

User
 │
 ├── createUser()  → logic for creating a user
 │
 └── deleteUser()  → logic for deleting a user

For learning the Command Pattern, we will keep these methods exactly as they are.

2. Now We Introduce UserService

Suppose another class, UserService, wants to perform operations on User.

class UserService {

    private User user;

    UserService(User user) {
        this.user = user;
    }

    void registerUser(User user) {
        System.out.println("I am registering a user");
    }

    void removeUser(int userId) {

        System.out.println("Printing user");

        user.deleteUser(userId);
    }
}

Look carefully at this method:

void removeUser(int userId) {

    System.out.println("Printing user");

    user.deleteUser(userId);
}

UserService directly knows:

User
 │
 └── deleteUser()

It knows the exact method name of the User class.

3. What Is the Coupling Problem?

The relationship is currently:

UserService
     │
     │ knows about
     ▼
    User
     │
     │ knows specific method
     ▼
deleteUser()

UserService is directly coupled to the concrete operation:

user.deleteUser(userId);

In other words, UserService is saying:

"I know that User has a method called deleteUser(), and I know that I have to call it."

This is the coupling we want to reduce.

4. Why Can This Become a Problem?

Imagine the User class grows.

User
 │
 ├── createUser()
 ├── deleteUser()
 ├── updateUser()
 ├── activateUser()
 ├── disableUser()
 └── resetPassword()

Now UserService may start directly calling all of these:

UserService
 │
 ├── register() ─────► user.createUser()
 │
 ├── remove() ───────► user.deleteUser()
 │
 ├── update() ───────► user.updateUser()
 │
 ├── activate() ─────► user.activateUser()
 │
 └── disable() ──────► user.disableUser()

Now UserService knows a lot about the concrete operations inside User.

If the way these operations are invoked changes, the service may need to change too.

So we want to separate:

"What operation should happen?"

from:

"How is that operation executed?"

5. What Do We Want Instead?

Currently we have:

UserService
      │
      │ directly calls
      ▼
User.deleteUser()

We want:

UserService
      │
      │ doesn't know the specific operation
      ▼
Command
      │
      ▼
execute()

The idea is:

Instead of UserService directly knowing which method to call, we give it an object representing the operation.

So instead of:

user.deleteUser(userId);

we want something conceptually like:

Command command = ...;

command.execute();

Now the question becomes:

How do we represent an operation as an object?

That's where the Command Pattern starts.

6. The Core Idea of Command Pattern

The Command Pattern says:

Encapsulate a request as an object.

In simple English:

Instead of treating:

user.deleteUser(10);

only as a method call, we create an object representing:

"Delete user 10"

For example:

DeleteUserCommand

So the transformation is:

BEFORE

deleteUser()
     ↓
direct method call


AFTER

deleteUser()
     ↓
DeleteUserCommand object

The operation becomes an object.

7. Step 1 — How Do We Make a Command Interface?

Now ask:

"If we have many different operations, how can all of them be treated in the same way?"

We create a common interface.

interface Command {

    void execute();
}

This is our Command interface.

Notice what we don't put inside it.

We don't do:

interface Command {

    void createUser();

    void deleteUser();

    void updateUser();
}

No.

We only define:

void execute();

Why?

Because every command represents some operation.

We want every command to have one common operation:

execute()

So:

                    Command
                       │
                    execute()
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
       Create        Delete       Update
       Command       Command      Command

The interface gives every command the same contract.

8. Step 2 — How Do We Think About Making a UserService Operation a Command?

This is the most important step.

Look at the original UserService code:

void removeUser(int userId) {

    System.out.println("Printing user");

    user.deleteUser(userId);
}

Ask yourself:

What is the actual operation being performed here?

It is:

user.deleteUser(userId);

That operation can become a Command.

So we create:

class DeleteUserCommand implements Command {

    private final User user;
    private final int userId;

    DeleteUserCommand(User user, int userId) {
        this.user = user;
        this.userId = userId;
    }

    @Override
    public void execute() {
        user.deleteUser(userId);
    }
}

Now look at the transformation.

Before:

UserService
     │
     │ directly knows
     ▼
user.deleteUser()

After:

DeleteUserCommand
     │
     │ knows
     ▼
user.deleteUser()

This is the important movement.

The knowledge of the specific operation has moved out of UserService and into the concrete Command.

9. Important Clarification — We Are Not Moving Methods Into the Interface

This is where it is easy to misunderstand the pattern.

We are not saying:

"Take the deleteUser() method and put it inside the Command interface."

Instead:

interface Command {

    void execute();
}

The interface only says:

"A Command can be executed."

Then:

class DeleteUserCommand implements Command {

    @Override
    public void execute() {
        user.deleteUser(userId);
    }
}

The concrete Command says:

"When someone executes me, I know which operation needs to happen."

So:

Command interface
       │
       │ defines common behavior
       ▼
   execute()
       │
       ├──────────────────────┐
       ▼                      ▼
DeleteUserCommand      CreateUserCommand
       │                      │
       │ execute()             │ execute()
       ▼                      ▼
deleteUser()             createUser()

The interface provides the common abstraction.

The concrete Commands contain the operation-specific knowledge.

10. Step 3 — Convert the Other Operations Into Commands

Our User has:

createUser()
deleteUser()

We can create a Command for each operation.

Create User Command

class CreateUserCommand implements Command {

    private final User user;
    private final User newUser;

    CreateUserCommand(User user, User newUser) {
        this.user = user;
        this.newUser = newUser;
    }

    @Override
    public void execute() {
        user.createUser(newUser);
    }
}

Delete User Command

class DeleteUserCommand implements Command {

    private final User user;
    private final int userId;

    DeleteUserCommand(User user, int userId) {
        this.user = user;
        this.userId = userId;
    }

    @Override
    public void execute() {
        user.deleteUser(userId);
    }
}

Now each operation has its own Command.

11. Complete Command Hierarchy

Now we have:

                         Command
                            │
                         execute()
                            │
               ┌────────────┴────────────┐
               │                         │
               ▼                         ▼
     CreateUserCommand          DeleteUserCommand
               │                         │
               ▼                         ▼
       user.createUser()         user.deleteUser()
               │                         │
               └────────────┬────────────┘
                            ▼
                           User

This is the Command hierarchy.

The important thing is:

CreateUserCommand IS-A Command

DeleteUserCommand IS-A Command

because both implement:

Command

Therefore, we can treat either one simply as:

Command

12. Step 4 — Now Change UserService

Before Command Pattern:

class UserService {

    private User user;

    UserService(User user) {
        this.user = user;
    }

    void removeUser(int userId) {

        System.out.println("Printing user");

        user.deleteUser(userId);
    }
}

The problem is here:

user.deleteUser(userId);

UserService knows the concrete User operation.

Now after Command Pattern, we don't want UserService to know:

User
deleteUser()
createUser()
updateUser()
...

Instead, it should know:

Command

So:

class UserService {

    void executeCommand(Command command) {

        command.execute();
    }
}

Now look at the dependency.

Before:

UserService
     │
     ▼
    User
     │
     ▼
deleteUser()

After:

UserService
     │
     ▼
  Command
     │
     ▼
execute()

This is the decoupling.

13. What Happened to the User Dependency?

This is very important.

The User object is still involved.

We didn't magically remove it.

Instead, its specific knowledge moved.

Before:

UserService
     │
     │ knows
     ▼
    User
     │
     └── deleteUser()

After:

DeleteUserCommand
     │
     │ knows
     ▼
    User
     │
     └── deleteUser()

So:

BEFORE

UserService knows
which User method to call.


AFTER

DeleteUserCommand knows
which User method to call.

And:

UserService
      │
      └── only knows Command

14. Step 5 — What Does UserService Do Now?

Its job has become extremely simple.

class UserService {

    void executeCommand(Command command) {

        command.execute();
    }
}

It doesn't ask:

"Is this Create?"

"Is this Delete?"

"Is this Update?"

It doesn't need:

if (...)

It simply says:

command.execute();

Its mental model is:

"You gave me a Command. I execute it."

15. Step 6 — Who Creates the Command?

Now we have another important question.

If UserService no longer decides the operation, then:

Who decides which Command should be used?

The caller/client can decide.

For example:

class UserController {

    void deleteUser(User user, int userId) {

        Command command =
                new DeleteUserCommand(user, userId);

        UserService userService =
                new UserService();

        userService.executeCommand(command);
    }
}

The caller says:

"I want to delete user 10."

So it creates:

DeleteUserCommand

Then passes it to UserService.

16. Complete Execution Flow

Now follow the flow carefully.

                         Caller
                           │
                           │
                           │ "Delete user 10"
                           ▼
                  DeleteUserCommand
                           │
                           │ represents
                           │ delete user 10
                           ▼
                     UserService
                           │
                           │ executeCommand(command)
                           ▼
                         Command
                           │
                           │ execute()
                           ▼
                  DeleteUserCommand
                           │
                           │ knows the
                           │ specific operation
                           ▼
                    User.deleteUser()

So the caller prepares the request.

The service executes the request.

The Command knows what operation to perform.

The User performs the actual operation.

17. What Does Each Class Now Know?

This is where the design becomes clear.

Caller

Knows:

"I need DeleteUserCommand."

Command

Knows:

"I represent a delete operation."

UserService

Knows:

"I receive a Command and execute it."

User

Knows:

"I perform the actual user operation."

Diagram:

Caller
  │
  │ decides
  ▼
Command
  │
  │ knows specific operation
  ▼
User
  │
  │ performs actual work
  ▼
Operation

And separately:

UserService
     │
     │ knows only
     ▼
Command
     │
     ▼
execute()

18. Why Is the Caller More Flexible Now?

Suppose the caller wants to create a user.

It prepares:

Command command =
        new CreateUserCommand(user, newUser);

userService.executeCommand(command);

Or delete:

Command command =
        new DeleteUserCommand(user, 10);

userService.executeCommand(command);

The UserService code does not change.

It remains:

void executeCommand(Command command) {

    command.execute();
}

So:

Create request
     ↓
CreateUserCommand
     ↓
UserService.executeCommand()


Delete request
     ↓
DeleteUserCommand
     ↓
UserService.executeCommand()

Same service method.

Different Command.

19. This Is Polymorphism

Because:

CreateUserCommand implements Command
DeleteUserCommand implements Command

we can write:

Command command;

and put either one inside it.

command = new CreateUserCommand(...);

or:

command = new DeleteUserCommand(...);

Then:

userService.executeCommand(command);

The service doesn't care about the concrete type.

It simply calls:

command.execute();

Java dynamically invokes the correct implementation.

So:

Command
   │
   └── execute()
        │
        ├── CreateUserCommand → createUser()
        │
        └── DeleteUserCommand → deleteUser()

20. Before and After

BEFORE

                       Caller
                          │
                          ▼
                    UserService
                          │
                          │ directly knows
                          ▼
                         User
                          │
                 ┌────────┴────────┐
                 ▼                 ▼
           createUser()       deleteUser()

The service knows the concrete operations.

AFTER

                       Caller
                          │
                          │ creates
                          ▼
                 Concrete Command
                          │
              ┌───────────┴───────────┐
              ▼                       ▼
      CreateUserCommand       DeleteUserCommand
              │                       │
              ▼                       ▼
       createUser()             deleteUser()
              │                       │
              └───────────┬───────────┘
                          ▼
                         User


Caller
   │
   │ passes Command
   ▼
UserService
   │
   │ execute(command)
   ▼
Command

The service no longer needs to know the concrete operation.

21. Problem 2 — One UserService Method With if/else

Now let's look at another common situation.

Suppose instead of having separate methods, UserService has one method:

class UserService {

    void performOperation(String operation, User user) {

        if (operation.equals("create")) {

            user.createUser(user);

        } else if (operation.equals("delete")) {

            user.deleteUser(10);
        }
    }
}

The method is now responsible for multiple operations.

performOperation()
       │
       ├── create
       │     ↓
       │ createUser()
       │
       └── delete
             ↓
           deleteUser()

As the system grows:

if create
else if delete
else if update
else if activate
else if disable
else if resetPassword
else if verifyEmail
...

The method becomes a central place that knows every operation.

22. How Command Solves This Problem

We can take each operation represented by an if branch and make it a Command.

The create branch:

if (operation.equals("create")) {

    user.createUser(user);
}

becomes:

class CreateUserCommand implements Command {

    private final User user;

    CreateUserCommand(User user) {
        this.user = user;
    }

    @Override
    public void execute() {
        user.createUser(user);
    }
}

The delete branch:

if (operation.equals("delete")) {

    user.deleteUser(10);
}

becomes:

class DeleteUserCommand implements Command {

    private final User user;
    private final int userId;

    DeleteUserCommand(User user, int userId) {
        this.user = user;
        this.userId = userId;
    }

    @Override
    public void execute() {
        user.deleteUser(userId);
    }
}

Now:

                    Command
                       │
          ┌────────────┼────────────┐
          │            │            │
          ▼            ▼            ▼
       Create        Delete       Update
       Command       Command      Command
          │            │            │
          ▼            ▼            ▼
       create()     delete()      update()

Each operation has its own object.

23. But Where Does the if/else Go?

This is important.

Command Pattern doesn't magically remove the need to decide which operation is wanted.

Someone still has to answer:

"What operation do I want?"

For example, a caller/dispatcher/factory might decide:

Command command;

if (operation.equals("create")) {

    command = new CreateUserCommand(user);

} else if (operation.equals("delete")) {

    command = new DeleteUserCommand(user, 10);
}

But now the important separation is:

DECIDING WHICH COMMAND

        versus

EXECUTING THE COMMAND

Once the command has been created:

userService.executeCommand(command);

UserService doesn't need to know whether it is:

Create
Delete
Update

It only knows:

Command

24. Better Structure for the if/else Case

In a larger application, the responsibility for choosing the Command can be moved into a dispatcher or factory.

                    Request
                       │
                       ▼
              Dispatcher / Factory
                       │
             ┌─────────┼─────────┐
             │         │         │
             ▼         ▼         ▼
          Create     Delete     Update
          Command    Command    Command
             │         │         │
             └─────────┼─────────┘
                       ▼
                  UserService
                       │
                  execute()

So the architecture separates:

WHO DECIDES THE OPERATION?
        ↓
Caller / Dispatcher / Factory


WHO REPRESENTS THE OPERATION?
        ↓
Concrete Command


WHO EXECUTES THE COMMAND?
        ↓
UserService / Invoker


WHO DOES THE ACTUAL WORK?
        ↓
Receiver

25. Final Complete Architecture

Now combine everything.

                         CLIENT
                  Controller / Caller
                           │
                           │
                           │ decides what
                           │ operation is needed
                           ▼
                  Command / Factory
                           │
             ┌─────────────┼─────────────┐
             │             │             │
             ▼             ▼             ▼
      CreateUserCommand DeleteUserCommand UpdateUserCommand
             │             │             │
             ▼             ▼             ▼
       createUser()    deleteUser()    updateUser()
             │             │             │
             └─────────────┼─────────────┘
                           │
                           ▼
                      UserService
                           │
                           │ knows only
                           │ Command
                           ▼
                      execute(command)
                           │
                           ▼
                  Concrete Command
                           │
                           ▼
                         User

26. Final Class Structure

Receiver

class User {

    void createUser(User user) {
        System.out.println("I am creating a user");
    }

    void deleteUser(int id) {
        System.out.println("I am deleting the user");
    }
}

Command

interface Command {

    void execute();
}

Concrete Command 1

class CreateUserCommand implements Command {

    private final User user;
    private final User newUser;

    CreateUserCommand(User user, User newUser) {
        this.user = user;
        this.newUser = newUser;
    }

    @Override
    public void execute() {
        user.createUser(newUser);
    }
}

Concrete Command 2

class DeleteUserCommand implements Command {

    private final User user;
    private final int userId;

    DeleteUserCommand(User user, int userId) {
        this.user = user;
        this.userId = userId;
    }

    @Override
    public void execute() {
        user.deleteUser(userId);
    }
}

UserService / Invoker

class UserService {

    void executeCommand(Command command) {

        command.execute();
    }
}

Caller

class UserController {

    void deleteUser(User user, int userId) {

        Command command =
                new DeleteUserCommand(user, userId);

        UserService userService =
                new UserService();

        userService.executeCommand(command);
    }
}

27. The Complete Runtime Flow

Suppose the controller wants:

Delete user 10

Step 1 — Caller decides the operation

new DeleteUserCommand(user, 10);

Step 2 — The request becomes an object

DeleteUserCommand
│
├── user
├── userId = 10
└── execute()

Step 3 — Caller passes it to UserService

userService.executeCommand(command);

Step 4 — UserService only knows Command

command.execute();

Step 5 — Polymorphism calls DeleteUserCommand.execute()

user.deleteUser(userId);

Step 6 — User performs the actual operation

Complete flow:

Caller
  │
  │ "Delete user 10"
  ▼
DeleteUserCommand
  │
  │ represents request
  ▼
UserService
  │
  │ execute(command)
  ▼
Command.execute()
  │
  ▼
DeleteUserCommand.execute()
  │
  ▼
User.deleteUser(10)

28. What Actually Changed?

This is the most important comparison.

Before

UserService
     │
     │ directly knows
     ▼
User.deleteUser()

After

DeleteUserCommand
     │
     │ knows
     ▼
User.deleteUser()

And:

UserService
     │
     │ knows only
     ▼
Command.execute()

So the concrete operation knowledge moved from:

UserService

to:

DeleteUserCommand

29. The Real Meaning of the Pattern

Don't think:

"Command Pattern means putting methods into an interface."

Instead think:

"I have an operation that I want to represent as an object."

Then:

Operation
    ↓
Concrete Command
    ↓
Common Command interface

For example:

deleteUser()
    ↓
DeleteUserCommand
    ↓
Command

And:

createUser()
    ↓
CreateUserCommand
    ↓
Command

30. Why Does the Invoker Become Generic?

Without Command:

Invoker
 │
 ├── createUser()
 ├── deleteUser()
 ├── updateUser()
 ├── activateUser()
 └── disableUser()

The invoker knows every operation.

With Command:

Invoker
   │
   └── execute(Command)

That's it.

Because:

CreateUserCommand implements Command
DeleteUserCommand implements Command
UpdateUserCommand implements Command

the invoker can execute all of them through:

command.execute();

31. The Four Roles — Final Understanding

1. Client / Caller

Decides:

"What operation do I want?"

Example:

new DeleteUserCommand(user, 10);

2. Command

Defines the common contract:

interface Command {

    void execute();
}

It means:

"Every operation represented as a Command
must be executable."

3. Concrete Command

Represents one specific operation.

DeleteUserCommand
        ↓
"I represent delete user."

It contains the knowledge needed to invoke the receiver.

4. Receiver

Actually performs the operation.

User
 ↓
deleteUser()

32. The Final Mental Model

Remember this diagram:

                         CLIENT
                            │
                            │
                    "What do I want?"
                            │
                            ▼
                     COMMAND OBJECT
                            │
                            │
                    "I represent this
                         operation."
                            │
                            ▼
                       INVOKER
                            │
                            │ execute(command)
                            ▼
                     CONCRETE COMMAND
                            │
                            │
                    "I know which
                     operation to call."
                            │
                            ▼
                       RECEIVER
                            │
                            │
                    actual business work

Or even simpler:

CLIENT
  │
  │ creates/obtains Command
  ▼
COMMAND
  │
  │ represents operation
  ▼
INVOKER
  │
  │ execute()
  ▼
CONCRETE COMMAND
  │
  │ calls specific operation
  ▼
RECEIVER

33. The Core Transformation

This is the part to remember when you see Command Pattern in the future:

BEFORE

UserService
     │
     │ directly calls
     ▼
User.someOperation()

becomes:

AFTER

Caller
   │
   │ prepares
   ▼
SomeOperationCommand
   │
   ▼
UserService / Invoker
   │
   │ execute()
   ▼
SomeOperationCommand
   │
   ▼
User.someOperation()

So the specific operation has been encapsulated into a Command object.