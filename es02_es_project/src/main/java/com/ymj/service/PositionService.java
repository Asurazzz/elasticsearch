package com.ymj.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Classname PostitionService
 * @Description TODO
 * @Date 2021/9/9 15:34
 * @Created by yemingjie
 */
public interface PositionService {
    /* 分页查询 */
    public List<Map<String, Object>> searchPos(String keyword, int pageNo, int pageSize) throws IOException;

    /**
     * 导入数据
     */
    void importAll() throws IOException;
}
