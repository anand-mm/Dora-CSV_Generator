package com.codeforyou.csv_generator;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class csvController {

    @Autowired
    csvService cService;

    @PostMapping("/multidomain")
    public void multiDomain(@RequestParam(name = "file") MultipartFile multipartFile,HttpServletResponse response) throws Exception {
        cService.csv_generator_listofMap(response,multipartFile);
    }

    @PostMapping("/singledomain")
    public void singleDomain(@RequestParam(name = "file") MultipartFile multipartFile,HttpServletResponse response) {
        cService.single_csv(response, multipartFile);;
    }
    
}
