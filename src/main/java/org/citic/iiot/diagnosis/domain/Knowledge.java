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
public class Knowledge extends PageEntity implements Serializable {

    private static final long serialVersionUID = -1L;
    @ApiModelProperty(value = "uuid",hidden = true)
    private String knowid;
    @ApiModelProperty(value = "现象分析",hidden = true)
    private String analyse;
    @ApiModelProperty(value = "解决方案",hidden = true)
    private String solution;
    @ApiModelProperty(value = "用户id",hidden = true)
    private String userid;
    @ApiModelProperty(value = "创建时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @ApiModelProperty(value = "更新时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    @ApiModelProperty(value = "现象代码",required = true,example = "TSJYJ001")
    private String code;
    @ApiModelProperty(value = "类型",required = true,example = "2")
    private String type;
    @ApiModelProperty(value = "现象名称",required = true,example = "提升机")
    private String knowname;
    @ApiModelProperty(value = "关联id",example = "idNo1")
    private String relationid;
    @ApiModelProperty(value = "关联名称",example = "relationname")
    private String relationname;
    @ApiModelProperty(value = "型号id",example = "idNo2")
    private String devicemodelid;
    @ApiModelProperty(value = "型号名称",example = "modelname")
    private String devicemodelname;
    @ApiModelProperty(value = "父id",hidden = true)
    private String parentid;
    private String deviceid;
    @ApiModelProperty(value = "机构id",hidden = true)
    private String orgid;
    @ApiModelProperty(value = "机构code",hidden = true)
    private String orgcode;
    //TODO 显示字段
    private  String viewName;
    /**
     *  现象分析  与 解决方案json
     */
    @ApiModelProperty(value = "现象分析 与 解决方案的json",required = true,example = "[{\"analyse\": \"这是一个修改的故障3\", \"solution\": \"想办法解决他3\",\"knowid\":\"80adcb5793e94771ad18364e74646eb5\"}]")
    private String msgJson;

    @ApiModelProperty(value = "故障种类name",example = "11")
    private String faultcategoryname;
    @ApiModelProperty(value = "故障种类id",example = "11")
    private String faultcategoryid;
    @ApiModelProperty(value = "上传的pdf 图片  及文档地址",example = "http://wwww.baidui.com")
    private String url;
    @ApiModelProperty(value = "paixu",example = "2")
    private String disindex;
}