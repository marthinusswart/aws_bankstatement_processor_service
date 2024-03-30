package com.mattswart.bankstatementprocessor.dto;

import java.util.List;

public record BSPServerInstances (int InstanceCount, List<BSPServerInstance> Instances){}
