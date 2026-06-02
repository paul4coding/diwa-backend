package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.CategoriePieceDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.CategoriePieceService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/categories-pieces")
public class CategoriePieceController {

    private final CategoriePieceService service;

    public CategoriePieceController(CategoriePieceService service) {
        this.service = service;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CategoriePieceDTOResponse>> create(@Valid @RequestBody CategoriePieceDTORequest request) {
        CategoriePieceDTOResponse data = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new BaseResponse<>(201, "Catégorie créée", data));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CategoriePieceDTOResponse>>> list() {
        List<CategoriePieceDTOResponse> data = service.list();
        return ResponseEntity.ok(new BaseResponse<>(200, "Liste des catégories de pièces", data));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CategoriePieceDTOResponse>> update(@PathVariable Long id, @Valid @RequestBody CategoriePieceDTORequest request) {
        CategoriePieceDTOResponse data = service.update(id, request);
        return ResponseEntity.ok(new BaseResponse<>(200, "Catégorie modifiée", data));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new BaseResponse<>(200, "Catégorie supprimée", null));
    }
}
