package com.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.entity.AudioChip;
import com.entity.ChipLabel;
import com.entity.MergeObject;

public class JsonParser {
	public static MergeObject jsonParse(HttpServletRequest req) {
		//自定义从body中获取json格式数据
        StringBuffer jsonBuffer = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jsonBuffer.append(line);
        } catch (Exception e) { /*report an error*/ }
        
        //使用FastJson工具解析成对象数据并做后续的业务逻辑处理
        JSONObject reqJson = JSONObject.parseObject(jsonBuffer.toString().replaceAll("\\s","").replaceAll("\n",""));
        
        JSONArray listJson  = reqJson.getJSONArray("list");
        List<ChipLabel> list = listJson.toJavaList(ChipLabel.class);
        
        MergeObject mergeObject = new MergeObject(reqJson.getString("name"),list);
        return mergeObject;
	}
}
