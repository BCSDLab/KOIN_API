package koreatech.in.service;

import koreatech.in.domain.BokDuck.Land;
import koreatech.in.domain.BokDuck.LandComment;
import koreatech.in.domain.BokDuck.LandResponseType;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.user.User;
import koreatech.in.exception.*;
import koreatech.in.repository.LandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;

@Service("landService")
public class LandServiceImpl implements LandService {
    @Resource(name="landMapper")
    private LandMapper landMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    private JsonConstructor con;

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
        if (land.getImage_urls() != null && !con.isJsonArrayWithOnlyString(land.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        landMapper.createLandForAdmin(land);

        return land;
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
        if (land.getImage_urls() != null && !con.isJsonArrayWithOnlyString(land.getImage_urls()))
            throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        land_old.update(land);
        landMapper.updateLandForAdmin(land_old);
        return land_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteLandForAdmin(int id) throws Exception {
        Land land = landMapper.getLandForAdmin(id);

        if (land == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        landMapper.deleteLandForAdmin(id);

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

        convertLand.replace("image_urls", con.parseJsonArrayWithOnlyString(land.getImage_urls()));
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
}
