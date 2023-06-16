package uj.wmii.jwzp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uj.wmii.jwzp.config.security.JwtService;
import uj.wmii.jwzp.config.timezone.TimeZoneService;
import uj.wmii.jwzp.exception.InvalidRequestException;
import uj.wmii.jwzp.user.Member;
import uj.wmii.jwzp.user.MemberRepository;
import uj.wmii.jwzp.user.Role;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final static Logger authenticationServiceLogger = LoggerFactory.getLogger(AuthenticationService.class);

    private final MemberRepository memberRepository;

    private final RegisterRequestValidator registerValidator;

    private final AuthenticationRequestValidator authValidator;
    private final MessageSource messageSource;

    private final PasswordEncoder passwordEncoder;

    private final TimeZoneService timeZoneService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(MemberRepository memberRepository,
                                 RegisterRequestValidator registerValidator,
                                 AuthenticationRequestValidator authValidator,
                                 MessageSource messageSource,
                                 PasswordEncoder passwordEncoder,
                                 TimeZoneService timeZoneService,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager
    ) {
        this.memberRepository = memberRepository;
        this.registerValidator = registerValidator;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
        this.timeZoneService = timeZoneService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authValidator = authValidator;
    }

    public AuthenticationResponse register(ZoneId zoneId, RegisterRequest registerRequest) {
        authenticationServiceLogger.info("Registering...");
        BindingResult result = new BeanPropertyBindingResult(registerRequest, "registerRequest");
        registerValidator.validate(registerRequest, result);
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(error -> messageSource.getMessage(error.getCode(), null, Locale.getDefault())).toList();
            authenticationServiceLogger.error("Request error: {}", errorMessages);
            throw new InvalidRequestException("Request error: " + errorMessages);
        }
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(registerRequest.email());
        if(memberByEmail.isPresent()){
            authenticationServiceLogger.error("There is already a member registered with email: {}", memberByEmail.get());
            throw new InvalidRequestException("There is already a member registered with email: "+ memberByEmail.get());
        }
        boolean isAdmin = registerRequest.firstName().equals("admin");
        Member member = new Member(
                registerRequest.firstName(),
                registerRequest.lastName(),
                registerRequest.email(),
                registerRequest.membershipDateStart(),
                passwordEncoder.encode(registerRequest.password()),
                isAdmin ? Role.ROLE_TRAINER : Role.ROLE_USER
        );
        member.fixMembershipDateStart(zoneId, timeZoneService.getZoneId());
        member.extendMembership(1);
        memberRepository.save(member);
        String jwtToken = jwtService.generateToken(member);
        authenticationServiceLogger.info("Token generated successfully for member: {}", registerRequest.email());
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationServiceLogger.info("Authenticating...");
        BindingResult result = new BeanPropertyBindingResult(authenticationRequest, "authenticationRequest");
        authValidator.validate(authenticationRequest, result);
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(error -> messageSource.getMessage(error.getCode(), null, Locale.getDefault())).toList();
            authenticationServiceLogger.error("Request error: {}",errorMessages);
            throw new InvalidRequestException("Request error: " + errorMessages);
        }
        Optional<Member> memberByEmail = memberRepository.findMemberByEmail(authenticationRequest.email());
        if (memberByEmail.isEmpty()){
            authenticationServiceLogger.error("No member exists with the provided email: {}", authenticationRequest.email());
            throw new InvalidRequestException("No member exists with the provided email " + authenticationRequest.email());
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.email(),
                authenticationRequest.password()
        ));
        String jwtToken = jwtService.generateToken(memberByEmail.get() );
        authenticationServiceLogger.info("Token generated successfully for member: {}", authenticationRequest.email());
        return new AuthenticationResponse(jwtToken);
    }
}
