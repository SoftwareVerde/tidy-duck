package com.softwareverde.tidyduck.most;

import java.util.ArrayList;
import java.util.List;

public class MostType {
    private Long _id;
    private String _name;
    private PrimitiveType _primitiveType;
    private boolean _isPrimaryType;
    private String _bitfieldLength;
    private String _enumMax;
    private MostType _numberBaseType;
    private String _numberExponent;
    private String _numberRangeMinimum;
    private String _numberRangeMaximum;
    private String _numberStep;
    private MostUnit _numberUnit;
    private String _stringMaxSize;
    private String _streamLength;
    private String _streamMaxLength;
    private String _streamMediaType;
    private String _arrayName;
    private String _arrayDescription;
    private MostType _arrayElementType;
    private String _arraySize;
    private String _recordName;
    private String _recordDescription;
    private String _recordSize;
    private List<BooleanField> _booleanFields = new ArrayList<>();
    private List<EnumValue> _enumValues = new ArrayList<>();
    private List<StreamCase> _streamCases = new ArrayList<>();
    private List<RecordField> _recordFields = new ArrayList<>();

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public PrimitiveType getPrimitiveType() {
        return _primitiveType;
    }

    public void setIsPrimitiveType(PrimitiveType primitiveType) {
        _primitiveType = primitiveType;
    }

    public boolean isPrimaryType() {
        return _isPrimaryType;
    }

    public void setIsPrimaryType(boolean primaryType) {
        _isPrimaryType = primaryType;
    }

    public String getBitfieldLength() {
        return _bitfieldLength;
    }

    public void setBitfieldLength(String bitfieldLength) {
        _bitfieldLength = bitfieldLength;
    }

    public String getEnumMax() {
        return _enumMax;
    }

    public void setEnumMax(String enumMax) {
        _enumMax = enumMax;
    }

    public MostType getNumberBaseType() {
        return _numberBaseType;
    }

    public void setNumberBaseType(MostType numberBaseType) {
        _numberBaseType = numberBaseType;
    }

    public String getNumberExponent() {
        return _numberExponent;
    }

    public void setNumberExponent(String numberExponent) {
        _numberExponent = numberExponent;
    }

    public String getNumberRangeMinimum() {
        return _numberRangeMinimum;
    }

    public void setNumberRangeMinimum(String numberRangeMinimum) {
        _numberRangeMinimum = numberRangeMinimum;
    }

    public String getNumberRangeMaximum() {
        return _numberRangeMaximum;
    }

    public void setNumberRangeMaximum(String numberRangeMaximum) {
        _numberRangeMaximum = numberRangeMaximum;
    }

    public String getNumberStep() {
        return _numberStep;
    }

    public void setNumberStep(String numberStep) {
        _numberStep = numberStep;
    }

    public MostUnit getNumberUnit() {
        return _numberUnit;
    }

    public void setNumberUnit(MostUnit numberUnit) {
        _numberUnit = numberUnit;
    }

    public String getStringMaxSize() {
        return _stringMaxSize;
    }

    public void setStringMaxSize(String stringMaxSize) {
        _stringMaxSize = stringMaxSize;
    }

    public String getStreamLength() {
        return _streamLength;
    }

    public void setStreamLength(String streamLength) {
        _streamLength = streamLength;
    }

    public String getStreamMaxLength() {
        return _streamMaxLength;
    }

    public void setStreamMaxLength(String streamMaxLength) {
        _streamMaxLength = streamMaxLength;
    }

    public String getStreamMediaType() {
        return _streamMediaType;
    }

    public void setStreamMediaType(String streamMediaType) {
        _streamMediaType = streamMediaType;
    }

    public String getArrayName() {
        return _arrayName;
    }

    public void setArrayName(String arrayName) {
        _arrayName = arrayName;
    }

    public String getArrayDescription() {
        return _arrayDescription;
    }

    public void setArrayDescription(String arrayDescription) {
        _arrayDescription = arrayDescription;
    }

    public MostType getArrayElementType() {
        return _arrayElementType;
    }

    public void setArrayElementType(MostType arrayElementType) {
        _arrayElementType = arrayElementType;
    }

    public String getArraySize() {
        return _arraySize;
    }

    public void setArraySize(String arraySize) {
        _arraySize = arraySize;
    }

    public String getRecordName() {
        return _recordName;
    }

    public void setRecordName(String recordName) {
        _recordName = recordName;
    }

    public String getRecordDescription() {
        return _recordDescription;
    }

    public void setRecordDescription(String recordDescription) {
        _recordDescription = recordDescription;
    }

    public String getRecordSize() {
        return _recordSize;
    }

    public void setRecordSize(String recordSize) {
        _recordSize = recordSize;
    }

    public List<BooleanField> getBooleanFields() {
        return new ArrayList<>(_booleanFields);
    }

    public void addBooleanField(final BooleanField booleanField) {
        _booleanFields.add(booleanField);
    }

    public void setBooleanFields(final List<BooleanField> booleanFields) {
        _booleanFields = new ArrayList<>(booleanFields);
    }

    public List<EnumValue> getEnumValues() {
        return new ArrayList<>(_enumValues);
    }

    public void addEnumValue(final EnumValue enumValue) {
        _enumValues.add(enumValue);
    }

    public void setEnumValues(final List<EnumValue> enumValues) {
        _enumValues = new ArrayList<>(enumValues);
    }

    public List<StreamCase> getStreamCases() {
        return new ArrayList<>(_streamCases);
    }

    public void addStreamCase(final StreamCase streamCase) {
        _streamCases.add(streamCase);
    }

    public void setStreamCases(final List<StreamCase> streamCases) {
        _streamCases = new ArrayList<>(streamCases);
    }

    public List<RecordField> getRecordFields() {
        return new ArrayList<>(_recordFields);
    }

    public void addRecordField(final RecordField recordField) {
        _recordFields.add(recordField);
    }

    public void setRecordFields(final List<RecordField> recordFields) {
        _recordFields = new ArrayList<>(recordFields);
    }
}
