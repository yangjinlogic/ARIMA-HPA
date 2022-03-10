package com.hpa.service;

import com.hpa.config.HttpUtils;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import java.io.IOException;
import java.util.List;

public class HpaTest {


    public static void ArimaHpa() throws IOException, InterruptedException {

        //从文件读取期间的预测请求数目,根据行数进行循环，每分钟获取一次数据
        List<String[]> excels =  ExcelUtil.readExcel("D:\\java_code\\data\\data.xls");
        int status = 1;//平稳模式或者惊慌模式   1为平稳模式 2为惊慌模式
        int persistTime = 0;//惊慌模式下目标没有达到两倍并发数的持续时间，若持续时间大于5分钟即（5次循环，1~5），则回到平稳模式
        int targetRequest = 300; //定义设置的目标请求数目
        Double desiredPods;
        for (String[] strings: excels){
            long timestampStart = System.currentTimeMillis();
            Double requestNum = Double.valueOf(strings[1]); //预测的数值
            Double httpRequest = HttpUtils.getMetric(); //Prometheus获取期间请求数目
            Deployment deployment = KubernetesClientContainer.getPodReplicas();  //读取当前副本数,记录excel
            int currentPods = deployment.getStatus().getAvailableReplicas();//当前副本数
            desiredPods = Math.max(Math.ceil((requestNum/targetRequest)*currentPods),Math.ceil((httpRequest/targetRequest))*currentPods);
            if(httpRequest/targetRequest>=2){  //目标达到预设并发数两倍，以惊慌模式运行.，并重置ersistTime时间
                status = 2;
                persistTime= 1;
                desiredPods = Math.max(desiredPods,currentPods);//在惊慌模式下，副本数不能收缩，避免伸缩抖动。
            }else {   //没有达到并发两倍，根据当前运行模式决定
                if(status==1){ //当前为平稳模式
                    persistTime= 0;
                }else {
                    if (persistTime==5){ //已经过了惊慌模式，开始进入平稳模式
                        status=1;
                        persistTime= 0;
                    }else {
                        persistTime = persistTime +1;//没有达到并发两倍下的惊慌模式运行，时间窗口增加一分钟
                        desiredPods = Math.max(desiredPods,currentPods);//在惊慌模式下，副本数不能收缩，避免伸缩抖动。
                    }
                }
            }
            deployment.getSpec().setReplicas(desiredPods.intValue());
            KubernetesClientContainer.updateDeployment(deployment);
            long timestampEnd = System.currentTimeMillis();
            Thread.sleep(60000 - (timestampEnd - timestampStart));
        }

        //读取当前副本数,记录excel

    }


    public static void main(String[] args) {


    }


}
