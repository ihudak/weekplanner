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

#### Kubernetes ####

In the k8s directory you will find yaml files to setup the weekplanner on kubernetes.

1. install ingress controller
   1. Docker Desktop: Using Helm:

    ```helm upgrade --install ingress-nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace ingress-nginx --create-namespace```

   2. Docker Desktop: Using kubectl:

    ```kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml```   
    *Note:* Restart Docker Desktop after setting up ingress-nginx

   3. Azure Kubernetes Service (AKS)

    ```helm install ingress-nginx ingress-nginx/ingress-nginx --create-namespace --namespace ingess-nginx `
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-health-probe-request-path"=/healthz```

   4. Minikube

    ```minikube addons enable ingress```


2. install the yaml in the following sequence:
    
   ```
   kubectl apply -f ./namespace.yaml
   kubectl apply -f ./databases.yaml
   kubectl apply -f ./config.yaml
   kubectl apply -f ./secret.yaml
   kubectl apply -f ./categories.yaml
   kubectl apply -f ./tasks.yaml
   kubectl apply -f ./users.yaml
   kubectl apply -f ./web.yaml
   kubectl apply -f ./ingress.yaml          
   ```

*Note 1:* web client is configured to the local docker desktop setup. Please change the web url in `config.yaml` to set it up elsewhere     
*Note 2:* ingress controller is configured to the local docker desktop setup. Please change the host field in `ingress.yaml` to set it up elsewhere
