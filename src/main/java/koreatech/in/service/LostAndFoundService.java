package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.LostAndFound.LostItemComment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LostAndFoundService {
    LostItem createLostItemForAdmin(LostItem lostItem) throws Exception;

    Map<String, Object> getLostItemsForAdmin(int type, Criteria criteria) throws Exception;

    Map<String, Object> getLostItemForAdmin(int id) throws Exception;

    LostItem updateLostItemForAdmin(LostItem lostItem, int id) throws Exception;

    Map<String, Object> deleteLostItemForAdmin(int id) throws Exception;

    LostItemComment createLostItemCommentForAdmin(LostItemComment lostItemComment, int lost_item_id) throws Exception;

    LostItemComment updateLostItemCommentForAdmin(LostItemComment lostItemComment, int lost_item_id, int comment_id) throws Exception;

    LostItem createLostItem(LostItem lostItem) throws Exception;

    LostItemComment getLostItemCommentForAdmin(int lost_item_id, int comment_id) throws Exception;

    Map<String, Object> deleteLostItemCommentForAdmin(int lost_item_id, int comment_id) throws Exception;

    Map<String, Object> getLostItems(int type, Criteria criteria) throws Exception;

    Map<String, Object> getLostItem(int id) throws Exception;

    LostItem updateLostItem(LostItem lostItem, int id) throws Exception;

    Map<String, Object> deleteLostItem(int id) throws Exception;

    LostItem updateStateOfLostItem(int state, int id) throws Exception;

    Map<String, Object> getMyLostItemList(int type, Criteria criteria) throws Exception;

    LostItemComment createLostItemComment(LostItemComment lostItemComment, int lost_item_id) throws Exception;

    LostItemComment updateLostItemComment(LostItemComment lostItemComment, int lost_item_id, int comment_id) throws Exception;

    LostItemComment getLostItemComment(int lost_item_id, int comment_id) throws Exception;

    Map<String, Object> deleteLostItemComment(int lost_item_id, int comment_id) throws Exception;

    Map<String, Boolean> checkGrantEditLostItem(int lostItem_id) throws Exception;

    Map<String, Object> lostItemImagesUpload(List<MultipartFile> fileList) throws Exception;

    Map<String, Object> lostItemThumbnailImageUpload(MultipartFile image) throws Exception;

}
