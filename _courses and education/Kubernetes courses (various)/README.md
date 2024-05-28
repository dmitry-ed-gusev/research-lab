# Full Kubernetes Course

As a source for this article, Ive used the following courses:

1. Author: Bogdan Stashchuk, Git repo: [git repo](https://github.com/bstashchuk/k8s)
   [course link](???)
2. ???
3. ???

For this course we  will use OS Windows 10/11 + gitbash terminal emulator (MinGW), but most of things and advice are applicable to the linux environments as well.

## Setup Software and Environment

1. Install kubectl: [tools installation](https://kubernetes.io/docs/tasks/tools/) as a separated tool (it would  be helpful for interacting with other/remote clusters). But in case you need only minikube, you can use the kubectl tool, provided alongside with minikube: use command `minikube kubectl ...` after installing minikube (see below)
2. Put your kubectl executable path into PATH env variable (make sure, your version is the first in PATH in case you have another kubectl instances - from docker or minikube)
3. Check installed kubectl version:
   `kubectl version` - check client/server versions
   `kubectl version --client` - check only kubectl client version
4. Hint: for convenience, it is recommended to create alias for the kubectl command:
   `alias k=kubectl` - put this alias to your profile file (in case of gitbash put it to *~/.bashrc*)
5. Install minikube: [tools installation](https://kubernetes.io/docs/tasks/tools/)
6. Check installed minikube version:
   `minikube version`
7. Hint: for convenience, you may create alial for the minikube command (as it was done for the kubectl command above):
   `alias m-minikube` - put this alias to your profile file (in case of gitbash put it to *~/.bashrc*)
8. Warning! In case while using minikube command you'll get the following error:
   <pre>Unable to resolve the current Docker CLI context "default": context "default": context not found: open ...</pre>
    try to fix it with the command: `docker context use default`
9.  Install Docker Desktop (as an engine for a cluster), distro you can easily find in the internet...
10. Check installed docker version:
   `docker version` or `docker status`
11. Start/init your new minikube cluster:
    `minikube start`
    and watch the output. It should say that cluster initialized and up (running)
12. Check your new shiny bright cluster:
    `minikube cluster-info` also check output of `minikube status`
13. With the command `docker ps` check running containers and find your **minikube** container (it should be running!)
14. Execute command `kubectl cluster-info` and check its output
15. You can get k8s dashboard, by executing `minikube dashboard`
16. Looks like we're done with environment!

## Basic and Advanced Actions

Here you can find some useful commands and working scenarios.

The following commands are shown in the idea, that you've done with aliases creating (as it was explained above (see hints)): `alias k=kubectl` and `alias m=minikube`

### Discover your cluster

- `m ip` - get IP address of your cluster
- `m ssh` -- ssh into the cluster
- `k get nodes` - list of nodes
- `k get pods`  - list of pods in the default namespace
- `k describe pod <name>` - get detailed info about pod
- `k get pods -A -o wide` - list of all pods in all namespaces (in a cluster) with wide representation on the screen (with additional info), option -A may be used separately
- `k get pods --namespace=<name space name>` - list of the pods in a namespace
- `k get namespaces` - list of namespaces

### Creating simple pod with nginx

- `k create <namespace|ns> <namespace_name>` - create your own namespace with the provided name
- `k run my-nginx-pod --image=nginx --namespace=dmgusev` - create one pod from the image nginx (from docker hub) in the namespace 'dmgusev', if you omit the --namespace key with the value, the pod will be created in the default namespace.
- `k describe pod my-nginx-pod` - show detailed info about pod with the specified name
- `k delete pod <name>` - deleting pod

### Connecting to the pod with nginx inside cluster

- `m ssh` - ssh into minikube cluster
- `curl <IP>` - we can do inside the cluster to the pod with nginx - it should return hello page from nginx server (pod IP address we can get by `k describe pod <name>`)
- `docker ps | grep nginx` - list of containers with nginx - here you can get container id or name
- `docker exec -it <container id|container name> sh` - before executing this cmd you have to get container ID or name (see previous step)
- `hostname` - inside the pod we will see the pod hostname - the same name what we see when we create pod or execute the command `k get pods`
- `hostname -I` - get the current machine (pod) IP address

### Creating and scaling deployment with nginx

Pod name in a deployment: <code>{deployment-name-specified}-{replica set id}-{pod id}</code>

- `k create <deploy|deployment> <name>` - creating deployment with the specified image
- `k create deploy my-nginx-deploy --image=nginx` - create real deployment with nginx
- `k describe deploy my-nginx-deploy` - get detailed info about deployment
- `k delete deploy <name>` - delete deployment with the specified name
- `k describe pod <pod-name-in-a-deployment>` - get detailed info about pod in a deployment

### Creating service

TBD

### Creating and updating application

TBD

### Using YAMLs for deployments

TBD

### Creating two deployments of related apps

TBD
