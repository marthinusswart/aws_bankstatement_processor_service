package com.mattswart.bankstatementprocessor.service;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstance;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstances;
import com.mattswart.bankstatementprocessor.dto.BankstatementTransaction;
import com.mattswart.bankstatementprocessor.utils.AmazonS3Utils;
import com.mattswart.bankstatementprocessor.utils.SimpleCSVParser;
import com.mattswart.bankstatementprocessor.utils.SimpleJsonParser;

@Service
public class BankstatementProcessorService {
    @Autowired
    private AmazonS3Utils amazonS3Utils;

    @Autowired
    private SimpleCSVParser simpleCSVParser;

    @Autowired
    private BankstatementProcessorMessagePublisher bankstatementProcessorMessagePublisher;

    public String refreshServerUpdateTime(String serverInstanceId) throws Exception {
        String runningServersJson = amazonS3Utils.loadJsonFile();

        SimpleJsonParser simpleJsonParser = SimpleJsonParser.builder()
                .dataString(runningServersJson)
                .build();

        BSPServerInstances serverInstances = simpleJsonParser.getServerInstances();

        for (int i = 0; i < serverInstances.Instances().size(); i++) {
            var instance = serverInstances.Instances().get(i);

            if (instance.InstanceId().equals(serverInstanceId)) {
                float currentTimestamp = Instant.now().getEpochSecond();
                var newInstance = new BSPServerInstance(instance.InstanceId(), instance.SpotInstanceRequestId(),
                        instance.StartTime(), currentTimestamp);
                serverInstances.Instances().remove(i);
                serverInstances.Instances().add(newInstance);
                break;
            }
        }

        runningServersJson = simpleJsonParser.setServerInstances(serverInstances);
        amazonS3Utils.storeJsonFile(runningServersJson);

        return runningServersJson;
    }

    public String processBankstatementDirectory(String bankstatementDirectory, String bankstatementArchiveDirectory)
            throws Exception {
        File directory = new File(bankstatementDirectory);
        File[] filesList = directory.listFiles();
        ArrayList<File> fileList = new ArrayList<>(Arrays.asList(filesList));

        fileList.forEach(this::processBankstatementFile);

        return "{}";
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private void processBankstatementFile(File bankstatementFile) {
        if (getFileExtension(bankstatementFile).equals("csv")) {
            try {
                simpleCSVParser.initialiseFile(bankstatementFile);
                String[] dataLine = simpleCSVParser.readLine();
                
                while (dataLine != null){
                    BankstatementTransaction bankStmtTrx = new BankstatementTransaction(dataLine[0], Float.parseFloat(dataLine[1]));
                    bankstatementProcessorMessagePublisher.sendBankstatementTransaction(bankStmtTrx);
                    dataLine = simpleCSVParser.readLine();
                }

            } catch (Exception ex) {
                System.out.println(String.format("Failed to parse %s", bankstatementFile.getName()));
            }
        }
    }
}
