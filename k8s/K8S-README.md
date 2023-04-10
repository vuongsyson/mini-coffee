# JHipster-generated Kubernetes configuration

## Preparation

You will need to push your image to a registry. If you have not done so, use the following commands to tag and push the images:

```

$ docker image tag gateway 140397/gateway
$ docker push 140397/gateway
$ docker image tag orderservice 140397/orderservice
$ docker push 140397/orderservice
$ docker image tag shopservice 140397/shopservice
$ docker push 140397/shopservice

```
```shell
docker login --username vuonghaison2@gmail.com --password .gpajtdm9 \
&& ./mvnw clean \
&&./mvnw -ntp verify -DskipTests -Pprod,api-docs jib:dockerBuild \
&& docker image tag orderservice 140397/orderservice:lastest \
&& docker push 140397/orderservice:lastest \
&& kubectl -n coffee set image deployments/orderservice orderservice-app=140397/orderservice:lastest \
&& sleep 5 \
&& kubectl logs -f -n coffee -l app=orderservice
```
```shell
aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com \
&& ./mvnw clean \
&&./mvnw -ntp verify -DskipTests -Pprod,api-docs jib:dockerBuild \
&& docker image tag shopservice 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/shopservice \
&& docker push 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/shopservice \
&& kubectl -n coffee set image deployments/shopservice shopservice-app=099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/shopservice:latest \
&& sleep 5 \
&& kubectl logs -f -n coffee -l app=shopservice
```

```shell
aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com \
&& ./mvnw clean \
&& ./npmw run webapp:build:dev \
&&./mvnw -ntp verify -DskipTests -Pprod,api-docs jib:dockerBuild \
&& docker image tag gateway 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/gateway:pre-releases11252022-1 \
&& docker push 099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/gateway:pre-releases11252022-1 \
&& kubectl -n coffee set image deployments/gateway gateway-app=099919086460.dkr.ecr.ap-southeast-1.amazonaws.com/gateway:pre-releases11252022-1 \
&& sleep 5 \
&& kubectl logs -f -n coffee -l app=gateway
```


## Deployment

You can deploy all your apps by running the below bash command:

```
./kubectl-apply.sh -f (default option)  [or] ./kubectl-apply.sh -k (kustomize option) [or] ./kubectl-apply.sh -s (skaffold run)
```

If you want to apply kustomize manifest directly using kubectl, then run

```
kubectl apply -k ./
```

If you want to deploy using skaffold binary, then run

```
skaffold run [or] skaffold deploy
```

## Exploring your services

Use these commands to find your application's IP addresses:

```
$ kubectl get svc gateway -n coffee
```

## Scaling your deployments

You can scale your apps using

```
$ kubectl scale deployment <app-name> --replicas <replica-count> -n coffee
```

## zero-downtime deployments

The default way to update a running app in kubernetes, is to deploy a new image tag to your docker registry and then deploy it using

```
$ kubectl set image deployment/<app-name>-app <app-name>=<new-image>  -n coffee
```

Using livenessProbes and readinessProbe allow you to tell Kubernetes about the state of your applications, in order to ensure availablity of your services. You will need minimum 2 replicas for every application deployment if you want to have zero-downtime deployed.
This is because the rolling upgrade strategy first stops a running replica in order to place a new. Running only one replica, will cause a short downtime during upgrades.

## Monitoring tools

### Prometheus metrics

Generator is also packaged with [Prometheus operator by CoreOS](https://github.com/coreos/prometheus-operator).

**hint**: use must build your apps with `prometheus` profile active!

Application metrics can be explored in Prometheus through,

```
$ kubectl get svc jhipster-prometheus -n coffee
```

Also the visualisation can be explored in Grafana which is pre-configured with a dashboard view. You can find the service details by

```
$ kubectl get svc jhipster-grafana -n coffee
```

- If you have chosen _Ingress_, then you should be able to access Grafana using the given ingress domain.
- If you have chosen _NodePort_, then point your browser to an IP of any of your nodes and use the node port described in the output.
- If you have chosen _LoadBalancer_, then use the IaaS provided load balancer IP

## JHipster registry

The registry is deployed using a headless service in kubernetes, so the primary service has no IP address, and cannot get a node port. You can create a secondary service for any type, using:

```
$ kubectl expose service jhipster-registry --type=NodePort --name=exposed-registry -n coffee
```

and explore the details using

```
$ kubectl get svc exposed-registry -n coffee
```

For scaling the JHipster registry, use

```
$ kubectl scale statefulset jhipster-registry --replicas 3 -n coffee
```

## Troubleshooting

> my apps doesn't get pulled, because of 'imagePullBackof'

Check the docker registry your Kubernetes cluster is accessing. If you are using a private registry, you should add it to your namespace by `kubectl create secret docker-registry` (check the [docs](https://kubernetes.io/docs/tasks/configure-pod-container/pull-image-private-registry/) for more info)

> my applications are stopped, before they can boot up

This can occur if your cluster has low resource (e.g. Minikube). Increase the `initialDelaySeconds` value of livenessProbe of your deployments

> my applications are starting very slow, despite I have a cluster with many resources

The default setting are optimized for middle-scale clusters. You are free to increase the JAVA_OPTS environment variable, and resource requests and limits to improve the performance. Be careful!
