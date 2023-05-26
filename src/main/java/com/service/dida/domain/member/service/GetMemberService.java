package com.service.dida.domain.member.service;

import com.service.dida.domain.member.dto.SendAuthEmailDto;
import com.service.dida.domain.member.entity.Member;
import com.service.dida.domain.member.repository.MemberRepository;
import com.service.dida.domain.member.usecase.GetMemberUseCase;
import com.service.dida.global.config.exception.BaseException;
import com.service.dida.global.config.exception.errorCode.MemberErrorCode;
import com.service.dida.global.util.mail.MailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMemberService implements GetMemberUseCase {

    private final MemberRepository memberRepository;
    private final MailUseCase mailUseCase;

    @Override
    public SendAuthEmailDto sendAuthMail(Member member) {
        return new SendAuthEmailDto(mailUseCase.sendAuthMail(member.getEmail()));
    }
}
