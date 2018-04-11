package org.citic.iiot.diagnosis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.citic.iiot.app.core.mybaits.domain.PageEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Faultrecord extends PageEntity implements Serializable {

    private static final long serialVersionUID = -1L;
    private String id;            //故障、预警记录ID
    private String name;          //故障、预警名称
    private String detail;       //预警、故障详情
    private String deviceid;     //设备uuid
    private String devicename;   //设备名称
    private String customer;     //客户名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;     //开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;       //结束时间
    private String type;        //类型（1:预警、2:故障）
    private String status;      //状态：1.解决 0.未解决
    private String monitorPointId; //监控点id
    private String code;        //现象编码
    private int salience;      //规则等级
    private String knowid;     //知识库id
    private String analyse;    //现象分析
    private String solution;   //解决方案
    private List<Knowledge> knowledges;  //知识条目集合
    private Knowledge knowledge;         //知识条目
    private String userid;     //用户UUID
    private String orgid;    //机构ID
    private String orgCode;    //机构Code
    private List<String> deviceidList; //设备id集合
}
