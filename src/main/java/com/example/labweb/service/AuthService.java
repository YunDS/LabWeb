package com.example.labweb.service;

import com.example.labweb.domain.MemberEntity;
import com.example.labweb.dto.JwtRequestDTO;
import com.example.labweb.dto.MemberSignupRequestDTO;
import com.example.labweb.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String signup(MemberSignupRequestDTO request){
        boolean exist = memberRepository.findAll().stream()
                .filter(memberEntity -> memberEntity.getId().equals(request.getId()))
                .count() >= 1;
        if(exist) return null;
        MemberEntity member = new MemberEntity(request);
        member.encryptPassword(passwordEncoder);
        memberRepository.save(member);
        return member.getId();
    }

    public String signin(JwtRequestDTO request) throws Exception{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getID(), request.getPW()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getUsername();
    }
}
