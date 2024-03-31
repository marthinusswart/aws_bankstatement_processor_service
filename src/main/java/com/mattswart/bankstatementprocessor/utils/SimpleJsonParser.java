package com.mattswart.bankstatementprocessor.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstance;
import com.mattswart.bankstatementprocessor.dto.BSPServerInstances;

public class SimpleJsonParser {
    private String jsonDataString;
    
    private SimpleJsonParser(){}
    
    public static Builder builder(){
        return new Builder();
    }

    public BSPServerInstances getServerInstances(){
        ObjectMapper objectMapper = new ObjectMapper();
        BSPServerInstances serverInstances = null;

        try{
            serverInstances = objectMapper.readValue(jsonDataString, new TypeReference<BSPServerInstances>() {});            
        } catch (Exception ex){
            System.out.println(ex.toString());
        } 
        
        return serverInstances;
    }

    public String setServerInstances(BSPServerInstances serverInstances) throws JsonProcessingException {   
        ObjectMapper objectMapper = new ObjectMapper();     
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());

        return writer.writeValueAsString(serverInstances);
    }

    public static class Builder {

        private SimpleJsonParser simpleJsonParser;

        public Builder() {
            simpleJsonParser = new SimpleJsonParser();
        }        

        public SimpleJsonParser build() {
            return simpleJsonParser;
        }

        public Builder dataString(String jsonDataString){
            simpleJsonParser.jsonDataString = jsonDataString;
            return this;
        }
    }


}
