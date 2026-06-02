package Tg.OSEOR.DIWA.Backend.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import Tg.OSEOR.DIWA.Backend.entity.PlageHoraire;
import Tg.OSEOR.DIWA.Backend.entity.Technicien;
import Tg.OSEOR.DIWA.Backend.repository.PlageHoraireRepository;
import Tg.OSEOR.DIWA.Backend.repository.TechnicienRepository;

@Service
@Transactional
public class PlageHoraireServiceImpl implements PlageHoraireService {

    private final PlageHoraireRepository plageHoraireRepository;
    private final TechnicienRepository technicienRepository;

    @Autowired
    public PlageHoraireServiceImpl(PlageHoraireRepository plageHoraireRepository, 
                                   TechnicienRepository technicienRepository) {
        this.plageHoraireRepository = plageHoraireRepository;
        this.technicienRepository = technicienRepository;
    }

    @Override
    public void initPlanningDefault(Long techId) {
        Technicien technicien = technicienRepository.findById(techId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Technicien introuvable"));

        // Supprimer les anciennes plages
        plageHoraireRepository.deleteByTechnicienId(techId);

        // Créneaux : 8h, 9h, 10h, 11h, 12h, 13h, 14h, 15h, 16h, 17h → 10 créneaux/jour
        DayOfWeek[] jours = {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };

        List<PlageHoraire> plages = new ArrayList<>();
        for (DayOfWeek jour : jours) {
            for (int heure = 8; heure < 18; heure++) {
                PlageHoraire plage = new PlageHoraire(
                        jour,
                        LocalTime.of(heure, 0),
                        LocalTime.of(heure + 1, 0),
                        technicien
                );
                plages.add(plage);
            }
        }

        plageHoraireRepository.saveAll(plages);
    }
}
