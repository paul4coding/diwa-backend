package Tg.OSEOR.DIWA.Backend.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ConfigurationVehiculeDTO.ConfigurationVehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.ConfigurationVehiculeService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/configuration")
public class ConfigurationVehiculeController {

    private final ConfigurationVehiculeService configService;

    public ConfigurationVehiculeController(ConfigurationVehiculeService configService) {
        this.configService = configService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<ConfigurationVehiculeDTOResponse>> create(
        @Valid @RequestBody ConfigurationVehiculeDTORequest request, 
        Principal principal) {
        
        String email = principal != null ? principal.getName() : "anonymous@diva.com"; // Just in case security allows anonymous creating for some reason. Ideally, @PreAuthorize handles it.
        ConfigurationVehiculeDTOResponse data = configService.create(request, email);
        BaseResponse<ConfigurationVehiculeDTOResponse> response = new BaseResponse<>(201, 
            "Configuration sauvegardée avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-configs")
    public ResponseEntity<BaseResponse<List<ConfigurationVehiculeDTOResponse>>> getMyConfigs(Principal principal) {
        String email = principal != null ? principal.getName() : "";
        List<ConfigurationVehiculeDTOResponse> data = configService.getMyConfigurations(email);
        BaseResponse<List<ConfigurationVehiculeDTOResponse>> response = new BaseResponse<>(200, 
            "Historique de vos configurations", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<ConfigurationVehiculeDTOResponse>> getById(@PathVariable Long id) {
        ConfigurationVehiculeDTOResponse data = configService.getById(id);
        BaseResponse<ConfigurationVehiculeDTOResponse> response = new BaseResponse<>(200, 
            "Configuration récupérée", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id, Principal principal) {
        String email = principal != null ? principal.getName() : "";
        configService.delete(id, email);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Configuration supprimée avec succès", null);
        return ResponseEntity.ok(response);
    }
}
