# dialog

Copyright (c) 2025–26 by Fred George  
author: Fred George fredgeorge@acm.org  
Licensed under the MIT License; see LICENSE file in root.

### Concepts

The _Dialog_ model represents the solicitation of information
between a system and a target user. Given a particular need,
a _Question_ can be presented to the user. An _Answer_ determines
the next action:

- Success, returning the _Answer_ to the system,
- Failure, usually with specifics on why the _Answer_   
was not satisfactory
- Ask another _Question_

A __Context__ component is used to track the _Answers_
for a _Dialog_. This facilitates the user going back
and "changing their mind" about a _Question_, and even
"changing their mind again" back to the original 
_Answer_. The Context also allows suspension of a 
_Dialgo_, and being able resume later.

The system triggers different _Dialogs_ based on its
needs. Needs will be expressed using the __Issue__ 
component. Possible _Issues_ would include:

- Invalid Situation indicating that changes or additional  
information is required for Success
- Missing Information indicating that new information is  
needed for Success

### Using the Dialog Model

_Dialogs_ are constructed with a Kotlin DSL. The DSL
specifies the _Question_, possible _Answers_, and the
next action associated with each _Answer_ (Success, Failure,
or next _Question_).

A _Dialog_ is associated with a particular _Issue_ that 
can resolve it. The user may be presented with multiple
_Dialogs_ if multiple _Issues_ arise.

### Project Structure

Code behavior is in the __engine__ package. 
Tests are in the __tests__ package to encourage testing 
only the public behavior of the engine.
Similarly, the persistence layer is in the 
__persistence__ package.
Included is also support for persistence
using _kotlin-serialization_ and the _Memento Pattern_.
The persistence package stops with encoding and decoding the
engine classes; interfaces to the outside world (databases,
REST APIs, or event buses) should be in yet other packages.
Some common test setup is in a separate 
__test_support__ package.

More on persistence is below.

## Building this Kotlin project using Gradle

Kotlin is relatively easy to set up with IntelliJ IDEA. 
Gradle is used for building and testing the project and is a 
prerequisite. Install if necessary.
The following instructions are for installing the code 
in IntelliJ IDEA by JetBrains. 
Adapt as necessary for your environment.

Note: This implementation was set up to use:

- IntelliJ 2026.1 (Ultimate Edition)
- Kotlin 2.3.20 (targeting Java 25 bytecode)
- Java SDK 25 LTS (Oracle)
- Gradle 9.4.1
- JUnit Jupiter 6.0.3 for testing
- Kotlin-serialization 1.8.1 for JSON

Open the reference code:

- Download the source code from github.com/fredgeorge
    - Clone or pull and extract the zip
- Open IntelliJ
- Choose "Open" (it's a Gradle project)
- Navigate to the reference code root and enter

Source and test directories should already be tagged as such,
with test directories in green.

Confirm that everything builds correctly (and the 
necessary libraries exist). From a terminal window:
```bash
./gradlew clean build test
```

Several settings may need to be manually changed if using IntelliJ IDEA:

- In File - Project Structure - Project Settings - Project, set SDK to 25 (or whatever you earlier SDK)
- In File - Settings - Build, Execution, Deployment - Compiler - Kotlin Compiler, set the Target JVM version to 25
- In File - Settings - Build, Execution, Deployment - Build Tools - Gradle, set Gradle JVM to JAVA_HOME or explicitly and select the latest Kotlin versions

## Persistence

Persistence is separated from the domain model (engine).
If embedded in the model, complexity can compromise the
clarity of the model design. To the maximum extent
possible, persistence should be separated from the model.

Persistence is handled by the Kotlin-serialization library. It 
provides a convenient way to serialize and deserialize 
Kotlin data classes to and from JSON and Base64 formats. 
This ensures that data can be easily stored and transmitted 
while maintaining its structure and integrity.

The _Memento Pattern_ (Design Patterns book) is used as the 
model for persistence. The pattern suggests an object can 
present a binary representation of itself that can only 
be reinterpreted by the object's class itself. It can't be 
used as an _encapsulation_ bypass.

The example injects _memento_ creation with an extension method
into the Rectangle class. It further injects restoration of the
class into the Companion object of Rectangle.

The base Rectangle class, to support the Memento Pattern,
defines a properly populated DTO in response to toDto(), and
must have a Companion object as a target for the restoration
injection. If JSON serialization is to be supported in creating
the memento, _@Serializable_ must be tagged on the DTO.

The creation of the _memento_ is done in the 
RectanglePersistence helper functions in the
persistence package, including the injection of the creation and
restoration functions. This helper class is solely 
responsible for the format and content of the _memento_.

The Encoding object allows for generation and 
restoration in either JSON or Base64 formats. Base64 
properly _hides_ the content of the memento from prying 
eyes. _Polymorphism_ support exists with SerializersModule 
parameter on JSON creation.
