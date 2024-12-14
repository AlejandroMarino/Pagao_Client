package org.marino.tfgpagao.domain.usecases.member

import org.marino.tfgpagao.data.repository.MemberRepository
import javax.inject.Inject

class SetUserToMember @Inject constructor(private val memberRepository: MemberRepository) {
    operator fun invoke(memberId: Int) = memberRepository.setUserToMember(memberId)
}