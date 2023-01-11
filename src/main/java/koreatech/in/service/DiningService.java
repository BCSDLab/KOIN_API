package koreatech.in.service;

import koreatech.in.domain.Dining.DiningMenuDTO;

import java.util.List;

public interface DiningService {
    List<DiningMenuDTO> getDinings(String date) throws Exception;
}
