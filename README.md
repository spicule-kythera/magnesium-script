# MagnesiumScript (Ms)

[![Code Quality](https://github.com/spicule-kythera/magnesium-script/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/spicule-kythera/magnesium-script/actions/workflows/codeql-analysis.yml)

A Domain-Specific-Language for creating expressive and simple automation scripts for Selenium-based web-agents.

# Language Usage and Documentation

For usage and descriptions on the language itself, see the [Github Wiki](https://github.com/spicule-kythera/magnesium-script/wiki).

# Development Quick start

### Run JAR as local executeable

`$` `java -jar target/magnesium-script-<version>-launcher.jar`

# Developing/Contributing

### Install dependencies

`$` `mvn clean install`

### Lint Code

`$` `mvn clean verify -Xlint:deprecation`

### Build Jar

`$` `mvn clean deploy`

***

# Development

### Expression Design Patterns

The basic elements of a MagnesiumScript expression are as follows:

1. All expressions contain a reference to an active Selenium driver session
2. Expressions may have child expressions, in which the child expression is resolved first before the parent
3. All expressions must have a `parse()` function which should either throws a syntax error upon incorrect parsing or return a reference to the instance of the expression itself
4. All expressions must have an `execute()` function which may return any `Object` which is also nullable (i.e., the function may choose to return nothing, analogous to a `void` return))
5. All expressions will have `parse()` invoked by the interpreter first, then `execute()` second, when the interpreter intends to resolve the expression at runtime
6. All expressions contain a context, where variable may be declared and referenced at runtime