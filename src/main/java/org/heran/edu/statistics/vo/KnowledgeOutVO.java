package org.heran.edu.statistics.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by yxw on 2017/7/19.
 */
@Data
@NoArgsConstructor
public class KnowledgeOutVO implements Serializable{

    private static final long serialVersionUID = -1L;
    private String knowid;
    private String code;
    private String knowname;
    private String type;
    private String relationid;
    private String relationname;
    private String devicemodelid;
    private String devicemodelname;
    private String userid;
    @ApiModelProperty(value = "故障种类name",example = "11")
    private String faultcategoryname;
    @ApiModelProperty(value = "故障种类id",example = "11")
    private String faultcategoryid;
    @ApiModelProperty(value = "上传的pdf 图片  及文档地址",example = "http://wwww.baidui.com")
    private String url;
    private String disindex;
}
