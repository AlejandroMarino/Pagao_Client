package org.marino.tfgpagao.domain.usecases.member

import org.marino.tfgpagao.data.repository.MemberRepository
import javax.inject.Inject

class GetMembersAvailableOfGroup @Inject constructor(private val memberRepository: MemberRepository) {
    operator fun invoke(groupId: Int) = memberRepository.fetchMembersAvailableOfGroup(groupId)
}