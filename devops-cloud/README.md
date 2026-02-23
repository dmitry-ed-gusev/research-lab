# K8s and Cloud Laboratory Project

Kubernetes and Cloud Learning Laboratory Project.
Dmitrii Gusev (C), 2024

[TOC]

## Tech Docs and Resources

Here are some useful tech resources and documentation for this Lab Project:

- [Kuber. Part I](https://habr.com/ru/companies/ruvds/articles/438982/)
- [Kuber. Part II](https://habr.com/ru/companies/ruvds/articles/438984/)
- [Repo for the articles above](https://github.com/rinormaloku/k8s-mastery)
- [Repo for 'Bootstrapping Microservices' book](https://github.com/bootstrapping-microservices-2nd-edition)
- [Bogdan Stashchuk git repo for k8s course](https://github.com/bstashchuk/k8s)

## Setup Local Software and Dev Environment

### Setup Local K8s

This installation was done in Windows 10+, but it should work in WSL or in any Linux environments.

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
9. Install Docker Desktop (as an engine for a cluster), distro you can easily find in the internet...
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

## Tech :: Docker / Podman

In all commands docker can be replaced with podman command.

### Working with Images

- `docker build -t <your-name-for-the-image> --file <path-to-your-Dockerfile> <path-to-project>` - building docker image from the specified docker file (-t - tag for the image, --file - docker file, path to project - usually dot . is used - specifying the current directory)
- `docker images` / `docker image list` - show list of images (local)
- `docker rmi <image name or tag>` / `docker image rm <image name or tag>` - removing image, with flag --force - this command can delete even image that is in use
- `docker tag <image-id> <target_name>` - tag image by ID with the specified tag
- `docker tag <existing-image-id> <registry-url>/<image-name>:<version>` - re-tag the existing local image by ID with the tag in order to upload it  to an external registry
- `docker login <your-registry-url> --username <your-username> --password <your-password>` - login into external registry
- `docker push <registry-url>/<image-name>:<version>` - upload image to the external registry

### Working with Containers

- `docker ps -a` / `docker container list -a` - show all containers (running and stopped), without flag -a this command shows only running containers, -a is a shorthand for --all option
- `docker container prune` - remove unused containers
- `docker rm <container-id> --force` / `docker container rm <container-id> --force` - forcibly removes container by provided ID, without flag --force - if container is referenced somehow, it won't be deleted
- `docker run -d -p <host-port>:<container-port> -e <name>=<value> <image-name>` - run container from a local image (-d - detached mode, run in background, -p - expose ports(s) from container to host, -e - pass environment variable into container execution environment, image name or ID for execution)
- `docker run -d -p <host-port>:<container-port> -e <name>=<value> <registry-url>/<image-name>:<version>` - run container from a remote repository image, options are as above (-d - detached, -p - port publishing, -e - env variables, image name with the registry (repository) address)
- `docker logs <container-id>` - show logs of the container
- `docker exec -it <container-id> bash` - shell into the container in order to check "what's going on inside?", this command allows you to shell even in the storred container (if it appears in the list returned by command `docker ps -a`)
- `docker stop <container-id>` - stopping the container by provided ID

### Other Useful Commands

- `docker stop $(docker ps -a -q)` - stop all executed containers
- `docker rm $(docker ps -a -q)` - remove all containers
- `docker system prune --volumes --all` - removes all cached images on the local PC + removes all attached volumes

## Tech :: docker-compose / podman-compose

1. `` - 
2. `` - 
3. `` - 

## Tech :: K8s

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

Pod name structure in a deployment: **{deployment-name-specified}-{replica set id}-{pod id}**.

- `k create <deploy|deployment> <name>` - creating deployment with the specified image
- `k create deploy my-nginx-deploy --image=nginx` - create real deployment with nginx
- `k describe deploy my-nginx-deploy` - get detailed info about deployment, including size in pods (sizing of the replica set)
- `k delete deploy <name>` - delete deployment with the specified name
- `k describe pod <pod-name-in-a-deployment>` - get detailed info about pod in a deployment
- `k scale deploy <name> --replicas=<NUMBER>` - scale deployment to the specified number of pods. for example to scale our deployment to 3 replicas, we can use: `k scale deploy my-nginx-deploy --replicas=3`

### Creating and managing service

TBD

### Creating and updating application

TBD

### Using YAMLs for deployments

TBD

### Creating two deployments of related apps

TBD

## Tech :: JavaScript / Node.js

This application requires the following versions of frameworks:

- node v18.17.1+
- npm v9.6.7+

Below there are some useful NPM / Node js commands.

### Installing Dependencies

- `npm init -y` - init project in the current dir
- `npm install --save <package>` - install runtime (prod) latest version of dependency (you can skip --save)
- `npm install <package>` / `npm i <package>` - same as above (short useful version)
- `npm install --save express@5.0.0-beta.3` - same as above, but specific version of the dependency
- `npm install` - install all (prod+dev) dependencies
- `npm install --omit=dev` - install all production (non-development) dependencies
- `npm install --save-dev nodemon` - install development dependency (in this example - nodemon - monitor for live reloading Node.js application on code change)

### NPM Useful Commands

- `npm audit` - check for vulnerabilities
- `npm audit fix` - check for vulnerabilities and fix, if possible

### Running The Application

- `node <path_to_the_main_js_file>` - run this from the module/service directory in order to simply start it (rough start)
- `npm start` - run prod version (Node.js convention) - see setup in **package.json** file
- `npm run start:dev` - run dev version (Node.js convention) - see setup in **package.json** file
