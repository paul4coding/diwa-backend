package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.OptionVehiculeDTO.OptionVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.OptionVehiculeService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/options-vehicule")
public class OptionVehiculeController {

    private final OptionVehiculeService optionVehiculeService;

    public OptionVehiculeController(OptionVehiculeService optionVehiculeService) {
        this.optionVehiculeService = optionVehiculeService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<OptionVehiculeDTOResponse>> create(
        @Valid @RequestBody OptionVehiculeDTORequest request) {
        OptionVehiculeDTOResponse data = optionVehiculeService.create(request);
        BaseResponse<OptionVehiculeDTOResponse> response = new BaseResponse<>(201, 
            "Option créée avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<OptionVehiculeDTOResponse>>> list() {
        List<OptionVehiculeDTOResponse> data = optionVehiculeService.list();
        BaseResponse<List<OptionVehiculeDTOResponse>> response = new BaseResponse<>(200, 
            "Liste de toutes les options", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<BaseResponse<List<OptionVehiculeDTOResponse>>> getByVehiculeId(@PathVariable Long vehiculeId) {
        List<OptionVehiculeDTOResponse> data = optionVehiculeService.getByVehiculeId(vehiculeId);
        BaseResponse<List<OptionVehiculeDTOResponse>> response = new BaseResponse<>(200, 
            "Options pour ce véhicule", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<OptionVehiculeDTOResponse>> getById(@PathVariable Long id) {
        OptionVehiculeDTOResponse data = optionVehiculeService.getById(id);
        BaseResponse<OptionVehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Option récupérée", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<OptionVehiculeDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody OptionVehiculeDTORequest request) {
        OptionVehiculeDTOResponse data = optionVehiculeService.update(id, request);
        BaseResponse<OptionVehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Option modifiée avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        optionVehiculeService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Option supprimée avec succès", null);
        return ResponseEntity.ok(response);
    }
}
