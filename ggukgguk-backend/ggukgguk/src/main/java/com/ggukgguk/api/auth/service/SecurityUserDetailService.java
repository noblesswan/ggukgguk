package com.ggukgguk.api.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ggukgguk.api.auth.vo.SecurityUserDetails;
import com.ggukgguk.api.member.dao.MemberDao;
import com.ggukgguk.api.member.vo.Member;

public class SecurityUserDetailService implements UserDetailsService {

	@Autowired
	MemberDao dao;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		try {
			Member member = dao.selectMemberById(userId);
			
			if (!member.isMemberActivated()) {
				throw new Exception();
			}

			return new SecurityUserDetails(member);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}