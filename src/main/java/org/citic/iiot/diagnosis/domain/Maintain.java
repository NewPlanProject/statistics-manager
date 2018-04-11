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
public class Maintain extends PageEntity implements Serializable {

    private static final long serialVersionUID = -3769296324502881779L;
    private String id;             //维修/维护记录id
    private String title;         //标题
    private String type;          //类型:(1.维修 2.维护)
    private String userid;       //修改人
    private String username;     //用户名称
    private String deviceid;     //设备uuid
    private String devicename;   //设备名称
    private String detail;       //维修/维护详情
    private String faultid;     //故障记录id
    private String faultname;    //故障名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;   //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;    //开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;      //结束时间
    private String orgid;    //机构ID
    private String orgCode;    //机构Code
    private String month;      //月份
    private List<String> deviceidList; //设备id集合
}
