# WeekPlanner #

This is a sample project to demonstrate how to instrument Graal Native images with Dynatrace OneAgent

### How do I get set up? ###

>The applications in the repo are interdependent and requre databases to work.   
>`workitems` application is an exception and can be deployed without any other apps and DBs.  
> It is recommended to use `workitems` app only if you want to give the Graal Agent a quick try  
>The applications have `load-test-curl.sh` script that makes API calls to the respective app. Please set the `server url` in the script according to your environment.

#### Prerequisites for the build machine ####

* docker and docker-compose
* graalvm sdk v 21+
* node.js 18+
* angular 17+
* environment variables are set:
  * DT_TENANT
  * DT_TENANTTOKEN
  * DT_CONNECTION_POINT

#### Build ####

* configure Dynatrace OneAgent in build.gradle
* Configure environment variables on your build machine
  * environmentUrl = <tenant-id>.live.dynatrace.com
    * *(please make sure you use ".live", not ".apps" for production tenants)*
    * *(please make sure you do not set ".apps" in the url for dev or sprint tenants, e.g. <tenant-id>.sprint.dynatracelabs.com instead of <tenant-id>. ~~apps.~~ sprint.dynatracelabs.com
  * apiToken = <doken with deployment scope>
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
* execute users app
  * `cd users/build/native/nativeCompile/`
  * `./users`
* execute workitems app
  * `cd workitems/build/native/nativeCompile/`
  * `./workitems`
* start the web app
  * `cd week-planner-web`
  * `ng serve --disable-host-check --host 0.0.0.0 -c production --port 4200`
* In your browser
  * open `<build-host-name>:4200` URL
  * create a task
  * check the data in your Dynatrace environment
