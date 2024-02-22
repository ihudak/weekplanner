# WeekPlanner #

This is a sample project to demonstrate how to instrument Graal Native images with Dynatrace OneAgent

### How do I get set up? ###

#### Prerequisites for the build machine ####

* docker and docker-compose
* graalvm sdk v 23+
* node.js 18+
* angular 16+
* download the Gradle plugin zip and OneAgent zip files to the build machine
* environment variables are set:
  * DT_TENANT
  * DT_TENANTTOKEN
  * DT_CONNECTION_POINT

#### Build ####

* unzip Gradle plugin to libs directory
* refer the directory with the Plugin as a maven repo in settings.gradle
* configure Dynatrace OneAgent in build.gradle
* build the project with `./gradlew clean dynatraceNativeCompile` command
* start the Databases:
  * go to db directory
  * run `docker-compose up -d`
* configure the web app
  * `cd week-planner-web/scr/environments`
  * set host in the `baseSrvUrl` variable in the `environment.ts`
  * run `npm install` in `week-planner-web` directory

#### Execute ####

* execute categories app
  * `cd categories/build/native/nativeCompile/`
  * `./categories`
* execute tasks app
    * `cd tasks/build/native/nativeCompile/`
    * `./tasks`
* start the web app
  * `cd week-planner-web`
  * `ng serve --disable-host-check --host 0.0.0.0 -c production --port 4200`
* In your browser
  * open `<build-host-name>:4200` URL
  * create a task
  * check the data in your Dynatrace environment
