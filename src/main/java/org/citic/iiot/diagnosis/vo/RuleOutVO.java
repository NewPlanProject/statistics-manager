package org.citic.iiot.diagnosis.vo;

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
public class RuleOutVO implements Serializable{

    private static final long serialVersionUID = -1L;

    private String code;
    private String codename;
    private String rulename;
    private String type;
    private String relationid;
    private String relationname;
    private String devicemodelid;
    private String devicemodelname;
}
