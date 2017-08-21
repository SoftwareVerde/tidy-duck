package com.softwareverde.mostadapter.type;

import com.softwareverde.mostadapter.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NumberType extends MostType {
    private MostType _basisDataType;
    private String _exponent;
    private String _rangeMin;
    private String _rangeMax;
    private String _step;
    private String _unit;

    public MostType getBasisDataType() {
        return _basisDataType;
    }

    public void setBasisDataType(MostType basisDataType) {
        _basisDataType = basisDataType;
    }

    public String getExponent() {
        return _exponent;
    }

    public void setExponent(String exponent) {
        _exponent = exponent;
    }

    public String getRangeMin() {
        return _rangeMin;
    }

    public void setRangeMin(String rangeMin) {
        _rangeMin = rangeMin;
    }

    public String getRangeMax() {
        return _rangeMax;
    }

    public void setRangeMax(String rangeMax) {
        _rangeMax = rangeMax;
    }

    public String getStep() {
        return _step;
    }

    public void setStep(String step) {
        _step = step;
    }

    public String getUnit() {
        return _unit;
    }

    public void setUnit(String unit) {
        _unit = unit;
    }

    @Override
    public String getTypeName() {
        return "TNumber";
    }

    @Override
    protected String getTypeRef() {
        return null;
    }

    @Override
    protected void appendChildElements(final Document document, final Element typeElement) {
        final Element basisDataTypeElement = document.createElement("BasisDataType");
        final Element baseTypeElement = _basisDataType.generateXmlElement(document);
        basisDataTypeElement.appendChild(baseTypeElement);

        final Element exponentElement = XmlUtil.createTextElement(document, "Exponent", _exponent);

        final Element rangeMinElement = (_rangeMin != null ? XmlUtil.createTextElement(document, "RangeMin", _rangeMin) : null);

        final Element rangeMaxElement = (_rangeMax != null ? XmlUtil.createTextElement(document, "RangeMax", _rangeMax) : null);

        final Element stepElement = XmlUtil.createTextElement(document, "Step", _step);

        final Element unitElement = document.createElement("Unit");
        unitElement.setAttribute("UnitRef", _unit);

        typeElement.appendChild(basisDataTypeElement);
        typeElement.appendChild(exponentElement);


        if (rangeMinElement != null) {
            typeElement.appendChild(rangeMinElement);
        }

        if (rangeMaxElement != null) {
            typeElement.appendChild(rangeMaxElement);
        }

        typeElement.appendChild(stepElement);

        typeElement.appendChild(unitElement);
    }
}
