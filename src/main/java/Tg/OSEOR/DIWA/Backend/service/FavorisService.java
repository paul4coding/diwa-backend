package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import java.util.List;

public interface FavorisService {
    void toggleFavoris(Long vehiculeId);
    List<VehiculeDTOResponse> getMyFavoris();
    boolean isFavoris(Long vehiculeId);
}
