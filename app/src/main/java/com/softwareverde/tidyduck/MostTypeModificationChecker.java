package com.softwareverde.tidyduck;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.tidyduck.database.MostTypeInflater;
import com.softwareverde.tidyduck.most.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class MostTypeModificationChecker {

    final DatabaseConnection<Connection> _databaseConnection;

    public MostTypeModificationChecker(final DatabaseConnection<Connection> databaseConnection) {
        _databaseConnection = databaseConnection;
    }

    /**
     * <p>Checks provided types against their current versions stored in the database.</p>
     *
     * <p>The set of validation rules employed are roughly to prevent "on-the-wire" changes to types
     * that have been included in prior releases.  By performing these checks we prevent the situation
     * where a change to a type impacts the meaning of an old version of a function catalog.</p>
     *
     * @param mostType The type to be validated.
     * @return A list of validation error messages.
     */
    public List<String> checkTypeForIllegalChanges(final MostType mostType) throws DatabaseException {
        final MostType storedType = _getStoredType(mostType.getId());

        if (!storedType.isReleased()) {
            // stored type is not released, all changes permitted
            return new ArrayList<>();
        }

        final List<String> errors = new ArrayList<>();

        switch (storedType.getPrimitiveType().getName()) {
            case "TArray": {
                _checkArray(errors, mostType, storedType);
            } break;
            case "TBitField": {
                _checkBitField(errors, mostType, storedType);
            } break;
            case "TBool": {
                _checkBool(errors, mostType, storedType);
            } break;
            case "TCStream": {
                _checkClassifiedStream(errors, mostType, storedType);
            } break;
            case "TEnum": {
                _checkEnum(errors, mostType, storedType);
            } break;
            case "TNumber": {
                _checkNumber(errors, mostType, storedType);
            } break;
            case "TRecord": {
                _checkRecord(errors, mostType, storedType);
            } break;
            case "TShortStream": {
                _checkShortStream(errors, mostType, storedType);
            } break;
            case "TStream": {
                _checkStream(errors, mostType, storedType);
            } break;
            case "TString": {
                _checkString(errors, mostType, storedType);
            } break;
        }
        return errors;
    }

    private void _checkArray(final List<String> errors, final MostType mostType, final MostType storedType) {
        // name can change
        // description can change
        _ifNotEqualAppendError(errors, mostType.getArrayElementType().getId(), storedType.getArrayElementType().getId(), "Array element type cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getArraySize(), storedType.getArraySize(), "Array size cannot be changed.");
    }

    private void _checkBitField(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getBitfieldLength(), storedType.getBitfieldLength(), "BitField length cannot be changed.");
        _checkBooleanFields(errors, mostType.getBooleanFields(), storedType.getBooleanFields());
    }

    private void _checkBool(final List<String> errors, final MostType mostType, final MostType storedType) {
        _checkBooleanFields(errors, mostType.getBooleanFields(), storedType.getBooleanFields());
    }

    private void _checkBooleanFields(final List<String> errors, final List<BooleanField> newBooleanFields, final List<BooleanField> storedBooleanFields) {
        if (!_ifNotEqualAppendError(errors, newBooleanFields.size(), storedBooleanFields.size(), "Number of boolean fields cannot be changed.")) {
            // have same number of boolean fields
            for (int i = 0; i < storedBooleanFields.size(); i++) {
                final BooleanField newBooleanField = newBooleanFields.get(i);
                final BooleanField storedBooleanField = storedBooleanFields.get(i);

                _ifNotEqualAppendError(errors, newBooleanField.getBitPosition(), storedBooleanField.getBitPosition(), "BitPosition " + i + " cannot be changed.");
                // true description can change
                // false description can change
            }
        }
    }

    private void _checkClassifiedStream(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getStreamMaxLength(), storedType.getStreamLength(), "Classified Stream length cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getStreamMediaType(), storedType.getStreamMediaType(), "Classified Stream media type cannot be changed.");
    }

    private void _checkEnum(final List<String> errors, final MostType mostType, final MostType storedType) {
        if (mostType.getEnumValues().size() < storedType.getEnumValues().size()) {
            errors.add("Enum values cannot be removed.");
        }
        // check that all stored values are present in order
        for (int i=0; i<storedType.getEnumValues().size(); i++) {
            final EnumValue newEnumValue = mostType.getEnumValues().get(i);
            final EnumValue storedEnumValue = storedType.getEnumValues().get(i);

            _ifNotEqualAppendError(errors, newEnumValue.getName(), storedEnumValue.getName(), "Enum value name cannot be changed.");
            _ifNotEqualAppendError(errors, newEnumValue.getCode(), storedEnumValue.getCode(), "Enum value code cannot be changed.");
            // description can change
        }
    }

    private void _checkNumber(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getNumberBaseType().getId(), storedType.getNumberBaseType().getId(), "Number base type cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getNumberExponent(), storedType.getNumberExponent(), "Number exponent cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getNumberRangeMinimum(), storedType.getNumberRangeMinimum(), "Number range minimum cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getNumberRangeMaximum(), storedType.getNumberRangeMaximum(), "Number range maximum cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getNumberStep(), storedType.getNumberStep(), "Number step cannot be changed.");
        _ifNotEqualAppendError(errors, mostType.getNumberUnit().getId(), storedType.getNumberUnit().getId(), "Number unit cannot be changed.");
    }

    private void _checkRecord(final List<String> errors, final MostType mostType, final MostType storedType) {
        // name can change
        // description can change
        _ifNotEqualAppendError(errors, mostType.getRecordSize(), mostType.getRecordSize(), "Record size cannot be changed.");
        if (!_ifNotEqualAppendError(errors, mostType.getRecordFields().size(), storedType.getRecordFields().size(), "Number of record fields cannot be changed.")) {
            // have same number of record fields
            for (int i=0; i<storedType.getRecordFields().size(); i++) {
                final RecordField newRecordField = mostType.getRecordFields().get(i);
                final RecordField storedRecordField = storedType.getRecordFields().get(i);

                // name can change
                // description can change
                _ifNotEqualAppendError(errors, newRecordField.getFieldIndex(), storedRecordField.getFieldIndex(), "Record field " + i + " index cannot be changed.");
                _ifNotEqualAppendError(errors, newRecordField.getFieldType().getId(), storedRecordField.getFieldType().getId(), "Record field " + i + " type cannot be changed.");
            }
        }
    }

    private void _checkShortStream(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getStreamLength(), storedType.getStreamLength(), "Short Stream length cannot be changed.");
    }

    private void _checkStream(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getStreamLength(), storedType.getStreamLength(), "Stream length cannot be changed.");

        if (!_ifNotEqualAppendError(errors, mostType.getStreamCases().size(), mostType.getStreamCases().size(), "Number of stream cases cannot be changed.")) {
            for (int i=0; i<storedType.getStreamCases().size(); i++) {
                final StreamCase newStreamCase = mostType.getStreamCases().get(i);
                final StreamCase storedStreamCase = storedType.getStreamCases().get(i);

                _ifNotEqualAppendError(errors, newStreamCase.getStreamPositionX(), storedStreamCase.getStreamPositionX(), "Stream case " + i + " position X cannot be changed.");
                _ifNotEqualAppendError(errors, newStreamCase.getStreamPositionY(), storedStreamCase.getStreamPositionY(), "Stream case " + i + " position Y cannot be changed.");

                _checkStreamCaseParameters(errors, newStreamCase.getStreamCaseParameters(), storedStreamCase.getStreamCaseParameters());
                _checkStreamCaseSignals(errors, newStreamCase.getStreamCaseSignals(), storedStreamCase.getStreamCaseSignals());
            }
        }
    }

    private void _checkStreamCaseParameters(final List<String> errors, final List<StreamCaseParameter> newStreamCaseParameters, final List<StreamCaseParameter> storedStreamCaseParameters) {
        if (!_ifNotEqualAppendError(errors, newStreamCaseParameters.size(), storedStreamCaseParameters.size(), "Number of stream case parameters cannot be changed.")) {
            for (int i=0; i<storedStreamCaseParameters.size(); i++) {
                final StreamCaseParameter newStreamCaseParameter = newStreamCaseParameters.get(i);
                final StreamCaseParameter storedStreamCaseParameter = storedStreamCaseParameters.get(i);

                // name can change
                // description can change
                _ifNotEqualAppendError(errors, newStreamCaseParameter.getParameterIndex(), storedStreamCaseParameter.getParameterIndex(), "Stream case parameter index cannot be changed.");
                _ifNotEqualAppendError(errors, newStreamCaseParameter.getParameterType().getId(), storedStreamCaseParameter.getParameterType().getId(), "Stream case parameter type cannot be changed.");
            }
        }
    }

    private void _checkStreamCaseSignals(final List<String> errors, final List<StreamCaseSignal> newStreamCaseSignals, final List<StreamCaseSignal> storedStreamCaseSignals) {
        if (!_ifNotEqualAppendError(errors, newStreamCaseSignals.size(), storedStreamCaseSignals.size(), "Number of stream case signals cannot be changed.")) {
            for (int i=0; i<storedStreamCaseSignals.size(); i++) {
                final StreamCaseSignal newStreamCaseSignal = newStreamCaseSignals.get(i);
                final StreamCaseSignal storedStreamCaseSignal = storedStreamCaseSignals.get(i);

                // name can change
                // description can change
                _ifNotEqualAppendError(errors, newStreamCaseSignal.getSignalIndex(), storedStreamCaseSignal.getSignalIndex(), "Stream case signal index cannot be changed.");
                _ifNotEqualAppendError(errors, newStreamCaseSignal.getSignalBitLength(), storedStreamCaseSignal.getSignalBitLength(), "Stream case signal bit length cannot be changed.");
            }
        }
    }

    private void _checkString(final List<String> errors, final MostType mostType, final MostType storedType) {
        _ifNotEqualAppendError(errors, mostType.getStringMaxSize(), storedType.getStringMaxSize(), "String max size cannot be changed.");
    }

    private MostType _getStoredType(final long mostTypeId) throws DatabaseException {
        MostTypeInflater mostTypeInflater = new MostTypeInflater(_databaseConnection);
        final MostType storedType = mostTypeInflater.inflateMostType(mostTypeId);
        return storedType;
    }

    /**
     * Returns true iff the error message was added.
     * @param errors
     * @param a
     * @param b
     * @param errorMessage
     * @param <T>
     * @return
     */
    private <T> boolean _ifNotEqualAppendError(List<String> errors, final T a, final T b, String errorMessage) {
        if (a == null && b == null) {
            // both null, no problem
            return false;
        }
        if (a == null || !a.equals(b)) {
            // if a == null, b is not (because they're not both null)
            // otherwise, equality check handles everything else
            errors.add(errorMessage);
            return true;
        }
        return false;
    }
}
