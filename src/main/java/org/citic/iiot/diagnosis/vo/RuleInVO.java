package org.citic.iiot.diagnosis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User: Mr.zheng
 * Date: 2017/8/9
 * Time: 16:11
 */
@Data
@NoArgsConstructor
public class RuleInVO implements Serializable{

    private static final long serialVersionUID = -5459005643003479951L;

    @ApiModelProperty(value = "起始位置",example = "0")
    private int startLine = 0;
    @ApiModelProperty(value = "每页条数",example = "10")
    private int limitLine = 10;
    @ApiModelProperty(value = "排序字段",example = "createtime")
    private String orderString = "createtime";
    @ApiModelProperty(value = "正序 倒序",example = "DESC")
    private String sequence = "DESC";
    @ApiModelProperty(value = "类型",example = "1")
    private String type;
    @ApiModelProperty(value = "规则名称",example = "s")
    private String rulename;
    @ApiModelProperty(value = "型号部件id",example = "11")
    private String partsid;
}
