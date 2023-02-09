package koreatech.in.service;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.domain.BokDuck.LandResponseType;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.User;
import koreatech.in.dto.admin.land.request.CreateLandRequest;
import koreatech.in.dto.admin.land.request.LandsCondition;
import koreatech.in.dto.admin.land.request.UpdateLandRequest;
import koreatech.in.dto.admin.land.response.LandsResponse;
import koreatech.in.dto.admin.land.response.LandResponse;
import koreatech.in.exception.*;
import koreatech.in.mapstruct.admin.land.AdminLandConverter;
import koreatech.in.repository.LandMapper;
import koreatech.in.util.JsonConstructor;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.exception.ExceptionInformation.*;

@Service("landService")
@Transactional
public class LandServiceImpl implements LandService {
    @Resource(name="landMapper")
    private LandMapper landMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    UploadFileUtils uploadFileUtils;

    @Override
    public void createLandForAdmin(CreateLandRequest request) throws Exception {
        Land sameNameLand = landMapper.getLandByNameForAdmin(request.getName());
        if (sameNameLand != null) {
            throw new BaseException(LAND_NAME_DUPLICATE);
        }

        Land land = AdminLandConverter.INSTANCE.toLand(request);
        landMapper.createLandForAdmin(land);
    }

    @Override
    @Transactional(readOnly = true)
    public LandResponse getLandForAdmin(Integer landId) throws Exception {
        Land land = getLandById(landId);

        return AdminLandConverter.INSTANCE.toLandResponse(land);
    }

    @Override
    @Transactional(readOnly = true)
    public LandsResponse getLandsForAdmin(LandsCondition condition) throws Exception {
        Integer totalCount = landMapper.getTotalCountByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<Land> lands = landMapper.getLandsByConditionForAdmin(condition.getCursor(), condition);

        return LandsResponse.of(totalCount, totalPage, currentPage, lands);
    }

    @Override
    public void updateLandForAdmin(UpdateLandRequest request, Integer landId) throws Exception {
        Land existingLand = getLandById(landId);

        Land sameNameLand = landMapper.getLandByNameForAdmin(request.getName());
        if (sameNameLand != null && !sameNameLand.hasSameId(landId)) {
            throw new BaseException(LAND_NAME_DUPLICATE);
        }

        existingLand.update(request);
        landMapper.updateLandForAdmin(existingLand);
    }

    @Override
    public void deleteLandForAdmin(Integer landId) throws Exception {
        Land land = getLandById(landId);

        if (land.isSoftDeleted()) {
            throw new BaseException(LAND_ALREADY_DELETED);
        }

        landMapper.deleteLandForAdmin(landId);
    }

    @Override
    public void undeleteLandForAdmin(Integer landId) throws Exception {
        Land land = getLandById(landId);

        if (!land.isSoftDeleted()) {
            throw new BaseException(LAND_NOT_DELETED);
        }

        landMapper.undeleteLandForAdmin(landId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLands() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        List<Land> lands = landMapper.getLandList();
        List<Map<String, Object>> appendLands = new ArrayList<Map<String, Object>>();

        for (Land land : lands) {
            //TODO: redis에서 최근 평점 가져와서 평균내고 grade에 입력
            Map<String, Object> convertLand = domainToMapWithExcept(land, LandResponseType.getArray());
//            convertLand.put("grade", Assessment::getCachedAssessmentInfo());
            appendLands.add(convertLand);
        }
        map.put("lands", appendLands);

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getLand(int id) throws Exception {
        User user = jwtValidator.validate();

        Land land = landMapper.getLand(id);
        if (land == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        List<LandComment> landComments = landMapper.getLandCommentList(id);

        for (LandComment itemComment:landComments) {
            if (user != null && (user.getId().equals(itemComment.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_land()))) {
                itemComment.setGrantEdit(true);
                itemComment.setGrantDelete(true);
            }
            else {
                itemComment.setGrantEdit(false);
                itemComment.setGrantDelete(false);
            }
        }
        Map<String, Object> convertLand = domainToMap(land);

        convertLand.replace("image_urls", JsonConstructor.parseJsonArrayWithOnlyString(land.getImage_urls()));
        convertLand.put("permalink", URLEncoder.encode(land.getInternal_name(), "UTF-8"));
        convertLand.put("comments", landComments);

        return convertLand;
    }

    @Override
    public LandComment createLandComment(LandComment landComment, int land_id) throws Exception {
        User user = jwtValidator.validate();

        //게시글이 삭제되었는지 체크
        Land land = landMapper.getLand(land_id);
        if (land == null)
            throw new NotFoundException(new ErrorMessage("There is no land", 0));

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //이미 평가한경우 체크
        LandComment last_comment = landMapper.getLandComment(land_id, user.getId());
        if (last_comment != null) {
            throw new ConflictException(new ErrorMessage("evaluate already exist", 2));
        }

        landComment.setLand_id(land_id);
        landComment.setUser_id(user.getId());
        landComment.setNickname(user.getNickname());
        landComment.setIs_deleted(false);

        landMapper.createLandComment(landComment);

        return landComment;
    }

    @Override
    public LandComment updateLandComment(LandComment landComment, int land_id) throws Exception {
        User user = jwtValidator.validate();

        //게시글이 삭제되었는지 체크
        Land land = landMapper.getLand(land_id);
        if (land == null)
            throw new NotFoundException(new ErrorMessage("There is no land", 0));

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        LandComment landComment_old = landMapper.getLandComment(land_id, user.getId());

        //빈 객체인지 체크
        if (landComment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no evaluate", 0));

        if(!landComment_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        landComment.setNickname(user.getNickname());

        landComment_old.update(landComment);
        landMapper.updateLandComment(landComment_old);

        return landComment_old;
    }

    @Override
    public Map<String, Object> deleteLandComment(int land_id) throws Exception {
        User user = jwtValidator.validate();

        LandComment landComment = landMapper.getLandComment(land_id, user.getId());

        //게시글이 삭제되었는지 체크
        Land land = landMapper.getLand(land_id);
        if (land == null)
            throw new NotFoundException(new ErrorMessage("There is no land", 0));
        if (landComment == null)
            throw new NotFoundException(new ErrorMessage("There is no evaluate", 0));

        if(!landComment.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        landComment.setIs_deleted(true);

        landMapper.updateLandComment(landComment);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    private Land getLandById(Integer landId) throws Exception {
        return Optional.ofNullable(landMapper.getLandByIdForAdmin(landId))
                .orElseThrow(() -> new BaseException(LAND_NOT_FOUND));
    }
}
