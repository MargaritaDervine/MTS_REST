package com.start.mts;

public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(int recordId) {
        super(String.format("Record id %d does not exist", recordId));
    }
}
