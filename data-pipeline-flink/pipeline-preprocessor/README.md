# Deduplication Flink

This job is used to de-duplicate data by running the job using Flink. It can be run either on Yarn or Kubernetes.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a yarn or kubernetes.

### Prerequisites

1. Download flink-1.10.0-scala_2.12 from [apache-flink-downloads](https://www.apache.org/dyn/closer.lua/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.12.tgz).
2. Download [hadoop dependencies](https://repo.maven.apache.org/maven2/org/apache/flink/flink-shaded-hadoop-2-uber/2.8.3-10.0/flink-shaded-hadoop-2-uber-2.8.3-10.0.jar) (only for running on Yarn). Copy the hadoop dependency jar under lib folder of the flink download.
3. export HADOOP_CLASSPATH=`<hadoop-executable-dir>/hadoop classpath` either in .bashrc or current execution shell.
4. Docker installed.
5. A running yarn cluster or a kubernetes cluster.

### Build

mvn clean install

## Deployment

### Yarn

Flink requires memory to be allocated for both job-manager and task manager. -yjm parameter assigns job-manager memory and -ytm assigns task-manager memory.

```
./bin/flink run -m yarn-cluster -p 2 -yjm 1024m -ytm 1024m <data-pipeline-src-dir>/de-duplication-flink/target/de-duplication-flink-0.0.1.jar
```

### Kubernetes

```
# Create a single node cluster
k3d create --server-arg --no-deploy --server-arg traefik --name flink-cluster --image rancher/k3s:v1.0.0
# Export the single node cluster into KUBECONFIG in the current shell or in ~/.bashrc.
export KUBECONFIG="$(k3d get-kubeconfig --name='flink-cluster')"

# Only for Mac OSX
# /usr/local/bin/kubectl -> /Applications/Docker.app/Contents/Resources/bin/kubectl
rm /usr/local/bin/kubectl
brew link --overwrite kubernetes-cli

# Create a configmap using the flink-configuration-configmap.yaml
kubectl create -f sunbird-data-pipeline/kubernetes/flink-configuration-configmap.yaml

# Create pods for jobmanager-service, job-manager and task-manager using the yaml files
kubectl create -f sunbird-data-pipeline/kubernetes/jobmanager-service.yaml
kubectl create -f sunbird-data-pipeline/kubernetes/jobmanager-deployment.yaml
kubectl create -f sunbird-data-pipeline/kubernetes/taskmanager-deployment.yaml

# Create a port-forwarding for accessing the job-manager UI on localhost:8081
kubectl port-forward deployment/flink-jobmanager 8081:8081

# Submit the de-duplication job to the Kubernetes single node cluster flink-cluster
./bin/flink run -m localhost:8081 <data-pipeline-src-dir>/de-duplication-flink/target/de-duplication-flink-0.0.1.jar

# Commands to delete the pods created in the cluster
kubectl delete deployment/flink-jobmanager
kubectl delete deployment/flink-taskmanager
kubectl delete service/flink-jobmanager
kubectl delete configmaps/flink-config

# Command to stop the single-node cluster
k3d stop --name="flink-cluster"
```
