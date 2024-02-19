package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.BlackListToken;
import mementee.mementee.api.repository.BlackListTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

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