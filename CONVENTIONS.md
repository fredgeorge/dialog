# Kotlin Conventions

- Use private instance variables
- Avoid accessor methods - no getters or setters
- Only essential public methods and constructors
- Prefer immutable data classes
- Use sealed interfaces for polymorphism
- No field injection
- Constructor injection only
- Prefer kotlinx.serialization over Jackson
- Prefer extension functions over utility classes
- Extract constants into companion objects with descriptive names
- Parameter validation on construction
- Chaining constructors preferred over static methods