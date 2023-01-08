package template_data;

public record AttributeDataModel(String attributeName, String attributeType, Multiplicity attributeMultiplicity) {

    public String getAttributeName() {
        return attributeName;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public Multiplicity getAttributeMultiplicity() {
        return attributeMultiplicity;
    }

    public String getTypeAndName() {
        return attributeType + " " + attributeName;
    }

    public String getConstructorAssignment() {
        return "this." + attributeName + " = " + attributeName + ";";
    }
}
