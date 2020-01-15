package koreatech.in.service;

import koreatech.in.domain.DiningMenu;

import java.util.List;
import java.util.Map;

public interface DiningService {
    List<Map<String, Object>> getDinings(String date) throws Exception;
}
