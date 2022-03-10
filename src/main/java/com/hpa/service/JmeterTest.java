package com.hpa.service;


import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;


public class JmeterTest {

    public static void main(String[] args) throws InterruptedException, IOException {

        //定义这一阶段的线程数int numThreads
        int numThreads = 60;
        runJmeter(numThreads);

        List<String[]> excels =  ExcelUtil.readExcel("D:\\java_code\\data\\aaa.xls");//读取当前的窗口内应该需要的压测数目

    }


    public static void runJmeter(int numThreads) {

        StandardJMeterEngine standardJMeterEngine = new StandardJMeterEngine();
        // 设置不适用gui的方式调用jmeter
        System.setProperty(JMeter.JMETER_NON_GUI, "true");
        // 设置jmeter.properties文件，我们将jmeter文件存放在resources中，通过classload
        String path = JmeterTest.class.getClassLoader().getResource("jmeter.properties").getPath();
        File jmeterPropertiesFile = new File(path);
        if (jmeterPropertiesFile.exists()) {
            JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
            HashTree testPlanTree = new HashTree();
            // 创建测试计划
            TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
            // 创建http请求收集器
            HTTPSamplerProxy examplecomSampler = createHTTPSamplerProxy();
            // 创建循环控制器
            LoopController loopController = createLoopController();
            // 创建线程组
            ThreadGroup threadGroup = createThreadGroup(numThreads);
            // 线程组设置循环控制
            threadGroup.setSamplerController(loopController);
            // 将测试计划添加到测试配置树种
            HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
            // 将http请求采样器添加到线程组下
            threadGroupHashTree.add(examplecomSampler);
            //增加结果收集
            Summariser summer = null;
            String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
            if (summariserName.length() > 0) {
                summer = new Summariser(summariserName);
            }


            String logFile = "test.jtl";
            ResultCollector logger = new ResultCollector(summer);

            logger.setFilename(logFile);
            testPlanTree.add(testPlanTree.getArray(), logger);

            // 配置jmeter
            standardJMeterEngine.configure(testPlanTree);
            // 运行
            standardJMeterEngine.run();


            System.out.println(summer.toString());

            // Thread.sleep(3000);

            //summer.testEnded();

            // summer.

        }
    }


    /**
     * 创建线程组
     *
     * @return
     */
    public static ThreadGroup createThreadGroup(int numThreads) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Example Thread Group");
        threadGroup.setNumThreads(numThreads); //输入线程数
        threadGroup.setRampUp(10);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setScheduler(true);
        threadGroup.setDuration(60);
        threadGroup.setDelay(0);
        return threadGroup;
    }


    /**
     * 创建循环控制器
     *
     * @return
     */
    public static LoopController createLoopController() {
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.setContinueForever(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.initialize();
        return loopController;
    }

    /**
     * 创建http采样器
     *
     * @return
     */
    public static HTTPSamplerProxy createHTTPSamplerProxy() {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setProperty("Content-Type", "multipart/form-data");
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setDomain("www.baidu.com");
        httpSamplerProxy.setPort(80);
        httpSamplerProxy.setPath("/");
        httpSamplerProxy.setMethod("GET");
        httpSamplerProxy.setConnectTimeout("5000");
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setHeaderManager(headerManager);
        return httpSamplerProxy;
    }



}
