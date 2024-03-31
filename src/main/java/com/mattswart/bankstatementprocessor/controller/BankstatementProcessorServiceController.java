package com.mattswart.bankstatementprocessor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.mattswart.bankstatementprocessor.dto.BSPServiceStatus;
import com.mattswart.bankstatementprocessor.service.BankstatementProcessorMessagePublisher;
import com.mattswart.bankstatementprocessor.service.BankstatementProcessorService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/bankstatement_processor_service/v1")
public class BankstatementProcessorServiceController {
    @Value("${SERVER_INSTANCE_ID}")
    private String runningInstanceId;

    @Value("${BANK_STATEMENT_DIRECTORY}")
    private String bankstatementDirectory;

    @Value("${BANK_STATEMENT_ARCHIVE_DIRECTORY}")
    private String bankstatementArchiveDirectory;

    @Autowired
	private BankstatementProcessorService bankstatementProcessorService;

    @GetMapping("/is_running")
	public BSPServiceStatus serviceStatus() {
		return new BSPServiceStatus(runningInstanceId, "Running");
	}

    @PostMapping("/refresh_server_update_time")
    public String refreshServerUpdateTime() {
        String response = "{}";

        try{
            response = bankstatementProcessorService.refreshServerUpdateTime(runningInstanceId);
        } catch (Exception ex){            
            System.out.println(ex.toString());
            return "{'exception':'"+ex.toString()+"'}";
        }

        return response;
    }

    @PostMapping("/process_bankstatement_directory")
    public String processBankstatementDirectory() {
        String response = "{'StatementDirectory':'" + bankstatementDirectory + "'}";

        try{
            response = bankstatementProcessorService.refreshServerUpdateTime(runningInstanceId);
            response = bankstatementProcessorService.processBankstatementDirectory(bankstatementDirectory, bankstatementArchiveDirectory);
        } catch (Exception ex){            
            System.out.println(ex.toString());
            return "{'exception':'"+ex.toString()+"'}";
        }

        return response;
    }
              
    
    
    
}
