package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.TechnicienDTO.TechnicienDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.TechnicienService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/techniciens")
public class TechnicienController {

    private final TechnicienService technicienService;

    public TechnicienController(TechnicienService technicienService) {
        this.technicienService = technicienService;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<TechnicienDTOResponse>> create(
        @Valid @RequestBody TechnicienDTORequest request) {
        TechnicienDTOResponse data = technicienService.create(request);
        BaseResponse<TechnicienDTOResponse> response = new BaseResponse<>(201, 
            "Technicien créé avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<TechnicienDTOResponse>>> list() {
        List<TechnicienDTOResponse> data = technicienService.list();
        BaseResponse<List<TechnicienDTOResponse>> response = new BaseResponse<>(200, 
            "Liste des techniciens récupérée", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<TechnicienDTOResponse>> getById(@PathVariable Long id) {
        TechnicienDTOResponse data = technicienService.getById(id);
        BaseResponse<TechnicienDTOResponse> response = new BaseResponse<>(200, 
            "Technicien récupéré", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<TechnicienDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody TechnicienDTORequest request) {
        TechnicienDTOResponse data = technicienService.update(id, request);
        BaseResponse<TechnicienDTOResponse> response = new BaseResponse<>(200, 
            "Technicien modifié avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        technicienService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Technicien supprimé avec succès", null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/actif/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleActif(@PathVariable Long id) {
        technicienService.toggleActif(id);
        return ResponseEntity.ok().build();
    }
}
