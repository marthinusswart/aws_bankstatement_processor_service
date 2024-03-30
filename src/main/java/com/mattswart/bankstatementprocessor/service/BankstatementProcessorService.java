package com.mattswart.bankstatementprocessor.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstance;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstances;
import com.mattswart.bankstatementprocessor.util.AmazonS3Utils;
import com.mattswart.bankstatementprocessor.util.SimpleJsonParser;

@Service
public class BankstatementProcessorService {
    @Autowired
    private AmazonS3Utils amazonS3Utils;

    public String refreshServerUpdateTime(String serverInstanceId) throws Exception {
        String running_servers_json = amazonS3Utils.loadJsonFile();

        SimpleJsonParser simpleJsonParser = SimpleJsonParser.builder()
                .dataString(running_servers_json)
                .build();

        BSPServerInstances serverInstances = simpleJsonParser.getServerInstances();

        for (int i = 0; i < serverInstances.Instances().size(); i++){
            var instance = serverInstances.Instances().get(i);
            
            if (instance.InstanceId().equals(serverInstanceId)){     
                float currentTimestamp = Instant.now().getEpochSecond();
                var newInstance = new BSPServerInstance(instance.InstanceId(), instance.SpotInstanceRequestId(), instance.StartTime(), currentTimestamp);
                serverInstances.Instances().remove(i);
                serverInstances.Instances().add(newInstance);
                break;
            }
        }

        running_servers_json = simpleJsonParser.setServerInstances(serverInstances);
        amazonS3Utils.storeJsonFile(running_servers_json);
                
        return running_servers_json;
    }
}
