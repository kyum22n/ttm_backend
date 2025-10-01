package com.example.demo.enums;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipateStatus {
    P("P", false, false),
    A("A", true, false),
    R("R", true, false),
    C("C", true, false),
    CANCEL(null, true, true);

    private final String dbStatus;
    private final boolean requireExisting;
    private final boolean deleteOp;

    public static ParticipateStatus parse(String raw) {
        String s = raw.trim().toUpperCase();
        if ("CANCEL".equals(s))
            return CANCEL;
        try {
            return ParticipateStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "status는 P/A/R/C 또는 CANCEL 이어야 합니다.");
        }
    }

}
