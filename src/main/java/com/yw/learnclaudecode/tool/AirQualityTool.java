package com.yw.learnclaudecode.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @Author: yw
 * @Date: 2026/5/21 09:54
 * @Description:
 **/
@JsonClassDescription("获取天气空气质量工具")
public class AirQualityTool {

    @JsonPropertyDescription("城市")
    public String city;

    public String getAirQuality() {
        return city + "今天空气质量优秀";
    }

}
