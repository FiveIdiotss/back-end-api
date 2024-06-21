package com.team.mementee.api.service;

import com.team.mementee.api.domain.BlackListToken;
import com.team.mementee.api.repository.BlackListTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    //로그아웃시 accessToken을 blackList에 추가 (토큰 탈취 방지)
    @Transactional
    public void addBlackList(String accessToken){
        BlackListToken bt = new BlackListToken(accessToken);
        blackListTokenRepository.save(bt);
    }

    public boolean isCheckBlackList(String accessToken){
        Optional<BlackListToken> bt = blackListTokenRepository.findBlackListTokenByBlackListToken(accessToken);
        return bt.isPresent();
    }

}
