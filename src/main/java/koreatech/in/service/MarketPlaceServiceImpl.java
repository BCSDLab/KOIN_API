package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.MarketPlace.ItemComment;
import koreatech.in.domain.MarketPlace.ItemViewLog;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.user.User;
import koreatech.in.exception.*;
import koreatech.in.repository.MarketPlaceMapper;
import koreatech.in.util.SearchUtil;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.UploadFileUtils;
import koreatech.in.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("marketPlaceService")
public class MarketPlaceServiceImpl implements MarketPlaceService {
    private static Map<Integer, String> market_url = new HashMap<Integer, String>() {{
        put(0, "sell");
        put(1, "buy");
    }};

    @Resource(name = "marketPlaceMapper")
    private MarketPlaceMapper marketPlaceMapper;

    @Autowired
    private SearchUtil searchUtil;

    @Autowired
    JwtValidator jwtValidator;

    @Autowired
    SlackNotiSender slackNotiSender;

    @Inject
    UploadFileUtils uploadFileUtils;

    @Override
    public Item createItemForAdmin(Item item) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        item.setUser_id(user.getId());
        item.setNickname(user.getNickname());
        item.setState(0);
        if (item.getIs_phone_open() == null || !item.getIs_phone_open()) {
            item.setIs_phone_open(false);
            item.setPhone(null);
        }
        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        item.setIp(ip);
        if (item.getIs_deleted() == null) {
            item.setIs_deleted(false);
        }

        marketPlaceMapper.createItemForAdmin(item);
        searchUtil.createArticle(item);

        return item;
    }

    @Override
    public Map<String, Object> getItemsForAdmin(int type, @ModelAttribute("criteria") Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount;
        List<Item> items;

        if (type == 0 || type == 1) {
            totalCount = marketPlaceMapper.totalItemCountByTypeForAdmin(type);
            items = marketPlaceMapper.getItemListByTypeForAdmin(type, criteria.getCursor(), criteria.getLimit());
        } else {
            totalCount = marketPlaceMapper.totalItemCountForAdmin();
            items = marketPlaceMapper.getItemListForAdmin(criteria.getCursor(), criteria.getLimit());
        }

        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        map.put("items", items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getItemForAdmin(int id) throws Exception {
        User user = jwtValidator.validate();

        Item item = marketPlaceMapper.getItemForAdmin(id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        List<ItemComment> itemComments = marketPlaceMapper.getItemCommentListForAdmin(id);

        for (ItemComment itemComment : itemComments) {
            itemComment.setGrantEdit(true);
            itemComment.setGrantDelete(true);
        }

        //연락처를 공개하기 싫다면
        if (!item.getIs_phone_open()) {
            item.setPhone(null);
        }

        Map<String, Object> map = domainToMap(item);
        Map<String, String> profile = new HashMap<String, String>();
        profile.put("profile_image_url", user != null ? user.getProfileImageUrl() : null);
        map.put("user", profile);
        map.put("comments", itemComments);

        return map;
    }

    @Transactional
    @Override
    public Item updateItemForAdmin(Item item, int id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        Item item_old = marketPlaceMapper.getItemForAdmin(id);
        if (item_old == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (!item.getIs_phone_open()) {
            item.setPhone(null);
        }

        item_old.update(item);
        marketPlaceMapper.updateItem(item_old);
        searchUtil.updateArticle(item_old);

        return item_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteItemForAdmin(int id) throws Exception {
        User user = jwtValidator.validate();

        Item item = marketPlaceMapper.getItemForAdmin(id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        marketPlaceMapper.deleteItemForAdmin(id);
        searchUtil.updateArticle(item);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Transactional
    @Override
    public ItemComment createItemCommentForAdmin(ItemComment itemComment, int item_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글 존재 체크
        Item item = marketPlaceMapper.getItemForAdmin(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        itemComment.setItem_id(item_id);
        itemComment.setUser_id(user.getId());
        itemComment.setNickname(user.getNickname());
        if (itemComment.getIs_deleted() == null) {
            itemComment.setIs_deleted(false);
        }

        marketPlaceMapper.createItemCommentForAdmin(itemComment);

        return itemComment;
    }

    @Transactional
    @Override
    public ItemComment updateItemCommentForAdmin(ItemComment itemComment, int item_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글이 삭제되었는지 체크
        Item item = marketPlaceMapper.getItemForAdmin(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        ItemComment itemComment_old = marketPlaceMapper.getItemCommentForAdmin(item_id, comment_id);

        //빈 객체인지 체크
        if (itemComment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        itemComment_old.update(itemComment);
        marketPlaceMapper.updateItemComment(itemComment_old);

        return itemComment_old;
    }

    @Override
    public ItemComment getItemCommentForAdmin(int item_id, int comment_id) throws Exception {
        //게시글이 삭제되었는지 체크
        Item item = marketPlaceMapper.getItemForAdmin(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        return marketPlaceMapper.getItemCommentForAdmin(item_id, comment_id);
    }

    @Transactional
    @Override
    public Map<String, Object> deleteItemCommentForAdmin(int item_id, int comment_id) throws Exception {
        Item item = marketPlaceMapper.getItemForAdmin(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        ItemComment itemComment = marketPlaceMapper.getItemCommentForAdmin(item_id, comment_id);
        if (itemComment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        marketPlaceMapper.deleteItemCommentForAdmin(comment_id);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Object> getItems(int type, Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        double totalCount;
        List<Item> items;

        if (type == 0 || type == 1) {
            totalCount = marketPlaceMapper.totalItemCountByType(type);
            items = marketPlaceMapper.getItemListByType(type, criteria.getCursor(), criteria.getLimit());
        } else {
            totalCount = marketPlaceMapper.totalItemCount();
            items = marketPlaceMapper.getItemList(criteria.getCursor(), criteria.getLimit());
        }

        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        map.put("items", items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Override
    public Map<String, Object> getItem(int id) throws Exception {
        User user = jwtValidator.validate();

        Item item = marketPlaceMapper.getItem(id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        List<ItemComment> itemComments = marketPlaceMapper.getItemCommentList(id);

        if (user != null) {
            // Get ip address
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ip = req.getHeader("X-FORWARDED-FOR");
            if (ip == null)
                ip = req.getRemoteAddr();

            if (shouldIncreaseItemHit(item, user, ip)) {
                marketPlaceMapper.increaseHit(item.getId());
            }
        }

        for (ItemComment itemComment : itemComments) {
            if (user != null && (user.getId().equals(itemComment.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_market()))) {
                itemComment.setGrantEdit(true);
                itemComment.setGrantDelete(true);
            } else {
                itemComment.setGrantEdit(false);
                itemComment.setGrantDelete(false);
            }
        }

        //연락처를 공개하기 싫다면
        if (!item.getIs_phone_open()) {
            item.setPhone(null);
        }

        Map<String, Object> map = domainToMap(item);
        Map<String, String> profile = new HashMap<String, String>();
        profile.put("profile_image_url", user != null ? user.getProfileImageUrl() : null);
        map.put("user", profile);
        map.put("comments", itemComments);

        return map;
    }

    @Transactional
    @Override
    public Item createItem(Item item) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        item.setUser_id(user.getId());
        item.setNickname(user.getNickname());
        item.setState(0);
        if (!item.getIs_phone_open()) {
            item.setPhone(null);
        }
        // Get ip address
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();

        item.setIp(ip);

        marketPlaceMapper.createItem(item);
        searchUtil.createArticle(item);

        slackNotiSender.noticeItem(NotiSlack.builder()
                .color("#36a64f")
                .author_name(item.getNickname() + "님이 작성")
                .title(item.getTitle())
                .title_link("https://koreatech.in/market/" + market_url.get(item.getType()) + '/' + item.getId())
                .build());

        return item;
    }

    @Transactional
    @Override
    public Item updateItem(Item item, int id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        Item item_old = marketPlaceMapper.getItem(id);
        if (item_old == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (!item_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        //TODO : validator를 사용해 입력된 정보의 유효화 검사 후 입력된 부분만 기존 내용에 반영
        if (!item.getIs_phone_open()) {
            item.setPhone(null);
        }
        item.setNickname(user.getNickname());

        item_old.update(item);
        marketPlaceMapper.updateItem(item_old);
        searchUtil.updateArticle(item_old);

        return item_old;
    }

    @Transactional
    @Override
    public Map<String, Object> deleteItem(int id) throws Exception {
        User user = jwtValidator.validate();

        Item item = marketPlaceMapper.getItem(id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (!item.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        item.setIs_deleted(true);

        marketPlaceMapper.updateItem(item);
        searchUtil.updateArticle(item);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Item updateStateOfItem(int state, int id) throws Exception {
        User user = jwtValidator.validate();

        Item item = marketPlaceMapper.getItem(id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (!item.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        item.setState(state);

        marketPlaceMapper.updateItem(item);

        return item;
    }

    @Override
    public Map<String, Object> getMyItemList(int type, Criteria criteria) throws Exception {
        User user = jwtValidator.validate();

        Map<String, Object> map = new HashMap<String, Object>();

        if (type > 1 | type < 0) {
            throw new PreconditionFailedException(new ErrorMessage("invalid type", 0));
        }

        double totalCount = marketPlaceMapper.totalMyItemCountByType(type, user.getId());
        //TODO: paging을 위한 중복코드, 반복코드 개선방법 찾기
        int totalPage = (int) Math.ceil(totalCount / criteria.getLimit());

        List<Item> items = marketPlaceMapper.getMyItemList(criteria.getCursor(), criteria.getLimit(), type, user.getId());

        map.put("items", items);
        map.put("totalPage", totalPage);
        map.put("totalItemCount", (int) totalCount);

        return map;
    }

    @Transactional
    @Override
    public ItemComment createItemComment(ItemComment itemComment, int item_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글 존재 체크
        Item item = marketPlaceMapper.getItem(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        itemComment.setItem_id(item_id);
        itemComment.setUser_id(user.getId());
        itemComment.setNickname(user.getNickname());
        itemComment.setIs_deleted(false);

        marketPlaceMapper.createItemComment(itemComment);

        slackNotiSender.noticeComment(NotiSlack.builder()
                .color("#36a64f")
                .author_name(user.getNickname() + "님이 작성")
                .title(item.getTitle())
                .title_link("https://koreatech.in/market/" + item.getType().toString() + '/' + item_id)
                .text(itemComment.getContent() + "...")
                .build());

        return itemComment;
    }

    @Transactional
    @Override
    public ItemComment updateItemComment(ItemComment itemComment, int item_id, int comment_id) throws Exception {
        User user = jwtValidator.validate();

        if (user.getNickname() == null)
            throw new PreconditionFailedException(new ErrorMessage("No user nickname", 2));

        //게시글이 삭제되었는지 체크
        Item item = marketPlaceMapper.getItem(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        ItemComment itemComment_old = marketPlaceMapper.getItemComment(item_id, comment_id);

        //빈 객체인지 체크
        if (itemComment_old == null)
            throw new NotFoundException(new ErrorMessage("There is no article", 0));

        if (!itemComment_old.hasGrantUpdate(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        itemComment.setNickname(user.getNickname());

        itemComment_old.update(itemComment);
        marketPlaceMapper.updateItemComment(itemComment_old);

        return itemComment_old;
    }

    @Override
    public ItemComment getItemComment(int item_id, int comment_id) throws Exception {
        //게시글이 삭제되었는지 체크
        Item item = marketPlaceMapper.getItem(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        return marketPlaceMapper.getItemComment(item_id, comment_id);
    }

    @Transactional
    @Override
    public Map<String, Object> deleteItemComment(int item_id, int comment_id) throws Exception {
        Item item = marketPlaceMapper.getItem(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        ItemComment itemComment = marketPlaceMapper.getItemComment(item_id, comment_id);
        if (itemComment == null)
            throw new NotFoundException(new ErrorMessage("There is no comment", 0));

        User user = jwtValidator.validate();

        if (!itemComment.hasGrantDelete(user))
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));

        itemComment.setIs_deleted(true);

        marketPlaceMapper.updateItemComment(itemComment);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public Map<String, Boolean> checkGrantEditItem(int item_id) throws Exception {
        User user = jwtValidator.validate();

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        Item item = marketPlaceMapper.getItem(item_id);
        if (item == null)
            throw new NotFoundException(new ErrorMessage("There is no item", 0));

        if (user.getId().equals(item.getUser_id()) || (user.getAuthority() != null && user.getAuthority().getGrant_market())) {
            map.put("grantEdit", true);
        } else {
            map.put("grantEdit", false);
        }
        return map;
    }

    @Override
    public Map<String, Object> itemImagesUpload(Map<String, MultipartFile> fileMap) throws Exception {
        String uploadpath = "upload/market";

        List<String> urls = new ArrayList<>();

        for (MultipartFile mf : fileMap.values()) {
            String img_path = uploadFileUtils.uploadFile(uploadpath, mf.getOriginalFilename(), mf.getBytes());
            String url = "https://" + uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

            urls.add(url);
        }

        return new HashMap<String, Object>() {{
            put("url", urls);
        }};
    }

    @Override
    public Map<String, Object> itemThumbnailImageUpload(MultipartFile image) throws Exception {
        String uploadpath = "upload/market/thumbnail";

        String originalFileName = image.getOriginalFilename();
        int index = originalFileName.lastIndexOf(".");
        String fileName = originalFileName.substring(0, index);
        String fileExt = originalFileName.substring(index + 1);

        File file = new File(fileName);
        image.transferTo(file);

        uploadFileUtils.makeThumbnail(file.getAbsolutePath(), originalFileName, fileExt, 500, 500);


        String img_path = uploadFileUtils.uploadFile(uploadpath, originalFileName, image.getBytes());
        String url = "https://" + uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

        // TODO: 스케줄 처리할 것
        uploadFileUtils.removeThumbnail(file.getAbsolutePath(), originalFileName, fileExt);

        return new HashMap<String, Object>() {{
            put("url", url);
        }};
    }

    public Boolean shouldIncreaseItemHit(Item item, User user, String ip) {
        try {
            ItemViewLog viewLog = marketPlaceMapper.getViewLog(item.getId(), user.getId());
            if (viewLog != null && (viewLog.getExpired_at().getTime() - (new Date()).getTime() > 0)) return false;

            Date expiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);

            //TODO: update Or insert 구현시 개선
            if (viewLog == null) {
                viewLog = new ItemViewLog();
                viewLog.setItem_id(item.getId());
                viewLog.setUser_id(user.getId());
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                marketPlaceMapper.createViewLog(viewLog);
            } else {
                viewLog.setExpired_at(expiredAt);
                viewLog.setIp(ip);
                marketPlaceMapper.updateViewLog(viewLog);
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}
