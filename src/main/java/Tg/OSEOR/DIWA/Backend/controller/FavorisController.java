package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.FavorisService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoris")
@CrossOrigin(origins = "*")
public class FavorisController {

    private final FavorisService favorisService;

    @Autowired
    public FavorisController(FavorisService favorisService) {
        this.favorisService = favorisService;
    }

    @PostMapping("/toggle/{vehiculeId}")
    public ResponseEntity<BaseResponse<String>> toggleFavoris(@PathVariable Long vehiculeId) {
        favorisService.toggleFavoris(vehiculeId);
        return ResponseEntity.ok(new BaseResponse<>(200, "Favori mis à jour", null));
    }

    @GetMapping("/my-list")
    public ResponseEntity<BaseResponse<List<VehiculeDTOResponse>>> getMyFavoris() {
        return ResponseEntity.ok(new BaseResponse<>(200, "Liste des favoris récupérée", favorisService.getMyFavoris()));
    }

    @GetMapping("/status/{vehiculeId}")
    public ResponseEntity<BaseResponse<Boolean>> isFavoris(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(new BaseResponse<>(200, "Status récupéré", favorisService.isFavoris(vehiculeId)));
    }
}
