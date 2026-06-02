package Tg.OSEOR.DIWA.Backend.service;

import java.util.List;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTORequest;
import Tg.OSEOR.DIWA.Backend.dto.AssetVisuelDTO.AssetVisuelDTOResponse;

public interface AssetVisuelService {
    AssetVisuelDTOResponse create(AssetVisuelDTORequest request);
    AssetVisuelDTOResponse update(Long id, AssetVisuelDTORequest request);
    void delete(Long id);
    List<AssetVisuelDTOResponse> list();
    AssetVisuelDTOResponse getById(Long id);
    List<AssetVisuelDTOResponse> getByOptionId(Long optionId);
}
