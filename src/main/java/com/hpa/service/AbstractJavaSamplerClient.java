//package com.hpa.service;
//
//public class AbstractJavaSamplerClient {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(EmoDb.class);
//    private Properties prop = new Properties();
//    public Arguments getDefaultParameters() {
//        Arguments defaultParameters = new Arguments();
//        return defaultParameters;
//    }
//    public void setupTest(JavaSamplerContext context) {
//        for (Iterator<String> it = context.getParameterNamesIterator(); it.hasNext();) {
//            String paramName = it.next();
//            prop.put(paramName, context.getParameter(paramName));
//        }
//    }
//    @Override
//    public SampleResult runTest(JavaSamplerContext arg0) {
//        SampleResult result = new SampleResult();
//        try {
//            URL url=new URL(" ");
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.connect();
//            result.sampleStart();
//            int code = connection.getResponseCode();
//            if (code==200)
//            {
//                System.out.println("OK");
//                result.sampleEnd();
//            }
//            else {
//                throw new Exception("status is not=200");
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(ExceptionUtils.getStackTrace(e));
//            result.sampleEnd();
//            result.setSuccessful(false);
//            result.setResponseMessage("Exception: " + e);
//            result.setSampleLabel(e.getMessage());
//            return result;
//        }
//        result.setSuccessful(true);
//        result.setResponseMessage("Successfully completed");
//        result.setResponseOK();
//        return result;
//    }
//
//
//
//}
