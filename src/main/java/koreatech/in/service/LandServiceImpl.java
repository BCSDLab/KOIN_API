package koreatech.in.service;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.domain.BokDuck.LandResponseType;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.User;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.land.admin.request.LandsCondition;
import koreatech.in.dto.land.admin.response.LandResponse;
import koreatech.in.dto.land.admin.response.LandsResponse;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.LandMapper;
import koreatech.in.util.JsonConstructor;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.util.ExceptionMessage.*;

@Service("landService")
public class LandServiceImpl implements LandService {
    @Resource(name="landMapper")
    private LandMapper landMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    UploadFileUtils uploadFileUtils;

    @Transactional
    @Override
    public Land createLandForAdmin(Land land) throws Exception {
        Land selectLand = landMapper.getLandByNameForAdmin(land.getName());
        if (selectLand != null) {
            throw new ConflictException(new ErrorMessage("exists land name", 0));
        }

        land.init();
        land.setInternal_name(land.getName().replace(" ","").toLowerCase());

        //image_urls 체크
        if (land.getImage_urls() != null && !JsonConstructor.isJsonArrayWithOnlyString(land.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        landMapper.createLandForAdmin(land);

        return land;
    }

    @Transactional(readOnly = true)
    @Override
    public LandResponse getLandForAdmin(Integer landId) throws Exception {
        Land land = landMapper.getLandForAdmin(landId);

        if (land == null) {
            throw new NotFoundException(new ErrorMessage(LAND_NOT_FOUND));
        }

        return LandResponse.builder()
                .land(LandResponse.Land.builder()
                        .id(land.getId())
                        .name(land.getName())
                        .is_deleted(land.getIs_deleted())
                        .room_type(land.getRoom_type())
                        .management_fee(land.getManagement_fee())
                        .size(land.getSize())
                        .monthly_fee(land.getMonthly_fee())
                        .charter_fee(land.getCharter_fee())
                        .latitude(land.getLatitude())
                        .longitude(land.getLongitude())
                        .deposit(land.getDeposit())
                        .floor(land.getFloor())
                        .phone(land.getPhone())
                        .address(land.getAddress())
                        .description(land.getDescription())
                        .opt_refrigerator(land.getOpt_refrigerator())
                        .opt_closet(land.getOpt_closet())
                        .opt_tv(land.getOpt_tv())
                        .opt_microwave(land.getOpt_microwave())
                        .opt_gas_range(land.getOpt_gas_range())
                        .opt_induction(land.getOpt_induction())
                        .opt_water_purifier(land.getOpt_water_purifier())
                        .opt_air_conditioner(land.getOpt_air_conditioner())
                        .opt_washer(land.getOpt_washer())
                        .opt_bed(land.getOpt_bed())
                        .opt_desk(land.getOpt_desk())
                        .opt_shoe_closet(land.getOpt_shoe_closet())
                        .opt_electronic_door_locks(land.getOpt_electronic_door_locks())
                        .opt_bidet(land.getOpt_bidet())
                        .opt_veranda(land.getOpt_veranda())
                        .opt_elevator(land.getOpt_elevator())
                        .image_urls(JsonConstructor.parseJsonArrayWithOnlyString(land.getImage_urls()))
                        .build()
                )
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public LandsResponse getLandsForAdmin(LandsCondition condition) throws Exception {
        if (condition.getQuery() != null && !StringUtils.hasText(condition.getQuery())) {
            throw new ConflictException(new ErrorMessage(SEARCH_QUERY_MUST_NOT_BE_BLANK));
        }

        Integer totalCount = landMapper.getTotalCountByConditionForAdmin(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException(new ErrorMessage(PAGE_NOT_FOUND));
        }

        List<LandsResponse.Land> lands = landMapper.getLandsByConditionForAdmin(condition.getCursor(), condition);

        return LandsResponse.builder()
                .total_count(totalCount)
                .total_page(totalPage)
                .current_count(lands.size())
                .current_page(currentPage)
                .lands(lands)
                .build();
    }

    @Transactional
    @Override
    public Land updateLandForAdmin(Land land, int id) throws Exception {
        Land land_old = landMapper.getLandForAdmin(id);
        if (land_old == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (land.getName() != null) {
            land.setInternal_name(land.getName().replace(" ", "").toLowerCase());
        }

        //image_urls 체크
        if (land.getImage_urls() != null && !JsonConstructor.isJsonArrayWithOnlyString(land.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        land_old.update(land);
        landMapper.updateLandForAdmin(land_old);
        return land_old;
    }

    @Transactional
    @Override
    public SuccessResponse deleteLandForAdmin(Integer landId) throws Exception {
        Land land = landMapper.getLandForAdmin(landId);

        if (land == null) {
            throw new NotFoundException(new ErrorMessage(LAND_NOT_FOUND));
        }

        if (land.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage(LAND_ALREADY_DELETED));
        }

        landMapper.softDeleteLandForAdmin(landId);

        return new SuccessResponse();
    }

    @Transactional
    @Override
    public Map<String, Object> undeleteLandForAdmin(int id) throws Exception {
        Land land = landMapper.getLandForAdmin(id);

        if (land == null) {
            throw new NotFoundException(new ErrorMessage("There is no item", 0));
        }

        if (!land.getIs_deleted()) {
            throw new ConflictException(new ErrorMessage("It is not soft deleted.", 0));
        }

        landMapper.undeleteLandForAdmin(id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Override
    public Map<String, Object> uploadImages(List<MultipartFile> images) throws Exception {
        // 무분별한 업로드 방지
        if (images.size() > 10) {
            throw new PreconditionFailedException(new ErrorMessage("한번에 최대 10개까지 업로드 가능합니다.", 0));
        }

        String directory = "lands";
        List<String> imageUrls = new LinkedList<>();

        for (MultipartFile image : images) {
            String url = uploadFileUtils.uploadFile(directory, image.getOriginalFilename(), image.getBytes(), image);
            imageUrls.add("https://" + uploadFileUtils.getDomain() + "/" + directory + url);
        }

        return new HashMap<String, Object>() {{
            put("success", true);
            put("image_urls", imageUrls);
        }};
    }
}
