package koreatech.in.service;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.dto.land.admin.response.LandResponse;

import java.util.Map;

public interface LandService {
    Map<String, Object> getLands() throws Exception;

    Map<String, Object> getLand(int id) throws Exception;

    LandComment createLandComment(LandComment landComment, int land_id) throws Exception;

    LandComment updateLandComment(LandComment landComment, int land_id) throws Exception;

    Map<String, Object> deleteLandComment(int land_id) throws Exception;

    Land createLandForAdmin(Land land) throws Exception;

    LandResponse getLandForAdmin(Integer landId) throws Exception;

    Land updateLandForAdmin(Land land, int id) throws Exception;

    Map<String, Object> deleteLandForAdmin(int id) throws Exception;

}
