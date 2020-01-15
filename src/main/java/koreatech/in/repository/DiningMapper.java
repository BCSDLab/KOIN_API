package koreatech.in.repository;

import koreatech.in.domain.DiningMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface DiningMapper {
    @Select("SELECT * FROM koin.dining_menus WHERE date=#{date}")
    List<DiningMenu> getList(@Param("date") String date);
}