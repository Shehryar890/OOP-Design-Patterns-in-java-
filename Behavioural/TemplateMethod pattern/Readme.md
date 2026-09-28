# Template Method Pattern

The **Template Method Pattern** is useful when multiple classes need to perform the **same overall algorithm in the same order**, but **one or more steps of that algorithm can vary** between implementations.

The main idea is:

> **The parent class defines the overall workflow, while subclasses provide the implementation of the steps that can vary.**

---

## 1. The Problem

Suppose we are building a system where users can upload files.

After a file is uploaded, our application needs to perform a sequence of operations:

```text
Read File
    ↓
Print File Name
    ↓
Print File Size
    ↓
Print File Type
    ↓
Save File
```

The first four operations are the same regardless of where we save the file.

However, the final operation can be different.

We might save the file to:

1. Database
2. Hard Disk
3. S3 Bucket

So we have:

```text
Same overall process
       +
One step that varies
```

The algorithm is stable, but one part of the algorithm changes.

---

# 2. First Attempt — One Class

We could initially put the complete process inside one `FileSaver` class:

```java
class FileSaver {

    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    void readFile() {
        System.out.println("Reading file");
    }

    void printFileName() {
        System.out.println("Printing file name");
    }

    void printFileSize() {
        System.out.println("Printing file size");
    }

    void printFileType() {
        System.out.println("Printing file type");
    }

    void saveFile() {
        System.out.println("Saving file");
    }
}
```

The `save()` method contains the complete workflow:

```java
void save() {

    readFile();
    printFileName();
    printFileSize();
    printFileType();
    saveFile();
}
```

We can think of this method as the **recipe for saving a file**.

It says:

```text
First  → Read the file
Second → Print file name
Third  → Print file size
Fourth → Print file type
Finally → Save the file
```

So far, everything is fine.

The problem appears when we introduce multiple ways of saving the file.

---

# 3. Different Types of File Savers

Suppose we need:

```text
DatabaseSaver
HardDiskSaver
S3BucketSaver
```

All of them should follow the same process:

```text
readFile()
    ↓
printFileName()
    ↓
printFileSize()
    ↓
printFileType()
    ↓
saveFile()
```

But their `saveFile()` operation is different:

```text
DatabaseSaver
     ↓
saveFile()
     ↓
Save in Database
```

```text
HardDiskSaver
     ↓
saveFile()
     ↓
Save on Hard Disk
```

```text
S3BucketSaver
     ↓
saveFile()
     ↓
Save in S3 Bucket
```

So the situation is:

```text
             SAME ALGORITHM
                   │
      ┌────────────┴────────────┐
      │                         │
  Common steps             Variable step
      │                         │
      ↓                         ↓
readFile()                 saveFile()
printFileName()                 │
printFileSize()          ┌─────┼─────┐
printFileType()           ↓     ↓     ↓
                         DB   Disk    S3
```

---

# 4. Naive Inheritance Solution

We might think:

> "Let's create a parent class and let the different savers extend it."

So we create:

```java
class FileSaver {

    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    void readFile() {
        System.out.println("Reading file");
    }

    void printFileName() {
        System.out.println("Printing file name");
    }

    void printFileSize() {
        System.out.println("Printing file size");
    }

    void printFileType() {
        System.out.println("Printing file type");
    }

    void saveFile() {
        System.out.println("Saving file");
    }
}
```

Then:

```java
class DatabaseSaver extends FileSaver {

    @Override
    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    @Override
    void saveFile() {
        System.out.println("Saving file in Database");
    }
}
```

And similarly:

```java
class HardDiskSaver extends FileSaver {

    @Override
    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    @Override
    void saveFile() {
        System.out.println("Saving file on Hard Disk");
    }
}
```

And:

```java
class S3BucketSaver extends FileSaver {

    @Override
    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    @Override
    void saveFile() {
        System.out.println("Saving file in S3 Bucket");
    }
}
```

---

# 5. What Is Wrong With This?

Look carefully at all three subclasses.

The `save()` method is exactly the same:

```java
void save() {

    readFile();
    printFileName();
    printFileSize();
    printFileType();
    saveFile();
}
```

We are copying the **same algorithm** into every subclass.

But what actually changes?

Only this:

```java
saveFile();
```

For example:

```text
DatabaseSaver
     ↓
saveFile()
     ↓
Database-specific implementation
```

```text
HardDiskSaver
     ↓
saveFile()
     ↓
Hard-disk-specific implementation
```

```text
S3BucketSaver
     ↓
saveFile()
     ↓
S3-specific implementation
```

So we can ask:

> **Why should every subclass redefine the entire `save()` algorithm when only one step is different?**

It shouldn't.

This is where the **Template Method Pattern** becomes useful.

---

# 6. Template Method Solution

Instead of allowing subclasses to redefine the entire algorithm, we put the complete workflow in the parent class.

The parent says:

> **"I know the correct order in which a file should be saved. Subclasses only need to provide the part that varies."**

```java
abstract class FileSaver {

    final void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();
        saveFile();
    }

    void readFile() {
        System.out.println("Reading file");
    }

    void printFileName() {
        System.out.println("Printing file name");
    }

    void printFileSize() {
        System.out.println("Printing file size");
    }

    void printFileType() {
        System.out.println("Printing file type");
    }

    abstract void saveFile();
}
```

Now notice the important part:

```java
final void save()
```

This is our **Template Method**.

It defines the complete algorithm:

```text
readFile()
    ↓
printFileName()
    ↓
printFileSize()
    ↓
printFileType()
    ↓
saveFile()
```

The parent owns this workflow.

---

# 7. Common Steps vs Variable Steps

The parent class contains the steps that are common:

```java
void readFile()
void printFileName()
void printFileSize()
void printFileType()
```

These implementations are already provided.

But `saveFile()` is different.

Therefore, we make it abstract:

```java
abstract void saveFile();
```

This basically means:

> **"I know that saving must happen at this point, but I don't know how each storage type will perform the saving."**

So:

```text
            FileSaver
                │
      ┌─────────┴─────────┐
      │                   │
   COMMON              VARIABLE
    STEPS                 STEP
      │                    │
      ↓                    ↓
readFile()             saveFile()
printFileName()            │
printFileSize()      ┌─────┼─────┐
printFileType()       ↓     ↓     ↓
                     DB   Disk    S3
```

---

# 8. Subclasses Only Implement What Varies

Now the database implementation becomes very small:

```java
class DatabaseSaver extends FileSaver {

    @Override
    void saveFile() {
        System.out.println("Saving file in Database");
    }
}
```

Hard disk:

```java
class HardDiskSaver extends FileSaver {

    @Override
    void saveFile() {
        System.out.println("Saving file on Hard Disk");
    }
}
```

S3:

```java
class S3BucketSaver extends FileSaver {

    @Override
    void saveFile() {
        System.out.println("Saving file in S3 Bucket");
    }
}
```

Look at the difference.

The subclasses **do not copy**:

```java
readFile();
printFileName();
printFileSize();
printFileType();
```

They only provide:

```java
saveFile();
```

because that is the part that varies.

---

# 9. Final Structure

The class hierarchy becomes:

```text
                         FileSaver
                       <<abstract>>
                            │
           ┌────────────────┼────────────────┐
           ↓                ↓                ↓
    DatabaseSaver     HardDiskSaver    S3BucketSaver
           │                │                │
           ↓                ↓                ↓
      saveFile()       saveFile()       saveFile()
           │                │                │
           ↓                ↓                ↓
       Database         Hard Disk            S3
```

And the workflow is controlled by the parent:

```text
                FileSaver.save()
                       │
                       ↓
                  readFile()
                       │
                       ↓
               printFileName()
                       │
                       ↓
                printFileSize()
                       │
                       ↓
                printFileType()
                       │
                       ↓
                  saveFile()
                       │
             ┌─────────┼─────────┐
             ↓         ↓         ↓
          Database  Hard Disk    S3
```

---

# 10. What Happens When the Client Calls It?

Suppose the client writes:

```java
FileSaver saver = new S3BucketSaver();

saver.save();
```

There are two important things here:

### Reference type

```java
FileSaver saver
```

### Actual object

```java
new S3BucketSaver()
```

When we call:

```java
saver.save();
```

the `save()` method is the Template Method defined by `FileSaver`.

So execution starts with:

```text
FileSaver.save()
      ↓
readFile()
      ↓
printFileName()
      ↓
printFileSize()
      ↓
printFileType()
      ↓
saveFile()
```

When Java reaches:

```java
saveFile();
```

runtime polymorphism determines which implementation should execute.

The actual object is:

```java
S3BucketSaver
```

Therefore:

```java
S3BucketSaver.saveFile();
```

executes.

Conceptually:

```text
FileSaver.save()
      │
      ├── readFile()
      │
      ├── printFileName()
      │
      ├── printFileSize()
      │
      ├── printFileType()
      │
      └── S3BucketSaver.saveFile()
```

If instead we write:

```java
FileSaver saver = new DatabaseSaver();

saver.save();
```

the workflow remains exactly the same.

Only the final step changes:

```text
FileSaver.save()
      │
      ├── readFile()
      ├── printFileName()
      ├── printFileSize()
      ├── printFileType()
      │
      └── DatabaseSaver.saveFile()
```

---

# 11. Why Is It Called Template Method?

Think about a template or recipe.

For example:

```text
1. Prepare
2. Process
3. Validate
4. Save
```

The template defines the **structure and order**.

Some particular operation inside that structure can be customized.

Template Method works in exactly this way.

The parent provides the template:

```java
final void save() {

    readFile();
    printFileName();
    printFileSize();
    printFileType();
    saveFile();
}
```

And the subclasses fill in the customizable part:

```java
abstract void saveFile();
```

So:

```text
                TEMPLATE
                   │
                   ↓
              A → B → C → D
                            ↑
                            │
                      variable step
                            │
                   ┌────────┼────────┐
                   ↓        ↓        ↓
                   DB      Disk      S3
```

---

# 12. The Most Important Mental Model

Don't think:

> **"Template Method means inheritance."**

Inheritance is the mechanism being used.

The actual idea is:

> **"I have one algorithm whose overall structure should remain fixed, but some steps inside that algorithm need different implementations."**

Then:

```text
            Parent Class
                 │
                 ↓
         owns the algorithm
                 │
                 ↓
          defines the order
                 │
                 ↓
          A → B → C → D → E
                      ↑
                      │
                variable step
                      │
              ┌───────┼───────┐
              ↓       ↓       ↓
              DB     Disk      S3
```

In one sentence:

> **Template Method lets the parent control WHAT steps happen and in WHAT ORDER, while subclasses customize HOW certain steps are performed.**

---

# 13. Important Correction

A common mistake is to make every subclass override the Template Method itself:

```java
@Override
void save() {
    // same algorithm again
}
```

That defeats the main purpose of the pattern.

The parent should own the algorithm:

```java
final void save() {
    readFile();
    printFileName();
    printFileSize();
    printFileType();
    saveFile();
}
```

The subclasses should only implement the varying operation:

```java
abstract void saveFile();
```

So:

```text
                FileSaver
                    │
         ┌──────────┴──────────┐
         │                     │
      COMMON                VARIABLE
       STEPS                   STEP
         │                     │
         ↓                     ↓
  readFile()              saveFile()
  printFileName()              ↑
  printFileSize()              │
  printFileType()       ┌──────┼──────┐
                        ↓      ↓      ↓
                       DB    Disk     S3
```

That is the core of Template Method.

---

# 14. Alternative Solution — Composition + Strategy

The same problem can also be solved **without inheritance**.

Instead of:

```text
                FileSaver
                    │
         ┌──────────┼──────────┐
         ↓          ↓          ↓
     Database    HardDisk      S3
```

we can extract the varying behavior into a separate interface.

The idea is:

> **Keep the overall algorithm inside `FileSaver`, but take the varying behavior out of `FileSaver` and represent it as a separate object.**

This is where the **Strategy Pattern** can be used.

---

# 15. Extract the Varying Behavior

Our varying operation was:

```java
saveFile();
```

So we create an interface:

```java
interface FileStorageStrategy {

    void saveFile();
}
```

This interface represents the behavior that can vary.

It basically says:

> **"Anything that knows how to store a file must provide `saveFile()`."**

---

# 16. Strategy Implementations

### Database

```java
class DatabaseStorage implements FileStorageStrategy {

    @Override
    public void saveFile() {
        System.out.println("Saving file in Database");
    }
}
```

### Hard Disk

```java
class HardDiskStorage implements FileStorageStrategy {

    @Override
    public void saveFile() {
        System.out.println("Saving file on Hard Disk");
    }
}
```

### S3

```java
class S3Storage implements FileStorageStrategy {

    @Override
    public void saveFile() {
        System.out.println("Saving file in S3 Bucket");
    }
}
```

Now:

```text
            FileStorageStrategy
                     │
            ┌────────┼────────┐
            ↓        ↓        ↓
        Database   HardDisk    S3
         Storage    Storage   Storage
```

The varying behavior has been separated from `FileSaver`.

---

# 17. FileSaver With Composition

Now `FileSaver` doesn't inherit from anything.

Instead, it **contains a strategy**:

```java
class FileSaver {

    private final FileStorageStrategy storageStrategy;

    FileSaver(FileStorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    void save() {

        readFile();
        printFileName();
        printFileSize();
        printFileType();

        storageStrategy.saveFile();
    }

    void readFile() {
        System.out.println("Reading file");
    }

    void printFileName() {
        System.out.println("Printing file name");
    }

    void printFileSize() {
        System.out.println("Printing file size");
    }

    void printFileType() {
        System.out.println("Printing file type");
    }
}
```

The workflow is still:

```text
readFile()
    ↓
printFileName()
    ↓
printFileSize()
    ↓
printFileType()
    ↓
saveFile()
```

But now the final operation is delegated:

```java
storageStrategy.saveFile();
```

---

# 18. Composition Instead of Inheritance

We can now create:

```java
FileStorageStrategy s3Storage =
        new S3Storage();

FileSaver fileSaver =
        new FileSaver(s3Storage);

fileSaver.save();
```

The relationship is now:

```text
                FileSaver
                    │
                  HAS-A
                    ↓
           FileStorageStrategy
                    │
                    ↓
               S3Storage
```

Execution:

```text
FileSaver.save()
      │
      ↓
readFile()
      │
      ↓
printFileName()
      │
      ↓
printFileSize()
      │
      ↓
printFileType()
      │
      ↓
storageStrategy.saveFile()
      │
      ↓
S3Storage.saveFile()
```

The actual implementation is selected through polymorphism.

---

# 19. Changing the Strategy

We can provide a different strategy.

### Database

```java
FileStorageStrategy database =
        new DatabaseStorage();

FileSaver saver = 
        new FileSaver(database);

saver.save();
```

The final operation becomes:

```text
DatabaseStorage.saveFile()
```

### Hard Disk

```java
FileStorageStrategy hardDisk =
        new HardDiskStorage();

FileSaver saver =
        new FileSaver(hardDisk);

saver.save();
```

The final operation becomes:

```text
HardDiskStorage.saveFile()
```

### S3

```java
FileStorageStrategy s3 =
        new S3Storage();

FileSaver saver =
        new FileSaver(s3);

saver.save();
```

The final operation becomes:

```text
S3Storage.saveFile()
```

The `FileSaver` class itself does not change.

---

# 20. Template Method vs Strategy

Both designs solve the same underlying problem:

```text
Same overall algorithm
       +
Some behavior varies
```

But they organize the variation differently.

---

## Template Method

Variation is provided through **inheritance**.

```text
                   FileSaver
                 <<abstract>>
                      │
         ┌────────────┼────────────┐
         ↓            ↓            ↓
     Database      HardDisk        S3
      Saver          Saver         Saver
         │            │            │
         └────── override ─────────┘
                  saveFile()
```

The parent controls the algorithm:

```java
final void save() {

    readFile();
    printFileName();
    printFileSize();
    printFileType();
    saveFile();
}
```

The subclass provides:

```java
saveFile();
```

### Mental model

> **Parent owns the algorithm; subclass customizes the varying step.**

---

# 21. Strategy

Variation is provided through **composition**.

```text
                FileSaver
                    │
                  HAS-A
                    ↓
           FileStorageStrategy
                    │
         ┌──────────┼──────────┐
         ↓          ↓          ↓
     Database    HardDisk      S3
     Strategy    Strategy    Strategy
```

`FileSaver` still owns the workflow:

```java
void save() {

    readFile();
    printFileName();
    printFileSize();
    printFileType();

    storageStrategy.saveFile();
}
```

But instead of inheriting the behavior, it **receives an object that provides the behavior**.

### Mental model

> **Main class owns the algorithm; strategy object provides the varying behavior.**

---

# 22. The Key Difference

This is the easiest way to remember it:

```text
             SAME PROBLEM
                  │
       ┌──────────┴──────────┐
       ↓                     ↓
  INHERITANCE           COMPOSITION
       ↓                     ↓
TEMPLATE METHOD           STRATEGY
       ↓                     ↓
"Subclass changes      "Object provides
    the step"              the step"
```

Or even simpler:

```text
Template Method
       ↓
variation through inheritance


Strategy
       ↓
variation through composition
```

---

# 23. Why Composition Can Be More Flexible

With Template Method:

```java
class S3BucketSaver extends FileSaver
```

The behavior is tied to a subclass.

The relationship is:

```text
S3BucketSaver
    │
   IS-A
    ↓
FileSaver
```

With Strategy:

```java
new FileSaver(new S3Storage());
```

The behavior is supplied from outside.

The relationship is:

```text
FileSaver
   │
  HAS-A
   ↓
FileStorageStrategy
```

This is the idea behind the OOP guideline:

> **Favor composition over inheritance.**

Composition allows us to build an object by combining it with another object that provides a required behavior.

---

# 24. But Template Method Is Not "Bad"

We should not conclude:

> "Composition is good, therefore Template Method is bad."

That's not the point.

Template Method gives us a useful design option when:

```text
There is a stable algorithm
       +
Some steps need customization
       +
Inheritance represents a sensible relationship
```

The parent can control the workflow while subclasses customize selected steps.

Strategy gives us another design option:

```text
There is a stable algorithm
       +
Some behavior varies
       +
We want that behavior to be an independent object
```

Then we extract that behavior and compose it into the main class.

---

# 25. Same Problem — Two Designs

Our original problem:

```text
                Save Uploaded File
                        │
         ┌──────────────┼──────────────┐
         ↓              ↓              ↓
     Read File     File Metadata    Save File
                                         │
                               ┌─────────┼────────┐
                               ↓         ↓        ↓
                              DB       Disk       S3
```

### Template Method

```text
                   FileSaver
                       │
                controls workflow
                       │
                       ↓
                readFile()
                       ↓
                printFileName()
                       ↓
                printFileSize()
                       ↓
                printFileType()
                       ↓
                  saveFile()
                       ↑
                       │
                overridden by
                       │
          ┌────────────┼────────────┐
          ↓            ↓            ↓
         DB          Disk           S3
```

### Strategy

```text
                   FileSaver
                       │
                controls workflow
                       │
                       ↓
                readFile()
                       ↓
                printFileName()
                       ↓
                printFileSize()
                       ↓
                printFileType()
                       ↓
             storageStrategy.saveFile()
                       │
                       ↓
              FileStorageStrategy
                       │
          ┌────────────┼────────────┐
          ↓            ↓            ↓
       Database       Disk          S3
       Strategy     Strategy      Strategy
```

The **business workflow remains inside `FileSaver` in both designs**.

The difference is:

> **How do we provide the varying behavior?**

---

# 26. SpringBoot Perspective

In a Spring backend, the Strategy approach fits naturally with **composition and dependency injection**.

For example:

```java
interface FileStorageStrategy {

    void saveFile(File file);
}
```

Implementations:

```java
class DatabaseStorage implements FileStorageStrategy {

    @Override
    public void saveFile(File file) {
        // Save to database
    }
}
```

```java
class S3Storage implements FileStorageStrategy {

    @Override
    public void saveFile(File file) {
        // Upload to S3
    }
}
```

```java
class LocalStorage implements FileStorageStrategy {

    @Override
    public void saveFile(File file) {
        // Save to local disk
    }
}
```

Then the service can depend on the abstraction:

```java
class FileService {

    private final FileStorageStrategy storageStrategy;

    FileService(FileStorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    void save(File file) {

        validate(file);
        processMetadata(file);

        storageStrategy.saveFile(file);

        recordResult(file);
    }
}
```

This fits naturally with Spring dependency injection because the implementation can be provided from outside the class.

---

# 27. Final Mental Picture

Remember these two pictures.

## Template Method

```text
             PARENT
                │
                │
         owns the algorithm
                │
                ↓
       ┌─────────────────┐
       │ A → B → C → D   │
       └─────────────────┘
                 ↑
                 │
             SUBCLASS
                 │
           changes one step
```

Think:

> **"I am a specialized version of this parent. Give me the varying step."**

---

## Strategy

```text
            MAIN CLASS
                │
         owns the algorithm
                │
                ↓
         A → B → STRATEGY
                      │
                      ↓
                concrete object
```

Think:

> **"I don't need to become a different subclass. Give me an object that knows how to perform this behavior."**

---

# 28. Final Comparison

| | Template Method | Strategy |
|---|---|---|
| Main mechanism | Inheritance | Composition |
| Algorithm | Parent class | Main class |
| Variable behavior | Subclass | Separate strategy object |
| Relationship | IS-A | HAS-A |
| Can behavior be supplied from outside? | Not directly | Yes |
| Can implementation be changed by replacing object? | Not naturally | Yes |
| Main idea | Fixed algorithm + customizable steps | Fixed algorithm + interchangeable behavior |

The deepest idea is:

> **Both patterns separate the stable part of an algorithm from the part that changes.**

The difference is **where the changing behavior lives**:

```text
Template Method
       ↓
Changing behavior lives in subclasses
       ↓
Inheritance


Strategy
       ↓
Changing behavior lives in separate objects
       ↓
Composition
```

And the simplest memory trick is:

```text
TEMPLATE METHOD
"Parent controls the recipe,
 subclass fills in the step."


STRATEGY
"Class controls the recipe,
 another object provides the step."
```

---

## One-Line Definition

> **Template Method Pattern defines the skeleton of an algorithm in a parent class, keeping the order of steps fixed while allowing subclasses to customize selected steps.**