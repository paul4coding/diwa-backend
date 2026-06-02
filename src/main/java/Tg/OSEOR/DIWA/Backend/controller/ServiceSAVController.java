package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.service.ServiceSAVService;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/servicesav")
public class ServiceSAVController {

    private final ServiceSAVService serviceSAVService;

    public ServiceSAVController(ServiceSAVService serviceSAVService) {
        this.serviceSAVService = serviceSAVService;
    }

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<ServiceSAVDTOResponse>> create(
        @Valid @RequestBody ServiceSAVDTORequest request) {
        ServiceSAVDTOResponse data = serviceSAVService.create(request);
        BaseResponse<ServiceSAVDTOResponse> response = new BaseResponse<>(201, 
            "Service créé avec succès", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<ServiceSAVDTOResponse>>> list() {
        List<ServiceSAVDTOResponse> data = serviceSAVService.list();
        BaseResponse<List<ServiceSAVDTOResponse>> response = new BaseResponse<>(200, 
            "Liste des services récupérée", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one/{id}")
    public ResponseEntity<BaseResponse<ServiceSAVDTOResponse>> getById(@PathVariable Long id) {
        ServiceSAVDTOResponse data = serviceSAVService.getById(id);
        BaseResponse<ServiceSAVDTOResponse> response = new BaseResponse<>(200, 
            "Service récupéré", data);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse<ServiceSAVDTOResponse>> update(
        @PathVariable Long id, 
        @Valid @RequestBody ServiceSAVDTORequest request) {
        ServiceSAVDTOResponse data = serviceSAVService.update(id, request);
        BaseResponse<ServiceSAVDTOResponse> response = new BaseResponse<>(200, 
            "Service modifié avec succès", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        serviceSAVService.delete(id);
        BaseResponse<Void> response = new BaseResponse<>(200, 
            "Service supprimé avec succès", null);
        return ResponseEntity.ok(response);
    }
}
