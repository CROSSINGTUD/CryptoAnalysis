# Coding Guidelines

We aim for keeping the code as clean as possible. To do so, we follow the Google style guide for Java. You can check whether your code complies with the guidelines, simply run the command
```mvn spotless:check```

You do not need to install a plugin to format your code. The command

```mvn spotless:apply```

formats all files. Note that the code will also be formatted when building the project, e.g. by
```mvn clean package -DskipTests```
