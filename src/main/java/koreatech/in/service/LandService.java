package koreatech.in.service;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.land.admin.request.LandsCondition;
import koreatech.in.dto.land.admin.response.LandResponse;
import koreatech.in.dto.land.admin.response.LandsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LandService {
    Map<String, Object> getLands() throws Exception;

    Map<String, Object> getLand(int id) throws Exception;

    LandComment createLandComment(LandComment landComment, int land_id) throws Exception;

    LandComment updateLandComment(LandComment landComment, int land_id) throws Exception;

    Map<String, Object> deleteLandComment(int land_id) throws Exception;

    Land createLandForAdmin(Land land) throws Exception;

    LandResponse getLandForAdmin(Integer landId) throws Exception;

    LandsResponse getLandsForAdmin(LandsCondition condition) throws Exception;

    Land updateLandForAdmin(Land land, int id) throws Exception;

    SuccessResponse deleteLandForAdmin(Integer landId) throws Exception;

    Map<String, Object> undeleteLandForAdmin(int id) throws Exception;

    Map<String, Object> uploadImages(List<MultipartFile> images) throws Exception;
}
