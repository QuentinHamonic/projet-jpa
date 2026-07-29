package fr.diginamic.dao;

import fr.diginamic.entities.LieuNaissance;
import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Accès aux données des lieux de naissance.
 */
public class LieuNaissanceDao extends GenericDao<LieuNaissance, Long> {

    public LieuNaissanceDao(EntityManager entityManager) {
        super(entityManager, LieuNaissance.class);
    }

    /**
     * Recherche un lieu de naissance par son libellé.
     *
     * @param libelle libellé recherché
     * @return lieu éventuellement trouvé
     */
    public Optional<LieuNaissance> findByLibelle(String libelle) {
        return entityManager.createQuery(
                        "SELECT l FROM LieuNaissance l WHERE l.libelle = :libelle",
                        LieuNaissance.class)
                .setParameter("libelle", libelle)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }
}
