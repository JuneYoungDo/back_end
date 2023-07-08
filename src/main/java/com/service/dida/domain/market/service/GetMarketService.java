package com.service.dida.domain.market.service;

import com.service.dida.domain.market.dto.MarketResponseDto.GetHotItem;
import com.service.dida.domain.market.dto.MarketResponseDto.GetHotSeller;
import com.service.dida.domain.market.dto.MarketResponseDto.GetRecentNft;
import com.service.dida.domain.market.dto.MarketResponseDto.GetHotUser;
import com.service.dida.domain.market.dto.MarketResponseDto.GetMainPageWithoutSoldOut;

import com.service.dida.domain.like.repository.LikeRepository;
import com.service.dida.domain.market.repository.MarketRepository;
import com.service.dida.domain.market.usecase.GetMarketUseCase;
import com.service.dida.domain.member.entity.Member;
import com.service.dida.domain.member.repository.MemberRepository;
import com.service.dida.domain.nft.Nft;
import com.service.dida.domain.nft.repository.NftRepository;
import com.service.dida.domain.transaction.repository.TransactionRepository;
import com.service.dida.global.config.exception.BaseException;
import com.service.dida.global.config.exception.errorCode.MemberErrorCode;
import com.service.dida.global.util.usecase.UtilUseCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMarketService implements GetMarketUseCase {


    private final LikeRepository likeRepository;
    private final TransactionRepository transactionRepository;
    private final NftRepository nftRepository;
    private final MemberRepository memberRepository;

    public String likeCountToString(long likeCount) {
        if (likeCount >= 1000) {
            return likeCount / 1000 + "K";
        } else return likeCountToString(likeCount);
    }

    public GetHotItem makeHotItemForm(Nft nft) {
        return new GetHotItem(nft.getNftId(), nft.getImgUrl(), nft.getTitle(), nft.getPrice(),
                likeCountToString(likeRepository.getLikeCountsByNftId(nft).orElse(0L)));
    }

    public GetHotSeller makeHotSellerForm(Member member) {
        return new GetHotSeller(member.getMemberId(), member.getNickname(), member.getProfileUrl(),
                nftRepository.findRecentNftImgUrlMinusHide(member).orElse(null));
    }

    public List<GetHotItem> getHotItems(Member member) {
        List<GetHotItem> hotItems = new ArrayList<>();
        List<Nft> nfts = likeRepository.getHotItems(member).orElse(null);
        if (nfts != null) {
            for (Nft nft : nfts) {
                hotItems.add(makeHotItemForm(nft));
            }
        }
        return hotItems;
    }

    public List<GetHotSeller> getHotSellers(Member member) {
        List<GetHotSeller> hotSellers = new ArrayList<>();
        List<Long> sellers = transactionRepository.getHotSellersMinusHide(member,
                LocalDateTime.now().minusDays(7)).orElse(null);
        if (sellers != null) {
            for (Long sellerId : sellers) {
                Member seller = memberRepository.findByMemberIdWithDeleted(sellerId)
                        .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
                hotSellers.add(makeHotSellerForm(seller));
            }
        }
        return hotSellers;
    }

    @Override
    public GetMainPageWithoutSoldOut getMainPage(Member member) {
        List<GetHotItem> hotItems = getHotItems(member);
        List<GetHotSeller> hotSellers = getHotSellers(member);
        //List<GetRecentNft> recentNfts = new ArrayList<>();
        //List<GetHotUser> hotUsers = new ArrayList<>();
        return new GetMainPageWithoutSoldOut();//hotItems, hotSellers, recentNfts, hotUsers);
    }

}
