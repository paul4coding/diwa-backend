package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.PieceDetacheeDTO.PieceDetacheeDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.PieceDetacheeService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;

@RestController
@RequestMapping("api/v1/pieces-detachees")
public class PieceDetacheeController {

    private final PieceDetacheeService service;

    public PieceDetacheeController(PieceDetacheeService service) {
        this.service = service;
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PieceDetacheeDTOResponse>> create(@Valid @RequestBody PieceDetacheeDTORequest request) {
        PieceDetacheeDTOResponse data = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new BaseResponse<>(201, "Pièce détachée créée", data));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<PieceDetacheeDTOResponse>>> listAll() {
        List<PieceDetacheeDTOResponse> data = service.listAll();
        return ResponseEntity.ok(new BaseResponse<>(200, "Catalogue complet", data));
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<PieceDetacheeDTOResponse>> getById(@PathVariable Long id) {
        PieceDetacheeDTOResponse data = service.getById(id);
        return ResponseEntity.ok(new BaseResponse<>(200, "Détail de la pièce", data));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BaseResponse<List<PieceDetacheeDTOResponse>>> listByCategory(@PathVariable Long categoryId) {
        List<PieceDetacheeDTOResponse> data = service.listByCategory(categoryId);
        return ResponseEntity.ok(new BaseResponse<>(200, "Pièces de la catégorie", data));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<PieceDetacheeDTOResponse>>> search(@RequestParam("q") String keyword) {
        List<PieceDetacheeDTOResponse> data = service.search(keyword);
        return ResponseEntity.ok(new BaseResponse<>(200, "Résultats de la recherche", data));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PieceDetacheeDTOResponse>> update(@PathVariable Long id, @Valid @RequestBody PieceDetacheeDTORequest request) {
        PieceDetacheeDTOResponse data = service.update(id, request);
        return ResponseEntity.ok(new BaseResponse<>(200, "Pièce modifiée", data));
    }

    @PutMapping("/stock/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PieceDetacheeDTOResponse>> adjustStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer delta = body.get("delta");
        PieceDetacheeDTOResponse data = service.adjustStock(id, delta);
        return ResponseEntity.ok(new BaseResponse<>(200, "Stock mis à jour", data));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new BaseResponse<>(200, "Pièce supprimée", null));
    }
}
