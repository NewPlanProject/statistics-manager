package org.citic.iiot.diagnosis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yxw on 2017/7/17.
 */
@Data
public class MaintainSubOutVO  implements Serializable {

    private static final long serialVersionUID = -1L;

    private String title;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    private String username;
    private String devicename;
    private String detail;
    private String faultid;
    private String faultname;
    private String id;
}
