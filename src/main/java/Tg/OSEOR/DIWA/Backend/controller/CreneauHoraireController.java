package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.CreneauHoraireDTO.CreneauHoraireDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.CreneauHoraireService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/creneau")
public class CreneauHoraireController {

    private final CreneauHoraireService creneauService;

    public CreneauHoraireController(CreneauHoraireService creneauService) {
        this.creneauService = creneauService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<CreneauHoraireDTOResponse>> create(
        @Valid @RequestBody CreneauHoraireDTORequest request) {
        CreneauHoraireDTOResponse data = creneauService.create(request);
        BaseResponse<CreneauHoraireDTOResponse> response = new BaseResponse<>(201, 
            "Créneau horaire créé avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CreneauHoraireDTOResponse>>> list() {
        List<CreneauHoraireDTOResponse> data = creneauService.list();
        BaseResponse<List<CreneauHoraireDTOResponse>> response = new BaseResponse<>(200, 
            "Liste complète des créneaux récupérée", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/libres")
    public ResponseEntity<BaseResponse<List<CreneauHoraireDTOResponse>>> listLibres() {
        List<CreneauHoraireDTOResponse> data = creneauService.listLibres();
        BaseResponse<List<CreneauHoraireDTOResponse>> response = new BaseResponse<>(200, 
            "Liste des créneaux libres récupérée", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<CreneauHoraireDTOResponse>> getById(@PathVariable Long id) {
        CreneauHoraireDTOResponse data = creneauService.getById(id);
        BaseResponse<CreneauHoraireDTOResponse> response = new BaseResponse<>(200, 
            "Créneau horaire récupéré", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<CreneauHoraireDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody CreneauHoraireDTORequest request) {
        CreneauHoraireDTOResponse data = creneauService.update(id, request);
        BaseResponse<CreneauHoraireDTOResponse> response = new BaseResponse<>(200, 
            "Créneau horaire modifié avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        creneauService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Créneau horaire supprimé avec succès", null);
        return ResponseEntity.ok(response);
    }
}
