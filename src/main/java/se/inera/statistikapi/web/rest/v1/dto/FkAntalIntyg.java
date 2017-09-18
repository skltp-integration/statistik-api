package se.inera.statistikapi.web.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "fkAntalIntyg")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FkAntalIntyg {

    private Long antalRegisterMedicalCertificateToFKTot;
    private IntygPerRecieverId registerMedicalCertificateToFKDirekt;
    private IntygPerRecieverId registerMedicalCertificateToFKViaIntygstjansten;
    private IntygErrors felRegisterMedicalCertificate;

    //    private Long antalFelRegisterMedicalCertificateTotal;
//    private Long antalFelRegisterMedicalCertificateOnSundays;
//    private String antalFelAvTotal;

    private IntygPerRecieverId sendMedicalCertificateToIntygstjansten;

    public FkAntalIntyg() {
    }

    public IntygPerRecieverId getRegisterMedicalCertificateToFKDirekt() {
        return registerMedicalCertificateToFKDirekt;
    }

    public void setRegisterMedicalCertificateToFKDirekt(IntygPerRecieverId registerMedicalCertificateToFKDirekt) {
        this.registerMedicalCertificateToFKDirekt = registerMedicalCertificateToFKDirekt;
    }

    public IntygPerRecieverId getRegisterMedicalCertificateToFKViaIntygstjansten() {
        return registerMedicalCertificateToFKViaIntygstjansten;
    }

    public void setRegisterMedicalCertificateToFKViaIntygstjansten(IntygPerRecieverId registerMedicalCertificateToFKViaIntygstjansten) {
        this.registerMedicalCertificateToFKViaIntygstjansten = registerMedicalCertificateToFKViaIntygstjansten;
    }

    public Long getAntalRegisterMedicalCertificateToFKTot() {
        antalRegisterMedicalCertificateToFKTot = registerMedicalCertificateToFKViaIntygstjansten.getAntal()
                + registerMedicalCertificateToFKDirekt.getAntal();
        return antalRegisterMedicalCertificateToFKTot;
    }

    public IntygPerRecieverId getSendMedicalCertificateToIntygstjansten() {
        return sendMedicalCertificateToIntygstjansten;
    }

    public void setSendMedicalCertificateToIntygstjansten(IntygPerRecieverId sendMedicalCertificateToIntygstjansten) {
        this.sendMedicalCertificateToIntygstjansten = sendMedicalCertificateToIntygstjansten;
    }

    public IntygErrors getFelRegisterMedicalCertificate() {
        return felRegisterMedicalCertificate;
    }

    public void setFelRegisterMedicalCertificate(IntygErrors felRegisterMedicalCertificate) {
        this.felRegisterMedicalCertificate = felRegisterMedicalCertificate;
    }
//    public Long getAntalFelRegisterMedicalCertificateTotal() {
//        return antalFelRegisterMedicalCertificateTotal;
//    }
//
//    public void setAntalFelRegisterMedicalCertificateTotal(Long antalFelRegisterMedicalCertificateTotal) {
//        this.antalFelRegisterMedicalCertificateTotal = antalFelRegisterMedicalCertificateTotal;
//    }


//    public Long getAntalFelRegisterMedicalCertificateOnSundays() {
//        return antalFelRegisterMedicalCertificateOnSundays;
//    }
//
//    public void setAntalFelRegisterMedicalCertificateOnSundays(Long antalFelRegisterMedicalCertificateOnSundays) {
//        this.antalFelRegisterMedicalCertificateOnSundays = antalFelRegisterMedicalCertificateOnSundays;
//    }
//
//    public String getAntalFelAvTotal() {
//        return antalFelAvTotal;
//    }
//
//    public void setAntalFelAvTotal(String antalFelAvTotal) {
//        this.antalFelAvTotal = antalFelAvTotal;
//    }
}
