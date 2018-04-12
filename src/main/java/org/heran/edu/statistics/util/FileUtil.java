package org.heran.edu.statistics.util;

import com.alibaba.fastjson.JSON;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.app.core.util.UUIDUtil;
import org.heran.edu.statistics.domain.Rule;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: Mr.zheng
 * Date: 2017/8/1
 * Time: 17:53
 */
public class FileUtil {

    static String packageName = "org.drools.examples.citic;";

    static List<String> importPackages = Arrays.asList(
            "import org.citic.iiot.diagnosis.drools.citic.Device;"
            ,"import org.slf4j.Logger;"
            ,"import org.slf4j.LoggerFactory;"
            ,"import java.util.HashMap;"
            ,"import org.citic.iiot.diagnosis.drools.RuleThread;"
            ,"import Faultrecord;"
            ,"import java.util.Map;"
            ,"import java.util.HashMap;"
            ,"import java.util.List;");

    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    // accessKey请登录https://ak-console.aliyun.com/#/查看
    private static String accessKeyId = "LTAIntdn9LU1Juyb";
    private static String accessKeySecret = "CaluWmRSuTuWGOlT5Vo98kDoc4xDJQ";
    private static String bucketName = "iiot-pic";

    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setRulename("str");
        rule.setType("2");
        String test = "[{\"opt\":\">\",\"deviceid\":\"3a4b5c\",\"sensor\":\"L0001\",\"value\":\"50\"},{\"relation\":\"&&\",\"name\":\"1111\",\"attr\":[{\"opt\":\">\",\"deviceid\":\"3a4b5c\",\"sensor\":\"L0001\",\"value\":\"50\"},{\"relation\":\"||\",\"opt\":\">\",\"deviceid\":\"3a4b5c\",\"sensor\":\"L0001\",\"value\":\"50\"}]},{\"relation\":\"||\",\"opt\":\">\",\"deviceid\":\"3a4b5c\",\"sensor\":\"L0001\",\"value\":\"50\"}]";
        List<Map> list = (List<Map>)JSON.parse(test);
//        Map map = new HashMap();
//        map.put("type","2");
//        map.put("opt",">");
//        map.put("value","20");
//        map.put("rulename","测试");
//        map.put("deviceid","1234567");
//        map.put("detail","这是一条测试数据");
        String classpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        int end = classpath.indexOf("target");
        classpath = classpath.substring(1,end).replace("/","\\");
//        String drlString = Generate(rule,list);
//        makeFile(drlString,rule,classpath);



//        //生成追加规则字符串
//        String drlAppendBodyString = FileUtil.makeFileBody(rule,list);
//        //追加文件内容
//        FileUtil.appendFile(rule,classpath,drlAppendBodyString);

        //获取文件内容
//        Map<String, Object> map = FileUtil.readFileByLines(rule,classpath);
//        if(CodeUtil.isNotNull(map)){
//            //生成追加规则字符串
//            String drlAppendBodyString = FileUtil.makeFileBody(rule,list);
//
//            int ruleStartLine = (Integer) map.get("ruleStartLine");
//            int ruleEndLine = (Integer) map.get("ruleEndLine");
//            List drlList = (List<String>) map.get("drlList");
//            StringBuffer sb = new StringBuffer();
//            for(int i=1;i<ruleStartLine;i++){
//                sb.append(drlList.get(i-1));
//                if(i != ruleStartLine-1){
//                    sb.append("\n");
//                }
//            }
//            sb.append(drlAppendBodyString);
//            for(int i=ruleEndLine;i<drlList.size();i++){
//                if(i==ruleEndLine){
//                    sb.append("\n");
//                }
//                sb.append(drlList.get(i));
//                if(i != drlList.size()-1){
//                    sb.append("\n");
//                }
//            }
//            //生成并保存drl文件
//            FileUtil.makeFile(sb.toString(),rule,classpath,null);
//        }

    }

//    public static void makeFile(String drlString,Rule rule,String path,String drlname){
//
//        byte[] sourceByte = drlString.getBytes();
//        if(null != sourceByte){
//            try {
//                    File file = new File(path+"src\\main\\resources\\org\\citic\\iiot\\monitor\\drools\\"+drlname+".drl");     //文件路径（路径+文件名）
//                if (!file.exists()) {   //文件不存在则创建文件，先创建目录
//                    File dir = new File(file.getParent());
//                    dir.mkdirs();
//                    file.createNewFile();
//                }
//                FileOutputStream outStream = new FileOutputStream(file);    //文件输出流用于将数据写入文件
//                outStream.write(sourceByte);
//                outStream.close();  //关闭文件输出流
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    /**
     * 生成文件头部
     * @return
     */
    public static String makeFileHead(){
        StringBuilder sb = new StringBuilder();
        //规则头部
        sb.append("package ").append(packageName).append("\n");

        importPackages.stream().forEach(t -> sb.append(t).append("\n"));

        sb.append("dialect  \"mvel\"").append("\n");
        sb.append("global java.util.List list");
        return sb.toString();
    }

    /**
     * 生成文件尾部
     * @return

    public static String makeFileEnd(Rule rule){
        StringBuilder sb = new StringBuilder();
        //规则名称
        sb.append("\n").append("rule  \"").append("right\"").append(" salience ").append( 0).append("\n");
        sb.append("no-loop true\n");
        sb.append("activation-group ").append("\""+rule.getMonitorPointid()+"\" \n");
        //规则条件
        sb.append("when").append("\n");
//        sb.append("eval(true)").append("\n");
        sb.append("$map: Map( $msg: (1==1)==true )").append("\n");
        sb.append("then").append("\n");
        sb.append("Logger logger = LoggerFactory.getLogger(RuleThread.class);\n");
        sb.append("logger.info(\"point is ok\");").append("\n");
        sb.append("SaveFaultrecordUtil.updateFaultrecordCache(");
        sb.append("$map.get(\"deviceid\")+\"\"");
        sb.append(",");
        sb.append("$map.get(\"sensors\")");
        sb.append(",");
        sb.append("\""+0+"\"");
        sb.append(",");
        sb.append("\""+rule.getMonitorPointid()+"\"");
        sb.append(");");
        sb.append("\n").append("end");
        return sb.toString();
    }
     */

    /**
     * 生成文件主体
     * @return
     */
    public static String makeFileBody(Rule rule, List<Map> list){
        StringBuilder sb = new StringBuilder();
        //规则名称
        sb.append("\n").append("rule  \"").append(rule.getRulename()+"\"").append(" salience ").append(rule.getSalience()).append("\n");
        sb.append("no-loop true  \n");
        sb.append("activation-group ").append("\""+rule.getMonitorPointid()+"\" \n");
        //规则条件
        sb.append("when").append("\n");

        sb.append(" $map").append(": Map( $msg:");

//                .append("this['").append("measure").append("']").append(map.get("opt")).append(map.get("value"))
        for (Map map : list) {
            Object deviceid = map.get("deviceid");
            Object sensor = map.get("sensor");
            Object relation = map.get("relation");
            Object name = map.get("name");
            if(CodeUtil.isNull(name)){
                if(CodeUtil.isNotNull(relation)){
                    sb.append(relation);
                }
                sb.append(" (");
                if(CodeUtil.isNotNull(deviceid)){
                    sb.append("this['deviceid'] == ").append("'").append(deviceid).append("'").append(" && ");
                }
                if(CodeUtil.isNotNull(sensor)){
                    sb.append("this['sensor'] == ").append("'").append(sensor).append("'").append(" && ");
                }
                sb.append("this['").append("measure").append("']").append(map.get("opt")).append(map.get("value"));
                sb.append(" ) ");
            }else{
                List<Map> attr = (List<Map>)map.get("attr");
                if(CodeUtil.isNotNull(relation)){
                    sb.append(relation);
                }
                sb.append(" (");
                for (Map m : attr) {
                    Object dev = m.get("deviceid");
                    Object sen = m.get("sensor");
                    Object rel = m.get("relation");
                    if(CodeUtil.isNotNull(rel)){
                        sb.append(rel);
                    }
                    sb.append(" (");
                    if(CodeUtil.isNotNull(dev)){
                        sb.append("this['deviceid'] == ").append("'").append(dev).append("'").append(" && ");
                    }
                    if(CodeUtil.isNotNull(sen)){
                        sb.append("this['sensor'] == ").append("'").append(sen).append("'").append(" && ");
                    }
                    sb.append("this['").append("measure").append("']").append(m.get("opt")).append(m.get("value"));
                    sb.append(" ) ");
                }
                sb.append(" )");
            }
        }

        sb.append("==true )\n");

        //规则执行动作
        sb.append("then \n");

        sb.append("Logger logger = LoggerFactory.getLogger(RuleThread.class);\n");
        sb.append("logger.debug(\"map 过滤\"+$msg+ $map.get(\"deviceid\"));\n");
        sb.append("SaveFaultrecordUtil.saveFaultrecord(").append("\""+rule.getRulename()+"\"");
        sb.append(",");
        sb.append("\""+rule.getDisp()+"\"");
        sb.append(",");
        sb.append("$map.get(\"deviceid\")+\"\"");
        sb.append(",");
        sb.append("\""+rule.getType()+"\"");
        sb.append(",");
        sb.append("\""+rule.getCode()+"\"");
        sb.append(",");
        sb.append("\""+rule.getMonitorPointid()+"\"");
        sb.append(",");
        sb.append(rule.getSalience());
        sb.append(");");
        sb.append("\n").append("end");
        return sb.toString();
    }

    /**
     * 生成文件主体
     * @return

    public static String makeFileBody(Rule rule){
        StringBuilder sb = new StringBuilder();
        //规则名称
        sb.append("\n").append("rule  \"").append(rule.getRulename()+"\"").append(" salience ").append(rule.getSalience()).append("\n");
        sb.append("no-loop true  \n");
        sb.append("activation-group ").append("\""+rule.getMonitorPointid()+"\" \n");
        //规则条件
        sb.append("when").append("\n");

        sb.append(" $map").append(": Map( $msg:(");
        sb.append(rule.getRuleConfig()).append(")");
        sb.append("==true )\n");

        //规则执行动作
        sb.append("then \n");

        sb.append("Logger logger = LoggerFactory.getLogger(RuleThread.class);\n");
        sb.append("logger.info(\"map 过滤\"+$msg+ $map.get(\"deviceid\"));\n");
        sb.append("SaveFaultrecordUtil.saveFaultrecord(").append("\""+rule.getRulename()+"\"");
        sb.append(",");
        sb.append("\""+rule.getDisp()+"\"");
        sb.append(",");
        sb.append("$map.get(\"deviceid\")+\"\"");
        sb.append(",");
        sb.append("$map.get(\"sensors\")");
        sb.append(",");
        sb.append("\""+rule.getType()+"\"");
        sb.append(",");
        sb.append("\""+rule.getCode()+"\"");
        sb.append(",");
        sb.append("\""+rule.getMonitorPointid()+"\"");
        sb.append(",");
        sb.append(rule.getSalience());
        sb.append(",");
        sb.append("\""+rule.getUserid()+"\"");
        sb.append(",");
        sb.append("\""+rule.getOrgcode()+"\"");
        sb.append(",");
        sb.append("\""+rule.getRuleConfig().replace("\"","")+"\"");
        sb.append(");");
        sb.append("\n").append("end");
        return sb.toString();
    }
     */

    /**
     * 生成文件主体
     * @return
     */
    public static String makeFileBody(Rule rule){
        StringBuilder sb = new StringBuilder();
        //规则名称
        sb.append("\n").append("rule  \"").append(UUIDUtil.creatUUID()+"\"").append(" salience ").append(rule.getSalience()).append("\n");
        //规则条件
        sb.append("when").append("\n");

        sb.append(" $map").append(": Map( $msg:(");
        sb.append(rule.getRuleConfig()).append(")");
        sb.append("==true )\n");

        //规则执行动作
        sb.append("then \n");

        sb.append("Logger logger = LoggerFactory.getLogger(RuleThread.class);\n");
        sb.append("logger.debug(\"map 过滤\"+$msg+ $map.get(\"deviceid\"));\n");
        sb.append("Faultrecord faultrecord = new Faultrecord();").append("\n");
        sb.append("faultrecord.setDeviceid($map.get(\"deviceid\")+\"\");").append("\n");
        sb.append("faultrecord.setType(\""+rule.getType()+"\");").append("\n");
        sb.append("faultrecord.setName(\""+rule.getRulename()+"\");").append("\n");
        sb.append("faultrecord.setDetail(\""+rule.getDisp()+"\");").append("\n");
        sb.append("faultrecord.setCode(\""+rule.getCode()+"\");").append("\n");
        sb.append("faultrecord.setSalience("+rule.getSalience()+");").append("\n");
        sb.append("faultrecord.setUserid(\""+rule.getUserid()+"\");").append("\n");
        sb.append("faultrecord.setOrgCode(\""+rule.getOrgcode()+"\");").append("\n");
        sb.append("faultrecord.setMonitorPointId(\""+rule.getMonitorPointid()+"\");").append("\n");
        sb.append("faultrecord.setRuleConfigContent(\""+rule.getRuleConfig().replace("\"","")+"\");").append("\n");
        sb.append("list.add(faultrecord);");
        sb.append("\n").append("end");
        return sb.toString();
    }

    /**
     * 生成文件尾部
     * @return

    public static String makeFileEnd(Rule rule){
        StringBuilder sb = new StringBuilder();
        //规则名称
        sb.append("\n").append("rule  \"").append("right\"").append(" salience ").append( 0).append("\n");
        sb.append("no-loop true\n");
        sb.append("activation-group ").append("\""+rule.getMonitorPointid()+"\" \n");
        //规则条件
        sb.append("when").append("\n");
        //        sb.append("eval(true)").append("\n");
        sb.append("$map: Map( $msg: (1==1)==true )").append("\n");
        sb.append("then").append("\n");
        sb.append("Logger logger = LoggerFactory.getLogger(RuleThread.class);\n");
        sb.append("logger.info(\"point is ok\");").append("\n");

        sb.append("Faultrecord faultrecord = new Faultrecord();").append("\n");
        sb.append("faultrecord.setDeviceid($map.get(\"deviceid\")+\"\");").append("\n");
        sb.append("faultrecord.setType(\""+0+"\");").append("\n");
        sb.append("faultrecord.setMonitorPointId(\""+rule.getMonitorPointid()+"\");").append("\n");
        sb.append("list.add(faultrecord);");

        sb.append("\n").append("end");
        return sb.toString();
    }
    */


    /**
     * 生成完整规则文件
     * @param rule
     * @param list
     * @return
     */
    public static String Generate(Rule rule,List<Map> list) {
        String strHead = makeFileHead();
        StringBuffer sb = new StringBuffer(strHead);
        String strBody = makeFileBody(rule,list);
        sb.append(strBody);
        return sb.toString();
    }

    /**
     * 追加文件：使用FileWriter
     */
//    public static void appendFile(Rule rule, String fileName, String content) {
//        try {
//            fileName = fileName+"src\\main\\resources\\org\\citic\\iiot\\monitor\\drools\\"+rule.getDrlname()+"\\"+rule.getDrlname()+".drl";
//            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
//            FileWriter writer = new FileWriter(fileName, true);
//            writer.write(content);
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
//    public static Map<String,Object> readFileByLines(Rule rule, String fileName) {
//        fileName = fileName + "src\\main\\resources\\org\\citic\\iiot\\monitor\\drools\\" + rule.getDrlname() + "\\" + rule.getDrlname() + ".drl";
//        File file = new File(fileName);
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            List<String> drlList = new LinkedList<String>();
//            Map<String, Object> map = new HashMap<String, Object>();
//            boolean hasRule = false;
//            String tempString = null;
//            int ruleStartLine = 0;
//            int ruleEndLine = 0;
//            int line = 1;
//            // 一次读入一行，直到读入null为文件结束
//            while ((tempString = reader.readLine()) != null) {
//                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
//                drlList.add(tempString);
//                if (tempString.contains(rule.getRulename() + "_" + rule.getType())) {
//                    hasRule = true;
//                    ruleStartLine = line;
//                }
//                if (tempString.contains("end") && hasRule) {
//                    hasRule = false;
//                    ruleEndLine = line;
//                }
//                line++;
//            }
//            map.put("ruleStartLine", ruleStartLine);
//            map.put("ruleEndLine", ruleEndLine);
//            map.put("drlList", drlList);
//            reader.close();
//            return map;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

    /**
     * 删除文件夹
     * @param folderPath
     */
//    public static void delFolder(String folderPath) {
//        try {
//            delAllFile(folderPath); //删除完里面所有内容
//            String filePath = folderPath;
//            filePath = filePath.toString();
//            java.io.File myFilePath = new java.io.File(filePath);
//            myFilePath.delete(); //删除空文件夹
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 删除指定文件夹下的所有文件
     * @param path
     * @return
     */
//    public static boolean delAllFile(String path) {
//        boolean flag = false;
//        File file = new File(path);
//        if (!file.exists()) {
//            return flag;
//        }
//        if (!file.isDirectory()) {
//            return flag;
//        }
//        String[] tempList = file.list();
//        File temp = null;
//        for (int i = 0; i < tempList.length; i++) {
//            if (path.endsWith(File.separator)) {
//                temp = new File(path + tempList[i]);
//            } else {
//                temp = new File(path + File.separator + tempList[i]);
//            }
//            if (temp.isFile()) {
//                temp.delete();
//            }
//            if (temp.isDirectory()) {
//                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
//                delFolder(path + "/" + tempList[i]);//再删除空文件夹
//                flag = true;
//            }
//        }
//        return flag;
//    }

//    /**
//     * 上传文件到阿里云服务器
//     * @param content
//     * @param ossFilePath
//     * @param fileName
//     * @return
//     */
//    public static Result<String> OSSUploadUtil(String content,String ossFilePath,String fileName){
//        Result<String> result = new Result<String>(ResultCode.SUCCESS,"上传成功",null);
//        ossFilePath = ossFilePath ==null?"":ossFilePath;
//        //创建OSSClient实例
//        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
//        try {
//            ossClient.putObject(bucketName, ossFilePath+"/"+fileName, new ByteArrayInputStream(content.getBytes()));
//            Date expiration = new Date(new Date().getTime() + 3600 * 1000 * 24 * 365 * 4);//设置URL过期时间为4年  3600 * 1000 * 24
//            URL url = ossClient.generatePresignedUrl(bucketName, ossFilePath+"/"+fileName, expiration);
//            result.setContent(url.toString().replace("\\","//"));
//        }catch(Exception e){
//            e.printStackTrace();
//            result.setCode(ResultCode.ERROR_SERVICE);
//            result.setMsg("上传失败");
//        }finally {
//            ossClient.shutdown();
//        }
//        return result;
//    }
}
