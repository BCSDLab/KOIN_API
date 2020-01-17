package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.LostAndFound.LostItemComment;
import koreatech.in.domain.LostAndFound.LostItemViewLog;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.User.User;
import koreatech.in.exception.*;
import koreatech.in.repository.LostAndFoundMapper;
import koreatech.in.util.DateUtil;
import koreatech.in.util.SearchUtil;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("lostAndFoundService")
public class LostAndFoundServiceImpl implements LostAndFoundService {
    @Resource(name = "lostAndFoundMapper")
    private LostAndFoundMapper lostAndFoundMapper;

    @Autowired
    JwtValidator jwtValidator;

    @Inject
    UploadFileUtils uploadFileUtils;

    @Autowired
    SlackNotiSender slackNotiSender;

    @Autowired
    private SearchUtil searchUtil;

    @Transactional
    @Override
    public LostItem createLostItemForAdmin(LostItem lostItem) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        lostItem.setUser_id(user.getId());
        lostItem.setNickname(user.getNickname());
        lostItem.setState(0);
        lostItem.setComment_count(0);
        lostItem.setHit(0);
        if (lostItem.getIs_phone_open() == null || !lostItem.getIs_phone_open()) {
            lostItem.setIs_phone_open(false);
            lostItem.setPhone(null);
        }
        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        lostItem.setIp(ip);

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty())
            if (!con.isArrayObjectParse(lostItem.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        if (lostItem.getIs_deleted() == null) {
            lostItem.setIs_deleted(false);
        }

        lostAndFoundMapper.createLostItemForAdmin(lostItem);
        searchUtil.createArticle(lostItem);

        return lostItem;
    }

    @Override
    public Map<String, Object> getLostItemsForAdmin(int type, Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount;
        List<LostItem> lostItems;

        if (type == 0 || type == 1) {
            totalCount = lostAndFoundMapper.totalItemCountByTypeForAdmin(type);
            lostItems = lostAndFoundMapper.getLostItemListByTypeForAdmin(type, criteria.getCursor(), criteria.getLimit());
        } else {
            totalCount = lostAndFoundMapper.totalItemCountForAdmin();
            lostItems = lostAndFoundMapper.getLostItemListForAdmin(criteria.getCursor(), criteria.getLimit());
        }

        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<Map<String, Object>> convert_items = new ArrayList<>();

        JsonConstructor con = new JsonConstructor();
        for (LostItem lostItem : lostItems) {
            Map<String, Object> map_lostItem = domainToMap(lostItem);
            //image_urls 변환
            if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty()) {
                try {
                    map_lostItem.replace("image_urls", con.arrayStringParse(lostItem.getImage_urls()));
                } catch (Exception e) {
                }
            }
            convert_items.add(map_lostItem);
        }

        map.put("lostItems", convert_items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getLostItemForAdmin(int id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        List<LostItemComment> comments = lostAndFoundMapper.getCommentListForAdmin(id);

        for (LostItemComment comment : comments) {
            comment.setGrantEdit(true);
            comment.setGrantDelete(true);
        }

        if (!lostItem.getIs_phone_open()) {
            lostItem.setPhone(null);
        }

        Map<String, Object> map = domainToMap(lostItem);
        Map<String, String> profile = new HashMap<String, String>();

        JsonConstructor con = new JsonConstructor();
        //image_urls 변환
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty()) {
            try {
                map.replace("image_urls", con.arrayStringParse(lostItem.getImage_urls()));
            } catch (Exception e) {
            }
        }
        profile.put("profile_image_url", user != null ? user.getProfile_image_url() : null);
        map.put("user", profile);
        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public LostItem updateLostItemForAdmin(LostItem lostItem, int id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        LostItem lostItem_old = lostAndFoundMapper.getLostItemForAdmin(id);
        if (lostItem_old == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        lostItem.setIp(ip);

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty())
            if (!con.isArrayObjectParse(lostItem.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        //TODO : validator를 사용해 입력된 정보의 유효화 검사 후 입력된 부분만 기존 내용에 반영
        if (!lostItem.getIs_phone_open()) {
            lostItem.setPhone(null);
        }

        lostItem_old.update(lostItem);
        lostAndFoundMapper.updateLostItem(lostItem_old);
        searchUtil.updateArticle(lostItem_old);

        return lostItem_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteLostItemForAdmin(int id) throws Exception {
        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        lostAndFoundMapper.deleteLostItem(id);
        searchUtil.updateArticle(lostItem);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public LostItemComment createLostItemCommentForAdmin(LostItemComment lostItemComment, int lost_item_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no lostItem", 0));

        lostItemComment.setLost_item_id(lost_item_id);
        lostItemComment.setUser_id(user.getId());
        lostItemComment.setNickname(user.getNickname());
        if (lostItemComment.getIs_deleted() == null) {
            lostItemComment.setIs_deleted(false);
        }

        lostAndFoundMapper.createLostItemCommentForAdmin(lostItemComment);
        if (!lostItemComment.getIs_deleted()) {
            lostItem.setComment_count(lostItem.getComment_count() + 1);
            lostAndFoundMapper.updateLostItem(lostItem);
        }

        return lostItemComment;
    }

    @Transactional
    @Override
    public LostItemComment updateLostItemCommentForAdmin(LostItemComment lostItemComment, int lost_item_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글이 삭제되었는지 체크
        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no lostItem", 0));

        LostItemComment lostItemComment_old = lostAndFoundMapper.getLostItemCommentForAdmin(lost_item_id, comment_id);
        //빈 객체인지 체크
        if (lostItemComment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        lostItemComment_old.update(lostItemComment);
        lostAndFoundMapper.updateLostItemComment(lostItemComment_old);

        return lostItemComment_old;
    }

    @Override
    public LostItemComment getLostItemCommentForAdmin(int lost_item_id, int comment_id) throws Exception {
        //게시글이 삭제되었는지 체크
        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        return lostAndFoundMapper.getLostItemCommentForAdmin(lost_item_id, comment_id);
    }

    @Transactional
    @Override
    public Map<String, Object> deleteLostItemCommentForAdmin(int lost_item_id, int comment_id) throws Exception {
        LostItem lostItem = lostAndFoundMapper.getLostItemForAdmin(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        LostItemComment itemComment = lostAndFoundMapper.getLostItemCommentForAdmin(lost_item_id, comment_id);
        if (itemComment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        lostAndFoundMapper.deleteLostItemComment(comment_id);
        lostItem.setComment_count(lostItem.getComment_count() - 1);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public LostItem createLostItem(LostItem lostItem) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        lostItem.setUser_id(user.getId());
        lostItem.setNickname(user.getNickname());
        lostItem.setState(0);
        if (!lostItem.getIs_phone_open()) {
            lostItem.setPhone(null);
        }
        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        lostItem.setIp(ip);

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty())
            if (!con.isArrayObjectParse(lostItem.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        lostAndFoundMapper.createLostItem(lostItem);
        searchUtil.createArticle(lostItem);

        NotiSlack slack_message = new NotiSlack();

        slack_message.setColor("#36a64f");
        slack_message.setAuthor_name(lostItem.getNickname() + "님이 작성");
        slack_message.setTitle(lostItem.getTitle());
        slack_message.setTitle_link("https://koreatech.in/lost/" + "detail/" + lostItem.getId().toString());

        slackNotiSender.noticeLostItem(slack_message);

        return lostItem;
    }

    @Override
    public Map<String, Object> getLostItems(int type, Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount;
        List<LostItem> lostItems;

        if (type == 0 || type == 1) {
            totalCount = lostAndFoundMapper.totalItemCountByType(type);
            lostItems = lostAndFoundMapper.getLostItemListByType(type, criteria.getCursor(), criteria.getLimit());
        } else {
            totalCount = lostAndFoundMapper.totalItemCount();
            lostItems = lostAndFoundMapper.getLostItemList(criteria.getCursor(), criteria.getLimit());
        }

        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int)Math.ceil(totalCount / criteria.getLimit());
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<Map<String, Object>> convert_items = new ArrayList<>();

        JsonConstructor con = new JsonConstructor();
        for (LostItem lostItem : lostItems) {
            Map<String, Object> map_lostItem = domainToMap(lostItem);
            //image_urls 변환
            if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty()) {
                try {
                    map_lostItem.replace("image_urls", con.arrayStringParse(lostItem.getImage_urls()));
                } catch (Exception e) {
                }
            }
            convert_items.add(map_lostItem);
        }

        map.put("lostItems", convert_items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getLostItem(int id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItem(id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (user != null) {
            // Get ip address
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();

            if (shouldIncreaseLostItemHit(lostItem, user, ip)) {
                lostAndFoundMapper.increaseHit(lostItem.getId());
            }
        }
        List<LostItemComment> comments = lostAndFoundMapper.getCommentList(id);

        for (LostItemComment comment : comments) {
            if (user != null && (user.getId().equals(comment.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_lost()))) {
                comment.setGrantEdit(true);
                comment.setGrantDelete(true);
            } else {
                comment.setGrantEdit(false);
                comment.setGrantDelete(false);
            }
        }

        if (!lostItem.getIs_phone_open()) {
            lostItem.setPhone(null);
        }

        Map<String, Object> map = domainToMap(lostItem);
        Map<String, String> profile = new HashMap<String, String>();

        JsonConstructor con = new JsonConstructor();
        //image_urls 변환
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty()) {
            try {
                map.replace("image_urls", con.arrayStringParse(lostItem.getImage_urls()));
            } catch (Exception e) {
            }
        }
        profile.put("profile_image_url", user != null ? user.getProfile_image_url() : null);
        map.put("user", profile);
        map.put("comments", comments);

        return map;
    }

    @Transactional
    @Override
    public LostItem updateLostItem(LostItem lostItem, int id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem_old = lostAndFoundMapper.getLostItem(id);
        if (lostItem_old == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        if (!lostItem_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        lostItem.setIp(ip);

        JsonConstructor con = new JsonConstructor();
        //image_urls 체크
        if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty())
            if (!con.isArrayObjectParse(lostItem.getImage_urls()))
                throw new PreconditionFailedException(new ErrorMessage("Image_urls are not valid", 0));

        //TODO : validator를 사용해 입력된 정보의 유효화 검사 후 입력된 부분만 기존 내용에 반영
        lostItem.setNickname(user.getNickname());
        if (!lostItem.getIs_phone_open()) {
            lostItem.setPhone(null);
        }

        lostItem_old.update(lostItem);
        lostAndFoundMapper.updateLostItem(lostItem_old);
        searchUtil.updateArticle(lostItem_old);

        return lostItem_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteLostItem(int id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItem(id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (!lostItem.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        lostItem.setIs_deleted(true);

        lostAndFoundMapper.updateLostItem(lostItem);
        searchUtil.updateArticle(lostItem);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public LostItem updateStateOfLostItem(int state, int id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItem(id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (!lostItem.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        if (state >= 0 && state < 2)
            lostItem.setState(state);

        lostAndFoundMapper.updateLostItem(lostItem);

        return lostItem;
    }

    @Override
    public Map<String, Object> getMyLostItemList(int type, Criteria criteria) throws Exception {
        User user = jwtValidator.validate();

        Map<String, Object> map = new HashMap<String, Object>();

        if (type > 1 | type < 0) {
            throw new PreconditionFailedException(new ErrorMessage("invalid type", 0));
        }

        double totalCount = lostAndFoundMapper.totalItemCountByType(type);

        //TODO: paging을 위한 중복코드, 반복코드 개선방법 찾기
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        List<LostItem> items = lostAndFoundMapper.getMyLostItemList(criteria.getCursor(), criteria.getLimit(), type, user.getId());
        List<Map<String, Object>> convert_items = new ArrayList<>();

        JsonConstructor con = new JsonConstructor();
        for (LostItem lostItem : items) {
            Map<String, Object> map_lostItem = domainToMap(lostItem);
            //image_urls 변환
            if (lostItem.getImage_urls() != null && !lostItem.getImage_urls().isEmpty()) {
                try {
                    map_lostItem.replace("image_urls", con.arrayStringParse(lostItem.getImage_urls()));
                } catch (Exception e) {
                }
            }
            convert_items.add(map_lostItem);
        }


        map.put("lostItems", convert_items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Transactional
    @Override
    public LostItemComment createLostItemComment(LostItemComment lostItemComment, int lost_item_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        LostItem lostItem = lostAndFoundMapper.getLostItem(lost_item_id);
        if (lostItem == null) {
            throw new NotFoundException(new ErrorMessage("there is no lostItem", 2));
        }

        lostItemComment.setLost_item_id(lost_item_id);
        lostItemComment.setUser_id(user.getId());
        lostItemComment.setNickname(user.getNickname());
        lostItemComment.setIs_deleted(false);

        lostAndFoundMapper.createLostItemComment(lostItemComment);
        lostItem.setComment_count(lostItem.getComment_count() + 1);
        lostAndFoundMapper.updateLostItem(lostItem);

        NotiSlack slack_message = new NotiSlack();

        slack_message.setColor("#36a64f");
        slack_message.setAuthor_name(lostItemComment.getNickname() + "님이 작성");
        slack_message.setTitle(lostItem.getTitle());
        slack_message.setTitle_link("https://koreatech.in/lost/" + "detail/" + lostItem.getId().toString());
        slack_message.setText(lostItemComment.getContent() + "...");

        slackNotiSender.noticeComment(slack_message);

        return lostItemComment;
    }

    @Transactional
    @Override
    public LostItemComment updateLostItemComment(LostItemComment lostItemComment, int lost_item_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글이 삭제되었는지 체크
        LostItem lostItem = lostAndFoundMapper.getLostItem(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        LostItemComment lostItemComment_old = lostAndFoundMapper.getLostItemComment(lost_item_id, comment_id);
        //빈 객체인지 체크
        if (lostItemComment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (!lostItemComment_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        lostItemComment.setNickname(user.getNickname());

        lostItemComment_old.update(lostItemComment);
        lostAndFoundMapper.updateLostItemComment(lostItemComment_old);

        return lostItemComment_old;
    }

    @Override
    public LostItemComment getLostItemComment(int lost_item_id, int comment_id) throws Exception {
        //게시글이 삭제되었는지 체크
        LostItem lostItem = lostAndFoundMapper.getLostItem(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        return lostAndFoundMapper.getLostItemComment(lost_item_id, comment_id);
    }

    @Transactional
    @Override
    public Map<String, Object> deleteLostItemComment(int lost_item_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItem(lost_item_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        LostItemComment itemComment = lostAndFoundMapper.getLostItemComment(lost_item_id, comment_id);
        if (itemComment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if (lostItem == null || lostItem.getIs_deleted())
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (itemComment == null || itemComment.getIs_deleted())
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        if (!itemComment.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        itemComment.setIs_deleted(true);

        lostAndFoundMapper.updateLostItemComment(itemComment);
        lostItem.setComment_count(lostItem.getComment_count() - 1);
        lostAndFoundMapper.updateLostItem(lostItem);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Boolean> checkGrantEditLostItem(int lostItem_id) throws Exception {
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        User user = jwtValidator.validate();

        LostItem lostItem = lostAndFoundMapper.getLostItem(lostItem_id);
        if (lostItem == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (user.getId().equals(lostItem.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_lost())) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }

    @Override
    public Map<String, Object> lostItemImagesUpload(List<MultipartFile> fileList) throws Exception {
        String uploadpath = "upload/lostAndFound";

        List<String> urls = new ArrayList<>();

        for (MultipartFile mf : fileList) {
            String img_path = uploadFileUtils.uploadFile(uploadpath, mf.getOriginalFilename(), mf.getBytes());
            String url = uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

            urls.add(url);
        }

        return new HashMap<String, Object>() {{
            put("url", urls);
        }};
    }

    @Override
    public Map<String, Object> lostItemThumbnailImageUpload(MultipartFile image) throws Exception {
        String uploadpath = "upload/lostAndFound/thumbnail";

        String originalFileName = image.getOriginalFilename();
        int index = originalFileName.lastIndexOf(".");
        String fileName = originalFileName.substring(0, index);
        String fileExt = originalFileName.substring(index+1);

        File file = new File(fileName);
        image.transferTo(file);

        uploadFileUtils.makeThumbnail(file.getAbsolutePath(), originalFileName, fileExt, 500, 500);

        String img_path = uploadFileUtils.uploadFile(uploadpath, originalFileName, image.getBytes());
        String url = uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

        // TODO: 스케줄 처리할 것
        uploadFileUtils.removeThumbnail(file.getAbsolutePath(), originalFileName, fileExt);

        return new HashMap<String, Object>(){{
            put("url", url);
        }};
    }

    public Boolean shouldIncreaseLostItemHit(LostItem lostItem, User user, String ip) {
        try {
            LostItemViewLog viewLog = lostAndFoundMapper.getViewLog(lostItem.getId(), user.getId());
            if (viewLog != null && (viewLog.getExpired_at().getTime() - (new Date()).getTime() > 0)) return false;

            Date expiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);

            //TODO: update Or insert 구현시 개선
            if(viewLog == null) {
                viewLog = new LostItemViewLog();
                viewLog.setArticle_id(lostItem.getId());
                viewLog.setUser_id(user.getId());
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                lostAndFoundMapper.createViewLog(viewLog);
            }
            else {
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                lostAndFoundMapper.updateViewLog(viewLog);
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

}
