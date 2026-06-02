package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.VehiculeDTO.VehiculeDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.Favoris;
import Tg.OSEOR.DIWA.Backend.entity.Vehicule;
import Tg.OSEOR.DIWA.Backend.mappers.VehiculeMapper;
import Tg.OSEOR.DIWA.Backend.repository.FavorisRepository;
import Tg.OSEOR.DIWA.Backend.repository.VehiculeRepository;
import Tg.OSEOR.DIWA.Backend.security.model.User;
import Tg.OSEOR.DIWA.Backend.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavorisServiceImpl implements FavorisService {

    private final FavorisRepository favorisRepository;
    private final VehiculeRepository vehiculeRepository;
    private final AuthService authService;
    private final VehiculeMapper vehiculeMapper;

    @Autowired
    public FavorisServiceImpl(FavorisRepository favorisRepository, 
                              VehiculeRepository vehiculeRepository, 
                              AuthService authService, 
                              VehiculeMapper vehiculeMapper) {
        this.favorisRepository = favorisRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.authService = authService;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    @Transactional
    public void toggleFavoris(Long vehiculeId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Vous devez être connecté pour gérer vos favoris");
        }
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new RuntimeException("Véhicule non trouvé"));

        if (favorisRepository.existsByUser_IdAndVehicule_Id(currentUser.getId(), vehiculeId)) {
            favorisRepository.deleteByUser_IdAndVehicule_Id(currentUser.getId(), vehiculeId);
        } else {
            Favoris favoris = new Favoris();
            favoris.setUser(currentUser);
            favoris.setVehicule(vehicule);
            favorisRepository.save(favoris);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTOResponse> getMyFavoris() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return new java.util.ArrayList<>();
        }
        return favorisRepository.findByUser_Id(currentUser.getId()).stream()
                .map(f -> vehiculeMapper.toResponse(f.getVehicule()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavoris(Long vehiculeId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) return false;
        return favorisRepository.existsByUser_IdAndVehicule_Id(currentUser.getId(), vehiculeId);
    }
}
