package com.hpa.service;

import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



public class KubernetesClientContainer {

    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int REQUEST_TIMEOUT = 30 * 1000;
    private static final  String masterUrl = "https://172.16.2.106:6443";


    public static KubernetesClient getClient() {
        Config config = new ConfigBuilder().withMasterUrl(masterUrl).withTrustCerts(true).build();
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setRequestTimeout(REQUEST_TIMEOUT);
        return new DefaultKubernetesClient(config);
    }


    public static void main(String[] args) {

        String namespace = "test";
        String name = "metric-java";
        KubernetesClient kubernetesClient = getClient();
        Deployment deployment = kubernetesClient.apps().deployments().inNamespace(namespace).withName(name).get();
        DeploymentStatus deploymentStatus = deployment.getStatus();
        deploymentStatus.getReadyReplicas();
        System.out.println("已准备副本数" +  deploymentStatus.getReadyReplicas());
        System.out.println("可用副本数" +  deploymentStatus.getAvailableReplicas());
        System.out.println("副本数" +  deploymentStatus.getReplicas());
        System.out.println("不可用副本数" +  deploymentStatus.getUnavailableReplicas());
        System.out.println(deployment);
    }


    public static Deployment getPodReplicas(){
        String namespace = "test";
        String name = "metric-java";
        KubernetesClient kubernetesClient = getClient();
        return  kubernetesClient.apps().deployments().inNamespace(namespace).withName(name).get();

    }


    public static void updateDeployment(Deployment deployment){
        String namespace = "test";
        String name = "metric-java";
        KubernetesClient kubernetesClient = getClient();
        kubernetesClient.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
    }



}
