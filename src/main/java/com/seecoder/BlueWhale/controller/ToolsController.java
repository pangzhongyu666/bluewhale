package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.ExcelService;
import com.seecoder.BlueWhale.service.ImageService;
import com.seecoder.BlueWhale.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ToolsController {
    @Autowired
    ImageService imageService;
    @Autowired
    ExcelService excelService;

    @PostMapping("/images")
    public ResultVO<String> upload(@RequestParam MultipartFile file){
        return ResultVO.buildSuccess(imageService.upload(file));
    }
    @GetMapping("/excel/{storeId}")
    public ResultVO<String> getOrdersExcel(@PathVariable(value = "storeId")Integer storeId) throws IOException {
        return ResultVO.buildSuccess(excelService.createOrderSheet(storeId));
    }
}
