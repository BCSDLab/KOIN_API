package koreatech.in.service;

import koreatech.in.domain.DiningMenu;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.DiningMapper;
import koreatech.in.util.JsonConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service
public class DiningServiceImpl implements DiningService {

    @Autowired
    private DiningMapper diningMapper;

    @Override
    public List<Map<String, Object>> getDinings(String from) throws Exception {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyMMdd"));
        } catch (DateTimeParseException e) {
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 날짜 포맷입니다.", 0));
        } catch (NullPointerException e) {
            localDate = LocalDate.now();
        }

        List<DiningMenu> dinings = diningMapper.getList(localDate.format(DateTimeFormatter.ISO_DATE));
        List<Map<String, Object>> menus = new ArrayList<>();

        for (DiningMenu dining : dinings) {
            Map<String, Object> convertMenu = domainToMap(dining);
            convertMenu.replace("menu", JsonConstructor.parseJsonArrayWithOnlyString(dining.getMenu()));
            menus.add(convertMenu);
        }

        return menus;
    }
}
