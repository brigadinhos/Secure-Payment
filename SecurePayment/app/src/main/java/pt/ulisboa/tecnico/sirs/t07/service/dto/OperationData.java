package pt.ulisboa.tecnico.sirs.t07.service.dto;


import pt.ulisboa.tecnico.sirs.t07.service.OperationService;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by tiago on 14/11/2016.
 */
public class OperationData {
    private UUID operationUid;
    private Timestamp timestamp;
    private OperationService service;

    public OperationData(UUID uid,Timestamp time,OperationService service){
        this.operationUid=uid;
        this.timestamp=time;
        this.service=service;
    }

    public void executeService(){
        this.service.execute();
    }
}