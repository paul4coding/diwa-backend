package Tg.OSEOR.DIWA.Backend.controller;

import Tg.OSEOR.DIWA.Backend.entity.CMSContent;
import Tg.OSEOR.DIWA.Backend.repository.CMSContentRepository;
import Tg.OSEOR.DIWA.Backend.utils.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
@CrossOrigin("*")
public class CMSController {

    private final CMSContentRepository repository;

    public CMSController(CMSContentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<List<CMSContent>>> getAll() {
        return ResponseEntity.ok(new BaseResponse<>(200, "Contenu CMS récupéré", repository.findAll()));
    }

    @GetMapping("/{key}")
    public ResponseEntity<BaseResponse<CMSContent>> getByKey(@PathVariable String key) {
        return repository.findById(key)
                .map(content -> ResponseEntity.ok(new BaseResponse<>(200, "Contenu trouvé", content)))
                .orElse(ResponseEntity.ok(new BaseResponse<>(404, "Contenu non trouvé", null)));
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<CMSContent>> save(@RequestBody CMSContent content) {
        CMSContent saved = repository.save(content);
        return ResponseEntity.ok(new BaseResponse<>(200, "Contenu CMS enregistré avec succès", saved));
    }
}
