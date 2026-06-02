package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.entity.PlageHoraireDIWA;
import Tg.OSEOR.DIWA.Backend.entity.ExceptionPlage;
import Tg.OSEOR.DIWA.Backend.service.CreneauService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/creneaux")
public class CreneauController {

    private final CreneauService creneauService;

    public CreneauController(CreneauService creneauService) {
        this.creneauService = creneauService;
    }

    @GetMapping("/disponibles")
    public ResponseEntity<Map<String, Object>> getDisponibilites(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(creneauService.getDisponibilites(date));
    }

    @GetMapping("/plages")
    public ResponseEntity<List<PlageHoraireDIWA>> getAllPlages() {
        return ResponseEntity.ok(creneauService.getAllPlages());
    }

    @PostMapping("/plages")
    public ResponseEntity<PlageHoraireDIWA> createPlage(@RequestBody PlageHoraireDIWA plage) {
        return ResponseEntity.ok(creneauService.createPlage(plage));
    }

    @DeleteMapping("/plages/{id}")
    public ResponseEntity<Void> deletePlage(@PathVariable Long id) {
        creneauService.deletePlage(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exceptions")
    public ResponseEntity<ExceptionPlage> createException(@RequestBody ExceptionPlage ex) {
        return ResponseEntity.ok(creneauService.createException(ex));
    }
}
