package com.mementee.api.service;

import com.mementee.api.domain.BlackListToken;
import lombok.RequiredArgsConstructor;
import com.mementee.api.repository.BlackListTokenRepository;
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
        Optional<BlackListToken> bt = blackListTokenRepository.isCheckBlackList(accessToken);
        return bt.isPresent();
    }
}
