package template_data;

import de.arinir.mdsd.metamodell.MDSDMetamodell.MultiplicityT;

public record AssociationsDataModel(String fkEntity, String fkName, MultiplicityT fkMultiplicity, String referencedEntity, String referencedName, MultiplicityT referencedMultiplicity) {

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
