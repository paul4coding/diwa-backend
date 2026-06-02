package Tg.OSEOR.DIWA.Backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import Tg.OSEOR.DIWA.Backend.dto.CreneauDTO;
import Tg.OSEOR.DIWA.Backend.dto.GlobalPlanningDTO;
import Tg.OSEOR.DIWA.Backend.entity.RendezVous;

public interface GarageService {

    /**
     * Retourne les créneaux disponibles pour une date et un type de service donnés.
     */
    List<CreneauDTO> getDisponibilites(LocalDate date, String typeService);

    /**
     * Crée un rendez-vous de manière atomique et marque le créneau comme indisponible.
     */
    RendezVous createRendezVous(Long plageHoraireId, Long userId, Long serviceId, String typeIntervention, LocalDate date);

    /**
     * Retourne le planning d'un technicien pour une semaine donnée.
     */
    Map<DayOfWeek, List<RendezVous>> getPlanningTechnicien(Long techId, LocalDate semaine);


    /**
     * Met à jour le statut d'un rendez-vous.
     */
    RendezVous updateStatutRdv(Long rdvId, String statut);

    /**
     * Retourne tous les rendez-vous d'un utilisateur.
     */
    List<RendezVous> getMesRendezVous(Long userId);

    GlobalPlanningDTO getGlobalPlanning(LocalDate debut, LocalDate fin);

    void assignTicketToTechnician(Long ticketId, Long techId);
}
