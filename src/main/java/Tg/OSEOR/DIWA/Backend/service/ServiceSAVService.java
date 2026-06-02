package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.ServiceSAVDTO.ServiceSAVDTOResponse;

public interface ServiceSAVService {
    ServiceSAVDTOResponse create(ServiceSAVDTORequest request);
    ServiceSAVDTOResponse update(Long id, ServiceSAVDTORequest request);
    void delete(Long id);
    List<ServiceSAVDTOResponse> list();
    ServiceSAVDTOResponse getById(Long id);
}
