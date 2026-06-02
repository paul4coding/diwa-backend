package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.MotorisationDTO.MotorisationDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.MotorisationService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/motorisations")
public class MotorisationController {

    private final MotorisationService motorisationService;

    public MotorisationController(MotorisationService motorisationService) {
        this.motorisationService = motorisationService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MotorisationDTOResponse>> create(
        @Valid @RequestBody MotorisationDTORequest request) {
        MotorisationDTOResponse data = motorisationService.create(request);
        BaseResponse<MotorisationDTOResponse> response = new BaseResponse<>(201, 
            "Motorisation créée avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<MotorisationDTOResponse>>> list() {
        List<MotorisationDTOResponse> data = motorisationService.list();
        BaseResponse<List<MotorisationDTOResponse>> response = new BaseResponse<>(200, 
            "Liste de toutes les motorisations", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<BaseResponse<List<MotorisationDTOResponse>>> getByVehiculeId(@PathVariable Long vehiculeId) {
        List<MotorisationDTOResponse> data = motorisationService.getByVehiculeId(vehiculeId);
        BaseResponse<List<MotorisationDTOResponse>> response = new BaseResponse<>(200, 
            "Motorisations pour ce véhicule", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<MotorisationDTOResponse>> getById(@PathVariable Long id) {
        MotorisationDTOResponse data = motorisationService.getById(id);
        BaseResponse<MotorisationDTOResponse> response = new BaseResponse<>(200, 
            "Motorisation récupérée", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<MotorisationDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody MotorisationDTORequest request) {
        MotorisationDTOResponse data = motorisationService.update(id, request);
        BaseResponse<MotorisationDTOResponse> response = new BaseResponse<>(200, 
            "Motorisation modifiée avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        motorisationService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Motorisation supprimée avec succès", null);
        return ResponseEntity.ok(response);
    }
}
