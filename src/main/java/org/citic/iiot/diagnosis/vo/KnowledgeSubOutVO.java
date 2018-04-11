package org.citic.iiot.diagnosis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.citic.iiot.app.core.util.CodeUtil;

import java.io.Serializable;

/**
 * Created by yxw on 2017/7/19.
 */
@Data
@NoArgsConstructor
public class KnowledgeSubOutVO implements Serializable{

    private static final long serialVersionUID = -1L;
    private String knowid;
    private String analyse;
    private String solution;
    private String userid;
    private Integer number = 200;
    private Integer numbers = 500;

    @ApiModelProperty(value = "故障种类name",example = "11")
    private String faultcategoryname;
    @ApiModelProperty(value = "故障种类id",example = "11")
    private String faultcategoryid;
    @ApiModelProperty(value = "上传的pdf 图片  及文档地址",example = "http://wwww.baidui.com")
    private String url;
    private String disindex;

    public Integer getNumber() {
        if(CodeUtil.isNotNullEmpty(analyse)){
            number = 200 - (analyse.length() > 200 ? 200 : analyse.length());
        }
        return number;
    }

    public Integer getNumbers() {
        if(CodeUtil.isNotNullEmpty(solution)){
            numbers = 500 - (solution.length() > 500 ? 500 : solution.length());
        }
        return numbers;
    }
}
