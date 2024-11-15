# Contribution to CryptoAnaylsis
You can contribute with issues and PRs. Simply filing issues for problems you encounter is a great 
way to contribute. Contributing implementations is greatly appreciated.

## DOs and DON'Ts
Please do:
- **DO** follow the [coding guidelines](CODING.md)
- **DO** follow the naming conventions for branches.
- **DO** include tests when adding new features. When fixing bugs, start with adding a test that highlights how the current behavior is broken.
- **DO** keep the discussions focused. When a new or related topic comes up it's often better to create new issue than to side track the discussion.
- **DO** document your changes in detail in the pull requests.

Please do not:
- **DON'T** make PRs for coding guidelines changes.
- **DON'T** create a big pull requests with no related issue. Instead, first file an issue.
- **DON'T** submit PRs that use licensed files we are not allowed to use.
- **DON'T** add or change API related code without filing an issue and discussing it with the project leads first

## Suggested Workflow

We aim for the following workflow:

1. Create an issue for your work.
  - You can skip this step for trivial changes.
  - Reuse an existing issue on the topic, if there is one.
  - If you want to implement the issue yourself, please state this. 
2. Create a personal fork of the repository on GitHub. 
3. In your fork, create a new branch of develop (`git checkout -b mybranch`).
    - Apply branch naming rules (see below)
3. Make and commit your changes to your branch.
    - Keep changes as small as possible.
4. Add new tests corresponding to your change, if applicable.
  - Minor changes, like fixing typos, adding documentation or non-critical bugfixes may are excluded.
5. Build the repository with your changes (e.g. by `mvn clean package -DrunAllTests`).
    - Make sure that the builds are clean.
    - Make sure that the tests are all passing, including your new tests.
6. Create a pull request against this repository's `develop` branch.
    - If the branch is not yet ready, give the PR's name a `WIP` prefix. 
    - Check if all the Continuous Integration checks are passing.
7. When you are done, add one or more reviewer.
    - Make sure the PR has the latest changes from `develop` merged in it.
8. Wait for feedback or approval of your changes.
9. When remaining issues from feedback are resolved and all checks are green, your PR will be merged by one of the reviewers.
    - Delete the source branch with the merge.


## Branches

This repository contains two central branches. The **master** and the **develop** branch. The develop branch is default. Both branches are *protected* against direct write access, thus Pull Requests are necessary to push into them. Other branches are unprotected and can be created and deleted by a contributer

The `master` branch holds the lastest stable release of the application.

The `develop` branch holds the lastest development version of the application.

New branches should *always* target the `develop`. Once, we decide to release a new version, we merge the changes from
the `develop` branch into the `master` branch and deploy the changes to Maven Central.

### Branching
Since `master` and `develop` branches are protected, working with branches is mandatory. 

In general each branch shall only be responsible for one idea. 
This way we try to minimize the amount of changes in all branches, which makes it easier to review.

### Naming Branches
Branch names should be declarative, meaning the name of a branch shall always yield what it's ultimately going to change. Since a branch can target different aspects of development (e.g. feature, bug-fix, refactoring, etc.) their names shall have that information also included by adding a PREFIX. The scheme of a branch name look as follows: `PREFIX/tell-what-it-does`

We suggest the following prefixes:
```
feature/    // For new features
fix/        // Fixes a bug
```

### Tests
Since we have multiple modules, we configured Maven to run subsets of the tests. You have the following options:

- Use `-DrunAnalysis` to run the tests for the analysis (CryptoAnalysis)
- Use `-DrunSootTests` to run the tests for the HeadlessJavaScanner with Soot enabled
- Use `-DrunSootUpTests` to run the tests for the HeadlessJavaScanner with SootUp enabled (not implemented yet)
- Use `-DrunOpalTests` to run the tests for the HeadlessJavaScanner with Opal enabled (not implemented yet)
- Use `-DrunAndroidTests` to run the tests for the HeadlessAndroidScanner
- Use `-DrunAllTests` to run the tests for CryptoAnalysis, HeadlessAndroidScanner and HeadlessJavaScanner (with Soot)

In all cases, the project is built and the corresponding tests are executed. For example, `mvn clean package -DrunAnalysisTests` builds the project and runs only the tests for the `CryptoAnalysis` module. You can also apply multiple arguments, e.g. `mvn clean package -DrunAnalysisTests -DrunSootTests` runs the tests for the CryptoAnalysis and HeadlessJavaScanner (with Soot) modules.

---
*Based on the .NET Runtime contributing guidelines*
