package koreatech.in.controller;


import io.swagger.annotations.Api;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Auth.Authority;
import koreatech.in.annotation.Auth.Role;
import org.springframework.stereotype.Controller;

@Api(tags = "(Normal) User", description = "회원")
@Auth(role = Role.OWNER, authority = Authority.SHOP)
@Controller
public class OwnerController {

}
