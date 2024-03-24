package com.mattswart.bankstatementprocessor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mattswart.bankstatementprocessor.util.AmazonS3Utils;

@Service
public class BankstatementProcessorService {
    @Autowired
    private AmazonS3Utils amazonS3Utils;
    
    public String refreshServerUpdateTime() throws Exception{
        String running_servers_json = amazonS3Utils.loadJsonFile();
        System.out.println(running_servers_json);
        return running_servers_json;
    }
}
