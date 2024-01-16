# Bookstore App Starter for Vaadin Flow / Java EE

A project example for a Vaadin application that requires a Servlet 6 container to run. The UI is built mostly with Java only.

Vaadin 24 supports Servlet 6 and Jakarta EE 10. This demo app demonstrates many use cases with Java EE and CDI such as

- Dependency injection and inversion of control
- How to apply model view presenter architecture in Vaadin app using CDI
- EAR packaging

## Other useful Vaadin tips demoed

These tips are not specific to CDI or JavaEE

- How to build custom app layout when not using AppLayout component
- How to build CRUD view without using Crud component
- How to use Dialog as Offcanvas style modal view
- How to highlight changed fields in the Form
- How to use CustomI18NProvider with multiple supported languages
- How to persist chosen language in a cookie

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

### Development version as WAR

To compile the entire project, run "mvn install" in the parent project.

  - run `mvn clean wildfly:run -PrunWar` in ui module 
  - open http://localhost:8080/bookstore-starter-flow-ui-1.1-SNAPSHOT/

### Production version as EAR

To compile the entire project, run "mvn install" in the parent project.

Other basic workflow steps:

- getting started
- compiling the whole project
  - run `mvn clean install -Production` in parent project
- running in production mode
  - edit code in the ui module
  - run `mvn clean install -Pproduction` in ui project
  - run `mvn clean wildfly:run -Pproduction` in ear module 
  - open http://localhost:8080/bookstore-starter-flow-ui/
- creating a production mode war
  - run `mvn package -Pproduction` ear module
- running in production mode
  - production mode is used by default
   
### Branching information:
* `v24` the latest version of the starter, using the latest platform version
