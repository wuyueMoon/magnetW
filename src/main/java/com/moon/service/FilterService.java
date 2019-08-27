package com.moon.service;

import com.moon.config.ApplicationConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

@Service
public class FilterService {
    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    ApplicationConfig config;

    private List<String> mFilterList;

    private Properties properties;
    private boolean completed = true;

    @PostConstruct
    public void init() {
        if (!config.resultFilterEnabled) {
            return;
        }
        mFilterList = new ArrayList<String>();
        File filterFile = getFilterPropertiesFile();
        try {
            properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(filterFile));
            Collection<Object> values = properties.values();
            for (Object value : values) {
                mFilterList.add((String) value);
            }
        } catch (IOException e) {
            properties = new Properties();
        }
        logger.info(String.format("过滤词文件 %s--->%s--->加载%d个过滤词", filterFile.getAbsolutePath(), String.valueOf(filterFile.exists()), mFilterList.size()));
    }

    public boolean add(String input) throws Exception {
        if (!completed) {
            throw new Exception("系统繁忙，请稍候再试");
        }
        //如果是占位符
        if (input.toLowerCase().equals(config.searchPlaceholder.toLowerCase())) {
            throw new Exception("暂不支持屏蔽占位搜索词");
        }
        if (mFilterList.contains(input)) {
            return true;
        }
        completed = false;
        try {
            mFilterList.add(input);
            properties.setProperty(String.valueOf(System.currentTimeMillis()), input);
            properties.store(new FileOutputStream(getFilterPropertiesFile()), null);
            completed = true;
            logger.info(String.format("屏蔽词添加成功--->%s--->当前%d个屏蔽词", input, mFilterList.size()));
            return true;
        } catch (Exception e) {
            completed = true;
            logger.error("屏蔽词添加失败", e);
            throw e;
        }
    }

    public boolean contains(String keyword) {
        return mFilterList.contains(keyword);
    }

    public List<String> getFilterList() {
        return mFilterList;
    }

    private File getFilterPropertiesFile() {
        return new File(config.getFilterPropertiesDir(), "filter.properties");
    }

}