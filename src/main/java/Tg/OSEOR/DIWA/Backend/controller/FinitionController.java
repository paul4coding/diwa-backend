package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.FinitionDTO.FinitionDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.FinitionService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/finitions")
public class FinitionController {

    private final FinitionService finitionService;

    public FinitionController(FinitionService finitionService) {
        this.finitionService = finitionService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<FinitionDTOResponse>> create(
        @Valid @RequestBody FinitionDTORequest request) {
        FinitionDTOResponse data = finitionService.create(request);
        BaseResponse<FinitionDTOResponse> response = new BaseResponse<>(201, 
            "Finition créée avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<FinitionDTOResponse>>> list() {
        List<FinitionDTOResponse> data = finitionService.list();
        BaseResponse<List<FinitionDTOResponse>> response = new BaseResponse<>(200, 
            "Liste de toutes les finitions", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<BaseResponse<List<FinitionDTOResponse>>> getByVehiculeId(@PathVariable Long vehiculeId) {
        List<FinitionDTOResponse> data = finitionService.getByVehiculeId(vehiculeId);
        BaseResponse<List<FinitionDTOResponse>> response = new BaseResponse<>(200, 
            "Finitions pour ce véhicule", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<FinitionDTOResponse>> getById(@PathVariable Long id) {
        FinitionDTOResponse data = finitionService.getById(id);
        BaseResponse<FinitionDTOResponse> response = new BaseResponse<>(200, 
            "Finition récupérée", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<FinitionDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody FinitionDTORequest request) {
        FinitionDTOResponse data = finitionService.update(id, request);
        BaseResponse<FinitionDTOResponse> response = new BaseResponse<>(200, 
            "Finition modifiée avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        finitionService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Finition supprimée avec succès", null);
        return ResponseEntity.ok(response);
    }
}
