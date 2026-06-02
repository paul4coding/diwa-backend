package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeConfigurationDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.VehiculeService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> create(
        @Valid @RequestBody VehiculeDTORequest request) {
        VehiculeDTOResponse data = vehiculeService.create(request);
        BaseResponse<VehiculeDTOResponse> response = new BaseResponse<>(201, 
            "Véhicule créé avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<VehiculeDTOResponse>>> list() {
        List<VehiculeDTOResponse> data = vehiculeService.list();
        BaseResponse<List<VehiculeDTOResponse>> response = new BaseResponse<>(200, 
            "Liste de tous les véhicules", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> getById(@PathVariable Long id) {
        VehiculeDTOResponse data = vehiculeService.getById(id);
        BaseResponse<VehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Véhicule récupéré", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/marque/{marque}")
    public ResponseEntity<BaseResponse<List<VehiculeDTOResponse>>> getByMarque(@PathVariable String marque) {
        List<VehiculeDTOResponse> data = vehiculeService.findByMarque(marque);
        BaseResponse<List<VehiculeDTOResponse>> response = new BaseResponse<>(200, 
            "Véhicules de la marque " + marque, data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> getByUuid(@PathVariable String uuid) {
        VehiculeDTOResponse data = vehiculeService.getByUuid(uuid);
        BaseResponse<VehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Véhicule récupéré par UUID", data);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/configuration-data")
    public ResponseEntity<BaseResponse<VehiculeConfigurationDTOResponse>> getConfigurationData(@PathVariable Long id) {
        VehiculeConfigurationDTOResponse data = vehiculeService.getConfigurationData(id);
        BaseResponse<VehiculeConfigurationDTOResponse> response = new BaseResponse<>(200, 
            "Données complètes du configurateur pour ce véhicule", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody VehiculeDTORequest request) {
        VehiculeDTOResponse data = vehiculeService.update(id, request);
        BaseResponse<VehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Véhicule modifié avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        vehiculeService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Véhicule supprimé avec succès", null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/glb")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> updateGlb(
        @PathVariable Long id, 
        @RequestBody Map<String, String> body) {
        String glbPath = body.get("glbPath");
        VehiculeDTOResponse data = vehiculeService.updateGlb(id, glbPath);
        return ResponseEntity.ok(new BaseResponse<>(200, "Modèle 3D mis à jour", data));
    }

    @PutMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<VehiculeDTOResponse>> updateImage(
        @PathVariable Long id, 
        @RequestBody Map<String, String> body) {
        String imagePath = body.get("imagePath");
        VehiculeDTOResponse data = vehiculeService.updateImage(id, imagePath);
        return ResponseEntity.ok(new BaseResponse<>(200, "Image principale mise à jour", data));
    }
}
