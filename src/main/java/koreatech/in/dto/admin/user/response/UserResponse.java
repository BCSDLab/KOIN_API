package koreatech.in.dto.admin.user.response;

import koreatech.in.domain.User.UserType;

import java.util.Date;

public class UserResponse {
    private Integer id;
    private String nickname;
    private String name;
    private String phone_number;
    private UserType user_type;
    private String email;
    private Integer gender;
    private Boolean is_authed;
    private Date last_logged_at;
    private Date created_at;
    private Date updated_at;
}
