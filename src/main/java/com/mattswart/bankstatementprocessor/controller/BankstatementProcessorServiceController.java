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

@RestController
@RequestMapping("/bankstatement_processor_service")
public class BankstatementProcessorServiceController {
    @Value("${SERVER_INSTANCE_ID}")
    private String runningInstanceId;

    @GetMapping("/is_running")
	public BSPServiceStatus serviceStatus() {
		return new BSPServiceStatus(runningInstanceId, "Running");
	}
    
}
