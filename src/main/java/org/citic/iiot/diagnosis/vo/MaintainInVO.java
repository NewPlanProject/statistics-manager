package org.citic.iiot.diagnosis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by yxw on 2017/7/17.
 */
@Data
public class MaintainInVO {

    private String title;
    @ApiModelProperty(value = "起始位置",example = "0")
    private int startLine = 0;
    @ApiModelProperty(value = "每页条数",example = "10")
    private int limitLine = 10;
    @ApiModelProperty(value = "排序字段",example = "update_Date")
    private String orderString = "update_Date";
    @ApiModelProperty(value = "正序 倒序",example = "DESC")
    private String sequence = "DESC";
    @ApiModelProperty(value = "开始时间",hidden = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "结束时间",hidden = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDate;
    private String deviceid;
    private String userid;       //修改人
    private String orgid;    //机构ID
    private String orgCode;    //机构Code
    private String type;          //类型:(1.维修 2.维护)

}
