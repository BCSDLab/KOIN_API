<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//Ddiv XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/Ddiv/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>코인 이메일 인증 폼</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body style="margin: 0; padding: 0;">
<div style="background: #FFF; position: relative; display: flex; flex-direction: column; justify-content: flex-start; align-items: stretch; gap: 20px; padding-left: 120px; border: 2px solid; width: 1158px; height: 874px;">
    <img src="https://team-kap-koin-storage.s3.ap-northeast-2.amazonaws.com/assets/img/logo_background.png"
         style="right: 0px; top: 0px; position: absolute; z-index: 1;"/>
    <div>
        <img src="https://team-kap-koin-storage.s3.ap-northeast-2.amazonaws.com/assets/img/logo_primary.png"
             style="margin-top: 85px; flex-shrink: 0; width: 150px; height: 84px" border="0"/>
    </div>
    <div style="margin-top: 35px; z-index: 2; color: #000; font-family: 'Noto Sans KR', Arial, sans-serif; font-size: 40px; font-style: normal; font-weight: 700; line-height: normal;">
        코인에서
        <titleColoredText style="color: #F7941E;">비밀번호 찾기 인증번호</titleColoredText>를<br>
        안내해 드립니다.
    </div>
    <div style="margin-top: 15px; z-index: 2; color: #252525; font-family: 'Noto Sans CJK KR', Arial, sans-serif; font-size: 28px; font-style: normal; font-weight: 500; line-height: normal;">
        <b>
            <emailColoredText style="color: #175C8E">
                ${email-address}
            </emailColoredText>
            님 안녕하세요,
        </b>
    </div>
    <div style="margin-top: 7px; z-index: 2; text-align: left; color: #252525; font-family: 'Noto Sans CJK KR', Arial, sans-serif; font-size: 20px; font-style: normal; font-weight: 400; line-height: 32px;">
        한국기술교육대학교 커뮤니티 코인 운영팀입니다.<br>
        고객님께서 ${year}년 ${month}월 ${day-of-month}일 ${hour}시 ${minute}분경 요청하신 인증번호입니다.
    </div>
    <div style="margin-top: 35px; z-index: 2; color: #000; font-family: 'Noto Sans CJK KR', Arial, sans-serif; font-size: 32px; font-style: normal; font-weight: 500; line-height: normal;">
        <b>비밀번호 찾기 인증번호  </b>
        <p style="border: 1.5px solid #C4C4C4; background: #FFF; width: 1001px; height: 141px; flex-shrink: 0; color: #000; text-align: center; font-family: 'Noto Sans CJK KR', Arial, sans-serif; font-size: 48px; font-style: normal; font-weight: 700; line-height: 141px; letter-spacing: 4.8px;">
            ${certification-code}
        </p>
    </div>
</div>
</body>
</html>
