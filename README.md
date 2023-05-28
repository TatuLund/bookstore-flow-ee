# Bookstore App Starter for Vaadin Flow / Java EE

A project example for a Vaadin application that requires a Servlet 6 container to run. The UI is built mostly with Java only.

Vaadin 24 supports Servlet 6 and Jakarta EE 10. This demo app demonstrates many use cases with Java EE and CDI such as

- Dependency injection and inversion of control
- How to apply model view presenter architecture in Vaadin app using CDI
- EAR packaging

## Other useful Vaadin tips demoed

- How to build custom app layout when not using AppLayout component
- How to build CRUD view without using Crud component
- How to use Dialog as Offcanvas style modal view

## Prerequisites

The project can be imported into the IDE of your choice, with Java 17 installed, as a Maven project.

## Project Structure

The project consists of the following three modules:

- parent project: common metadata and configuration
- bookstore-starter-flow-ui: main application module that includes views (war)
- bookstore-starter-flow-my-component: sub module for custom components (jar)
- bookstore-starter-flow-backend: POJO classes and mock services being used in the ui (jar)
- bookstore-starter-flow-it: TestBench test examples (ToDo: update to work)
- bookstore-starter-flow-ear: EAR packaging

## Workflow

To compile the entire project, run "mvn install" in the parent project.

Other basic workflow steps:

- getting started
- compiling the whole project
  - run `mvn clean install` in parent project
- developing the application
  - edit code in the ui module
  - run `mvn clean install` in ui project
  - run `mvn clean wildfly:run` in ear module 
  - open http://localhost:8080/bookstore-starter-flow-ui/
- creating a production mode war
  - run `mvn package` ear module
- running in production mode
  - production mode is used by default
   
### Branching information:
* `v24` the latest version of the starter, using the latest platform version
