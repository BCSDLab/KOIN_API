package koreatech.in.domain.Global;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
public class UpdatedAt {
    private static final String DATE_FORM = "yyyy-MM-dd";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORM);

    private final Date updatedAt;

    public String dateForm() {
        return SIMPLE_DATE_FORMAT.format(updatedAt);
    }

}
