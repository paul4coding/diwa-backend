package Tg.OSEOR.DIWA.Backend.service;

/**
 * Service de gestion des factures finales (Sprint 6).
 * Une facture est générée automatiquement à la CLOTURE du dossier.
 */
public interface FactureService {

    /**
     * Génère (ou régénère) la facture pour la demande donnée.
     * Stocke le PDF sur disque, met à jour ProForma.factureUrl et envoie l'email.
     *
     * @param demandeId ID de la DemandeIntervention (statut CLOTURE attendu)
     * @return bytes du PDF généré
     */
    byte[] generer(Long demandeId);

    /**
     * Télécharge la facture existante, ou la régénère si absente.
     * Vérifie que l'utilisateur y a accès (propriétaire, réceptionniste ou admin).
     *
     * @param demandeId  ID de la demande
     * @param identifier email ou username de l'utilisateur
     * @return bytes du PDF
     */
    byte[] telecharger(Long demandeId, String identifier);
}
