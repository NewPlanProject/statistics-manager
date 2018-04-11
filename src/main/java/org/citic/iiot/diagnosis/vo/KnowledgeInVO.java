package org.citic.iiot.diagnosis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yxw on 2017/7/19.
 */
@Data
@NoArgsConstructor
public class KnowledgeInVO {

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
    @ApiModelProperty(value = "现象代码",example = "TSJYJ001")
    private String code;
    @ApiModelProperty(value = "现象名称",example = "s")
    private String knowname;
    @ApiModelProperty(value = "用户id",hidden = true)
    private String userid;
    @ApiModelProperty(value = "机构id",hidden = true)
    private String orgid;
    @ApiModelProperty(value = "机构code",hidden = true)
    private String orgcode;
    @ApiModelProperty(value = "故障种类name",example = "11")
    private String faultcategoryname;
    @ApiModelProperty(value = "故障种类id",example = "11")
    private String faultcategoryid;
    @ApiModelProperty(value = "上传的pdf 图片  及文档地址",example = "http://wwww.baidui.com")
    private String url;
    @ApiModelProperty(value = "paixu",example = "2")
    private String disindex;
}
