package com.example.Demotion.Common;

public enum ErrorCode {
    DUPLICATED_EMAIL("이미 가입된 이메일입니다.", 1001, 400),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다.", 1500, 500),
    INVALID_CREDENTIALS("이메일 또는 비밀번호가 올바르지 않습니다.", 1002, 400),
    INVALID_REFRESH_TOKEN("유효하지 않거나 만료된 리프레시 토큰입니다.", 1006, 401),
    INVALID_EMAIL("등록되지 않은 이메일입니다.", 1003, 400),
    INVALID_VERIFICATION_CODE("인증 코드가 일치하지 않거나 만료되었습니다.", 1005, 400),
    MISSING_AUTHORIZATION_HEADER("인증 헤더가 없습니다.", 1007, 401),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다.", 1008, 401),
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다.", 1009, 404),
    INVALID_FILE_FORMAT("잘못된 파일 포맷입니다.", 2001, 400),
    PRESIGNED_URL_GENERATION_FAILED("Presigned URL 생성에 실패했습니다.", 2002, 500),
    MISSING_REQUIRED_FIELDS("필수 필드가 누락되었습니다.", 2003, 400),
    DEMO_DB_SAVE_FAILED("데모 저장에 실패했습니다.", 2004, 500),
    DEMO_NOT_FOUND("해당 데모가 존재하지 않습니다.", 2005, 404),
    UNAUTHORIZED_ACCESS("데모에 대한 권한이 없습니다.", 2006, 403),
    SCREENSHOT_NOT_FOUND("잘못된 스크린샷 ID입니다.", 2007, 400),
    DEMO_DELETE_FAILED("데모 삭제에 실패했습니다.", 2009, 500);

    private final String message;
    private final int code;
    private final int status;

    ErrorCode(String message, int code, int status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}
