package koreatech.in.service;

import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.MarketPlace.ItemComment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface MarketPlaceService {

    Item createItemForAdmin(Item item) throws Exception;

    Map<String, Object> getItemsForAdmin(int type, Criteria criteria) throws Exception;

    Map<String, Object> getItemForAdmin(int id) throws Exception;

    Item updateItemForAdmin(Item item, int id) throws Exception;

    Map<String, Object> deleteItemForAdmin(int id) throws Exception;

    ItemComment createItemCommentForAdmin(ItemComment itemComment, int item_id) throws Exception;

    ItemComment updateItemCommentForAdmin(ItemComment itemComment, int item_id, int comment_id) throws Exception;

    ItemComment getItemCommentForAdmin(int item_id, int comment_id) throws Exception;

    Map<String, Object> deleteItemCommentForAdmin(int item_id, int comment_id) throws Exception;

    Map<String, Object> getItems(int type, Criteria criteria) throws Exception;

    Map<String, Object> getItem(int id) throws Exception;

    Item createItem(Item item) throws Exception;

    Item updateItem(Item item, int id) throws Exception;

    Map<String, Object> deleteItem(int id) throws Exception;

    Item updateStateOfItem(int state, int id) throws Exception;

    Map<String, Object> getMyItemList(int type, Criteria criteria) throws Exception;

    ItemComment createItemComment(ItemComment itemComment, int item_id) throws Exception;

    ItemComment updateItemComment(ItemComment itemComment, int item_id, int comment_id) throws Exception;

    ItemComment getItemComment(int item_id, int comment_id) throws Exception;

    Map<String, Object> deleteItemComment(int item_id, int comment_id) throws Exception;

    Map<String, Boolean> checkGrantEditItem(int item_id) throws Exception;

    Map<String, Object> itemImagesUpload(Map<String, MultipartFile> fileMap) throws Exception;

    Map<String, Object> itemThumbnailImageUpload(MultipartFile image) throws Exception;

}
