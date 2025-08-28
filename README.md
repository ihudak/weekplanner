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
  * environmentUrl = <tenant-id>.live.dynatrace.com (for Dynatrace SaaS)
    * *(please make sure you use ".live", not ".apps" for production tenants)*
    * *(please make sure you do not set ".apps" in the url for dev or sprint tenants, e.g. <tenant-id>.sprint.dynatracelabs.com instead of <tenant-id>. ~~apps.~~ sprint.dynatracelabs.com
  * environmentUrl = <dynatrace-cluster-url>/e/<tenant-id> (for Dynatrace Managed)
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



#### Kubernetes ####

In the k8s directory you will find yaml files to setup the weekplanner on kubernetes.

1. install ingress controller
   1. Docker Desktop: Using Helm:

    ```helm upgrade --install ingress-nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace ingress-nginx --create-namespace```

   2. Docker Desktop: Using kubectl:

    ```kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml```   
    *Note:* Restart Docker Desktop after setting up ingress-nginx

   3. Azure Kubernetes Service (AKS)

    ```helm install ingress-nginx ingress-nginx/ingress-nginx --create-namespace --namespace ingress-nginx `
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-health-probe-request-path"=/healthz```

   4. Minikube

    ```minikube addons enable ingress```


2. install the yaml in the following sequence:
    
   ```
   kubectl apply -f ./namespace.yaml
   kubectl apply -f ./config.yaml
   kubectl apply -f ./secret.yaml
   
   kubectl apply -f ./databases.yaml
   # echo wait ~15 seconds to let the DBs start
   
   kubectl apply -f ./categories.yaml
   kubectl apply -f ./tasks.yaml
   kubectl apply -f ./users.yaml
   kubectl apply -f ./workitems.yaml
   kubectl apply -f ./rest-api-test.yaml
   
   kubectl apply -f ./web.yaml
   ### echo Web App needs ~3min to start
   
   kubectl apply -f ./ingress.yaml 
   ```

*Note 1:* web client is configured to the local docker desktop setup. Please change the web url in `config.yaml` to set it up elsewhere     
*Note 1:* web client is configured to the local docker desktop setup. Please change the web url in `config.yaml` to set it up elsewhere     
*Note 2:* ingress controller is configured to the local docker desktop setup. Please change the host field in `ingress.yaml` to set it up elsewhere



#### Test Execution #### 

1. Create some data
   1. Categories
      1. set the `server_url` variable to the host that you had specified in `ingress.yaml` in the `k8s/prepopulate_categories.sh` file 
      2. execute the `k8s/prepopulate_categories.sh` script to create categories
   2. Tasks
      1. Open the UI (`http://<host-from-ingress.yaml>/`)
      2. Create several tasks in the UI

2. Start the Data Generators
   1. set the `srv_url` variable to the host that you had specified in `ingress.yaml` in the `workitems/load-test-curl.sh` file
   2. execute the `workitems/load-test-curl.sh` script (it starts the endless loop calling the tasks and workitems services)

