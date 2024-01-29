package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.repository.FriendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;


}
