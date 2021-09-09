package com.ymj.controller;

import com.ymj.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Classname PositionController
 * @Description TODO
 * @Date 2021/9/9 15:53
 * @Created by yemingjie
 */
@Controller
public class PositionController {
    @Autowired
    private PositionService service;

    /**
     * 测试页面
     *
     * @return
     */
    @GetMapping({"/", "/index"})
    public String indexPage() {
        return "index";
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    @ResponseBody
    public List<Map<String, Object>> searchPosition(@PathVariable("keyword") String keyword, @PathVariable("pageNo") int pageNo,
                                                    @PathVariable("pageSize") int pageSize) throws IOException {
        List<Map<String, Object>> list = service.searchPos(keyword, pageNo, pageSize);
        return list;
    }


    /**
     * 导入数据
     *
     * @return
     */
    @RequestMapping("/importAll")
    @ResponseBody
    public String importAll() {
        try {
            service.importAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

}
