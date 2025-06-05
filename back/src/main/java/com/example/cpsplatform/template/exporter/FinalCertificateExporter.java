package com.example.cpsplatform.template.exporter;

import com.example.cpsplatform.certificate.domain.Certificate;
import com.example.cpsplatform.contest.Contest;
import com.example.cpsplatform.finalcontest.FinalContest;
import com.example.cpsplatform.member.domain.Member;
import com.example.cpsplatform.team.domain.Team;
import com.example.cpsplatform.template.generator.CertificateGenerator;
import com.example.cpsplatform.template.renderer.TemplateRenderer;
import com.example.cpsplatform.utils.TimeConvertUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.example.cpsplatform.utils.TimeConvertUtils.*;
import static com.example.cpsplatform.utils.TimeConvertUtils.convertDateToString;

/*
본선 진출 확인증을 추출해주는 클래스
 */
@Slf4j
public class FinalCertificateExporter implements CertificateExporter{

    //앞에 "/" 붙일경우 절대 경로로 해석되어 templates아래의 html을 인식못할 수 있으니 주의
    public static final String PRELIMINARY_CERTIFICATE_FORM_PATH = "certificate/final-certificate";

    private final CertificateGenerator certificateGenerator;
    private final TemplateRenderer templateRenderer;

    public FinalCertificateExporter(final CertificateGenerator certificateGenerator, final TemplateRenderer templateRenderer) {
        this.certificateGenerator = certificateGenerator;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public byte[] export(final Certificate certificate) {
        //본선 진출 확인증 폼을 html로 변환
        Map<String, String> variableMap = createVariableMap(
                certificate,
                certificate.getContest(),
                certificate.getMember(),
                certificate.getTeam()
        );
        String html = templateRenderer.render(variableMap, PRELIMINARY_CERTIFICATE_FORM_PATH);
        return certificateGenerator.generate(html);
    }

    private Map<String,String> createVariableMap(final Certificate certificate, final Contest contest, final Member member, final Team team){
        FinalContest finalContest = contest.getFinalContest();
        Map<String,String> map = new HashMap<>();
        map.put("season",String.valueOf(contest.getSeason()));
        map.put("name",member.getName());
        map.put("birth", convertDateToString(member.getBirth()));
        map.put("organizationName", member.getOrganization().getName());
        map.put("teamName", team.getName());
        map.put("teamNumber",team.getTeamNumber());
        map.put("registrationDate",convertDateTimeToString(team.getCreatedAt()));
        map.put("title", finalContest.getTitle());
        map.put("location", finalContest.getLocation());
        map.put("date", getRangeDateTime(finalContest.getStartTime(),finalContest.getEndTime()));
        map.put("createdDate",convertDateToString(LocalDate.from(certificate.getCreatedAt())));
        map.put("serialNumber", certificate.getSerialNumber());
        return map;
    }
}
