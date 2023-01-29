package template_data;

public record RepositoryModel(String repositoryName) {

    public String getRepositoryName() {
        return repositoryName + "Repository";
    }

    public String getEntityName() {
        return repositoryName;
    }

}
