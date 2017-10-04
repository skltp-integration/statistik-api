package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "fkAntalIntyg")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FkAntalIntyg {

    private Long antalRegisterMedicalCertificateToFKTot;
    private IntygGrupperatPaSenderIds registerMedicalCertificateToFKDirekt;
    private IntygGrupperatPaSenderIds registerMedicalCertificateToFKViaIntygstjansten;
    private IntygErrors felRegisterMedicalCertificate;

    private IntygGrupperatPaSenderIds sendMedicalCertificateToIntygstjansten;

    public FkAntalIntyg() {
    }

    public IntygGrupperatPaSenderIds getRegisterMedicalCertificateToFKDirekt() {
        return registerMedicalCertificateToFKDirekt;
    }

    public void setRegisterMedicalCertificateToFKDirekt(IntygGrupperatPaSenderIds registerMedicalCertificateToFKDirekt) {
        this.registerMedicalCertificateToFKDirekt = registerMedicalCertificateToFKDirekt;
    }

    public IntygGrupperatPaSenderIds getRegisterMedicalCertificateToFKViaIntygstjansten() {
        return registerMedicalCertificateToFKViaIntygstjansten;
    }

    public void setRegisterMedicalCertificateToFKViaIntygstjansten(IntygGrupperatPaSenderIds registerMedicalCertificateToFKViaIntygstjansten) {
        this.registerMedicalCertificateToFKViaIntygstjansten = registerMedicalCertificateToFKViaIntygstjansten;
    }

    public Long getAntalRegisterMedicalCertificateToFKTot() {
        antalRegisterMedicalCertificateToFKTot = registerMedicalCertificateToFKViaIntygstjansten.getAntal()
                + registerMedicalCertificateToFKDirekt.getAntal();
        return antalRegisterMedicalCertificateToFKTot;
    }

    public IntygGrupperatPaSenderIds getSendMedicalCertificateToIntygstjansten() {
        return sendMedicalCertificateToIntygstjansten;
    }

    public void setSendMedicalCertificateToIntygstjansten(IntygGrupperatPaSenderIds sendMedicalCertificateToIntygstjansten) {
        this.sendMedicalCertificateToIntygstjansten = sendMedicalCertificateToIntygstjansten;
    }

    public IntygErrors getFelRegisterMedicalCertificate() {
        return felRegisterMedicalCertificate;
    }

    public void setFelRegisterMedicalCertificate(IntygErrors felRegisterMedicalCertificate) {
        this.felRegisterMedicalCertificate = felRegisterMedicalCertificate;
    }

}
