package com.yw.learnclaudecode.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * @Author: yw
 * @Date: 2026/5/20 18:56
 * @Description:
 **/
@JsonClassDescription("获取天气工具")
public class WeatherTool {

    @JsonPropertyDescription("城市")
    public String city;

    public String getWeather() {
        return city + "今天26度";
    }

}
