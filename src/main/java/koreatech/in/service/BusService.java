package koreatech.in.service;

import java.util.Map;


public interface BusService {
    public Map<String, Object> getBus (String depart, String arrival) throws Exception;
}
