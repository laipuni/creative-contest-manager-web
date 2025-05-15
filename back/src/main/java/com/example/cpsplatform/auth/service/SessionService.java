package com.example.cpsplatform.auth.service;

import com.example.cpsplatform.auth.service.session.SessionType;

public interface SessionService {

    public String storeSession(final String loginId, final SessionType sessionType);

    public void confirmSession(final String loginId, final String session, final SessionType sessionType);

}
