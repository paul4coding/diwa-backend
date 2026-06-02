package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.ServiceSAV;
import Tg.OSEOR.DIWA.Backend.mappers.ServiceSAVMapper;
import Tg.OSEOR.DIWA.Backend.repository.ServiceSAVRepository;

@Service
@Transactional
public class ServiceSAVServiceImpl implements ServiceSAVService {

    private final ServiceSAVRepository repo;
    private final ServiceSAVMapper mapper;

    public ServiceSAVServiceImpl(ServiceSAVRepository repo, ServiceSAVMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public ServiceSAVDTOResponse create(ServiceSAVDTORequest request) {
        ServiceSAV service = mapper.toEntity(request);
        return mapper.toResponse(repo.save(service));
    }

    @Override
    public ServiceSAVDTOResponse update(Long id, ServiceSAVDTORequest request) {
        ServiceSAV service = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("ServiceSAV non trouvé: " + id));
        mapper.updateEntityFromRequest(request, service);
        return mapper.toResponse(repo.save(service));
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("ServiceSAV non trouvé: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public List<ServiceSAVDTOResponse> list() {
        return repo.findAll().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ServiceSAVDTOResponse getById(Long id) {
        return repo.findById(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new RuntimeException("ServiceSAV non trouvé: " + id));
    }
}
