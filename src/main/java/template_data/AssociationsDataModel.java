package template_data;

public record AssociationsDataModel(String fkEntity, String fkName, Multiplicity fkMultiplicity, String referencedEntity, String referencedName, Multiplicity referencedMultiplicity) {

    public String getFKEntity() {
        return fkEntity;
    }

    public String getFKName() {
        return fkName;
    }

    public String getReferencedEntity() {
        return referencedEntity;
    }

    public String getReferencedName() {
        return referencedName;
    }

    public String getMappedByKey() {
        return referencedName.substring(0, 1).toLowerCase() + referencedName.substring(1) ;
    }

    public String getFKKey() {
        return "FK_" + referencedName();
    }
}
