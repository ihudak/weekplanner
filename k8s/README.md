# WeekPlanner #

Minimal setup (WorkItem with load generator)

### How do I get set up? ###

#### Kubernetes ####

In the k8s directory you will find yaml files to setup the weekplanner on kubernetes.

1. Configure pause between API calls:
   - Open `config.yaml`
   - set the `WORKITEM_TIMEOUT` (pause between the API calls, in seconds)

2. install the yaml in the following sequence:
    
   ```
   kubectl apply -f ./namespace.yaml
   kubectl apply -f ./config.yaml
   kubectl apply -f ./secret.yaml
   kubectl apply -f ./workitems.yaml
   kubectl apply -f ./databases.yaml
   kubectl apply -f ./categories.yaml
   kubectl apply -f ./users.yaml
   kubectl apply -f ./tasks.yaml
   kubectl apply -f ./web.yaml
   kubectl apply -f ./ingress.yaml
   
   ```

*Note:* `workitem` pod gets one primary and one sidecar containers (the sidecar calls the workitem API in endless loop)
