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
public class MonitorPoint extends PageEntity implements Serializable {

    private static final long serialVersionUID = -1L;
    @ApiModelProperty(value = "监控点id",hidden = true)
    private String pointid;
    @ApiModelProperty(value = "配置id",required = true,example = "qwer")
    private String configid;
    @ApiModelProperty(value = "型号或者关联id",required = true,example = "2e3r3")
    private String foreginid;
    @ApiModelProperty(value = "监控点名称",required = true,example = "温度")
    private String name;
    @ApiModelProperty(value = "X坐标",required = true,example = "50.2")
    private double xposition;
    @ApiModelProperty(value = "y坐标",required = true,example = "86.3")
    private double yposition;
    @ApiModelProperty(value = "设备id",example = "1weq")
    private String deviceid;
    @ApiModelProperty(value = "规则路径",example = "http:oss")
    private String drlname;
    @ApiModelProperty(value = "规则执行间隔",example = "20*1000")
    private int runrate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastupdate;
    @ApiModelProperty(value = "用户id",hidden = true)
    private String userid;
    @ApiModelProperty(value = "机构id",hidden = true)
    private String orgid;
    @ApiModelProperty(value = "机构编码",hidden = true)
    private String orgcode;
    @ApiModelProperty(value = "型号或关联标记",example = "model")
    private String flag;
}
