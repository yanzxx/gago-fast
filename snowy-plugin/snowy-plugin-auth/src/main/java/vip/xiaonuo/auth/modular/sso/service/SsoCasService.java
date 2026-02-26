
package vip.xiaonuo.auth.modular.sso.service;

import vip.xiaonuo.auth.modular.sso.body.LoginTicket2_0Body;

public interface SsoCasService {

    String getLoginRedirectUrl();

    String loginTicket2_0(LoginTicket2_0Body body);
}
