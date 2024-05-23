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
7. Warning! In case while using minikube command you'll get the following error:
   <pre>Unable to resolve the current Docker CLI context "default": context "default": context not found: open ...</pre>
    try to fix it with the command: `docker context use default`
8. Install Docker Desktop (as an engine for a cluster), distro you can easily find in the internet...
9. Check installed docker version:
   `docker version` or `docker status`
10. Start/init your new minikube cluster:
    `minikube start`
    and watch the output. It should say that cluster initialized and up (running)
11. Check your new shiny bright cluster:
    `minikube cluster-info` also check output of `minikube status`
12. With the command `docker ps` check running containers and find your **minikube** container (it should be running!)
13. Execute command `kubectl cluster-info` and check its output
14. You can get k8s dashboard, by executing `minikube dashboard`
15. Looks like we're done with environment!

## Basic and Advanced Actions

Here you can find some useful commands and working scenarios.

### Discover your cluster

The following commands are shown in the idea, that you've done with alias creating `alias k=kubectl`.

- `minikube ip` - get IP address of your cluster
- `k get nodes` - list of nodes
- `k get pods`  - list of pods in the default namespace
- `k get pods -A -o wide` - list of all pods in all namespaces (in a cluster) with wide representation on the screen (with additional info)
- `k get pods --namespace=<name space name>` - list of the pods in a namespace
- `k get namespaces` - list of namespaces
- ???

### Simple Actions #1

TBD

### Simple Actions #2

TBD
