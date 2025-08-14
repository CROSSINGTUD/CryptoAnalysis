# Contributing

We hare happy for every contribution from the community! Please consider the following aspects when contributing to this project.

## Coding Guidelines

We aim for keeping the code as clean as possible. To do so, we follow the Google style guide for Java/Android. To format the code, run the command

```shell
mvn spotless:apply
```

before committing your changes.

## Workflow

We aim for the following workflow:

1) Create a fork from this repository

2) Create a new branch and push your changes to it

3) Open a pull request against the `develop` branch from this repository
   
   - The pull request should contain a small description of the changes and why they are needed

4) Wait for a review or approval
   
When adding your changes, please make sure that all tests are passing. We have corresponding GitHub actions that check whether there are any problems. You can also check the status locally (see [Installation and Setup](installation.md)).

## Branches

This repository contains two central branches. The **master** and the **develop** branch. The `develop` branch is default. Both branches are protected against direct write access, thus Pull Requests are necessary to push into them. Other branches are unprotected and can be created and deleted by a contributor

The `master` branch holds the latest stable release of the application.

The `develop` branch holds the latest development version of the application.

New branches should always target `develop`. Once, we decide to release a new version, we merge the changes from the `develop` branch into the `master` branch and deploy the changes to Maven Central.

### Branching

Since `master` and `develop` branches are protected, working with branches is mandatory.

In general, each branch shall only be responsible for one idea. This way we try to minimize the amount of changes in all branches, which makes it easier to review.

### Naming Branches

Branch names should be declarative, meaning the name of a branch shall always yield what it's ultimately going to change. Since a branch can target different aspects of development (e.g. feature, bug-fix, refactoring, etc.) their names shall have that information also included by adding a *PREFIX*. The scheme of a branch name look as follows: `PREFIX/tell-what-it-does`

For example, we suggest the following prefixes:

```
feature/    // For new features
fix/        // Fixes a bug
```
