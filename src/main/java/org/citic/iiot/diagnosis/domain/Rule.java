package org.citic.iiot.diagnosis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.citic.iiot.app.core.mybaits.domain.PageEntity;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class Rule extends PageEntity implements Serializable {

    private static final long serialVersionUID = -1L;
    @ApiModelProperty(value = "uuid",hidden = true)
    private String ruleid;
    @ApiModelProperty(value = "现象代码",example = "TSJYJ001",required = true)
    private String code;
    @ApiModelProperty(value = "现象民称",example = "提升机预警001")
    private String codename;
    @ApiModelProperty(value = "现象描素",example = "高温预警",required = true)
    private String disp;
    @ApiModelProperty(value = "类型",example = "1",required = true)
    private String type;
    @ApiModelProperty(value = "监控点id",example = "id",required = true)
    private String monitorPointid;
    @ApiModelProperty(value = "规则json",example = "this['sensor']=='L0023'&&this['measure']>=40",required = true)
    private String ruleConfig;
    @ApiModelProperty(value = "规则名称",example = "高温预警",required = true)
    private String rulename;
    @ApiModelProperty(value = "规则内容",example = "举个例子")
    private String rulecontent;
    @ApiModelProperty(value = "规则刷新时间",example = "5000",required = true)
    private int runrate;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @ApiModelProperty(value = "修改时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    @ApiModelProperty(value = "设备UUID",example = "sbuuid",required = true)
    private String deviceuuid;
    @ApiModelProperty(value = "设备名称",example = "sbmc",required = true)
    private String devicename;
    @ApiModelProperty(value = "关联uuid",example = "gluuid",required = true)
    private String relationid;
    @ApiModelProperty(value = "关联名称",example = "glmc",required = true)
    private String relationname;
    @ApiModelProperty(value = "型号uuid",example = "xhuuid",required = true)
    private String devicemodelid;
    @ApiModelProperty(value = "型号名称",example = "xhmc",required = true)
    private String devicemodelname;
    @ApiModelProperty(value = "规则等级",example = "5",required = true)
    private int salience;
    @ApiModelProperty(value = "类型uuid",example = "类型uuid",required = true)
    private String devicetypeid;
    @ApiModelProperty(value = "类型名称",example = "lxmc",required = true)
    private String devicetypename;
    @ApiModelProperty(value = "开始时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "结束时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;
    @ApiModelProperty(value = "知识库id",hidden = true)
    private String knowledgeid;
    @ApiModelProperty(value = "用户id",hidden = true)
    private String userid;
    @ApiModelProperty(value = "机构id",hidden = true)
    private String orgid;
    @ApiModelProperty(value = "机构code",hidden = true)
    private String orgcode;
    @ApiModelProperty(value = "设备种类名称",example = "磨机")
    private String parentdictname;
    @ApiModelProperty(value = "型号或关联标记",example = "model")
    private String flag;
    @ApiModelProperty(value = "型号部件id",example = "11")
    private String partsid;

    @ApiModelProperty(value = "种类code",example = "11")
    private String categorycode;
    @ApiModelProperty(value = "型号code",example = "11")
    private String modelcode;
    @ApiModelProperty(value = "型号code",example = "11")
    private String partscode;
}