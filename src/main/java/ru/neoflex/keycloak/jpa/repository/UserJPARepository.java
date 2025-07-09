package ru.neoflex.keycloak.jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import ru.neoflex.keycloak.jpa.entity.ExteranalUser;

public class UserJPARepository extends AbstractJpaRepository<ExteranalUser, String> {

    public UserJPARepository(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<ExteranalUser> getEntityClass() {
        return ExteranalUser.class;
    }


    public ExteranalUser getUserByUsername(String username) {
        TypedQuery<ExteranalUser> query = entityManager.createQuery(
                "SELECT entity FROM ExteranalUser entity "
                        + "WHERE entity.username = :username",
                getEntityClass()
        );
        query.setParameter("username", username);
        return getSingleResultFromQueryOrNull(query);
    }


    public ExteranalUser getUserByEmail(String email) {
        TypedQuery<ExteranalUser> query = entityManager.createQuery(
                "SELECT entity FROM ExteranalUser entity "
                        + "WHERE entity.email = LOWER(:email)",
                getEntityClass()
        );
        query.setParameter("email", email);
        return getSingleResultFromQueryOrNull(query);
    }
}
