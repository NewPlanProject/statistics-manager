package org.citic.iiot.diagnosis.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.citic.iiot.app.core.json.BaseJSON;
import org.citic.iiot.app.core.util.CodeUtil;
import org.citic.iiot.diagnosis.vo.MaintainSubOutVO;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */
@Slf4j
public class ExcelUtils {

    private Sheet sheet;    //表格类实例
    private InputStream is;

    @Value("${operation.endpoint}")
    private String endpoint;//上传阿里云地址

    @Value("${operation.accessKeyId}")
    private String accessKeyId;//登录id（// accessKey请登录https://ak-console.aliyun.com/#/查看）

    @Value("${operation.accessKeySecret}")
    private String accessKeySecret;//登录密码

    @Value("${operation.bucketName}")
    private String bucketName;//OSS对象存储名称

    public void readFile(String filePath) throws Exception {
        //is = new FileInputStream(filePath);
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        String accessKeyId = "LTAIntdn9LU1Juyb";
        String accessKeySecret = "CaluWmRSuTuWGOlT5Vo98kDoc4xDJQ";
        String bucketName = "iiot-pic";


        // 关闭client/*  // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        //String newURL = filePath.replaceAll("\\\\","/");
        int position = filePath.lastIndexOf(".com");
        int lastPosition = filePath.indexOf("?");
        String fileName = filePath.substring(position+5,lastPosition);
//        String dir = filePath.substring(0, position);
        OSSObject ossObject = ossClient.getObject(bucketName, fileName);
//        ossClient.getO
        is = ossObject.getObjectContent();
        //ossClient.shutdown();
    }


    /**
     * 获取excel内容
     */
    public static InputStream getExcelContent(List<MaintainSubOutVO> maintainList){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HSSFWorkbook workbook = new HSSFWorkbook();
        if(maintainList.size()==0 || maintainList==null){
            //throw new DpmException(ExceptionCode.DPM_DATA_ERROR.getValue(), "数据源中没有任何数据！", null);
        }
        HSSFSheet sheet = workbook.createSheet();
        // 设置列宽
        // 设置表格默认列宽度为22个字节
        sheet.setDefaultColumnWidth(22);
        // 字体加粗 居中对齐
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        // 字体大小
        font.setFontHeightInPoints((short) 11);
        // 设置字体
        font.setFontName("宋体");
        font.setCharSet(HSSFFont.DEFAULT_CHARSET);
        // 上边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 右边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 下边框
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 左边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        // 水平居中
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        // 设置自动换行
        style.setWrapText(true);
        style.setFont(font);
        // 设置格式
        HSSFDataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("@"));// 文本格式
        // 产生表格标题行
        String[] headers = new String[14];
        headers[0] = "标题";
        headers[1] = "类型";
        headers[2] = "修改日期";
        headers[3] = "修改人";
        headers[4] = "设备名称";
        headers[5] = "维修维护详情";
//        headers[6] = "故障记录ID";         //************ 1822_导出的列表数据与页面列表的不一致_贾楠_2017/09/14_start
        headers[6] = "故障名称";
        headers[7] = "维修/维护记录ID";
        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
        int index = 0;
        HSSFCell cell = null;
        MaintainSubOutVO bean = null;
        if (maintainList != null && !maintainList.isEmpty()) {
            for (int i = 0; i < maintainList.size(); i++) {
                bean = maintainList.get(i);
                index++;
                if(bean == null){
                    continue;
                }
                row = sheet.createRow(index);
                for (int cellIndex = 0; cellIndex < headers.length; cellIndex++) {
                    cell = row.createCell(cellIndex);
                    cell.setCellStyle(style);
                    switch (cellIndex){
                        case 0:// 标题
                            cell.setCellValue(safetyString(bean.getTitle()));
                            break;
                        case 1:// 类型
                            //************ 1822_导出的文件的“类型”修改_贾楠_20170915_end ************
                            cell.setCellValue(safetyString(bean.getType().equals("1")?"维修":"维护"));
                            break;
                        case 2:// 修改日期
                            cell.setCellValue(safetyString(dateFormat.format(bean.getUpdateDate())));
                            break;
                        case 3:// 修改人
                            cell.setCellValue(safetyString(bean.getUsername()));
                            break;
                        case 4:// 设备名称
                            cell.setCellValue(safetyString(bean.getDevicename()));
                            break;
                        case 5://维修维护详情
                            cell.setCellValue(safetyString(bean.getDetail()));
                            break;
                        case 6:// 故障名称
                            cell.setCellValue(safetyString(bean.getFaultname()));
                            //************ 1822_导出的列表数据与页面列表的不一致_贾楠_2017/09/14_start
                            break;
                        case 7:// 维修/维护记录ID
                            cell.setCellValue(safetyString(bean.getId()));
                            break;
                    }
                }
            }
        }
        ByteArrayOutputStream exportFileStream = new ByteArrayOutputStream();
        InputStream excelStream = null;
        try {
            workbook.write(exportFileStream);
            byte[] workFile = exportFileStream.toByteArray();
            excelStream = new ByteArrayInputStream(workFile);
        }catch (Exception e){
            e.printStackTrace();
        }
        return excelStream;
    }

    private static String safetyString(String str){
        return str == null ? "" :str;
    }


    /**
     * 读取excel文件，创建表格实例
     */
    public void loadFile() {
        Workbook workBook = null;
        try {
            workBook = WorkbookFactory.create(is);
            sheet = workBook.getSheetAt(0);
        } catch (Exception e) {
            log.debug("Found Exception:", e);
        }finally{
            try {
                if (workBook != null) {
                    workBook.close();
                }
            } catch (IOException e) {
                log.error("Found Exception:", e);
            }
        }
    }


    //获取单元格的值
    private String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            //判断单元格数据的类型，不同类型调用不同的方法
            switch (cell.getCellType()) {
                //数值类型
                case Cell.CELL_TYPE_NUMERIC:
                    //进一步判断 ，单元格格式是日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        //数值
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                //判断单元格是公式格式，需要做一种特殊处理来得到相应的值
                case Cell.CELL_TYPE_FORMULA:{
                    try{
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    }catch(IllegalStateException e){
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                    }

                }
                break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }



    //初始化表格中的每一行，并得到每一个单元格的值
    /*public List<Sensor> init(){
        //获取行数
        int rowNum = sheet.getLastRowNum();
        List<Sensor> result = new LinkedList<Sensor>();
        int number = 0;
        for(int i=1;i<=rowNum;i++){
            Row row = sheet.getRow(i);
            //每有新的一行，创建一个新的LinkedList对象
//            Map rowBean = new HashMap();
            String name = getCellValue(row.getCell(0));
            String code = getCellValue(row.getCell(1));
            if(CodeUtil.isNotNullEmpty(name) && CodeUtil.isNotNullEmpty(code)){
                number++;
                Sensor sensor = new Sensor();
                sensor.setName(name);
                sensor.setCode(code);
                sensor.setDispindex(number);
                result.add(sensor);
            }
//            Sensor sensor = new Sensor();
//            sensor.setName(getCellValue(row.getCell(0)));
//            sensor.setCode(getCellValue(row.getCell(1)));
//            sensor.setDispindex(i);
//            rowBean.put("name",getCellValue(row.getCell(3)));
//            rowBean.put("code",getCellValue(row.getCell(4)));
//            rowBean.put("dispindex",i);
//            result.add(sensor);
        }
        return result;
    }*/


    //控制台打印保存的表格数据
    public String toJson(boolean hasHead,int readType,int keyType,List<LinkedList> results){
        LinkedList head = null;
        if (hasHead){
            head = results.remove(0);
        }
        if (results.size()==0){
            return null;
        }
        switch (readType){
            case 0:
                List<JSONObject> res = new ArrayList<JSONObject>();
                if (head == null){
                    break;
                }
                for (LinkedList row :results){
                    JSONObject bean = new JSONObject();
                    for (int j=0;j<head.size();j++){
                        if (head.get(j) == null || String.valueOf(head.get(j)) == ""){
                            continue;
                        }
                        bean.put(String.valueOf(head.get(j)),row.get(j));
                    }
                    res.add(bean);
                }
                return JSONObject.toJSONString(res);
            case 1:
                JSONObject typeRow = new JSONObject();
                for (LinkedList row :results){
                    for (int i=0;i<row.size();i+=2){
                        if (keyType ==1){
                            if (row.get(i) == null || row.get(i) == "")
                                continue;
                            typeRow.put(String.valueOf(row.get(i)),(i+1)>(row.size()-1) ? "default" :String.valueOf(row.get(i+1)));
                        }else {
                            String key = (i+1)>(row.size()-1) ? "default" :String.valueOf(row.get(i+1));
                            if (StringUtils.isBlank(key))
                                continue;
                            typeRow.put(key,row.get(i));
                        }
                    }
                }
                return typeRow.toJSONString();
            case 2:
                List<JSONObject> val = new ArrayList<JSONObject>();
                for (LinkedList row :results){
                    JSONObject typeRow1 = new JSONObject();
                    for (int i=0;i<row.size();i+=2){
                        if (keyType ==1){
                            typeRow1.put(String.valueOf(row.get(i)),(i+1)>(row.size()-1) ? "default" :String.valueOf(row.get(i+1)));
                        }else {
                            typeRow1.put((i+1)>(row.size()-1) ? "default" :String.valueOf(row.get(i+1)),row.get(i));
                        }
                    }
                    val.add(typeRow1);
                }
                return BaseJSON.toJSONString(val);
            default:
                break;
        }
        return null;
    }



}


