package com.softwareverde.mostadapter;

public class Operation {
    public static final String DEFAULT_OPERATION = "Control";

    public enum OperationType {
        // Property operations
        SET("PCmdSet", true),
        GET("PCmdGet", true),
        SET_GET("PCmdSetGet", true),
        INCREMENT("PCmdIncrement", true),
        DECREMENT("PCmdDecrement", true),
        GET_PROPERTY_INTERFACE("PcmdGetInterface", true),
        STATUS("PReportStatus", false),
        PROPERTY_INTERFACE("PReportInterface", false),
        PROPERTY_ERROR("PReportError", false),
        // Method operations
        START("MCmdStart", true),
        START_RESULT("MCmdStartResult", true),
        START_RESULT_ACK("MCmdStartResultAck", true),
        GET_METHOD_INTERFACE("MCmdGetInterface", true),
        START_ACK("MCmdStartAck", true),
        ABORT("MCmdAbort", true),
        ABORT_ACK("MCmdAbortAck", true),
        ERROR_ACK("MReportErrorAck", false),
        PROCESSING_ACK("MReportProcessingAck", false),
        PROCESSING("MReportProcessing", false),
        RESULT("MReportResult", false),
        RESULT_ACK("MReportResultAck", false),
        METHOD_INTERFACE("MReportInterface", false),
        METHOD_ERROR("MReportError", false);

        private String _name;
        private boolean _isInput;

        OperationType(String name, boolean isInput) {
            _name = name;
            _isInput = isInput;
        }

        public String getName() {
            return _name;
        }

        public boolean isInput() {
        return _isInput;
    }
    }

    private OperationType _operationType;
    private String _parameterPosition;
    private String _channel = DEFAULT_OPERATION;

    public OperationType getOperationType() {
        return _operationType;
    }

    public void setOperationType(OperationType operationType) {
        _operationType = operationType;
    }

    public String getParameterPosition() {
        return _parameterPosition;
    }

    public void setParameterPosition(final String parameterPosition) {
        _parameterPosition = parameterPosition;
    }

    public String getChannel() {
        return _channel;
    }

    public void setChannel(final String channel) {
        _channel = channel;
    }
}
