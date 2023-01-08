package koreatech.in.service;

import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.dto.admin.land.request.CreateLandRequest;
import koreatech.in.dto.admin.land.request.LandsCondition;
import koreatech.in.dto.admin.land.request.UpdateLandRequest;
import koreatech.in.dto.admin.land.response.LandsResponse;
import koreatech.in.dto.admin.land.response.LandResponse;

import java.util.Map;

public interface LandService {
    void createLandForAdmin(CreateLandRequest request) throws Exception;

    LandResponse getLandForAdmin(Integer landId) throws Exception;

    LandsResponse getLandsForAdmin(LandsCondition condition) throws Exception;

    void updateLandForAdmin(UpdateLandRequest request, Integer landId) throws Exception;

    void deleteLandForAdmin(Integer landId) throws Exception;

    void undeleteLandForAdmin(Integer landId) throws Exception;

    Map<String, Object> getLands() throws Exception;

    Map<String, Object> getLand(int id) throws Exception;

    LandComment createLandComment(LandComment landComment, int land_id) throws Exception;

    LandComment updateLandComment(LandComment landComment, int land_id) throws Exception;

    Map<String, Object> deleteLandComment(int land_id) throws Exception;
}
