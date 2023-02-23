package template_data;

/**
 * Record that holds information for the JPARepository creation.
 *
 * @param repositoryName name of JPARepository
 *
 * @author Tom Schmanke
 * @version 1.0 initial creation
 */
public record RepositoryModel(String repositoryName) {

    public String getRepositoryName() {
        return repositoryName + "Repository";
    }

    public String getEntityName() {
        return repositoryName;
    }

}
