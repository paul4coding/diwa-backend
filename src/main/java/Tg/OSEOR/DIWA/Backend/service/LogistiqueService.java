package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.dto.ChauffeurStatusDTO;
import java.util.List;

public interface LogistiqueService {
    List<ChauffeurStatusDTO> getChauffeursMonitoring();
}
