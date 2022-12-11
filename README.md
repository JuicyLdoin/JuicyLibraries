# JuicyLibraries
A simple library loader that download .jar libraries from maven-repository.
To add the library that it will load you need to create a .json file with the description of the library in the libraries/data folder from where you launched it.

Library loader example:
```
{
  "group": "com.google.code.gson",
  "artifact": "gson",
  "version": "2.10"
}
```


After deleting .json file of library it will automatically be removed from the libraries after a restart.
