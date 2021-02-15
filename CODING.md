# Coding Guidelines

## General Notes
In general we aim for keeping the coding guidelines that are common for the Java Language. This includes code style,
as well as the usage of coding patterns. Exceptions have to get discussed and agreed with the project leads.

## Checkstyle Config
This repository contains a `checkstyle.xml` Checkstyle config which gets automatically used by the CI Runner for code 
validation on the **master**, **develop** and **Pull Requests** branches.

Feature branches are not automatically checked because we want to get development done quickly. To achieve this, everybody may codes as 
they feel most familiar. A pull request will make sure `develop` and `master` branches are consistent.   

The Checkstyle config got confirmed by the core development team at UPB, TUD and Fraunhofer IEM. Changes may only be made by the project leaders
for valid reasons.

### Tool Support

Checkstyle is available for various IDEs.

##### Eclipse
1. Install Checkstyle plugin from [here](https://checkstyle.org/eclipse-cs/)
2. In your preferences window you can select the Checkstyle configuration `checkstyle.xml` from the root of the
repository.
3. Configure the Eclipse Code Formatter to automatically format the code regarding the Checkstyle rules. 
Unfortunately it does not support importing the Checkstyle Rules directly.

##### IntelliJ
1. Search for "Checkstyle-IDEA" in the `Settings->Plugin` window.
2. Install and restart the IDE.
3. Open settings again and select the `iem_checks.xml` file in `Settings-Checkstyle`.
4. In `Settings->Editor->Code Style` from the Scheme combobox you can import that Checkstyle file to apply all settings 
for code refactoring tools of the IDE. 
5. In the bottom right or left corner of the IDE there should be a Checkstyle Tool-Window. 
Open it and let the plugin check your code.
