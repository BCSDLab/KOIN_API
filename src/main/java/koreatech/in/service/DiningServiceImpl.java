package koreatech.in.service;

import koreatech.in.domain.Dining.DiningMenu;
import koreatech.in.domain.Dining.DiningMenuDTO;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.mapstruct.DiningMenuMapper;
import koreatech.in.repository.DiningMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiningServiceImpl implements DiningService {

    @Autowired
    private DiningMapper diningMapper;

    @Override
    public List<DiningMenuDTO> getDinings(String from) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyMMdd"));
        } catch (DateTimeParseException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 날짜 포맷입니다.", 0));
        } catch (NullPointerException e) {
            localDate = LocalDate.now();
        }

        List<DiningMenu> dinings = diningMapper.getList(localDate.format(DateTimeFormatter.ISO_DATE));
        List<DiningMenuDTO> menus = new ArrayList<>();
        dinings.forEach(dining -> menus.add(DiningMenuMapper.INSTANCE.toDiningMenuDTO(dining)));

        return menus;
    }
}
