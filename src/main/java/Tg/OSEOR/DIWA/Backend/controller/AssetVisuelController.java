package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.AssetVisuelService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/assetvisuel")
public class AssetVisuelController {

    private final AssetVisuelService assetService;

    public AssetVisuelController(AssetVisuelService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<AssetVisuelDTOResponse>> create(
        @Valid @RequestBody AssetVisuelDTORequest request) {
        AssetVisuelDTOResponse data = assetService.create(request);
        BaseResponse<AssetVisuelDTOResponse> response = new BaseResponse<>(201, 
            "Asset Visuel créé avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<AssetVisuelDTOResponse>>> list() {
        List<AssetVisuelDTOResponse> data = assetService.list();
        BaseResponse<List<AssetVisuelDTOResponse>> response = new BaseResponse<>(200, 
            "Liste complète des assets visuels", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/option/{optionId}")
    public ResponseEntity<BaseResponse<List<AssetVisuelDTOResponse>>> getByOptionId(@PathVariable Long optionId) {
        List<AssetVisuelDTOResponse> data = assetService.getByOptionId(optionId);
        BaseResponse<List<AssetVisuelDTOResponse>> response = new BaseResponse<>(200, 
            "Assets visuels pour cette option", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<AssetVisuelDTOResponse>> getById(@PathVariable Long id) {
        AssetVisuelDTOResponse data = assetService.getById(id);
        BaseResponse<AssetVisuelDTOResponse> response = new BaseResponse<>(200, 
            "Asset visuel récupéré", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<AssetVisuelDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody AssetVisuelDTORequest request) {
        AssetVisuelDTOResponse data = assetService.update(id, request);
        BaseResponse<AssetVisuelDTOResponse> response = new BaseResponse<>(200, 
            "Asset Visuel modifié avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        assetService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Asset Visuel supprimé avec succès", null);
        return ResponseEntity.ok(response);
    }
}
