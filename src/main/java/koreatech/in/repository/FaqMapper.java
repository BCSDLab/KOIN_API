package koreatech.in.repository;

import koreatech.in.domain.Faq.Faq;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("faqMapper")
public interface FaqMapper {
    @Select("SELECT * FROM koin.faqs WHERE circle_id = 0 AND IS_DELETED = 0 ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<Faq> getFaqList(@Param("cursor") int cursor, @Param("limit") int limit);

    @Insert("INSERT INTO koin.faqs (QUESTION, ANSWER, IS_DELETED, CIRCLE_ID) " +
            "VALUES (#{question}, #{answer}, #{is_deleted}, #{circle_id})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", before = false, resultType = int.class)
    void createFaqForAdmin(Faq faq);

    @Update("UPDATE koin.faqs SET QUESTION = #{question}, ANSWER = #{answer}, IS_DELETED = #{is_deleted}, CIRCLE_ID = #{circle_id} WHERE ID = #{id}")
    void updateFaqForAdmin(Faq faq);

    @Delete("DELETE FROM koin.faqs WHERE ID = #{id}")
    void deleteFaqForAdmin(@Param("id") int id);

    @Select("SELECT answer, question, id, created_at, updated_at FROM koin.faqs WHERE circle_id = 0 AND ID = #{id} AND IS_DELETED = 0")
    Faq getFaq(@Param("id") int id);

    @Select("SELECT * FROM koin.faqs WHERE ID = #{id}")
    Faq getFaqForAdmin(@Param("id") int id);

    @Select("SELECT COUNT(*) AS totalCount FROM koin.faqs WHERE circle_id = 0 AND IS_DELETED = 0")
    Integer totalCount();

    @Select("SELECT COUNT(*) AS faqTotalCount FROM koin.faqs WHERE circle_id = #{circle_id} AND IS_DELETED = 0")
    Integer totalCountByCircle(@Param("circle_id") int circle_id);

    @Select("SELECT * FROM koin.faqs WHERE circle_id = #{circle_id} AND IS_DELETED = 0 ORDER BY created_at LIMIT #{cursor}, #{limit}")
    List<Faq> getFaqListByCircle(@Param("cursor") int cursor, @Param("limit") int limit, @Param("circle_id") int circle_id);
}
